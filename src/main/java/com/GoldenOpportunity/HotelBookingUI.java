package com.GoldenOpportunity;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.GoldenOpportunity.Roles.*;
import com.github.lgooddatepicker.components.DatePicker;

// Changed to JPanel instead of JFrame

public class HotelBookingUI extends JPanel {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private DatePicker startDate;
    private DatePicker endDate;
    private JSpinner numGuests;
    private UIState uiState;

    public HotelBookingUI(CardLayout cardLayout, JPanel mainPanel,UIState uiState) throws IOException {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.uiState = uiState;

        setLayout(new BorderLayout(10, 10));

        add(createHeader(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);
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

    private JPanel createSearchBar() {
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
        search.add(new JLabel("Search"), gbc);

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
        JTextField searchTextField = new JTextField();
        searchTextField.setPreferredSize(new Dimension(300,25));
        search.add(searchTextField, gbc);

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

    private JPanel createRoomList() {
        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBackground(new Color(245, 245, 245));

        List<String> photos = new ArrayList<>();
        photos.add("src/main/java/com/GoldenOpportunity/Images/Rooms/roomStandard.jpg");
        photos.add("src/main/java/com/GoldenOpportunity/Images/Rooms/roomDeluxe.png");
        photos.add("src/main/java/com/GoldenOpportunity/Images/Rooms/roomSuite.jpg");

        List<Room> allRooms = uiState.roomService.getAllRooms();

        for(Room room : allRooms){
            int randomNum = (int)(Math.random() * 3);
            list.add(createRoomCard(room,photos.get(randomNum)));
            list.add(Box.createVerticalStrut(15));
        }

        return list;
    }

    private JPanel createMainContent() {
        JPanel main = new JPanel(new BorderLayout(15, 15));
        main.setBorder(new EmptyBorder(10, 20, 10, 20));
        main.setBackground(new Color(245, 245, 245));

        main.add(createSearchBar(), BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(createRoomList());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);

        main.add(scrollPane, BorderLayout.CENTER);

        return main;
    }

    private JPanel createRoomCard(Room room, String imageFile) {
        JPanel card = new JPanel(new BorderLayout(15, 15));
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        card.setPreferredSize(new Dimension(1000, 180));

        JLabel image = new JLabel();

        Image roomImg = null;
        try {
            roomImg = ImageIO.read(new File(imageFile));
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

        JLabel nameLabel = new JLabel(room.getRoomType() + " Room");
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        String desc = "";
        for(Map.Entry<String,Integer> entry : room.getBedTypes().entrySet()){
            desc = desc.concat(entry.getValue() + " ");
            desc = desc.concat(entry.getKey() + ", ");
        }
        String finalDesc = desc.substring(0,desc.length()-2);
        JLabel descLabel = new JLabel(finalDesc);
        JLabel priceLabel = new JLabel("Price: " + String.format("%.2f",room.getRate()) + " / night");

        JButton book = new JButton("Select / Book");
        book.setBackground(new Color(30, 170, 70));
        book.setForeground(Color.WHITE);
        book.setFocusPainted(false);
        book.setOpaque(true);
        book.setBorderPainted(false);
        book.setContentAreaFilled(true);
        book.setAlignmentX(Component.LEFT_ALIGNMENT);

        book.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Check if user selected dates
                    if (startDate.getDate() == null || endDate.getDate() == null ||
                            Period.between(startDate.getDate(),endDate.getDate()).getDays() < 1) {
                        JOptionPane.showMessageDialog(null, "Please select valid dates");
                        return;
                    }

                    uiState.startDate = startDate.getDate();
                    uiState.endDate = endDate.getDate();
                    uiState.numGuests = (int) numGuests.getValue();
                    uiState.room = room;
                    uiState.imageFile = imageFile;

                    mainPanel.add(new RoomDetailsPage(cardLayout, mainPanel,uiState), "DETAILS");

                    mainPanel.revalidate();
                    mainPanel.repaint();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error processing booking");
                }
                cardLayout.show(mainPanel,"DETAILS");
            }
        });

        info.add(nameLabel);
        info.add(Box.createVerticalStrut(10));
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

    private JLabel sectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 18));
        label.setBorder(new EmptyBorder(5, 10, 5, 10));
        return label;
    }
}
