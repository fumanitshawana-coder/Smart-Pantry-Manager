package com.example.smartpantrtymananger;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private TextView pantrySummary;
    private PantryDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (view, insets) -> {
                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    view.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );

        databaseHelper = new PantryDatabaseHelper(this);

        pantrySummary = findViewById(R.id.pantrySummary);

        Button pantryButton = findViewById(R.id.viewPantryButton);
        Button recipesButton = findViewById(R.id.findRecipesButton);
        Button settingsButton = findViewById(R.id.settingsButton);

        pantryButton.setOnClickListener(view -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    PantryActivity.class
            );
            startActivity(intent);
        });

        recipesButton.setOnClickListener(view -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    RecipesActivity.class
            );
            startActivity(intent);
        });

        settingsButton.setOnClickListener(view -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    SettingsActivity.class
            );
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (pantrySummary != null && databaseHelper != null) {
            updateSummary();
        }
    }

    private void updateSummary() {
        Cursor cursor = databaseHelper.getAllIngredients();

        if (cursor == null) {
            pantrySummary.setText("Unable to load pantry ingredients.");
            return;
        }

        StringBuilder names = new StringBuilder();
        int total = 0;
        int shown = 0;

        try {
            int nameColumn = cursor.getColumnIndexOrThrow("name");

            while (cursor.moveToNext()) {
                String name = cursor.getString(nameColumn);

                total++;

                if (shown < 5) {
                    if (shown > 0) {
                        names.append(", ");
                    }

                    names.append(name);
                    shown++;
                }
            }
        } finally {
            cursor.close();
        }

        if (total == 0) {
            pantrySummary.setText(
                    "Your ingredients will appear here."
            );
            return;
        }

        String more = total > 5
                ? " +" + (total - 5) + " more"
                : "";

        String label = total == 1
                ? " ingredient"
                : " ingredients";

        pantrySummary.setText(
                total + label + "\n" + names + more
        );
    }

    @Override
    protected void onDestroy() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }

        super.onDestroy();
    }
}