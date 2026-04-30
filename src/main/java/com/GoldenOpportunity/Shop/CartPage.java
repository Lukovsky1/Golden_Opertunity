package com.GoldenOpportunity.Shop;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CartPage extends JPanel {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    public CartPage(CardLayout cardLayout, JPanel mainPanel) throws IOException {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(createHeader(), BorderLayout.NORTH);
        add(createCenterContent(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(15, 20, 15, 20));
        header.setBackground(Color.WHITE);

        JLabel logoLabel = new JLabel("Golden Opportunity Hotel");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        nav.setBackground(Color.WHITE);

        String[] items = {"Home", "Rooms", "Shop", "Login", "Sign Up", "🛒 Cart (2)"};
        Map<String, JButton> buttonMap = new HashMap<>();

        for (String item : items) {
            JButton button = new JButton(item);
            button.setFocusPainted(false);
            button.setBackground(Color.WHITE);
            button.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            button.setPreferredSize(new Dimension(100, 35));
            buttonMap.put(item, button);
            nav.add(button);
        }

        buttonMap.get("Home").addActionListener(e -> cardLayout.show(mainPanel, "HOME"));
        buttonMap.get("Rooms").addActionListener(e -> cardLayout.show(mainPanel, "ROOMS"));
        buttonMap.get("Shop").addActionListener(e -> cardLayout.show(mainPanel, "SHOP"));
        buttonMap.get("Login").addActionListener(e -> cardLayout.show(mainPanel, "LOGIN"));
        

        header.add(logoLabel, BorderLayout.WEST);
        header.add(nav, BorderLayout.EAST);

        return header;
    }

    private JPanel createCenterContent() {
        JPanel wrapper = new JPanel(new BorderLayout(20, 0));
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(new EmptyBorder(20, 30, 30, 30));

        JLabel title = new JLabel("Shopping Cart");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBorder(new EmptyBorder(0, 0, 15, 0));

        JPanel cartItemsPanel = new JPanel();
        cartItemsPanel.setLayout(new BoxLayout(cartItemsPanel, BoxLayout.Y_AXIS));
        cartItemsPanel.setBackground(Color.WHITE);

        cartItemsPanel.add(createCartItemRow("T-shirt", 25.00));
        cartItemsPanel.add(Box.createVerticalStrut(12));
        cartItemsPanel.add(createCartItemRow("Hat", 15.00));

        JPanel contentPanel = new JPanel(new BorderLayout(20, 0));
        contentPanel.setBackground(Color.WHITE);

        contentPanel.add(cartItemsPanel, BorderLayout.CENTER);
        contentPanel.add(createOrderSummary(), BorderLayout.EAST);

        wrapper.add(title, BorderLayout.NORTH);
        wrapper.add(contentPanel, BorderLayout.CENTER);

        return wrapper;
    }

    private JPanel createCartItemRow(String productName, double price) {
        JPanel row = new JPanel(new BorderLayout(15, 0));
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(12, 12, 12, 12)
        ));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JCheckBox selectBox = new JCheckBox();
        selectBox.setBackground(Color.WHITE);

        JLabel imagePlaceholder = new JLabel("Image", SwingConstants.CENTER);
        imagePlaceholder.setPreferredSize(new Dimension(70, 60));
        imagePlaceholder.setOpaque(true);
        imagePlaceholder.setBackground(new Color(220, 220, 220));

        JPanel productInfo = new JPanel();
        productInfo.setLayout(new BoxLayout(productInfo, BoxLayout.Y_AXIS));
        productInfo.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel(productName);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel priceLabel = new JLabel("Price: $" + String.format("%.2f", price));
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 13));

        productInfo.add(nameLabel);
        productInfo.add(Box.createVerticalStrut(5));
        productInfo.add(priceLabel);

        JPanel leftSide = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftSide.setBackground(Color.WHITE);
        leftSide.add(selectBox);
        leftSide.add(imagePlaceholder);
        leftSide.add(productInfo);

        JPanel rightSide = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 10));
        rightSide.setBackground(Color.WHITE);

        JLabel qtyLabel = new JLabel("Qty:");
        JComboBox<Integer> qtyBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        qtyBox.setPreferredSize(new Dimension(55, 28));

        JButton removeButton = new JButton("Remove");
        removeButton.setBackground(new Color(55, 65, 81));
        removeButton.setForeground(Color.WHITE);
        removeButton.setFocusPainted(false);

        rightSide.add(qtyLabel);
        rightSide.add(qtyBox);
        rightSide.add(removeButton);

        row.add(leftSide, BorderLayout.CENTER);
        row.add(rightSide, BorderLayout.EAST);

        return row;
    }

    private JPanel createOrderSummary() {
        JPanel summary = new JPanel();
        summary.setLayout(new BoxLayout(summary, BoxLayout.Y_AXIS));
        summary.setBackground(Color.WHITE);
        summary.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(18, 18, 18, 18)
        ));
        summary.setPreferredSize(new Dimension(260, 170));

        JLabel title = new JLabel("Order Summary");
        title.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel subtotal = new JLabel("Subtotal: $40.00");
        JLabel items = new JLabel("Items: 2");

        JButton checkoutButton = new JButton("Proceed to Checkout");
        checkoutButton.setBackground(new Color(31, 41, 55));
        checkoutButton.setForeground(Color.WHITE);
        checkoutButton.setFocusPainted(false);
        checkoutButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        checkoutButton.addActionListener(e -> cardLayout.show(mainPanel, "CHECKOUT"));

        summary.add(title);
        summary.add(Box.createVerticalStrut(18));
        summary.add(subtotal);
        summary.add(Box.createVerticalStrut(10));
        summary.add(items);
        summary.add(Box.createVerticalStrut(20));
        summary.add(checkoutButton);

        return summary;
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
}