# Login (Procuredox)

A login page built with **Spring Boot MVC**, **JTE** templates and **Tailwind CSS**.
Users are stored in **MySQL** and authenticated with **Spring Security** (BCrypt password hashes).

## Features

- Custom login page (`/login`) with error message on invalid credentials
- CSRF protection on the login and logout forms
- Protected home page (`/home`) with a logout button
- Users loaded from the database (`users` table)
- Already-authenticated users are redirected from `/login` to `/home`

## Tech stack

| Layer | Technology |
|---|---|
| Backend | Java 21, Spring Boot 4, Spring MVC, Spring Security, Spring Data JPA |
| Views | JTE (`src/main/jte`) |
| Styling | Tailwind CSS v4 (CLI) |
| Database | MySQL 8 |

## Requirements

- JDK 21 or newer
- Node.js (LTS) and npm
- MySQL 8
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

Test credentials: `admin@test.com` / `<test-password>`

### 2. Configuration

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

### 3. Frontend (Tailwind)

```bash
npm install
npm run css:build
```

This generates `src/main/resources/static/css/output.css` from the classes used in `src/main/jte`.
While developing, keep the watcher running in a separate terminal so new classes are picked up:

```bash
npm run css:watch
```

### 4. Run the application

```bash
./mvnw spring-boot:run        # Linux / macOS
.\mvnw.cmd spring-boot:run    # Windows
```

Open <http://localhost:8080>. If port 8080 is already in use:

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

## Project structure

```
database/                     SQL scripts (schema.sql, seed.sql)
src/main/frontend/input.css   Tailwind entry file
src/main/jte/                 JTE templates (login.jte, home.jte)
src/main/resources/
  application.properties      App configuration
  static/css/output.css       Generated CSS
src/main/java/com/procuredox/login/
  LoginController             GET /login, GET /home
  SecurityConfig              Security rules, login/logout configuration, password encoder
  user/AppUser                JPA entity mapped to the users table
  user/UserRepository         Spring Data repository (findByEmail)
  user/DbUserDetailsService   Loads users from the database for Spring Security
```

## How it works

1. `GET /login` is handled by `LoginController`, which returns the `login` view (`login.jte`) together with the CSRF token.
2. The form posts to `POST /login`, which is handled by Spring Security (not by a controller).
3. Spring Security calls `DbUserDetailsService`, loads the user by email through `UserRepository`, and compares the submitted password with the stored BCrypt hash.
4. On success the user is redirected to `/home`; on failure back to `/login?error`.
5. `POST /logout` ends the session and redirects to `/login?logout`.

## Notes

- `gg.jte.developmentMode=true` is for development only. JTE templates should be precompiled for production.
- Passwords are stored as BCrypt hashes only. Never commit real credentials.
- `LoginApplicationTests` loads the full application context, so it needs a reachable database and `DB_PASSWORD` set.
