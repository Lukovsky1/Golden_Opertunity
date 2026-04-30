package com.GoldenOpportunity;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.List;

import com.GoldenOpportunity.Roles.*;
import com.github.lgooddatepicker.components.DatePicker;

// Changed to JPanel instead of JFrame

public class HotelBookingUI extends JPanel {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private UIState uiState;
    private HotelHomePageUI hotelHomePageUI;

    private SearchBarPanel searchPanel;
    private JPanel roomListPanel;
    private JScrollPane scrollPane;
    private JPanel mainContentPanel;

    public HotelBookingUI(HotelHomePageUI hotelHomePageUI,CardLayout cardLayout, JPanel mainPanel,UIState uiState) throws IOException {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.uiState = uiState;
        this.hotelHomePageUI = hotelHomePageUI;
        this.uiState.filteredRooms = uiState.roomService.getAllRooms();

        setLayout(new BorderLayout(10, 10));

        mainContentPanel = createMainContent();

        add(createHeader(), BorderLayout.NORTH);
        add(mainContentPanel, BorderLayout.CENTER);
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
            searchPanel.saveToUIState();
            hotelHomePageUI.loadSearchFromUIState();
            cardLayout.show(mainPanel,"HOME");
        });
        buttonMap.get("Rooms").addActionListener(e -> {
            updateRooms(uiState.roomService.getAllRooms());
            searchPanel.clearSearchPanel();
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

    private JPanel createRoomList(List<Room> rooms) {
        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBackground(new Color(245, 245, 245));

        for(Room room : rooms){
            list.add(createRoomCard(room));
            list.add(Box.createVerticalStrut(15));
        }

        return list;
    }

    private JPanel createMainContent() {
        JPanel main = new JPanel(new BorderLayout(15, 15));
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

            try {
                updateRooms(uiState.searchController.searchAvailableRooms(searchPanel.search()));
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        main.add(searchPanel, BorderLayout.NORTH);

        roomListPanel = createRoomList(uiState.roomService.getAllRooms());

        scrollPane = new JScrollPane(roomListPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);

        main.add(scrollPane, BorderLayout.CENTER);

        return main;
    }

    private JPanel createRoomCard(Room cardRoom) {
        JPanel card = new JPanel(new BorderLayout(15, 15));
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        card.setPreferredSize(new Dimension(1000, 180));

        JLabel image = new JLabel();

        Image roomImg = null;
        try {
            roomImg = ImageIO.read(new File(cardRoom.getImage()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Image scaled = roomImg.getScaledInstance(250, 160, Image.SCALE_SMOOTH);
        image.setIcon(new ImageIcon(scaled));
        image.setPreferredSize(new Dimension(250, 160));

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(Color.WHITE);
        info.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel nameLabel = new JLabel(cardRoom.getRoomType() + " Room");
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        JLabel floorLabel = new JLabel("Floor Number: " + cardRoom.getFloorNum());

        JLabel roomNumberLabel = new JLabel("Room Number: " + cardRoom.getRoomNo());

        String desc = "";
        for(Map.Entry<String,Integer> entry : cardRoom.getBedTypes().entrySet()){
            desc = desc.concat(entry.getValue() + " ");
            desc = desc.concat(entry.getKey() + ", ");
        }
        String finalDesc = desc.substring(0,desc.length()-2);
        JLabel descLabel = new JLabel(finalDesc);
        JLabel priceLabel = new JLabel("Price: " + String.format("%.2f",cardRoom.getRate()) + " / night");

        JButton book = new JButton("Select / Book");
        book.setBackground(new Color(30, 170, 70));
        book.setForeground(Color.WHITE);
        book.setFocusPainted(false);
        book.setOpaque(true);
        book.setBorderPainted(false);
        book.setContentAreaFilled(true);
        book.setAlignmentX(Component.LEFT_ALIGNMENT);

        book.addActionListener(e -> {
            try {
                // Check if user selected dates
                DatePicker startDatePicker = searchPanel.getStartDatePicker();
                DatePicker endDatePicker = searchPanel.getEndDatePicker();
                if (startDatePicker.getDate() == null || endDatePicker.getDate() == null ||
                        Period.between(startDatePicker.getDate(),endDatePicker.getDate()).getDays() < 1 ||
                        startDatePicker.getDate().isBefore(LocalDate.now())) {
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

        info.add(nameLabel);
        info.add(Box.createVerticalStrut(10));
        info.add(floorLabel);
        info.add(roomNumberLabel);
        info.add(descLabel);
        info.add(Box.createVerticalStrut(10));
        info.add(priceLabel);
        info.add(Box.createVerticalStrut(15));
        info.add(book);

        card.add(image, BorderLayout.WEST);
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

    public void updateRooms(List<Room> rooms) {
        roomListPanel = createRoomList(rooms);

        scrollPane.setViewportView(roomListPanel);

        roomListPanel.revalidate();
        roomListPanel.repaint();

        scrollPane.revalidate();
        scrollPane.repaint();

        SwingUtilities.invokeLater(() -> {
            scrollPane.getVerticalScrollBar().setValue(0);
        });
    }

    public void loadSearchFromUIState() {
        searchPanel.loadFromUIState();
    }
}
