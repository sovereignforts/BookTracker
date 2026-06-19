package com.booktracker.ui.home;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.*;

import com.booktracker.data.model.BookSummary;
import com.booktracker.data.repository.ReadingRepository;
import com.booktracker.util.DateUtil;

import java.util.List;

public class HomeViewModel extends ViewModel {

    private final ReadingRepository repository;
    private final LiveData<List<BookSummary>> todayBookSummaries;
    private final LiveData<Long> todayTotalSeconds;

    public HomeViewModel(Application app) {
        repository = new ReadingRepository(app);
        String today = DateUtil.todayKey();
        todayBookSummaries = repository.getBookSummariesForDate(today);
        todayTotalSeconds = repository.getTotalSecondsForDate(today);
    }

    public LiveData<List<BookSummary>> getTodayBookSummaries() { return todayBookSummaries; }
    public LiveData<Long> getTodayTotalSeconds() { return todayTotalSeconds; }

    public static class Factory extends ViewModelProvider.AndroidViewModelFactory {
        private final Application app;
        public Factory(Application app) {
            super(app);
            this.app = app;
        }
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(HomeViewModel.class)) {
                //noinspection unchecked
                return (T) new HomeViewModel(app);
            }
            return super.create(modelClass);
        }
    }
}
