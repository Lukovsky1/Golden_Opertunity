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

public class ProductDetailsPage extends JPanel {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Product product;
    private JSpinner quantitySpinner;

    public ProductDetailsPage(CardLayout cardLayout, JPanel mainPanel, Product product) throws IOException {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.product = product;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(createHeader(), BorderLayout.NORTH);
        add(createCenterContent(), BorderLayout.CENTER);
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

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        nav.setBackground(Color.WHITE);

        String[] items = {"Home", "Rooms", "Shop", "Login", "Sign Up", "🛒"};
        Map<String, JButton> buttonMap = new HashMap<>();

        for (String item : items) {
            JButton button = new JButton(item);
            button.setFocusPainted(false);
            button.setBackground(Color.WHITE);
            button.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            button.setPreferredSize(new Dimension(90, 35));
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

    private JPanel createCenterContent() throws IOException {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel pageTitle = new JLabel("Product Details");
        pageTitle.setFont(new Font("Arial", Font.BOLD, 24));
        pageTitle.setBorder(new EmptyBorder(0, 0, 20, 0));

        JPanel contentPanel = new JPanel(new BorderLayout(30, 0));
        contentPanel.setBackground(Color.WHITE);

        contentPanel.add(createLeftSection(), BorderLayout.CENTER);
        contentPanel.add(createRightSection(), BorderLayout.EAST);

        wrapper.add(pageTitle, BorderLayout.NORTH);
        wrapper.add(contentPanel, BorderLayout.CENTER);

        return wrapper;
    }

    private JPanel createLeftSection() throws IOException {
        JPanel leftSection = new JPanel(new BorderLayout(25, 0));
        leftSection.setBackground(Color.WHITE);

        JPanel imageSection = new JPanel();
        imageSection.setLayout(new BoxLayout(imageSection, BoxLayout.Y_AXIS));
        imageSection.setBackground(Color.WHITE);

        Image productImage = ImageIO.read(new File(product.getImage()));
        Image bigImage = productImage.getScaledInstance(250, 250, Image.SCALE_SMOOTH);

        JLabel mainImageLabel = new JLabel(new ImageIcon(bigImage));
        mainImageLabel.setPreferredSize(new Dimension(250, 250));
        mainImageLabel.setBorder(new LineBorder(Color.LIGHT_GRAY));

        JPanel thumbsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        thumbsPanel.setBackground(Color.WHITE);

        for (int i = 0; i < 3; i++) {
            Image thumbImage = productImage.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            JLabel thumbLabel = new JLabel(new ImageIcon(thumbImage));
            thumbLabel.setPreferredSize(new Dimension(60, 60));
            thumbLabel.setBorder(new LineBorder(Color.LIGHT_GRAY));
            thumbsPanel.add(thumbLabel);
        }

        imageSection.add(mainImageLabel);
        imageSection.add(Box.createVerticalStrut(10));
        imageSection.add(thumbsPanel);

        JPanel infoSection = new JPanel();
        infoSection.setLayout(new BoxLayout(infoSection, BoxLayout.Y_AXIS));
        infoSection.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel(product.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 26));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea descriptionArea = new JTextArea(
                "This is a short description of the product. It provides a brief overview "
                + "to help the customer understand what they are buying."
        );
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEditable(false);
        descriptionArea.setOpaque(false);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 15));
        descriptionArea.setBorder(null);
        descriptionArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        descriptionArea.setMaximumSize(new Dimension(400, 100));

        JLabel priceLabel = new JLabel("Price: $" + String.format("%.2f", product.getPrice()));
        priceLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JLabel stockLabel = new JLabel(product.getStock() > 0 ? "Stock: In Stock" : "Stock: Out of Stock");
        stockLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        JLabel detailsTitle = new JLabel("Details:");
        detailsTitle.setFont(new Font("Arial", Font.BOLD, 18));

