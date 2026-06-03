--liquibase formatted sql

--changeset mesh:001-create-users
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(500),
    date_of_birth DATE,
    password VARCHAR(500) NOT NULL
);

--changeset mesh:002-create-account
CREATE TABLE account (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id),
    balance DECIMAL(19, 2) NOT NULL CHECK (balance >= 0),
    start_balance DECIMAL(19, 2) NOT NULL CHECK (start_balance >= 0)
);

--changeset mesh:003-create-email-data
CREATE TABLE email_data (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    email VARCHAR(200) NOT NULL UNIQUE
);

--changeset mesh:004-create-phone-data
CREATE TABLE phone_data (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    phone VARCHAR(13) NOT NULL UNIQUE
);

--changeset mesh:005-insert-users
INSERT INTO users (id, name, date_of_birth, password)
VALUES
    (1, 'Ivan Petrov', DATE '1993-05-01', 'password1'),
    (2, 'Anna Sidorova', DATE '1990-07-15', 'password2'),
    (3, 'Petr Ivanov', DATE '1988-11-20', 'password3');

--changeset mesh:006-insert-accounts
INSERT INTO account (user_id, balance, start_balance)
VALUES
    (1, 1000.00, 1000.00),
    (2, 2500.00, 2500.00),
    (3, 500.00, 500.00);

--changeset mesh:007-insert-emails
INSERT INTO email_data (user_id, email)
VALUES
    (1, 'ivan.petrov@example.com'),
    (2, 'anna.sidorova@example.com'),
    (3, 'petr.ivanov@example.com');

--changeset mesh:008-insert-phones
INSERT INTO phone_data (user_id, phone)
VALUES
    (1, '79207865432'),
    (2, '79207865433'),
    (3, '79207865434');
