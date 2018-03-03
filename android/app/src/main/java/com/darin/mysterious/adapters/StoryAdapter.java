package com.darin.mysterious.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.darin.mysterious.R;
import com.darin.mysterious.data.StoryData;

import java.util.List;

import nl.siegmann.epublib.domain.Book;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {

    private List<StoryData> stories;

    public StoryAdapter(List<StoryData> stories) {
        this.stories = stories;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_story, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        StoryData story = stories.get(position);
        if (story.isLoading()) {
            holder.cover.setVisibility(View.GONE);
            holder.title.setText("Loading...");
            holder.author.setVisibility(View.GONE);
        } else {
            Book book = story.getBook();
            if (book != null) {
                holder.cover.setVisibility(View.GONE);
                //TODO: book cover
                holder.title.setText(book.getTitle());
                holder.author.setVisibility(View.VISIBLE);
            } else {
                holder.cover.setVisibility(View.GONE);
                //TODO: empty state/error message
            }
        }

        holder.date.setText(story.getDate());
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView cover;
        private TextView title;
        private TextView author;
        private TextView date;

        private ViewHolder(View v) {
            super(v);
            cover = v.findViewById(R.id.cover);
            title = v.findViewById(R.id.title);
            author = v.findViewById(R.id.author);
            date = v.findViewById(R.id.date);
        }
    }

}
