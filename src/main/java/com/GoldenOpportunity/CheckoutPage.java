package com.GoldenOpportunity;

import com.GoldenOpportunity.Login.enums.Role;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.time.Year;
import java.util.HashMap;
import java.util.Map;

public class CheckoutPage extends JPanel {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private UIState uiState;

    // Guest Info Fields
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField memberNumberField;
    private JTextField phoneField;
    private JTextField addressField;
    private JTextField cityField;
    private JTextField stateField;
    private JTextField postalCodeField;
    private JComboBox<String> countryBox;

    // Payment Info Fields
    private JTextField cardNumberField;
    private JComboBox<String> monthBox;
    private JComboBox<String> yearBox;
    private JTextField cvvField;
    private JTextField billingZipField;

    public CheckoutPage(CardLayout cardLayout, JPanel mainPanel, UIState uiState) throws IOException {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.uiState = uiState;

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 245, 245));

        if(uiState.getCurrentSession() != null && uiState.getCurrentSession().getRole() == Role.CLERK){
            add(createClerkHeader(), BorderLayout.NORTH);
        }
        else{
            add(createGuestHeader(), BorderLayout.NORTH);
        }

        JScrollPane scrollPane = new JScrollPane(createMainContent());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        SwingUtilities.invokeLater(() -> {
            scrollPane.getVerticalScrollBar().setValue(0);
        });

        add(scrollPane, BorderLayout.CENTER);
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

    // ================= HEADER =================
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
            cardLayout.show(mainPanel,"CHECKOUT");
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

    // ================= MAIN =================
    private JPanel createMainContent() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
        container.setBorder(new EmptyBorder(20, 20, 20, 20));
        container.setBackground(new Color(245, 245, 245));

        JPanel leftPanel = createLeftPanel();
        JPanel summaryPanel = createSummaryPanel();

        leftPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        summaryPanel.setAlignmentY(Component.TOP_ALIGNMENT);

        container.add(leftPanel);
        container.add(Box.createHorizontalStrut(20));
        container.add(summaryPanel);

        return container;
    }

    // ================= LEFT SIDE =================
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 245, 245));

        panel.add(createGuestInfo());
        panel.add(Box.createVerticalStrut(20));
        panel.add(createPaymentInfo());

        return panel;
    }

    private JPanel createGuestInfo() {
        JPanel box = createCardPanel();

        box.add(sectionTitle("Guest Information"));

        firstNameField = styledField();
        lastNameField = styledField();
        box.add(twoFieldRow("First Name", firstNameField, "Last Name", lastNameField));

        emailField = styledField();
        memberNumberField = styledField();
        box.add(twoFieldRow("Email Address", emailField, "Member Number", memberNumberField));

        phoneField = styledField();
        box.add(oneField("Phone Number", phoneField));

        String[] countries = {"United States", "Canada", "United Kingdom", "Germany", "France", "India", "Japan", "Australia"};
        countryBox = new JComboBox<>(countries);
        countryBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        box.add(labeledComponent("Country/Region", countryBox));

        addressField = styledField();
        box.add(oneField("Address Line", addressField));

        cityField = styledField();
        stateField = styledField();
        box.add(twoFieldRow("City", cityField, "State/Province", stateField));

        postalCodeField = styledField();
        box.add(oneField("Postal Code", postalCodeField));

        return box;
    }

    private JPanel createPaymentInfo() {
        JPanel box = createCardPanel();

        box.add(sectionTitle("Payment Information"));

        cardNumberField = styledField();
        box.add(oneField("Credit/Debit Card Number", cardNumberField));

        JPanel expiryRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 0));
        expiryRow.setBackground(Color.WHITE);
        expiryRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] months = {"01","02","03","04","05","06","07","08","09","10","11","12"};
        monthBox = new JComboBox<>(months);
        monthBox.setMaximumSize(new Dimension(80, 30));

        String[] years = new String[10];
        int currentYear = Year.now().getValue();
        for (int i = 0; i < 10; i++) {
            years[i] = String.valueOf(currentYear + i);
        }
        yearBox = new JComboBox<>(years);
        yearBox.setMaximumSize(new Dimension(100, 30));

        expiryRow.add(labeledComponent("Expiration Month", monthBox));
        expiryRow.add(labeledComponent("Expiration Year", yearBox));

        box.add(expiryRow);
        box.add(Box.createVerticalStrut(10));

        cvvField = styledField();
        billingZipField = styledField();
        box.add(twoFieldRow("CVV Number", cvvField, "Billing Zip Code", billingZipField));

        return box;
    }

    // ================= RIGHT SIDE =================
    private JPanel createSummaryPanel() {
        JPanel panel = createCardPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = sectionTitle("Booking Summary");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);

        double sum = 0.0;
        long nights = ChronoUnit.DAYS.between(uiState.startDate, uiState.endDate);
        if (nights < 0) {
            nights = 0;
        }

        for (Room room : uiState.potentialRooms) {
            JPanel roomCard = new JPanel();
            roomCard.setLayout(new BoxLayout(roomCard, BoxLayout.Y_AXIS));
            roomCard.setBackground(Color.WHITE);
            roomCard.setAlignmentX(Component.LEFT_ALIGNMENT);
            roomCard.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(Color.LIGHT_GRAY, 1),
                    new EmptyBorder(10, 10, 10, 10)
            ));
            roomCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));

            roomCard.add(realImagePanel(room.getImage()));

            String floor = "Floor Number: " + room.getFloorNum();
            String roomNumber = "Room Number: " + room.getRoomNo();

            String desc = "";
            for (Map.Entry<String, Integer> entry : room.getBedTypes().entrySet()) {
                desc = desc.concat(entry.getValue() + " ");
                desc = desc.concat(entry.getKey() + ", ");
            }
            String finalDesc = desc.substring(0, desc.length() - 2);

            roomCard.add(leftLabel(floor));
            roomCard.add(leftLabel(roomNumber));
            roomCard.add(leftLabel(finalDesc));
            roomCard.add(leftLabel("Price: " + String.format("%.2f", room.getRate()) + " / night"));

            JButton deleteBtn = new JButton("Delete");
            deleteBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

