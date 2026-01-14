package com.example.studypartner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.studypartner.utils.ValidationUtils;

/**
 * EditPersonalInformation
 *
 * Allows users to edit their personal information after initial profile setup.
 * Pre-populates fields with existing user data and validates updates before saving.
 * Part of the account settings/preferences flow accessible from the account fragment.
 *
 * Features:
 * - Pre-populated fields with existing user data
 * - Edit first name, last name, age, gender, and occupation
 * - Age validation (must be between 13-120)
 * - Required fields: first name and last name
 * - Updates database with new information
 * - Navigates to MainActivity after successful update
 */
public class EditPersonalInformation extends AppCompatActivity {

    private static final String TAG = "EditPersonalInfo";

    // Intent extra keys
    private static final String EXTRA_FIRST_NAME = "userFirstName";
    private static final String EXTRA_LAST_NAME = "userLastName";
    private static final String EXTRA_AGE = "userAge";
    private static final String EXTRA_OCCUPATION = "userOccupation";
    private static final String EXTRA_GENDER = "userGender";
    private static final String EXTRA_EMAIL = "userEmail";

    // Gender values
    private static final String GENDER_MALE = "Male";
    private static final String GENDER_FEMALE = "Female";

    // UI Components
    private EditText editFirstName;
    private EditText editLastName;
    private EditText editAge;
    private EditText editOccupation;
    private RadioGroup radioGroupGender;
    private Button btnSaveProfile;
    private ImageButton backButton;

    // Business Logic
    private DatabaseHelper databaseHelper;

    // Data
    private String userEmail;

    /**
     * Initializes the edit personal information activity and sets up UI components.
     *
     * @param savedInstanceState Saved state from previous instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_personal_information);

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
        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        editAge = findViewById(R.id.editAge);
        editOccupation = findViewById(R.id.editOccupationText);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        backButton = findViewById(R.id.backButton);
        btnSaveProfile = findViewById(R.id.buttonSaveProfile);
    }

    /**
     * Initializes the database helper.
     */
    private void initializeDatabase() {
        databaseHelper = new DatabaseHelper(this);
    }

    /**
     * Loads user data from intent and populates the UI fields.
     */
    private void loadUserData() {
        Intent intent = getIntent();
        String firstName = intent.getStringExtra(EXTRA_FIRST_NAME);
        String lastName = intent.getStringExtra(EXTRA_LAST_NAME);
        String age = intent.getStringExtra(EXTRA_AGE);
        String occupation = intent.getStringExtra(EXTRA_OCCUPATION);
        String gender = intent.getStringExtra(EXTRA_GENDER);
        userEmail = intent.getStringExtra(EXTRA_EMAIL);

        populateFields(firstName, lastName, age, occupation, gender);
        Log.d(TAG, "Loaded user data for editing: " + userEmail);
    }

    /**
     * Populates the UI fields with user data.
     *
     * @param firstName User's first name
     * @param lastName User's last name
     * @param age User's age as string
     * @param occupation User's occupation
     * @param gender User's gender
     */
    private void populateFields(String firstName, String lastName, String age, String occupation, String gender) {
        editFirstName.setText(firstName);
        editLastName.setText(lastName);
        editAge.setText(age);
        editOccupation.setText(occupation);
        setGenderSelection(gender);
    }

    /**
     * Sets the gender radio button selection based on the provided gender.
     *
     * @param gender User's gender (Male, Female, or Other)
     */
    private void setGenderSelection(String gender) {
        if (gender == null || gender.isEmpty()) {
            return;
        }

        if (GENDER_FEMALE.equals(gender)) {
            radioGroupGender.check(R.id.radioFemale);
        } else if (GENDER_MALE.equals(gender)) {
            radioGroupGender.check(R.id.radioMale);
        } else {
            radioGroupGender.check(R.id.radioOther);
        }
    }

    /**
     * Sets up click listeners for interactive UI components.
     */
    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
        btnSaveProfile.setOnClickListener(v -> handleSaveProfile());
    }

    /**
     * Handles the save profile button click.
     * Validates input and updates the database.
     */
    private void handleSaveProfile() {
        String firstName = getFirstName();
        String lastName = getLastName();
        String ageString = getAge();
        String occupation = getOccupation();
        String gender = getSelectedGender();

        if (!validateInput(firstName, lastName, ageString)) {
            return;
        }

        int age = parseAge(ageString);
        saveProfileToDatabase(firstName, lastName, age, gender, occupation);
    }

    /**
     * Gets the first name input value.
     *
     * @return First name string
     */
    private String getFirstName() {
        return editFirstName.getText().toString().trim();
    }

    /**
     * Gets the last name input value.
     *
     * @return Last name string
     */
    private String getLastName() {
        return editLastName.getText().toString().trim();
    }

    /**
     * Gets the age input value.
     *
     * @return Age string
     */
    private String getAge() {
        return editAge.getText().toString().trim();
    }

    /**
     * Gets the occupation input value.
     *
     * @return Occupation string
     */
    private String getOccupation() {
        return editOccupation.getText().toString().trim();
    }

    /**
     * Gets the selected gender from radio group.
     *
     * @return Selected gender text, or empty string if nothing selected
     */
    private String getSelectedGender() {
        int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();
        RadioButton selectedGenderButton = findViewById(selectedGenderId);
        return (selectedGenderButton != null) ? selectedGenderButton.getText().toString() : "";
    }

    /**
     * Validates user input before saving.
     *
     * @param firstName User's first name
     * @param lastName User's last name
     * @param ageString User's age as string
     * @return true if validation passes, false otherwise
     */
    private boolean validateInput(String firstName, String lastName, String ageString) {
        if (firstName.isEmpty() || lastName.isEmpty()) {
            showErrorMessage("First name and Last name are required.");
            return false;
        }

        if (!ValidationUtils.isValidUserName(firstName)) {
            showErrorMessage("First name cannot be empty");
            return false;
        }

        if (!ValidationUtils.isValidUserName(lastName)) {
            showErrorMessage("Last name cannot be empty");
            return false;
        }

        if (!ageString.isEmpty()) {
            try {
                int age = Integer.parseInt(ageString);
                if (!ValidationUtils.isValidAge(age)) {
                    showErrorMessage("Age must be between " + ValidationUtils.getMinAge() +
                                   " and " + ValidationUtils.getMaxAge());
                    return false;
                }
            } catch (NumberFormatException e) {
                showErrorMessage("Please enter a valid age");
                return false;
            }
        }

        return true;
    }

    /**
     * Parses age string to integer, returning 0 if empty.
     *
     * @param ageString Age as string
     * @return Age as integer, or 0 if empty
     */
    private int parseAge(String ageString) {
        if (ageString.isEmpty()) {
            return 0;
        }

        try {
            return Integer.parseInt(ageString);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Failed to parse age: " + ageString, e);
            return 0;
        }
    }

    /**
     * Saves the updated profile information to the database.
     *
     * @param firstName User's first name
     * @param lastName User's last name
     * @param age User's age
     * @param gender User's gender
     * @param occupation User's occupation
     */
    private void saveProfileToDatabase(String firstName, String lastName, int age, String gender, String occupation) {
        boolean isUpdated = databaseHelper.updateUserProfile(userEmail, firstName, lastName, age, gender, occupation);

        if (isUpdated) {
            Log.d(TAG, "Profile updated successfully for user: " + userEmail);
            showSuccessMessage("Profile updated successfully!");
            navigateToMainActivity();
        } else {
            Log.e(TAG, "Failed to update profile for user: " + userEmail);
            showErrorMessage("Failed to update profile.");
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