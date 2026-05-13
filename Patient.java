package clinicapp;

import java.util.Date;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
public class Patient {
    private String patientId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String dob;
    private String phoneNumber;

    public Patient(String patientId, String firstName, String middleName, String lastName, String dob, String phoneNumber) {
        this.patientId = patientId;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.dob = dob;
        this.phoneNumber = phoneNumber;
    }

    public String getFullName() {
        return firstName + " " + middleName + " " + lastName;
    }

    public String getPatientId() { return patientId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getDob() { return dob; }
    public String getPhoneNumber() { return phoneNumber; }
    
    
    public static void loadPatientsData(DefaultTableModel model, String currentRole, int currentDoctorId) {
        model.setRowCount(0);
        String sql = currentRole.equalsIgnoreCase("Doctor") ? 
            "SELECT DISTINCT p.*, " +
            "(SELECT COUNT(*) FROM EMERGENCY_CONTACT e WHERE e.Patient_ID = p.Patient_ID) as EmgCount, " +
            "(SELECT COUNT(*) FROM MEDICAL_RECORD m WHERE m.Patient_ID = p.Patient_ID) as MedCount " +
            "FROM PATIENT p JOIN APPOINTMENT a ON p.Patient_ID = a.Patient_ID WHERE a.Doctor_ID = ?" : 
            "SELECT p.*, " +
            "(SELECT COUNT(*) FROM EMERGENCY_CONTACT e WHERE e.Patient_ID = p.Patient_ID) as EmgCount, " +
            "(SELECT COUNT(*) FROM MEDICAL_RECORD m WHERE m.Patient_ID = p.Patient_ID) as MedCount " +
            "FROM PATIENT p";
            
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            if (currentRole.equalsIgnoreCase("Doctor")) ps.setInt(1, currentDoctorId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String emgDisplay = (rs.getInt("EmgCount") > 0) ? "Show" : "Add";
                String medDisplay = (rs.getInt("MedCount") > 0) ? "Show" : "Add";
                model.addRow(new Object[]{
                    rs.getInt("Patient_ID"), rs.getString("First_Name"), rs.getString("Last_Name"), 
                    rs.getString("Phone_Number"), rs.getDate("Date_Of_Birth"), emgDisplay, medDisplay
                });
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    public static void addPatient(String first, String last, String phone, String dob) throws Exception {
        int newId = DBConnection.getNextId("PATIENT", "Patient_ID");
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO PATIENT VALUES (?,?,?,?,?, CURRENT_TIMESTAMP)")) {
            ps.setInt(1, newId); ps.setString(2, first); ps.setString(3, last);
            ps.setString(4, phone); ps.setDate(5, Date.valueOf(dob));
            ps.executeUpdate();
        }
    }

    public static void deletePatient(int id) throws Exception {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM PATIENT WHERE Patient_ID=?")) {
            ps.setInt(1, id); ps.executeUpdate();
        }
    }
}