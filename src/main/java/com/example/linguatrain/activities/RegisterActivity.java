package com.example.linguatrain.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.linguatrain.R;
import com.example.linguatrain.utils.DatabaseHelper;

public class RegisterActivity extends AppCompatActivity {

    private EditText etLogin, etPassword;
    private RadioGroup roleGroup;
    private Button btnRegister;
    private TextView tvGoToLogin;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);
        etLogin = findViewById(R.id.etLogin);
        etPassword = findViewById(R.id.etPassword);
        roleGroup = findViewById(R.id.roleGroup);
        btnRegister = findViewById(R.id.btnRegister);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);

        btnRegister.setOnClickListener(v -> {
            String login = etLogin.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selected = findViewById(roleGroup.getCheckedRadioButtonId());
            String role = selected.getText().toString().equals("Репетитор") ? "tutor" : "student";

            if (dbHelper.isUserExists(login)) {
                Toast.makeText(this, "Пользователь с таким логином уже существует", Toast.LENGTH_SHORT).show();
                return;
            }

            dbHelper.registerUser(login, password, role);
            Toast.makeText(this, "Регистрация успешна! Теперь войдите.", Toast.LENGTH_SHORT).show();
            finish();
        });

        tvGoToLogin.setOnClickListener(v -> finish());
    }
}