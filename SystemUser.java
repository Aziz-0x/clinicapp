package clinicapp;

import javax.swing.table.DefaultTableModel;
import java.sql.*;

public abstract class SystemUser {
    protected String userId;
    protected String username;
    protected String passwordHash;
    protected String role;

    public SystemUser(String userId, String username, String passwordHash, String role) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }
    
    public static String[] authenticate(String username, String password) throws Exception {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM SYSTEM_USER WHERE Username=? AND Password_Hash=?")) {
            ps.setString(1, username); ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return new String[]{rs.getString("User_ID"), rs.getString("Role_Type")};
        }
        return null;
    }

    public static void loadUsersData(DefaultTableModel model) {
        model.setRowCount(0);
        try (Connection conn = DBConnection.getConnection(); 
             ResultSet rs = conn.createStatement().executeQuery("SELECT User_ID, Username, Role_Type FROM SYSTEM_USER")) {
            while (rs.next()) model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3)});
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    public boolean login(String inputUsername, String inputPassword) {
        return this.username.equals(inputUsername);
    }

    public void logout() {
        System.out.println("User logged out.");
    }
    
    public String getUsername() { return username; }
    public String getRole() { return role; }
    
    
    
}