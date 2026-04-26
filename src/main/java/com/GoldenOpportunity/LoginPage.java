package com.GoldenOpportunity;

import com.GoldenOpportunity.Login.LoginResult;
import com.GoldenOpportunity.Login.enums.Role;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * LoginPage represents the UI where a user (Guest, Clerk, or Admin)
 * can enter credentials and request authentication.
 */
public class LoginPage extends JPanel {

    // Input fields for user credentials
    private JTextField usernameField;
    private JPasswordField passwordField;

    // Label used to display feedback messages
    private JLabel messageLabel;

    private final AuthenticationController authController = new AuthenticationController();

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private UIState uiState;

    /**
     * Constructor: initializes the login window
     */
    public LoginPage(CardLayout cardLayout, JPanel mainPanel, UIState uiState) throws IOException {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.uiState = uiState;

        setSize(500, 450);
        setLayout(new BorderLayout());

        add(createHeader(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);
    }

    private JPanel createMainContent() throws IOException {
        JPanel content = new JPanel(new GridLayout(1,2));
        content.setBackground(new Color(243, 246, 249));
        content.setBorder(new EmptyBorder(12, 15, 18, 15));

        content.add(createFormPanel());
        content.add(new SignUpPage());

        return content;
    }

    /**
     * Creates the page header with the title
     */
    private JPanel createHeader() throws IOException {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(15, 20, 15, 20));
        header.setBackground(Color.WHITE);

        Image logo = ImageIO.read(new File("src/main/java/com/GoldenOpportunity/Images/logo.png"));

        int originalWidth = logo.getWidth(null);
        int originalHeight = logo.getHeight(null);

        int newHeight = 70;
        int newWidth = (originalWidth * newHeight) / originalHeight;

        Image scaledLogo = logo.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
        logoLabel.setFont(new Font("SansSerif", Font.BOLD, 20));

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        nav.setBackground(Color.WHITE);
        String[] items = {"Home", "Rooms", "Shop", "Login", "🛒","👤"};
        Map<String,JButton> buttonMap = new HashMap<>();

        for (String item : items) {
            buttonMap.put(item,new JButton(item));
            buttonMap.get(item).setFocusPainted(false);
            buttonMap.get(item).setBackground(Color.WHITE);
            buttonMap.get(item).setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            buttonMap.get(item).setPreferredSize(new Dimension(90, 35));
            nav.add(buttonMap.get(item));
        }

        uiState.registerLoginButton(buttonMap.get("Login"));

        buttonMap.get("Home").addActionListener(e -> {
            cardLayout.show(mainPanel,"HOME");
        });
        buttonMap.get("Rooms").addActionListener(e -> {
            cardLayout.show(mainPanel,"ROOMS");
        });
        buttonMap.get("Login").addActionListener(e -> {
            cardLayout.show(mainPanel,"LOGIN");
        });
        buttonMap.get("Shop").addActionListener(e -> {
            cardLayout.show(mainPanel,"SHOP");
        });
        buttonMap.get("🛒").addActionListener(e -> {
            if(uiState.potentialRooms.isEmpty()){
                JOptionPane.showMessageDialog(this, "Please add a room first.");
            }
            else{
                try {
                    mainPanel.add(new CheckoutPage(cardLayout, mainPanel,uiState), "CHECKOUT");

                    mainPanel.revalidate();
                    mainPanel.repaint();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error processing booking");
                }
                cardLayout.show(mainPanel,"CHECKOUT");
            }
        });
        buttonMap.get("👤").addActionListener(e -> {
            if(!uiState.isLoggedIn){
                cardLayout.show(mainPanel,"LOGIN");
            }
            else{
                cardLayout.show(mainPanel,"PROFILE");
                mainPanel.revalidate();
                mainPanel.repaint();
            }
        });

        header.add(logoLabel, BorderLayout.WEST);
        header.add(nav, BorderLayout.EAST);
        return header;
    }

    /**
     * Builds the main login form
     */
    private JPanel createFormPanel() {
        JPanel outerPanel = new JPanel(new GridBagLayout());
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

        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(titleLabel, gbc);

        // Initialize input fields
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);

        int row = 1;

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

        outerPanel.add(formPanel);
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
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Please enter both username/email and password.");
            return;
        }

        LoginResult result = authController.logIn(username, password);
        if (!result.isSuccess()) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText(result.getMessage());
            passwordField.setText("");
            messageLabel.setText(" ");
            return;
        }

        messageLabel.setForeground(new Color(0, 130, 0));
        messageLabel.setText(result.getMessage());
        usernameField.setText("");
        passwordField.setText("");
        messageLabel.setText(" ");

        uiState.setCurrentSession(result.getSession());
        Role role = result.getSession().getRole();
        if (role == Role.GUEST) {
            cardLayout.show(mainPanel, "HOME");
            return;
        }

        if (role == Role.ADMIN) {
            cardLayout.show(mainPanel, "ADMIN");
            return;
        }

        if (role == Role.CLERK) {
            cardLayout.show(mainPanel, "CLERK_HOME");
            return;
        }

        JOptionPane.showMessageDialog(this,
                "Authentication successful for role: " + role,
                "Login Success",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
