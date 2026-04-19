package com.GoldenOpportunity;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

import com.github.lgooddatepicker.components.DatePicker;

// Changed to JPanel instead of JFrame

public class HotelHomePageUI extends JPanel {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private DatePicker startDate;
    private DatePicker endDate;
    private JSpinner numGuests;

    private RoomService roomService;
    private ReservationService reservationService;

    public RoomService getRoomService(){return roomService;}
    public ReservationService getReservationService(){return reservationService;}

    public HotelHomePageUI(CardLayout cardLayout, JPanel mainPanel) throws IOException {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        reservationService = new ReservationService(Path.of("src/main/resources/testReservationData1.csv"));
        roomService = new RoomService("src/main/resources/testRoomData1.csv");

        setLayout(new BorderLayout(10, 10));

        add(createHeader(), BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(createMainContent());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);

        add(createFooter(), BorderLayout.SOUTH);
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
            cardLayout.show(mainPanel,"PROFILE");
        });

        header.add(logoLabel, BorderLayout.WEST);
        header.add(nav, BorderLayout.EAST);
        return header;
    }

    private JPanel createMainContent() throws IOException {
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
        JPanel hero = new JPanel(new BorderLayout());
        hero.setPreferredSize(new Dimension(900, 450));
        hero.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        try {
            Image heroImage = ImageIO.read(new File("src/main/java/com/GoldenOpportunity/Images/lobby.jpg"));
            Image scaledHero = heroImage.getScaledInstance(884, 360, Image.SCALE_SMOOTH);

            JLabel imageLabel = new JLabel(new ImageIcon(scaledHero));
            hero.add(imageLabel, BorderLayout.CENTER);

        } catch (IOException | IllegalArgumentException e) {
            JLabel fallback = new JLabel("Image not found", SwingConstants.CENTER);
            fallback.setOpaque(true);
            fallback.setBackground(new Color(210, 215, 220));
            hero.add(fallback, BorderLayout.CENTER);
        }

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
        startDate = new DatePicker(); // Calendar picker for check-in
        search.add(startDate, gbc);

        gbc.gridx = 1;
        endDate = new DatePicker(); // Calendar picker for check-out
        search.add(endDate, gbc);
        
        gbc.gridx = 2;
        numGuests = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        search.add(numGuests, gbc);
        gbc.gridx = 3;
        search.add(new JComboBox<>(new String[]{"Standard", "Deluxe", "Suite"}), gbc);

        gbc.gridx = 4;
        JButton searchBtn = new JButton("Search");
        searchBtn.setBackground(new Color(50, 100, 230));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.setOpaque(true);
        searchBtn.setBorderPainted(false);
        searchBtn.setContentAreaFilled(true);
        search.add(searchBtn, gbc);

        searchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        return search;
    }

    private JPanel createFeaturedRooms() throws IOException {
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        wrapper.setBackground(new Color(245, 245, 245));

        JPanel rooms = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        rooms.setBackground(new Color(245, 245, 245));

        rooms.add(createRoomCard(roomService.getRoomList().get(4),"src/main/java/com/GoldenOpportunity/Images/Rooms/roomStandard.jpg"));
        rooms.add(createRoomCard(roomService.getRoomList().get(6),"src/main/java/com/GoldenOpportunity/Images/Rooms/roomDeluxe.png"));
        rooms.add(createRoomCard(roomService.getRoomList().get(8),"src/main/java/com/GoldenOpportunity/Images/Rooms/roomSuite.jpg"));

        wrapper.add(rooms);
        return wrapper;
    }

    private JPanel createRoomCard(Room room,String imageFile) throws IOException {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        card.setPreferredSize(new Dimension(280, 280));
        card.setBackground(Color.WHITE);

        Image roomImage = ImageIO.read(new File(imageFile));
        Image scaledRoom = roomImage.getScaledInstance(260, 120, Image.SCALE_SMOOTH);

        JLabel image = new JLabel(new ImageIcon(scaledRoom));
        image.setPreferredSize(new Dimension(260, 120));

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBorder(new EmptyBorder(10, 10, 10, 10));
        info.setBackground(Color.WHITE);

        info.add(new JLabel(room.getRoomType() + "Room"));
        info.add(Box.createVerticalStrut(8));
        String desc = "";
        for(Map.Entry<String,Integer> entry : room.getBedTypes().entrySet()){
            desc = desc.concat(entry.getValue() + " ");
            desc = desc.concat(entry.getKey() + ", ");
        }
        String finalDesc = desc.substring(0,desc.length()-2);
        info.add(new JLabel(finalDesc));
        info.add(Box.createVerticalStrut(10));
        info.add(new JLabel("Price: $" + String.format("%.2f",room.getRate()) + " / night"));
        info.add(Box.createVerticalStrut(10));

        JButton details = new JButton("Select / Book");
        details.setBackground(new Color(30, 170, 70));
        details.setForeground(Color.WHITE);
        details.setFocusPainted(false);
        details.setOpaque(true);
        details.setBorderPainted(false);
        details.setContentAreaFilled(true);
        info.add(details);

        details.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Check if user selected dates
                    if (startDate.getDate() == null || endDate.getDate() == null ||
                            Period.between(startDate.getDate(),endDate.getDate()).getDays() < 1) {
                        JOptionPane.showMessageDialog(null, "Please select valid dates");
                        return;
                    }

                    mainPanel.add(new RoomDetailsPage(
                            cardLayout,
                            mainPanel,
                            startDate.getDate(),
                            endDate.getDate(),
                            (int) numGuests.getValue(),
                            room,
                            imageFile,
                            reservationService
                    ), "DETAILS");

                    mainPanel.revalidate();
                    mainPanel.repaint();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error processing booking");
                }
                cardLayout.show(mainPanel,"DETAILS");
            }
        });

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
}
