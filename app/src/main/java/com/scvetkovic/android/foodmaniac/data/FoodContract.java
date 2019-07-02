/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.scvetkovic.android.foodmaniac.data;

import android.net.Uri;
import android.content.ContentResolver;
import android.provider.BaseColumns;

/**
 * API Contract for the Food Maniac app.
 */
public final class FoodContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private FoodContract() {}

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.scvetkovic.android.foodmaniac";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.scvetkovic.android.foodmaniac/foodmaniac/ is a valid path for
     * looking at pet data. content://com.scvetkovic.android.foodmaniac/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_FOOD = "foodmaniac";

    /**
     * Inner class that defines constant values for the foodmaniac database table.
     * Each entry in the table represents a single pet.
     */
    public static final class FoodEntry implements BaseColumns {

        /** The content URI to access the pet data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_FOOD);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of recipes.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FOOD;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FOOD;

        /** Name of database table for recipes */
        public final static String TABLE_NAME = "food";

        /**
         * Unique ID number for the recipe (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the recipe.
         *
         * Type: TEXT
         */
        public final static String COLUMN_FOOD_NAME ="name";

        /**
         * Hashtags for the recipe.
         *
         * Type: TEXT
         */
        public final static String COLUMN_FOOD_HASHTAGS = "hashtags";

        /**
         * Meal category.
         *
         * The only possible values are {@link #MEAL_DESSERT}, {@link #MEAL_BREAKFAST}, {@link #MEAL_LUNCH},
         * or {@link #MEAL_LUNCH}.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_FOOD_MEAL = "meal";

        /**
         * Preparation time.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_FOOD_TIME = "time";

        /**
         * Ingredients.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_FOOD_INGREDIENTS= "ingredients";

        /**
         * Instructions.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_FOOD_INSTRUCIONS = "instructions";

        /**
         * Possible values for meal.
         */
        public static final int MEAL_DESSERT = 0;
        public static final int MEAL_BREAKFAST = 1;
        public static final int MEAL_LUNCH = 2;
        public static final int MEAL_DINNER= 3;


        /**
         * Returns whether or not the given meal is {@link #MEAL_DESSERT}, {@link #MEAL_BREAKFAST},
         * {@link #MEAL_LUNCH}, or {@link #MEAL_DINNER}.
         */
        public static boolean isValidMeal(int meal) {
            if (meal == MEAL_DESSERT || meal == MEAL_BREAKFAST || meal == MEAL_LUNCH || meal == MEAL_DINNER) {
                return true;
            }
            return false;
        }
    }

}

