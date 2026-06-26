package com.example.linguatrain.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.linguatrain.R;
import com.example.linguatrain.models.Test;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class TestsAdapter extends RecyclerView.Adapter<TestsAdapter.TestViewHolder> {

    private List<Test> testList;
    private OnTestClickListener listener;

    public interface OnTestClickListener {
        void onTestClick(Test test);
    }

    public TestsAdapter(List<Test> testList, OnTestClickListener listener) {
        this.testList = testList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_test_card, parent, false);
        return new TestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TestViewHolder holder, int position) {
        Test test = testList.get(position);
        holder.tvTitle.setText(test.getTitle());
        holder.itemView.setOnClickListener(v -> listener.onTestClick(test));
    }

    @Override
    public int getItemCount() {
        return testList.size();
    }

    static class TestViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView tvTitle, tvQuestion;

        public TestViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardTest);
            tvTitle = itemView.findViewById(R.id.tvTestTitle);
        }
    }
}