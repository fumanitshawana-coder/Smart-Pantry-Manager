package com.example.smartpantrtymananger;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RecipesActivity extends AppCompatActivity {

    private PantryDatabaseHelper databaseHelper;
    private TextView recipeStatusText;
    private ListView recipesListView;
    private RecipeAdapter recipeAdapter;

    private final List<RecipeAdapter.RecipeItem> displayedRecipes =
            new ArrayList<>();

    private final List<Recipe> allRecipes = new ArrayList<>();

    private static class Requirement {
        String name;
        double quantity;
        String unit;

        Requirement(String name, double quantity, String unit) {
            this.name = name;
            this.quantity = quantity;
            this.unit = unit;
        }
    }

    private static class Recipe {
        String name;
        String description;
        String instructions;
        List<Requirement> requirements;

        Recipe(
                String name,
                String description,
                String instructions,
                Requirement... requirements
        ) {
            this.name = name;
            this.description = description;
            this.instructions = instructions;
            this.requirements = Arrays.asList(requirements);
        }
    }

    private Requirement req(String name, double quantity, String unit) {
        return new Requirement(name, quantity, unit);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        databaseHelper = new PantryDatabaseHelper(this);

        recipeStatusText = findViewById(R.id.recipeStatusText);
        recipesListView = findViewById(R.id.recipesListView);

        Button backButton = findViewById(R.id.recipesBackButton);
        Button refreshButton = findViewById(R.id.refreshRecipesButton);

        recipeAdapter = new RecipeAdapter(this, displayedRecipes);
        recipesListView.setAdapter(recipeAdapter);

        backButton.setOnClickListener(view -> finish());
        refreshButton.setOnClickListener(view -> findAvailableRecipes());

        recipesListView.setOnItemClickListener((parent, view, position, id) -> {
            RecipeAdapter.RecipeItem item = displayedRecipes.get(position);

            android.content.Intent intent = new android.content.Intent(
                    this,
                    RecipeDetailActivity.class
            );
            intent.putExtra(
                    RecipeDetailActivity.EXTRA_RECIPE_NAME,
                    item.getName()
            );
            intent.putExtra(
                    RecipeDetailActivity.EXTRA_RECIPE_DESCRIPTION,
                    item.getDescription()
            );
            intent.putExtra(
                    RecipeDetailActivity.EXTRA_RECIPE_INGREDIENTS,
                    item.getIngredientsSummary()
            );
            intent.putExtra(
                    RecipeDetailActivity.EXTRA_RECIPE_INSTRUCTIONS,
                    item.getInstructions()
            );

            startActivity(intent);
        });

        loadRecipes();
        findAvailableRecipes();
    }

    private void loadRecipes() {
        allRecipes.clear();

        allRecipes.add(new Recipe(
                "Pancakes",
                "Simple homemade pancakes.",
                "1. Whisk flour, milk, eggs, and oil together in a bowl until smooth.\n"
                        + "2. Heat a oiled skillet or pan over medium heat.\n"
                        + "3. Pour batter onto the skillet and cook until bubbles form on top.\n"
                        + "4. Flip and cook until golden brown on both sides. Serve warm.",
                req("flour", 200, "g"), req("milk", 250, "ml"),
                req("eggs", 1, "pcs"), req("oil", 15, "ml")));

        allRecipes.add(new Recipe(
                "Scrambled Eggs",
                "Soft scrambled eggs.",
                "1. Crack eggs into a bowl, add milk, and whisk lightly.\n"
                        + "2. Melt butter in a skillet over medium-low heat.\n"
                        + "3. Pour egg mixture into skillet and cook slowly, stirring gently until softly set.\n"
                        + "4. Season with salt and serve immediately.",
                req("eggs", 2, "pcs"), req("butter", 10, "g"),
                req("milk", 30, "ml")));

        allRecipes.add(new Recipe(
                "Omelette",
                "A basic egg omelette.",
                "1. Whisk eggs in a bowl with a pinch of salt.\n"
                        + "2. Melt butter in a non-stick pan over medium heat.\n"
                        + "3. Pour in eggs and tilt pan to spread evenly.\n"
                        + "4. When edges set, add cheese to one half, fold over, and serve.",
                req("eggs", 2, "pcs"), req("butter", 10, "g"),
                req("cheese", 30, "g")));

        allRecipes.add(new Recipe(
                "French Toast",
                "Golden egg-coated toast.",
                "1. Whisk eggs and milk together in a shallow bowl.\n"
                        + "2. Dip bread slices into mixture, soaking both sides briefly.\n"
                        + "3. Cook on a buttered pan over medium heat until golden brown on each side.",
                req("bread", 2, "pcs"), req("eggs", 1, "pcs"),
                req("milk", 60, "ml")));

        allRecipes.add(new Recipe(
                "Peanut Butter Toast",
                "Quick peanut butter toast.",
                "1. Toast bread slices to your desired crispness.\n"
                        + "2. Spread peanut butter evenly over warm toast and enjoy.",
                req("bread", 2, "pcs"), req("peanut butter", 30, "g")));

        allRecipes.add(new Recipe(
                "Cheese Sandwich",
                "A simple cheese sandwich.",
                "1. Butter one side of each bread slice.\n"
                        + "2. Place cheese between the unbuttered sides.\n"
                        + "3. Toast in a pan over medium heat until bread is golden and cheese melts.",
                req("bread", 2, "pcs"), req("cheese", 40, "g"),
                req("butter", 10, "g")));

        allRecipes.add(new Recipe(
                "Mashed Potatoes",
                "Creamy mashed potatoes.",
                "1. Peel and chop potatoes, then boil in salted water until soft.\n"
                        + "2. Drain well and return to pot.\n"
                        + "3. Add butter and milk, then mash until creamy and smooth.",
                req("potatoes", 300, "g"), req("butter", 20, "g"),
                req("milk", 50, "ml")));

        allRecipes.add(new Recipe(
                "Boiled Eggs",
                "Easy boiled eggs.",
                "1. Place eggs in a small pot and cover with water.\n"
                        + "2. Bring water to a boil, then cover and remove from heat.\n"
                        + "3. Let sit for 7-10 minutes depending on desired firmness, then peel.",
                req("eggs", 2, "pcs"), req("water", 500, "ml")));

        allRecipes.add(new Recipe(
                "Tomato Omelette",
                "Eggs cooked with tomatoes.",
                "1. Dice fresh tomato into small pieces.\n"
                        + "2. Heat oil in a pan and sauté tomato for 2 minutes.\n"
                        + "3. Beat eggs, pour over tomatoes, and cook until golden and set.",
                req("eggs", 2, "pcs"), req("tomato", 100, "g"),
                req("oil", 10, "ml")));

        allRecipes.add(new Recipe(
                "Fried Potatoes",
                "Pan-fried potatoes.",
                "1. Slice potatoes into thin rounds or small cubes.\n"
                        + "2. Heat oil in a heavy skillet over medium heat.\n"
                        + "3. Fry potatoes, stirring occasionally, until crispy and tender.",
                req("potatoes", 250, "g"), req("oil", 20, "ml")));

        allRecipes.add(new Recipe(
                "Basic Muffins",
                "Simple homemade muffins.",
                "1. Combine flour and sugar in a mixing bowl.\n"
                        + "2. Whisk eggs, milk, and melted butter, then combine with dry ingredients.\n"
                        + "3. Spoon into muffin tin and bake at 180°C for 20 minutes.",
                req("flour", 200, "g"), req("sugar", 100, "g"),
                req("eggs", 1, "pcs"), req("milk", 150, "ml"),
                req("butter", 50, "g")));

        allRecipes.add(new Recipe(
                "Sugar Cookies",
                "Basic sweet cookies.",
                "1. Cream softened butter and sugar until fluffy.\n"
                        + "2. Stir in egg, then add flour to form a dough.\n"
                        + "3. Roll into small balls, flatten on baking sheet, and bake at 175°C for 10-12 minutes.",
                req("flour", 200, "g"), req("sugar", 100, "g"),
                req("butter", 100, "g"), req("eggs", 1, "pcs")));

        allRecipes.add(new Recipe(
                "Cheese Omelette",
                "An omelette filled with cheese.",
                "1. Beat eggs with a pinch of salt.\n"
                        + "2. Melt butter in a pan over medium heat and pour in eggs.\n"
                        + "3. Sprinkle shredded cheese over eggs, fold in half, and serve hot.",
                req("eggs", 2, "pcs"), req("cheese", 40, "g"),
                req("butter", 10, "g")));

        allRecipes.add(new Recipe(
                "Potato Pancakes",
                "Crispy potato pancakes.",
                "1. Grate potatoes and squeeze out excess liquid.\n"
                        + "2. Mix grated potato with egg, flour, and salt.\n"
                        + "3. Heat oil in skillet and drop spoonfuls of potato batter. Fry until golden and crisp.",
                req("potatoes", 250, "g"), req("eggs", 1, "pcs"),
                req("flour", 40, "g"), req("oil", 15, "ml")));

        allRecipes.add(new Recipe(
                "Peanut Butter Sandwich",
                "A quick peanut butter sandwich.",
                "1. Spread peanut butter evenly over one bread slice.\n"
                        + "2. Cover with second bread slice and cut in half.",
                req("bread", 2, "pcs"), req("peanut butter", 30, "g")));

        allRecipes.add(new Recipe(
                "Simple Cake",
                "A basic homemade cake.",
                "1. Mix melted butter and sugar, then beat in eggs one by one.\n"
                        + "2. Stir in flour alternating with milk until smooth batter forms.\n"
                        + "3. Pour into greased pan and bake at 180°C for 30 minutes.",
                req("flour", 250, "g"), req("sugar", 150, "g"),
                req("eggs", 2, "pcs"), req("butter", 100, "g"),
                req("milk", 100, "ml")));

        allRecipes.add(new Recipe(
                "Tomato Sandwich",
                "Fresh tomato on bread.",
                "1. Spread butter on bread slices.\n"
                        + "2. Layer sliced fresh tomatoes between bread and season with salt.",
                req("bread", 2, "pcs"), req("tomato", 100, "g"),
                req("butter", 10, "g")));

        allRecipes.add(new Recipe(
                "Cheesy Mashed Potatoes",
                "Mashed potatoes with cheese.",
                "1. Boil potatoes until tender and mash with milk and butter.\n"
                        + "2. Stir in grated cheese while hot until melted and creamy.",
                req("potatoes", 300, "g"), req("milk", 50, "ml"),
                req("butter", 20, "g"), req("cheese", 40, "g")));

        allRecipes.add(new Recipe(
                "Fried Egg Sandwich",
                "Eggs served on bread.",
                "1. Fry egg in oil until egg whites are cooked.\n"
                        + "2. Season egg and place between bread slices.",
                req("bread", 2, "pcs"), req("eggs", 1, "pcs"),
                req("oil", 10, "ml")));

        allRecipes.add(new Recipe(
                "Basic Scones",
                "Simple homemade scones.",
                "1. Rub butter into flour until crumbly, then mix in sugar and milk to form dough.\n"
                        + "2. Roll out dough and cut into rounds.\n"
                        + "3. Bake at 200°C for 12-15 minutes until light golden brown.",
                req("flour", 250, "g"), req("milk", 120, "ml"),
                req("butter", 50, "g"), req("sugar", 30, "g")));
    }

    private void findAvailableRecipes() {
        Map<String, Double> pantry = loadPantryQuantities();

        displayedRecipes.clear();

        for (Recipe recipe : allRecipes) {
            if (canMakeRecipe(recipe, pantry)) {
                StringBuilder ingredientsSummary = new StringBuilder();

                for (int i = 0; i < recipe.requirements.size(); i++) {
                    Requirement req = recipe.requirements.get(i);

                    if (i > 0) {
                        ingredientsSummary.append("\n");
                    }

                    ingredientsSummary.append("• ")
                            .append(formatQuantity(req.quantity))
                            .append(" ")
                            .append(req.unit)
                            .append(" ")
                            .append(req.name);
                }

                displayedRecipes.add(
                        new RecipeAdapter.RecipeItem(
                                recipe.name,
                                recipe.description,
                                recipe.instructions,
                                ingredientsSummary.toString()
                        )
                );
            }
        }

        recipeAdapter.notifyDataSetChanged();

        if (displayedRecipes.isEmpty()) {
            recipeStatusText.setText(
                    "No complete recipe matches your pantry yet. "
                            + "Add more ingredients and try again."
            );
        } else {
            recipeStatusText.setText(
                    displayedRecipes.size()
                            + " recipe(s) you can make with your current ingredients:"
            );
        }

        Toast.makeText(
                this,
                displayedRecipes.size() + " matching recipe(s) found",
                Toast.LENGTH_SHORT
        ).show();
    }

    private String formatQuantity(double quantity) {
        if (quantity == (long) quantity) {
            return String.format(Locale.getDefault(), "%d", (long) quantity);
        } else {
            return String.format(Locale.getDefault(), "%s", quantity);
        }
    }

    private Map<String, Double> loadPantryQuantities() {
        Map<String, Double> pantry = new HashMap<>();
        Cursor cursor = databaseHelper.getAllIngredients();

        if (cursor == null) {
            return pantry;
        }

        try {
            int nameColumn = cursor.getColumnIndexOrThrow("name");
            int quantityColumn = cursor.getColumnIndexOrThrow("quantity");
            int unitColumn = cursor.getColumnIndexOrThrow("unit");

            while (cursor.moveToNext()) {
                String name = normalizeName(cursor.getString(nameColumn));
                double quantity = cursor.getDouble(quantityColumn);
                String unit = normalizeUnit(cursor.getString(unitColumn));

                if (name.isEmpty() || quantity <= 0) {
                    continue;
                }

                String key = name + "|" + unit;
                double previous = pantry.containsKey(key)
                        ? pantry.get(key) : 0.0;

                pantry.put(key, previous + quantity);
            }
        } finally {
            cursor.close();
        }

        return pantry;
    }

    private boolean canMakeRecipe(Recipe recipe, Map<String, Double> pantry) {
        for (Requirement requirement : recipe.requirements) {
            double available = getAvailableQuantity(
                    pantry, requirement.name, requirement.unit
            );

            if (available + 0.000001 < requirement.quantity) {
                return false;
            }
        }

        return true;
    }

    private double getAvailableQuantity(
            Map<String, Double> pantry,
            String ingredientName,
            String requiredUnit
    ) {
        String name = normalizeName(ingredientName);
        String unit = normalizeUnit(requiredUnit);
        double total = 0;

        for (Map.Entry<String, Double> entry : pantry.entrySet()) {
            String[] parts = entry.getKey().split("\\|", 2);

            if (parts.length != 2 || !parts[0].equals(name)) {
                continue;
            }

            String availableUnit = parts[1];
            double quantity = entry.getValue();

            if (availableUnit.equals(unit)) {
                total += quantity;
            } else if (unit.equals("g") && availableUnit.equals("kg")) {
                total += quantity * 1000;
            } else if (unit.equals("kg") && availableUnit.equals("g")) {
                total += quantity / 1000;
            } else if (unit.equals("ml") && availableUnit.equals("L")) {
                total += quantity * 1000;
            } else if (unit.equals("L") && availableUnit.equals("ml")) {
                total += quantity / 1000;
            }
        }

        return total;
    }

    private String normalizeUnit(String unit) {
        String value = unit == null ? "" : unit.trim().toLowerCase(Locale.ROOT);

        switch (value) {
            case "piece":
            case "pieces":
            case "pc":
            case "pcs":
                return "pcs";
            case "gram":
            case "grams":
            case "g":
                return "g";
            case "kilogram":
            case "kilograms":
            case "kg":
                return "kg";
            case "millilitre":
            case "millilitres":
            case "milliliter":
            case "milliliters":
            case "ml":
                return "ml";
            case "litre":
            case "litres":
            case "liter":
            case "liters":
            case "l":
                return "L";
            case "cup":
            case "cups":
                return "cups";
            default:
                return value;
        }
    }

    private String normalizeName(String name) {
        String value = name == null ? "" : name.trim().toLowerCase(Locale.ROOT);
        value = value.replace('_', ' ').replace('-', ' ');
        value = value.replaceAll("\\s+", " ");

        switch (value) {
            case "egg":
            case "eggs":
                return "eggs";
            case "potato":
            case "potatoes":
                return "potatoes";
            case "tomatoes":
            case "tomato":
                return "tomato";
            case "bbread":
            case "loaf of bread":
            case "bread":
                return "bread";
            case "cooking oil":
            case "oil":
                return "oil";
            case "floor":
            case "flour":
                return "flour";
            default:
                return value;
        }
    }

    @Override
    protected void onDestroy() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        super.onDestroy();
    }
}