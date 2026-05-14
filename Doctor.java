package clinicapp;

import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class Doctor extends SystemUser {
    private int doctorId;
    private String firstName;
    private String lastName;
    private double salary;
    private String specialization;
    private int deptId;

    // Constructor to initialize superclass and doctor-specific fields
    public Doctor(int userId, String username, String passwordHash, String role, 
                  int doctorId, String firstName, String lastName, double salary, String specialization, int deptId) {
        
        super(userId, username, passwordHash, role); 
        this.doctorId = doctorId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.salary = salary;
        this.specialization = specialization;
        this.deptId = deptId;
    }

    // Getters and Setters
    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public int getDeptId() { return deptId; }
    public void setDeptId(int deptId) { this.deptId = deptId; }
    
    
    public static int getDoctorIdByUserId(int userId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT Doctor_ID FROM DOCTOR WHERE User_ID = ?")) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException ex) { ex.printStackTrace(); }
        return -1;
    }

    public static void loadDoctorsData(DefaultTableModel model) {
        model.setRowCount(0);
        try (Connection conn = DBConnection.getConnection(); ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM DOCTOR")) {
            while (rs.next()) model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDouble(4), rs.getString(5), rs.getInt(6), rs.getInt(7)});
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    public static void addDoctor(String first, String last, double salary, String spec, int deptId, int userId) throws Exception {
        int newId = DBConnection.getNextId("DOCTOR", "Doctor_ID");
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO DOCTOR VALUES (?,?,?,?,?,?,?)")) {
            ps.setInt(1, newId); ps.setString(2, first); ps.setString(3, last);
            ps.setDouble(4, salary); ps.setString(5, spec); ps.setInt(6, deptId); ps.setInt(7, userId);
            ps.executeUpdate();
        }
    }

    public static void deleteDoctor(int id) throws Exception {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement("DELETE FROM DOCTOR WHERE Doctor_ID=?")) {
            ps.setInt(1, id); ps.executeUpdate();
        }
    }
}