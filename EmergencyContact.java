package clinicapp;

import java.sql.*;

public class EmergencyContact {
    private int patientId;
    private String contactName;
    private String contactNumber;
    private String relationship;

    public EmergencyContact(int patientId, String contactName, String contactNumber, String relationship) {
        this.patientId = patientId;
        this.contactName = contactName;
        this.contactNumber = contactNumber;
        this.relationship = relationship;
    }

    // Getters
    public int getPatientId() { return patientId; }
    public String getContactName() { return contactName; }
    public String getContactNumber() { return contactNumber; }
    public String getRelationship() { return relationship; }
    
    public static void addContact(int patId, String name, String phone, String relation) throws Exception {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO EMERGENCY_CONTACT VALUES (?,?,?,?)")) {
            ps.setInt(1, patId); ps.setString(2, name); ps.setString(3, phone); ps.setString(4, relation);
            ps.executeUpdate();
        }
    }

    public static String getContactsInfo(int patId) {
        StringBuilder sb = new StringBuilder("Emergency Contacts:\n\n");
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM EMERGENCY_CONTACT WHERE Patient_ID = ?")) {
            ps.setInt(1, patId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                sb.append("Name: ").append(rs.getString("Contact_Name")).append(" | Phone: ").append(rs.getString("Contact_Number"))
                  .append(" | Relation: ").append(rs.getString("Relationship")).append("\n");
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return sb.toString();
    }
}