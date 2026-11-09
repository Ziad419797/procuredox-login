# Login (Procuredox)

A login system built with **Spring Boot MVC**, **JTE** templates and **Tailwind CSS**.
Users are stored in **MySQL**, authenticated with **Spring Security** (BCrypt password hashes)
and protected by a two-factor step (a one-time code, stored in **Redis**).

## Features

- Sign up page (`/signup`): first name, last name, email, phone, password + confirmation, with server-side validation
- Custom login page (`/login`) with error and success messages
- Two-factor authentication (`/otp`): a 6-digit code is generated after the password is accepted
  and **printed in the server terminal** (no email/SMS is sent). The code is valid for **5 minutes**
  and allows **3 attempts**
- CSRF protection on all forms (login, signup, OTP, logout)
- Protected home page (`/home`) with a logout button, available only after the OTP is verified
- Users loaded from the database (`users` table)
- Already-authenticated users are redirected away from `/login`

## Tech stack

| Layer | Technology |
|---|---|
| Backend | Java 21, Spring Boot 4, Spring MVC, Spring Security, Spring Data JPA, Bean Validation |
| Views | JTE (`src/main/jte`) |
| Styling | Tailwind CSS v4 (CLI) |
| Database | MySQL 8 |
| OTP storage | Redis 7 (keys expire automatically) |

## Requirements

- JDK 21 or newer
- Node.js (LTS) and npm
- MySQL 8
- Redis (the easiest way is Docker, see below)
- Maven is **not** required, the Maven wrapper (`mvnw`) is included

## Setup

### 1. Database

Create the database and the `users` table (run as a MySQL admin user):

```bash
mysql -u root -p < database/schema.sql
```

Create a dedicated user for the application (choose your own password):

```sql
CREATE USER 'login_app'@'localhost' IDENTIFIED BY '<choose-a-password>';
GRANT SELECT, INSERT, UPDATE, DELETE ON procuredox_login.* TO 'login_app'@'localhost';
```

Optionally add a test user (development only):

```bash
mysql -u root -p < database/seed.sql
```

Test credentials: `admin@test.com` / `<5070>`
(the password that matches the BCrypt hash in `database/seed.sql`).

### 2. Redis

```bash
docker run -d --name redis -p 127.0.0.1:6379:6379 redis:7
```

Next time: `docker start redis`. The app connects to `localhost:6379` by default
(`spring.data.redis.host` / `spring.data.redis.port` in `application.properties`).
This setup has no Redis password, so it is meant for local development only.

### 3. Configuration

The database connection is read from environment variables, so no password is stored in the code.

| Variable | Description | Default |
|---|---|---|
| `DB_USER` | Database user | `login_app` |
| `DB_PASSWORD` | Database password | *(required)* |

The JDBC URL is set in `src/main/resources/application.properties`
(`jdbc:mysql://localhost:3306/procuredox_login`).

PowerShell:

```powershell
$env:DB_PASSWORD="<your-password>"
```

Bash:

```bash
export DB_PASSWORD="<your-password>"
```

### 4. Frontend (Tailwind)

```bash
npm install
npm run css:build
```

This generates `src/main/resources/static/css/output.css` from the classes used in `src/main/jte`.
While developing, keep the watcher running in a separate terminal so new classes are picked up:

```bash
npm run css:watch
```

### 5. Run the application

```bash
./mvnw spring-boot:run        # Linux / macOS
.\mvnw.cmd spring-boot:run    # Windows
```

Open <http://localhost:8080>. If port 8080 is already in use:

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

## Usage

1. Create an account at `/signup`, or use the test user.
2. Sign in at `/login`.
3. Look in the **application terminal** for a line like `OTP for you@example.com is 123456`.
4. Enter the code at `/otp`. After 3 wrong attempts, or after 5 minutes, you must sign in again.

Signup rules: phone number must be 11 digits and start with `0`; password must be 8 to 72 characters;
email and phone must be unique.

## Endpoints

| Route | Access | Purpose |
|---|---|---|
| `GET/POST /signup` | public | Create an account |
| `GET /login`, `POST /login` | public | Sign in (password step) |
| `GET/POST /otp` | password accepted, OTP pending | Verify the one-time code |
| `GET /home` | fully authenticated | Home page |
| `POST /logout` | authenticated | Sign out |

## Project structure

```
database/                     SQL scripts (schema.sql, seed.sql)
src/main/frontend/input.css   Tailwind entry file
src/main/jte/                 JTE templates (login, signup, otp, home)
src/main/resources/
  application.properties      App configuration
  static/css/output.css       Generated CSS
src/main/java/com/procuredox/login/
  LoginController             GET /login, GET /home
  SignupController            GET/POST /signup
  SecurityConfig              Security rules, login/logout configuration, password encoder
  user/AppUser                JPA entity mapped to the users table
  user/UserRepository         Spring Data repository (findByEmail, existsByEmail, existsByPhone)
  user/SignupForm             Signup form data with validation rules
  user/UserService            Registers users (normalizes email, hashes the password)
  user/DbUserDetailsService   Loads users from the database for Spring Security
  otp/OtpService              Generates and verifies one-time codes (Redis)
  otp/OtpLoginSuccessHandler  Generates the code after the password is accepted
  otp/OtpController           GET/POST /otp
  otp/OtpResult               Result of a verification attempt
```

## How it works

1. `GET /login` returns the login page with a CSRF token.
2. `POST /login` is handled by Spring Security, which loads the user through `DbUserDetailsService`
   and compares the password with the stored BCrypt hash.
3. On success the user only gets a temporary `PRE_AUTH` authority. `OtpLoginSuccessHandler` generates
   a 6-digit code, stores it in Redis with a 5-minute expiry, prints it in the terminal and redirects to `/otp`.
4. `/otp` is the only page available with `PRE_AUTH`. All other pages require `ROLE_USER`.
5. When the code is correct, `OtpController` replaces the authentication with one that has `ROLE_USER`
   (and changes the session id) and redirects to `/home`.
6. `POST /logout` ends the session and redirects to `/login?logout`.

## Notes

- `gg.jte.developmentMode=true` is for development only. JTE templates should be precompiled for production.
- Passwords are stored as BCrypt hashes only. Never commit real credentials.
- The OTP is printed in the terminal on purpose (requirement of the task). A real system would send it by SMS or email.
- `LoginApplicationTests` loads the full application context, so it needs a reachable database, Redis and `DB_PASSWORD` set.
