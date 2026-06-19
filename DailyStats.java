package com.booktracker.data.model;

public class DailyStats {
    public String dateKey;       // "2024-06-19"
    public long totalSeconds;
    public int sessionCount;

    public String getFormattedTotal() {
        long mins = totalSeconds / 60;
        if (mins < 60) return mins + " min";
        return (mins / 60) + "h " + (mins % 60) + "m";
    }
}
