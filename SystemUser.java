package clinicapp;

import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class SystemUser {
    public static String[] authenticate(String username, String password) throws Exception {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM SYSTEM_USER WHERE Username=? AND Password_Hash=?")) {
            ps.setString(1, username); ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return new String[]{rs.getString("User_ID"), rs.getString("Role_Type")};
        }
        return null;
    }

    public static void loadUsersDataForpanel(DefaultTableModel model) {
        model.setRowCount(0);
        try (Connection conn = DBConnection.getConnection(); 
             ResultSet rs = conn.createStatement().executeQuery("SELECT User_ID, Username, Role_Type FROM SYSTEM_USER WHERE Role_Type != 'Doctor'")) {
            while (rs.next()) model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3)});
        } catch (SQLException ex) { 
            ex.printStackTrace(); 
        }
    }
}
