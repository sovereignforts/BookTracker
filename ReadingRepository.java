package com.booktracker.data.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;

import com.booktracker.data.db.AppDatabase;
import com.booktracker.data.db.ReadingSessionDao;
import com.booktracker.data.model.BookSummary;
import com.booktracker.data.model.DailyStats;
import com.booktracker.data.model.ReadingSession;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReadingRepository {

    private final ReadingSessionDao dao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ReadingRepository(Context context) {
        dao = AppDatabase.getInstance(context).readingSessionDao();
    }

    public void insertSession(ReadingSession session, Callback<Long> callback) {
        executor.execute(() -> {
            long id = dao.insert(session);
            if (callback != null) callback.onResult(id);
        });
    }

    public void updateSession(ReadingSession session) {
        executor.execute(() -> dao.update(session));
    }

    public void closeOpenSession(long endTime) {
        executor.execute(() -> {
            ReadingSession open = dao.getOpenSession();
            if (open != null) {
                open.endTime = endTime;
                open.durationSeconds = (endTime - open.startTime) / 1000;
                dao.update(open);
            }
        });
    }

    public void deleteSession(long id) {
        executor.execute(() -> dao.deleteById(id));
    }

    public LiveData<List<ReadingSession>> getAllSessions() {
        return dao.getAllSessions();
    }

    public LiveData<List<ReadingSession>> getSessionsForDate(String dateKey) {
        return dao.getSessionsForDate(dateKey);
    }

    public LiveData<List<BookSummary>> getAllBookSummaries() {
        return dao.getAllBookSummaries();
    }

    public LiveData<List<BookSummary>> getBookSummariesForDate(String dateKey) {
        return dao.getBookSummariesForDate(dateKey);
    }

    public LiveData<List<DailyStats>> getDailyStats(int days) {
        return dao.getDailyStats(days);
    }

    public LiveData<Long> getTotalSecondsForDate(String dateKey) {
        return dao.getTotalSecondsForDate(dateKey);
    }

    public interface Callback<T> {
        void onResult(T result);
    }
}
