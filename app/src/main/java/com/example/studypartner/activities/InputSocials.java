package com.example.studypartner.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.studypartner.R;
import com.example.studypartner.data.database.DatabaseHelper;
import com.example.studypartner.utils.ValidationUtils;

/**
 * InputSocials
 *
 * Final step of the user onboarding process where users can optionally provide social media links.
 * This activity allows users to input their LinkedIn, GitHub, and personal website URLs.
 * Users can skip this step by leaving all fields empty to proceed directly to the main app.
 *
 * Features:
 * - Optional social media link input (LinkedIn, GitHub, Personal Website)
 * - URL format validation for provided links
 * - Skip functionality if all fields are empty
 * - Marks user setup as complete upon successful save
 * - Navigates to MainActivity after completion
 */
public class InputSocials extends AppCompatActivity {

    private static final String TAG = "InputSocials";
    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_USER_EMAIL = "userEmail";

    // UI Components
    private EditText editLinkedIn;
    private EditText editGithub;
    private EditText editPersonal;
    private Button buttonSaveProfile;

    // Business Logic
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;

    /**
     * Initializes the social media input activity and sets up UI components.
     *
     * @param savedInstanceState Saved state from previous instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_input_socials);

        setupEdgeToEdge();
        initializeComponents();
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
        initializeSharedPreferences();
    }

    /**
     * Initializes all view references.
     */
    private void initializeViews() {
        editLinkedIn = findViewById(R.id.editLinkedIn);
        editGithub = findViewById(R.id.editGithub);
        editPersonal = findViewById(R.id.editPersonal);
        buttonSaveProfile = findViewById(R.id.buttonSaveProfile);
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
        buttonSaveProfile.setOnClickListener(v -> handleSaveProfile());
    }

    /**
     * Handles the save profile button click.
     * Either saves social links or skips to MainActivity if all fields are empty.
     */
    private void handleSaveProfile() {
        String linkedIn = getLinkedInUrl();
        String github = getGithubUrl();
        String personal = getPersonalUrl();

        if (areAllFieldsEmpty(linkedIn, github, personal)) {
            Log.d(TAG, "All social fields empty - skipping to MainActivity");
            markSetupCompleteAndNavigate();
        } else {
            saveSocials(linkedIn, github, personal);
        }
    }

    /**
     * Gets the LinkedIn URL input value.
     *
     * @return LinkedIn URL string
     */
    private String getLinkedInUrl() {
        return editLinkedIn.getText().toString().trim();
    }

    /**
     * Gets the GitHub URL input value.
     *
     * @return GitHub URL string
     */
    private String getGithubUrl() {
        return editGithub.getText().toString().trim();
    }

    /**
     * Gets the personal website URL input value.
     *
     * @return Personal website URL string
     */
    private String getPersonalUrl() {
        return editPersonal.getText().toString().trim();
    }

    /**
     * Checks if all social media fields are empty.
     *
     * @param linkedIn LinkedIn URL
     * @param github GitHub URL
     * @param personal Personal website URL
     * @return true if all fields are empty, false otherwise
     */
    private boolean areAllFieldsEmpty(String linkedIn, String github, String personal) {
        return linkedIn.isEmpty() && github.isEmpty() && personal.isEmpty();
    }

    /**
     * Validates and saves social media links to the database.
     *
     * @param linkedIn LinkedIn URL
     * @param github GitHub URL
     * @param personal Personal website URL
     */
    private void saveSocials(String linkedIn, String github, String personal) {
        if (!validateSocialUrls(linkedIn, github, personal)) {
            return;
        }

        String userEmail = getUserEmail();
        if (userEmail == null) {
            showErrorMessage("User not logged in.");
            return;
        }

        boolean isUpdated = databaseHelper.saveSocials(userEmail, linkedIn, github, personal);

        if (isUpdated) {
            Log.d(TAG, "Social links saved successfully for user: " + userEmail);
            showSuccessMessage("Socials updated successfully!");
            markSetupCompleteAndNavigate();
        } else {
            Log.e(TAG, "Failed to save social links for user: " + userEmail);
            showErrorMessage("Failed to update preferences.");
        }
    }

    /**
     * Validates social media URLs if they are provided.
     * Empty URLs are considered valid (optional fields).
     *
     * @param linkedIn LinkedIn URL
     * @param github GitHub URL
     * @param personal Personal website URL
     * @return true if all provided URLs are valid, false otherwise
     */
    private boolean validateSocialUrls(String linkedIn, String github, String personal) {
        if (!linkedIn.isEmpty() && !ValidationUtils.isValidUrl(linkedIn)) {
            showErrorMessage("Invalid LinkedIn URL format");
            return false;
        }

        if (!github.isEmpty() && !ValidationUtils.isValidUrl(github)) {
            showErrorMessage("Invalid GitHub URL format");
            return false;
        }

        if (!personal.isEmpty() && !ValidationUtils.isValidUrl(personal)) {
            showErrorMessage("Invalid personal website URL format");
            return false;
        }

        return true;
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
     * Marks the user's setup as complete and navigates to MainActivity.
     */
    private void markSetupCompleteAndNavigate() {
        String userEmail = getUserEmail();
        if (userEmail != null) {
            databaseHelper.setUserSetupComplete(userEmail);
            Log.d(TAG, "User setup marked complete for: " + userEmail);
        }
        navigateToMainActivity();
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