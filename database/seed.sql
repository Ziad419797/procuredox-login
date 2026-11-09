USE procuredox_login;

INSERT INTO users (first_name, last_name, email, phone, password_hash, enabled)
VALUES ('Admin', 'User', 'admin@test.com', '01000000000', '$2a$10$tLpkn6F2B/q/hT7D6Ou9WObrI.A6sjvidsFrI/xstJKnNjGC.lahm', TRUE);