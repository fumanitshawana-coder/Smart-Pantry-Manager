package com.example.smartpantrtymananger;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PantryActivity extends AppCompatActivity {

    private EditText nameInput;
    private EditText quantityInput;
    private Spinner unitSpinner;
    private ListView ingredientsListView;

    private PantryDatabaseHelper databaseHelper;

    private final List<String> ingredients = new ArrayList<>();
    private final List<Integer> ingredientIds = new ArrayList<>();
    private ArrayAdapter<String> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);

        nameInput = findViewById(R.id.ingredientNameInput);
        quantityInput = findViewById(R.id.ingredientQuantityInput);
        unitSpinner = findViewById(R.id.ingredientUnitSpinner);
        ingredientsListView = findViewById(R.id.ingredientsListView);

        Button addButton = findViewById(R.id.addIngredientButton);
        Button backButton = findViewById(R.id.backButton);

        databaseHelper = new PantryDatabaseHelper(this);

        String[] units = {"pcs", "g", "kg", "ml", "L", "cups"};

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                units
        );

        unitAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        unitSpinner.setAdapter(unitAdapter);

        listAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                ingredients
        );

        ingredientsListView.setAdapter(listAdapter);

        addButton.setOnClickListener(view -> addIngredient());

        backButton.setOnClickListener(view -> finish());

        // Tap an ingredient to edit it.
        ingredientsListView.setOnItemClickListener(
                (parent, view, position, id) -> editIngredient(position)
        );

        // Hold an ingredient to delete it.
        ingredientsListView.setOnItemLongClickListener(
                (parent, view, position, id) -> {
                    confirmDelete(position);
                    return true;
                }
        );

        loadIngredients();
    }

    private boolean validateInputs() {
        String name = nameInput.getText().toString().trim();
        String quantityText = quantityInput.getText().toString().trim();

        if (name.isEmpty()) {
            nameInput.setError("Enter an ingredient name");
            nameInput.requestFocus();
            return false;
        }

        if (quantityText.isEmpty()) {
            quantityInput.setError("Enter a quantity");
            quantityInput.requestFocus();
            return false;
        }

        try {
            double quantity = Double.parseDouble(quantityText);

            if (Double.isNaN(quantity)
                    || Double.isInfinite(quantity)
                    || quantity <= 0) {
                quantityInput.setError(
                        "Quantity must be greater than zero"
                );
                quantityInput.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            quantityInput.setError(
                    "Enter a valid number, such as 2 or 0.5"
            );
            quantityInput.requestFocus();
            return false;
        }

        if (unitSpinner.getSelectedItem() == null) {
            Toast.makeText(
                    this,
                    "Please select a unit",
                    Toast.LENGTH_SHORT
            ).show();
            return false;
        }

        return true;
    }

    private void addIngredient() {
        if (!validateInputs()) {
            return;
        }

        String name = nameInput.getText().toString().trim();
        double quantity = Double.parseDouble(
                quantityInput.getText().toString().trim()
        );
        String unit = unitSpinner.getSelectedItem().toString();

        long result = databaseHelper.addIngredient(name, quantity, unit);

        if (result != -1) {
            clearInputs();
            loadIngredients();

            Toast.makeText(
                    this,
                    "Ingredient added successfully",
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            Toast.makeText(
                    this,
                    "Could not save ingredient",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void editIngredient(int position) {
        int ingredientId = ingredientIds.get(position);

        Cursor cursor = databaseHelper.getAllIngredients();

        if (cursor == null) {
            return;
        }

        String currentName = "";
        double currentQuantity = 0;
        String currentUnit = "";

        try {
            int idColumn = cursor.getColumnIndexOrThrow("id");
            int nameColumn = cursor.getColumnIndexOrThrow("name");
            int quantityColumn = cursor.getColumnIndexOrThrow("quantity");
            int unitColumn = cursor.getColumnIndexOrThrow("unit");

            while (cursor.moveToNext()) {
                if (cursor.getInt(idColumn) == ingredientId) {
                    currentName = cursor.getString(nameColumn);
                    currentQuantity = cursor.getDouble(quantityColumn);
                    currentUnit = cursor.getString(unitColumn);
                    break;
                }
            }
        } finally {
            cursor.close();
        }

        if (currentName.isEmpty()) {
            Toast.makeText(
                    this,
                    "Ingredient could not be found",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        EditText editName = new EditText(this);
        editName.setHint("Ingredient name");
        editName.setSingleLine(true);
        editName.setText(currentName);

        EditText editQuantity = new EditText(this);
        editQuantity.setHint("Quantity");
        editQuantity.setSingleLine(true);
        editQuantity.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER
                        | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
                        | android.text.InputType.TYPE_NUMBER_FLAG_SIGNED
        );
        editQuantity.setText(
                String.format(Locale.US, "%s", currentQuantity)
        );

        Spinner editUnit = new Spinner(this);

        String[] units = {"pcs", "g", "kg", "ml", "L", "cups"};

        ArrayAdapter<String> editUnitAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                units
        );
        editUnitAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
        editUnit.setAdapter(editUnitAdapter);

        for (int i = 0; i < units.length; i++) {
            if (units[i].equalsIgnoreCase(currentUnit)) {
                editUnit.setSelection(i);
                break;
            }
        }

        android.widget.LinearLayout layout =
                new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);

        int padding = (int) (20 * getResources()
                .getDisplayMetrics().density);

        layout.setPadding(padding, padding / 2, padding, 0);
        layout.addView(editName);
        layout.addView(editQuantity);
        layout.addView(editUnit);

        new AlertDialog.Builder(this)
                .setTitle("Edit ingredient")
                .setView(layout)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = editName.getText()
                            .toString().trim();
                    String quantityText = editQuantity.getText()
                            .toString().trim();

                    if (newName.isEmpty()) {
                        Toast.makeText(
                                this,
                                "Ingredient name cannot be empty",
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }

                    if (quantityText.isEmpty()) {
                        Toast.makeText(
                                this,
                                "Enter a quantity",
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }

                    double newQuantity;

                    try {
                        newQuantity = Double.parseDouble(quantityText);
                    } catch (NumberFormatException e) {
                        Toast.makeText(
                                this,
                                "Enter a valid quantity",
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }

                    if (Double.isNaN(newQuantity)
                            || Double.isInfinite(newQuantity)
                            || newQuantity <= 0) {
                        Toast.makeText(
                                this,
                                "Quantity must be greater than zero",
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }

                    String newUnit = editUnit.getSelectedItem().toString();

                    boolean updated = databaseHelper.updateIngredient(
                            ingredientId,
                            newName,
                            newQuantity,
                            newUnit
                    );

                    if (updated) {
                        loadIngredients();

                        Toast.makeText(
                                this,
                                "Ingredient updated successfully",
                                Toast.LENGTH_SHORT
                        ).show();
                    } else {
                        Toast.makeText(
                                this,
                                "Could not update ingredient",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void loadIngredients() {
        ingredients.clear();
        ingredientIds.clear();

        Cursor cursor = databaseHelper.getAllIngredients();

        if (cursor != null) {
            try {
                int idColumn = cursor.getColumnIndexOrThrow("id");
                int nameColumn = cursor.getColumnIndexOrThrow("name");
                int quantityColumn = cursor.getColumnIndexOrThrow("quantity");
                int unitColumn = cursor.getColumnIndexOrThrow("unit");

                while (cursor.moveToNext()) {
                    int id = cursor.getInt(idColumn);
                    String name = cursor.getString(nameColumn);
                    double quantity = cursor.getDouble(quantityColumn);
                    String unit = cursor.getString(unitColumn);

                    ingredientIds.add(id);

                    String quantityDisplay = String.format(
                            Locale.getDefault(),
                            "%s",
                            quantity
                    );

                    ingredients.add(
                            name + " - " + quantityDisplay + " " + unit
                    );
                }
            } finally {
                cursor.close();
            }
        }

        listAdapter.notifyDataSetChanged();
    }

    private void confirmDelete(int position) {
        String ingredient = ingredients.get(position);
        int ingredientId = ingredientIds.get(position);

        new AlertDialog.Builder(this)
                .setTitle("Delete ingredient")
                .setMessage("Do you want to delete " + ingredient + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    boolean deleted =
                            databaseHelper.deleteIngredient(ingredientId);

                    loadIngredients();

                    Toast.makeText(
                            this,
                            deleted ? "Ingredient deleted"
                                    : "Could not delete ingredient",
                            Toast.LENGTH_SHORT
                    ).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void clearInputs() {
        nameInput.setText("");
        quantityInput.setText("");
        nameInput.requestFocus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}