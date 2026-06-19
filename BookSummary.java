package com.booktracker.data.model;

/**
 * Aggregated stats per book — returned by DAO queries.
 */
public class BookSummary {
    public String fileName;
    public String fileType;
    public long totalSeconds;
    public int sessionCount;
    public long lastRead; // epoch millis

    public String getFormattedTotal() {
        long secs = totalSeconds;
        if (secs < 60) return secs + "s";
        long mins = secs / 60;
        if (mins < 60) return mins + " min";
        return (mins / 60) + "h " + (mins % 60) + "m";
    }

    public String getFileEmoji() {
        if ("PDF".equals(fileType)) return "📄";
        if ("DOCX".equals(fileType)) return "📝";
        return "📃";
    }
}
