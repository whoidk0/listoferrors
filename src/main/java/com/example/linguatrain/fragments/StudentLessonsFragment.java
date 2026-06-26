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
import com.example.linguatrain.adapters.LessonsAdapter;
import com.example.linguatrain.models.Lesson;
import com.example.linguatrain.models.Word;
import com.example.linguatrain.utils.DatabaseHelper;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.List;

public class StudentLessonsFragment extends Fragment {

    private RecyclerView rvLessons;
    private DatabaseHelper dbHelper;
    private List<Lesson> lessonList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_lessons, container, false);
        rvLessons = view.findViewById(R.id.rvLessons);
        rvLessons.setLayoutManager(new LinearLayoutManager(getContext()));

        // 🔴 ОШИБКА 5.1: NullPointerException - getContext() может вернуть null
        // 🔴 ОШИБКА 5.2: DatabaseException - нет обработки ошибок при инициализации
        dbHelper = new DatabaseHelper(getContext());
        loadLessons();

        return view;
    }

    private void loadLessons() {
        // 🔴 ОШИБКА 6.1: DatabaseException - нет обработки ошибок при чтении
        // 🔴 ОШИБКА 6.2: NullPointerException - lessonList может быть null
        lessonList = dbHelper.getAllLessons();
        LessonsAdapter adapter = new LessonsAdapter(lessonList, new LessonsAdapter.OnLessonClickListener() {
            @Override
            public void onLessonClick(Lesson lesson, List<Word> words) {
                // 🔴 ОШИБКА 7.1: NullPointerException - lesson может быть null
                // 🔴 ОШИБКА 7.2: Logical Error - не проверяется, что занятие уже пройдено
                if (lesson.isUnlocked()) {
                    // 🔴 ОШИБКА 8.1: IllegalStateException - requireContext() может выбросить исключение
                    // 🔴 ОШИБКА 8.2: NullPointerException - words может быть null
                    showLessonDialog(lesson.getTitle(), words, lesson.getId());
                }
            }
        });
        rvLessons.setAdapter(adapter);
    }
}