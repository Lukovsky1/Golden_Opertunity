package com.GoldenOpportunity;

import com.GoldenOpportunity.Roles.Guest;
import com.GoldenOpportunity.Roles.RolePermissions;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class ClerkHomePage extends JPanel {

    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final UIState uiState;

    private JScrollPane scrollPane;

    public ClerkHomePage(CardLayout cardLayout, JPanel mainPanel, UIState uiState) throws IOException {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.uiState = uiState;

        setLayout(new BorderLayout());
        setBackground(new Color(240, 243, 247));

        add(createHeader(), BorderLayout.NORTH);
        add(createBody(), BorderLayout.CENTER);
    }

    @Override
    public void setVisible(boolean aFlag) {
        if (aFlag && !RolePermissions.requireRole(this, uiState, "Viewing clerk tools", "HOME", cardLayout, mainPanel, com.GoldenOpportunity.Login.enums.Role.CLERK)) {
            super.setVisible(false);
            return;
        }

        updatePage();

        super.setVisible(aFlag);
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

    // =========================
    // RIGHT CONTENT
    // =========================
    private JPanel createContentSection() {
        JPanel contentSection = new JPanel(new BorderLayout(0, 18));
        contentSection.setOpaque(false);

        scrollPane = createScrollableCards();

        contentSection.add(createSearchBar(), BorderLayout.NORTH);
        contentSection.add(scrollPane, BorderLayout.CENTER);

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

        try{
            for(Reservation reservation : uiState.reservationService.getAllReservations()){
                cardsPanel.add(createReservationCard(reservation));
            }
        } catch (SQLException e){
            e.printStackTrace();
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
    private JPanel createReservationCard(Reservation reservation) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(190, 205, 218), 1),
                new EmptyBorder(12, 12, 12, 12)
        ));
        card.setPreferredSize(new Dimension(300, 260));

        Font textFont = new Font("SansSerif", Font.PLAIN, 15);
        Font titleFont = new Font("SansSerif", Font.BOLD, 16);

        JLabel nameLabel = new JLabel(reservation.getName());
        nameLabel.setFont(titleFont);
        nameLabel.setForeground(new Color(55, 70, 85));

        // ===== RESERVATION INFO =====
        JLabel reservationTitle = new JLabel("Reservation:");
        reservationTitle.setFont(titleFont);
        reservationTitle.setForeground(new Color(55, 70, 85));

        JLabel datesLabel = new JLabel("Dates: " + reservation.getDateRange().toString());
        datesLabel.setFont(textFont);
        datesLabel.setForeground(new Color(55, 70, 85));

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        statusPanel.setOpaque(false);

        JLabel statusLabel = new JLabel("Status: ");
        statusLabel.setFont(textFont);
        statusLabel.setForeground(new Color(55, 70, 85));

        JLabel statusValue = new JLabel(
                reservation.isCheckedIn() ? "Checked-In" : "Not Checked-In"
        );
        statusValue.setFont(textFont);

        if (reservation.isCheckedIn()) {
            statusValue.setForeground(new Color(30, 130, 114)); // green
        } else {
            statusValue.setForeground(new Color(214, 65, 88)); // red
        }

        statusPanel.add(statusLabel);
        statusPanel.add(statusValue);

        JButton detailsButton = new JButton("Details");
        detailsButton.setFocusPainted(false);
        detailsButton.setForeground(Color.WHITE);
        detailsButton.setBackground(Color.BLACK);
        detailsButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        detailsButton.setPreferredSize(new Dimension(180, 42));
        detailsButton.setMaximumSize(new Dimension(180, 42));
        detailsButton.setBorder(BorderFactory.createEmptyBorder());
        detailsButton.setOpaque(true);
        detailsButton.setContentAreaFilled(true);

        detailsButton.addActionListener(e -> {
            try {
                mainPanel.add(new ModifyReservationPage(this, cardLayout, mainPanel, reservation, uiState),
                        "CLERK_MODIFY_RESERVE");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            mainPanel.revalidate();
            mainPanel.repaint();
            cardLayout.show(mainPanel, "CLERK_MODIFY_RESERVE");
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(detailsButton);

        // ===== ALIGN LEFT =====
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        reservationTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        datesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ===== BUILD CARD =====
        card.add(nameLabel);
        card.add(Box.createVerticalStrut(16));

        card.add(reservationTitle);
        card.add(Box.createVerticalStrut(8));
        card.add(datesLabel);
        card.add(Box.createVerticalStrut(5));

        for(Room room : reservation.getRooms()){
            JLabel roomsLabel = new JLabel("Rooms: " + room);
            roomsLabel.setFont(textFont);
            roomsLabel.setForeground(new Color(55, 70, 85));
            roomsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            card.add(roomsLabel);
            card.add(Box.createVerticalStrut(5));
        }

        statusPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(statusPanel);
        card.add(Box.createVerticalStrut(18));
        card.add(buttonPanel);

        return card;
    }

    private void updateReservationPanel() {
        JPanel cardsPanel = new JPanel(new GridLayout(0, 2, 18, 18));
        cardsPanel.setOpaque(false);
        cardsPanel.setBorder(new EmptyBorder(2, 2, 2, 2));

        try {
            for (Reservation reservation : uiState.reservationService.getAllReservations()) {
                cardsPanel.add(createReservationCard(reservation));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        scrollPane.setViewportView(cardsPanel);

        scrollPane.revalidate();
        scrollPane.repaint();
    }

    public void updatePage(){
        updateReservationPanel();
    }
}
