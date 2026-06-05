package com.example.expedienteenlneaues;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navView = findViewById(R.id.nav_view);
        BottomNavigationView bottomNavView = findViewById(R.id.bottom_nav_view);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        // Pass IDs of top-level destinations
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_materias, R.id.nav_expedientes)
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
}