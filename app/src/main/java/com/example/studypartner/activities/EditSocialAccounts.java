package com.example.studypartner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.studypartner.R;
import com.example.studypartner.data.database.DatabaseHelper;
import com.example.studypartner.utils.ValidationUtils;
import android.widget.EditText;

/**
 * EditSocialAccounts
 *
 * Activity for editing user social media links and personal website.
 * Allows users to update their LinkedIn, GitHub, and personal website URLs
 * to share with potential study partners.
 *
 * Features:
 * - Pre-populated fields with existing social links
 * - URL validation using ValidationUtils
 * - Back button navigation
 * - Saves updated links to database
 * - Navigates to main screen upon successful save
 *
 * Required Intent Extras:
 * - "linkedIn": Current LinkedIn URL (can be empty)
 * - "github": Current GitHub URL (can be empty)
 * - "personal": Current personal website URL (can be empty)
 * - "userEmail": User's email address (required)
 *
 */
public class EditSocialAccounts extends AppCompatActivity {

    // UI Components
    private ImageButton backButton;
    private EditText linkedInInput;
    private EditText githubInput;
    private EditText personalInput;
    private Button saveButton;

    // Business Logic
    private DatabaseHelper databaseHelper;

    // State
    private String userEmail;

    /**
     * Initializes the edit social accounts activity and sets up UI components.
     *
     * @param savedInstanceState Saved state from previous instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_social_accounts);
        setupWindowInsets();

        initializeComponents();
        loadExistingSocials();
        setupClickListeners();
    }

    /**
     * Sets up window insets for edge-to-edge display.
     */
    private void setupWindowInsets() {
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
        backButton = findViewById(R.id.backButton);
        linkedInInput = findViewById(R.id.editLinkedIn);
        githubInput = findViewById(R.id.editGithub);
        personalInput = findViewById(R.id.editPersonal);
        saveButton = findViewById(R.id.buttonSaveProfile);
    }

    /**
     * Initializes the database helper.
     */
    private void initializeDatabase() {
        databaseHelper = new DatabaseHelper(this);
    }

    /**
     * Loads existing social media links from intent extras and populates the input fields.
     */
    private void loadExistingSocials() {
        Intent intent = getIntent();

        // Get existing social links from intent
        String linkedInLink = intent.getStringExtra("linkedIn");
        String githubLink = intent.getStringExtra("github");
        String personalLink = intent.getStringExtra("personal");
        userEmail = intent.getStringExtra("userEmail");

        // Pre-populate fields with existing data
        if (linkedInLink != null) linkedInInput.setText(linkedInLink);
        if (githubLink != null) githubInput.setText(githubLink);
        if (personalLink != null) personalInput.setText(personalLink);
    }

    /**
     * Sets up click listeners for interactive UI components.
     */
    private void setupClickListeners() {
        backButton.setOnClickListener(v -> handleBack());
        saveButton.setOnClickListener(v -> handleSave());
    }

    /**
     * Handles the back button click.
     * Returns to the previous screen without saving.
     */
    private void handleBack() {
        finish();
    }

    /**
     * Handles the save button click.
     * Validates URLs, saves to database, and navigates to main screen.
     */
    private void handleSave() {
        String linkedInUrl = getLinkedInUrl();
        String githubUrl = getGithubUrl();
        String personalUrl = getPersonalUrl();

        // Validate URLs
        if (!validateSocialUrls(linkedInUrl, githubUrl, personalUrl)) {
            return;
        }

        // Validate user email
        if (!isUserEmailValid()) {
            showErrorMessage("User not logged in");
            return;
        }

        // Save to database
        saveSocialsToDatabase(linkedInUrl, githubUrl, personalUrl);
    }

    /**
     * Gets the LinkedIn URL input value.
     *
     * @return LinkedIn URL as string
     */
    private String getLinkedInUrl() {
        return linkedInInput.getText() != null ? linkedInInput.getText().toString().trim() : "";
    }

    /**
     * Gets the GitHub URL input value.
     *
     * @return GitHub URL as string
     */
    private String getGithubUrl() {
        return githubInput.getText() != null ? githubInput.getText().toString().trim() : "";
    }

    /**
     * Gets the personal website URL input value.
     *
     * @return Personal website URL as string
     */
    private String getPersonalUrl() {
        return personalInput.getText() != null ? personalInput.getText().toString().trim() : "";
    }

    /**
     * Validates all social media URLs using ValidationUtils.
     *
     * @param linkedInUrl LinkedIn URL to validate
     * @param githubUrl GitHub URL to validate
     * @param personalUrl Personal website URL to validate
     * @return true if all URLs are valid (or empty), false otherwise
     */
    private boolean validateSocialUrls(String linkedInUrl, String githubUrl, String personalUrl) {
        // Validate LinkedIn URL
        if (!ValidationUtils.isValidUrl(linkedInUrl)) {
            showErrorMessage("Invalid LinkedIn URL format");
            return false;
        }

        // Validate GitHub URL
        if (!ValidationUtils.isValidUrl(githubUrl)) {
            showErrorMessage("Invalid GitHub URL format");
            return false;
        }

        // Validate personal website URL
        if (!ValidationUtils.isValidUrl(personalUrl)) {
            showErrorMessage("Invalid personal website URL format");
            return false;
        }

        return true;
    }

    /**
     * Checks if the user email is valid.
     *
     * @return true if user email is not null or empty, false otherwise
     */
    private boolean isUserEmailValid() {
        return userEmail != null && !userEmail.trim().isEmpty();
    }

    /**
     * Saves social media URLs to database for the current user.
     *
     * @param linkedInUrl LinkedIn URL
     * @param githubUrl GitHub URL
     * @param personalUrl Personal website URL
     */
    private void saveSocialsToDatabase(String linkedInUrl, String githubUrl, String personalUrl) {
        boolean isUpdated = databaseHelper.saveSocials(userEmail, linkedInUrl, githubUrl, personalUrl);

        if (isUpdated) {
            showSuccessMessage("Social accounts updated successfully!");
            navigateToMainActivity();
        } else {
            showErrorMessage("Failed to update social accounts");
        }
    }

    /**
     * Navigates to the main activity.
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
