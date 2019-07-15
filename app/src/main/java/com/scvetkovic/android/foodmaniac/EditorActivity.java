package com.scvetkovic.android.foodmaniac;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.scvetkovic.android.foodmaniac.data.FoodContract;
import com.scvetkovic.android.foodmaniac.data.FoodContract.FoodEntry;

/**
 * Allows user to create a new recipe or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /** Identifier for the recipe data loader */
    private static final int EXISTING_PET_LOADER = 0;

    /** Content URI for the existing recipe (null if it's a new pet) */
    private Uri mCurrentFoodUri;

    /** EditText field to enter the recipe name*/
    private EditText mNameEditText;

    /** EditText field to enter the recipe hashtags */
    private EditText mHashtagEditText;

    /** EditText field to enter the recipe time */
    private EditText mTimeEditText;

    /** EditText field to enter the recipe category*/
    private Spinner mMealSpinner;

    /** EditText field to enter the recipe ingredients*/
    private EditText mIngredientsEditText;

    /** EditText field to enter the recipe instructions*/
    private EditText mInstructionsEditText;

    /**
     * Gender of the pet. The possible valid values are in the FoodContract.java file:
     * {@link FoodEntry#MEAL_DESSERT}, {@link FoodContract.FoodEntry#MEAL_BREAKFAST}, or
     * {@link FoodContract.FoodEntry#MEAL_LUNCH}.
     */
    private int mMeal = FoodContract.FoodEntry.MEAL_DESSERT;

    /** Boolean flag that keeps track of whether the recipe has been edited (true) or not (false) */
    private boolean mFoodHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mFoodHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mFoodHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);


        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new recipe or editing an existing one.
        Intent intent = getIntent();
        mCurrentFoodUri = intent.getData();

        // If the intent DOES NOT contain a recipe content URI, then we know that we are
        // creating a new recipe.
        if (mCurrentFoodUri == null) {
            // This is a new pet, so change the app bar to say "Add a Recipe"
            setTitle(getString(R.string.editor_activity_title_new_recipe));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a recipe that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing recipe, so change app bar to say "Recipe Details"
            setTitle(getString(R.string.editor_activity_title_recipe_details));

            // Initialize a loader to read the recipe data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_PET_LOADER, null, this);
        }


        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_recipe_name);
        mHashtagEditText = (EditText) findViewById(R.id.edit_recipe_hashtags);
        mTimeEditText = (EditText) findViewById(R.id.edit_recipe_time);
        mMealSpinner = (Spinner) findViewById(R.id.spinner_meal);
        mIngredientsEditText = (EditText) findViewById(R.id.edit_ingredients);
        mInstructionsEditText = (EditText) findViewById(R.id.edit_preparation);

        if (mCurrentFoodUri != null) {

            mNameEditText.setInputType(0);

            mNameEditText.setEnabled(false);
            mHashtagEditText.setEnabled(false);
            mTimeEditText.setEnabled(false);
            mMealSpinner.setEnabled(false);
            mIngredientsEditText.setEnabled(false);
            mInstructionsEditText.setEnabled(false);

        }
        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mHashtagEditText.setOnTouchListener(mTouchListener);
        mTimeEditText.setOnTouchListener(mTouchListener);
        mMealSpinner.setOnTouchListener(mTouchListener);
        mIngredientsEditText.setOnTouchListener(mTouchListener);
        mInstructionsEditText.setOnTouchListener(mTouchListener);

        setupSpinner();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_meal_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mMealSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mMealSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.meal_breakfast))) {
                        mMeal = FoodEntry.MEAL_BREAKFAST;
                    } else if (selection.equals(getString(R.string.meal_lunch))) {
                        mMeal = FoodContract.FoodEntry.MEAL_LUNCH;
                    } else if (selection.equals(getString(R.string.meal_dinner))) {
                        mMeal = FoodContract.FoodEntry.MEAL_DINNER;
                    } else {
                        mMeal = FoodContract.FoodEntry.MEAL_DESSERT;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mMeal = FoodContract.FoodEntry.MEAL_DESSERT;
            }
        });
    }


    /**
     * Edit food
     */
    public void editFood(){
        setTitle(getString(R.string.editor_activity_title_edit_recipe));
        mNameEditText.setInputType(1);
        mNameEditText.setEnabled(true);;
        mHashtagEditText.setEnabled(true);
        mTimeEditText.setEnabled(true);
        mMealSpinner.setEnabled(true);
        mIngredientsEditText.setEnabled(true);
        mInstructionsEditText.setEnabled(true);

    }

    /**
     * Get user input from editor and save pet into database.
     */
    private void saveFood() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String hashtagString = mHashtagEditText.getText().toString().trim();
        String timeString = mTimeEditText.getText().toString().trim();
        String ingredientsString = mIngredientsEditText.getText().toString().trim();
        String instructionsString = mInstructionsEditText.getText().toString().trim();

