package com.GoldenOpportunity;

import com.GoldenOpportunity.Roles.Guest;
import com.GoldenOpportunity.dbLogin.DbUser;
import com.GoldenOpportunity.dbLogin.UserDao;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ProfilePage extends JPanel {

    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private UIState uiState;

    private JPanel namePanel;
    private JPanel emailPanel;
    private JPanel phonePanel;
    private JPanel usernamePanel;
    private JPanel passwordPanel;
    private JButton editProfileBtn;
    private JPanel fieldsPanel;
    private JPanel profilePanel;
    private JPanel reservationPanel;

    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField usernameField;
    private JTextField passwordField;

    private DbUser dbUser;
    private UserDao userDao;

    public ProfilePage(CardLayout cardLayout, JPanel mainPanel, UIState uiState) throws IOException {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.uiState = uiState;
        userDao = new UserDao();

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        add(createHeader(), BorderLayout.NORTH);
        add(createScrollableContent(), BorderLayout.CENTER);
    }

    // ================= HEADER =================
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
            if(!uiState.isLoggedIn){
                cardLayout.show(mainPanel,"LOGIN");
            }
            else{
                cardLayout.show(mainPanel,"PROFILE");
                mainPanel.revalidate();
                mainPanel.repaint();
            }
        });

        header.add(logoLabel, BorderLayout.WEST);
        header.add(nav, BorderLayout.EAST);
        return header;
    }

    // ================= SCROLLABLE CONTENT =================
    private JScrollPane createScrollableContent() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(new Color(245, 245, 245));
        outer.setBorder(new EmptyBorder(14, 14, 14, 14));

        JPanel content = new JPanel(new GridLayout(1, 2, 14, 0));
        content.setBackground(new Color(245, 245, 245));

        profilePanel = createProfilePanel();
        reservationPanel = createReservationsPanel();

        content.add(profilePanel);
        content.add(reservationPanel);

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

        fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setOpaque(false);

        //TODO: need to access guest information for this
        /*
        String[] data = uiState.getCurrentSession().getUserId().split(",");

        String name = data[0];
        String email = data[1];
        String phone = data[2];

        namePanel = createLabelField("Name", name);
        emailPanel = createLabelField("Email", email);
        phonePanel = createLabelField("Phone Number", phone);
        usernamePanel = createLabelField("Username", username);
        passwordPanel = createLabelField("Password", password);
*/
        nameField = createTextField("x");
        emailField = createTextField("x");
        phoneField = createTextField("x");
        usernameField = createTextField("x");
        passwordField = createTextField("x");

        namePanel = createLabelPanel("Name",createLabelField(nameField.getText()));
        emailPanel = createLabelPanel("Email", createLabelField(emailField.getText()));
        phonePanel = createLabelPanel("Phone Number", createLabelField(phoneField.getText()));
        usernamePanel = createLabelPanel("Username", createLabelField(usernameField.getText()));
        passwordPanel = createLabelPanel("Password", createLabelField(passwordField.getText()));

        addFieldsToPanel();

        panel.add(fieldsPanel);

        editProfileBtn = createBlackButton("Edit Profile", 150, 50);
        panel.add(editProfileBtn);
        panel.add(Box.createVerticalStrut(28));

        editProfileBtn.addActionListener(e -> updateProfileToEdit());

        panel.add(Box.createVerticalStrut(30));

        JButton logoutBtn = createRedButton("Logout", 150, 50);
        logoutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to logout?",
                    "Logout",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                handleSignOut();
                cardLayout.show(mainPanel, "LOGIN");
            }
        });

        panel.add(logoutBtn);

        return panel;
    }

    private JPanel createTextPanel(String labelText, JTextField textField) {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setOpaque(false);
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 15));
        label.setForeground(new Color(45, 55, 70));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        wrapper.add(label);
        wrapper.add(Box.createVerticalStrut(4));
        wrapper.add(textField);

        return wrapper;
    }

    private JPanel createLabelPanel(String labelText, JLabel labelField) {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setOpaque(false);
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 15));
        label.setForeground(new Color(45, 55, 70));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        wrapper.add(label);
        wrapper.add(Box.createVerticalStrut(4));
        wrapper.add(labelField);

        return wrapper;
    }

    private JLabel createLabelField(String value){
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        valueLabel.setForeground(Color.BLACK);
        valueLabel.setOpaque(true);
        valueLabel.setBackground(Color.WHITE);
        valueLabel.setBorder(new CompoundBorder(
                new LineBorder(new Color(190, 200, 210), 2, true),
                new EmptyBorder(10, 10, 10, 10)
        ));
        valueLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        valueLabel.setPreferredSize(new Dimension(300, 48));
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        return valueLabel;
    }

    private JTextField createTextField(String value){
        JTextField valueLabel = new JTextField(value);
        valueLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        valueLabel.setForeground(Color.BLACK);
        valueLabel.setOpaque(true);
        valueLabel.setBackground(Color.WHITE);
        valueLabel.setBorder(new CompoundBorder(
                new LineBorder(new Color(190, 200, 210), 2, true),
                new EmptyBorder(10, 10, 10, 10)
        ));
        valueLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        valueLabel.setPreferredSize(new Dimension(300, 48));
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        return  valueLabel;
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
/*
    private JPanel createReservationCard(Guest guest, Reservation reservation) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(new Color(243, 243, 243));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(190, 200, 210), 2),
                new EmptyBorder(10, 12, 10, 12)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        // LEFT SIDE (INFO)
        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        JLabel reservationLabel = new JLabel("Reservation:");
        reservationLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        reservationLabel.setForeground(new Color(45, 55, 70));

        JLabel datesLabel = new JLabel(reservation.getDateRange().toString());
        datesLabel.setFont(new Font("SansSerif", Font.PLAIN, 17));

        JLabel roomsLabel = new JLabel("Rooms: " + reservation.getRooms());
        roomsLabel.setFont(new Font("SansSerif", Font.PLAIN, 17));

        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(reservationLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(datesLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(roomsLabel);

        // RIGHT SIDE (BUTTON - CENTERED)
        JPanel buttonWrapper = new JPanel(new GridBagLayout()); // this centers vertically
        buttonWrapper.setOpaque(false);

        JButton modifyBtn = createBlackButton("Modify Reservation", 225, 50);
        buttonWrapper.add(modifyBtn); // GridBagLayout centers it automatically

        modifyBtn.addActionListener(e -> {
            try {
                mainPanel.add(new ModifyReservationPage(this,cardLayout, mainPanel,guest,reservation,uiState), "GUEST_MODIFY_RESERVE");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            mainPanel.revalidate();
            mainPanel.repaint();

            cardLayout.show(mainPanel,"GUEST_MODIFY_RESERVE");
        });

        // ADD TO CARD
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(buttonWrapper, BorderLayout.EAST);

        return card;
    }
*/
private JPanel createReservationCard(String dates, String rooms) {
    JPanel card = new JPanel(new BorderLayout(10, 10));
    card.setBackground(new Color(243, 243, 243));
    card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(190, 200, 210), 2),
            new EmptyBorder(10, 12, 10, 12)
    ));
    card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

    // LEFT SIDE (INFO)
    JPanel infoPanel = new JPanel();
    infoPanel.setOpaque(false);
    infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

    JLabel reservationLabel = new JLabel("Reservation:");
    reservationLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
    reservationLabel.setForeground(new Color(45, 55, 70));

    JLabel datesLabel = new JLabel(dates);
    datesLabel.setFont(new Font("SansSerif", Font.PLAIN, 17));

    JLabel roomsLabel = new JLabel("Rooms: " + rooms);
    roomsLabel.setFont(new Font("SansSerif", Font.PLAIN, 17));

    infoPanel.add(Box.createVerticalStrut(10));
    infoPanel.add(reservationLabel);
    infoPanel.add(Box.createVerticalStrut(8));
    infoPanel.add(datesLabel);
    infoPanel.add(Box.createVerticalStrut(8));
    infoPanel.add(roomsLabel);

    // RIGHT SIDE (BUTTON - CENTERED)
    JPanel buttonWrapper = new JPanel(new GridBagLayout()); // this centers vertically
    buttonWrapper.setOpaque(false);

    JButton modifyBtn = createBlackButton("Modify Reservation", 225, 50);
    buttonWrapper.add(modifyBtn); // GridBagLayout centers it automatically

    modifyBtn.addActionListener(e -> {
        //mainPanel.add(new ModifyReservationPage(this,cardLayout, mainPanel,guest,reservation,uiState), "GUEST_MODIFY_RESERVE");

        mainPanel.revalidate();
        mainPanel.repaint();

        cardLayout.show(mainPanel,"GUEST_MODIFY_RESERVE");
    });

    // ADD TO CARD
    card.add(infoPanel, BorderLayout.CENTER);
    card.add(buttonWrapper, BorderLayout.EAST);

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

    private void addFieldsToPanel() {
        fieldsPanel.add(namePanel);
        fieldsPanel.add(Box.createVerticalStrut(8));
        fieldsPanel.add(emailPanel);
        fieldsPanel.add(Box.createVerticalStrut(8));
        fieldsPanel.add(phonePanel);
        fieldsPanel.add(Box.createVerticalStrut(8));
        fieldsPanel.add(usernamePanel);
        fieldsPanel.add(Box.createVerticalStrut(8));
        fieldsPanel.add(passwordPanel);
        fieldsPanel.add(Box.createVerticalStrut(28));
    }

    private void handleEdit(){
        int id = dbUser.id;

        try{
            userDao.updateFullName(id,nameField.getText().toString());
            userDao.updatePassword(id,passwordField.getText().toString());
            userDao.updateUsername(id,usernameField.getText().toString());
            userDao.updatePhoneNumber(id,phoneField.getText().toString());
            userDao.updateEmail(id,emailField.getText().toString());
            throw new SQLException("Error with changing profile data");
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    private void updateProfileToEdit(){
        fieldsPanel.removeAll();

        if(editProfileBtn.getText().equals("Edit Profile")){
            passwordField.setText("");
            namePanel = createTextPanel("Name", nameField);
            emailPanel = createTextPanel("Email", emailField);
            phonePanel = createTextPanel("Phone Number", phoneField);
            usernamePanel = createTextPanel("Username", usernameField);
            passwordPanel = createTextPanel("Password", passwordField);

            editProfileBtn.setText("Save Profile");
        }
        else{
            handleEdit();

            namePanel = createLabelPanel("Name",createLabelField(nameField.getText()));
            emailPanel = createLabelPanel("Email", createLabelField(emailField.getText()));
            phonePanel = createLabelPanel("Phone Number", createLabelField(phoneField.getText()));
            usernamePanel = createLabelPanel("Username", createLabelField(usernameField.getText()));
            passwordPanel = createLabelPanel("Password", createLabelField("**********"));

            editProfileBtn.setText("Edit Profile");
        }

        addFieldsToPanel();

        fieldsPanel.revalidate();
        fieldsPanel.repaint();

        profilePanel.revalidate();
        profilePanel.repaint();
    }

    private void updateReservationPanel(){
        reservationPanel.removeAll();
        reservationPanel = createReservationsPanel();
        reservationPanel.revalidate();
        reservationPanel.repaint();
    }

    public void updatePage(){
        updateProfileToEdit();
        updateReservationPanel();
    }

    private void handleSignOut() {
        uiState.setLoggedIn(false);
        cardLayout.show(mainPanel, "HOME");
    }

    public void updateProfilePage() throws SQLException {
        dbUser = userDao.findById(uiState.getCurrentSession().getUserId());

        nameField = createTextField(dbUser.fullName);
        emailField = createTextField(dbUser.contactInfo);
        phoneField = createTextField(dbUser.phoneNumber);
        usernameField = createTextField(dbUser.username);
        passwordField = createTextField("**********");

        namePanel = createLabelPanel("Name",createLabelField(nameField.getText()));
        emailPanel = createLabelPanel("Email", createLabelField(emailField.getText()));
        phonePanel = createLabelPanel("Phone Number", createLabelField(phoneField.getText()));
        usernamePanel = createLabelPanel("Username", createLabelField(usernameField.getText()));
        passwordPanel = createLabelPanel("Password", createLabelField(passwordField.getText()));

        fieldsPanel.removeAll();

        addFieldsToPanel();

        fieldsPanel.revalidate();
        fieldsPanel.repaint();

        profilePanel.revalidate();
        profilePanel.repaint();
    }
}
