package com.example.studypartner.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studypartner.R;
import com.example.studypartner.data.database.DatabaseHelper;
import com.example.studypartner.utils.ValidationUtils;

/**
 * UserProfileActivity
 *
 * First step in the user onboarding flow after signup, where users enter their basic profile information.
 * Collects first name, last name, age, gender, and occupation to build a complete user profile.
 * This information is used for matching users with compatible study partners.
 *
 * Required Fields:
 * - First Name (required)
 * - Last Name (required)
 * - Age (optional but validated if provided)
 * - Gender (optional)
 * - Occupation (optional)
 *
 * Features:
 * - Input validation for required fields
 * - Age validation using ValidationUtils
 * - Radio button group for gender selection
 * - Saves profile data to database
 * - Navigates to topic preference screen upon completion
 *
 */
public class UserProfileActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_USER_EMAIL = "userEmail";

    // UI Components
    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText ageInput;
    private EditText occupationInput;
    private RadioGroup genderRadioGroup;
    private Button saveProfileButton;

    // Business Logic
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;

    /**
     * Initializes the user profile activity and sets up UI components.
     *
     * @param savedInstanceState Saved state from previous instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
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
        firstNameInput = findViewById(R.id.editFirstName);
        lastNameInput = findViewById(R.id.editLastName);
        ageInput = findViewById(R.id.editAge);
        occupationInput = findViewById(R.id.editOccupation);
        genderRadioGroup = findViewById(R.id.radioGroupGender);
        saveProfileButton = findViewById(R.id.buttonSaveProfile);
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
        saveProfileButton.setOnClickListener(v -> handleSaveProfile());
    }

    /**
     * Handles the save profile button click.
     * Collects user input, validates, and saves to database.
     */
    private void handleSaveProfile() {
        String firstName = getFirstName();
        String lastName = getLastName();
        int age = getAge();
        String gender = getSelectedGender();
        String occupation = getOccupation();

        if (!validateProfileInput(firstName, lastName, age)) {
            return;
        }

        saveProfileToDatabase(firstName, lastName, age, gender, occupation);
    }

    /**
     * Gets the first name input value.
     *
     * @return First name as string
     */
    private String getFirstName() {
        return firstNameInput.getText().toString().trim();
    }

    /**
     * Gets the last name input value.
     *
     * @return Last name as string
     */
    private String getLastName() {
        return lastNameInput.getText().toString().trim();
    }

    /**
     * Gets the age input value and converts to integer.
     *
     * @return Age as integer, or 0 if not provided
     */
    private int getAge() {
        String ageString = ageInput.getText().toString().trim();

        if (ageString.isEmpty()) {
            return 0;
        }

        try {
            return Integer.parseInt(ageString);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Gets the selected gender from radio group.
     *
     * @return Selected gender text, or empty string if nothing selected
     */
    private String getSelectedGender() {
        int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();

        if (selectedGenderId == -1) {
            return "";
        }

        RadioButton selectedGenderButton = findViewById(selectedGenderId);
        return selectedGenderButton != null ? selectedGenderButton.getText().toString() : "";
    }

    /**
     * Gets the occupation input value.
     *
     * @return Occupation as string
     */
    private String getOccupation() {
        return occupationInput.getText().toString().trim();
    }

    /**
     * Validates profile input data.
     * Ensures required fields are filled and age is valid if provided.
     *
     * @param firstName User's first name
     * @param lastName User's last name
     * @param age User's age
     * @return true if input is valid, false otherwise
     */
    private boolean validateProfileInput(String firstName, String lastName, int age) {
        // Validate required fields
        if (!ValidationUtils.isValidUserName(firstName) || !ValidationUtils.isValidUserName(lastName)) {
            showErrorMessage("First name and last name are required");
            return false;
        }

        // Validate age if provided
        if (age > 0 && !ValidationUtils.isValidAge(age)) {
            showErrorMessage("Age must be between 13 and 120");
            return false;
        }

        return true;
    }

    /**
     * Saves user profile data to database for the current logged-in user.
     *
     * @param firstName User's first name
     * @param lastName User's last name
     * @param age User's age
     * @param gender User's gender
     * @param occupation User's occupation
     */
    private void saveProfileToDatabase(String firstName, String lastName, int age, String gender, String occupation) {
        String userEmail = getUserEmail();

        if (userEmail == null) {
            showErrorMessage("User not logged in");
            return;
        }

        boolean isUpdated = databaseHelper.updateUserProfile(userEmail, firstName, lastName, age, gender, occupation);

        if (isUpdated) {
            showSuccessMessage("Profile saved successfully!");
            navigateToTopicPreference();
        } else {
            showErrorMessage("Failed to save profile");
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
     * Navigates to the topic preference activity.
     */
    private void navigateToTopicPreference() {
        Intent intent = new Intent(this, TopicPreferenceActivity.class);
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
