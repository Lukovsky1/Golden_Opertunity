
package com.GoldenOpportunity;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;

/**
 * RoomDetailsPage represents the UI page where a guest can:
 * - View detailed information about a selected room
 * - Enter booking details (dates, guests)
 * - Proceed to reservation
 */

public class RoomDetailsPage extends JPanel {

    // Input fields for booking information
    private JLabel checkInField;
    private JLabel checkOutField;
    private JSpinner guestsSpinner;
    // Labels used to display booking summary
    private JLabel nightsValueLabel;
    private JLabel totalValueLabel;

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private LocalDate startDate;
    private LocalDate endDate;
    private int numGuests;
    private String imageFile;
    private Room room;
    private ReservationService reservationService;

    /**
     * Constructor: initializes the main window and layout
     */
    public RoomDetailsPage(CardLayout cardLayout,JPanel mainPanel,
                           LocalDate startDate,LocalDate endDate,int numGuests,Room room,String imageFile,
                           ReservationService reservationService) throws IOException {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.startDate = startDate;
        this.endDate = endDate;
        this.numGuests = numGuests;
        this.room = room;
        this.imageFile = imageFile;
        this.reservationService = reservationService;

        setLayout(new BorderLayout());

        add(createHeader(), BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(createMainContent());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);

        add(scrollPane, BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);
    }

    /**
     * Creates the header with logo and navigation buttons
     */
    private JPanel createHeader() throws IOException {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(15, 20, 15, 20));
        header.setBackground(Color.WHITE);

        Image logo = ImageIO.read(new File("src/main/java/com/GoldenOpportunity/logo.png"));

        int originalWidth = logo.getWidth(null);
        int originalHeight = logo.getHeight(null);

        int newHeight = 70;
        int newWidth = (originalWidth * newHeight) / originalHeight;

        Image scaledLogo = logo.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
        logoLabel.setFont(new Font("SansSerif", Font.BOLD, 20));

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
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

        buttonMap.get("Home").addActionListener(e -> {
            cardLayout.show(mainPanel,"HOME");
        });
        buttonMap.get("Rooms").addActionListener(e -> {
            cardLayout.show(mainPanel,"ROOMS");
        });

        header.add(logoLabel, BorderLayout.WEST);
        header.add(nav, BorderLayout.EAST);
        return header;
    }

    /**
     * Creates the main layout with two sections:
     * - Left: room details
     * - Right: booking panel
     */
    private JPanel createMainContent() throws IOException {
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
    private JPanel createLeftPanel() throws IOException {
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel pageTitle = new JLabel("Room Details");
        pageTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        pageTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Room image
        Image roomIcon = ImageIO.read(new File(imageFile));
        //ImageIcon roomIcon = new ImageIcon(getClass().getResource("/com/GoldenOpportunity/room.png"));
        Image scaledRoom = roomIcon.getScaledInstance(500, 280, Image.SCALE_SMOOTH);
        //Image scaledRoom = roomIcon.getImage().getScaledInstance(500, 280, Image.SCALE_SMOOTH);
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
        JLabel priceLabel = new JLabel("Price: $" + room.getRate() + " / night");
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
        checkInField = new JLabel(startDate.toString());

        JLabel checkOutLabel = new JLabel("Check-out Date");
        checkOutField = new JLabel(endDate.toString());

        JLabel guestsLabel = new JLabel("Number of Guests");
        guestsSpinner = new JSpinner(new SpinnerNumberModel(numGuests, 1, 10, 1));

        JSeparator separator = new JSeparator();

        // Cost summary section
        JLabel summaryTitle = new JLabel("Total Cost Summary");
        summaryTitle.setFont(new Font("SansSerif", Font.BOLD, 18));

        JPanel priceRow = new JPanel(new BorderLayout());
        priceRow.setOpaque(false);
        priceRow.add(new JLabel("Price per night:"), BorderLayout.WEST);
        priceRow.add(new JLabel("$" + String.format("%.2f",room.getRate())), BorderLayout.EAST);

        JPanel nightsRow = new JPanel(new BorderLayout());
        nightsRow.setOpaque(false);
        nightsRow.add(new JLabel("Number of nights:"), BorderLayout.WEST);
        nightsValueLabel = new JLabel(String.valueOf(Period.between(startDate,endDate).getDays()));
        nightsRow.add(nightsValueLabel, BorderLayout.EAST);

        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);
        JLabel totalText = new JLabel("Total:");
        totalText.setFont(new Font("SansSerif", Font.BOLD, 18));
        totalValueLabel = new JLabel("$" + String.format("%.2f",room.getRate()*Period.between(startDate,endDate).getDays()));
        totalValueLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        totalRow.add(totalText, BorderLayout.WEST);
        totalRow.add(totalValueLabel, BorderLayout.EAST);

        // Booking button
        JButton proceedButton = new JButton("Proceed to Booking");
        proceedButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        proceedButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        proceedButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    startDate = LocalDate.parse(checkInField.getText());
                    endDate = LocalDate.parse(checkOutField.getText());
                    reservationService.createReservation(room,startDate,endDate,room.getRate());
                }
            }
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
}
