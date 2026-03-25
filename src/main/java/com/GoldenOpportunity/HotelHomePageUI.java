package com.GoldenOpportunity;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class HotelHomePageUI extends JFrame {

    public HotelHomePageUI() {
        setTitle("Hotel Home Page");
        setSize(1200, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(createHeader(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(15, 20, 15, 20));
        header.setBackground(Color.WHITE);

        JLabel logo = new JLabel("Logo");
        logo.setFont(new Font("SansSerif", Font.BOLD, 24));

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        nav.setBackground(Color.WHITE);
        String[] items = {"Home", "Rooms", "Shop", "Login", "Sign Up"};

        for (String item : items) {
            JButton btn = new JButton(item);
            btn.setFocusPainted(false);
            btn.setBackground(Color.WHITE);
            btn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            btn.setPreferredSize(new Dimension(90, 35));
            nav.add(btn);
        }

        header.add(logo, BorderLayout.WEST);
        header.add(nav, BorderLayout.EAST);
        return header;
    }

    private JPanel createMainContent() {
        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBorder(new EmptyBorder(10, 20, 10, 20));
        main.setBackground(new Color(245, 245, 245));

        main.add(createHeroSection());
        main.add(Box.createVerticalStrut(15));
        main.add(createSearchPanel());
        main.add(Box.createVerticalStrut(20));
        main.add(sectionTitle("Featured Rooms"));
        main.add(createFeaturedRooms());
        main.add(Box.createVerticalStrut(20));
        main.add(sectionTitle("About Our Hotel"));
        main.add(createAboutSection());

        return main;
    }

    private JPanel createHeroSection() {
        JPanel hero = new JPanel();
        hero.setPreferredSize(new Dimension(1100, 220));
        hero.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
        hero.setBackground(new Color(210, 215, 220));
        hero.add(new JLabel("Image Placeholder"));
        return hero;
    }

    private JPanel createSearchPanel() {
        JPanel search = new JPanel(new GridBagLayout());
        search.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        search.setBackground(Color.WHITE);
        search.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        search.add(new JLabel("Check-In Date"), gbc);
        gbc.gridx = 1;
        search.add(new JLabel("Check-out Date"), gbc);
        gbc.gridx = 2;
        search.add(new JLabel("Number of Guests"), gbc);
        gbc.gridx = 3;
        search.add(new JLabel("Room Type"), gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        search.add(new JTextField("mm/dd/yyyy", 10), gbc);
        gbc.gridx = 1;
        search.add(new JTextField("mm/dd/yyyy", 10), gbc);
        gbc.gridx = 2;
        search.add(new JTextField("1", 10), gbc);
        gbc.gridx = 3;
        search.add(new JComboBox<>(new String[]{"Standard", "Deluxe", "Suite"}), gbc);

        gbc.gridx = 4;
        JButton searchBtn = new JButton("Search");
        searchBtn.setBackground(new Color(50, 100, 230));
        searchBtn.setForeground(Color.WHITE);
        search.add(searchBtn, gbc);

        return search;
    }

    private JPanel createFeaturedRooms() {
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        wrapper.setBackground(new Color(245, 245, 245));

        JPanel rooms = new JPanel(new GridLayout(1, 3, 15, 15));
        rooms.setPreferredSize(new Dimension(900, 300));
        rooms.setBackground(new Color(245, 245, 245));

        rooms.add(createRoomCard("Standard Room", "Comfortable room", "$120 / night"));
        rooms.add(createRoomCard("Deluxe Room", "Spacious deluxe room", "$180 / night"));
        rooms.add(createRoomCard("Suite", "Luxury suite", "$250 / night"));

        wrapper.add(rooms);
        return wrapper;
    }

    private JPanel createRoomCard(String name, String desc, String price) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        card.setPreferredSize(new Dimension(280, 280));
        card.setBackground(Color.WHITE);

        JPanel image = new JPanel();
        image.setPreferredSize(new Dimension(200, 120));
        image.setBackground(new Color(210, 215, 220));
        image.add(new JLabel("Image Placeholder"));

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBorder(new EmptyBorder(10, 10, 10, 10));
        info.setBackground(Color.WHITE);

        info.add(new JLabel(name));
        info.add(Box.createVerticalStrut(8));
        info.add(new JLabel(desc));
        info.add(Box.createVerticalStrut(10));
        info.add(new JLabel("Price: " + price));
        info.add(Box.createVerticalStrut(10));

        JButton details = new JButton("View Details");
        details.setBackground(new Color(30, 170, 70));
        details.setForeground(Color.WHITE);
        info.add(details);

        card.add(image, BorderLayout.NORTH);
        card.add(info, BorderLayout.CENTER);
        return card;
    }

    private JPanel createAboutSection() {
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapper.setBackground(new Color(245, 245, 245));

        JPanel about = new JPanel(new BorderLayout());
        about.setPreferredSize(new Dimension(900, 100));
        about.setBackground(Color.WHITE);
        about.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextArea text = new JTextArea("Welcome to our exquisite hotel, where luxury meets comfort. Located in the heart of the city, we offer a serene escape with unparalleled service and amenities.");
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setEditable(false);
        text.setBackground(Color.WHITE);

        about.add(text, BorderLayout.CENTER);
        wrapper.add(about);
        return wrapper;
    }

    private JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 18));
        label.setBorder(new EmptyBorder(10, 0, 10, 0));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel();
        footer.setBackground(Color.WHITE);
        footer.add(new JLabel("Contact Info: 123 Hotel St, City, Country | +123 456 7890 | info@goldenopportunity.com"));
        return footer;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HotelHomePageUI().setVisible(true));
    }
}
