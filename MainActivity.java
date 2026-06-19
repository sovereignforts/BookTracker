package com.booktracker.ui.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.booktracker.R;
import com.booktracker.ui.reader.ReaderActivity;
import com.booktracker.ui.stats.StatsActivity;
import com.booktracker.util.DateUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private HomeViewModel viewModel;
    private BookSummaryAdapter adapter;
    private TextView tvTodayTotal, tvDate, tvEmpty;
    private RecyclerView recyclerView;

    private final ActivityResultLauncher<String[]> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {});

    private final ActivityResultLauncher<String[]> filePicker =
            registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri != null) openFileInReader(uri);
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("BookTracker");
        }

        tvTodayTotal = findViewById(R.id.tv_today_total);
        tvDate       = findViewById(R.id.tv_date);
        tvEmpty      = findViewById(R.id.tv_empty);
        recyclerView = findViewById(R.id.recycler_books);

        tvDate.setText(DateUtil.toDisplayDate(DateUtil.todayKey()));

        adapter = new BookSummaryAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this,
                new HomeViewModel.Factory(getApplication()))
                .get(HomeViewModel.class);

        viewModel.getTodayBookSummaries().observe(this, summaries -> {
            adapter.submitList(summaries);
            tvEmpty.setVisibility(summaries == null || summaries.isEmpty() ? View.VISIBLE : View.GONE);
        });

        viewModel.getTodayTotalSeconds().observe(this, secs -> {
            if (secs == null || secs == 0) {
                tvTodayTotal.setText("0 min");
            } else {
                tvTodayTotal.setText(DateUtil.formatSeconds(secs));
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab_open);
        fab.setOnClickListener(v -> openFilePicker());

        requestPermissions();
    }

    private void openFilePicker() {
        filePicker.launch(new String[]{
                "application/pdf",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        });
    }

    private void openFileInReader(Uri uri) {
        Intent intent = new Intent(this, ReaderActivity.class);
        intent.setData(uri);
        startActivity(intent);
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(new String[]{
                    Manifest.permission.READ_MEDIA_DOCUMENTS,
                    Manifest.permission.POST_NOTIFICATIONS
            });
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_stats) {
            startActivity(new Intent(this, StatsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
