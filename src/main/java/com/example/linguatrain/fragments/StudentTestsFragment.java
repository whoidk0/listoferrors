package com.example.linguatrain.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.linguatrain.R;
import com.example.linguatrain.adapters.TestsAdapter;
import com.example.linguatrain.models.Test;
import com.example.linguatrain.utils.DatabaseHelper;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StudentTestsFragment extends Fragment {

    private RecyclerView rvTests;
    private DatabaseHelper dbHelper;
    private List<Test> testList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_tests, container, false);
        rvTests = view.findViewById(R.id.rvTests);
        rvTests.setLayoutManager(new LinearLayoutManager(getContext()));

        dbHelper = new DatabaseHelper(getContext());
        loadTests();

        return view;
    }

    private void loadTests() {
        testList = dbHelper.getAllTests();
        TestsAdapter adapter = new TestsAdapter(testList, new TestsAdapter.OnTestClickListener() {
            @Override
            public void onTestClick(Test test) {
                if (!dbHelper.isLessonCompleted(String.valueOf(test.getLessonId()))) {
                    new MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Доступ запрещён")
                            .setMessage("Сначала пройдите занятие по этой теме!")
                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                            .show();
                    return;
                }
                if (!dbHelper.isTestUnlocked(test.getId())) {
                    new MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Тест заблокирован")
                            .setMessage("Сначала пройдите предыдущий тест!")
                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                            .show();
                    return;
                }
                List<Test> questions = dbHelper.getQuestionsForTest(test.getId());
                if (!questions.isEmpty()) {
                    showTestDialog(test.getTitle(), questions, test.getId());
                }
            }
        });
        rvTests.setAdapter(adapter);
    }

    private void showTestDialog(String testTitle, List<Test> questions, String testId) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle("📝 " + testTitle);

        LinearLayout dialogLayout = new LinearLayout(getContext());
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setPadding(16, 16, 16, 16);

        final int[] currentIndex = {0};
        final Button[] saveButton = {null};
        final boolean[] answered = {false};

        TextView questionText = new TextView(getContext());
        questionText.setText(questions.get(0).getQuestion());
        questionText.setTextSize(18);
        questionText.setTextColor(getResources().getColor(R.color.black, null));
        questionText.setPadding(0, 0, 0, 16);
        dialogLayout.addView(questionText);

        LinearLayout optionsLayout = new LinearLayout(getContext());
        optionsLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.addView(optionsLayout);

        Button nextButton = new Button(getContext());
        nextButton.setText("Далее");
        nextButton.setBackgroundTintList(getResources().getColorStateList(R.color.pastel_primary, null));
        nextButton.setTextColor(getResources().getColor(R.color.white, null));
        nextButton.setAllCaps(false);
        nextButton.setOnClickListener(v -> {
            if (currentIndex[0] < questions.size() - 1) {
                currentIndex[0]++;
                questionText.setText(questions.get(currentIndex[0]).getQuestion());
                updateOptions(optionsLayout, questions.get(currentIndex[0]));
                answered[0] = false;
                nextButton.setText("Далее");
            } else {
                nextButton.setVisibility(View.GONE);
                if (saveButton[0] != null) {
                    saveButton[0].setVisibility(View.VISIBLE);
                }
            }
        });

        saveButton[0] = new Button(getContext());
        saveButton[0].setText("✅ Сохранить прогресс");
        saveButton[0].setBackgroundTintList(getResources().getColorStateList(R.color.green_light, null));
        saveButton[0].setTextColor(getResources().getColor(R.color.black, null));
        saveButton[0].setAllCaps(false);
        saveButton[0].setVisibility(View.GONE);
        saveButton[0].setOnClickListener(v -> {
            dbHelper.markTestCompleted(testId);
            dbHelper.unlockNextTest(testId);
            builder.create().dismiss();
            loadTests();
        });

        updateOptions(optionsLayout, questions.get(0));

        dialogLayout.addView(nextButton);
        dialogLayout.addView(saveButton[0]);

        builder.setView(dialogLayout);
        builder.setNegativeButton("Закрыть", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void updateOptions(LinearLayout optionsLayout, Test question) {
        optionsLayout.removeAllViews();

        List<String> options = new ArrayList<>();
        options.add(question.getCorrectAnswer());
        if (question.getWrongAnswer1() != null && !question.getWrongAnswer1().isEmpty()) {
            options.add(question.getWrongAnswer1());
        }
        if (question.getWrongAnswer2() != null && !question.getWrongAnswer2().isEmpty()) {
            options.add(question.getWrongAnswer2());
        }
        if (question.getWrongAnswer3() != null && !question.getWrongAnswer3().isEmpty()) {
            options.add(question.getWrongAnswer3());
        }

        Collections.shuffle(options);

        for (String option : options) {
            if (option != null && !option.isEmpty()) {
                Button optionButton = new Button(getContext());
                optionButton.setText(option);
                optionButton.setBackgroundTintList(getResources().getColorStateList(R.color.white, null));
                optionButton.setTextColor(getResources().getColor(R.color.black, null));
                optionButton.setAllCaps(false);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 0, 0, 8);
                optionButton.setLayoutParams(params);

                optionButton.setOnClickListener(v -> {
                    for (int i = 0; i < optionsLayout.getChildCount(); i++) {
                        Button btn = (Button) optionsLayout.getChildAt(i);
                        btn.setBackgroundTintList(getResources().getColorStateList(R.color.white, null));
                        btn.setTextColor(getResources().getColor(R.color.black, null));
                    }
                    if (option.equals(question.getCorrectAnswer())) {
                        optionButton.setBackgroundTintList(getResources().getColorStateList(R.color.green_light, null));
                        optionButton.setTextColor(getResources().getColor(R.color.black, null));
                    } else {
                        optionButton.setBackgroundTintList(getResources().getColorStateList(R.color.red_light, null));
                        optionButton.setTextColor(getResources().getColor(R.color.white, null));
                    }
                });

                optionsLayout.addView(optionButton);
            }
        }
    }
}