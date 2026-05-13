package clinicapp;

import java.sql.*;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/clinic_db";
    private static final String USER = "root";
    private static final String PASSWORD = ""; 

    public static Connection getConnection() throws SQLException {
        try { Class.forName("com.mysql.cj.jdbc.Driver"); } 
        catch (ClassNotFoundException e) { System.err.println("Driver not found!"); }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // دالة الترقيم التلقائي الذكية
    public static int getNextId(String tableName, String idColumn) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT MAX(" + idColumn + ") FROM " + tableName)) {
            if (rs.next()) return rs.getInt(1) + 1;
        } catch (SQLException e) { e.printStackTrace(); }
        return 1;
    }
}