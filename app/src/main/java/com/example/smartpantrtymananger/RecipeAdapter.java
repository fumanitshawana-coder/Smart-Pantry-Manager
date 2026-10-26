package com.example.smartpantrtymananger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class RecipeAdapter extends ArrayAdapter<RecipeAdapter.RecipeItem> {

    public static class RecipeItem {
        private final String name;
        private final String description;
        private final String instructions;
        private final String ingredientsSummary;

        public RecipeItem(
                String name,
                String description,
                String instructions,
                String ingredientsSummary
        ) {
            this.name = name;
            this.description = description;
            this.instructions = instructions;
            this.ingredientsSummary = ingredientsSummary;
        }

        public RecipeItem(String name, String description) {
            this(name, description, "", "");
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getInstructions() {
            return instructions;
        }

        public String getIngredientsSummary() {
            return ingredientsSummary;
        }
    }

    public RecipeAdapter(
            @NonNull Context context,
            @NonNull List<RecipeItem> recipes
    ) {
        super(context, 0, recipes);
    }

    @NonNull
    @Override
    public View getView(
            int position,
            View convertView,
            @NonNull ViewGroup parent
    ) {
        View row = convertView;

        if (row == null) {
            row = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_recipe,
                    parent,
                    false
            );
        }

        TextView nameText = row.findViewById(R.id.recipeName);
        TextView descriptionText = row.findViewById(R.id.recipeDescription);

        RecipeItem recipe = getItem(position);

        if (recipe != null) {
            nameText.setText(recipe.getName());
            descriptionText.setText(recipe.getDescription());
        }

        return row;
    }
}