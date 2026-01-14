package com.example.studypartner.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studypartner.R;
import com.example.studypartner.data.database.DatabaseHelper;

/**
 * DifficultyPreferenceActivity
 *
 * Final step of the user profile setup flow where users select their preferred study difficulty level.
 * Presents difficulty options (e.g., Beginner, Intermediate, Advanced) as radio buttons.
 * This helps match users with study partners at similar skill levels for effective learning.
 *
 * Features:
 * - Single difficulty level selection via radio group
 * - Validation to ensure a difficulty is selected
 * - Saves selection to database linked to user's email
 * - Navigates to social media input screen upon completion
 *
 */
public class DifficultyPreferenceActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_USER_EMAIL = "userEmail";

    // UI Components
    private RadioGroup difficultyRadioGroup;
    private Button saveButton;

    // Business Logic
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;

    /**
     * Initializes the difficulty preference activity and sets up UI components.
     *
     * @param savedInstanceState Saved state from previous instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty_preference);

        initializeComponents();
        setupClickListeners();
    }

    /**
     * Initializes all components including views and database helper.
     */
    private void initializeComponents() {
        initializeViews();
        initializeDatabase();
        initializeSharedPreferences();
    }

    /**
     * Initializes all view references.
     */
    private void initializeViews() {
        difficultyRadioGroup = findViewById(R.id.radioGroupDifficulty);
        saveButton = findViewById(R.id.btnSaveDifficulty);
    }

    /**
     * Initializes the database helper.
     */
    private void initializeDatabase() {
        databaseHelper = new DatabaseHelper(this);
    }

    /**
     * Initializes SharedPreferences for accessing user session data.
     */
    private void initializeSharedPreferences() {
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    }

    /**
     * Sets up click listeners for interactive UI components.
     */
    private void setupClickListeners() {
        saveButton.setOnClickListener(v -> handleSave());
    }

    /**
     * Handles the save button click.
     * Collects selected difficulty, validates, saves, and navigates to next screen.
     */
    private void handleSave() {
        String selectedDifficulty = getSelectedDifficulty();

        if (validateDifficultySelection(selectedDifficulty)) {
            saveDifficultyToDatabase(selectedDifficulty);
            navigateToInputSocials();
        } else {
            showErrorMessage("Please select a difficulty level");
        }
    }

    /**
     * Gets the selected difficulty level from radio group.
     *
     * @return Selected difficulty text, or null if nothing selected
     */
    private String getSelectedDifficulty() {
        int selectedId = difficultyRadioGroup.getCheckedRadioButtonId();

        if (selectedId == -1) {
            return null;
        }

        RadioButton selectedRadioButton = findViewById(selectedId);
        return selectedRadioButton.getText().toString();
    }

    /**
     * Validates that a difficulty level was selected.
     *
     * @param difficulty The selected difficulty string
     * @return true if difficulty selected, false otherwise
     */
    private boolean validateDifficultySelection(String difficulty) {
        return difficulty != null && !difficulty.isEmpty();
    }

    /**
     * Saves selected difficulty level to database for the current logged-in user.
     *
     * @param difficulty Selected difficulty level
     */
    private void saveDifficultyToDatabase(String difficulty) {
        String userEmail = getUserEmail();

        if (userEmail == null) {
            showErrorMessage("User not logged in");
            return;
        }

        boolean isUpdated = databaseHelper.updateUserStudyDifficultyLevel(userEmail, difficulty);

        if (isUpdated) {
            showSuccessMessage("Difficulty preference saved!");
        } else {
            showErrorMessage("Failed to save difficulty preference");
        }
    }

    /**
     * Retrieves the logged-in user's email from SharedPreferences.
     *
     * @return User's email address, or null if not logged in
     */
    private String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, null);
    }

    /**
     * Navigates to the social media input activity.
     */
    private void navigateToInputSocials() {
        Intent intent = new Intent(this, InputSocials.class);
        startActivity(intent);
        finish();
    }

    /**
     * Displays a success message to the user.
     *
     * @param message Success message to display
     */
    private void showSuccessMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Displays an error message to the user.
     *
     * @param message Error message to display
     */
    private void showErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
