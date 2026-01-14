package com.example.studypartner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.studypartner.R;
import com.example.studypartner.data.database.DatabaseHelper;

/**
 * EditPreferredTime
 *
 * Allows users to edit their preferred study time preferences after initial setup.
 * Pre-selects currently selected time slots and validates that at least one slot is chosen.
 * Part of the account settings/preferences flow accessible from the account fragment.
 *
 * Features:
 * - Pre-selected time slots based on current user preferences
 * - Multiple time slot selection via checkboxes (6 slots available)
 * - Validation to ensure at least one time slot is selected
 * - Updates database with new time preferences
 * - Navigates to MainActivity after successful update
 *
 * Available Time Slots:
 * Weekday Morning, Weekday Afternoon, Weekday Evening,
 * Weekend Morning, Weekend Afternoon, Weekend Evening
 */
public class EditPreferredTime extends AppCompatActivity {

    private static final String TAG = "EditPreferredTime";

    // Intent extra keys
    private static final String EXTRA_USER_EMAIL = "userEmail";

    // UI Components
    private CheckBox checkWeekdayMorning;
    private CheckBox checkWeekdayAfternoon;
    private CheckBox checkWeekdayEvening;
    private CheckBox checkWeekendMorning;
    private CheckBox checkWeekendAfternoon;
    private CheckBox checkWeekendEvening;
    private Button buttonSaveProfile;
    private ImageView backButton;

    // Business Logic
    private DatabaseHelper databaseHelper;

    // Data
    private String userEmail;
    private CheckBox[] allCheckBoxes;

    /**
     * Initializes the edit preferred time activity and sets up UI components.
     *
     * @param savedInstanceState Saved state from previous instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_preferred_time);

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
        initializeCheckBoxArray();
    }

    /**
     * Initializes all view references.
     */
    private void initializeViews() {
        checkWeekdayMorning = findViewById(R.id.checkWeekdayMorning);
        checkWeekdayAfternoon = findViewById(R.id.checkWeekdayAfternoon);
        checkWeekdayEvening = findViewById(R.id.checkWeekdayEvening);
        checkWeekendMorning = findViewById(R.id.checkWeekendMorning);
        checkWeekendAfternoon = findViewById(R.id.checkWeekendAfternoon);
        checkWeekendEvening = findViewById(R.id.checkWeekendEvening);
        buttonSaveProfile = findViewById(R.id.buttonSaveProfile);
        backButton = findViewById(R.id.backButton);
    }

    /**
     * Initializes the database helper.
     */
    private void initializeDatabase() {
        databaseHelper = new DatabaseHelper(this);
    }

    /**
     * Initializes the array of all checkboxes for easier iteration.
     */
    private void initializeCheckBoxArray() {
        allCheckBoxes = new CheckBox[]{
            checkWeekdayMorning, checkWeekdayAfternoon, checkWeekdayEvening,
            checkWeekendMorning, checkWeekendAfternoon, checkWeekendEvening
        };
    }

    /**
     * Loads user data from intent and pre-selects current time preferences.
     */
    private void loadUserData() {
        Intent intent = getIntent();
        userEmail = intent.getStringExtra(EXTRA_USER_EMAIL);

        String timePreference = databaseHelper.getUserStudyTimeString(userEmail);
        preSelectTimeSlots(timePreference);

        Log.d(TAG, "Loaded time preferences for editing: " + timePreference);
    }

    /**
     * Pre-selects checkboxes based on current user time preferences.
     *
     * @param currentTimePreferences Comma-separated string of current time slots
     */
    private void preSelectTimeSlots(String currentTimePreferences) {
        if (currentTimePreferences == null || currentTimePreferences.isEmpty()) {
            return;
        }

        for (CheckBox checkBox : allCheckBoxes) {
            String timeSlot = checkBox.getText().toString();
            checkBox.setChecked(currentTimePreferences.contains(timeSlot));
        }
    }

    /**
     * Sets up click listeners for interactive UI components.
     */
    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
        buttonSaveProfile.setOnClickListener(v -> handleSavePreferences());
    }

    /**
     * Handles the save preferences button click.
     * Collects selected time slots, validates, and updates the database.
     */
    private void handleSavePreferences() {
        String selectedTimes = collectSelectedTimeSlots();

        if (!validateTimeSelection(selectedTimes)) {
            return;
        }

        saveTimePreferencesToDatabase(selectedTimes);
    }

    /**
     * Collects all selected time slots into a comma-separated string.
     *
     * @return Comma-separated string of selected time slots
     */
    private String collectSelectedTimeSlots() {
        StringBuilder timePreferences = new StringBuilder();

        if (checkWeekdayMorning.isChecked()) timePreferences.append("Weekday Morning, ");
        if (checkWeekdayAfternoon.isChecked()) timePreferences.append("Weekday Afternoon, ");
        if (checkWeekdayEvening.isChecked()) timePreferences.append("Weekday Evening, ");
        if (checkWeekendMorning.isChecked()) timePreferences.append("Weekend Morning, ");
        if (checkWeekendAfternoon.isChecked()) timePreferences.append("Weekend Afternoon, ");
        if (checkWeekendEvening.isChecked()) timePreferences.append("Weekend Evening, ");

        if (timePreferences.length() > 0) {
            timePreferences.setLength(timePreferences.length() - 2);
        }

        return timePreferences.toString();
    }

    /**
     * Validates that at least one time slot was selected.
     *
     * @param selectedTimes Comma-separated string of selected time slots
     * @return true if at least one time slot selected, false otherwise
     */
    private boolean validateTimeSelection(String selectedTimes) {
        if (selectedTimes.isEmpty()) {
            showErrorMessage("No time slots selected.");
            return false;
        }

        if (userEmail == null || userEmail.isEmpty()) {
            showErrorMessage("User not logged in.");
            return false;
        }

        return true;
    }

    /**
     * Saves the selected time preferences to the database.
     *
     * @param selectedTimes Comma-separated string of selected time slots
     */
    private void saveTimePreferencesToDatabase(String selectedTimes) {
        boolean isUpdated = databaseHelper.updateUserStudyTime(userEmail, selectedTimes);

        if (isUpdated) {
            Log.d(TAG, "Time preferences updated successfully for user: " + userEmail);
            showSuccessMessage("Time preferences updated successfully!");
            navigateToMainActivity();
        } else {
            Log.e(TAG, "Failed to update time preferences for user: " + userEmail);
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