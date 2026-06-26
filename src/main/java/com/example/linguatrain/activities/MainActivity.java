package com.example.linguatrain.activities;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.linguatrain.R;
import com.example.linguatrain.fragments.ProgressFragment;
import com.example.linguatrain.fragments.StudentLessonsFragment;
import com.example.linguatrain.fragments.StudentTestsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottomNav);

        // По умолчанию открываем занятия
        if (savedInstanceState == null) {
            loadFragment(new StudentLessonsFragment());
        }

        // Обработка нажатий на нижнюю панель
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_lessons) {
                loadFragment(new StudentLessonsFragment());
                return true;
            } else if (itemId == R.id.nav_tests) {
                loadFragment(new StudentTestsFragment());
                return true;
            } else if (itemId == R.id.nav_progress) {
                loadFragment(new ProgressFragment());
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.nav_host_fragment, fragment);
        ft.commit();
    }
}