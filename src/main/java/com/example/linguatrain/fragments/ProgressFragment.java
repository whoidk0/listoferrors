package com.example.linguatrain.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.linguatrain.R;
import com.example.linguatrain.activities.LoginActivity;
import com.example.linguatrain.utils.DatabaseHelper;

public class ProgressFragment extends Fragment {

    private DatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress, container, false);

        // 🔴 ОШИБКА 1.1: NullPointerException - getContext() может вернуть null
        // 🔴 ОШИБКА 1.2: DatabaseException - нет обработки ошибок при инициализации БД
        dbHelper = new DatabaseHelper(getContext());

        TextView tvLessonProgress = view.findViewById(R.id.tvLessonProgress);
        TextView tvTestProgress = view.findViewById(R.id.tvTestProgress);
        Button btnLogout = view.findViewById(R.id.btnLogout);

        // 🔴 ОШИБКА 2.1: DatabaseException - нет обработки ошибок при чтении из БД
        // 🔴 ОШИБКА 2.2: NullPointerException - методы могут вернуть null
        int completedLessons = dbHelper.getCompletedLessonsCount();
        int totalLessons = dbHelper.getTotalLessonsCount();
        int completedTests = dbHelper.getCompletedTestsCount();
        int totalTests = dbHelper.getTotalTestsCount();

        // 🔴 ОШИБКА 3.1: Logical Error - нет проверки на отрицательные значения
        // 🔴 ОШИБКА 3.2: String Concatenation Error - может привести к некорректному отображению
        tvLessonProgress.setText("📚 Занятия: выполнено " + completedLessons + "/" + totalLessons);
        tvTestProgress.setText("📝 Тесты: пройдено " + completedTests + "/" + totalTests);

        // 🔴 ОШИБКА 4.1: NullPointerException - getActivity() может вернуть null
        // 🔴 ОШИБКА 4.2: IllegalStateException - фрагмент может быть не привязан
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }
}