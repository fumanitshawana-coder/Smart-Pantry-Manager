package com.example.smartpantrtymananger;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private Switch notificationsSwitch;
    private Switch darkModeSwitch;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferences = getSharedPreferences("app_settings", MODE_PRIVATE);

        notificationsSwitch = findViewById(R.id.notificationsSwitch);
        darkModeSwitch = findViewById(R.id.darkModeSwitch);

        Button backButton = findViewById(R.id.settingsBackButton);
        Button clearPantryButton = findViewById(R.id.clearPantryButton);

        notificationsSwitch.setChecked(
                preferences.getBoolean("notifications_enabled", true)
        );

        darkModeSwitch.setChecked(
                preferences.getBoolean("dark_mode_enabled", false)
        );

        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit()
                    .putBoolean("notifications_enabled", isChecked)
                    .apply();

            Toast.makeText(
                    this,
                    isChecked ? "Notifications enabled" : "Notifications disabled",
                    Toast.LENGTH_SHORT
            ).show();
        });

        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit()
                    .putBoolean("dark_mode_enabled", isChecked)
                    .apply();

            Toast.makeText(
                    this,
                    isChecked
                            ? "Dark mode preference saved"
                            : "Light mode preference saved",
                    Toast.LENGTH_SHORT
            ).show();
        });

        backButton.setOnClickListener(view -> finish());

        clearPantryButton.setOnClickListener(view -> {
            new android.app.AlertDialog.Builder(this)
                    .setTitle("Clear pantry")
                    .setMessage("Are you sure you want to delete all pantry ingredients? This cannot be undone.")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Clear", (dialog, which) -> {
                        PantryDatabaseHelper databaseHelper =
                                new PantryDatabaseHelper(this);

                        databaseHelper.clearIngredients();
                        databaseHelper.close();

                        Toast.makeText(
                                this,
                                "Pantry cleared",
                                Toast.LENGTH_SHORT
                        ).show();
                    })
                    .show();
        });
    }
}