package com.GoldenOpportunity;

//import com.GoldenOpportunity.Shop.Product;
import com.GoldenOpportunity.Shop.ShopPage;
import com.GoldenOpportunity.DatabaseTools.DBInitializer;
//import com.GoldenOpportunity.Shop.ShopPage;
import com.GoldenOpportunity.Shop.ShopDBInitializer;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;

// New main frame for all of the UI

public class MainUIFrame extends JFrame {

    private HotelBookingUI hotelBookingUI;
    private HotelHomePageUI hotelHomePageUI;
    private LoginPage loginPage;
    private ShopPage shopPage;
    private ProfilePage profilePage;
    private ClerkHomePage clerkHomePage;
    private AddRoomPage addRoomPage;
    private ModifyRoomsPage modifyRoomsPage;
    private AdminPage adminPage;

    //Must load database before all other operations
    static {
        try {
            DBInitializer.initialize();
        } catch (SQLException e) {
            System.err.println("Error initializing DB, exiting program");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Error reading DB Files, exiting program");
            throw new RuntimeException(e);
        }
    }

    public MainUIFrame() throws IOException {
        CardLayout cardLayout = new CardLayout();
        JPanel mainPanel = new JPanel(cardLayout);

        UIState uiState = new UIState();

        hotelHomePageUI = new HotelHomePageUI(cardLayout,mainPanel,uiState);
        hotelBookingUI = hotelHomePageUI.hotelBookingUI;
        loginPage = new LoginPage(cardLayout,mainPanel,uiState);
        shopPage = new ShopPage(cardLayout,mainPanel,uiState);
        profilePage = new ProfilePage(cardLayout, mainPanel,uiState);
        adminPage = new AdminPage(cardLayout, mainPanel, uiState);
        clerkHomePage = new ClerkHomePage(cardLayout, mainPanel, uiState);
        addRoomPage = new AddRoomPage(cardLayout,mainPanel,uiState);
        modifyRoomsPage = new ModifyRoomsPage(cardLayout,mainPanel,uiState);

        mainPanel.add(hotelHomePageUI, "HOME");
        mainPanel.add(hotelBookingUI, "ROOMS");
        mainPanel.add(loginPage,"LOGIN");
        mainPanel.add(shopPage,"SHOP");
        mainPanel.add(profilePage, "PROFILE");
        mainPanel.add(clerkHomePage,"CLERK_HOME");
        mainPanel.add(addRoomPage,"ADD_ROOMS");
        mainPanel.add(modifyRoomsPage, "MODIFY_ROOMS");
        mainPanel.add(adminPage, "ADMIN");

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
                //ShopDBInitializer.initializeDatabase();
                new MainUIFrame().setVisible(true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
