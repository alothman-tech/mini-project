-- Library Management System: schema + testdata
-- Uitvoeren:  createdb library   daarna   psql -d library -f db/schema.sql

DROP TABLE IF EXISTS loans, library_items, users, roles CASCADE;

CREATE TABLE roles (
  id   SERIAL PRIMARY KEY,
  name VARCHAR(20) UNIQUE NOT NULL            -- STUDENT, LIBRARIAN
);

CREATE TABLE users (
  id            SERIAL PRIMARY KEY,
  username      VARCHAR(50) UNIQUE NOT NULL,
  password_hash VARCHAR(200) NOT NULL,
  role_id       INT NOT NULL REFERENCES roles(id)
);

-- Eén tabel voor de hele overervingshiërarchie (single table inheritance)
CREATE TABLE library_items (
  id           SERIAL PRIMARY KEY,
  item_type    VARCHAR(10) NOT NULL CHECK (item_type IN ('BOOK','MAGAZINE')),
  title        VARCHAR(200) NOT NULL,
  year         INT NOT NULL,
  author       VARCHAR(100),                  -- alleen Book
  issue_number INT                            -- alleen Magazine
);

CREATE TABLE loans (
  id          SERIAL PRIMARY KEY,
  user_id     INT NOT NULL REFERENCES users(id),
  item_id     INT NOT NULL REFERENCES library_items(id),
  borrowed_at DATE NOT NULL DEFAULT CURRENT_DATE,
  returned_at DATE
);

-- Een item kan maar één openstaande uitlening tegelijk hebben (vangnet tegen dubbel lenen)
CREATE UNIQUE INDEX one_open_loan_per_item ON loans(item_id) WHERE returned_at IS NULL;

INSERT INTO roles(name) VALUES ('STUDENT'), ('LIBRARIAN');

-- Demo-accounts: student1 / student123  en  librarian1 / librarian123
INSERT INTO users(username, password_hash, role_id) VALUES
  ('student1',   '65536:VGFU/uXFNpYTHapaZOe1AQ==:Z820KosDDK8/vNuv/SYHHciEbAeRdBDABnhOHi0EdLY=',   (SELECT id FROM roles WHERE name='STUDENT')),
  ('librarian1', '65536:tRx3xPnFQEsOS8QLgPm3uQ==:3CUMR9HVZxEWa3MRNSl0lxktIaW6TyFoFbuvi0O+/0E=', (SELECT id FROM roles WHERE name='LIBRARIAN'));

INSERT INTO library_items(item_type, title, year, author, issue_number) VALUES
  ('BOOK',     'Clean Code',            2008, 'Robert C. Martin', NULL),
  ('BOOK',     'Refactoring',           1999, 'Martin Fowler',    NULL),
  ('BOOK',     'Head First Java',       2005, 'Kathy Sierra',     NULL),
  ('MAGAZINE', 'Wired',                 2024, NULL,               7),
  ('MAGAZINE', 'Computer Weekly',       2025, NULL,               42);
