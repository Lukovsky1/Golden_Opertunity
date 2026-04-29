package com.GoldenOpportunity;

import com.GoldenOpportunity.Login.AuthResult;
import com.GoldenOpportunity.Login.enums.Role;
import com.GoldenOpportunity.Roles.RolePermissions;
import com.GoldenOpportunity.dbLogin.EmailValidator;
import com.GoldenOpportunity.dbLogin.DbUser;
import com.GoldenOpportunity.dbLogin.UserDao;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminPage extends JPanel {

    private static final int DETAILS_PANEL_WIDTH = 300;
    private static final int ANIMATION_STEP = 30;
    private static final int ANIMATION_DELAY_MS = 12;
    private static final Color PAGE_BACKGROUND = new Color(240, 243, 247);
    private static final Color PANEL_BORDER = new Color(190, 205, 218);
    private static final Color PANEL_BACKGROUND = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(55, 70, 85);
    private static final Color PRIMARY_BLUE = new Color(52, 138, 230);

    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final UIState uiState;
    private final AuthenticationController authController = new AuthenticationController();
    private final UserDao userDao = new UserDao();
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Name", "Role", "Email"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final List<DbUser> displayedUsers = new ArrayList<>();
    private final List<DbUser> allUsers = new ArrayList<>();

    private JTable userTable;
    private JPanel detailsPanel;
    private JLabel selectedNameLabel;
    private JLabel selectedRoleLabel;
    private JLabel selectedEmailLabel;
    private Timer drawerTimer;
    private int currentDrawerWidth;
    private DbUser selectedUser;
    private String selectedRoleFilter = "ALL";

    public AdminPage(CardLayout cardLayout, JPanel mainPanel, UIState uiState) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.uiState = uiState;

        setLayout(new BorderLayout());
        setBackground(PAGE_BACKGROUND);
        setBorder(new EmptyBorder(14, 16, 16, 16));

        add(createHeader(), BorderLayout.NORTH);
        add(createBody(), BorderLayout.CENTER);
        refreshUsers();
    }

    @Override
    public void setVisible(boolean aFlag) {
        if (aFlag) {
            if (!RolePermissions.requireRole(this, uiState, "Viewing the admin user directory", "HOME", cardLayout, mainPanel, Role.ADMIN)) {
                super.setVisible(false);
                return;
            }
            refreshUsers();
            clearSelectionAndHidePanel();
        }
        super.setVisible(aFlag);
    }

    private JPanel createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout(0, 12));
        headerPanel.setBackground(PANEL_BACKGROUND);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(PANEL_BORDER, 1),
                new EmptyBorder(14, 18, 14, 18)
        ));

        JLabel titleLabel = new JLabel("Admin User Directory", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);

        JButton createUserButton = new JButton("Create Admin / Clerk");
        styleActionButton(createUserButton, PRIMARY_BLUE, Color.WHITE);
        createUserButton.addActionListener(e -> openCreateUserDialog());

        JButton filterRoleButton = new JButton("Filter Role");
        styleActionButton(filterRoleButton, PANEL_BACKGROUND, TEXT_COLOR);
        filterRoleButton.addActionListener(e -> openRoleFilterDialog());

        JButton signOutButton = new JButton("Sign Out");
        styleActionButton(signOutButton, PANEL_BACKGROUND, TEXT_COLOR);
        signOutButton.addActionListener(e -> handleSignOut());

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsPanel.setBackground(PANEL_BACKGROUND);
        actionsPanel.add(createUserButton);
        actionsPanel.add(filterRoleButton);
        actionsPanel.add(signOutButton);

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(actionsPanel, BorderLayout.CENTER);
        return headerPanel;
    }

    private JPanel createBody() {
        JPanel body = new JPanel(new BorderLayout(18, 0));
        body.setBackground(PAGE_BACKGROUND);
        body.setBorder(new EmptyBorder(18, 0, 0, 0));

        userTable = new JTable(tableModel);
        userTable.setFillsViewportHeight(true);
        userTable.setRowHeight(36);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        userTable.setForeground(TEXT_COLOR);
        userTable.setGridColor(new Color(210, 218, 226));
        userTable.setSelectionBackground(new Color(220, 230, 240));
        userTable.setSelectionForeground(TEXT_COLOR);
        userTable.getTableHeader().setReorderingAllowed(false);
        userTable.getTableHeader().setBackground(PANEL_BACKGROUND);
        userTable.getTableHeader().setForeground(TEXT_COLOR);
        userTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 15));
        userTable.getTableHeader().setBorder(new LineBorder(new Color(210, 218, 226), 1));
        userTable.getTableHeader().setPreferredSize(new Dimension(0, 38));
        userTable.getSelectionModel().addListSelectionListener(this::handleRowSelection);

        installEscapeShortcut();

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(new LineBorder(new Color(210, 218, 226), 1));
        scrollPane.getViewport().setBackground(PANEL_BACKGROUND);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(PANEL_BACKGROUND);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(PANEL_BORDER, 1),
                new EmptyBorder(14, 16, 16, 16)
        ));

        JLabel tableTitle = new JLabel("Users", SwingConstants.CENTER);
        tableTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        tableTitle.setForeground(TEXT_COLOR);
        tableTitle.setBorder(new EmptyBorder(0, 0, 12, 0));

        tablePanel.add(tableTitle, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        body.add(tablePanel, BorderLayout.CENTER);

        detailsPanel = createDetailsPanel();
        body.add(detailsPanel, BorderLayout.EAST);

        return body;
    }

    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(PANEL_BORDER, 1),
                new EmptyBorder(20, 20, 20, 20)
        ));
        panel.setBackground(PANEL_BACKGROUND);
        panel.setPreferredSize(new Dimension(0, 0));

        JLabel titleLabel = new JLabel("Selected User");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        selectedNameLabel = createInfoLabel("Name: ");
        selectedRoleLabel = createInfoLabel("Role: ");
        selectedEmailLabel = createInfoLabel("Email: ");

        JButton resetUsernameButton = new JButton("Reset Username");
        styleActionButton(resetUsernameButton, PANEL_BACKGROUND, TEXT_COLOR);
        resetUsernameButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        resetUsernameButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        resetUsernameButton.addActionListener(e -> handleUsernameReset());

        JButton resetPasswordButton = new JButton("Reset Password");
        styleActionButton(resetPasswordButton, PANEL_BACKGROUND, TEXT_COLOR);
        resetPasswordButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        resetPasswordButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        resetPasswordButton.addActionListener(e -> handlePasswordReset());

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(18));
        panel.add(selectedNameLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(selectedRoleLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(selectedEmailLabel);
        panel.add(Box.createVerticalStrut(24));
        panel.add(resetUsernameButton);
        panel.add(Box.createVerticalStrut(12));
        panel.add(resetPasswordButton);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        label.setForeground(TEXT_COLOR);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void styleActionButton(JButton button, Color background, Color foreground) {
        button.setFocusPainted(false);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBorder(new LineBorder(new Color(150, 150, 150), 1, true));
        button.setPreferredSize(new Dimension(160, 40));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
    }

    private void installEscapeShortcut() {
        InputMap inputMap = userTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap actionMap = userTable.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "clearAdminSelection");
        actionMap.put("clearAdminSelection", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                clearSelectionAndHidePanel();
            }
        });
    }

    private void handleRowSelection(ListSelectionEvent event) {
        if (event.getValueIsAdjusting()) {
            return;
        }

        int selectedRow = userTable.getSelectedRow();
        if (selectedRow < 0 || selectedRow >= displayedUsers.size()) {
            selectedUser = null;
            hideDetailsPanel();
            return;
        }

        selectedUser = displayedUsers.get(selectedRow);
        updateDetailsPanel();
        showDetailsPanel();
    }

    private void updateDetailsPanel() {
        if (selectedUser == null) {
            selectedNameLabel.setText("Name: ");
            selectedRoleLabel.setText("Role: ");
            selectedEmailLabel.setText("Email: ");
            return;
        }

        selectedNameLabel.setText("Name: " + selectedUser.username);
        selectedRoleLabel.setText("Role: " + selectedUser.role);
        selectedEmailLabel.setText("Email: " + selectedUser.contactInfo);
    }

    private void refreshUsers() {
        tableModel.setRowCount(0);
        allUsers.clear();
        displayedUsers.clear();

        try {
            userDao.initializeSchema();
            List<DbUser> users = userDao.findAllUsers();
            allUsers.addAll(users);
            applyRoleFilter();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load users for the admin page.",
                    "Admin Page Error",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load users for the admin page. File error",
                    "Admin Page Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void handleUsernameReset() {
        if (!RolePermissions.requireRole(this, uiState, "Resetting usernames", null, null, null, Role.ADMIN)) {
            return;
        }
        if (selectedUser == null) {
            return;
        }

        String newUsername = JOptionPane.showInputDialog(
                this,
                "Enter a new username:",
                selectedUser.username
        );

        if (newUsername == null) {
            return;
        }

        String normalizedUsername = newUsername.trim();
        if (normalizedUsername.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username cannot be empty.");
            return;
        }

        try {
            userDao.updateUsername(selectedUser.id, normalizedUsername);
            JOptionPane.showMessageDialog(this, "Username updated.");
            refreshUsers();
            reselectUserById(selectedUser.id);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Unable to update username.", "Admin Page Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handlePasswordReset() {
        if (!RolePermissions.requireRole(this, uiState, "Resetting passwords", null, null, null, Role.ADMIN)) {
            return;
        }
        if (selectedUser == null) {
            return;
        }

        JPasswordField passwordField = new JPasswordField();
        int result = JOptionPane.showConfirmDialog(
                this,
                passwordField,
                "Enter a new password",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String newPassword = new String(passwordField.getPassword()).trim();
        if (newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password cannot be empty.");
            return;
        }

        try {
            userDao.updatePassword(selectedUser.id, newPassword);
            JOptionPane.showMessageDialog(this, "Password updated.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Unable to update password.", "Admin Page Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openCreateUserDialog() {
        if (!RolePermissions.requireRole(this, uiState, "Creating admin or clerk users", null, null, null, Role.ADMIN)) {
            return;
        }
        JTextField usernameField = new JTextField(18);
        JTextField emailField = new JTextField(18);
        JPasswordField passwordField = new JPasswordField(18);
        JPasswordField confirmPasswordField = new JPasswordField(18);
        JComboBox<String> roleDropdown = new JComboBox<>(new String[]{"ADMIN", "CLERK"});

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addFormRow(formPanel, gbc, 0, "Role:", roleDropdown);
        addFormRow(formPanel, gbc, 1, "Username:", usernameField);
        addFormRow(formPanel, gbc, 2, "Email:", emailField);
        addFormRow(formPanel, gbc, 3, "Password:", passwordField);
        addFormRow(formPanel, gbc, 4, "Confirm Password:", confirmPasswordField);

        int result = JOptionPane.showConfirmDialog(
                this,
                formPanel,
                "Create Admin or Clerk User",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirmPassword = new String(confirmPasswordField.getPassword()).trim();
        String role = (String) roleDropdown.getSelectedItem();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        if (!EmailValidator.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, EmailValidator.supportedDomainsMessage());
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.");
            return;
        }

        AuthResult resultMessage = authController.createPrivilegedUser(username, email, password, role);
        JOptionPane.showMessageDialog(
                this,
                resultMessage.getMessage(),
                resultMessage.isSuccess() ? "User Created" : "Create User Error",
                resultMessage.isSuccess() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE
        );

        if (resultMessage.isSuccess()) {
            refreshUsers();
            selectUserByUsername(username);
        }
    }

    private void openRoleFilterDialog() {
        if (!RolePermissions.requireRole(this, uiState, "Filtering the admin user directory", null, null, null, Role.ADMIN)) {
            return;
        }
        JComboBox<String> roleDropdown = new JComboBox<>(new String[]{"ALL", "ADMIN", "CLERK", "GUEST"});
        roleDropdown.setSelectedItem(selectedRoleFilter);

        int result = JOptionPane.showConfirmDialog(
                this,
                roleDropdown,
                "Show Only This Role",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        selectedRoleFilter = (String) roleDropdown.getSelectedItem();
        applyRoleFilter();
        clearSelectionAndHidePanel();
    }

    private void applyRoleFilter() {
        tableModel.setRowCount(0);
        displayedUsers.clear();

        for (DbUser user : allUsers) {
            if (!"ALL".equalsIgnoreCase(selectedRoleFilter)
                    && !selectedRoleFilter.equalsIgnoreCase(user.role)) {
                continue;
            }

            displayedUsers.add(user);
            tableModel.addRow(new Object[]{
                    user.username,
                    user.role,
                    user.contactInfo
            });
        }
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent component) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(component, gbc);
    }

    private void selectUserByUsername(String username) {
        for (int i = 0; i < displayedUsers.size(); i++) {
            if (displayedUsers.get(i).username.equals(username)) {
                userTable.setRowSelectionInterval(i, i);
                return;
            }
        }
    }

    private void reselectUserById(int userId) {
        for (int i = 0; i < displayedUsers.size(); i++) {
            if (displayedUsers.get(i).id == userId) {
                userTable.setRowSelectionInterval(i, i);
                return;
            }
        }
        clearSelectionAndHidePanel();
    }

    private void clearSelectionAndHidePanel() {
        selectedUser = null;
        updateDetailsPanel();
        if (userTable != null) {
            userTable.clearSelection();
        }
        hideDetailsPanel();
    }

    private void showDetailsPanel() {
        animateDrawerTo(DETAILS_PANEL_WIDTH);
    }

    private void hideDetailsPanel() {
        animateDrawerTo(0);
    }

    private void animateDrawerTo(int targetWidth) {
        if (drawerTimer != null && drawerTimer.isRunning()) {
            drawerTimer.stop();
        }

        drawerTimer = new Timer(ANIMATION_DELAY_MS, e -> {
            if (currentDrawerWidth < targetWidth) {
                currentDrawerWidth = Math.min(currentDrawerWidth + ANIMATION_STEP, targetWidth);
            } else if (currentDrawerWidth > targetWidth) {
                currentDrawerWidth = Math.max(currentDrawerWidth - ANIMATION_STEP, targetWidth);
            }

            detailsPanel.setPreferredSize(new Dimension(currentDrawerWidth, 0));
            detailsPanel.revalidate();
            detailsPanel.repaint();
            revalidate();
            repaint();

            if (currentDrawerWidth == targetWidth) {
                drawerTimer.stop();
            }
        });
        drawerTimer.start();
    }

    private void handleSignOut() {
        clearSelectionAndHidePanel();
        uiState.setLoggedIn(false);
        cardLayout.show(mainPanel, "HOME");
    }
}
