package com.example.smarttrip.model;

import java.util.List;

/** API response model returned to the client. */
public record TripItinerary(
        String destination,
        int budget,
        int durationDays,
        List<String> dailyActivities,
        double estimatedTotalCost,
        boolean withinBudget,
        int planningAttempts,
        List<String> systemLogs) {
}
