/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clinicapp;

/**
 *
 * @author aziz0x
 */

import java.sql.*;

public class User {
    
    public static void addUser(String username, String passwords, String role) throws Exception {
        int newId = DBConnection.getNextId("system_user", "User_ID");
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO system_user VALUES (?,?,?,?)")) {
            ps.setInt(1, newId); ps.setString(2, username); ps.setString(3, passwords);
            ps.setString(4, role);
            ps.executeUpdate();
        }
    }

    public static void deleteUser(int id) throws Exception {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement("DELETE FROM system_user WHERE User_ID=?")) {
            ps.setInt(1, id); ps.executeUpdate();
        }
    }
}
