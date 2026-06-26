package com.example.linguatrain.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.linguatrain.R;
import com.example.linguatrain.models.Lesson;
import com.example.linguatrain.models.Test;
import com.example.linguatrain.models.Word;
import com.example.linguatrain.utils.DatabaseHelper;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.List;

public class TutorActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private RecyclerView rvLessons, rvTests;
    private Button btnCreateLesson, btnCreateTest, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor);

        dbHelper = new DatabaseHelper(this);
        rvLessons = findViewById(R.id.rvLessons);
        rvTests = findViewById(R.id.rvTests);
        btnCreateLesson = findViewById(R.id.btnCreateLesson);
        btnCreateTest = findViewById(R.id.btnCreateTest);
        btnLogout = findViewById(R.id.btnLogout);

        rvLessons.setLayoutManager(new LinearLayoutManager(this));
        rvTests.setLayoutManager(new LinearLayoutManager(this));

        loadLessons();
        loadTests();

        btnCreateLesson.setOnClickListener(v -> showCreateLessonDialog());
        btnCreateTest.setOnClickListener(v -> showCreateTestDialog());
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void loadLessons() {
        List<Lesson> lessons = dbHelper.getAllLessons();
        LessonsAdapter adapter = new LessonsAdapter(lessons);
        rvLessons.setAdapter(adapter);
    }

    private void loadTests() {
        List<Test> tests = dbHelper.getAllTests();
        TestsAdapter adapter = new TestsAdapter(tests);
        rvTests.setAdapter(adapter);
    }

    // --- Диалог создания занятия ---
    private void showCreateLessonDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Создать занятие");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_lesson, null);
        builder.setView(dialogView);

        EditText etTitle = dialogView.findViewById(R.id.etLessonTitle);
        LinearLayout wordsContainer = dialogView.findViewById(R.id.wordsContainer);
        Button btnAddWord = dialogView.findViewById(R.id.btnAddWord);

        btnAddWord.setOnClickListener(v -> {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(0, 8, 0, 8);

            EditText etEn = new EditText(this);
            etEn.setHint("English");
            etEn.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

            EditText etRu = new EditText(this);
            etRu.setHint("Русский");
            etRu.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

            row.addView(etEn);
            row.addView(etRu);
            wordsContainer.addView(row);
        });

        builder.setPositiveButton("Создать", (dialog, which) -> {
            String title = etTitle.getText().toString().trim();
            if (!title.isEmpty()) {
                long lessonId = dbHelper.createLesson(title);
                for (int i = 0; i < wordsContainer.getChildCount(); i++) {
                    LinearLayout row = (LinearLayout) wordsContainer.getChildAt(i);
                    EditText etEn = (EditText) row.getChildAt(0);
                    EditText etRu = (EditText) row.getChildAt(1);
                    String en = etEn.getText().toString().trim();
                    String ru = etRu.getText().toString().trim();
                    if (!en.isEmpty() && !ru.isEmpty()) {
                        dbHelper.addWordToLesson(String.valueOf(lessonId), en, ru);
                    }
                }
                loadLessons();
            }
        });
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    // --- Диалог редактирования занятия ---
    private void showEditLessonDialog(Lesson lesson) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Редактировать занятие");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_lesson, null);
        builder.setView(dialogView);

        EditText etTitle = dialogView.findViewById(R.id.etLessonTitle);
        LinearLayout wordsContainer = dialogView.findViewById(R.id.wordsContainer);
        Button btnAddWord = dialogView.findViewById(R.id.btnAddWord);

        etTitle.setText(lesson.getTitle());

        List<Word> existingWords = dbHelper.getWordsForLesson(lesson.getId());
        for (Word word : existingWords) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(0, 8, 0, 8);

            EditText etEn = new EditText(this);
            etEn.setText(word.getEnWord());
            etEn.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

            EditText etRu = new EditText(this);
            etRu.setText(word.getRuTranslate());
            etRu.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

            row.addView(etEn);
            row.addView(etRu);
            wordsContainer.addView(row);
        }

        btnAddWord.setOnClickListener(v -> {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(0, 8, 0, 8);

            EditText etEn = new EditText(this);
            etEn.setHint("English");
            etEn.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

            EditText etRu = new EditText(this);
            etRu.setHint("Русский");
            etRu.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

            row.addView(etEn);
            row.addView(etRu);
            wordsContainer.addView(row);
        });

        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            String title = etTitle.getText().toString().trim();
            if (!title.isEmpty()) {
                dbHelper.updateLesson(lesson.getId(), title);
                dbHelper.clearWordsForLesson(lesson.getId());
                for (int i = 0; i < wordsContainer.getChildCount(); i++) {
                    LinearLayout row = (LinearLayout) wordsContainer.getChildAt(i);
                    EditText etEn = (EditText) row.getChildAt(0);
                    EditText etRu = (EditText) row.getChildAt(1);
                    String en = etEn.getText().toString().trim();
                    String ru = etRu.getText().toString().trim();
                    if (!en.isEmpty() && !ru.isEmpty()) {
                        dbHelper.addWordToLesson(lesson.getId(), en, ru);
                    }
                }
                loadLessons();
            }
        });
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    // --- Диалог создания теста ---
    private void showCreateTestDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Создать тест");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_test, null);
        builder.setView(dialogView);

        EditText etTitle = dialogView.findViewById(R.id.etTestTitle);
        LinearLayout questionsContainer = dialogView.findViewById(R.id.questionsContainer);
        Button btnAddQuestion = dialogView.findViewById(R.id.btnAddQuestion);

        btnAddQuestion.setOnClickListener(v -> {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.VERTICAL);
            row.setPadding(0, 8, 0, 8);

            EditText etQuestion = new EditText(this);
            etQuestion.setHint("Вопрос");
            etQuestion.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            LinearLayout answersRow = new LinearLayout(this);
            answersRow.setOrientation(LinearLayout.HORIZONTAL);

            EditText etCorrect = new EditText(this);
            etCorrect.setHint("✅ Правильный");
            etCorrect.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

            EditText etWrong1 = new EditText(this);
            etWrong1.setHint("❌ Неправильный 1");
            etWrong1.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

            EditText etWrong2 = new EditText(this);
            etWrong2.setHint("❌ Неправильный 2");
            etWrong2.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

            EditText etWrong3 = new EditText(this);
            etWrong3.setHint("❌ Неправильный 3");
            etWrong3.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

            answersRow.addView(etCorrect);
            answersRow.addView(etWrong1);
            answersRow.addView(etWrong2);
            answersRow.addView(etWrong3);

            row.addView(etQuestion);
            row.addView(answersRow);
            questionsContainer.addView(row);
        });

        builder.setPositiveButton("Создать", (dialog, which) -> {
            String title = etTitle.getText().toString().trim();
            if (!title.isEmpty()) {
                long testId = dbHelper.createTest(title, "1"); // Привязываем к первому занятию
                for (int i = 0; i < questionsContainer.getChildCount(); i++) {
                    LinearLayout row = (LinearLayout) questionsContainer.getChildAt(i);
                    EditText etQuestion = (EditText) row.getChildAt(0);
                    LinearLayout answersRow = (LinearLayout) row.getChildAt(1);
                    EditText etCorrect = (EditText) answersRow.getChildAt(0);
                    EditText etWrong1 = (EditText) answersRow.getChildAt(1);
                    EditText etWrong2 = (EditText) answersRow.getChildAt(2);
                    EditText etWrong3 = (EditText) answersRow.getChildAt(3);

                    String question = etQuestion.getText().toString().trim();
                    String correct = etCorrect.getText().toString().trim();
                    String w1 = etWrong1.getText().toString().trim();
                    String w2 = etWrong2.getText().toString().trim();
                    String w3 = etWrong3.getText().toString().trim();

                    if (!question.isEmpty() && !correct.isEmpty()) {
                        dbHelper.addQuestionToTest(String.valueOf(testId), question, correct, w1, w2, w3);
                    }
                }
                loadTests();
            }
        });
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    // --- Диалог редактирования теста ---
    private void showEditTestDialog(Test test) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Редактировать тест");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_test, null);
        builder.setView(dialogView);

        EditText etTitle = dialogView.findViewById(R.id.etTestTitle);
        LinearLayout questionsContainer = dialogView.findViewById(R.id.questionsContainer);
        Button btnAddQuestion = dialogView.findViewById(R.id.btnAddQuestion);

        etTitle.setText(test.getTitle());

        List<Test> existingQuestions = dbHelper.getQuestionsForTest(test.getId());
        for (Test q : existingQuestions) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.VERTICAL);
            row.setPadding(0, 8, 0, 8);

            EditText etQuestion = new EditText(this);
            etQuestion.setText(q.getQuestion());
            etQuestion.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            LinearLayout answersRow = new LinearLayout(this);
            answersRow.setOrientation(LinearLayout.HORIZONTAL);

            EditText etCorrect = new EditText(this);
            etCorrect.setText(q.getCorrectAnswer());
            etCorrect.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

            EditText etWrong1 = new EditText(this);
            etWrong1.setText(q.getWrongAnswer1());
            etWrong1.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

            EditText etWrong2 = new EditText(this);
            etWrong2.setText(q.getWrongAnswer2());
            etWrong2.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

            EditText etWrong3 = new EditText(this);
            etWrong3.setText(q.getWrongAnswer3());
            etWrong3.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

            answersRow.addView(etCorrect);
            answersRow.addView(etWrong1);
            answersRow.addView(etWrong2);
            answersRow.addView(etWrong3);

            row.addView(etQuestion);
            row.addView(answersRow);
            questionsContainer.addView(row);
        }

        btnAddQuestion.setOnClickListener(v -> {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.VERTICAL);
            row.setPadding(0, 8, 0, 8);

            EditText etQuestion = new EditText(this);
            etQuestion.setHint("Вопрос");
            etQuestion.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            LinearLayout answersRow = new LinearLayout(this);
            answersRow.setOrientation(LinearLayout.HORIZONTAL);

            EditText etCorrect = new EditText(this);
            etCorrect.setHint("✅ Правильный");
            etCorrect.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

            EditText etWrong1 = new EditText(this);
            etWrong1.setHint("❌ Неправильный 1");
            etWrong1.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

            EditText etWrong2 = new EditText(this);
            etWrong2.setHint("❌ Неправильный 2");
            etWrong2.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

            EditText etWrong3 = new EditText(this);
            etWrong3.setHint("❌ Неправильный 3");
            etWrong3.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

            answersRow.addView(etCorrect);
            answersRow.addView(etWrong1);
            answersRow.addView(etWrong2);
            answersRow.addView(etWrong3);

            row.addView(etQuestion);
            row.addView(answersRow);
            questionsContainer.addView(row);
        });

        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            String title = etTitle.getText().toString().trim();
            if (!title.isEmpty()) {
                dbHelper.updateTest(test.getId(), title);
                dbHelper.clearQuestionsForTest(test.getId());
                for (int i = 0; i < questionsContainer.getChildCount(); i++) {
                    LinearLayout row = (LinearLayout) questionsContainer.getChildAt(i);
                    EditText etQuestion = (EditText) row.getChildAt(0);
                    LinearLayout answersRow = (LinearLayout) row.getChildAt(1);
                    EditText etCorrect = (EditText) answersRow.getChildAt(0);
                    EditText etWrong1 = (EditText) answersRow.getChildAt(1);
                    EditText etWrong2 = (EditText) answersRow.getChildAt(2);
                    EditText etWrong3 = (EditText) answersRow.getChildAt(3);

                    String question = etQuestion.getText().toString().trim();
                    String correct = etCorrect.getText().toString().trim();
                    String w1 = etWrong1.getText().toString().trim();
                    String w2 = etWrong2.getText().toString().trim();
                    String w3 = etWrong3.getText().toString().trim();

                    if (!question.isEmpty() && !correct.isEmpty()) {
                        dbHelper.addQuestionToTest(test.getId(), question, correct, w1, w2, w3);
                    }
                }
                loadTests();
            }
        });
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    // --- АДАПТЕР ЗАНЯТИЙ ---
    class LessonsAdapter extends RecyclerView.Adapter<LessonsAdapter.LessonViewHolder> {
        private List<Lesson> lessons;

        public LessonsAdapter(List<Lesson> lessons) {
            this.lessons = lessons;
        }

        @NonNull
        @Override
        public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lesson_tutor, parent, false);
            return new LessonViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
            Lesson lesson = lessons.get(position);
            holder.tvTitle.setText(lesson.getTitle());
            if (dbHelper.isLessonCompleted(lesson.getId())) {
                holder.cardView.setCardBackgroundColor(getResources().getColor(R.color.green_light, null));
                holder.tvStatus.setText("✅ Выполнено");
            } else {
                holder.cardView.setCardBackgroundColor(getResources().getColor(R.color.red_light, null));
                holder.tvStatus.setText("❌ Не выполнено");
            }
            holder.btnEdit.setOnClickListener(v -> showEditLessonDialog(lesson));
            holder.btnDelete.setOnClickListener(v -> {
                new MaterialAlertDialogBuilder(TutorActivity.this)
                        .setTitle("Удалить занятие")
                        .setMessage("Вы уверены, что хотите удалить занятие \"" + lesson.getTitle() + "\"?")
                        .setPositiveButton("Удалить", (dialog, which) -> {
                            dbHelper.deleteLesson(lesson.getId());
                            lessons.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, lessons.size());
                        })
                        .setNegativeButton("Отмена", null)
                        .show();
            });
        }

        @Override
        public int getItemCount() {
            return lessons.size();
        }

        class LessonViewHolder extends RecyclerView.ViewHolder {
            MaterialCardView cardView;
            TextView tvTitle, tvStatus;
            ImageView btnEdit, btnDelete;

            public LessonViewHolder(@NonNull View itemView) {
                super(itemView);
                cardView = itemView.findViewById(R.id.cardLesson);
                tvTitle = itemView.findViewById(R.id.tvLessonTitle);
                tvStatus = itemView.findViewById(R.id.tvStatus);
                btnEdit = itemView.findViewById(R.id.btnEdit);
                btnDelete = itemView.findViewById(R.id.btnDelete);
            }
        }
    }

    // --- АДАПТЕР ТЕСТОВ ---
    class TestsAdapter extends RecyclerView.Adapter<TestsAdapter.TestViewHolder> {
        private List<Test> tests;

        public TestsAdapter(List<Test> tests) {
            this.tests = tests;
        }

        @NonNull
        @Override
        public TestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_test_tutor, parent, false);
            return new TestViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TestViewHolder holder, int position) {
            Test test = tests.get(position);
            holder.tvTitle.setText(test.getTitle());
            if (dbHelper.isTestCompleted(test.getId())) {
                holder.cardView.setCardBackgroundColor(getResources().getColor(R.color.green_light, null));
                holder.tvStatus.setText("✅ Выполнено");
            } else {
                holder.cardView.setCardBackgroundColor(getResources().getColor(R.color.red_light, null));
                holder.tvStatus.setText("❌ Не выполнено");
            }
            holder.btnEdit.setOnClickListener(v -> showEditTestDialog(test));
            holder.btnDelete.setOnClickListener(v -> {
                new MaterialAlertDialogBuilder(TutorActivity.this)
                        .setTitle("Удалить тест")
                        .setMessage("Вы уверены, что хотите удалить тест \"" + test.getTitle() + "\"?")
                        .setPositiveButton("Удалить", (dialog, which) -> {
                            dbHelper.deleteTest(test.getId());
                            tests.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, tests.size());
                        })
                        .setNegativeButton("Отмена", null)
                        .show();
            });
        }

        @Override
        public int getItemCount() {
            return tests.size();
        }

        class TestViewHolder extends RecyclerView.ViewHolder {
            MaterialCardView cardView;
            TextView tvTitle, tvStatus;
            ImageView btnEdit, btnDelete;

            public TestViewHolder(@NonNull View itemView) {
                super(itemView);
                cardView = itemView.findViewById(R.id.cardTest);
                tvTitle = itemView.findViewById(R.id.tvTestTitle);
                tvStatus = itemView.findViewById(R.id.tvStatus);
                btnEdit = itemView.findViewById(R.id.btnEdit);
                btnDelete = itemView.findViewById(R.id.btnDelete);
            }
        }
    }
}