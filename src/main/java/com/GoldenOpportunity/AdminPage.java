package com.GoldenOpportunity;

import com.GoldenOpportunity.dbLogin.DbUser;
import com.GoldenOpportunity.dbLogin.UserDao;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminPage extends JPanel {

    private static final int DETAILS_PANEL_WIDTH = 300;
    private static final int ANIMATION_STEP = 30;
    private static final int ANIMATION_DELAY_MS = 12;

    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final UIState uiState;
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

    private JTable userTable;
    private JPanel detailsPanel;
    private JLabel selectedNameLabel;
    private JLabel selectedRoleLabel;
    private JLabel selectedEmailLabel;
    private Timer drawerTimer;
    private int currentDrawerWidth;
    private DbUser selectedUser;

    public AdminPage(CardLayout cardLayout, JPanel mainPanel, UIState uiState) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.uiState = uiState;

        setLayout(new GridBagLayout());
        setBackground(new Color(245, 245, 245));

        JPanel card = new JPanel(new BorderLayout(0, 18));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(24, 24, 24, 24));
        card.setPreferredSize(new Dimension(980, 520));

        card.add(createHeader(), BorderLayout.NORTH);
        card.add(createBody(), BorderLayout.CENTER);

        add(card);
        refreshUsers();
    }

    @Override
    public void setVisible(boolean aFlag) {
        if (aFlag) {
            refreshUsers();
            clearSelectionAndHidePanel();
        }
        super.setVisible(aFlag);
    }

    private JPanel createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Admin User Directory", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));

        JButton signOutButton = new JButton("Sign Out");
        signOutButton.addActionListener(e -> handleSignOut());

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(signOutButton, BorderLayout.EAST);
        return headerPanel;
    }

    private JPanel createBody() {
        JPanel body = new JPanel(new BorderLayout(18, 0));
        body.setOpaque(false);

        userTable = new JTable(tableModel);
        userTable.setFillsViewportHeight(true);
        userTable.setRowHeight(30);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.getTableHeader().setReorderingAllowed(false);
        userTable.getSelectionModel().addListSelectionListener(this::handleRowSelection);

        installEscapeShortcut();

        JScrollPane scrollPane = new JScrollPane(userTable);
        body.add(scrollPane, BorderLayout.CENTER);

        detailsPanel = createDetailsPanel();
        body.add(detailsPanel, BorderLayout.EAST);

        return body;
    }

    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(248, 249, 251));
        panel.setPreferredSize(new Dimension(0, 0));

        JLabel titleLabel = new JLabel("Selected User");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        selectedNameLabel = createInfoLabel("Name: ");
        selectedRoleLabel = createInfoLabel("Role: ");
        selectedEmailLabel = createInfoLabel("Email: ");

        JButton resetUsernameButton = new JButton("Reset Username");
        resetUsernameButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        resetUsernameButton.addActionListener(e -> handleUsernameReset());

        JButton resetPasswordButton = new JButton("Reset Password");
        resetPasswordButton.setAlignmentX(Component.LEFT_ALIGNMENT);
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
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
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
        displayedUsers.clear();

        try {
            userDao.initializeSchema();
            List<DbUser> users = userDao.findAllUsers();
            displayedUsers.addAll(users);

            for (DbUser user : users) {
                tableModel.addRow(new Object[]{
                        user.username,
                        user.role,
                        user.contactInfo
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load users for the admin page.",
                    "Admin Page Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void handleUsernameReset() {
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
