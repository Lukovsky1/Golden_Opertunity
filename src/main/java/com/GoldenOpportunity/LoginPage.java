package com.GoldenOpportunity;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * LoginPage represents the UI where a user (Guest, Clerk, or Admin)
 * can enter credentials and request authentication.
 */
public class LoginPage extends JFrame {

    // Input fields for user credentials
    private JTextField usernameField;
    private JPasswordField passwordField;

    // Label used to display feedback messages
    private JLabel messageLabel;

    /**
     * Constructor: initializes the login window
     */
    public LoginPage() {
        setTitle("Log In");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createHeader(), BorderLayout.NORTH);
        add(createFormPanel(), BorderLayout.CENTER);
    }

    /**
     * Creates the page header with the title
     */
    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(20, 20, 10, 20));

        JLabel titleLabel = new JLabel("Log In");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));

        header.add(titleLabel);
        return header;
    }

    /**
     * Builds the main login form
     */
    private JPanel createFormPanel() {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(new Color(245, 245, 245));
        outerPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(25, 25, 25, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 10, 12, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Initialize input fields
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);

        int row = 0;

        addFormRow(formPanel, gbc, row++, "Username / Email:", usernameField);
        addFormRow(formPanel, gbc, row++, "Password:", passwordField);

        // Feedback label for login result
        messageLabel = new JLabel(" ");
        messageLabel.setForeground(Color.RED);
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        formPanel.add(messageLabel, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton loginButton = new JButton("Log In");
        JButton forgotPasswordButton = new JButton("Forgot Password");
        JButton backButton = new JButton("Back");

        // Temporary UI actions for now
        loginButton.addActionListener(e -> handleLogin());
        forgotPasswordButton.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Go to Reset Password page")
        );
        backButton.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Go back to previous page")
        );

        buttonPanel.add(loginButton);
        buttonPanel.add(forgotPasswordButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        outerPanel.add(formPanel, BorderLayout.NORTH);
        return outerPanel;
    }

    /**
     * Helper method to add one label + one input field in a row
     */
    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent component) {
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        panel.add(component, gbc);
    }

    /**
     * Handles the login action.
     * For now, this is frontend-only and does basic validation.
     * Later, this should connect to the authentication logic.
     */
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter both username/email and password.");
            return;
        }

        // Temporary success simulation
        messageLabel.setForeground(new Color(0, 130, 0));
        messageLabel.setText("Login request submitted successfully.");

        JOptionPane.showMessageDialog(this,
                "Authentication successful. Open next page based on user role.",
                "Login Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Entry point to run the page independently
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginPage page = new LoginPage();
            page.setVisible(true);
        });
    }
}