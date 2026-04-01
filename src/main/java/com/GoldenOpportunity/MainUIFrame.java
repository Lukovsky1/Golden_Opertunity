package com.GoldenOpportunity;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;

// New main frame for all of the UI

public class MainUIFrame extends JFrame {

    public MainUIFrame() throws IOException {
        CardLayout cardLayout = new CardLayout();
        JPanel mainPanel = new JPanel(cardLayout);

        HotelHomePageUI hotelHomePageUI = new HotelHomePageUI(cardLayout, mainPanel);
        HotelBookingUI hotelBookingUI = new HotelBookingUI(cardLayout,mainPanel,
                hotelHomePageUI.getRoomService(),hotelHomePageUI.getReservationService());

        mainPanel.add(hotelHomePageUI, "HOME");
        mainPanel.add(hotelBookingUI, "ROOMS");

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
