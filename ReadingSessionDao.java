package com.booktracker.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import com.booktracker.data.model.BookSummary;
import com.booktracker.data.model.DailyStats;
import com.booktracker.data.model.ReadingSession;

import java.util.List;

@Dao
public interface ReadingSessionDao {

    @Insert
    long insert(ReadingSession session);

    @Update
    void update(ReadingSession session);

    @Delete
    void delete(ReadingSession session);

    // Get all sessions ordered by most recent
    @Query("SELECT * FROM reading_sessions ORDER BY startTime DESC")
    LiveData<List<ReadingSession>> getAllSessions();

    // Get today's sessions
    @Query("SELECT * FROM reading_sessions WHERE dateKey = :dateKey ORDER BY startTime DESC")
    LiveData<List<ReadingSession>> getSessionsForDate(String dateKey);

    // Get the currently open session (endTime = 0)
    @Query("SELECT * FROM reading_sessions WHERE endTime = 0 LIMIT 1")
    ReadingSession getOpenSession();

    // Per-book totals (all time)
    @Query("SELECT fileName, fileType, " +
           "SUM(durationSeconds) as totalSeconds, " +
           "COUNT(*) as sessionCount, " +
           "MAX(startTime) as lastRead " +
           "FROM reading_sessions WHERE durationSeconds > 0 " +
           "GROUP BY fileName ORDER BY totalSeconds DESC")
    LiveData<List<BookSummary>> getAllBookSummaries();

    // Per-book totals for a specific date
    @Query("SELECT fileName, fileType, " +
           "SUM(durationSeconds) as totalSeconds, " +
           "COUNT(*) as sessionCount, " +
           "MAX(startTime) as lastRead " +
           "FROM reading_sessions WHERE dateKey = :dateKey AND durationSeconds > 0 " +
           "GROUP BY fileName ORDER BY totalSeconds DESC")
    LiveData<List<BookSummary>> getBookSummariesForDate(String dateKey);

    // Daily totals for the last N days (for chart)
    @Query("SELECT dateKey, SUM(durationSeconds) as totalSeconds, COUNT(*) as sessionCount " +
           "FROM reading_sessions WHERE durationSeconds > 0 " +
           "GROUP BY dateKey ORDER BY dateKey DESC LIMIT :days")
    LiveData<List<DailyStats>> getDailyStats(int days);

    // Total reading time today
    @Query("SELECT COALESCE(SUM(durationSeconds), 0) FROM reading_sessions WHERE dateKey = :dateKey AND durationSeconds > 0")
    LiveData<Long> getTotalSecondsForDate(String dateKey);

    // Delete session by id
    @Query("DELETE FROM reading_sessions WHERE id = :id")
    void deleteById(long id);
}
