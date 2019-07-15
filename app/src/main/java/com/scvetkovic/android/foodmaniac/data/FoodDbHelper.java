package com.scvetkovic.android.foodmaniac.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.scvetkovic.android.foodmaniac.data.FoodContract.FoodEntry;

/**
 * Database helper for Pets app. Manages database creation and version management.
 */
public class FoodDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = FoodDbHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "recepti.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 5;

    /**
     * Constructs a new instance of {@link FoodDbHelper}.
     *
     * @param context of the app
     */
    public FoodDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the foodmaniac table
        String SQL_CREATE_PETS_TABLE =  "CREATE TABLE " + FoodEntry.TABLE_NAME + " ("
                + FoodContract.FoodEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FoodEntry.COLUMN_FOOD_NAME + " TEXT NOT NULL, "
                + FoodEntry.COLUMN_FOOD_HASHTAGS + " TEXT, "
                + FoodEntry.COLUMN_FOOD_MEAL + " INTEGER NOT NULL, "
                + FoodContract.FoodEntry.COLUMN_FOOD_TIME + " INTEGER NOT NULL DEFAULT 0,"
                + FoodEntry.COLUMN_FOOD_INGREDIENTS + " TEXT,"
                + FoodEntry.COLUMN_FOOD_INSTRUCIONS + ");";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}