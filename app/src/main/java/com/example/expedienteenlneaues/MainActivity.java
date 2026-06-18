package com.example.expedienteenlneaues;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private AppBarConfiguration appBarConfiguration;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ImageView ivProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Apply Dark Mode from preferences before setting content view
        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("isDarkMode", false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_main);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImage = result.getData().getData();
                        if (selectedImage != null) {
                            saveImageToInternalStorage(selectedImage);
                        }
                    }
                }
        );

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navView = findViewById(R.id.nav_view);
        BottomNavigationView bottomNavView = findViewById(R.id.bottom_nav_view);

        // Configurar Header
        View headerView = navView.getHeaderView(0);
        ivProfileImage = headerView.findViewById(R.id.ivProfileImage);
        TextView tvProfileName = headerView.findViewById(R.id.tvProfileName);
        
        String fullName = prefs.getString("fullName", null);
        String username = prefs.getString("username", "Usuario");
        tvProfileName.setText(fullName != null && !fullName.isEmpty() ? fullName : username);
        
        String profileImagePath = prefs.getString("profile_image_uri", null);
        loadProfileImage(profileImagePath);

        ivProfileImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        // Pass IDs of top-level destinations
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_carreras, R.id.nav_expedientes)
                .setOpenableLayout(drawerLayout)
                .build();

        // Setup ActionBar with Drawer and NavController
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        
        // Setup Navigation Drawer
        NavigationUI.setupWithNavController(navView, navController);
        
        // Setup Bottom Navigation
        NavigationUI.setupWithNavController(bottomNavView, navController);

        // Initialize Dark Mode Checkbox state
        MenuItem darkModeItem = navView.getMenu().findItem(R.id.nav_dark_mode);
        darkModeItem.setChecked(isDarkMode);
        darkModeItem.setIcon(isDarkMode ? R.drawable.ic_light_mode : R.drawable.ic_dark_mode);
        darkModeItem.setTitle(isDarkMode ? "Modo Claro" : "Modo Oscuro");

        // Handle clicks in Drawer
        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_logout) {
                logout();
                return true;
            } else if (id == R.id.nav_dark_mode) {
                boolean newMode = !item.isChecked();
                item.setChecked(newMode);
                item.setIcon(newMode ? R.drawable.ic_light_mode : R.drawable.ic_dark_mode);
                item.setTitle(newMode ? "Modo Claro" : "Modo Oscuro");
                toggleDarkMode(newMode);
                return true;
            }
            // Let NavigationUI handle the rest
            return NavigationUI.onNavDestinationSelected(item, navController)
                    || super.onOptionsItemSelected(item);
        });
    }

    private void toggleDarkMode(boolean isDarkMode) {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        prefs.edit().putBoolean("isDarkMode", isDarkMode).apply();
        
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void logout() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void saveImageToInternalStorage(Uri uri) {
        try {
            java.io.InputStream inputStream = getContentResolver().openInputStream(uri);
            java.io.File file = new java.io.File(getFilesDir(), "profile_picture.jpg");
            java.io.FileOutputStream outputStream = new java.io.FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();

            SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
            prefs.edit().putString("profile_image_uri", file.getAbsolutePath()).apply();

            loadProfileImage(file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error guardando imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadProfileImage(String path) {
        if (path != null && !path.isEmpty()) {
            ivProfileImage.clearColorFilter();
            ivProfileImage.setPadding(0, 0, 0, 0);
            ivProfileImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            com.bumptech.glide.Glide.with(this)
                    .load(new java.io.File(path))
                    .placeholder(R.drawable.ic_person)
                    .into(ivProfileImage);
        } else {
            ivProfileImage.setPadding(40, 40, 40, 40);
            ivProfileImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
            ivProfileImage.setImageResource(R.drawable.ic_person);
            // Pintarlo usando el color primario de forma programática
            android.util.TypedValue typedValue = new android.util.TypedValue();
            getTheme().resolveAttribute(com.google.android.material.R.attr.colorPrimary, typedValue, true);
            ivProfileImage.setColorFilter(typedValue.data);
        }
    }
}