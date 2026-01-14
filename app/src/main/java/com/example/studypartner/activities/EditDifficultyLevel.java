package com.example.studypartner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.studypartner.R;
import com.example.studypartner.data.database.DatabaseHelper;

/**
 * EditDifficultyLevel
 *
 * Allows users to edit their study difficulty level preference after initial setup.
 * Pre-selects the current difficulty level and validates the update before saving.
 * Part of the account settings/preferences flow accessible from the account fragment.
 *
 * Features:
 * - Pre-selected difficulty level (Easy, Medium, Hard)
 * - Single difficulty selection via radio group
 * - Validation to ensure a difficulty is selected
 * - Updates database with new difficulty preference
 * - Navigates to MainActivity after successful update
 */
public class EditDifficultyLevel extends AppCompatActivity {

    private static final String TAG = "EditDifficultyLevel";

    // Intent extra keys
    private static final String EXTRA_USER_EMAIL = "userEmail";
    private static final String EXTRA_DIFFICULTY_LEVEL = "userDifficultyLevel";

    // Difficulty values
    private static final String DIFFICULTY_EASY = "Easy";
    private static final String DIFFICULTY_MEDIUM = "Medium";
    private static final String DIFFICULTY_HARD = "Hard";

    // UI Components
    private RadioGroup radioGroupDifficulty;
    private Button btnSaveProfile;
    private ImageButton backButton;

    // Business Logic
    private DatabaseHelper databaseHelper;

    // Data
    private String userEmail;

    /**
     * Initializes the edit difficulty level activity and sets up UI components.
     *
     * @param savedInstanceState Saved state from previous instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_difficulty_level);

        setupEdgeToEdge();
        initializeComponents();
        loadUserData();
        setupClickListeners();
    }

    /**
     * Sets up edge-to-edge display with proper window insets.
     */
    private void setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Initializes all components including views and database helper.
     */
    private void initializeComponents() {
        initializeViews();
        initializeDatabase();
    }

    /**
     * Initializes all view references.
     */
    private void initializeViews() {
        radioGroupDifficulty = findViewById(R.id.radioGroupDifficulty);
        btnSaveProfile = findViewById(R.id.buttonSaveProfile);
        backButton = findViewById(R.id.backButton);
    }

    /**
     * Initializes the database helper.
     */
    private void initializeDatabase() {
        databaseHelper = new DatabaseHelper(this);
    }

    /**
     * Loads user data from intent and pre-selects the current difficulty level.
     */
    private void loadUserData() {
        Intent intent = getIntent();
        userEmail = intent.getStringExtra(EXTRA_USER_EMAIL);
        String difficulty = intent.getStringExtra(EXTRA_DIFFICULTY_LEVEL);

        setDifficultySelection(difficulty);
        Log.d(TAG, "Loaded difficulty level for editing: " + difficulty);
    }

    /**
     * Sets the difficulty radio button selection based on the provided difficulty level.
     *
     * @param difficulty Current difficulty level (Easy, Medium, Hard)
     */
    private void setDifficultySelection(String difficulty) {
        if (difficulty == null || difficulty.isEmpty()) {
            return;
        }

        if (DIFFICULTY_EASY.equals(difficulty)) {
            radioGroupDifficulty.check(R.id.radioEasy);
        } else if (DIFFICULTY_MEDIUM.equals(difficulty)) {
            radioGroupDifficulty.check(R.id.radioMedium);
        } else if (DIFFICULTY_HARD.equals(difficulty)) {
            radioGroupDifficulty.check(R.id.radioHard);
        }
    }

    /**
     * Sets up click listeners for interactive UI components.
     */
    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
        btnSaveProfile.setOnClickListener(v -> handleSaveDifficulty());
    }

    /**
     * Handles the save difficulty button click.
     * Validates selection and updates the database.
     */
    private void handleSaveDifficulty() {
        String selectedDifficulty = getSelectedDifficulty();

        if (!validateSelection(selectedDifficulty)) {
            return;
        }

        saveDifficultyToDatabase(selectedDifficulty);
    }

    /**
     * Gets the selected difficulty level from radio group.
     *
     * @return Selected difficulty text, or null if nothing selected
     */
    private String getSelectedDifficulty() {
        int selectedId = radioGroupDifficulty.getCheckedRadioButtonId();

        if (selectedId == -1) {
            return null;
        }

        RadioButton selectedRadioButton = findViewById(selectedId);
        return selectedRadioButton.getText().toString();
    }

    /**
     * Validates that a difficulty level was selected and user is logged in.
     *
     * @param difficulty The selected difficulty string
     * @return true if validation passes, false otherwise
     */
    private boolean validateSelection(String difficulty) {
        if (difficulty == null) {
            showErrorMessage("No difficulty level selected.");
            return false;
        }

        if (userEmail == null || userEmail.isEmpty()) {
            showErrorMessage("User not logged in.");
            return false;
        }

        return true;
    }

    /**
     * Saves the selected difficulty level to the database.
     *
     * @param difficulty Selected difficulty level
     */
    private void saveDifficultyToDatabase(String difficulty) {
        boolean isUpdated = databaseHelper.updateUserStudyDifficultyLevel(userEmail, difficulty);

        if (isUpdated) {
            Log.d(TAG, "Difficulty level updated successfully for user: " + userEmail);
            showSuccessMessage("Study difficulty preferences updated successfully!");
            navigateToMainActivity();
        } else {
            Log.e(TAG, "Failed to update difficulty level for user: " + userEmail);
            showErrorMessage("Failed to update preferences.");
        }
    }

    /**
     * Navigates to the main activity and finishes this activity.
     */
    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
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