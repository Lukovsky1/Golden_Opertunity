package com.GoldenOpportunity;

import java.time.LocalDate;
import java.util.List;

public class UIState {
    public LocalDate startDate;
    public LocalDate endDate;
    public int numGuests;
    public Room room;
    public String imageFile;
    public ReservationService reservationService;
    public RoomService roomService;
    public List<Room> potentialRooms;
}
