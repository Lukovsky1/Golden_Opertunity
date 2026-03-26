package com.GoldenOpportunity;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import com.GoldenOpportunity.Roles.*;

public class HotelBookingUI extends JFrame {

    public HotelBookingUI() {
        setTitle("Hotel Booking UI");
        setSize(1200, 800);
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

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        nav.setBackground(Color.WHITE);
        String[] items = {"Home", "Rooms", "Shop", "Login", "Sign Up"};
        Map<String,JButton> buttonMap = new HashMap<>();

        for (String item : items) {
            buttonMap.put(item,new JButton(item));
            buttonMap.get(item).setFocusPainted(false);
            buttonMap.get(item).setBackground(Color.WHITE);
            buttonMap.get(item).setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            buttonMap.get(item).setPreferredSize(new Dimension(90, 35));
            nav.add(buttonMap.get(item));
        }

        header.add(logo, BorderLayout.WEST);
        header.add(nav, BorderLayout.EAST);

        return header;
    }

    private JPanel createMainContent() {
        JPanel main = new JPanel(new BorderLayout(15, 15));
        main.setBorder(new EmptyBorder(10, 20, 10, 20));

        main.add(createFilterPanel(), BorderLayout.WEST);
        main.add(createRoomGrid(), BorderLayout.CENTER);


        return main;
    }

    private JPanel createFilterPanel() {
        JPanel filter = new JPanel(new GridBagLayout());
        filter.setPreferredSize(new Dimension(170, 600));
        filter.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        filter.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.weightx = 1.0;

        gbc.gridy = 0;
        JLabel filtersHeader = sectionLabel("Filters");
        filtersHeader.setFont(new Font("SansSerif", Font.BOLD, 22));
        filter.add(filtersHeader, gbc);

        gbc.gridy++;
        filter.add(sectionLabel("Price Range"), gbc);

        gbc.gridy++;
        JPanel sliderLabels = new JPanel(new BorderLayout());
        sliderLabels.setOpaque(false);
        sliderLabels.add(new JLabel("$0"), BorderLayout.WEST);
        sliderLabels.add(new JLabel("$1000+"), BorderLayout.EAST);
        filter.add(sliderLabels, gbc);

        gbc.gridy++;
        JSlider slider = new JSlider(0, 1000, 500);
        slider.setOpaque(false);
        filter.add(slider, gbc);

        gbc.gridy++;
        filter.add(sectionLabel("Room Type"), gbc);

        gbc.gridy++;
        filter.add(new JCheckBox("Standard"), gbc);
        gbc.gridy++;
        filter.add(new JCheckBox("Deluxe"), gbc);
        gbc.gridy++;
        filter.add(new JCheckBox("Suite"), gbc);

        gbc.gridy++;
        filter.add(sectionLabel("Bed Type"), gbc);

        gbc.gridy++;
        filter.add(new JCheckBox("King"), gbc);
        gbc.gridy++;
        filter.add(new JCheckBox("Queen"), gbc);
        gbc.gridy++;
        filter.add(new JCheckBox("Twin"), gbc);

        gbc.gridy++;
        JButton apply = new JButton("Apply Filters");
        apply.setBackground(new Color(70, 80, 95));
        apply.setForeground(Color.WHITE);
        apply.setFocusPainted(false);
        filter.add(apply, gbc);

        return filter;
    }

    private JPanel createRoomGrid() {
        JPanel grid = new JPanel(new GridLayout(2, 3, 15, 15));
        grid.setBackground(new Color(245, 245, 245));

        grid.add(createRoomCard("Standard Room", "Comfortable room with queen bed", "$120 / night"));
        grid.add(createRoomCard("Deluxe Room", "Spacious room with balcony", "$180 / night"));
        grid.add(createRoomCard("Suite", "Luxury suite with living area", "$250 / night"));
        grid.add(createRoomCard("King Room", "King bed with city view", "$200 / night"));
        grid.add(createRoomCard("Twin Room", "Two beds for shared stay", "$150 / night"));

        grid.add(new JPanel());
        return grid;
    }

    private JPanel createRoomCard(String roomName, String description, String price) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        card.setBackground(Color.WHITE);

        JPanel image = new JPanel();
        image.setPreferredSize(new Dimension(250, 140));
        image.setBackground(new Color(210, 215, 220));
        image.add(new JLabel("Image Placeholder"));

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBorder(new EmptyBorder(10, 10, 10, 10));
        info.setBackground(Color.WHITE);

        info.add(new JLabel(roomName));
        info.add(Box.createVerticalStrut(8));
        info.add(new JLabel(description));
        info.add(Box.createVerticalStrut(10));
        info.add(new JLabel("Price: " + price));
        info.add(Box.createVerticalStrut(10));

        JButton book = new JButton("Select / Book");
        book.setBackground(new Color(30, 170, 70));
        book.setForeground(Color.WHITE);
        book.setFocusPainted(false);
        info.add(book);

        card.add(image, BorderLayout.NORTH);
        card.add(info, BorderLayout.CENTER);

        return card;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel();
        footer.setBorder(new EmptyBorder(10, 10, 10, 10));
        footer.setBackground(Color.WHITE);
        footer.add(new JLabel("Contact Info: 123 Hotel St, City, Country | +123 456 7890 | info@goldenopportunity.com"));
        return footer;
    }

    private JLabel sectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 18));
        label.setBorder(new EmptyBorder(5, 10, 5, 10));
        return label;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HotelBookingUI().setVisible(true));
    }
}
