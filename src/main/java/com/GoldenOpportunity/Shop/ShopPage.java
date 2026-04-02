package com.GoldenOpportunity.Shop;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ShopPage extends JPanel {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Shop shop;
    private ShoppingCart shoppingCart;

    public ShopPage(CardLayout cardLayout,JPanel mainPanel) throws IOException {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;

        shop = new Shop("src/main/resources/testProductData.csv");
        shoppingCart = new ShoppingCart();

        setLayout(new BorderLayout(10, 10));

        add(createHeader(), BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(createMainContent());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);

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
        logoLabel.setFont(new Font("SansSerif", Font.BOLD, 20));

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        nav.setBackground(Color.WHITE);
        String[] items = {"Home", "Rooms", "Shop", "Login", "Sign Up", "🛒"};
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
        buttonMap.get("Login").addActionListener(e -> {
            cardLayout.show(mainPanel,"LOGIN");
        });
        buttonMap.get("Shop").addActionListener(e -> {
            cardLayout.show(mainPanel,"SHOP");
        });

        header.add(logoLabel, BorderLayout.WEST);
        header.add(nav, BorderLayout.EAST);
        return header;
    }

    private JPanel createMainContent() throws IOException {
        JPanel center = new JPanel();
        center.setLayout(new BorderLayout());
        center.setBackground(Color.WHITE);

        JLabel title = new JLabel("Our Products");
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel grid = new JPanel(new GridLayout(0, 3, 25, 25));
        grid.setBackground(Color.WHITE);
        grid.setBorder(new EmptyBorder(10, 20, 20, 20));

        for (Product product : shop.getProducts()) {
            grid.add(createProductCard(product));
        }

        center.add(title, BorderLayout.NORTH);
        center.add(grid, BorderLayout.CENTER);

        return center;
    }

    private JPanel createProductCard(Product product) throws IOException {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(Color.LIGHT_GRAY));
        card.setPreferredSize(new Dimension(280, 240));

        // Image placeholder
        Image roomImage = ImageIO.read(new File(product.getImage()));
        Image scaledRoom = roomImage.getScaledInstance(180, 120, Image.SCALE_SMOOTH);

        JLabel image = new JLabel(new ImageIcon(scaledRoom));
        image.setPreferredSize(new Dimension(180, 120));
        /*
        JPanel image = new JPanel();
        image.setPreferredSize(new Dimension(180, 120));
        image.setBackground(new Color(220, 220, 220));
        image.add(new JLabel("Product Image"));
        */
        // Info section
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(Color.WHITE);
        info.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel name = new JLabel(product.getName());
        name.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel price = new JLabel("Price: $" + String.format("%.2f",product.getPrice()));

        JLabel stock = new JLabel("Stock: " + product.getStock());

        JButton addButton = new JButton("Add to Cart");
        addButton.setBackground(new Color(255, 204, 0));
        addButton.setFocusPainted(false);

        info.add(name);
        info.add(Box.createVerticalStrut(5));
        info.add(price);
        info.add(stock);
        info.add(Box.createVerticalStrut(10));
        info.add(addButton);

        card.add(image, BorderLayout.NORTH);
        card.add(info, BorderLayout.CENTER);

        return card;
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