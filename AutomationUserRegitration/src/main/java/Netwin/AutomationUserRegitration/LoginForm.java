package Netwin.AutomationUserRegitration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.security.MessageDigest;
import java.sql.*;

@SuppressWarnings("serial")
public class LoginForm extends JFrame implements ActionListener {

    // Declare fields for user input
    private JTextField userIdField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;  // Cancel button for exiting without login

    public static void main(String[] args) {
        // Launch the login form on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(LoginForm::new);
    }

    // Constructor to create the login form
    public LoginForm() {
        // Set the title of the frame
        setTitle("Login Form");

        // Set the layout manager to GridBagLayout for flexible form layout
        setLayout(new GridBagLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);  // Ensure application exits on close

        // GridBagConstraints to configure layout positions
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);  // Set padding
        gbc.fill = GridBagConstraints.HORIZONTAL;  // Make components stretch horizontally

        // Create and configure title label
        JLabel titleLabel = new JLabel("Admin Login", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.BLACK);

        // Create the text fields for user input
        userIdField = createTextField();
        passwordField = new JPasswordField();

        // Create login and cancel buttons
        loginButton = new JButton("Login");
        cancelButton = new JButton("Cancel");

        // Add action listeners for buttons
        loginButton.addActionListener(this);
        cancelButton.addActionListener(this);

        // Add title label to the form
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;  // Span across two columns
        gbc.anchor = GridBagConstraints.CENTER;  // Center the label
        add(titleLabel, gbc);

        // Add User ID label and text field to the form
        gbc.gridwidth = 1;  // Reset to single column
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("User ID:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(userIdField, gbc);

        // Add Password label and password field to the form
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        add(passwordField, gbc);

        // Create a panel to hold the buttons, with spacing between them
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // Set a fixed size for the buttons
        Dimension buttonSize = new Dimension(80, 30);
        loginButton.setPreferredSize(buttonSize);
        cancelButton.setPreferredSize(buttonSize);

        // Add buttons to the panel
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);

        // Add the button panel to the form
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);

        // Set size and location of the frame
        setSize(400, 250);
        setLocationRelativeTo(null);  // Center the frame on the screen
        setResizable(false);  // Disable resizing
        setVisible(true);  // Show the form
    }

    // Helper method to create a text field with a border
    private JTextField createTextField() {
        JTextField textField = new JTextField(15);
        textField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        return textField;
    }

    // Handle button actions (login and cancel)
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            // Retrieve user input
            String userId = userIdField.getText();
            String password = new String(passwordField.getPassword());

            // Validate login credentials
            try {
                if (validateLogin(userId, password)) {
                    // Show success message
                    JOptionPane.showMessageDialog(this,
                            "<html><div style='padding: 10px;', margin:'Center'><font color='green'>Access Granted...<br>Press Ok For New User Registration</font></div></html>",
                            "Login Successful", JOptionPane.INFORMATION_MESSAGE);

                    // Close the login form
                    this.dispose();

                    // Open the registration form after successful login
                    new RegistrationForm();
                } else {
                    // Show error message if login fails
                    JOptionPane.showMessageDialog(this,
                            "<html> <div style='padding: 10px;', margin:'Center'><font color='red'>Access Denied...<br>Please enter the correct username and password.</font></html>",
                            "Login Failed", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (HeadlessException | ClassNotFoundException e1) {
                e1.printStackTrace();
            }
        } else if (e.getSource() == cancelButton) {
            // Close the form when cancel button is clicked
            this.dispose();
        }
    }

    // Method to validate the user login credentials
    private boolean validateLogin(String userId, String password) throws ClassNotFoundException {
        String query = "SELECT * FROM users WHERE User_Id = ? AND Password = ? AND Status = 'Active' AND Log_Level = '2'";

        // Load the MySQL driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Database connection URL
        String url = "jdbc:mysql://localhost:3306/userdb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        try (Connection con = DriverManager.getConnection(url, "root", "Netwin@123");
             PreparedStatement pst = con.prepareStatement(query)) {

            // Set parameters for SQL query
            pst.setString(1, userId);
            pst.setString(2, hashPassword(password));

            // Execute the query and check if a matching user is found
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();  // Return true if a match is found
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // Hash the password using SHA-256 algorithm
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));  // Convert each byte to hex format
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
