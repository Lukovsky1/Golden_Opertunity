package com.GoldenOpportunity;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ClerkHomePage extends JPanel {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    public ClerkHomePage(CardLayout cardLayout, JPanel mainPanel) throws IOException {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;

        setLayout(new BorderLayout());
        setBackground(new Color(240, 243, 247));

        add(createHeader(), BorderLayout.NORTH);
        add(createBody(), BorderLayout.CENTER);
    }

    // =========================
    // HEADER
    // =========================
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

    // =========================
    // MAIN BODY
    // =========================
    private JPanel createBody() {
        JPanel body = new JPanel(new BorderLayout(18, 18));
        body.setBackground(new Color(240, 243, 247));
        body.setBorder(new EmptyBorder(14, 16, 16, 16));

        body.add(createSidebar(), BorderLayout.WEST);
        body.add(createContentSection(), BorderLayout.CENTER);

        return body;
    }

    // =========================
    // SIDEBAR
    // =========================
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Color.WHITE);
        sidebar.setBorder(new LineBorder(new Color(190, 205, 218), 1));
        sidebar.setPreferredSize(new Dimension(185, 0));

        JButton addRoomsButton = createSidebarButton("Add Rooms");
        JButton modifyRoomsButton = createSidebarButton("Modify Rooms");

        addRoomsButton.addActionListener(e -> {
            cardLayout.show(mainPanel,"ADD_ROOMS");
        });
        modifyRoomsButton.addActionListener(e -> {
            cardLayout.show(mainPanel,"MODIFY_ROOMS");
        });

        sidebar.add(Box.createVerticalStrut(14));
        sidebar.add(addRoomsButton);
        sidebar.add(Box.createVerticalStrut(14));
        sidebar.add(modifyRoomsButton);
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

    // =========================
    // RIGHT CONTENT
    // =========================
    private JPanel createContentSection() {
        JPanel contentSection = new JPanel(new BorderLayout(0, 18));
        contentSection.setOpaque(false);

        contentSection.add(createSearchBar(), BorderLayout.NORTH);
        contentSection.add(createScrollableCards(), BorderLayout.CENTER);

        return contentSection;
    }

    // =========================
    // SEARCH BAR
    // =========================
    private JPanel createSearchBar() {
        JPanel searchPanel = new JPanel(new BorderLayout(12, 0));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(190, 205, 218), 1),
                new EmptyBorder(10, 16, 10, 10)
        ));
        searchPanel.setPreferredSize(new Dimension(0, 66));

        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(300, 40));
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 18));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 210, 220), 1, true),
                new EmptyBorder(0, 14, 0, 14)
        ));
        searchField.setText("Search");
        searchField.setForeground(new Color(170, 180, 190));

        JButton searchButton = new JButton("Search");
        searchButton.setFocusPainted(false);
        searchButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        searchButton.setForeground(Color.WHITE);
        searchButton.setBackground(new Color(52, 138, 230));
        searchButton.setPreferredSize(new Dimension(110, 40));
        searchButton.setBorder(BorderFactory.createEmptyBorder());
        searchButton.setOpaque(true);
        searchButton.setContentAreaFilled(true);

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        return searchPanel;
    }

    // =========================
    // SCROLLABLE CARD AREA
    // =========================
    private JScrollPane createScrollableCards() {
        JPanel cardsPanel = new JPanel(new GridLayout(0, 2, 18, 18));
        cardsPanel.setOpaque(false);
        cardsPanel.setBorder(new EmptyBorder(2, 2, 2, 2));

        // Example cards
        for (int i = 0; i < 8; i++) {
            cardsPanel.add(createReservationCard(
                    "Name",
                    "Email",
                    "Telephone",
                    "xx/xx/xxxx - xx/xx/xxxx",
                    "102, 103, 104"
            ));
        }

        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(240, 243, 247));
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        return scrollPane;
    }

    // =========================
    // CARD
    // =========================
    private JPanel createReservationCard(String name, String email, String phone, String dates, String rooms) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(190, 205, 218), 1),
                new EmptyBorder(12, 12, 12, 12)
        ));
        card.setPreferredSize(new Dimension(300, 300));

        Font textFont = new Font("SansSerif", Font.PLAIN, 15);
        Font titleFont = new Font("SansSerif", Font.BOLD, 15);

        JLabel guestInfoTitle = new JLabel("Guest Information:");
        guestInfoTitle.setFont(titleFont);
        guestInfoTitle.setForeground(new Color(55, 70, 85));

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(textFont);
        nameLabel.setForeground(new Color(55, 70, 85));

        JLabel emailLabel = new JLabel(email);
        emailLabel.setFont(textFont);
        emailLabel.setForeground(new Color(55, 70, 85));

        JLabel phoneLabel = new JLabel(phone);
        phoneLabel.setFont(textFont);
        phoneLabel.setForeground(new Color(55, 70, 85));

        JLabel reservationTitle = new JLabel("Reservation:");
        reservationTitle.setFont(titleFont);
        reservationTitle.setForeground(new Color(55, 70, 85));

        JLabel datesLabel = new JLabel("Dates: " + dates);
        datesLabel.setFont(textFont);
        datesLabel.setForeground(new Color(55, 70, 85));

        JLabel roomsLabel = new JLabel("Rooms: " + rooms);
        roomsLabel.setFont(textFont);
        roomsLabel.setForeground(new Color(55, 70, 85));

        JButton detailsButton = new JButton("Modify Reservation");
        detailsButton.setFocusPainted(false);
        detailsButton.setForeground(Color.WHITE);
        detailsButton.setBackground(Color.BLACK);
        detailsButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        detailsButton.setPreferredSize(new Dimension(160, 42));
        detailsButton.setMaximumSize(new Dimension(160, 42));
        detailsButton.setBorder(BorderFactory.createEmptyBorder());
        detailsButton.setOpaque(true);
        detailsButton.setContentAreaFilled(true);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(detailsButton);

        guestInfoTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        phoneLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        reservationTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        datesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        roomsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(guestInfoTitle);
        card.add(Box.createVerticalStrut(8));
        card.add(nameLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(emailLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(phoneLabel);
        card.add(Box.createVerticalStrut(28));
        card.add(reservationTitle);
        card.add(Box.createVerticalStrut(8));
        card.add(datesLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(roomsLabel);
        card.add(Box.createVerticalStrut(18));
        card.add(buttonPanel);

        return card;
    }

    // =========================
    // TEST MAIN
    // =========================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Reservation Manager");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(860, 860);
            frame.setLocationRelativeTo(null);

            CardLayout cardLayout = new CardLayout();
            JPanel mainPanel = new JPanel(cardLayout);

            ClerkHomePage page = null;
            try {
                page = new ClerkHomePage(cardLayout, mainPanel);
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
