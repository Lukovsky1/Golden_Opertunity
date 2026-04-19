package com.GoldenOpportunity;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class ProfilePage extends JPanel {

    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    public ProfilePage(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        add(createHeader(), BorderLayout.NORTH);
        add(createScrollableContent(), BorderLayout.CENTER);
    }

    // ================= HEADER =================
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new LineBorder(new Color(190, 200, 210), 1));

        // Left logo area
        JPanel logoPanel = new JPanel(new GridBagLayout());
        logoPanel.setPreferredSize(new Dimension(190, 160));
        logoPanel.setBackground(new Color(210, 216, 223));

        JLabel logoPlaceholder = new JLabel("\uD83D\uDDBC");
        logoPlaceholder.setFont(new Font("SansSerif", Font.PLAIN, 40));
        logoPlaceholder.setForeground(new Color(130, 145, 160));
        logoPanel.add(logoPlaceholder);

        // Right nav area
        JPanel navWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 55));
        navWrapper.setOpaque(false);
        navWrapper.setBorder(new EmptyBorder(0, 20, 0, 20));

        navWrapper.add(createNavButton("Home"));
        navWrapper.add(createNavButton("Rooms"));
        navWrapper.add(createNavButton("Shop"));
        navWrapper.add(createNavButton("Login"));
        navWrapper.add(createNavButton("🛒"));
        navWrapper.add(createNavButton("👤"));

        header.add(logoPanel, BorderLayout.WEST);
        header.add(navWrapper, BorderLayout.CENTER);

        return header;
    }

    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(
                text.length() <= 2 ? 50 : 95, 48
        ));
        button.setFocusPainted(false);
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setBorder(new LineBorder(new Color(170, 170, 170), 1, true));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        return button;
    }

    // ================= SCROLLABLE CONTENT =================
    private JScrollPane createScrollableContent() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(new Color(245, 245, 245));
        outer.setBorder(new EmptyBorder(14, 14, 14, 14));

        JPanel content = new JPanel(new GridLayout(1, 2, 14, 0));
        content.setBackground(new Color(245, 245, 245));

        content.add(createProfilePanel());
        content.add(createReservationsPanel());

        outer.add(content, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(outer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        return scrollPane;
    }

    // ================= LEFT PANEL =================
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(243, 243, 243));
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(190, 200, 210), 2),
                new EmptyBorder(14, 14, 14, 14)
        ));

        panel.add(createField("Name", "XXXXXXX"));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createField("Email", "XXXXXXX"));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createField("Phone Number", "XXXXXXX"));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createField("Username", "XXXXXXX"));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createField("Password", "**********"));
        panel.add(Box.createVerticalStrut(28));

        JButton editProfileBtn = createBlackButton("Edit Profile", 150, 50);
        panel.add(editProfileBtn);
        panel.add(Box.createVerticalStrut(28));

        JLabel paymentLabel = new JLabel("Payment Information");
        paymentLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        paymentLabel.setForeground(new Color(45, 55, 70));
        paymentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(paymentLabel);
        panel.add(Box.createVerticalStrut(6));

        JPanel paymentRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        paymentRow.setOpaque(false);
        paymentRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField paymentField = new JTextField("****XXXX");
        paymentField.setPreferredSize(new Dimension(200, 48));
        paymentField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        paymentField.setBorder(new LineBorder(new Color(190, 200, 210), 2, true));

        JButton editPaymentBtn = createBlackButton("Edit Payment Info", 210, 48);

        paymentRow.add(paymentField);
        paymentRow.add(editPaymentBtn);

        panel.add(paymentRow);

        return panel;
    }

    private JPanel createField(String labelText, String value) {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setOpaque(false);
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 15));
        label.setForeground(new Color(45, 55, 70));

        JTextField field = new JTextField(value);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        field.setPreferredSize(new Dimension(300, 48));
        field.setFont(new Font("SansSerif", Font.PLAIN, 15));
        field.setBorder(new LineBorder(new Color(190, 200, 210), 2, true));

        wrapper.add(label);
        wrapper.add(Box.createVerticalStrut(4));
        wrapper.add(field);

        return wrapper;
    }

    // ================= RIGHT PANEL =================
    private JPanel createReservationsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(243, 243, 243));
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(190, 200, 210), 2),
                new EmptyBorder(14, 14, 14, 14)
        ));

        panel.add(createReservationCard("xx/xx/xxxx - xx/xx/xxxx", "102, 103, 104"));
        panel.add(Box.createVerticalStrut(14));
        panel.add(createReservationCard("xx/xx/xxxx - xx/xx/xxxx", "102, 103, 104"));
        panel.add(Box.createVerticalStrut(14));
        panel.add(createReservationCard("xx/xx/xxxx - xx/xx/xxxx", "102, 103, 104"));
        panel.add(Box.createVerticalStrut(14));

        // Extra cards so scrolling can be seen
        panel.add(createReservationCard("xx/xx/xxxx - xx/xx/xxxx", "201, 202"));
        panel.add(Box.createVerticalStrut(14));
        panel.add(createReservationCard("xx/xx/xxxx - xx/xx/xxxx", "305"));
        panel.add(Box.createVerticalStrut(14));
        panel.add(createReservationCard("xx/xx/xxxx - xx/xx/xxxx", "401, 402, 403"));

        return panel;
    }

    private JPanel createReservationCard(String dates, String rooms) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(new Color(243, 243, 243));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(190, 200, 210), 2),
                new EmptyBorder(10, 12, 10, 12)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        JLabel reservationLabel = new JLabel("Reservation:");
        reservationLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        reservationLabel.setForeground(new Color(45, 55, 70));

        JLabel datesLabel = new JLabel("Dates: " + dates);
        datesLabel.setFont(new Font("SansSerif", Font.PLAIN, 17));
        datesLabel.setForeground(new Color(45, 55, 70));

        JLabel roomsLabel = new JLabel("Rooms: " + rooms);
        roomsLabel.setFont(new Font("SansSerif", Font.PLAIN, 17));
        roomsLabel.setForeground(new Color(45, 55, 70));

        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(reservationLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(datesLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(roomsLabel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        JButton modifyBtn = createBlackButton("Modify Reservation", 225, 50);
        JButton cancelBtn = createRedButton("Cancel Reservation", 225, 50);

        modifyBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        cancelBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonPanel.add(modifyBtn);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(cancelBtn);

        card.add(infoPanel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.EAST);

        return card;
    }

    // ================= BUTTON HELPERS =================
    private JButton createBlackButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, height));
        button.setMaximumSize(new Dimension(width, height));
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.setForeground(Color.WHITE);
        button.setBackground(Color.BLACK);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        return button;
    }

    private JButton createRedButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, height));
        button.setMaximumSize(new Dimension(width, height));
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(220, 53, 69)); // RED
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        return button;
    }

    // ================= TEST MAIN =================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Profile Page");
            CardLayout cardLayout = new CardLayout();
            JPanel mainPanel = new JPanel(cardLayout);

            ProfilePage profilePage = new ProfilePage(cardLayout, mainPanel);
            mainPanel.add(profilePage, "PROFILE");

            frame.setContentPane(mainPanel);
            frame.setSize(1250, 820);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            cardLayout.show(mainPanel, "PROFILE");
        });
    }
}