// Make it red
            deleteBtn.setBackground(new Color(200, 50, 50)); // nice red
            deleteBtn.setForeground(Color.WHITE);
            deleteBtn.setOpaque(true);
            deleteBtn.setContentAreaFilled(true);
            deleteBtn.setBorderPainted(false);

// Optional: nicer padding/size
            deleteBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

            deleteBtn.addActionListener(e -> {
                uiState.potentialRooms.remove(room);

                removeAll();
                try {
                    if(uiState.getCurrentSession() != null && uiState.getCurrentSession().getRole() == Role.CLERK){
                        add(createClerkHeader(), BorderLayout.NORTH);
                    }
                    else{
                        add(createGuestHeader(), BorderLayout.NORTH);
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                JScrollPane scrollPane = new JScrollPane(createMainContent());
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                scrollPane.getVerticalScrollBar().setUnitIncrement(16);

                add(scrollPane, BorderLayout.CENTER);

                revalidate();
                repaint();

                if(uiState.potentialRooms.isEmpty()){
                    if(uiState.getCurrentSession() != null && uiState.getCurrentSession().getRole() == Role.CLERK){
                        cardLayout.show(mainPanel,"NEW_RESERVATION");
                    }
                    else{
                        cardLayout.show(mainPanel,"ROOMS");
                    }
                }
            });

            roomCard.add(Box.createVerticalStrut(8));
            roomCard.add(deleteBtn);

            panel.add(roomCard);
            panel.add(Box.createVerticalStrut(15));

            sum += room.getRate() * nights;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        JLabel dates = leftLabel(formatter.format(uiState.startDate) + " - " + formatter.format(uiState.endDate));
        dates.setFont(new Font("SansSerif", Font.BOLD, 18));
        JLabel numGuests = leftLabel(uiState.numGuests + " Guests");
        numGuests.setFont(new Font("SansSerif", Font.BOLD, 18));
        JLabel totalPrice = leftLabel("USD Total: $" + String.format("%.2f",sum));
        totalPrice.setFont(new Font("SansSerif", Font.BOLD, 18));

        panel.add(dates);
        panel.add(numGuests);
        panel.add(totalPrice);

        JButton bookBtn = new JButton("Book");
        bookBtn.setBackground(new Color(30, 170, 70));
        bookBtn.setForeground(Color.WHITE);
        bookBtn.setOpaque(true);
        bookBtn.setBorderPainted(false);
        bookBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        bookBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        double finalSum = sum;
        bookBtn.addActionListener(e -> {
            try {
                uiState.reservationService.createReservation(uiState.potentialRooms,uiState.startDate,uiState.endDate, finalSum);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            uiState.potentialRooms.clear();
            if(uiState.getCurrentSession() != null && uiState.getCurrentSession().getRole() == Role.CLERK){
                cardLayout.show(mainPanel,"NEW_RESERVATION");
            }
            else{
                cardLayout.show(mainPanel,"HOME");
            }
            JOptionPane.showMessageDialog(null, "Reservation was successfully made!");
        });

        panel.add(Box.createVerticalStrut(10));
        panel.add(bookBtn);

        Dimension preferred = panel.getPreferredSize();
        panel.setPreferredSize(new Dimension(400, preferred.height));
        panel.setMaximumSize(new Dimension(400, Integer.MAX_VALUE));

        return panel;
    }

    // ================= HELPERS =================

    private JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new CompoundBorder(
                new LineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(15, 15, 15, 15)
        ));
        return panel;
    }

    private JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 20));
        label.setBorder(new EmptyBorder(5, 0, 10, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JLabel leftLabel(String text) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField styledField() {
        JTextField field = new JTextField();
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }

    private JPanel oneField(String label, JTextField field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel jLabel = new JLabel(label);
        jLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(jLabel);
        panel.add(field);
        panel.add(Box.createVerticalStrut(10));

        return panel;
    }

    private JPanel twoFieldRow(String l1, JTextField f1, String l2, JTextField f2) {
        JPanel row = new JPanel(new GridLayout(1, 2, 15, 0));
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        row.add(labeledField(l1, f1));
        row.add(labeledField(l2, f2));

        return row;
    }

    private JPanel labeledField(String label, JTextField field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel jLabel = new JLabel(label);
        jLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(jLabel);
        panel.add(field);

        return panel;
    }

    private JPanel labeledComponent(String label, JComponent comp) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel jLabel = new JLabel(label);
        jLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        comp.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(jLabel);
        panel.add(comp);
        panel.add(Box.createVerticalStrut(10));

        return panel;
    }

    private JPanel realImagePanel(String path) {
        JPanel imgPanel = new JPanel(new BorderLayout());
        imgPanel.setPreferredSize(new Dimension(300, 140));
        imgPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        imgPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        imgPanel.setBackground(Color.WHITE);

        try {
            Image img = ImageIO.read(new File(path));
            Image scaled = img.getScaledInstance(300, 140, Image.SCALE_SMOOTH);

            JLabel label = new JLabel(new ImageIcon(scaled));
            imgPanel.add(label, BorderLayout.CENTER);

        } catch (IOException e) {
            JLabel fallback = new JLabel("Image not found", SwingConstants.CENTER);
            imgPanel.add(fallback, BorderLayout.CENTER);
        }

        return imgPanel;
    }

    private void validateGuestInfo(){

    }

    private void validatePaymentInfo(){

    }
}
