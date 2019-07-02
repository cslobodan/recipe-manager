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
package com.scvetkovic.android.foodmaniac;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.scvetkovic.android.foodmaniac.data.FoodContract.FoodEntry;


public class FoodCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link FoodCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public FoodCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For scvetkovic, the name for the current recipe can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView summaryTextView = (TextView) view.findViewById(R.id.summary);
        TextView minutesTextView = (TextView) view.findViewById(R.id.preparation_time);

        // Find the columns of pet attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(FoodEntry.COLUMN_FOOD_NAME);
        int hashtagColumnIndex = cursor.getColumnIndex(FoodEntry.COLUMN_FOOD_HASHTAGS);
        int minutesColumnIndex = cursor.getColumnIndex(FoodEntry.COLUMN_FOOD_TIME);

        // Read the pet attributes from the Cursor for the current pet
        String recipeName = cursor.getString(nameColumnIndex);
        String recipeHashtags = cursor.getString(hashtagColumnIndex);
        String recipeMinutes = cursor.getString(minutesColumnIndex);


        if (TextUtils.isEmpty(recipeHashtags)) {
            recipeHashtags = context.getString(R.string.no_hashtag);
        }

        // Update the TextViews with the attributes for the current pet
        nameTextView.setText(recipeName);
        summaryTextView.setText(recipeHashtags);
        minutesTextView.setText(recipeMinutes);
    }
}
