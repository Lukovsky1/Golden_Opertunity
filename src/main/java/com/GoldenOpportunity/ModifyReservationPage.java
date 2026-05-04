package com.GoldenOpportunity;

import com.GoldenOpportunity.Login.enums.Role;
import com.GoldenOpportunity.Roles.Guest;
import com.GoldenOpportunity.dbLogin.DbUser;
import com.GoldenOpportunity.dbLogin.UserDao;
import com.github.lgooddatepicker.components.DatePicker;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//TODO finish modify reservation for both clerk and guest

public class ModifyReservationPage extends JPanel {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    private Reservation reservation;
    private UIState uiState;
    private ClerkHomePage clerkHomePage;
    private ProfilePage profilePage;

    private JTextField nameField;
    private DatePicker startDate;
    private DatePicker endDate;

    JComboBox<String> addRoomBox;
    JComboBox<String> currentRoomBox;

    private JPanel reservationPanel;
    private JPanel billPanel;

    public ModifyReservationPage(ClerkHomePage clerkHomePage, CardLayout cardLayout, JPanel mainPanel, Reservation reservation, UIState uiState) throws IOException {
        this.clerkHomePage = clerkHomePage;
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.reservation = reservation;
        this.uiState = uiState;

        setLayout(new BorderLayout());
        setBackground(new Color(240, 243, 247));

        if(uiState.getCurrentSession().getRole() == Role.CLERK){
            add(createClerkHeader(), BorderLayout.NORTH);
        }
        else if(uiState.getCurrentSession().getRole() == Role.GUEST){
            add(createGuestHeader(), BorderLayout.NORTH);
        }
        add(createBody(), BorderLayout.CENTER);
    }

    public ModifyReservationPage(ProfilePage profilePage, CardLayout cardLayout, JPanel mainPanel, Reservation reservation, UIState uiState) throws IOException {
        this.profilePage = profilePage;
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.reservation = reservation;
        this.uiState = uiState;

        setLayout(new BorderLayout());
        setBackground(new Color(240, 243, 247));

        if(uiState.getCurrentSession().getRole() == Role.CLERK){
            add(createClerkHeader(), BorderLayout.NORTH);
        }
        else if(uiState.getCurrentSession().getRole() == Role.GUEST){
            add(createGuestHeader(), BorderLayout.NORTH);
        }
        add(createBody(), BorderLayout.CENTER);
    }

