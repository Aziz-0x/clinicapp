
package clinicapp;

import javax.swing.table.DefaultTableModel;
import java.sql.*;
public class Appointment {

    // Getters
    
    public static void loadAppointmentsData(DefaultTableModel model, String currentRole, int currentDoctorId) {
        model.setRowCount(0);
//        System.out.println(currentDoctorId);
        String sql = currentRole.equalsIgnoreCase("Doctor") ? "SELECT * FROM APPOINTMENT WHERE Doctor_ID = ?" : "SELECT * FROM APPOINTMENT";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            if (currentRole.equalsIgnoreCase("Doctor")) ps.setInt(1, currentDoctorId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) model.addRow(new Object[]{rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getDate(4), rs.getTime(5), rs.getString(6)});
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    public static void addAppointment(int patId, int docId, String date, String time) throws Exception {
        int newId = DBConnection.getNextId("APPOINTMENT", "Appt_ID");
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO APPOINTMENT VALUES (?,?,?,?,?,'Scheduled')")) {
            ps.setInt(1, newId); ps.setInt(2, patId); ps.setInt(3, docId);
            ps.setDate(4, Date.valueOf(date)); ps.setTime(5, Time.valueOf(time));
            ps.executeUpdate();
        }
    }

    public static void deleteAppointment(int id) throws Exception {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement("DELETE FROM APPOINTMENT WHERE Appt_ID=?")) {
            ps.setInt(1, id); ps.executeUpdate();
        }
    }
}