//        // Check if this is supposed to be a new pet
//        // and check if all the fields in the editor are blank
//        if (mCurrentFoodUri == null &&
//                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(hashtagString) &&
//                TextUtils.isEmpty(timeString) && mMeal == FoodEntry.MEAL_DESSERT) {
//            // Since no fields were modified, we can return early without creating a new pet.
//            // No need to create ContentValues and no need to do any ContentProvider operations.
//            return;
//        }

        if(TextUtils.isEmpty(nameString) && TextUtils.isEmpty(timeString)){
            mNameEditText.requestFocus();
            mNameEditText.setError(getText(R.string.name_required));
            mTimeEditText.setError(getText(R.string.time_required));
        } else if(TextUtils.isEmpty(nameString)) {
            mNameEditText.requestFocus();
            mNameEditText.setError(getText(R.string.name_required));
        } else if(TextUtils.isEmpty(timeString)) {
            mTimeEditText.requestFocus();
            mTimeEditText.setError(getText(R.string.time_required));
        } else {

                // Create a ContentValues object where column names are the keys,
                // and pet attributes from the editor are the values.
                ContentValues values = new ContentValues();
                values.put(FoodContract.FoodEntry.COLUMN_FOOD_NAME, nameString);
                values.put(FoodEntry.COLUMN_FOOD_HASHTAGS, hashtagString);
                values.put(FoodContract.FoodEntry.COLUMN_FOOD_MEAL, mMeal);
                values.put(FoodEntry.COLUMN_FOOD_INGREDIENTS, ingredientsString);
                values.put(FoodEntry.COLUMN_FOOD_INSTRUCIONS, instructionsString);
                // If the time is not provided by the user, don't try to parse the string into an
                // integer value. Use 0 by default.
                int time = 0;
                if (!TextUtils.isEmpty(timeString)) {
                    time = Integer.parseInt(timeString);
                }
                values.put(FoodContract.FoodEntry.COLUMN_FOOD_TIME, time);

                // Determine if this is a new or existing pet by checking if mCurrentFoodUri is null or not
                if (mCurrentFoodUri == null) {
                    // This is a NEW pet, so insert a new pet into the provider,
                    // returning the content URI for the new pet.
                    Uri newUri = getContentResolver().insert(FoodContract.FoodEntry.CONTENT_URI, values);

                    // Show a toast message depending on whether or not the insertion was successful.
                    if (newUri == null) {
                        // If the new content URI is null, then there was an error with insertion.
                        Toast.makeText(this, getString(R.string.editor_insert_recipe_failed),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // Otherwise, the insertion was successful and we can display a toast.
                        Toast.makeText(this, getString(R.string.editor_insert_recipe_successful),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Otherwise this is an EXISTING pet, so update the pet with content URI: mCurrentFoodUri
                    // and pass in the new ContentValues. Pass in null for the selection and selection args
                    // because mCurrentFoodUri will already identify the correct row in the database that
                    // we want to modify.
                    int rowsAffected = getContentResolver().update(mCurrentFoodUri, values, null, null);

                    // Show a toast message depending on whether or not the update was successful.
                    if (rowsAffected == 0) {
                        // If no rows were affected, then there was an error with the update.
                        Toast.makeText(this, getString(R.string.editor_update_recipe_failed),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // Otherwise, the update was successful and we can display a toast.
                        Toast.makeText(this, getString(R.string.editor_update_recipe_successful),
                                Toast.LENGTH_SHORT).show();
                    }
                }

                finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);

        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentFoodUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }

        MenuItem menuItem = menu.findItem(R.id.action_save);
        if(mNameEditText.isEnabled()){
            menuItem.setVisible(true);
        } else {
            menuItem.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save pet to database
                saveFood();
                // Exit activity
                //finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            //Respond to aclick on the "Edit" menu option
            case R.id.action_edit:
                editFood();
                invalidateOptionsMenu();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mFoodHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mFoodHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all pet attributes, define a projection that contains
        // all columns from the pet table
        String[] projection = {
                FoodEntry._ID,
                FoodEntry.COLUMN_FOOD_NAME,
                FoodEntry.COLUMN_FOOD_HASHTAGS,
                FoodEntry.COLUMN_FOOD_MEAL,
                FoodContract.FoodEntry.COLUMN_FOOD_TIME,
                FoodEntry.COLUMN_FOOD_INGREDIENTS,
                FoodEntry.COLUMN_FOOD_INSTRUCIONS
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentFoodUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(FoodContract.FoodEntry.COLUMN_FOOD_NAME);
            int hashtagsColumnIndex = cursor.getColumnIndex(FoodEntry.COLUMN_FOOD_HASHTAGS);
            int mealColumnIndex = cursor.getColumnIndex(FoodEntry.COLUMN_FOOD_MEAL);
            int timeColumnIndex = cursor.getColumnIndex(FoodEntry.COLUMN_FOOD_TIME);
            int ingredientsColumnIndex = cursor.getColumnIndex(FoodEntry.COLUMN_FOOD_INGREDIENTS);
            int instructionsColumnIndex = cursor.getColumnIndex(FoodEntry.COLUMN_FOOD_INSTRUCIONS);


            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String hashtags = cursor.getString(hashtagsColumnIndex);
            int meal = cursor.getInt(mealColumnIndex);
            int time = cursor.getInt(timeColumnIndex);
            String ingredients = cursor.getString(ingredientsColumnIndex);
            String instructions = cursor.getString(instructionsColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mHashtagEditText.setText(hashtags);
            mTimeEditText.setText(Integer.toString(time));
            mIngredientsEditText.setText(ingredients);
            mInstructionsEditText.setText(instructions);

            // Gender is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is Unknown, 1 is Male, 2 is Female).
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (meal) {
                case FoodEntry.MEAL_BREAKFAST:
                    mMealSpinner.setSelection(1);
                    break;
                case FoodEntry.MEAL_LUNCH:
                    mMealSpinner.setSelection(2);
                    break;
                case FoodEntry.MEAL_DINNER:
                    mMealSpinner.setSelection(3);
                    break;
                default:
                    mMealSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mHashtagEditText.setText("");
        mTimeEditText.setText("");
        mMealSpinner.setSelection(0);
        mIngredientsEditText.setText("");
        mInstructionsEditText.setText("");
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the recipe.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this pet.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deletePet() {
        // Only perform the delete if this is an existing pet.
        if (mCurrentFoodUri != null) {
            // Call the ContentResolver to delete the pet at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentFoodUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentFoodUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_recipe_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_recipe_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

}