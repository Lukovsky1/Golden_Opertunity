package com.GoldenOpportunity;

import com.GoldenOpportunity.Roles.RolePermissions;
import com.github.lgooddatepicker.components.DatePicker;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class ModifyReservationPage extends JPanel {

    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final UIState uiState;

    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private DatePicker startDate;
    private DatePicker endDate;
    private JTextField roomsField;

    public ModifyReservationPage(CardLayout cardLayout, JPanel mainPanel, UIState uiState) throws IOException {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.uiState = uiState;

        setLayout(new BorderLayout());
        setBackground(new Color(240, 242, 245));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        add(createHeader(), BorderLayout.NORTH);
        add(createBody(), BorderLayout.CENTER);
    }

    @Override
    public void setVisible(boolean aFlag) {
        if (aFlag && !RolePermissions.requireRole(this, uiState, "Modifying reservations", "HOME", cardLayout, mainPanel, com.GoldenOpportunity.Login.enums.Role.CLERK)) {
            super.setVisible(false);
            return;
        }
        super.setVisible(aFlag);
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

    private JPanel createBody() {
        JPanel bodyWrapper = new JPanel(new BorderLayout(20, 0));
        bodyWrapper.setBackground(new Color(245, 245, 245));
        bodyWrapper.setBorder(new EmptyBorder(15, 0, 0, 0));

        bodyWrapper.add(createSidebar(), BorderLayout.WEST);
        bodyWrapper.add(createFormPanel(), BorderLayout.CENTER);

        return bodyWrapper;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(230, 500));
        sidebar.setBackground(new Color(245, 245, 245));
        sidebar.setBorder(new CompoundBorder(
                new LineBorder(new Color(190, 200, 210), 2),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JButton addRoomsButton = createSidebarButton("Add Rooms");
        JButton modifyRoomsButton = createSidebarButton("Modify Rooms");

        addRoomsButton.addActionListener(e -> {
            if (cardLayout != null && mainPanel != null) {
                cardLayout.show(mainPanel, "ADD_ROOMS");
            }
        });

        modifyRoomsButton.addActionListener(e -> {
            if (cardLayout != null && mainPanel != null) {
                cardLayout.show(mainPanel, "MODIFY_ROOMS");
            }
        });

        sidebar.add(addRoomsButton);
        sidebar.add(Box.createVerticalStrut(18));
        sidebar.add(modifyRoomsButton);

        return sidebar;
    }

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 50));
        button.setPreferredSize(new Dimension(180, 50));
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.PLAIN, 18));
        button.setBackground(new Color(250, 250, 250));
        button.setBorder(new LineBorder(new Color(160, 160, 160), 2));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        return button;
    }

    private JPanel createFormPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(new Color(245, 245, 245));
        outer.setBorder(new CompoundBorder(
                new LineBorder(new Color(190, 200, 210), 2),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel title = new JLabel("Reservation:");
        title.setFont(new Font("SansSerif", Font.PLAIN, 34));
        title.setForeground(new Color(45, 60, 75));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setOpaque(false);
        titlePanel.add(title);

        outer.add(titlePanel, BorderLayout.NORTH);
        outer.add(createFormFields(), BorderLayout.CENTER);

        return outer;
    }

    private JPanel createFormFields() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(14, 12, 14, 12);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("SansSerif", Font.PLAIN, 18);

        nameField = createTextField(24);
        emailField = createTextField(24);
        phoneField = createTextField(24);
        roomsField = createTextField(18);

        startDate = new DatePicker();
        startDate.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        endDate = new DatePicker();
        endDate.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(createLabel("Name:", labelFont), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        form.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        form.add(createLabel("Email:", labelFont), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        form.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        form.add(createLabel("Phone Number:", labelFont), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        form.add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        form.add(createLabel("Start Date:", labelFont), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        form.add(startDate, gbc);

        gbc.gridx = 2;
        gbc.gridy = 3;
        form.add(createLabel("End Date:", labelFont), gbc);

        gbc.gridx = 3;
        gbc.gridy = 3;
        form.add(endDate, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        form.add(createLabel("Rooms:", labelFont), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        form.add(roomsField, gbc);

        JButton cancelButton = new JButton("Cancel Reservation");
        cancelButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setBackground(new Color(215, 72, 95));
        cancelButton.setFocusPainted(false);
        cancelButton.setBorder(new EmptyBorder(12, 24, 12, 24));
        cancelButton.setOpaque(true);
        cancelButton.setContentAreaFilled(true);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(26, 12, 12, 12);
        form.add(cancelButton, gbc);

        return form;
    }

    private JLabel createLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(new Color(45, 60, 75));
        return label;
    }

    private JTextField createTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setPreferredSize(new Dimension(300, 42));
        field.setFont(new Font("SansSerif", Font.PLAIN, 16));
        field.setBorder(new LineBorder(new Color(190, 200, 210), 2, true));
        field.setBackground(Color.WHITE);
        return field;
    }

    private void styleComboBox(JComboBox<String> comboBox, int rows) {
        comboBox.setPreferredSize(new Dimension(210, 42));
        comboBox.setFont(new Font("SansSerif", Font.PLAIN, 16));
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(new LineBorder(new Color(190, 200, 210), 2, true));
        comboBox.setMaximumRowCount(rows);
    }

    // Optional getters if you want to use the field values elsewhere
    public String getNameValue() {
        return nameField.getText();
    }

    public String getEmailValue() {
        return emailField.getText();
    }

    public String getPhoneValue() {
        return phoneField.getText();
    }

    public String getRoomsValue() {
        return roomsField.getText();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Reservation Manager");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 860);
            frame.setLocationRelativeTo(null);

            CardLayout cardLayout = new CardLayout();
            JPanel mainPanel = new JPanel(cardLayout);

            ModifyReservationPage page = null;
            try {
                page = new ModifyReservationPage(cardLayout, mainPanel, new UIState());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            mainPanel.add(page, "RESERVATIONS");

            frame.setContentPane(mainPanel);
            cardLayout.show(mainPanel, "RESERVATIONS");

            frame.setVisible(true);
        });
    }
}
