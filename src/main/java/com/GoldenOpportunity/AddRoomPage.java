package com.GoldenOpportunity;

import com.GoldenOpportunity.Roles.RolePermissions;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class AddRoomPage extends JPanel {

    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final UIState uiState;

    public AddRoomPage(CardLayout cardLayout, JPanel mainPanel, UIState uiState) throws IOException {
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
        JPanel centerArea = new JPanel(new BorderLayout());
        centerArea.setOpaque(false);

        centerArea.add(new EditRoomPanel("Add New Room",uiState));

        return centerArea;
    }

    // =========================
    // TEST MAIN
    // =========================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Modify Rooms");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1220, 800);
            frame.setLocationRelativeTo(null);

            CardLayout cardLayout = new CardLayout();
            JPanel mainPanel = new JPanel(cardLayout);

            AddRoomPage addRoomPage = null;
            try {
                addRoomPage = new AddRoomPage(cardLayout, mainPanel, new UIState());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            mainPanel.add(addRoomPage, "MODIFY_ROOMS");

            frame.setContentPane(mainPanel);
            cardLayout.show(mainPanel, "MODIFY_ROOMS");

            frame.setVisible(true);
        });
    }
}
