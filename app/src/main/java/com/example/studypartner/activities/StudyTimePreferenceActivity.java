package com.example.studypartner.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studypartner.R;
import com.example.studypartner.data.database.DatabaseHelper;

/**
 * StudyTimePreferenceActivity
 *
 * Part of the user profile setup flow where users select their preferred study times.
 * Presents weekday and weekend time slots (Morning, Afternoon, Evening) as checkboxes.
 * This helps match users with compatible schedules for effective study partnerships.
 *
 * Available Time Slots:
 * - Weekday: Morning, Afternoon, Evening
 * - Weekend: Morning, Afternoon, Evening
 *
 * Features:
 * - Multiple time slot selection via checkboxes
 * - Validation to ensure at least one time slot is selected
 * - Saves selections to database linked to user's email
 * - Navigates to difficulty preference screen upon completion
 *
 */
public class StudyTimePreferenceActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_USER_EMAIL = "userEmail";

    // UI Components
    private Button nextButton;
    private CheckBox weekdayMorningCheckbox;
    private CheckBox weekdayAfternoonCheckbox;
    private CheckBox weekdayEveningCheckbox;
    private CheckBox weekendMorningCheckbox;
    private CheckBox weekendAfternoonCheckbox;
    private CheckBox weekendEveningCheckbox;

    // Business Logic
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;

    /**
     * Initializes the study time preference activity and sets up UI components.
     *
     * @param savedInstanceState Saved state from previous instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_time_preference);

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
        nextButton = findViewById(R.id.buttonNext2);

        // Initialize weekday time checkboxes
        weekdayMorningCheckbox = findViewById(R.id.checkWeekdayMorning);
        weekdayAfternoonCheckbox = findViewById(R.id.checkWeekdayAfternoon);
        weekdayEveningCheckbox = findViewById(R.id.checkWeekdayEvening);

        // Initialize weekend time checkboxes
        weekendMorningCheckbox = findViewById(R.id.checkWeekendMorning);
        weekendAfternoonCheckbox = findViewById(R.id.checkWeekendAfternoon);
        weekendEveningCheckbox = findViewById(R.id.checkWeekendEvening);
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
        nextButton.setOnClickListener(v -> handleNext());
    }

    /**
     * Handles the next button click.
     * Collects selected time slots, validates, saves, and navigates to next screen.
     */
    private void handleNext() {
        String selectedTimes = collectSelectedTimes();

        if (validateTimeSelection(selectedTimes)) {
            saveTimesToDatabase(selectedTimes);
            navigateToDifficultyPreference();
        } else {
            showErrorMessage("Please select at least one study time");
        }
    }

    /**
     * Collects all selected time slots from checkboxes and formats as comma-separated string.
     *
     * @return Comma-separated string of selected time slots, or empty string if none selected
     */
    private String collectSelectedTimes() {
        StringBuilder timePreferences = new StringBuilder();

        // Check weekday time slots
        if (weekdayMorningCheckbox.isChecked()) timePreferences.append("Weekday Morning, ");
        if (weekdayAfternoonCheckbox.isChecked()) timePreferences.append("Weekday Afternoon, ");
        if (weekdayEveningCheckbox.isChecked()) timePreferences.append("Weekday Evening, ");

        // Check weekend time slots
        if (weekendMorningCheckbox.isChecked()) timePreferences.append("Weekend Morning, ");
        if (weekendAfternoonCheckbox.isChecked()) timePreferences.append("Weekend Afternoon, ");
        if (weekendEveningCheckbox.isChecked()) timePreferences.append("Weekend Evening, ");

        // Remove trailing comma and space if any times were selected
        if (timePreferences.length() > 0) {
            timePreferences.setLength(timePreferences.length() - 2);
        }

        return timePreferences.toString();
    }

    /**
     * Validates that at least one time slot was selected.
     *
     * @param times The collected time slots string
     * @return true if at least one time selected, false otherwise
     */
    private boolean validateTimeSelection(String times) {
        return times != null && !times.isEmpty();
    }

    /**
     * Saves selected time slots to database for the current logged-in user.
     *
     * @param times Comma-separated string of selected time slots
     */
    private void saveTimesToDatabase(String times) {
        String userEmail = getUserEmail();

        if (userEmail == null) {
            showErrorMessage("User not logged in");
            return;
        }

        boolean isUpdated = databaseHelper.updateUserStudyTime(userEmail, times);

        if (isUpdated) {
            showSuccessMessage("Study time preferences saved! Selected: " + times);
        } else {
            showErrorMessage("Failed to save study time preferences");
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
     * Navigates to the difficulty preference activity.
     */
    private void navigateToDifficultyPreference() {
        Intent intent = new Intent(this, DifficultyPreferenceActivity.class);
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
