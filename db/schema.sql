CREATE TABLE roles (
  id   SERIAL PRIMARY KEY,
  name VARCHAR(20) UNIQUE NOT NULL          -- STUDENT, LIBRARIAN
);

CREATE TABLE users (
  id            SERIAL PRIMARY KEY,
  username      VARCHAR(50) UNIQUE NOT NULL,
  password_hash VARCHAR(100) NOT NULL,
  role_id       INT NOT NULL REFERENCES roles(id)
);

-- Eén tabel voor de hele overervingshiërarchie (single table inheritance)
CREATE TABLE library_items (
  id           SERIAL PRIMARY KEY,
  item_type    VARCHAR(10) NOT NULL CHECK (item_type IN ('BOOK','MAGAZINE')),
  title        VARCHAR(200) NOT NULL,
  year         INT NOT NULL,
  author       VARCHAR(100),                -- alleen Book
  issue_number INT                          -- alleen Magazine
);

CREATE TABLE loans (
  id          SERIAL PRIMARY KEY,
  user_id     INT NOT NULL REFERENCES users(id),
  item_id     INT NOT NULL REFERENCES library_items(id),
  borrowed_at DATE NOT NULL DEFAULT CURRENT_DATE,
  returned_at DATE
);

INSERT INTO roles(name) VALUES ('STUDENT'), ('LIBRARIAN');
