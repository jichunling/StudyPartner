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
 * TopicPreferenceActivity
 *
 * Part of the user profile setup flow where new users select their study topics.
 * Presents a list of academic subjects as checkboxes and saves selections to the database.
 * This helps the matching algorithm find compatible study partners with similar interests.
 *
 * Available Topics:
 * - Computer Science, Biology, Chemistry, Mathematics, Engineering
 * - Physics, English, French, History, Philosophy
 *
 * Features:
 * - Multiple topic selection via checkboxes
 * - Validation to ensure at least one topic is selected
 * - Saves selections to database linked to user's email
 * - Navigates to study time preference screen upon completion
 *
 */
public class TopicPreferenceActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_USER_EMAIL = "userEmail";

    // UI Components
    private Button nextButton;
    private CheckBox computerScienceCheckbox;
    private CheckBox biologyCheckbox;
    private CheckBox chemistryCheckbox;
    private CheckBox mathematicsCheckbox;
    private CheckBox engineeringCheckbox;
    private CheckBox physicsCheckbox;
    private CheckBox englishCheckbox;
    private CheckBox frenchCheckbox;
    private CheckBox historyCheckbox;
    private CheckBox philosophyCheckbox;

    // Business Logic
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;

    /**
     * Initializes the topic preference activity and sets up UI components.
     *
     * @param savedInstanceState Saved state from previous instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_preference);

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
        nextButton = findViewById(R.id.buttonNext);

        // Initialize all topic checkboxes
        computerScienceCheckbox = findViewById(R.id.checkComputerScience);
        biologyCheckbox = findViewById(R.id.checkBiology);
        chemistryCheckbox = findViewById(R.id.checkChemistry);
        mathematicsCheckbox = findViewById(R.id.checkMathematics);
        engineeringCheckbox = findViewById(R.id.checkEngineering);
        physicsCheckbox = findViewById(R.id.checkPhysics);
        englishCheckbox = findViewById(R.id.checkEnglish);
        frenchCheckbox = findViewById(R.id.checkFrench);
        historyCheckbox = findViewById(R.id.checkHistory);
        philosophyCheckbox = findViewById(R.id.checkPhilosophy);
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
     * Collects selected topics, validates, saves, and navigates to next screen.
     */
    private void handleNext() {
        String selectedTopics = collectSelectedTopics();

        if (validateTopicSelection(selectedTopics)) {
            saveTopicsToDatabase(selectedTopics);
            navigateToStudyTimePreference();
        } else {
            showErrorMessage("Please select at least one topic");
        }
    }

    /**
     * Collects all selected topics from checkboxes and formats as comma-separated string.
     *
     * @return Comma-separated string of selected topics, or empty string if none selected
     */
    private String collectSelectedTopics() {
        StringBuilder selectedTopics = new StringBuilder();

        // Check each checkbox and append topic name if selected
        if (computerScienceCheckbox.isChecked()) selectedTopics.append("Computer Science, ");
        if (biologyCheckbox.isChecked()) selectedTopics.append("Biology, ");
        if (chemistryCheckbox.isChecked()) selectedTopics.append("Chemistry, ");
        if (mathematicsCheckbox.isChecked()) selectedTopics.append("Mathematics, ");
        if (engineeringCheckbox.isChecked()) selectedTopics.append("Engineering, ");
        if (physicsCheckbox.isChecked()) selectedTopics.append("Physics, ");
        if (englishCheckbox.isChecked()) selectedTopics.append("English, ");
        if (frenchCheckbox.isChecked()) selectedTopics.append("French, ");
        if (historyCheckbox.isChecked()) selectedTopics.append("History, ");
        if (philosophyCheckbox.isChecked()) selectedTopics.append("Philosophy, ");

        // Remove trailing comma and space if any topics were selected
        if (selectedTopics.length() > 0) {
            selectedTopics.setLength(selectedTopics.length() - 2);
        }

        return selectedTopics.toString();
    }

    /**
     * Validates that at least one topic was selected.
     *
     * @param topics The collected topics string
     * @return true if at least one topic selected, false otherwise
     */
    private boolean validateTopicSelection(String topics) {
        return topics != null && !topics.isEmpty();
    }

    /**
     * Saves selected topics to database for the current logged-in user.
     *
     * @param topics Comma-separated string of selected topics
     */
    private void saveTopicsToDatabase(String topics) {
        String userEmail = getUserEmail();

        if (userEmail == null) {
            showErrorMessage("User not logged in");
            return;
        }

        boolean isSaved = databaseHelper.updateUserTopic(userEmail, topics);

        if (isSaved) {
            showSuccessMessage("Topics saved! Selected: " + topics);
        } else {
            showErrorMessage("Failed to save topics");
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
     * Navigates to the study time preference activity.
     */
    private void navigateToStudyTimePreference() {
        Intent intent = new Intent(this, StudyTimePreferenceActivity.class);
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
