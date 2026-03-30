package com.GoldenOpportunity;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * RoomDetailsPage represents the UI page where a guest can:
 * - View detailed information about a selected room
 * - Enter booking details (dates, guests)
 * - Proceed to reservation
 */

public class RoomDetailsPage extends JFrame {

    // Input fields for booking information
    private JTextField checkInField;
    private JTextField checkOutField;
    private JSpinner guestsSpinner;
    // Labels used to display booking summary
    private JLabel nightsValueLabel;
    private JLabel totalValueLabel;

    /**
    * Constructor: initializes the main window and layout
    */
    public RoomDetailsPage() {
        setTitle("Room Details");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top navigation bar
        add(createHeader(), BorderLayout.NORTH);

        // Scrollable main content (left: room info, right: booking)
        JScrollPane scrollPane = new JScrollPane(createMainContent());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);

        add(scrollPane, BorderLayout.CENTER);

        // Footer with contact info
        add(createFooter(), BorderLayout.SOUTH);
    }

    /**
     * Creates the header with logo and navigation buttons
     */
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(15, 20, 15, 20));
        header.setBackground(Color.WHITE);

        // Load and scale logo while preserving aspect ratio
        java.net.URL logoUrl = getClass().getResource("/com/GoldenOpportunity/logo.png");
        ImageIcon logoIcon = new ImageIcon(logoUrl);

        int originalWidth = logoIcon.getIconWidth();
        int originalHeight = logoIcon.getIconHeight();

        int newHeight = 70;
        int newWidth = (originalWidth * newHeight) / originalHeight;

        Image scaledLogo = logoIcon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
        logoLabel.setFont(new Font("SansSerif", Font.BOLD, 20));

        // Navigation buttons (simulate navigation for now)
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        navPanel.setOpaque(false);

        JButton homeButton = new JButton("Home");
        JButton roomsButton = new JButton("Rooms");
        JButton shopButton = new JButton("Shop");
        JButton loginButton = new JButton("Log In");

        // Temporary actions (to be replaced by real navigation later)
        homeButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Go to Home page"));
        roomsButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Go to Rooms page"));
        shopButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Go to Shop page"));
        loginButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Go to Login page"));

        navPanel.add(homeButton);
        navPanel.add(roomsButton);
        navPanel.add(shopButton);
        navPanel.add(loginButton);

        header.add(logoLabel, BorderLayout.WEST);
        header.add(navPanel, BorderLayout.EAST);

        return header;
    }

    /**
     * Creates the main layout with two sections:
     * - Left: room details
     * - Right: booking panel
     */
    private JPanel createMainContent() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(245, 245, 245));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 20);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;

        // Left panel: room information
        gbc.gridx = 0;
        gbc.weightx = 0.52;
        gbc.weighty = 1.0;
        mainPanel.add(createLeftPanel(), gbc);

        // Right panel: booking section
        gbc.gridx = 1;
        gbc.weightx = 0.48;
        gbc.insets = new Insets(0, 0, 0, 0);
        mainPanel.add(createRightPanel(), gbc);

        return mainPanel;
    }

    /**
     * Builds the left section displaying:
     * - Room image
     * - Description
     * - Amenities
     * - Price
     */
    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel pageTitle = new JLabel("Room Details");
        pageTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        pageTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Room image
        ImageIcon roomIcon = new ImageIcon(getClass().getResource("/com/GoldenOpportunity/room.png"));
        Image scaledRoom = roomIcon.getImage().getScaledInstance(500, 280, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(scaledRoom));
        imageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel roomTitle = new JLabel("Deluxe Suite with Ocean View");
        roomTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        roomTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Room description
        JTextArea description = new JTextArea(
            "Experience luxury in our spacious Deluxe Suite, offering breathtaking ocean views. "
          + "This elegantly designed room features a comfortable king-size bed, a private balcony, "
          + "and a modern en-suite bathroom. Enjoy premium amenities and personalized service during your stay."
        );
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setEditable(false);
        description.setOpaque(false);
        description.setFont(new Font("SansSerif", Font.PLAIN, 15));
        description.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel amenitiesTitle = new JLabel("Amenities:");
        amenitiesTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        amenitiesTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Amenities list
        JTextArea amenities = new JTextArea(
            "- King-size bed\n"
          + "- Private balcony\n"
          + "- Ocean view\n"
          + "- Air conditioning\n"
          + "- Flat-screen TV\n"
          + "- Free Wi-Fi\n"
          + "- Minibar\n"
          + "- Coffee maker\n"
          + "- Hairdryer"
        );
        amenities.setEditable(false);
        amenities.setOpaque(false);
        amenities.setFont(new Font("SansSerif", Font.PLAIN, 15));
        amenities.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Price display
        JLabel priceLabel = new JLabel("Price: $250 / night");
        priceLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        priceLabel.setForeground(new Color(0, 130, 0));
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add all components to layout
        leftPanel.add(pageTitle);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(imageLabel);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(roomTitle);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(description);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(amenitiesTitle);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(amenities);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(priceLabel);

        return leftPanel;
    }

    /**
     * Builds the booking panel where users:
     * - Select dates
     * - Choose number of guests
     * - View total cost
     */
    private JPanel createRightPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(245, 245, 245));

        JPanel bookingPanel = new JPanel();
        bookingPanel.setLayout(new BoxLayout(bookingPanel, BoxLayout.Y_AXIS));
        bookingPanel.setBackground(Color.WHITE);
        bookingPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel title = new JLabel("Book Your Stay");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));

        // Booking inputs
        JLabel checkInLabel = new JLabel("Check-in Date");
        checkInField = new JTextField("mm/dd/yyyy");

        JLabel checkOutLabel = new JLabel("Check-out Date");
        checkOutField = new JTextField("mm/dd/yyyy");

        JLabel guestsLabel = new JLabel("Number of Guests");
        guestsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));

        JSeparator separator = new JSeparator();

        // Cost summary section
        JLabel summaryTitle = new JLabel("Total Cost Summary");
        summaryTitle.setFont(new Font("SansSerif", Font.BOLD, 18));

        JPanel priceRow = new JPanel(new BorderLayout());
        priceRow.setOpaque(false);
        priceRow.add(new JLabel("Price per night:"), BorderLayout.WEST);
        priceRow.add(new JLabel("$250"), BorderLayout.EAST);

        JPanel nightsRow = new JPanel(new BorderLayout());
        nightsRow.setOpaque(false);
        nightsRow.add(new JLabel("Number of nights:"), BorderLayout.WEST);
        nightsValueLabel = new JLabel("3");
        nightsRow.add(nightsValueLabel, BorderLayout.EAST);

        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);
        JLabel totalText = new JLabel("Total:");
        totalText.setFont(new Font("SansSerif", Font.BOLD, 18));
        totalValueLabel = new JLabel("$750");
        totalValueLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        totalRow.add(totalText, BorderLayout.WEST);
        totalRow.add(totalValueLabel, BorderLayout.EAST);

        // Booking button
        JButton proceedButton = new JButton("Proceed to Booking");
        proceedButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        proceedButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        proceedButton.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Go to Booking / Confirmation page")
        );

        // Add components
        bookingPanel.add(title);
        bookingPanel.add(Box.createVerticalStrut(20));
        bookingPanel.add(checkInLabel);
        bookingPanel.add(Box.createVerticalStrut(5));
        bookingPanel.add(checkInField);
        bookingPanel.add(Box.createVerticalStrut(15));
        bookingPanel.add(checkOutLabel);
        bookingPanel.add(Box.createVerticalStrut(5));
        bookingPanel.add(checkOutField);
        bookingPanel.add(Box.createVerticalStrut(15));
        bookingPanel.add(guestsLabel);
        bookingPanel.add(Box.createVerticalStrut(5));
        bookingPanel.add(guestsSpinner);
        bookingPanel.add(Box.createVerticalStrut(20));
        bookingPanel.add(separator);
        bookingPanel.add(Box.createVerticalStrut(20));
        bookingPanel.add(summaryTitle);
        bookingPanel.add(Box.createVerticalStrut(15));
        bookingPanel.add(priceRow);
        bookingPanel.add(Box.createVerticalStrut(10));
        bookingPanel.add(nightsRow);
        bookingPanel.add(Box.createVerticalStrut(10));
        bookingPanel.add(totalRow);
        bookingPanel.add(Box.createVerticalStrut(25));
        bookingPanel.add(proceedButton);

        wrapper.add(bookingPanel, BorderLayout.NORTH);
        return wrapper;
    }

    /**
     * Footer displaying contact information
     */
    private JPanel createFooter() {
        JPanel footer = new JPanel();
        footer.setBorder(new EmptyBorder(10, 10, 10, 10));
        footer.setBackground(Color.WHITE);

        JLabel contactLabel = new JLabel(
            "Contact Info: 123 Hotel St, City, Country | +123 456 7890 | info@goldenopportunity.com"
        );
        footer.add(contactLabel);

        return footer;
    }

    /**
     * Entry point to launch the application
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RoomDetailsPage page = new RoomDetailsPage();
            page.setVisible(true);
        });
    }
}