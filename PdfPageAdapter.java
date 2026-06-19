package com.booktracker.ui.reader;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.view.*;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.booktracker.R;

public class PdfPageAdapter extends RecyclerView.Adapter<PdfPageAdapter.PageVH> {

    private final PdfRenderer renderer;

    public PdfPageAdapter(PdfRenderer renderer) {
        this.renderer = renderer;
    }

    @NonNull
    @Override
    public PageVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pdf_page, parent, false);
        return new PageVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PageVH holder, int position) {
        holder.render(renderer, position);
    }

    @Override
    public int getItemCount() {
        return renderer.getPageCount();
    }

    static class PageVH extends RecyclerView.ViewHolder {
        ImageView imageView;

        PageVH(View v) {
            super(v);
            imageView = v.findViewById(R.id.iv_page);
        }

        void render(PdfRenderer renderer, int index) {
            PdfRenderer.Page page = renderer.openPage(index);
            int width  = itemView.getResources().getDisplayMetrics().widthPixels;
            int height = (int) ((float) page.getHeight() / page.getWidth() * width);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            page.close();
            imageView.setImageBitmap(bitmap);
        }
    }
}
