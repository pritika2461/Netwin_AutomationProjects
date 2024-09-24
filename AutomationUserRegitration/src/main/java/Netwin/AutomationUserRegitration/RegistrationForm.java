package Netwin.AutomationUserRegitration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.regex.*;

@SuppressWarnings("serial")
public class RegistrationForm extends JFrame implements ActionListener {

    // Form fields for user input
    private JTextField userIdField, userNameField, mobNoField, createdByField;
    private JPasswordField passwordField;
    private JComboBox<String> statusDropdown, logLevelDropdown;
    private JButton registerButton, cancelButton;

    // Constructor to set up the form layout and components
    public RegistrationForm() {
        setTitle("User Registration Form");
        setLayout(new GridBagLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title label configuration
        JLabel titleLabel = new JLabel("Automation Tester Registration", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.BLACK);

        // Create input fields
        userIdField = createTextField();
        passwordField = new JPasswordField();
        userNameField = createTextField();
        mobNoField = createTextField();
        createdByField = createTextField();

        // Create dropdowns for status and log level
        String[] statusOptions = {"Active", "Inactive"};
        statusDropdown = new JComboBox<>(statusOptions);

        String[] logLevelOptions = {"1", "2"};
        logLevelDropdown = new JComboBox<>(logLevelOptions);

        // Create buttons
        registerButton = new JButton("Register");
        registerButton.addActionListener(this);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);

        // Add title to the form
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(titleLabel, gbc);

        // Add User ID field
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(createLabel("User ID"), gbc);
        gbc.gridx = 1;
        add(userIdField, gbc);

        // Add Password field
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        add(createLabel("Password"), gbc);
        gbc.gridx = 1;
        add(passwordField, gbc);

        // Add User Name field
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        add(createLabel("User Name"), gbc);
        gbc.gridx = 1;
        add(userNameField, gbc);

        // Add Mobile No field
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        add(createLabel("Mobile No"), gbc);
        gbc.gridx = 1;
        add(mobNoField, gbc);

        // Add Status dropdown
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.EAST;
        add(createLabel("Status"), gbc);
        gbc.gridx = 1;
        add(statusDropdown, gbc);

        // Add Created By field
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.EAST;
        add(createLabel("Created By"), gbc);
        gbc.gridx = 1;
        add(createdByField, gbc);

        // Add Log Level dropdown
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.EAST;
        add(createLabel("Log Level"), gbc);
        gbc.gridx = 1;
        add(logLevelDropdown, gbc);

        // Create a panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);

        // Add button panel to the form
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);

        // Set fixed size for the frame and make it visible
        setSize(450, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    // Helper method to create a text field with a border
    private JTextField createTextField() {
        JTextField textField = new JTextField(20);
        textField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        return textField;
    }

    // Helper method to create a label with a red asterisk
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text + "<font color='red'> *</font>", JLabel.RIGHT);
        label.setText("<html>" + label.getText() + "</html>"); // Enable HTML rendering
        return label;
    }

    // Action handler for the buttons
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == registerButton) {
            // Retrieve input values
            String userId = userIdField.getText();
            String password = new String(passwordField.getPassword());
            String userName = userNameField.getText();
            String mobNo = mobNoField.getText();
            String status = (String) statusDropdown.getSelectedItem();
            String createdBy = createdByField.getText();
            String logLevel = (String) logLevelDropdown.getSelectedItem();

            // Validate form inputs
            if (validateInputs(userId, password, userName, mobNo, createdBy)) {
                String hashedPassword = hashPassword(password);
                if (hashedPassword == null) {
                    JOptionPane.showMessageDialog(this, "Error hashing the password.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Insert data into the database
                insertUserData(userId, hashedPassword, userName, mobNo, status, createdBy, logLevel);
            }
        } else if (e.getSource() == cancelButton) {
            // Close the form when Cancel is pressed
            dispose();
        }
    }

    // Method to validate all the input fields
    private boolean validateInputs(String userId, String password, String userName, String mobNo, String createdBy) {
        if (userId.isEmpty() || password.isEmpty() || userName.isEmpty() || mobNo.isEmpty() || createdBy.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate userId (at least 3 characters or numbers)
        if (userId.length() < 3) {
            JOptionPane.showMessageDialog(this, "User ID must be at least 3 characters or numbers.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate password strength (at least one letter, one number, one special character)
        if (!Pattern.matches("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+])[A-Za-z\\d!@#$%^&*()_+]{6,}$", password)) {
            JOptionPane.showMessageDialog(this, "Password must contain at least one letter, one number, and one special character.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate mobile number (exactly 10 digits)
        if (!Pattern.matches("\\d{10}", mobNo)) {
            JOptionPane.showMessageDialog(this, "Mobile number must be exactly 10 digits.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate userName (more than one character)
        if (userName.length() <= 1) {
            JOptionPane.showMessageDialog(this, "User Name must contain more than one character.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate createdBy (alphabetic characters and spaces only)
        if (!Pattern.matches("[a-zA-Z ]+", createdBy)) {
            JOptionPane.showMessageDialog(this, "Created By can only contain alphabetic characters and spaces.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    // Method to hash the password using SHA-256
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to insert the user data into the database
    private void insertUserData(String userId, String hashedPassword, String userName, String mobNo, String status, String createdBy, String logLevel) {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/userdb", "root", "Netwin@123");
             PreparedStatement pst = con.prepareStatement("INSERT INTO users (User_Id, Password, User_Name, MOB_NO, Status, Created_By, Log_Level) VALUES (?, ?, ?, ?, ?, ?, ?)")) {

            pst.setString(1, userId);
            pst.setString(2, hashedPassword);
            pst.setString(3, userName);
            pst.setString(4, mobNo);
            pst.setString(5, status);
            pst.setString(6, createdBy);
            pst.setString(7, logLevel);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "User registered successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving user data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Method to clear all input fields after successful registration
    private void clearFields() {
        userIdField.setText("");
        passwordField.setText("");
        userNameField.setText("");
        mobNoField.setText("");
        createdByField.setText("");
    }
}
