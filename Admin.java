package clinicapp;

public class Admin extends SystemUser {
    
    public Admin(String userId, String username, String passwordHash, String role) {
        super(userId, username, passwordHash, role);
    }

    public void manageSystem() {
        System.out.println("Admin " + getUsername() + " is managing the system.");
    }
}