package com.GoldenOpportunity;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.List;

import com.github.lgooddatepicker.components.DatePicker;

// Changed to JPanel instead of JFrame

public class HotelHomePageUI extends JPanel {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private UIState uiState;
    public HotelBookingUI hotelBookingUI;

    private SearchBarPanel searchPanel;
    private JPanel mainContentPanel;

    public HotelHomePageUI(CardLayout cardLayout, JPanel mainPanel, UIState uiState) throws IOException {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.uiState = uiState;
        uiState.reservationService = new ReservationService();
        uiState.roomService = new RoomService();
        uiState.searchController = new SearchController(uiState.roomService,uiState.reservationService);
        this.hotelBookingUI = new HotelBookingUI(this,cardLayout,mainPanel,uiState);

        setLayout(new BorderLayout(10, 10));

        add(createHeader(), BorderLayout.NORTH);

        mainContentPanel = createMainContent();

        JScrollPane scrollPane = new JScrollPane(mainContentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        SwingUtilities.invokeLater(() -> {
            scrollPane.getVerticalScrollBar().setValue(0);
        });

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

        uiState.registerLoginButton(buttonMap.get("Login"));

        buttonMap.get("Home").addActionListener(e -> {
            searchPanel.clearSearchPanel();
            cardLayout.show(mainPanel,"HOME");
        });
        buttonMap.get("Rooms").addActionListener(e -> {
            searchPanel.saveToUIState();
            hotelBookingUI.loadSearchFromUIState();
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

    private JPanel createMainContent() throws IOException {
        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBorder(new EmptyBorder(10, 20, 10, 20));
        main.setBackground(new Color(245, 245, 245));

        searchPanel = new SearchBarPanel(uiState);

        searchPanel.addSearchListener(e -> {
            DatePicker startDatePicker = searchPanel.getStartDatePicker();
            DatePicker endDatePicker = searchPanel.getEndDatePicker();
            if (startDatePicker.getDate() == null || endDatePicker.getDate() == null ||
                    Period.between(startDatePicker.getDate(),endDatePicker.getDate()).getDays() < 1 ||
                    startDatePicker.getDate().isBefore(LocalDate.now())) {
                JOptionPane.showMessageDialog(null, "Please select valid dates");
                return;
            }
            searchPanel.saveToUIState();
            hotelBookingUI.loadSearchFromUIState();
            try {
                hotelBookingUI.updateRooms(uiState.searchController.searchAvailableRooms(searchPanel.search()));
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            cardLayout.show(mainPanel,"ROOMS");

            mainPanel.revalidate();
            mainPanel.repaint();
        });

        main.add(createHeroSection());
        main.add(Box.createVerticalStrut(15));
        main.add(searchPanel);
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

    private JPanel createFeaturedRooms() throws IOException {
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        wrapper.setBackground(new Color(245, 245, 245));

        JPanel rooms = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        rooms.setBackground(new Color(245, 245, 245));

        rooms.add(createRoomCard(uiState.roomService.getAllRooms().get(4)));
        rooms.add(createRoomCard(uiState.roomService.getAllRooms().get(6)));
        rooms.add(createRoomCard(uiState.roomService.getAllRooms().get(8)));

        wrapper.add(rooms);
        return wrapper;
    }

    private JPanel createRoomCard(Room cardRoom) throws IOException {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        card.setPreferredSize(new Dimension(280, 280));
        card.setBackground(Color.WHITE);

        Image roomImage = ImageIO.read(new File(cardRoom.getImage()));
        Image scaledRoom = roomImage.getScaledInstance(260, 120, Image.SCALE_SMOOTH);

        JLabel image = new JLabel(new ImageIcon(scaledRoom));
        image.setPreferredSize(new Dimension(260, 120));

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBorder(new EmptyBorder(10, 10, 10, 10));
        info.setBackground(Color.WHITE);

        info.add(new JLabel(cardRoom.getRoomType() + " Room"));
        info.add(Box.createVerticalStrut(8));
        String desc = "";
        for(Map.Entry<String,Integer> entry : cardRoom.getBedTypes().entrySet()){
            desc = desc.concat(entry.getValue() + " ");
            desc = desc.concat(entry.getKey() + ", ");
        }
        String finalDesc = desc.substring(0,desc.length()-2);
        info.add(new JLabel(finalDesc));
        info.add(Box.createVerticalStrut(10));
        info.add(new JLabel("Price: $" + String.format("%.2f",cardRoom.getRate()) + " / night"));
        info.add(Box.createVerticalStrut(10));

        JButton details = new JButton("Select / Book");
        details.setBackground(new Color(30, 170, 70));
        details.setForeground(Color.WHITE);
        details.setFocusPainted(false);
        details.setOpaque(true);
        details.setBorderPainted(false);
        details.setContentAreaFilled(true);
        info.add(details);

        details.addActionListener(e -> {
            DatePicker startDatePicker = searchPanel.getStartDatePicker();
            DatePicker endDatePicker = searchPanel.getEndDatePicker();
            try {
                // Check if user selected dates
                if (startDatePicker.getDate() == null || endDatePicker.getDate() == null ||
                        Period.between(startDatePicker.getDate(),endDatePicker.getDate()).getDays() < 1) {
                    JOptionPane.showMessageDialog(null, "Please select valid dates");
                    return;
                }

                if(uiState.containsRoom(cardRoom.getRoomNo())){
                    JOptionPane.showMessageDialog(null, "Room already added");
                    return;
                }

                uiState.startDate = startDatePicker.getDate();
                uiState.endDate = endDatePicker.getDate();
                uiState.numGuests = 1;
                uiState.room = cardRoom;

                mainPanel.add(new RoomDetailsPage(cardLayout, mainPanel,uiState), "DETAILS");

                mainPanel.revalidate();
                mainPanel.repaint();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error processing booking");
            }
            cardLayout.show(mainPanel,"DETAILS");
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

    public void loadSearchFromUIState() {
        searchPanel.loadFromUIState();
    }
}
