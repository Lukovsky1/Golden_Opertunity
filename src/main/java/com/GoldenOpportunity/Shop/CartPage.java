package com.GoldenOpportunity.Shop;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CartPage extends JPanel {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    private ShopController shopController;
    private ShoppingCart shoppingCart;
    private int guestID;

    private JPanel cartItemsPanel;
    private JLabel subtotalLabel;
    private JLabel itemsLabel;

    public CartPage(CardLayout cardLayout, JPanel mainPanel,
                    ShopController shopController,
                    ShoppingCart shoppingCart,
                    int guestID) throws IOException {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.shopController = shopController;
        this.shoppingCart = shoppingCart;
        this.guestID = guestID;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(createHeader(), BorderLayout.NORTH);
        add(createCenterContent(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);

        refreshCartDisplay();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        if (visible && cartItemsPanel != null) {
            refreshCartDisplay();
        }
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(15, 20, 15, 20));
        header.setBackground(Color.WHITE);

        JLabel logoLabel = new JLabel("Golden Opportunity Hotel");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        nav.setBackground(Color.WHITE);

        String[] items = {"Home", "Rooms", "Shop", "Login", "Sign Up", "🛒 Cart"};
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
        buttonMap.get("Sign Up").addActionListener(e -> cardLayout.show(mainPanel, "SIGNUP"));
        buttonMap.get("🛒 Cart").addActionListener(e -> {
            refreshCartDisplay();
            cardLayout.show(mainPanel, "CART");
        });

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

        cartItemsPanel = new JPanel();
        cartItemsPanel.setLayout(new BoxLayout(cartItemsPanel, BoxLayout.Y_AXIS));
        cartItemsPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(cartItemsPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel contentPanel = new JPanel(new BorderLayout(20, 0));
        contentPanel.setBackground(Color.WHITE);

        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(createOrderSummary(), BorderLayout.EAST);

        wrapper.add(title, BorderLayout.NORTH);
        wrapper.add(contentPanel, BorderLayout.CENTER);

        return wrapper;
    }

    private void refreshCartDisplay() {
        cartItemsPanel.removeAll();

        if (shoppingCart.getCartItems().isEmpty()) {
            JLabel emptyLabel = new JLabel("Your cart is empty.");
            emptyLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            emptyLabel.setBorder(new EmptyBorder(20, 0, 0, 0));
            cartItemsPanel.add(emptyLabel);
        } else {
            for (ProductDescription product : shoppingCart.getUniqueCartItems()) {
                cartItemsPanel.add(createCartItemRow(product));
                cartItemsPanel.add(Box.createVerticalStrut(12));
            }
        }

        updateOrderSummary();

        cartItemsPanel.revalidate();
        cartItemsPanel.repaint();
    }

    private JPanel createCartItemRow(ProductDescription product) {
        JPanel row = new JPanel(new BorderLayout(15, 0));
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(12, 12, 12, 12)
        ));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        JLabel imageLabel = createProductImageLabel(product);

        JPanel productInfo = new JPanel();
        productInfo.setLayout(new BoxLayout(productInfo, BoxLayout.Y_AXIS));
        productInfo.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel(product.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel priceLabel = new JLabel("Price: $" + String.format("%.2f", product.getPrice()));
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 13));

        productInfo.add(nameLabel);
        productInfo.add(Box.createVerticalStrut(5));
        productInfo.add(priceLabel);

        JPanel leftSide = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftSide.setBackground(Color.WHITE);
        leftSide.add(imageLabel);
        leftSide.add(productInfo);

        JPanel rightSide = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 10));
        rightSide.setBackground(Color.WHITE);

        int quantity = shoppingCart.getQuantity(product.getProductID());

        JButton minusButton = new JButton("-");
        JLabel qtyValueLabel = new JLabel(String.valueOf(quantity), SwingConstants.CENTER);
        JButton plusButton = new JButton("+");

        minusButton.setPreferredSize(new Dimension(45, 30));
        qtyValueLabel.setPreferredSize(new Dimension(40, 30));
        plusButton.setPreferredSize(new Dimension(45, 30));

        JButton removeButton = new JButton("Remove");
        removeButton.setBackground(new Color(55, 65, 81));
        removeButton.setForeground(Color.WHITE);
        removeButton.setFocusPainted(false);

        plusButton.addActionListener(e -> {
            String result = shopController.addProductToCart(
                    guestID,
                    product.getProductID(),
                    shoppingCart
            );

            if (result.equals("updatedCart")) {
                refreshCartDisplay();
            } else {
                JOptionPane.showMessageDialog(this, result);
            }
        });

        minusButton.addActionListener(e -> {
            shopController.removeProductFromCart(product.getProductID(), shoppingCart);
            refreshCartDisplay();
        });

        removeButton.addActionListener(e -> {
            while (shoppingCart.getQuantity(product.getProductID()) > 0) {
                shopController.removeProductFromCart(product.getProductID(), shoppingCart);
            }

            refreshCartDisplay();
        });

        rightSide.add(new JLabel("Qty:"));
        rightSide.add(minusButton);
        rightSide.add(qtyValueLabel);
        rightSide.add(plusButton);
        rightSide.add(removeButton);

        row.add(leftSide, BorderLayout.CENTER);
        row.add(rightSide, BorderLayout.EAST);

        return row;
    }

    private JLabel createProductImageLabel(ProductDescription product) {
        JLabel imageLabel = new JLabel("Image", SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(75, 70));
        imageLabel.setBorder(new LineBorder(Color.LIGHT_GRAY));

        try {
            Image productImage = ImageIO.read(new File(product.getImage()));
            Image scaledImage = productImage.getScaledInstance(70, 65, Image.SCALE_SMOOTH);
            imageLabel.setText("");
            imageLabel.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            imageLabel.setOpaque(true);
            imageLabel.setBackground(new Color(220, 220, 220));
        }

        return imageLabel;
    }

    private JPanel createOrderSummary() {
        JPanel summary = new JPanel();
        summary.setLayout(new BoxLayout(summary, BoxLayout.Y_AXIS));
        summary.setBackground(Color.WHITE);
        summary.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(18, 18, 18, 18)
        ));
        summary.setPreferredSize(new Dimension(260, 190));

        JLabel title = new JLabel("Order Summary");
        title.setFont(new Font("Arial", Font.BOLD, 16));

        subtotalLabel = new JLabel("Subtotal: $0.00");
        itemsLabel = new JLabel("Items: 0");

        JButton checkoutButton = new JButton("Proceed to Checkout");
        checkoutButton.setBackground(new Color(31, 41, 55));
        checkoutButton.setForeground(Color.WHITE);
        checkoutButton.setFocusPainted(false);
        checkoutButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        checkoutButton.addActionListener(e -> {
            if (shoppingCart.getCartItems().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Your cart is empty.");
                return;
            }

            cardLayout.show(mainPanel, "CHECKOUT");
        });

        summary.add(title);
        summary.add(Box.createVerticalStrut(18));
        summary.add(subtotalLabel);
        summary.add(Box.createVerticalStrut(10));
        summary.add(itemsLabel);
        summary.add(Box.createVerticalStrut(20));
        summary.add(checkoutButton);

        return summary;
    }

    private void updateOrderSummary() {
        double subtotal = shoppingCart.calculateTotal();
        int itemCount = shoppingCart.getCartItems().size();

        subtotalLabel.setText("Subtotal: $" + String.format("%.2f", subtotal));
        itemsLabel.setText("Items: " + itemCount);
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