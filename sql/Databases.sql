CREATE DATABASE event_management_system;
USE event_management_system;

-- STUDENT TABLE

CREATE TABLE users(
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    logged_in BOOLEAN DEFAULT FALSE
);

INSERT INTO users (email,password) VALUES ('test@srec.ac.in','123');
SELECT * FROM users;


-- EVENTS TABLE
CREATE TABLE events (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    description TEXT,
    start_time DATETIME,
    end_time DATETIME,
    venue VARCHAR(100),
    type VARCHAR(50),
    organizer VARCHAR(100),
    max_participants INT
);

SELECT * FROM events;

-- EVENT REGISTRATIONS (WITH APPROVAL)
CREATE TABLE event_registrations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    event_id INT,
    student_email VARCHAR(100),
    status ENUM('pending','approved') DEFAULT 'pending',
    FOREIGN KEY (event_id) REFERENCES events(id)
);

CREATE TABLE faculty (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL
);

INSERT INTO faculty (email, password)
VALUES ('ananthi@srec.ac.in', 'Srec@123');
INSERT INTO faculty (email, password)
VALUES ('vishnuvarthan@srec.ac.in', 'Srec@123');
INSERT INTO faculty (email, password)
VALUES ('rishi@srec.ac.in', 'Srec@123');

SELECT * FROM faculty;