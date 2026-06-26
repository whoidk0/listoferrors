package com.example.linguatrain.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.linguatrain.R;
import com.example.linguatrain.utils.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {
    private EditText etLogin, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);
        etLogin = findViewById(R.id.etLogin);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        btnLogin.setOnClickListener(v -> {
            String login = etLogin.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            if (checkUser(login, password)) {
                navigateToRole(login);
                finish();
            } else {
                Toast.makeText(this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show();
            }
        });

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private boolean checkUser(String login, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM users WHERE login = ? AND password = ?",
                new String[]{login, password}
        );
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    private void navigateToRole(String login) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT role FROM users WHERE login = ?",
                new String[]{login}
        );
        if (cursor.moveToFirst()) {
            String role = cursor.getString(0);
            if ("tutor".equals(role)) {
                startActivity(new Intent(this, TutorActivity.class));
            } else {
                startActivity(new Intent(this, MainActivity.class));
            }
        }
        cursor.close();
    }
}