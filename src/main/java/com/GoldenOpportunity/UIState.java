package com.GoldenOpportunity;

import com.GoldenOpportunity.Login.Session;
import com.GoldenOpportunity.Login.enums.Role;

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
    public SearchController searchController;
    public List<Room> potentialRooms = new ArrayList<>();
    public boolean isLoggedIn;
    private Session currentSession;
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
        if (!loggedIn) {
            currentSession = null;
        }
        for (JButton loginButton : loginButtons) {
            loginButton.setVisible(!loggedIn);
            if (loginButton.getParent() != null) {
                loginButton.getParent().revalidate();
                loginButton.getParent().repaint();
            }
        }
    }

    public void setCurrentSession(Session currentSession) {
        this.currentSession = currentSession;
        setLoggedIn(currentSession != null);
    }

    public Session getCurrentSession() {
        return currentSession;
    }

    public Role getCurrentRole() {
        return currentSession == null ? null : currentSession.getRole();
    }

    public boolean hasRole(Role role) {
        return role != null && role == getCurrentRole();
    }

}