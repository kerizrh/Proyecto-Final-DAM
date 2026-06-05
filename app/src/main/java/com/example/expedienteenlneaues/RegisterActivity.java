package com.example.expedienteenlneaues;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expedienteenlneaues.data.AppDatabase;
import com.example.expedienteenlneaues.data.entity.Usuario;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etRegUsername, etRegPassword;
    private Button btnRegister;
    private AppDatabase db;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = AppDatabase.getDatabase(this);
        executorService = Executors.newSingleThreadExecutor();

        etFullName = findViewById(R.id.etFullName);
        etRegUsername = findViewById(R.id.etRegUsername);
        etRegPassword = findViewById(R.id.etRegPassword);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String fullName = etFullName.getText().toString().trim();
        String username = etRegUsername.getText().toString().trim();
        String password = etRegPassword.getText().toString().trim();

        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        executorService.execute(() -> {
            Usuario exist = db.usuarioDao().getByUsername(username);
            if (exist != null) {
                runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "El usuario ya existe", Toast.LENGTH_SHORT).show());
            } else {
                Usuario newUser = new Usuario();
                newUser.nombreCompleto = fullName;
                newUser.username = username;
                newUser.password = password;

                long result = db.usuarioDao().insert(newUser);
                runOnUiThread(() -> {
                    if (result > 0) {
                        Toast.makeText(RegisterActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                        finish(); // Returns to Login
                    } else {
                        Toast.makeText(RegisterActivity.this, "Error al registrar", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
