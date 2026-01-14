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
 * EditMyTopicPreferences
 *
 * Allows users to edit their study topic preferences after initial setup.
 * Pre-selects currently selected topics and validates that at least one topic is chosen.
 * Part of the account settings/preferences flow accessible from the account fragment.
 *
 * Features:
 * - Pre-selected topics based on current user preferences
 * - Multiple topic selection via checkboxes (10 topics available)
 * - Validation to ensure at least one topic is selected
 * - Updates database with new topic preferences
 * - Navigates to MainActivity after successful update
 *
 * Available Topics:
 * Computer Science, Biology, Chemistry, Mathematics, Engineering,
 * Physics, English, French, History, Philosophy
 */
public class EditMyTopicPreferences extends AppCompatActivity {

    private static final String TAG = "EditMyTopicPref";

    // Intent extra keys
    private static final String EXTRA_USER_EMAIL = "userEmail";

    // UI Components
    private CheckBox checkComputerScience;
    private CheckBox checkBiology;
    private CheckBox checkChemistry;
    private CheckBox checkMathematics;
    private CheckBox checkEngineering;
    private CheckBox checkPhysics;
    private CheckBox checkEnglish;
    private CheckBox checkFrench;
    private CheckBox checkHistory;
    private CheckBox checkPhilosophy;
    private Button saveTopics;
    private ImageView backButton;

    // Business Logic
    private DatabaseHelper databaseHelper;

    // Data
    private String userEmail;
    private CheckBox[] allCheckBoxes;

    /**
     * Initializes the edit topic preferences activity and sets up UI components.
     *
     * @param savedInstanceState Saved state from previous instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_my_topic_preferences);

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
        checkComputerScience = findViewById(R.id.editCheckComputerScience);
        checkBiology = findViewById(R.id.editCheckBiology);
        checkChemistry = findViewById(R.id.editCheckChemistry);
        checkMathematics = findViewById(R.id.editCheckMathematics);
        checkEngineering = findViewById(R.id.editCheckEngineering);
        checkPhysics = findViewById(R.id.editCheckPhysics);
        checkEnglish = findViewById(R.id.editCheckEnglish);
        checkFrench = findViewById(R.id.editCheckFrench);
        checkHistory = findViewById(R.id.editCheckHistory);
        checkPhilosophy = findViewById(R.id.editCheckPhilosophy);
        saveTopics = findViewById(R.id.saveButton);
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
            checkComputerScience, checkBiology, checkChemistry, checkMathematics,
            checkEngineering, checkPhysics, checkEnglish, checkFrench,
            checkHistory, checkPhilosophy
        };
    }

    /**
     * Loads user data from intent and pre-selects current topic preferences.
     */
    private void loadUserData() {
        Intent intent = getIntent();
        userEmail = intent.getStringExtra(EXTRA_USER_EMAIL);

        String currentUserTopics = databaseHelper.getUserTopicString(userEmail);
        preSelectTopics(currentUserTopics);

        Log.d(TAG, "Loaded topics for editing: " + currentUserTopics);
    }

    /**
     * Pre-selects checkboxes based on current user topic preferences.
     *
     * @param currentTopics Comma-separated string of current topics
     */
    private void preSelectTopics(String currentTopics) {
        if (currentTopics == null || currentTopics.isEmpty()) {
            return;
        }

        for (CheckBox checkBox : allCheckBoxes) {
            String topicName = checkBox.getText().toString();
            checkBox.setChecked(currentTopics.contains(topicName));
        }
    }

    /**
     * Sets up click listeners for interactive UI components.
     */
    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
        saveTopics.setOnClickListener(v -> handleSaveTopics());
    }

    /**
     * Handles the save topics button click.
     * Collects selected topics, validates, and updates the database.
     */
    private void handleSaveTopics() {
        String selectedTopics = collectSelectedTopics();

        if (!validateTopicSelection(selectedTopics)) {
            return;
        }

        saveTopicsToDatabase(selectedTopics);
    }

    /**
     * Collects all selected topics into a comma-separated string.
     *
     * @return Comma-separated string of selected topics
     */
    private String collectSelectedTopics() {
        StringBuilder selectedTopics = new StringBuilder();

        if (checkComputerScience.isChecked()) selectedTopics.append("Computer Science, ");
        if (checkBiology.isChecked()) selectedTopics.append("Biology, ");
        if (checkChemistry.isChecked()) selectedTopics.append("Chemistry, ");
        if (checkMathematics.isChecked()) selectedTopics.append("Mathematics, ");
        if (checkEngineering.isChecked()) selectedTopics.append("Engineering, ");
        if (checkPhysics.isChecked()) selectedTopics.append("Physics, ");
        if (checkEnglish.isChecked()) selectedTopics.append("English, ");
        if (checkFrench.isChecked()) selectedTopics.append("French, ");
        if (checkHistory.isChecked()) selectedTopics.append("History, ");
        if (checkPhilosophy.isChecked()) selectedTopics.append("Philosophy, ");

        if (selectedTopics.length() > 0) {
            selectedTopics.setLength(selectedTopics.length() - 2);
        }

        return selectedTopics.toString();
    }

    /**
     * Validates that at least one topic was selected.
     *
     * @param topics Comma-separated string of selected topics
     * @return true if at least one topic selected, false otherwise
     */
    private boolean validateTopicSelection(String topics) {
        if (topics.isEmpty()) {
            showErrorMessage("No topics selected.");
            return false;
        }
        return true;
    }

    /**
     * Saves the selected topics to the database.
     *
     * @param topics Comma-separated string of selected topics
     */
    private void saveTopicsToDatabase(String topics) {
        if (userEmail == null || userEmail.isEmpty()) {
            showErrorMessage("User not logged in.");
            return;
        }

        boolean isSaved = databaseHelper.updateUserTopic(userEmail, topics);

        if (isSaved) {
            Log.d(TAG, "Topics updated successfully for user: " + userEmail);
            showSuccessMessage("Topics Updated Successfully!");
            navigateToMainActivity();
        } else {
            Log.e(TAG, "Failed to update topics for user: " + userEmail);
            showErrorMessage("Failed to save topics.");
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