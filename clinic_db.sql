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

INSERT INTO SYSTEM_USER (User_ID, Username, Password_Hash, Role_Type) VALUES 
(2, 'dr_bader', 'doc123', 'Doctor'),
(4, 'rec', 'rec', 'User'),
(6, 'admin', 'admin', 'Admin'),
(7, 'doc2', 'doc2', 'Doctor'),
(8, 'rec2', 'rec2', 'User'),
(9, 'x', 'x', 'Doctor');

INSERT INTO DEPARTMENT VALUES 
(101, 'Cardiology'), 
(102, 'Pediatrics');

INSERT INTO PATIENT (Patient_ID, First_Name, Middle_Name, Last_Name, Phone_Number, Date_Of_Birth, Created_At) VALUES 
(1, 'Ahmed', NULL, 'Ali', '0501234567', '1995-05-15', '2026-05-13 14:00:04'),
(2, 'Sara', NULL, 'Salem', '0507654321', '1988-10-20', '2026-05-13 14:00:04'),
(101, 'Ahmed', 'Saad', 'Alshahrani', '050-111-2222', '1985-04-12', '2026-05-09 17:18:55'),
(102, 'Norah', 'Ali', 'Alqahtani', '055-222-3333', '1990-08-25', '2026-05-09 17:18:55'),
(104, 'Sara', NULL, 'Almutairi', '056-444-5555', '2005-02-14', '2026-05-09 17:18:55'),
(105, 'Abdulaziz', 'Dakhel', 'Alharthi', '050-555-6666', '1992-07-30', '2026-05-09 17:18:55'),
(106, 'Fatima', 'Saleh', 'Alghamdi', '054-666-7777', '1988-09-18', '2026-05-09 17:18:55'),
(109, 'Faris', 'Fouad', 'Alsayed', '050-999-0000', '1980-05-10', '2026-05-09 17:18:55'),
(110, 'Maha', 'Tariq', 'Aljuhani', '056-000-1111', '2010-10-08', '2026-05-09 17:18:55');

INSERT INTO DOCTOR (Doctor_ID, First_Name, Last_Name, Salary, Specialization, Dept_ID, User_ID) VALUES 
(501, 'Bader', 'Al-Fahd', 15000.00, 'Cardiologist', 101, 2),
(502, 'Abdulaziz', 'Alharthi', 20000.00, 'Cardiologist', 101, 7),
(503, 'red', 'x', 22222.00, 'Cardiologist', 101, 9);

INSERT INTO MEDICAL_RECORD (Record_ID, Blood_Type, Patient_ID, Doctor_ID, Clinical_Notes) VALUES 
(1, 'B', 1, 501, 'RED'),
(2, 'b', 102, 501, 'tews');

INSERT INTO EMERGENCY_CONTACT (Patient_ID, Contact_Name, Contact_Number, Relationship) VALUES 
(1, 'Az', '05000610634', 'Brother'),
(101, 'az', '@!@#12qwdas', 'ax'),
(105, 'Ahamd', '050000000', 'Bro');

INSERT INTO APPOINTMENT (Appt_ID, Patient_ID, Doctor_ID, Appt_Date, Appt_Time, Status) VALUES 
(12, 102, 501, '2026-05-05', '12:12:12', 'Scheduled'),
(1001, 1, 501, '2026-06-01', '10:00:00', 'Scheduled'),
(1002, 106, 502, '2026-06-05', '12:00:00', 'Scheduled'),
(1003, 102, 502, '2026-06-20', '22:00:00', 'Scheduled');