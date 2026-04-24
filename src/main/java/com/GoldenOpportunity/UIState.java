package com.GoldenOpportunity;

import javax.swing.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UIState {
    public LocalDate startDate = LocalDate.now();
    public LocalDate endDate = LocalDate.now();
    public int numGuests;
    public Room room;
    public String imageFile;
    public ReservationService reservationService;
    public RoomService roomService;
    public List<Room> potentialRooms = new ArrayList<>();
    public boolean isLoggedIn;
    private final List<JButton> loginButtons = new ArrayList<>();

    public void registerLoginButton(JButton loginButton) {
        if (loginButton == null) {
            return;
        }
        loginButtons.add(loginButton);
        loginButton.setVisible(!isLoggedIn);
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
        for (JButton loginButton : loginButtons) {
            loginButton.setVisible(!loggedIn);
            if (loginButton.getParent() != null) {
                loginButton.getParent().revalidate();
                loginButton.getParent().repaint();
            }
        }
    }
}
