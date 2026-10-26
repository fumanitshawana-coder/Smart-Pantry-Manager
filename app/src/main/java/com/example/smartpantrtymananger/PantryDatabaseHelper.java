package com.example.smartpantrtymananger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PantryDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_INGREDIENTS = "ingredients";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_UNIT = "unit";

    public PantryDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_INGREDIENTS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT NOT NULL, "
                + COLUMN_QUANTITY + " REAL NOT NULL, "
                + COLUMN_UNIT + " TEXT NOT NULL"
                + ")";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INGREDIENTS);
        onCreate(db);
    }

    // CREATE: Add a new ingredient.
    public long addIngredient(String name, double quantity, String unit) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name.trim());
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_UNIT, unit);

        return db.insert(TABLE_INGREDIENTS, null, values);
    }

    // READ: Retrieve all pantry ingredients.
    public Cursor getAllIngredients() {
        SQLiteDatabase db = getReadableDatabase();

        return db.query(
                TABLE_INGREDIENTS,
                null,
                null,
                null,
                null,
                null,
                COLUMN_NAME + " ASC"
        );
    }

    // UPDATE: Edit an existing ingredient.
    public boolean updateIngredient(
            long id,
            String name,
            double quantity,
            String unit
    ) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name.trim());
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_UNIT, unit);

        int rowsUpdated = db.update(
                TABLE_INGREDIENTS,
                values,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );

        return rowsUpdated > 0;
    }

    // DELETE: Remove one ingredient.
    public boolean deleteIngredient(long id) {
        SQLiteDatabase db = getWritableDatabase();

        int rowsDeleted = db.delete(
                TABLE_INGREDIENTS,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );

        return rowsDeleted > 0;
    }

    // DELETE: Remove all pantry ingredients.
    public void clearIngredients() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_INGREDIENTS, null, null);
    }
}