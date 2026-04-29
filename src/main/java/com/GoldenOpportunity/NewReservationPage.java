package com.GoldenOpportunity;

import com.GoldenOpportunity.Roles.RolePermissions;
import com.github.lgooddatepicker.components.DatePicker;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewReservationPage extends JPanel{

    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final UIState uiState;

    private SearchBarPanel searchPanel;
    private JPanel roomListPanel;
    private JScrollPane scrollPane;

    public NewReservationPage(CardLayout cardLayout, JPanel mainPanel, UIState uiState) throws IOException {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.uiState = uiState;

        setLayout(new BorderLayout());
        setBackground(new Color(243, 246, 249));

        add(createHeader(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);
    }

    @Override
    public void setVisible(boolean aFlag) {
        if (aFlag && !RolePermissions.requireRole(this, uiState, "Managing rooms", "HOME", cardLayout, mainPanel, com.GoldenOpportunity.Login.enums.Role.CLERK)) {
            super.setVisible(false);
            return;
        }
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
    // MAIN CONTENT
    // =========================
    private JPanel createMainContent() {
        JPanel content = new JPanel(new BorderLayout(20, 0));
        content.setBackground(new Color(243, 246, 249));
        content.setBorder(new EmptyBorder(12, 15, 18, 15));

        content.add(createSidebar(), BorderLayout.WEST);
        content.add(createCenterArea(), BorderLayout.CENTER);

        return content;
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
        JButton finishCheckout = createSidebarButton("Finish Checkout");

        addRoomsButton.addActionListener(e -> {
            cardLayout.show(mainPanel,"ADD_ROOMS");
        });
        modifyRoomsButton.addActionListener(e -> {
            cardLayout.show(mainPanel,"MODIFY_ROOMS");
        });
        newReservation.addActionListener(e -> {
            cardLayout.show(mainPanel,"NEW_RESERVATION");
        });
        finishCheckout.addActionListener(e -> {
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

        sidebar.add(Box.createVerticalStrut(14));
        sidebar.add(addRoomsButton);
        sidebar.add(Box.createVerticalStrut(14));
        sidebar.add(modifyRoomsButton);
        sidebar.add(Box.createVerticalStrut(14));
        sidebar.add(newReservation);
        sidebar.add(Box.createVerticalStrut(14));
        sidebar.add(finishCheckout);
        sidebar.add(Box.createVerticalGlue());

        return sidebar;
    }

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setPreferredSize(new Dimension(135, 40));
        button.setMaximumSize(new Dimension(135, 40));
        button.setMinimumSize(new Dimension(135, 40));
        button.setFocusPainted(false);
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBorder(new LineBorder(new Color(150, 150, 150), 1, true));
        button.setOpaque(true);
        button.setContentAreaFilled(true);

        return button;
    }

    // =========================
    // CENTER AREA
    // =========================
    private JPanel createCenterArea() {
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
}
