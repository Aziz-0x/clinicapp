package clinicapp;
import java.sql.*;

public class MedicalRecord {
    private int recordId;
    private String bloodType;
    private int patientId;
    private int doctorId;
    private String clinicalNotes;

    public MedicalRecord(int recordId, String bloodType, int patientId, int doctorId, String clinicalNotes) {
        this.recordId = recordId;
        this.bloodType = bloodType;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.clinicalNotes = clinicalNotes;
    }

    public int getRecordId() { return recordId; }
    public String getBloodType() { return bloodType; }
    public int getPatientId() { return patientId; }
    public String getClinicalNotes() { return clinicalNotes; }
    
    
    public static void addRecord(int patId, int docId, String blood, String notes) throws Exception {
        int newId = DBConnection.getNextId("MEDICAL_RECORD", "Record_ID");
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO MEDICAL_RECORD VALUES (?,?,?,?,?)")) {
            ps.setInt(1, newId); ps.setString(2, blood); ps.setInt(3, patId); ps.setInt(4, docId); ps.setString(5, notes);
            ps.executeUpdate();
        }
    }

    public static String getRecordsInfo(int patId) {
        StringBuilder sb = new StringBuilder("Medical Records:\n\n");
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM MEDICAL_RECORD WHERE Patient_ID = ?")) {
            ps.setInt(1, patId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                sb.append("Blood Type: ").append(rs.getString("Blood_Type")).append("\n")
                  .append("Doc ID: ").append(rs.getInt("Doctor_ID")).append("\n")
                  .append("Notes: ").append(rs.getString("Clinical_Notes")).append("\n")
                  .append("----------------------------\n");
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return sb.toString();
    }
}