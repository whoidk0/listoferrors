package com.example.linguatrain.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.linguatrain.R;
import com.example.linguatrain.models.Lesson;
import com.example.linguatrain.models.Word;
import com.example.linguatrain.utils.DatabaseHelper;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class LessonsAdapter extends RecyclerView.Adapter<LessonsAdapter.LessonViewHolder> {

    private List<Lesson> lessons;
    private OnLessonClickListener listener;

    public interface OnLessonClickListener {
        void onLessonClick(Lesson lesson, List<Word> words);
    }

    public LessonsAdapter(List<Lesson> lessons, OnLessonClickListener listener) {
        this.lessons = lessons;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lesson_card, parent, false);
        return new LessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        Lesson lesson = lessons.get(position);
        holder.tvTitle.setText(lesson.getTitle());

        if (lesson.isUnlocked()) {
            holder.cardView.setAlpha(1.0f);
            holder.itemView.setOnClickListener(v -> {
                DatabaseHelper dbHelper = new DatabaseHelper(holder.itemView.getContext());
                List<Word> words = dbHelper.getWordsForLesson(lesson.getId());
                listener.onLessonClick(lesson, words);
            });
        } else {
            holder.cardView.setAlpha(0.5f);
            holder.itemView.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    static class LessonViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView tvTitle;

        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardLesson);
            tvTitle = itemView.findViewById(R.id.tvLessonTitle);
        }
    }
}