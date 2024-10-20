CREATE SCHEMA ylab_schema;

CREATE TYPE role AS ENUM ('USER', 'ADMIN');

CREATE TABLE IF NOT EXISTS ylab_schema.users (
	id bigint GENERATED ALWAYS AS IDENTITY,
	email varchar(255) NOT NULL,
	name varchar(255) NOT NULL,
	password varchar(255) NOT NULL,
	role varchar(255) NOT NULL,
	is_blocked bool DEFAULT false NOT NULL,
	CONSTRAINT users_email_key UNIQUE (email),
	CONSTRAINT users_pkey PRIMARY KEY (id)
);

INSERT INTO ylab_schema.users (name, email, password, role) VALUES
('Test User', 'tu@example.com', 'testuserpass123', 'USER');

CREATE TYPE frequency AS ENUM ('DAILY', 'WEEKLY');

CREATE TABLE IF NOT EXISTS ylab_schema.habits (
	id bigint GENERATED ALWAYS AS IDENTITY,
	title varchar(255) NOT NULL,
	description varchar(255) NOT NULL,
	frequency varchar(255) NOT NULL,
	user_id bigint NOT NULL,
	CONSTRAINT habits_pkey PRIMARY KEY (id),
	CONSTRAINT fk_habit_user FOREIGN KEY (user_id) REFERENCES ylab_schema.users(id)
);

INSERT INTO ylab_schema.habits (title, description, frequency, user_id) VALUES
('Go to shower', 'Go to shower every day', 'DAILY', 1);

CREATE TABLE IF NOT EXISTS ylab_schema.records (
	id bigint GENERATED ALWAYS AS IDENTITY,
	date timestamp NOT NULL,
	completed bool NOT NULL,
	habit_id bigint NOT NULL,
	CONSTRAINT records_pkey PRIMARY KEY (id),
	CONSTRAINT fk_record_habit FOREIGN KEY (habit_id) REFERENCES ylab_schema.habits(id)
);

INSERT INTO ylab_schema.records (date, completed, habit_id) VALUES
('2024-10-19 00:00:00.000', true, 1),
('2024-10-20 00:00:00.000', true, 1);
