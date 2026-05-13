CREATE DATABASE IF NOT EXISTS clinic_db;
USE clinic_db;

CREATE TABLE SYSTEM_USER (
    User_ID INT PRIMARY KEY AUTO_INCREMENT,
    Username VARCHAR(50) UNIQUE NOT NULL,
    Password_Hash VARCHAR(255) NOT NULL,
    Role_Type VARCHAR(20) NOT NULL
);

CREATE TABLE DEPARTMENT (
    Dept_ID INT PRIMARY KEY,
    Dept_Name VARCHAR(50) NOT NULL
);

CREATE TABLE PATIENT (
    Patient_ID INT PRIMARY KEY,
    First_Name VARCHAR(50) NOT NULL,
    Middle_Name VARCHAR(50),
    Last_Name VARCHAR(50) NOT NULL,
    Phone_Number VARCHAR(20) NOT NULL,
    Date_Of_Birth DATE NOT NULL,
    Created_At TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE DOCTOR (
    Doctor_ID INT PRIMARY KEY,
    First_Name VARCHAR(50) NOT NULL,
    Last_Name VARCHAR(50) NOT NULL,
    Salary DECIMAL(10,2) NOT NULL,
    Specialization VARCHAR(50) NOT NULL,
    Dept_ID INT,
    User_ID INT UNIQUE,
    FOREIGN KEY (Dept_ID) REFERENCES DEPARTMENT(Dept_ID),
    FOREIGN KEY (User_ID) REFERENCES SYSTEM_USER(User_ID)
);

CREATE TABLE MEDICAL_RECORD (
    Record_ID INT PRIMARY KEY,
    Blood_Type VARCHAR(5) NOT NULL,
    Patient_ID INT UNIQUE NOT NULL,
    Doctor_ID INT,
    Clinical_Notes TEXT,
    FOREIGN KEY (Patient_ID) REFERENCES PATIENT(Patient_ID),
    FOREIGN KEY (Doctor_ID) REFERENCES DOCTOR(Doctor_ID)
);

CREATE TABLE APPOINTMENT (
    Appt_ID INT PRIMARY KEY,
    Patient_ID INT NOT NULL,
    Doctor_ID INT NOT NULL,
    Appt_Date DATE NOT NULL,
    Appt_Time TIME NOT NULL,
    Status VARCHAR(20) DEFAULT 'Scheduled',
    FOREIGN KEY (Patient_ID) REFERENCES PATIENT(Patient_ID),
    FOREIGN KEY (Doctor_ID) REFERENCES DOCTOR(Doctor_ID),
    UNIQUE (Doctor_ID, Appt_Date, Appt_Time)
);

CREATE TABLE EMERGENCY_CONTACT (
    Patient_ID INT,
    Contact_Name VARCHAR(50),
    Contact_Number VARCHAR(20),
    Relationship VARCHAR(30) NOT NULL,
    PRIMARY KEY (Patient_ID, Contact_Number),
    FOREIGN KEY (Patient_ID) REFERENCES PATIENT(Patient_ID) ON DELETE CASCADE
);

CREATE TABLE TREATS (
    Treatment_ID INT PRIMARY KEY,
    Doctor_ID INT NOT NULL,
    Patient_ID INT NOT NULL,
    Diagnosis VARCHAR(100) NOT NULL,
    Treatment_Date DATE NOT NULL DEFAULT (CURRENT_DATE),
    FOREIGN KEY (Doctor_ID) REFERENCES DOCTOR(Doctor_ID),
    FOREIGN KEY (Patient_ID) REFERENCES PATIENT(Patient_ID)
);

INSERT INTO SYSTEM_USER (Username, Password_Hash, Role_Type) VALUES 
('aziz', '123', 'Admin'),
('dr_bader', 'doc123', 'Doctor'),
('reception', 'rec123', 'User');

INSERT INTO DEPARTMENT VALUES (101, 'Cardiology'), (102, 'Pediatrics');

INSERT INTO PATIENT (Patient_ID, First_Name, Last_Name, Phone_Number, Date_Of_Birth) VALUES 
(1, 'Ahmed', 'Ali', '0501234567', '1995-05-15'),
(2, 'Sara', 'Salem', '0507654321', '1988-10-20');

INSERT INTO DOCTOR (Doctor_ID, First_Name, Last_Name, Salary, Specialization, Dept_ID, User_ID) VALUES 
(501, 'Bader', 'Al-Fahd', 15000.00, 'Cardiologist', 101, 2);

INSERT INTO APPOINTMENT (Appt_ID, Patient_ID, Doctor_ID, Appt_Date, Appt_Time, Status) VALUES 
(1001, 1, 501, '2026-06-01', '10:00:00', 'Scheduled');