package com.GoldenOpportunity;

import com.GoldenOpportunity.Shop.Product;
import com.GoldenOpportunity.Shop.ShopPage;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

// New main frame for all of the UI

public class MainUIFrame extends JFrame {

    private HotelBookingUI hotelBookingUI;
    private HotelHomePageUI hotelHomePageUI;
    private LoginPage loginPage;
    private ShopPage shopPage;
    private ProfilePage profilePage;

    public MainUIFrame() throws IOException {
        CardLayout cardLayout = new CardLayout();
        JPanel mainPanel = new JPanel(cardLayout);

        UIState uiState = new UIState();

        hotelHomePageUI = new HotelHomePageUI(cardLayout,mainPanel,uiState);
        hotelBookingUI = new HotelBookingUI(cardLayout,mainPanel,uiState);
        loginPage = new LoginPage(cardLayout,mainPanel,uiState);
        shopPage = new ShopPage(cardLayout,mainPanel,uiState);
        profilePage = new ProfilePage(cardLayout, mainPanel,uiState);

        mainPanel.add(hotelHomePageUI, "HOME");
        mainPanel.add(hotelBookingUI, "ROOMS");
        mainPanel.add(loginPage,"LOGIN");
        mainPanel.add(shopPage,"SHOP");
        mainPanel.add(profilePage, "PROFILE");

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
                new MainUIFrame().setVisible(true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
