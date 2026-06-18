package com.example.expedienteenlneaues;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expedienteenlneaues.data.AppDatabase;
import com.example.expedienteenlneaues.data.entity.Usuario;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etRegUsername, etRegPassword;
    private Button btnRegister;
    private AppDatabase db;
    private ExecutorService executorService;

    private static final Pattern FULL_NAME_PATTERN =
            Pattern.compile("^[A-Za-zÁÉÍÓÚáéíóúÑñÜü ]{3,60}$");

    private static final Pattern CARNET_PATTERN =
            Pattern.compile("^[A-Za-z]{2}\\d{1,6}$");

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^[A-Za-z0-9]{6,20}$");

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
        String username = etRegUsername.getText().toString().trim().toUpperCase(Locale.ROOT);
        String password = etRegPassword.getText().toString().trim();

        if (!validateFields(fullName, username, password)) {
            return;
        }

        executorService.execute(() -> {
            Usuario exist = db.usuarioDao().getByUsername(username);
            if (exist != null) {
                runOnUiThread(() -> {
                    etRegUsername.setError("Este carnet ya está registrado");
                    etRegUsername.requestFocus();
                    Toast.makeText(RegisterActivity.this, "El usuario ya existe", Toast.LENGTH_SHORT).show();
                });
            } else {
                Usuario newUser = new Usuario();
                newUser.nombreCompleto = fullName;
                newUser.username = username;
                newUser.password = password;

                long result = db.usuarioDao().insert(newUser);
                runOnUiThread(() -> {
                    if (result > 0) {
                        Toast.makeText(RegisterActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Error al registrar", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private boolean validateFields(String fullName, String username, String password) {
        if (fullName.isEmpty()) {
            etFullName.setError("Ingrese su nombre completo");
            etFullName.requestFocus();
            return false;
        }

        if (!FULL_NAME_PATTERN.matcher(fullName).matches()) {
            etFullName.setError("Solo letras y espacios. Mínimo 3 caracteres");
            etFullName.requestFocus();
            return false;
        }

        if (username.isEmpty()) {
            etRegUsername.setError("Ingrese su carnet");
            etRegUsername.requestFocus();
            return false;
        }

        if (!CARNET_PATTERN.matcher(username).matches()) {
            etRegUsername.setError("Formato válido: 2 letras y hasta 6 números. Ejemplo: MS19059");
            etRegUsername.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            etRegPassword.setError("Ingrese una contraseña");
            etRegPassword.requestFocus();
            return false;
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            etRegPassword.setError("Use solo letras y números. Mínimo 6 y máximo 20 caracteres");
            etRegPassword.requestFocus();
            return false;
        }

        return true;
    }
}
