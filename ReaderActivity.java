package com.booktracker.ui.reader;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.booktracker.R;
import com.booktracker.service.ReadingSessionService;
import com.booktracker.util.FileUtil;

import java.io.IOException;

public class ReaderActivity extends AppCompatActivity {

    private PdfRenderer pdfRenderer;
    private ParcelFileDescriptor fileDescriptor;
    private RecyclerView recyclerView;
    private PdfPageAdapter pageAdapter;
    private TextView tvFileName, tvPageCount;
    private String fileName;
    private String fileType;
    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        setSupportActionBar(findViewById(R.id.toolbar_reader));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tvFileName  = findViewById(R.id.tv_reader_filename);
        tvPageCount = findViewById(R.id.tv_page_count);
        recyclerView = findViewById(R.id.recycler_pdf_pages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fileUri = getIntent().getData();
        if (fileUri == null) {
            Toast.makeText(this, "No file to open", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        fileName = FileUtil.getFileName(this, fileUri);
        fileType = FileUtil.getFileType(this, fileUri);

        if (getSupportActionBar() != null) getSupportActionBar().setTitle(fileName);
        tvFileName.setText(fileName);

        if ("PDF".equals(fileType)) {
            openPdf(fileUri);
        } else {
            // For DOCX, show a message (full DOCX rendering needs Apache POI which is large)
            showDocxMessage();
        }

        // Start tracking
        ReadingSessionService.startTracking(this, fileName, fileUri.toString(), fileType);
    }

    private void openPdf(Uri uri) {
        try {
            fileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
            if (fileDescriptor == null) {
                Toast.makeText(this, "Cannot open file", Toast.LENGTH_SHORT).show();
                return;
            }
            pdfRenderer = new PdfRenderer(fileDescriptor);
            int pageCount = pdfRenderer.getPageCount();
            tvPageCount.setText(pageCount + " pages");

            pageAdapter = new PdfPageAdapter(pdfRenderer);
            recyclerView.setAdapter(pageAdapter);

        } catch (IOException e) {
            Toast.makeText(this, "Error opening PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void showDocxMessage() {
        recyclerView.setVisibility(View.GONE);
        TextView msg = findViewById(R.id.tv_docx_message);
        msg.setVisibility(View.VISIBLE);
        msg.setText("📝 " + fileName + "\n\nDOCX reading is being tracked.\n\n" +
                "Your reading time for this document is being recorded automatically.");
        tvPageCount.setText("DOCX document");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        ReadingSessionService.stopTracking(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pdfRenderer != null) {
            pdfRenderer.close();
        }
        try {
            if (fileDescriptor != null) fileDescriptor.close();
        } catch (IOException ignored) {}
    }
}
