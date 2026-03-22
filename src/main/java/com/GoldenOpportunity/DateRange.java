package com.GoldenOpportunity;

import java.time.LocalDate;

//Used to check overlaps between reservations
public record DateRange(LocalDate startDate, LocalDate endDate) {
    public boolean overlaps(DateRange other) {
        return this.startDate.isBefore(other.endDate) && other.startDate.isBefore(this.endDate);
    }
}