        JTextArea detailsArea = new JTextArea(
                "• Product ID: " + product.getProductID() + "\n" +
                "• Name: " + product.getName() + "\n" +
                "• Available Stock: " + product.getStock()
        );
        detailsArea.setFont(new Font("Arial", Font.PLAIN, 14));
        detailsArea.setEditable(false);
        detailsArea.setOpaque(false);
        detailsArea.setBorder(null);
        detailsArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton backButton = new JButton("Back to Shop");
        backButton.setFocusPainted(false);
        backButton.setBackground(new Color(230, 230, 230));
        backButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "SHOP"));

        infoSection.add(nameLabel);
        infoSection.add(Box.createVerticalStrut(10));
        infoSection.add(descriptionArea);
        infoSection.add(Box.createVerticalStrut(20));
        infoSection.add(priceLabel);
        infoSection.add(Box.createVerticalStrut(10));
        infoSection.add(stockLabel);
        infoSection.add(Box.createVerticalStrut(15));
        infoSection.add(detailsTitle);
        infoSection.add(Box.createVerticalStrut(8));
        infoSection.add(detailsArea);
        infoSection.add(Box.createVerticalStrut(20));
        infoSection.add(backButton);

        leftSection.add(imageSection, BorderLayout.WEST);
        leftSection.add(infoSection, BorderLayout.CENTER);

        return leftSection;
    }

    private JPanel createRightSection() {
        JPanel rightSection = new JPanel();
        rightSection.setLayout(new BoxLayout(rightSection, BoxLayout.Y_AXIS));
        rightSection.setBackground(Color.WHITE);
        rightSection.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220)),
                new EmptyBorder(20, 20, 20, 20)
        ));
        rightSection.setPreferredSize(new Dimension(280, 350));

        JLabel priceLabel = new JLabel("$" + String.format("%.2f", product.getPrice()));
        priceLabel.setFont(new Font("Arial", Font.BOLD, 26));
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel stockLabel = new JLabel(product.getStock() > 0 ? "In Stock" : "Out of Stock");
        stockLabel.setForeground(new Color(0, 118, 0));
        stockLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        stockLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel qtyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        qtyPanel.setBackground(Color.WHITE);

        JLabel qtyLabel = new JLabel("Qty:");
        quantitySpinner = new JSpinner(
                new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1)
        );
        quantitySpinner.setPreferredSize(new Dimension(60, 30));

        JLabel stockErrorLabel = new JLabel(" ");
        stockErrorLabel.setForeground(Color.RED);
        stockErrorLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        stockErrorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // lets the user type normally
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) quantitySpinner.getEditor();
        JTextField textField = editor.getTextField();

        Runnable validateQuantity = () -> {
            try {
                quantitySpinner.commitEdit();
                int value = (Integer) quantitySpinner.getValue();

                if (value > product.getStock()) {
                    stockErrorLabel.setText("Not enough stock available");
                } else {
                    stockErrorLabel.setText(" ");
                }
            } catch (Exception ex) {
                stockErrorLabel.setText(" ");
            }
        };

        quantitySpinner.addChangeListener(e -> validateQuantity.run());

        textField.addActionListener(e -> validateQuantity.run());

        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                validateQuantity.run();
            }
        });

        qtyPanel.add(qtyLabel);
        qtyPanel.add(quantitySpinner);

        JButton addToCartButton = new JButton("Add to Cart");
        addToCartButton.setBackground(new Color(255, 216, 20));
        addToCartButton.setForeground(Color.BLACK);
        addToCartButton.setFocusPainted(false);
        addToCartButton.setBorder(new LineBorder(new Color(200, 170, 0)));
        addToCartButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        addToCartButton.setFont(new Font("Arial", Font.BOLD, 14));

        JButton buyNowButton = new JButton("Buy Now");
        buyNowButton.setBackground(new Color(255, 153, 0));
        buyNowButton.setForeground(Color.BLACK);
        buyNowButton.setFocusPainted(false);
        buyNowButton.setBorder(new LineBorder(new Color(200, 120, 0)));
        buyNowButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        buyNowButton.setFont(new Font("Arial", Font.BOLD, 14));

        addToCartButton.addActionListener(e -> {
            try {
                quantitySpinner.commitEdit();
                int quantity = (Integer) quantitySpinner.getValue();

                if (quantity > product.getStock()) {
                    stockErrorLabel.setText("Not enough stock available");
                    return;
                }

                stockErrorLabel.setText(" ");
                JOptionPane.showMessageDialog(this,
                        quantity + " x " + product.getName() + " added to cart.");
            } catch (Exception ex) {
                stockErrorLabel.setText("Please enter a valid quantity");
            }
        });

        buyNowButton.addActionListener(e -> {
            try {
                quantitySpinner.commitEdit();
                int quantity = (Integer) quantitySpinner.getValue();

                if (quantity > product.getStock()) {
                    stockErrorLabel.setText("Not enough stock available");
                    return;
                }

                stockErrorLabel.setText(" ");
                JOptionPane.showMessageDialog(this,
                        "Buy Now clicked for " + quantity + " x " + product.getName());
            } catch (Exception ex) {
                stockErrorLabel.setText("Please enter a valid quantity");
            }
        });

        rightSection.add(priceLabel);
        rightSection.add(Box.createVerticalStrut(10));
        rightSection.add(stockLabel);
        rightSection.add(Box.createVerticalStrut(15));
        rightSection.add(qtyPanel);
        rightSection.add(Box.createVerticalStrut(5));
        rightSection.add(stockErrorLabel);
        rightSection.add(Box.createVerticalStrut(20));
        rightSection.add(addToCartButton);
        rightSection.add(Box.createVerticalStrut(10));
        rightSection.add(buyNowButton);

        return rightSection;
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