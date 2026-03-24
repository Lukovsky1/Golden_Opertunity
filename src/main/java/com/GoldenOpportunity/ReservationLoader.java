package com.GoldenOpportunity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReservationLoader {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("M/d/yy");

    public List<Reservation> loadReservations(Path csvPath) throws IOException {
        List<String> lines = Files.readAllLines(csvPath);
        List<Reservation> reservations = new ArrayList<>();

        // Skip header
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split(",");

            String ID = parts[0].trim();
            int roomNumber = Integer.parseInt(parts[1].trim());
            LocalDate start = LocalDate.parse(parts[2].trim(), FORMATTER);
            LocalDate end = LocalDate.parse(parts[3].trim(), FORMATTER);

            DateRange range = new DateRange(start, end);

            // Bill is unknown → set to 0 for now
            reservations.add(new Reservation(ID, roomNumber, range, 0.0));
        }

        return reservations;
    }
}