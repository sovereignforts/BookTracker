package com.booktracker.ui.home;

import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.booktracker.R;
import com.booktracker.data.model.BookSummary;

public class BookSummaryAdapter extends ListAdapter<BookSummary, BookSummaryAdapter.VH> {

    public BookSummaryAdapter() {
        super(DIFF);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book_summary, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bind(getItem(position));
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvEmoji, tvName, tvType, tvTime, tvSessions;

        VH(View v) {
            super(v);
            tvEmoji    = v.findViewById(R.id.tv_emoji);
            tvName     = v.findViewById(R.id.tv_book_name);
            tvType     = v.findViewById(R.id.tv_file_type);
            tvTime     = v.findViewById(R.id.tv_read_time);
            tvSessions = v.findViewById(R.id.tv_sessions);
        }

        void bind(BookSummary b) {
            tvEmoji.setText(b.getFileEmoji());
            tvName.setText(b.fileName);
            tvType.setText(b.fileType);
            tvTime.setText(b.getFormattedTotal());
            tvSessions.setText(b.sessionCount + (b.sessionCount == 1 ? " session" : " sessions"));
        }
    }

    private static final DiffUtil.ItemCallback<BookSummary> DIFF =
            new DiffUtil.ItemCallback<BookSummary>() {
                @Override
                public boolean areItemsTheSame(@NonNull BookSummary a, @NonNull BookSummary b) {
                    return a.fileName.equals(b.fileName);
                }
                @Override
                public boolean areContentsTheSame(@NonNull BookSummary a, @NonNull BookSummary b) {
                    return a.totalSeconds == b.totalSeconds && a.sessionCount == b.sessionCount;
                }
            };
}
