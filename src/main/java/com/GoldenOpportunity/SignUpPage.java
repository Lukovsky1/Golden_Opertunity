package com.GoldenOpportunity;

import com.GoldenOpportunity.Login.AuthResult;
import com.GoldenOpportunity.dbLogin.EmailValidator;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SignUpPage extends JPanel {

    private final AuthenticationController authController = new AuthenticationController();

    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JLabel messageLabel;

    public SignUpPage() throws IOException {
        setLayout(new BorderLayout());
        add(createFormPanel(), BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel outerPanel = new JPanel(new GridBagLayout());
        outerPanel.setBackground(new Color(245, 245, 245));
        outerPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(25, 25, 25, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        formPanel.add(titleLabel, gbc);

        usernameField = createResponsiveTextField();
        emailField = createResponsiveTextField();
        passwordField = createResponsivePasswordField();
        confirmPasswordField = createResponsivePasswordField();

        int row = 1;
        addFormRow(formPanel, gbc, row++, "Username:", usernameField);
        addFormRow(formPanel, gbc, row++, "Email:", emailField);
        addFormRow(formPanel, gbc, row++, "Password:", passwordField);
        addFormRow(formPanel, gbc, row++, "Confirm Password:", confirmPasswordField);

        messageLabel = new JLabel(" ");
        messageLabel.setForeground(Color.RED);
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        formPanel.add(messageLabel, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton createAccountButton = new JButton("Create Account");

        createAccountButton.addActionListener(e -> handleSignUp());

        buttonPanel.add(createAccountButton);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        formPanel.add(buttonPanel, gbc);

        GridBagConstraints outerGbc = new GridBagConstraints();
        outerGbc.gridx = 0;
        outerGbc.gridy = 0;
        outerGbc.weightx = 1.0;
        outerGbc.fill = GridBagConstraints.HORIZONTAL;
        outerGbc.anchor = GridBagConstraints.NORTH;
        outerPanel.add(formPanel, outerGbc);
        return outerPanel;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent component) {
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(component, gbc);
    }

    private JTextField createResponsiveTextField() {
        JTextField field = new JTextField(24);
        field.setPreferredSize(new Dimension(260, 36));
        field.setMinimumSize(new Dimension(180, 36));
        return field;
    }

    private JPasswordField createResponsivePasswordField() {
        JPasswordField field = new JPasswordField(24);
        field.setPreferredSize(new Dimension(260, 36));
        field.setMinimumSize(new Dimension(180, 36));
        return field;
    }

    private void handleSignUp() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirmPassword = new String(confirmPasswordField.getPassword()).trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Please fill in all fields.");
            return;
        }

        if (!EmailValidator.isValidEmail(email)) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText(EmailValidator.supportedDomainsMessage());
            return;
        }

        if (!password.equals(confirmPassword)) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Passwords do not match.");
            return;
        }

        AuthResult result = authController.signUp(username, email, password);
        messageLabel.setForeground(result.isSuccess() ? new Color(0, 130, 0) : Color.RED);
        messageLabel.setText(result.getMessage());

        if (result.isSuccess()) {
            usernameField.setText("");
            emailField.setText("");
            passwordField.setText("");
            confirmPasswordField.setText("");
            JOptionPane.showMessageDialog(this, result.getMessage(), "Account Created", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
