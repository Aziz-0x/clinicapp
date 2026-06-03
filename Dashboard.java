package clinicapp;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.MaskFormatter;

public class Dashboard extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContainer;
    private JLabel lblWelcome;
    private JPanel dashboardButtonsPanel;

    private DefaultTableModel patientModel, apptModel, docModel, userModel;
    private JPanel patientInputPanel, docInputPanel, apptInputPanel;
    private JComboBox<Integer> apptPatientCombo, apptDoctorCombo;

    private String currentRole;
    private int currentUserId;
    private int currentDoctorId;

    private static class IntDocumentFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string != null && string.matches("\\d*")) super.insertString(fb, offset, string, attr);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text != null && text.matches("\\d*")) super.replace(fb, offset, length, text, attrs);
        }
    }

    private JTextField createIntField() {
        JTextField field = new JTextField();
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new IntDocumentFilter());
        return field;
    }

    private JFormattedTextField createDateField() {
        try {
            MaskFormatter formatter = new MaskFormatter("####-##-##");
            formatter.setPlaceholderCharacter('_');
            formatter.setValidCharacters("0123456789");
            return new JFormattedTextField(formatter);
        } catch (ParseException ex) {
            return new JFormattedTextField();
        }
    }

    private JFormattedTextField createTimeField() {
        try {
            MaskFormatter formatter = new MaskFormatter("##:##:##");
            formatter.setPlaceholderCharacter('_');
            formatter.setValidCharacters("0123456789");
            return new JFormattedTextField(formatter);
        } catch (ParseException ex) {
            return new JFormattedTextField();
        }
    }

    private void refreshIdCombo(JComboBox<Integer> combo, String tableName, String idColumn) {
        combo.removeAllItems();
        try (java.sql.Connection conn = DBConnection.getConnection();
             java.sql.Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery("SELECT " + idColumn + " FROM " + tableName + " ORDER BY " + idColumn)) {
            while (rs.next()) combo.addItem(rs.getInt(1));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Dashboard() {
        setTitle("Clinic System - MVC Architecture");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        mainContainer.add(createLoginPanel(), "Login");
        mainContainer.add(createDashboardPanel(), "Home");
        mainContainer.add(createPatientsPanel(), "Patients");
        mainContainer.add(createAppointmentsPanel(), "Appointments");
        mainContainer.add(createDoctorsPanel(), "Doctors");
        mainContainer.add(createUsersPanel(), "Users");

        add(mainContainer);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
//        panel.setBackground(new Color(45, 45, 45));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JTextField txtUsername = new JTextField(15);
        JPasswordField txtPassword = new JPasswordField(15);
        JButton btnLogin = new JButton("Login");
//        btnLogin.setBackground(new Color(70, 130, 180));
//        btnLogin.setForeground(Color.WHITE);

        btnLogin.addActionListener(e -> {
            try {
                String[] auth = SystemUser.authenticate(txtUsername.getText(), new String(txtPassword.getPassword()));
                if (auth != null) {
                    currentUserId = Integer.parseInt(auth[0]);
                    currentRole = auth[1];
                    
                    if (currentRole.equalsIgnoreCase("Doctor")) {
                        currentDoctorId = Doctor.getDoctorIdByUserId(currentUserId);
                    }
                    
                    lblWelcome.setText("User: " + txtUsername.getText() + " | Role: " + currentRole);
                    setupRoleBasedUI(); 
                    setSize(1000, 700); setLocationRelativeTo(null);
                    cardLayout.show(mainContainer, "Home");
                    txtUsername.setText(""); txtPassword.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Login!");
                }
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "DB Error"); }
        });

        JLabel lblU = new JLabel("Username:"); // lblU.setForeground(Color.WHITE);
        JLabel lblP = new JLabel("Password:"); // lblP.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 0; panel.add(lblU, gbc); gbc.gridx = 1; panel.add(txtUsername, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(lblP, gbc); gbc.gridx = 1; panel.add(txtPassword, gbc);
        gbc.gridx = 1; gbc.gridy = 2; panel.add(btnLogin, gbc);
        return panel;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        lblWelcome = new JLabel("Welcome", SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(lblWelcome, BorderLayout.NORTH);
        dashboardButtonsPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        panel.add(dashboardButtonsPanel, BorderLayout.CENTER);
        return panel;
    }

    private void setupRoleBasedUI() {
        dashboardButtonsPanel.removeAll();
        JButton btnPatients = new JButton("Patients Hub");
        JButton btnAppts = new JButton("Appointments");
        JButton btnDocs = new JButton("Doctors");
        JButton btnUsers = new JButton("System Users");
        JButton btnLogout = new JButton("Logout");

        btnPatients.addActionListener(e -> { Patient.loadPatientsData(patientModel, currentRole, currentDoctorId); cardLayout.show(mainContainer, "Patients"); });
        btnAppts.addActionListener(e -> {
            refreshIdCombo(apptPatientCombo, "PATIENT", "Patient_ID");
            refreshIdCombo(apptDoctorCombo, "DOCTOR", "Doctor_ID");
            Appointment.loadAppointmentsData(apptModel, currentRole, currentDoctorId);
            cardLayout.show(mainContainer, "Appointments");
        });
        btnDocs.addActionListener(e -> { Doctor.loadDoctorsData(docModel); cardLayout.show(mainContainer, "Doctors"); });
        btnUsers.addActionListener(e -> { SystemUser.loadUsersDataForpanel(userModel); cardLayout.show(mainContainer, "Users"); });
        btnLogout.addActionListener(e -> { setSize(400, 250); setLocationRelativeTo(null); cardLayout.show(mainContainer, "Login"); });

        dashboardButtonsPanel.add(btnPatients); dashboardButtonsPanel.add(btnAppts);
        if (!currentRole.equalsIgnoreCase("Doctor")) dashboardButtonsPanel.add(btnDocs);
        if (currentRole.equalsIgnoreCase("Admin")) dashboardButtonsPanel.add(btnUsers);
        dashboardButtonsPanel.add(btnLogout);

        patientInputPanel.setVisible(!currentRole.equalsIgnoreCase("Doctor"));
        docInputPanel.setVisible(currentRole.equalsIgnoreCase("Admin"));
        apptInputPanel.setVisible(!currentRole.equalsIgnoreCase("Doctor"));

        dashboardButtonsPanel.revalidate(); dashboardButtonsPanel.repaint();
    }

    private JPanel createPatientsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        patientInputPanel = new JPanel(new GridLayout(1, 9, 5, 5));
        patientInputPanel.setBorder(BorderFactory.createTitledBorder("Add Patient (Auto ID)"));
        JTextField fFirst = new JTextField(); JTextField fLast = new JTextField(); 
        JTextField fPhone = createIntField(); JTextField fDOB = createDateField();
        JButton btnAdd = new JButton("Add");

        patientInputPanel.add(new JLabel("First:")); patientInputPanel.add(fFirst);
        patientInputPanel.add(new JLabel("Last:")); patientInputPanel.add(fLast);
        patientInputPanel.add(new JLabel("Phone:")); patientInputPanel.add(fPhone);
        patientInputPanel.add(new JLabel("DOB:")); patientInputPanel.add(fDOB);
        patientInputPanel.add(btnAdd);

        patientModel = new DefaultTableModel(new String[]{"ID", "First Name", "Last Name", "Phone", "DOB", "Emergency", "Medical Record"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(patientModel);

        DefaultTableCellRenderer btnRenderer = new DefaultTableCellRenderer() {
            JButton btn = new JButton();
            @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                btn.setText((value == null) ? "" : value.toString());
                btn.setBackground(UIManager.getColor("Button.background"));
                return btn;
            }
        };
        table.getColumn("Emergency").setCellRenderer(btnRenderer);
        table.getColumn("Medical Record").setCellRenderer(btnRenderer);

        table.addMouseListener(new MouseAdapter() {
            @Override 
            public void mouseClicked(MouseEvent e) {
                int col = table.getColumnModel().getColumnIndexAtX(e.getX());
                int row = e.getY() / table.getRowHeight();
                if (row >= 0 && col >= 0) {
                    String colName = table.getColumnName(col);
                    Object cellValue = table.getValueAt(row, col);
                    String action = cellValue != null ? String.valueOf(cellValue) : "";
                    Object idValue = table.getValueAt(row, 0);
                    int patId = idValue instanceof Number ? ((Number) idValue).intValue() : Integer.parseInt(idValue.toString());
                    if (colName.equals("Emergency")) {
                        if ("Show".equals(action)) JOptionPane.showMessageDialog(panel, EmergencyContact.getContactsInfo(patId));
                        else if (!currentRole.equalsIgnoreCase("Doctor")) addEmergencyDialog(patId);
                        else JOptionPane.showMessageDialog  (panel, "Doctors cannot add emergencies.");
                    } else if (colName.equals("Medical Record")) {
                        if ("Show".equals(action)) JOptionPane.showMessageDialog(panel, MedicalRecord.getRecordsInfo(patId));
                        else if (!currentRole.equalsIgnoreCase("StandardUser")) addMedicalDialog(patId);
                        else JOptionPane.showMessageDialog(panel, "Receptionists cannot add medical records.");
                    }
                }
            }
        });

        JButton btnRemove = new JButton("Remove Selected");
        btnRemove.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                try { Patient.deletePatient((int) patientModel.getValueAt(row, 0)); Patient.loadPatientsData(patientModel, currentRole, currentDoctorId); } 
                catch (Exception ex) { JOptionPane.showMessageDialog(panel, "Error"); }
            }
        });

        btnAdd.addActionListener(e -> {
            try {
                Patient.addPatient(fFirst.getText(), fLast.getText(), fPhone.getText(), fDOB.getText());
                Patient.loadPatientsData(patientModel, currentRole, currentDoctorId);
            }
            catch (Exception ex) { JOptionPane.showMessageDialog(panel, "Use a valid date: YYYY-MM-DD"); }
        });

        JPanel bottom = new JPanel(new BorderLayout());
        JPanel left = new JPanel(); left.add(btnRemove); bottom.add(left, BorderLayout.WEST);
        JButton btnBack = new JButton("Back"); btnBack.addActionListener(e -> cardLayout.show(mainContainer, "Home")); bottom.add(btnBack, BorderLayout.EAST);
        panel.add(patientInputPanel, BorderLayout.NORTH); panel.add(new JScrollPane(table), BorderLayout.CENTER); panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private void addEmergencyDialog(int patId) {
        JPanel p = new JPanel(new GridLayout(3, 2));
        JTextField fName = new JTextField(); JTextField fPhone = new JTextField(); JTextField fRel = new JTextField();
        p.add(new JLabel("Name:")); p.add(fName); p.add(new JLabel("Phone:")); p.add(fPhone); p.add(new JLabel("Relation:")); p.add(fRel);
        if (JOptionPane.showConfirmDialog(this, p, "Add Emergency", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try { EmergencyContact.addContact(patId, fName.getText(), fPhone.getText(), fRel.getText()); Patient.loadPatientsData(patientModel, currentRole, currentDoctorId); } 
            catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error"); }
        }
    }

    private void addMedicalDialog(int patId) {
        if (currentRole.equalsIgnoreCase("User")){
            { JOptionPane.showMessageDialog(this, "Only doctor can add record"); }
            return;
        }
        JPanel p = new JPanel(new BorderLayout(5, 5));
        JPanel top = new JPanel(new GridLayout(2, 2));
        JTextField fBlood = new JTextField();
        JTextField fDocId = createIntField();
        fDocId.setText(currentRole.equalsIgnoreCase("Doctor") ? String.valueOf(currentDoctorId) : "");
        fDocId.setEditable(!currentRole.equalsIgnoreCase("Doctor"));
        JTextArea fNotes = new JTextArea(5, 25);
        fNotes.setLineWrap(true);
        fNotes.setWrapStyleWord(true);
        top.add(new JLabel("Blood Type:")); top.add(fBlood); top.add(new JLabel("Doc ID:")); top.add(fDocId);
        p.add(top, BorderLayout.NORTH);
        p.add(new JLabel("Notes:"), BorderLayout.WEST);
        p.add(new JScrollPane(fNotes), BorderLayout.CENTER);
        if (JOptionPane.showConfirmDialog(this, p, "Add Medical Record", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try { MedicalRecord.addRecord(patId, Integer.parseInt(fDocId.getText()), fBlood.getText(), fNotes.getText()); Patient.loadPatientsData(patientModel, currentRole, currentDoctorId); } 
            catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error"); }
        }
    }

    private JPanel createAppointmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        apptInputPanel = new JPanel(new GridLayout(1, 9, 5, 5));
        apptInputPanel.setBorder(BorderFactory.createTitledBorder("Add Appointment (Auto ID)"));
        apptPatientCombo = new JComboBox<>(); apptDoctorCombo = new JComboBox<>();
        JTextField fDate = createDateField(); JTextField fTime = createTimeField();
        JButton btnAdd = new JButton("Add");
        refreshIdCombo(apptPatientCombo, "PATIENT", "Patient_ID");
        refreshIdCombo(apptDoctorCombo, "DOCTOR", "Doctor_ID");

        apptInputPanel.add(new JLabel("Pat ID:")); apptInputPanel.add(apptPatientCombo);
        apptInputPanel.add(new JLabel("Doc ID:")); apptInputPanel.add(apptDoctorCombo);
        apptInputPanel.add(new JLabel("Date:")); apptInputPanel.add(fDate);
        apptInputPanel.add(new JLabel("Time:")); apptInputPanel.add(fTime); apptInputPanel.add(btnAdd);

        apptModel = new DefaultTableModel(new String[]{"Appt ID", "Patient ID", "Doctor ID", "Date", "Time", "Status"}, 0);
        JTable table = new JTable(apptModel);

        btnAdd.addActionListener(e -> {
            try {
                Appointment.addAppointment((Integer) apptPatientCombo.getSelectedItem(), (Integer) apptDoctorCombo.getSelectedItem(), fDate.getText(), fTime.getText());
                Appointment.loadAppointmentsData(apptModel, currentRole, currentDoctorId);
            } 
            catch (Exception ex) { JOptionPane.showMessageDialog(panel, "Error"); }
        });

        JButton btnRemove = new JButton("Remove Selected");
        btnRemove.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) try { Appointment.deleteAppointment((int) apptModel.getValueAt(row, 0)); Appointment.loadAppointmentsData(apptModel, currentRole, currentDoctorId); } catch(Exception ex){}
        });

        JPanel bottom = new JPanel(new BorderLayout()); bottom.add(btnRemove, BorderLayout.WEST);
        JButton btnBack = new JButton("Back"); btnBack.addActionListener(e -> cardLayout.show(mainContainer, "Home")); bottom.add(btnBack, BorderLayout.EAST);
        panel.add(apptInputPanel, BorderLayout.NORTH); panel.add(new JScrollPane(table), BorderLayout.CENTER); panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createDoctorsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        docInputPanel = new JPanel(new GridLayout(1, 13, 5, 5));
        docInputPanel.setBorder(BorderFactory.createTitledBorder("Add Doctor (Auto ID)"));
        JTextField fFirst = new JTextField(); JTextField fLast = new JTextField(); JTextField fSal = new JTextField(); 
        JTextField fSpec = new JTextField(); JTextField fDept = createIntField(); JTextField fUsername = new JTextField();
        JPasswordField fPassword = new JPasswordField();
        JButton btnAdd = new JButton("Add");

        docInputPanel.add(new JLabel("First Name:")); docInputPanel.add(fFirst); docInputPanel.add(new JLabel("Last Name:")); docInputPanel.add(fLast);
        docInputPanel.add(new JLabel("Salary:")); docInputPanel.add(fSal); docInputPanel.add(new JLabel("Special:")); docInputPanel.add(fSpec);
        docInputPanel.add(new JLabel("Dept:")); docInputPanel.add(fDept); docInputPanel.add(new JLabel("Username:")); docInputPanel.add(fUsername);
        docInputPanel.add(new JLabel("Password:")); docInputPanel.add(fPassword); docInputPanel.add(btnAdd);

        docModel = new DefaultTableModel(new String[]{"Doctor ID", "First Name", "Last Name", "Salary", "Special", "Dept", "User ID"}, 0);
        JTable table = new JTable(docModel);

        btnAdd.addActionListener(e -> {
            try {
                Doctor.addDoctor(fFirst.getText(), fLast.getText(), Double.parseDouble(fSal.getText()), fSpec.getText(), Integer.parseInt(fDept.getText()), fUsername.getText(), new String(fPassword.getPassword()));
                Doctor.loadDoctorsData(docModel);
                SystemUser.loadUsersDataForpanel(userModel);
            }
            catch (Exception ex) { JOptionPane.showMessageDialog(panel, "Error"); }
        });

        JButton btnRemove = new JButton("Remove Selected");
        btnRemove.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) try { Doctor.deleteDoctor((int) docModel.getValueAt(row, 0)); Doctor.loadDoctorsData(docModel); SystemUser.loadUsersDataForpanel(userModel); } catch(Exception ex){}
        });

        JPanel bottom = new JPanel(new BorderLayout()); bottom.add(btnRemove, BorderLayout.WEST);
        JButton btnBack = new JButton("Back"); btnBack.addActionListener(e -> cardLayout.show(mainContainer, "Home")); bottom.add(btnBack, BorderLayout.EAST);
        panel.add(docInputPanel, BorderLayout.NORTH); panel.add(new JScrollPane(table), BorderLayout.CENTER); panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JPanel userInputPanel = new JPanel(new GridLayout(1, 7, 5, 5));
        userInputPanel.setBorder(BorderFactory.createTitledBorder("Add User (Auto ID)"));
        
        JTextField fUsername = new JTextField();
        JPasswordField fPassword = new JPasswordField();
        String[] roleOptions = {"Admin", "User"};
        JComboBox<String> fRole = new JComboBox<>(roleOptions); 
        JButton btnAdd = new JButton("Add");

        userInputPanel.add(new JLabel("Username:")); 
        userInputPanel.add(fUsername);
        userInputPanel.add(new JLabel("Password:")); 
        userInputPanel.add(fPassword);
        userInputPanel.add(new JLabel("Role:")); 
        userInputPanel.add(fRole);
        userInputPanel.add(btnAdd);

        userModel = new DefaultTableModel(new String[]{"User ID", "Username", "Role Type"}, 0);
        JTable table = new JTable(userModel);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                int modelRow = table.convertRowIndexToModel(row);
                int rowUserId = (int) userModel.getValueAt(modelRow, 0);
                
                if (rowUserId == currentUserId) {
                    c.setBackground(new Color(173, 216, 230)); // Light Blue
                    c.setForeground(Color.BLACK);
                } else if (!isSelected) {
                    c.setBackground(table.getBackground());
                    c.setForeground(table.getForeground());
                }
                return c;
            }
        });

        btnAdd.addActionListener(e -> {
            try {
                String password = new String(fPassword.getPassword());
                String selectedRole = (String) fRole.getSelectedItem();
                
                User.addUser(fUsername.getText(), password, selectedRole);
                User.loadUsersDataForpanel(userModel);
                
                fUsername.setText("");
                fPassword.setText("");
                fRole.setSelectedIndex(0);
            } catch (Exception ex) { 
                JOptionPane.showMessageDialog(panel, "Error adding user."); 
            }
        });

        JButton btnRemove = new JButton("Remove Selected");
        btnRemove.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int modelRow = table.convertRowIndexToModel(row);
                int selectedUserId = (int) userModel.getValueAt(modelRow, 0);
                
                // Prevent the active user from deleting their own account
                if (selectedUserId == currentUserId) {
                    JOptionPane.showMessageDialog(panel, "You cannot remove your own active account.");
                    return;
                }
                
                try { 
                    User.deleteUser(selectedUserId); 
                    User.loadUsersDataForpanel(userModel); 
                } catch(Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Error deleting user.");
                }
            }
        });

        JButton btnBack = new JButton("Back"); 
        btnBack.addActionListener(e -> cardLayout.show(mainContainer, "Home"));
        
        JPanel bottom = new JPanel(new BorderLayout()); 
        bottom.add(btnRemove, BorderLayout.WEST);
        bottom.add(btnBack, BorderLayout.EAST);

        panel.add(userInputPanel, BorderLayout.NORTH); 
        panel.add(new JScrollPane(table), BorderLayout.CENTER); 
        panel.add(bottom, BorderLayout.SOUTH);
        
        return panel;
    }

    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new Dashboard().setVisible(true)); }
}
