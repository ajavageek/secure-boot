CREATE SCHEMA secure;

CREATE TABLE secure.employee (
    user_name VARCHAR(50) PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    salary NUMERIC(10,0) NOT NULL,
    manager_id VARCHAR(50),
    FOREIGN KEY (manager_id) REFERENCES secure.employee(user_name)
);

CREATE TABLE secure.account (
    id VARCHAR(50) PRIMARY KEY,
    password VARCHAR(50) NOT NULL
);
