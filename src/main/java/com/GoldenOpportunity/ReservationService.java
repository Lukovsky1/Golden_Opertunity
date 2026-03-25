package com.GoldenOpportunity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

public class ReservationService {
    private final List<Reservation> reserveList = new ArrayList<>();
    private final Map<String, Reservation> reservationMap = new HashMap<>();
    private final ReservationLoader loader = new ReservationLoader();

    //Used to read CSV files of the form of the Reservation file


    public void loadData(Path filePath) {
        reserveList.clear();
        reservationMap.clear();

        try {
            List<Reservation> loaded = loader.loadReservations(filePath);
            reserveList.addAll(loaded);

            // Populate map
            for (Reservation r : loaded) {
                reservationMap.put(r.getId().toUpperCase(), r);
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading reservations from file: " + e.getMessage());
        }
    }

    public void createReservation(int roomNumber, LocalDate start, LocalDate end, double bill) {
        String newResId = generateReservationId();

        Reservation newRes = new Reservation(
                newResId,
                roomNumber,
                new DateRange(start, end),
                bill
        );

        reserveList.add(newRes);
        reservationMap.put(newResId.toUpperCase(), newRes);
    }

    private String generateReservationId() {
        int next = reserveList.size() + 1;
        return String.format("R-%03d", next);
    }

    //TODO: Must be able to write to the database/file and remove/add reservations
    public void deleteReservation(String reservationId) {
        String key = reservationId.toUpperCase();
        Reservation found = reservationMap.remove(key);

        if (found == null) {
            System.err.println("Delete: Reservation not found: " + reservationId);
            return false;
        }

        reserveList.remove(found);
        return true;
    }

    /** Ability to book multiple rooms in a reservation
     * Busca una reservación por su ID.
     * @param reservationId ID de la reservación a buscar
     * @return Optional<Reservation> si existe, vacío si no
     */
    public Optional<Reservation> findReservation(String reservationId) {
        if (reservationId == null || reservationId.isBlank()) {
            return Optional.empty();
        }

        return Optional.ofNullable(reservationMap.get(reservationId.toUpperCase()));
    }

    public List<Reservation> getReservations() {
        return Collections.unmodifiableList(reserveList);
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