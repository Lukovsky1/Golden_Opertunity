package com.GoldenOpportunity;

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
}
