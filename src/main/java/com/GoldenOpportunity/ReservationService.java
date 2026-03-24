package com.GoldenOpportunity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

public class ReservationService {
    private List<Reservation> reserveList = new ArrayList<>();
    private final ReservationLoader loader = new ReservationLoader();

    //Used to read CSV files of the form of the Reservation file


    public void loadData(Path filePath) {
        reserveList.clear(); //May need to be edited, currently assigned for testing
        try {
            reserveList = loader.loadReservations(filePath);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading reservations from file: " + e.getMessage());
        }
    }

    public void createReservation(int roomNumber, LocalDate start, LocalDate end, double bill) {
        String newResId = "R-0" + (reserveList.size() + 1);
        Reservation newRes = new Reservation(newResId, roomNumber, new DateRange(start, end), bill);
        reserveList.add(newRes);
    }

    //TODO: Must be able to write to the database/file and remove/add reservations
    public void deleteReservation(String reservationId) {
        Optional<Reservation> reservation = findReservation(reservationId);
        try {
            reserveList.remove((findReservation(reservationId)));
        }
        catch (NoSuchElementException e) {
            e.printStackTrace();
            System.err.println("Delete: Reservation not found: " + e.getMessage());
        }

    }

    /** Ability to book multiple rooms in a reservation
     * Busca una reservación por su ID.
     * @param reservationId ID de la reservación a buscar
     * @return Optional<Reservation> si existe, vacío si no
     */
    public Optional<Reservation> findReservation(String reservationId) {
        return reserveList.stream()
                .filter(r -> r.getId().equalsIgnoreCase(reservationId))
                .findFirst();
    }

    public List<Reservation> getReservations() {
        return reserveList;
    }

    public static void main (String[] args) {
        ReservationService reservationService = new ReservationService();
        reservationService.loadData(Path.of("src/main/resources/testReservationData1.csv"));



        reservationService.createReservation(3, LocalDate.now(), LocalDate.now(), 0.5);
        reservationService.getReservations().forEach(System.out::println);
        reservationService.deleteReservation("R-001");
        reservationService.getReservations().forEach(System.out::println);
    }



}
