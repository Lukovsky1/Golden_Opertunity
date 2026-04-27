package com.GoldenOpportunity;

import com.GoldenOpportunity.Login.enums.Role;
import com.GoldenOpportunity.Roles.Guest;
import com.github.lgooddatepicker.components.DatePicker;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//TODO finish modify reservation for both clerk and guest

public class ClerkModifyReservationPage extends JPanel {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    private Guest guest;
    private Reservation reservation;
    private UIState uiState;

    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private DatePicker startDate;
    private DatePicker endDate;

    public ClerkModifyReservationPage(CardLayout cardLayout,JPanel mainPanel,Guest guest,Reservation reservation,UIState uiState) throws IOException {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.guest = guest;
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
            cardLayout.show(mainPanel,"PROFILE");
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
                cardLayout.show(mainPanel,"PROFILE");
                mainPanel.revalidate();
                mainPanel.repaint();
            }
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

        if(uiState.getCurrentSession().getRole() == Role.CLERK){
            body.add(createSidebar(), BorderLayout.WEST);
        }
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

        JLabel title = new JLabel("Reservation:");
        title.setFont(new Font("SansSerif", Font.PLAIN, 32));
        title.setForeground(new Color(43, 58, 72));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.weightx = 1;
        panel.add(title, gbc);

        nameField = createTextField();
        emailField = createTextField();
        phoneField = createTextField();

        String[] data = guest.getContactInfo().split(",");
        nameField.setText(data[0]);
        emailField.setText(data[1]);
        phoneField.setText(data[2]);

        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(createLabel("Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(createLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1;
        panel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(createLabel("Phone Number:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1;
        panel.add(phoneField, gbc);

        startDate = new DatePicker();
        endDate = new DatePicker();

        startDate.setDate(reservation.dateRange.startDate());
        endDate.setDate(reservation.dateRange.endDate());

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

        JComboBox<String> RoomEditChoice = new JComboBox<>(new String[]{"Add", "Change", "Delete"});
        JComboBox<String> roomToEditBox = new JComboBox<>(new String[]{"Current Room"});

        for(Room room : reservation.getRooms()){
            roomToEditBox.addItem(String.valueOf(room.getRoomNo()));
        }

        JComboBox<String> newRoomBox = new JComboBox<>(new String[]{"New Room"});

        //TODO: implement better search
        for(Room room : uiState.roomService.searchRoom(DateRange)){
            newRoomBox.addItem(String.valueOf(room.getRoomNo()));
        }

        styleRoomComboBox(RoomEditChoice, 175, 50);
        styleRoomComboBox(roomToEditBox, 125, 50);

        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(createLabel("Rooms:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.4;
        panel.add(currentRoomBox, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.25;
        panel.add(newRoomBox, gbc);

        JButton saveRoomEditsButton = createBlackButton("Save Room Edits", 210, 55);

        JPanel roomEditPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        roomEditPanel.setOpaque(false);
        roomEditPanel.add(saveRoomEditsButton);

        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.gridwidth = 4;
        gbc.weightx = 1;
        gbc.insets = new Insets(28, 12, 10, 12);
        panel.add(roomEditPanel, gbc);

        JButton saveButton = createBlackButton("Save Reservation", 230, 55);
        JButton cancelButton = createRedButton("Cancel Reservation", 250, 55);

        cancelButton.addActionListener(e -> {
            uiState.reservationService.deleteReservation(reservation.getId());
            if(uiState.getCurrentSession().getRole() == Role.CLERK){
                cardLayout.show(mainPanel,"CLERK_HOME");
            }
            else if(uiState.getCurrentSession().getRole() == Role.GUEST){
                cardLayout.show(mainPanel,"PROFILE");
            }
        });

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        actionPanel.setOpaque(false);
        actionPanel.add(saveButton);
        actionPanel.add(Box.createHorizontalStrut(22));
        actionPanel.add(cancelButton);

        gbc.gridy = 7;
        gbc.gridx = 0;
        gbc.gridwidth = 4;
        gbc.weightx = 1;
        gbc.insets = new Insets(10, 12, 0, 12);
        panel.add(actionPanel, gbc);

        return panel;
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
                clerkReservation = new ClerkModifyReservationPage(cardLayout, mainPanel,null,null);
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