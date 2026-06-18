package com.example.expedienteenlneaues;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expedienteenlneaues.data.AppDatabase;
import com.example.expedienteenlneaues.data.entity.Usuario;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvRegisterLink;
    private AppDatabase db;
    private ExecutorService executorService;

    private static final Pattern CARNET_PATTERN =
            Pattern.compile("^[A-Za-z]{2}\\d{1,6}$");

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^[A-Za-z0-9]{6,20}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Check session first
        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            goToMainActivity();
            return;
        }

        db = AppDatabase.getDatabase(this);
        executorService = Executors.newSingleThreadExecutor();

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegisterLink = findViewById(R.id.tvRegisterLink);

        btnLogin.setOnClickListener(v -> loginUser());

        tvRegisterLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String username = etUsername.getText().toString().trim().toUpperCase(Locale.ROOT);
        String password = etPassword.getText().toString().trim();

        if (!validateFields(username, password)) {
            return;
        }

        executorService.execute(() -> {
            Usuario usuario = db.usuarioDao().login(username, password);
            runOnUiThread(() -> {
                if (usuario != null) {
                    // Save session
                    SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.putString("username", usuario.username);
                    editor.putString("fullName", usuario.nombreCompleto);
                    editor.apply();

                    Toast.makeText(LoginActivity.this, "Bienvenido " + usuario.nombreCompleto, Toast.LENGTH_SHORT).show();
                    goToMainActivity();
                } else {
                    Toast.makeText(LoginActivity.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private boolean validateFields(String username, String password) {
        if (username.isEmpty()) {
            etUsername.setError("Ingrese su carnet");
            etUsername.requestFocus();
            return false;
        }

        if (!CARNET_PATTERN.matcher(username).matches()) {
            etUsername.setError("Formato válido: 2 letras y hasta 6 números. Ejemplo: MS19059");
            etUsername.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            etPassword.setError("Ingrese su contraseña");
            etPassword.requestFocus();
            return false;
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            etPassword.setError("Use solo letras y números. Mínimo 6 y máximo 20 caracteres");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void goToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
