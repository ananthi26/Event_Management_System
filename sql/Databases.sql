CREATE DATABASE event_management_system;
USE event_management_system;

CREATE TABLE student (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL
);

INSERT INTO student VALUES
(1,'ananthi.2301013@srec.ac.in','Anvii@2602');

INSERT INTO student VALUES
(2,'vishnu.2301001@srec.ac.in','Vishnu@2602');

INSERT INTO student VALUES
(3,'ashmitha.2301023@srec.ac.in','Ash@123');

SELECT * FROM student;

CREATE TABLE faculty (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL
);

INSERT INTO faculty VALUES
(1,'sudha.g@srec.ac.in','Srec@123');

SELECT * FROM faculty;


CREATE TABLE events (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    venue VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    organizer VARCHAR(100) NOT NULL,
    max_participants INT NOT NULL
);
SELECT * FROM events;

ALTER TABLE events ADD COLUMN total_participants INT;

CREATE TABLE event_registrations (
  id INT AUTO_INCREMENT PRIMARY KEY,
  event_id INT,
  student_email VARCHAR(100),
  registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (event_id) REFERENCES events(id)
);

SELECT * FROM event_registrations;

