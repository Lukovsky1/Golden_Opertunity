package com.GoldenOpportunity;

import com.github.lgooddatepicker.components.DatePicker;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

public class ClerkModifyReservationPage extends JPanel {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private DatePicker startDate;
    private DatePicker endDate;

    public ClerkModifyReservationPage(CardLayout cardLayout, JPanel mainPanel) throws IOException {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;

        setLayout(new BorderLayout());
        setBackground(new Color(240, 243, 247));

        add(createHeader(), BorderLayout.NORTH);
        add(createBody(), BorderLayout.CENTER);
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

        Image scaledLogo = logo.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
        logoLabel.setFont(new Font("SansSerif", Font.BOLD, 20));

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        nav.setBackground(Color.WHITE);
        String profileIcon = "👤";

        JButton homeButton = new JButton("Home");
        homeButton.setFocusPainted(false);
        homeButton.setBackground(Color.WHITE);
        homeButton.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        homeButton.setPreferredSize(new Dimension(90, 35));
        nav.add(homeButton);

        JButton profileButton = new JButton(profileIcon);
        profileButton.setFocusPainted(false);
        profileButton.setBackground(Color.WHITE);
        profileButton.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        profileButton.setPreferredSize(new Dimension(90, 35));
        nav.add(profileButton);

        profileButton.addActionListener(e -> {
            cardLayout.show(mainPanel,"PROFILE");
        });
        homeButton.addActionListener(e -> {
            cardLayout.show(mainPanel,"CLERK_HOME");
        });

        header.add(logoLabel, BorderLayout.WEST);
        header.add(nav, BorderLayout.EAST);
        return header;
    }

    private JButton createTopButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, height));
        button.setFocusPainted(false);
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("SansSerif", Font.BOLD, 20));
        button.setBorder(new LineBorder(new Color(150, 150, 150), 2, true));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        return button;
    }

    private JPanel createBody() {
        JPanel body = new JPanel(new BorderLayout(20, 0));
        body.setBackground(new Color(240, 243, 247));
        body.setBorder(new EmptyBorder(18, 20, 20, 20));

        body.add(createSidebar(), BorderLayout.WEST);
        body.add(createReservationPanel(), BorderLayout.CENTER);

        return body;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Color.WHITE);
        sidebar.setBorder(new LineBorder(new Color(190, 205, 218), 2));
        sidebar.setPreferredSize(new Dimension(230, 0));

        sidebar.add(Box.createVerticalStrut(12));

        JButton addRoomsButton = createSideButton("Add Rooms");
        JButton modifyRoomsButton = createSideButton("Modify Rooms");

        addRoomsButton.addActionListener(e -> cardLayout.show(mainPanel, "ADD_ROOM"));
        modifyRoomsButton.addActionListener(e -> cardLayout.show(mainPanel, "MODIFY_ROOMS"));

        sidebar.add(addRoomsButton);
        sidebar.add(Box.createVerticalStrut(18));
        sidebar.add(modifyRoomsButton);
        sidebar.add(Box.createVerticalGlue());

        return sidebar;
    }

    private JButton createSideButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setPreferredSize(new Dimension(185, 52));
        button.setMaximumSize(new Dimension(185, 52));
        button.setFocusPainted(false);
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("SansSerif", Font.BOLD, 20));
        button.setBorder(new LineBorder(new Color(150, 150, 150), 2, true));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        return button;
    }

    private JPanel createReservationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(190, 205, 218), 2),
                new EmptyBorder(18, 24, 24, 24)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 12, 10, 12);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ===== TITLE =====
        JLabel title = new JLabel("Reservation:");
        title.setFont(new Font("SansSerif", Font.PLAIN, 32));
        title.setForeground(new Color(43, 58, 72));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.weightx = 1;
        panel.add(title, gbc);

        // ===== FIELDS =====
        nameField = createTextField();
        emailField = createTextField();
        phoneField = createTextField();

        // Name
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(createLabel("Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1;
        panel.add(nameField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(createLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1;
        panel.add(emailField, gbc);

        // Phone
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(createLabel("Phone Number:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1;
        panel.add(phoneField, gbc);

        // ===== DATES =====
        startDate = new DatePicker();
        endDate = new DatePicker();

        gbc.gridy = 4;
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.weightx = 0;
        panel.add(createLabel("Start Date:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.4;
        panel.add(startDate, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        panel.add(createLabel("End Date:"), gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.4;
        panel.add(endDate, gbc);

        // ===== CHANGE ROOMS =====
        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(createLabel("Change Rooms:"), gbc);

        JPanel roomsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        roomsPanel.setOpaque(false);

        roomsPanel.add(createRoomButton("105"));
        roomsPanel.add(Box.createHorizontalStrut(22));
        roomsPanel.add(createRoomButton("106"));

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1;
        panel.add(roomsPanel, gbc);

        // ===== ACTION BUTTONS =====
        JButton saveButton = createBlackButton("Save Reservation", 260, 65);
        JButton cancelButton = createRedButton("Cancel Reservation", 280, 65);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        actionPanel.setOpaque(false);

        actionPanel.add(saveButton);
        actionPanel.add(Box.createHorizontalStrut(28));
        actionPanel.add(cancelButton);

        gbc.gridy = 6;
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1;
        gbc.insets = new Insets(30, 12, 0, 12);
        panel.add(actionPanel, gbc);

        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 20));
        label.setForeground(new Color(43, 58, 72));
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(400, 52));
        field.setFont(new Font("SansSerif", Font.PLAIN, 18));
        field.setBorder(new LineBorder(new Color(190, 205, 218), 2, true));
        return field;
    }

    private JComboBox<String> createDateBox() {
        JComboBox<String> box = new JComboBox<>(new String[]{
                "", "01/01/2026", "01/02/2026", "01/03/2026"
        });
        box.setPreferredSize(new Dimension(175, 52));
        box.setFont(new Font("SansSerif", Font.PLAIN, 18));
        return box;
    }

    private JButton createRoomButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(85, 55));
        button.setFocusPainted(false);
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 22));
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        return button;
    }

    private JButton createBlackButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, height));
        button.setFocusPainted(false);
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 20));
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        return button;
    }

    private JButton createRedButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, height));
        button.setFocusPainted(false);
        button.setBackground(new Color(214, 65, 88));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 20));
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Modify Rooms");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1220, 800);
            frame.setLocationRelativeTo(null);

            CardLayout cardLayout = new CardLayout();
            JPanel mainPanel = new JPanel(cardLayout);

            ClerkModifyReservationPage clerkReservation = null;
            try {
                clerkReservation = new ClerkModifyReservationPage(cardLayout, mainPanel);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            mainPanel.add(clerkReservation, "MODIFY_ROOMS");

            frame.setContentPane(mainPanel);
            cardLayout.show(mainPanel, "MODIFY_ROOMS");

            frame.setVisible(true);
        });
    }
}