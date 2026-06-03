package clinicapp;

import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class Doctor {
    
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

    public static void addDoctor(String first, String last, double salary, String spec, int deptId, String username, String password) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int userId = getNextId(conn, "SYSTEM_USER", "User_ID");
                int doctorId = getNextId(conn, "DOCTOR", "Doctor_ID");

                try (PreparedStatement ps = conn.prepareStatement("INSERT INTO SYSTEM_USER VALUES (?,?,?,?)")) {
                    ps.setInt(1, userId);
                    ps.setString(2, username);
                    ps.setString(3, password);
                    ps.setString(4, "Doctor");
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement("INSERT INTO DOCTOR VALUES (?,?,?,?,?,?,?)")) {
                    ps.setInt(1, doctorId);
                    ps.setString(2, first);
                    ps.setString(3, last);
                    ps.setDouble(4, salary);
                    ps.setString(5, spec);
                    ps.setInt(6, deptId);
                    ps.setInt(7, userId);
                    ps.executeUpdate();
                }

                conn.commit();
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    private static int getNextId(Connection conn, String tableName, String idColumn) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT MAX(" + idColumn + ") FROM " + tableName)) {
            if (rs.next()) return rs.getInt(1) + 1;
        }
        return 1;
    }

    public static void deleteDoctor(int id) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Integer userId = null;
                try (PreparedStatement ps = conn.prepareStatement("SELECT User_ID FROM DOCTOR WHERE Doctor_ID=?")) {
                    ps.setInt(1, id);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) userId = rs.getInt(1);
                }

                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM DOCTOR WHERE Doctor_ID=?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }

                if (userId != null) {
                    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM SYSTEM_USER WHERE User_ID=?")) {
                        ps.setInt(1, userId);
                        ps.executeUpdate();
                    }
                }

                conn.commit();
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}
