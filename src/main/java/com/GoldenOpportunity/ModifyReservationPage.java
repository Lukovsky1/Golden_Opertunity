package com.GoldenOpportunity;

import com.GoldenOpportunity.Roles.Guest;
import com.GoldenOpportunity.Roles.RolePermissions;
import com.github.lgooddatepicker.components.DatePicker;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ModifyReservationPage extends JPanel {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private ProfilePage profilePage;

    private Guest guest;
    private Reservation reservation;
    private UIState uiState;

    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private DatePicker startDate;
    private DatePicker endDate;

    public ModifyReservationPage(ProfilePage profilePage,
                                      CardLayout cardLayout,JPanel mainPanel,Guest guest,
                                      Reservation reservation,UIState uiState) throws IOException {
        this.profilePage = profilePage;
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.guest = guest;
        this.reservation = reservation;
        this.uiState = uiState;

        setLayout(new BorderLayout());
        setBackground(new Color(240, 243, 247));

        add(createHeader(),BorderLayout.NORTH);
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

    private JPanel createBody() {
        JPanel body = new JPanel(new BorderLayout(20, 0));
        body.setBackground(new Color(240, 243, 247));
        body.setBorder(new EmptyBorder(18, 20, 20, 20));

        body.add(createReservationPanel(), BorderLayout.CENTER);

        return body;
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

        saveButton.addActionListener(e -> {
            //TODO
        });

        cancelButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to cancel reservation?",
                    "Logout",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                uiState.reservationService.deleteReservation(reservation.getId());
                profilePage.updatePage();
                cardLayout.show(mainPanel,"CLERK_HOME");
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
