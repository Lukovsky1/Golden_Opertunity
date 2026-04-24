package com.GoldenOpportunity;

//import com.GoldenOpportunity.Shop.ShopPage;
import com.GoldenOpportunity.Shop.ShopDBInitializer;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

// New main frame for all of the UI

public class MainUIFrame extends JFrame {

    public MainUIFrame() throws IOException {
        CardLayout cardLayout = new CardLayout();
        JPanel mainPanel = new JPanel(cardLayout);

        HotelHomePageUI hotelHomePageUI = new HotelHomePageUI(cardLayout,mainPanel);
        HotelBookingUI hotelBookingUI = new HotelBookingUI(cardLayout,mainPanel,
                hotelHomePageUI.getRoomService(),hotelHomePageUI.getReservationService());
        LoginPage loginPage = new LoginPage(cardLayout,mainPanel);
        //ShopPage shopPage = new ShopPage(cardLayout,mainPanel);

        mainPanel.add(hotelHomePageUI, "HOME");
        mainPanel.add(hotelBookingUI, "ROOMS");
        mainPanel.add(loginPage,"LOGIN");
        //mainPanel.add(shopPage,"SHOP");

        add(mainPanel);

        setTitle("Golden Opportunity Hotel");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout.show(mainPanel, "HOME");

        setVisible(true);
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            try {
                // load shop database
                ShopDBInitializer.initializeDatabase();
                new MainUIFrame().setVisible(true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