    private JPanel createClerkHeader() throws IOException {
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
            if(!uiState.isLoggedIn){
                cardLayout.show(mainPanel,"LOGIN");
            }
            else{
                uiState.updateProfilePanel();
                cardLayout.show(mainPanel,"PROFILE");
                mainPanel.revalidate();
                mainPanel.repaint();
            }
        });
        homeButton.addActionListener(e -> {
            cardLayout.show(mainPanel,"CLERK_HOME");
        });

        header.add(logoLabel, BorderLayout.WEST);
        header.add(nav, BorderLayout.EAST);
        return header;
    }

    private JPanel createGuestHeader() throws IOException {
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
                uiState.updateProfilePanel();
                cardLayout.show(mainPanel,"PROFILE");
                mainPanel.revalidate();
                mainPanel.repaint();
            }
        });

        header.add(logoLabel, BorderLayout.WEST);
        header.add(nav, BorderLayout.EAST);
        return header;
    }

    private JPanel createBody() {
        JPanel body = new JPanel(new BorderLayout(20, 0));
        body.setBackground(new Color(240, 243, 247));
        body.setBorder(new EmptyBorder(18, 20, 20, 20));

        if(uiState.getCurrentSession().getRole() == Role.CLERK){
            body.add(createSidebar(), BorderLayout.WEST);
        }
        reservationPanel = createReservationPanel();
        body.add(reservationPanel, BorderLayout.CENTER);

        return body;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Color.WHITE);
        sidebar.setBorder(new LineBorder(new Color(190, 205, 218), 1));
        sidebar.setPreferredSize(new Dimension(185, 0));

        JButton addRoomsButton = createSidebarButton("Add Rooms");
        JButton modifyRoomsButton = createSidebarButton("Modify Rooms");
        JButton newReservation = createSidebarButton("New Reservation");

        addRoomsButton.addActionListener(e -> {
            cardLayout.show(mainPanel,"ADD_ROOMS");
        });
        modifyRoomsButton.addActionListener(e -> {
            cardLayout.show(mainPanel,"MODIFY_ROOMS");
        });
        newReservation.addActionListener(e -> {
            cardLayout.show(mainPanel,"NEW_RESERVATION");
        });

        sidebar.add(Box.createVerticalStrut(14));
        sidebar.add(addRoomsButton);
        sidebar.add(Box.createVerticalStrut(14));
        sidebar.add(modifyRoomsButton);
        sidebar.add(Box.createVerticalStrut(14));
        sidebar.add(newReservation);
        sidebar.add(Box.createVerticalGlue());

        return sidebar;
    }

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(145, 42));
        button.setPreferredSize(new Dimension(145, 42));
        button.setFocusPainted(false);
        button.setBackground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.setBorder(new LineBorder(new Color(140, 140, 140), 1, true));

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
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel title = new JLabel("Reservation:");
        title.setFont(new Font("SansSerif", Font.PLAIN, 32));
        title.setForeground(new Color(43, 58, 72));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.weightx = 1;
        panel.add(title, gbc);

        nameField = createTextField();

        nameField.setText(reservation.getName());

        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(createLabel("Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        nameField.setPreferredSize(new Dimension(400, 50));
        panel.add(nameField, gbc);

        startDate = new DatePicker();
        endDate = new DatePicker();

        startDate.setDate(reservation.dateRange.startDate());
        endDate.setDate(reservation.dateRange.endDate());

        startDate.addDateChangeListener(e -> changeRoomBoxes());
        endDate.addDateChangeListener(e -> changeRoomBoxes());

        gbc.gridy = 2;
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(createLabel("Start Date:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.4;
        gbc.anchor = GridBagConstraints.WEST;
        startDate.setPreferredSize(new Dimension(170, 50));
        panel.add(startDate, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(createLabel("End Date:"), gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.4;
        gbc.anchor = GridBagConstraints.WEST;
        endDate.setPreferredSize(new Dimension(170, 50));
        panel.add(endDate, gbc);

        addRoomBox = new JComboBox<>(new String[]{"Add Rooms"});
        currentRoomBox = new JComboBox<>(new String[]{"Current Rooms / Delete Rooms"});

        Criteria criteria = new Criteria();
        criteria.setDateRange(new DateRange(startDate.getDate(),endDate.getDate()));

        try{
            for(Room room : uiState.searchController.searchAvailableRooms(criteria)){
                addRoomBox.addItem(String.valueOf(room.getRoomNo()));
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

        for (Room room : reservation.getRooms()) {
            currentRoomBox.addItem(String.valueOf(room.getRoomNo()));
        }

        styleRoomComboBox(addRoomBox, 150, 50);
        styleRoomComboBox(currentRoomBox, 300, 50);

        gbc.gridy = 3;

        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(createLabel("Rooms:"), gbc);

        JPanel roomComboPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        roomComboPanel.setOpaque(false);

        roomComboPanel.add(addRoomBox);
        roomComboPanel.add(Box.createHorizontalStrut(28));
        roomComboPanel.add(currentRoomBox);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(roomComboPanel, gbc);

        JButton addRoomButton = createGreenButton("Add Room", 150, 55);
        JButton deleteRoomButton = createRedButton("Delete Room", 170, 55);

        addRoomButton.addActionListener(e -> {
            if(addRoomBox.getSelectedItem().toString().equals("Add Rooms")){
                JOptionPane.showMessageDialog(null, "Make a valid room selection.");
            }
            else if(comboBoxContains(currentRoomBox,addRoomBox.getSelectedItem().toString())){
                JOptionPane.showMessageDialog(null, "Room already added.");
            }
            else{
                currentRoomBox.addItem(addRoomBox.getSelectedItem().toString());
                updateRoomBoxes();
            }
        });
        deleteRoomButton.addActionListener(e -> {
            if(currentRoomBox.getSelectedItem().toString().equals("Current Rooms / Delete Rooms")){
                JOptionPane.showMessageDialog(null, "Make a valid room selection.");
            }
            else{
                currentRoomBox.removeItem(currentRoomBox.getSelectedItem());
                updateRoomBoxes();
            }
        });

        JPanel roomButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 0));
        roomButtonPanel.setOpaque(false);
        roomButtonPanel.add(addRoomButton);
        roomButtonPanel.add(deleteRoomButton);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 4;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(26, 12, 8, 12);
        panel.add(roomButtonPanel, gbc);

        int buttonRow = 5;

        if (uiState.getCurrentSession().getRole() == Role.CLERK) {
            JButton checkInButton = createGreenButton("Check-In", 135, 55);
            JButton checkOutButton = createRedButton("Check-Out", 150, 55);
            JButton generateBillButton = createBlackButton("Generate Bill", 175, 55);

            checkInButton.addActionListener(e -> {
                if(reservation.isCheckedIn()){
                    JOptionPane.showMessageDialog(null, "Guest has already been checked-in.");
                }
                else{
                    try {
                        int confirm = JOptionPane.showConfirmDialog(
                                this,
                                "Are you sure you want to check-in?",
                                "Check-In",
                                JOptionPane.YES_NO_OPTION
                        );

                        if (confirm == JOptionPane.YES_OPTION) {
                            uiState.reservationService.checkIn(reservation.getId());
                            JOptionPane.showMessageDialog(null, "Guest has been checked-in.");
                        }
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
            checkOutButton.addActionListener(e -> {
                if(!reservation.isCheckedIn()){
                    JOptionPane.showMessageDialog(null, "Guest has not been checked-in.");
                }
                else{
                    try {
                        int confirm = JOptionPane.showConfirmDialog(
                                this,
                                "Are you sure you want to check-out?",
                                "Check-Out",
                                JOptionPane.YES_NO_OPTION
                        );

                        if (confirm == JOptionPane.YES_OPTION) {
                            uiState.reservationService.checkout(reservation.getId());
                            JOptionPane.showMessageDialog(null, "Guest has been checked-out.");
                            clerkHomePage.updatePage();
                            cardLayout.show(mainPanel, "CLERK_HOME");
                        }
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
            generateBillButton.addActionListener(e -> handleBill());

            JPanel clerkActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
            clerkActionPanel.setOpaque(false);
            clerkActionPanel.add(checkInButton);
            clerkActionPanel.add(checkOutButton);
            clerkActionPanel.add(generateBillButton);

            gbc.gridy = buttonRow++;
            gbc.gridx = 0;
            gbc.gridwidth = 4;
            gbc.insets = new Insets(8, 12, 8, 12);
            panel.add(clerkActionPanel, gbc);
        }

        JButton backButton = createBlackButton("Back", 140, 55);
        JButton saveButton = createBlackButton("Save Modifications", 230, 55);
        JButton cancelButton = createRedButton("Cancel Reservation", 250, 55);

        backButton.addActionListener(e -> {
            if (uiState.getCurrentSession().getRole() == Role.CLERK) {
                clerkHomePage.updatePage();
                cardLayout.show(mainPanel, "CLERK_HOME");
            } else if (uiState.getCurrentSession().getRole() == Role.GUEST) {
                try {
                    profilePage.updateProfilePage();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                cardLayout.show(mainPanel, "PROFILE");
            }
        });

        saveButton.addActionListener(e -> handleSave());
        cancelButton.addActionListener(e -> handleCancel());

        JPanel savePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        savePanel.setOpaque(false);

        savePanel.add(backButton);
        savePanel.add(saveButton);
        savePanel.add(cancelButton);

        gbc.gridy = buttonRow;
        gbc.gridx = 0;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(8, 12, 8, 12);
        panel.add(savePanel, gbc);

        return panel;
    }

    private JButton createGreenButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, height));
        button.setFocusPainted(false);
        button.setBackground(new Color(30, 130, 114));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 20));
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        return button;
    }

    private void styleRoomComboBox(JComboBox<String> box, int width, int height) {
        box.setPreferredSize(new Dimension(width, height));
        box.setFont(new Font("SansSerif", Font.PLAIN, 18));
        box.setBorder(new LineBorder(new Color(190, 205, 218), 2, true));
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

    private JPanel createBillSummaryPanel(Receipt receipt) throws SQLException {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(190, 205, 218), 2),
                new EmptyBorder(18, 24, 24, 24)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Receipt Summary:");
        title.setFont(new Font("SansSerif", Font.PLAIN, 32));
        title.setForeground(new Color(43, 58, 72));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        panel.add(title, gbc);

        int row = 1;

        addBillRow(panel, gbc, row++, createLabel("Guest Name:"), createValueLabel(nameField.getText().toString()));
        addBillRow(panel, gbc, row++, createLabel("Reservation ID:"), createValueLabel(receipt.getResID()));

        row = addSectionTitle(panel, gbc, row, "Room Charges");

        if (receipt.getRoomBill() == null || receipt.getRoomBill().isEmpty()) {
            addBillRow(panel, gbc, row++, createLabel("Rooms:"), createValueLabel("None"));
        } else {
            for (Map.Entry<Integer, Double> entry : receipt.getRoomBill().entrySet()) {
                addBillRow(
                        panel,
                        gbc,
                        row++,
                        createLabel("Room " + entry.getKey() + ":"),
                        createValueLabel(String.format("$%.2f", entry.getValue()))
                );
            }
        }

        row = addSectionTitle(panel, gbc, row, "Shop Items");

        if (receipt.getShopItemBill() == null || receipt.getShopItemBill().isEmpty()) {
            addBillRow(panel, gbc, row++, createLabel("Items:"), createValueLabel("None"));
        } else {
            for (Map.Entry<String, Double> entry : receipt.getShopItemBill().entrySet()) {
                addBillRow(
                        panel,
                        gbc,
                        row++,
                        createLabel(entry.getKey() + ":"),
                        createValueLabel(String.format("$%.2f", entry.getValue()))
                );
            }
        }

        addBillRow(panel, gbc, row++, createLabel("Penalty:"), createValueLabel(String.format("$%.2f", receipt.getPenalty())));

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(16, 12, 16, 12);
        panel.add(new JSeparator(), gbc);

        JLabel totalLabel = createLabel("Total:");
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 24));

        JLabel totalValue = createValueLabel(String.format("$%.2f", receipt.calculateTotal()));
        totalValue.setFont(new Font("SansSerif", Font.BOLD, 24));

        addBillRow(panel, gbc, row++, totalLabel, totalValue);

        JButton closeButton = createBlackButton("Close", 130, 55);
        closeButton.addActionListener(e -> {
            if (uiState.getCurrentSession().getRole() == Role.CLERK) {
                cardLayout.show(mainPanel, "CLERK_MODIFY_RESERVE");
            } else if (uiState.getCurrentSession().getRole() == Role.GUEST) {
                try {
                    profilePage.updateProfilePage();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                cardLayout.show(mainPanel, "PROFILE");
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(closeButton);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(22, 12, 0, 12);
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private int addSectionTitle(JPanel panel, GridBagConstraints gbc, int row, String text) {
        JLabel sectionTitle = new JLabel(text);
        sectionTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        sectionTitle.setForeground(new Color(43, 58, 72));

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.insets = new Insets(18, 12, 8, 12);

        panel.add(sectionTitle, gbc);

        return row + 1;
    }

    private void addBillRow(JPanel panel, GridBagConstraints gbc, int row, JLabel label, JLabel value) {
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 12, 8, 12);

        gbc.gridx = 0;
        gbc.weightx = 0;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(value, gbc);
    }

    private JLabel createValueLabel(String text) {
        JLabel label = new JLabel(text == null ? "" : text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 20));
        label.setForeground(new Color(43, 58, 72));
        return label;
    }

    private void handleSave(){
        List<Room> roomList = new ArrayList<>();

        for (int i = 1; i < currentRoomBox.getItemCount(); i++) {
            Room room = uiState.roomService.findRoom(Integer.parseInt(currentRoomBox.getItemAt(i)));

            if (room != null) {
                roomList.add(room);
            }
        }

        try {
            uiState.reservationService.modifyReservation(
                    reservation.getId(),
                    startDate.getDate(),
                    endDate.getDate(),
                    roomList,
                    nameField.getText()
            );

            if (uiState.getCurrentSession().getRole() == Role.CLERK) {
                clerkHomePage.updatePage();
                cardLayout.show(mainPanel, "CLERK_HOME");
            } else if (uiState.getCurrentSession().getRole() == Role.GUEST) {
                profilePage.updateProfilePage();
                cardLayout.show(mainPanel, "PROFILE");
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        JOptionPane.showMessageDialog(null, "Edit's Saved.");
    }

    private void handleCancel(){
        LocalDate today = LocalDate.now();
        LocalDate start = reservation.getDateRange().startDate();
        LocalDate cutoff = start.minusDays(2);
        boolean penalty = false;
        int confirm = 0;

        if(!today.isBefore(cutoff)){
            penalty = true;
            confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to cancel reservation? There will be a charge.",
                    "Cancel",
                    JOptionPane.YES_NO_OPTION
            );
        }
        else{
            confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to cancel reservation?",
                    "Cancel",
                    JOptionPane.YES_NO_OPTION
            );
        }

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if(!penalty){
                    Receipt receipt = uiState.reservationService.cancelReservation(reservation.getId());
                    JOptionPane.showMessageDialog(null, "Reservation Cancelled with No Charge.");
                    if (uiState.getCurrentSession().getRole() == Role.CLERK) {
                        clerkHomePage.updatePage();
                        cardLayout.show(mainPanel, "CLERK_HOME");
                    } else if (uiState.getCurrentSession().getRole() == Role.GUEST) {
                        profilePage.updateProfilePage();
                        cardLayout.show(mainPanel, "PROFILE");
                    }
                }
                else{
                    if (uiState.getCurrentSession().getRole() == Role.CLERK) {
                        handleBill();
                    } else if (uiState.getCurrentSession().getRole() == Role.GUEST) {
                        handleBill();
                    }
                    Receipt receipt = uiState.reservationService.cancelReservation(reservation.getId());
                    JOptionPane.showMessageDialog(null, "Reservation Cancelled with Charge of $" + receipt.getPenalty() + ".");
                }

            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private void changeRoomBoxes() {
        addRoomBox.removeAllItems();
        addRoomBox.addItem("Add Rooms");

        if (startDate.getDate() == null || endDate.getDate() == null) {
            return;
        }

        Criteria criteria = new Criteria();
        criteria.setDateRange(new DateRange(startDate.getDate(), endDate.getDate()));

        try {
            for (Room room : uiState.searchController.searchAvailableRooms(criteria)) {
                addRoomBox.addItem(String.valueOf(room.getRoomNo()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        reservationPanel.revalidate();
        reservationPanel.repaint();
    }

    private void updateRoomBoxes(){
        currentRoomBox.setSelectedIndex(0);
        addRoomBox.setSelectedIndex(0);

        reservationPanel.revalidate();
        reservationPanel.repaint();
    }

    public boolean comboBoxContains(JComboBox<String> box, String value) {
        for (int i = 0; i < box.getItemCount(); i++) {
            if (box.getItemAt(i).equals(value)) {
                return true;
            }
        }
        return false;
    }

    private void handleBill(){
        try {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Do you want to generate bill?",
                    "Generate Bill",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                if(reservation.getUserID() == null){
                    JOptionPane.showMessageDialog(null, "Bill sent to guest's email.");
                }
                else{
                    JOptionPane.showMessageDialog(null, "Bill sent to corporate email.");
                }
                Receipt receipt = uiState.reservationService.generateBilling(reservation.getId());

                if (billPanel != null) {
                    mainPanel.remove(billPanel);
                }

                billPanel = createBillSummaryPanel(receipt);
                mainPanel.add(billPanel, "BILL");

                cardLayout.show(mainPanel, "BILL");
            }

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}