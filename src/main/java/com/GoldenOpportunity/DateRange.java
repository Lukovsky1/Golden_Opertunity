package com.GoldenOpportunity;

import java.time.LocalDate;

//Used to check overlaps between reservations
public record DateRange(LocalDate startDate, LocalDate endDate) {
    public boolean overlaps(DateRange other) {
        return this.startDate.isBefore(other.endDate) && other.startDate.isBefore(this.endDate);
    }

    /**
     * validateRange: Takes two dates and confirms that they comprise a valid
     * time span. Example use: start(12/1/2026) with end(11/30/26) would return false.
     * @param startDate
     * @param endDate
     * @return
     */
    public static boolean validateRange(LocalDate startDate, LocalDate endDate) {
        return startDate.isBefore(endDate) || startDate.isEqual(endDate);
    }

    @Override
    public String toString() {
        return "Dates: " + startDate + " - " + endDate;
    }
}
