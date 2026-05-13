/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clinicapp;

/**
 *
 * @author aziz0x
 */

import java.io.*;
import java.sql.*;

public class User extends SystemUser {

    public User(String userId, String username, String passwordHash, String role) {
        super(userId, username, passwordHash, role);
    }

    public void registerPatient(Patient p) {
        String sql = "INSERT INTO PATIENT (patient_id, first_name, middle_name, last_name, dob, phone_number) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, p.getPatientId());
            pstmt.setString(2, p.getFirstName());
            pstmt.setString(3, p.getLastName());
            pstmt.setString(4, p.getLastName()); 
            pstmt.setString(5, p.getDob());
            pstmt.setString(6, p.getPhoneNumber());
            
            pstmt.executeUpdate();
            System.out.println("Patient " + p.getFullName() + " registered successfully!");
            
        } catch (SQLException e) {
            System.err.println("Database Error in Registration: " + e.getMessage());
        }
    }

    public void exportPatientData(Patient p, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("Clinic Medical History Report ");
            writer.println("Patient ID: " + p.getPatientId());
            writer.println("Name: " + p.getFullName());
            writer.println("DOB: " + p.getDob());
            writer.println("Phone: " + p.getPhoneNumber());
            System.out.println("Data exported to: " + filePath);
        } catch (IOException e) {
            System.err.println("File Export Error: " + e.getMessage());
        }
    }
}
