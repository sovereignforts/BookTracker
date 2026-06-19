package com.booktracker.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "reading_sessions")
public class ReadingSession {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String fileName;       // e.g. "Chapter1.pdf"
    public String filePath;       // full path or URI
    public String fileType;       // "PDF" or "DOCX"
    public long startTime;        // epoch millis
    public long endTime;          // epoch millis (0 = still reading)
    public long durationSeconds;  // computed at close

    // Date string for easy daily grouping: "2024-06-19"
    public String dateKey;

    public ReadingSession() {}

    public ReadingSession(String fileName, String filePath, String fileType, long startTime, String dateKey) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileType = fileType;
        this.startTime = startTime;
        this.dateKey = dateKey;
        this.endTime = 0;
        this.durationSeconds = 0;
    }

    public String getFormattedDuration() {
        long secs = durationSeconds;
        if (secs < 60) return secs + "s";
        long mins = secs / 60;
        if (mins < 60) return mins + "m " + (secs % 60) + "s";
        return (mins / 60) + "h " + (mins % 60) + "m";
    }
}
