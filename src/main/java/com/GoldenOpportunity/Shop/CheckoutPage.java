package com.GoldenOpportunity.Shop;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class CheckoutPage extends JPanel {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField addressField;
    private JTextField cityField;
    private JTextField stateField;
    private JTextField postalCodeField;

    private JTextField cardNumberField;
    private JComboBox<String> monthBox;
    private JComboBox<String> yearBox;
    private JTextField cvvField;
    private JTextField billingZipField;

    public CheckoutPage(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        JScrollPane scrollPane = new JScrollPane(createMainContent());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);
    }

    private JPanel createMainContent() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
        container.setBorder(new EmptyBorder(25, 30, 25, 30));
        container.setBackground(new Color(245, 245, 245));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(new Color(245, 245, 245));

        leftPanel.add(createGuestInfo());
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(createPaymentInfo());

        JPanel summaryPanel = createSummaryPanel();

        leftPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        summaryPanel.setAlignmentY(Component.TOP_ALIGNMENT);

        container.add(leftPanel);
        container.add(Box.createHorizontalStrut(20));
        container.add(summaryPanel);

        return container;
    }

    private JPanel createGuestInfo() {
        JPanel box = createCardPanel();
        box.add(sectionTitle("Guest Information"));

        firstNameField = styledField();
        lastNameField = styledField();
        box.add(twoFieldRow("First Name", firstNameField, "Last Name", lastNameField));

        emailField = styledField();
        phoneField = styledField();
        box.add(twoFieldRow("Email Address", emailField, "Phone Number", phoneField));

        addressField = styledField();
        box.add(oneField("Address Line", addressField));

        cityField = styledField();
        stateField = styledField();
        box.add(twoFieldRow("City", cityField, "State/Province", stateField));

        postalCodeField = styledField();
        box.add(oneField("Postal Code", postalCodeField));

        return box;
    }

    private JPanel createPaymentInfo() {
        JPanel box = createCardPanel();
        box.add(sectionTitle("Payment Information"));

        cardNumberField = styledField();
        box.add(oneField("Credit/Debit Card Number", cardNumberField));

        JPanel expiryRow = new JPanel(new GridLayout(1, 2, 15, 0));
        expiryRow.setBackground(Color.WHITE);
        expiryRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        expiryRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        monthBox = new JComboBox<>(new String[]{"01","02","03","04","05","06","07","08","09","10","11","12"});
        yearBox = new JComboBox<>(new String[]{"2026","2027","2028","2029","2030","2031","2032","2033","2034","2035"});

        expiryRow.add(labeledComponent("Expiration Month", monthBox));
        expiryRow.add(labeledComponent("Expiration Year", yearBox));

        box.add(expiryRow);
        box.add(Box.createVerticalStrut(10));

        cvvField = styledField();
        billingZipField = styledField();
        box.add(twoFieldRow("Security Code", cvvField, "Billing Zip Code", billingZipField));

        return box;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = createCardPanel();
        panel.setPreferredSize(new Dimension(360, 280));
        panel.setMaximumSize(new Dimension(360, Integer.MAX_VALUE));

        panel.add(sectionTitle("Order Summary"));

        panel.add(leftLabel("Product 1: $XX.XX"));
        panel.add(Box.createVerticalStrut(8));
        panel.add(leftLabel("Product 2: $XX.XX"));
        panel.add(Box.createVerticalStrut(15));

        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        panel.add(separator);
        panel.add(Box.createVerticalStrut(15));

        JLabel total = leftLabel("Total: $XXX.XX");
        total.setFont(new Font("SansSerif", Font.BOLD, 18));
        panel.add(total);

        JButton placeOrderButton = new JButton("Place Order");
        placeOrderButton.setBackground(new Color(30, 170, 70));
        placeOrderButton.setForeground(Color.WHITE);
        placeOrderButton.setFocusPainted(false);
        placeOrderButton.setBorderPainted(false);
        placeOrderButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        placeOrderButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        placeOrderButton.addActionListener(e -> {
            if (!validateGuestInfo() || !validatePaymentInfo()) {
                return;
            }

            JOptionPane.showMessageDialog(this, "Order placed successfully!");
            cardLayout.show(mainPanel, "SHOP");
        });

        panel.add(Box.createVerticalStrut(20));
        panel.add(placeOrderButton);

        return panel;
    }

    private JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new CompoundBorder(
                new LineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(18, 18, 18, 18)
        ));
        return panel;
    }

    private JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 20));
        label.setBorder(new EmptyBorder(5, 0, 12, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JLabel leftLabel(String text) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField styledField() {
        JTextField field = new JTextField();
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }

    private JPanel oneField(String label, JTextField field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel jLabel = new JLabel(label);
        jLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(jLabel);
        panel.add(field);
        panel.add(Box.createVerticalStrut(12));

        return panel;
    }

    private JPanel twoFieldRow(String label1, JTextField field1, String label2, JTextField field2) {
        JPanel row = new JPanel(new GridLayout(1, 2, 15, 0));
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        row.add(labeledField(label1, field1));
        row.add(labeledField(label2, field2));

        return row;
    }

    private JPanel labeledField(String label, JTextField field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel jLabel = new JLabel(label);
        jLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(jLabel);
        panel.add(field);

        return panel;
    }

    private JPanel labeledComponent(String label, JComponent component) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel jLabel = new JLabel(label);
        jLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        component.setAlignmentX(Component.LEFT_ALIGNMENT);
        component.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        panel.add(jLabel);
        panel.add(component);

        return panel;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel();
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

        JLabel contact = new JLabel(
                "Contact Info: 123 Hotel St, City, Country | +123 456 7890 | info@goldenopportunity.com"
        );

        footer.add(contact);
        return footer;
    }

    private boolean validateGuestInfo() {
        if (firstNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your first name.");
            return false;
        }

        if (lastNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your last name.");
            return false;
        }

        if (!emailField.getText().contains("@")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email.");
            return false;
        }

        return true;
    }

    private boolean validatePaymentInfo() {
        String cardNumber = cardNumberField.getText().trim();
        String cvv = cvvField.getText().trim();

        if (!cardNumber.matches("\\d{16}")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid 16-digit card number.");
            return false;
        }

        if (!cvv.matches("\\d{3}")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid 3-digit security code.");
            return false;
        }

        return true;
    }
}