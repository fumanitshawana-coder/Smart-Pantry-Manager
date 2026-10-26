package com.example.smartpantrtymananger;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_NAME = "RECIPE_NAME";
    public static final String EXTRA_RECIPE_DESCRIPTION = "RECIPE_DESCRIPTION";
    public static final String EXTRA_RECIPE_INGREDIENTS = "RECIPE_INGREDIENTS";
    public static final String EXTRA_RECIPE_INSTRUCTIONS = "RECIPE_INSTRUCTIONS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        Button backButton = findViewById(R.id.recipeDetailBackButton);
        TextView nameText = findViewById(R.id.detailRecipeName);
        TextView descriptionText = findViewById(R.id.detailRecipeDescription);
        TextView ingredientsText = findViewById(R.id.detailRecipeIngredients);
        TextView instructionsText = findViewById(R.id.detailRecipeInstructions);

        backButton.setOnClickListener(view -> finish());

        String name = getIntent().getStringExtra(EXTRA_RECIPE_NAME);
        String description = getIntent().getStringExtra(EXTRA_RECIPE_DESCRIPTION);
        String ingredients = getIntent().getStringExtra(EXTRA_RECIPE_INGREDIENTS);
        String instructions = getIntent().getStringExtra(EXTRA_RECIPE_INSTRUCTIONS);

        if (name != null) {
            nameText.setText(name);
        }

        if (description != null) {
            descriptionText.setText(description);
        }

        if (ingredients != null && !ingredients.isEmpty()) {
            ingredientsText.setText(ingredients);
        }

        if (instructions != null && !instructions.isEmpty()) {
            instructionsText.setText(instructions);
        }
    }
}