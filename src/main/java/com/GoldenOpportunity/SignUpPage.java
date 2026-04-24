package com.GoldenOpportunity;

import com.GoldenOpportunity.Login.AuthResult;

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

    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final AuthenticationController authController = new AuthenticationController();

    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JLabel messageLabel;

    public SignUpPage(CardLayout cardLayout, JPanel mainPanel) throws IOException {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;

        setLayout(new BorderLayout());
        add(createHeader(), BorderLayout.NORTH);
        add(createFormPanel(), BorderLayout.CENTER);
    }

    private JPanel createHeader() throws IOException {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(15, 20, 15, 20));
        header.setBackground(Color.WHITE);

        Image logo = ImageIO.read(new File("src/main/java/com/GoldenOpportunity/Images/logo.png"));
        int originalWidth = logo.getWidth(null);
        int originalHeight = logo.getHeight(null);
        int newHeight = 70;
        int newWidth = (originalWidth * newHeight) / originalHeight;

        JLabel logoLabel = new JLabel(new ImageIcon(logo.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH)));

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        nav.setBackground(Color.WHITE);
        String[] items = {"Home", "Rooms", "Shop", "Login", "Sign Up"};
        Map<String, JButton> buttonMap = new HashMap<>();

        for (String item : items) {
            JButton button = new JButton(item);
            button.setFocusPainted(false);
            button.setBackground(Color.WHITE);
            button.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            button.setPreferredSize(new Dimension(90, 35));
            buttonMap.put(item, button);
            nav.add(button);
        }

        buttonMap.get("Home").addActionListener(e -> cardLayout.show(mainPanel, "HOME"));
        buttonMap.get("Rooms").addActionListener(e -> cardLayout.show(mainPanel, "ROOMS"));
        buttonMap.get("Shop").addActionListener(e -> cardLayout.show(mainPanel, "SHOP"));
        buttonMap.get("Login").addActionListener(e -> cardLayout.show(mainPanel, "LOGIN"));
        buttonMap.get("Sign Up").addActionListener(e -> cardLayout.show(mainPanel, "SIGNUP"));

        header.add(logoLabel, BorderLayout.WEST);
        header.add(nav, BorderLayout.EAST);
        return header;
    }

    private JPanel createFormPanel() {
        JPanel outerPanel = new JPanel(new GridBagLayout());
        outerPanel.setBackground(new Color(245, 245, 245));
        outerPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setPreferredSize(new Dimension(420, 360));
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
        formPanel.add(titleLabel, gbc);

        usernameField = new JTextField(20);
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);

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
        formPanel.add(messageLabel, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton createAccountButton = new JButton("Create Account");
        JButton loginButton = new JButton("Back to Login");

        createAccountButton.addActionListener(e -> handleSignUp());
        loginButton.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN"));

        buttonPanel.add(createAccountButton);
        buttonPanel.add(loginButton);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        outerPanel.add(formPanel);
        return outerPanel;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent component) {
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        panel.add(component, gbc);
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

        if (!email.contains("@") || !email.contains(".")) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Please enter a valid email address.");
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
            cardLayout.show(mainPanel, "LOGIN");
        }
    }
}
