package com.booktracker.ui.stats;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.*;

import com.booktracker.data.model.BookSummary;
import com.booktracker.data.model.DailyStats;
import com.booktracker.data.repository.ReadingRepository;

import java.util.List;

public class StatsViewModel extends ViewModel {

    private final ReadingRepository repository;
    private final LiveData<List<BookSummary>> allBookSummaries;
    private final LiveData<List<DailyStats>> dailyStats;

    public StatsViewModel(Application app) {
        repository = new ReadingRepository(app);
        allBookSummaries = repository.getAllBookSummaries();
        dailyStats = repository.getDailyStats(14); // last 14 days
    }

    public LiveData<List<BookSummary>> getAllBookSummaries() { return allBookSummaries; }
    public LiveData<List<DailyStats>> getDailyStats() { return dailyStats; }

    public static class Factory extends ViewModelProvider.AndroidViewModelFactory {
        private final Application app;
        public Factory(Application app) { super(app); this.app = app; }
        @NonNull @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(StatsViewModel.class)) {
                //noinspection unchecked
                return (T) new StatsViewModel(app);
            }
            return super.create(modelClass);
        }
    }
}
