package com.GoldenOpportunity;

//import com.GoldenOpportunity.Shop.Product;
import com.GoldenOpportunity.Shop.ShopPage;
import com.GoldenOpportunity.DatabaseTools.DBInitializer;
//import com.GoldenOpportunity.Shop.ShopPage;
//import com.GoldenOpportunity.Shop.ShopDBInitializer;
import com.GoldenOpportunity.Shop.*;
import com.GoldenOpportunity.Roles.Clerk;
import com.GoldenOpportunity.dbLogin.UserDao;

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
    private NewReservationPage newReservationPage;
    private AdminPage adminPage;
    private CartPage cartPage;
    private com.GoldenOpportunity.Shop.CheckoutPage checkoutPage;

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

    public MainUIFrame() throws IOException, SQLException {
        CardLayout cardLayout = new CardLayout();
        JPanel mainPanel = new JPanel(cardLayout);

        UIState uiState = new UIState();

        hotelHomePageUI = new HotelHomePageUI(cardLayout,mainPanel,uiState);
        hotelBookingUI = hotelHomePageUI.hotelBookingUI;
        loginPage = new LoginPage(cardLayout,mainPanel,uiState);
        profilePage = new ProfilePage(cardLayout, mainPanel,uiState);
        adminPage = new AdminPage(cardLayout, mainPanel, uiState);
        clerkHomePage = new ClerkHomePage(cardLayout, mainPanel, uiState);
        addRoomPage = new AddRoomPage(cardLayout,mainPanel,uiState);
        modifyRoomsPage = new ModifyRoomsPage(cardLayout,mainPanel,uiState);
        newReservationPage = new NewReservationPage(cardLayout,mainPanel,uiState);

        uiState.setProfilePage(profilePage);

        // Shop backend setup
        // been updated to account for movement of code
        ProductRepo productRepo = new ProductRepo();
        Shop shop = new Shop(productRepo);
        PaymentMethod paymentMethod = new PaymentMethod();
        Clerk clerk = new Clerk(2, "clerk1", "clerkpass", "clerk@golden.com");

        ReservationService reservationService = new ReservationService();
        UserDao userDao = new UserDao();

        ShopService shopService = new ShopService(
                shop,
                paymentMethod,
                clerk,
                reservationService,
                userDao
        );

        ShopController shopController = new ShopController(shopService);

        ShoppingCart shoppingCart = new ShoppingCart();

        int guestID = -1; // fallback only; real ID comes from uiState after login

        shopPage = new ShopPage(cardLayout, mainPanel, shopController, shoppingCart, guestID, uiState);
        cartPage = new CartPage(cardLayout, mainPanel, shopController, shoppingCart, guestID, uiState);
        checkoutPage = new com.GoldenOpportunity.Shop.CheckoutPage(cardLayout, mainPanel, shopController, shoppingCart, guestID, uiState);
        
        mainPanel.add(hotelHomePageUI, "HOME");
        mainPanel.add(hotelBookingUI, "ROOMS");
        mainPanel.add(loginPage,"LOGIN");
        mainPanel.add(shopPage,"SHOP");
        mainPanel.add(profilePage, "PROFILE");
        mainPanel.add(clerkHomePage,"CLERK_HOME");
        mainPanel.add(addRoomPage,"ADD_ROOMS");
        mainPanel.add(modifyRoomsPage, "MODIFY_ROOMS");
        mainPanel.add(newReservationPage, "NEW_RESERVATION");
        mainPanel.add(adminPage, "ADMIN");
        mainPanel.add(cartPage, "CART");
        mainPanel.add(checkoutPage, "CHECKOUT");

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
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
