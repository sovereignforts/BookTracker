package com.booktracker.ui.stats;

import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.booktracker.R;
import com.booktracker.data.model.DailyStats;
import com.booktracker.ui.home.BookSummaryAdapter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StatsActivity extends AppCompatActivity {

    private StatsViewModel viewModel;
    private BarChart barChart;
    private RecyclerView recyclerAllBooks;
    private BookSummaryAdapter allBooksAdapter;
    private TextView tvAllTimeTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        setSupportActionBar(findViewById(R.id.toolbar_stats));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Reading Stats");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        barChart        = findViewById(R.id.bar_chart);
        tvAllTimeTotal  = findViewById(R.id.tv_all_time_total);
        recyclerAllBooks = findViewById(R.id.recycler_all_books);

        allBooksAdapter = new BookSummaryAdapter();
        recyclerAllBooks.setLayoutManager(new LinearLayoutManager(this));
        recyclerAllBooks.setAdapter(allBooksAdapter);

        viewModel = new ViewModelProvider(this,
                new StatsViewModel.Factory(getApplication()))
                .get(StatsViewModel.class);

        viewModel.getAllBookSummaries().observe(this, summaries -> {
            allBooksAdapter.submitList(summaries);
            if (summaries != null) {
                long total = 0;
                for (var b : summaries) total += b.totalSeconds;
                tvAllTimeTotal.setText(formatTotal(total));
            }
        });

        viewModel.getDailyStats().observe(this, stats -> {
            if (stats != null && !stats.isEmpty()) {
                setupChart(stats);
            }
        });
    }

    private void setupChart(List<DailyStats> stats) {
        // Reverse to chronological order
        List<DailyStats> ordered = new ArrayList<>(stats);
        Collections.reverse(ordered);

        ArrayList<BarEntry> entries = new ArrayList<>();
        String[] labels = new String[ordered.size()];

        for (int i = 0; i < ordered.size(); i++) {
            DailyStats d = ordered.get(i);
            float mins = d.totalSeconds / 60f;
            entries.add(new BarEntry(i, mins));
            // Show only MM/dd
            String key = d.dateKey;
            labels[i] = key.length() >= 10 ? key.substring(5) : key;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Minutes read");
        dataSet.setColor(getColor(R.color.accent));
        dataSet.setValueTextColor(getColor(R.color.on_surface));

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.6f);

        barChart.setData(data);
        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true);
        barChart.getLegend().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisLeft().setTextColor(getColor(R.color.on_surface));

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(getColor(R.color.on_surface));
        xAxis.setDrawGridLines(false);

        barChart.invalidate();
    }

    private String formatTotal(long seconds) {
        if (seconds < 60) return seconds + "s total";
        long mins = seconds / 60;
        if (mins < 60) return mins + " min total";
        return (mins / 60) + "h " + (mins % 60) + "m total";
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
