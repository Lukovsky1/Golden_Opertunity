package com.GoldenOpportunity;

import com.GoldenOpportunity.dbLogin.DbUser;
import com.GoldenOpportunity.dbLogin.GuestReservationDao;
import com.GoldenOpportunity.dbLogin.UserDao;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfilePage extends JPanel {

    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final UIState uiState;
    private final UserDao userDao = new UserDao();
    private final GuestReservationDao guestReservationDao = new GuestReservationDao();
    private final ReservationService reservationService = new ReservationService();
    private JLabel fullNameValueLabel;
    private JLabel emailValueLabel;
    private JLabel phoneValueLabel;
    private JLabel usernameValueLabel;
    private JPanel reservationsContainer;

    public ProfilePage(CardLayout cardLayout, JPanel mainPanel, UIState uiState) throws IOException {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.uiState = uiState;

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        add(createHeader(), BorderLayout.NORTH);
        add(createScrollableContent(), BorderLayout.CENTER);
    }

    @Override
    public void setVisible(boolean aFlag) {
        if (aFlag) {
            refreshProfileData();
        }
        super.setVisible(aFlag);
    }

    // ================= HEADER =================
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
            cardLayout.show(mainPanel,"PROFILE");
        });

        header.add(logoLabel, BorderLayout.WEST);
        header.add(nav, BorderLayout.EAST);
        return header;
    }

    // ================= SCROLLABLE CONTENT =================
    private JScrollPane createScrollableContent() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(new Color(245, 245, 245));
        outer.setBorder(new EmptyBorder(14, 14, 14, 14));

        JPanel content = new JPanel(new GridLayout(1, 2, 14, 0));
        content.setBackground(new Color(245, 245, 245));

        content.add(createProfilePanel());
        content.add(createReservationsPanel());

        outer.add(content, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(outer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        return scrollPane;
    }

    // ================= LEFT PANEL =================
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(243, 243, 243));
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(190, 200, 210), 2),
                new EmptyBorder(14, 14, 14, 14)
        ));

        fullNameValueLabel = createValueLabel("XXXXXXX");
        emailValueLabel = createValueLabel("XXXXXXX");
        phoneValueLabel = createValueLabel("XXXXXXX");
        usernameValueLabel = createValueLabel("XXXXXXX");

        panel.add(createField("Name", fullNameValueLabel));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createField("Email", emailValueLabel));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createField("Phone Number", phoneValueLabel));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createField("Username", usernameValueLabel));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createField("Password", createValueLabel("**********")));
        panel.add(Box.createVerticalStrut(28));

        JButton editProfileBtn = createBlackButton("Edit Profile", 150, 50);
        panel.add(editProfileBtn);
        panel.add(Box.createVerticalStrut(28));

        JLabel paymentLabel = new JLabel("Payment Information");
        paymentLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        paymentLabel.setForeground(new Color(45, 55, 70));
        paymentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(paymentLabel);
        panel.add(Box.createVerticalStrut(6));

        JPanel paymentRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        paymentRow.setOpaque(false);
        paymentRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel paymentField = new JLabel("****XXXX");
        paymentField.setPreferredSize(new Dimension(200, 48));
        paymentField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        paymentField.setOpaque(true);
        paymentField.setBackground(Color.WHITE);
        paymentField.setBorder(new CompoundBorder(
                new LineBorder(new Color(190, 200, 210), 2, true),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JButton editPaymentBtn = createBlackButton("Edit Payment Info", 210, 48);

        paymentRow.add(paymentField);
        paymentRow.add(editPaymentBtn);

        panel.add(paymentRow);

        return panel;
    }

    private JPanel createField(String labelText, JLabel valueLabel) {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setOpaque(false);
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 15));
        label.setForeground(new Color(45, 55, 70));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        wrapper.add(label);
        wrapper.add(Box.createVerticalStrut(4));
        wrapper.add(valueLabel);

        return wrapper;
    }

    private JLabel createValueLabel(String value) {
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        valueLabel.setForeground(Color.BLACK);
        valueLabel.setOpaque(true);
        valueLabel.setBackground(Color.WHITE);
        valueLabel.setBorder(new CompoundBorder(
                new LineBorder(new Color(190, 200, 210), 2, true),
                new EmptyBorder(10, 10, 10, 10)
        ));
        valueLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        valueLabel.setPreferredSize(new Dimension(300, 48));
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return valueLabel;
    }

    // ================= RIGHT PANEL =================
    private JPanel createReservationsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(243, 243, 243));
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(190, 200, 210), 2),
                new EmptyBorder(14, 14, 14, 14)
        ));

        JLabel titleLabel = new JLabel("Reservations");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(new Color(45, 55, 70));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        reservationsContainer = new JPanel();
        reservationsContainer.setLayout(new BoxLayout(reservationsContainer, BoxLayout.Y_AXIS));
        reservationsContainer.setOpaque(false);
        reservationsContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(12));
        panel.add(reservationsContainer);

        return panel;
    }

    private JPanel createReservationCard(Reservation reservation) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(new Color(243, 243, 243));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(190, 200, 210), 2),
                new EmptyBorder(10, 12, 10, 12)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String dates = formatter.format(reservation.getDateRange().startDate())
                + " - " + formatter.format(reservation.getDateRange().endDate());
        String rooms = reservation.getRooms().stream()
                .map(room -> Integer.toString(room.getRoomNo()))
                .reduce((left, right) -> left + ", " + right)
                .orElse("No rooms");

        // LEFT SIDE (INFO)
        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        JLabel reservationLabel = new JLabel("Reservation: " + reservation.getId());
        reservationLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        reservationLabel.setForeground(new Color(45, 55, 70));

        JLabel datesLabel = new JLabel("Dates: " + dates);
        datesLabel.setFont(new Font("SansSerif", Font.PLAIN, 17));

        JLabel roomsLabel = new JLabel("Rooms: " + rooms);
        roomsLabel.setFont(new Font("SansSerif", Font.PLAIN, 17));

        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(reservationLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(datesLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(roomsLabel);

        // RIGHT SIDE (BUTTON - CENTERED)
        JPanel buttonWrapper = new JPanel(new GridBagLayout()); // this centers vertically
        buttonWrapper.setOpaque(false);

        JButton modifyBtn = createBlackButton("Modify Reservation", 225, 50);
        modifyBtn.addActionListener(e -> openModifyReservationPage(reservation.getId()));
        buttonWrapper.add(modifyBtn); // GridBagLayout centers it automatically

        // ADD TO CARD
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(buttonWrapper, BorderLayout.EAST);

        return card;
    }

    private void refreshProfileData() {
        refreshUserDetails();
        refreshReservations();
    }

    private void refreshUserDetails() {
        if (uiState.getCurrentSession() == null) {
            fullNameValueLabel.setText("Not logged in");
            emailValueLabel.setText("Not logged in");
            phoneValueLabel.setText("Not logged in");
            usernameValueLabel.setText("Not logged in");
            return;
        }

        try {
            DbUser user = userDao.findById(uiState.getCurrentSession().getUserId());
            if (user == null) {
                fullNameValueLabel.setText("Unknown user");
                emailValueLabel.setText("Unknown user");
                phoneValueLabel.setText("Unknown user");
                usernameValueLabel.setText("Unknown user");
                return;
            }

            fullNameValueLabel.setText(user.fullName == null || user.fullName.isBlank() ? user.username : user.fullName);
            emailValueLabel.setText(user.contactInfo == null || user.contactInfo.isBlank() ? "-" : user.contactInfo);
            phoneValueLabel.setText(user.phoneNumber == null || user.phoneNumber.isBlank() ? "-" : user.phoneNumber);
            usernameValueLabel.setText(user.username);
        } catch (SQLException e) {
            fullNameValueLabel.setText("Error loading user");
            emailValueLabel.setText("Error loading user");
            phoneValueLabel.setText("Error loading user");
            usernameValueLabel.setText("Error loading user");
        }
    }

    private void refreshReservations() {
        reservationsContainer.removeAll();

        List<Reservation> reservations = loadCurrentGuestReservations();
        if (reservations.isEmpty()) {
            JLabel emptyLabel = new JLabel("No reservations found for this guest.");
            emptyLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
            emptyLabel.setForeground(new Color(70, 80, 90));
            emptyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            reservationsContainer.add(emptyLabel);
        } else {
            for (int i = 0; i < reservations.size(); i++) {
                reservationsContainer.add(createReservationCard(reservations.get(i)));
                if (i < reservations.size() - 1) {
                    reservationsContainer.add(Box.createVerticalStrut(14));
                }
            }
        }

        reservationsContainer.revalidate();
        reservationsContainer.repaint();
    }

    private List<Reservation> loadCurrentGuestReservations() {
        if (uiState.getCurrentSession() == null) {
            return List.of();
        }

        try {
            List<String> reservationIds = guestReservationDao.findReservationIdsByGuestId(uiState.getCurrentSession().getUserId());
            List<Reservation> reservations = new ArrayList<>();
            for (String reservationId : reservationIds) {
                Reservation reservation = reservationService.findReservation(reservationId);
                if (reservation != null) {
                    reservations.add(reservation);
                }
            }
            return reservations;
        } catch (SQLException e) {
            return List.of();
        }
    }

    private void openModifyReservationPage(String reservationId) {
        uiState.setSelectedReservationId(reservationId);
        cardLayout.show(mainPanel, "MODIFY_RESERVATION");
    }

    // ================= BUTTON HELPERS =================
    private JButton createBlackButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, height));
        button.setMaximumSize(new Dimension(width, height));
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.setForeground(Color.WHITE);
        button.setBackground(Color.BLACK);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        return button;
    }

    private JButton createRedButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, height));
        button.setMaximumSize(new Dimension(width, height));
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(220, 53, 69)); // RED
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        return button;
    }
}
