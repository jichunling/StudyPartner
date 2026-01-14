package com.example.studypartner.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studypartner.R;
import com.example.studypartner.data.database.DatabaseHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * LoginActivity
 *
 * Handles user authentication for the studyPartner application.
 * Validates user credentials, manages session state, and navigates to the appropriate
 * screen based on whether the user has completed their profile setup.
 *
 * Features:
 * - Email and password validation
 * - Password visibility toggle
 * - Persistent login session management
 * - Auto-navigation for logged-in users
 * - Links to signup and password recovery
 *
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_ID = "userID";

    // UI Components
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private TextInputLayout passwordLayout;
    private Button loginButton;
    private TextView signUpLink;
    private TextView forgotPasswordLink;

    // Business Logic
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;

    // State
    private boolean isPasswordVisible = false;

    /**
     * Initializes the login activity and sets up UI components.
     * Checks for existing user session and auto-navigates if found.
     *
     * @param savedInstanceState Saved state from previous instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeComponents();
        setupClickListeners();
        checkExistingSession();
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
        emailInput = findViewById(R.id.emailEditText);
        passwordInput = findViewById(R.id.passwordEditText);
        passwordLayout = findViewById(R.id.confirmPasswordLayout);
        loginButton = findViewById(R.id.buttonLogin);
        signUpLink = findViewById(R.id.signUpLink);
        forgotPasswordLink = findViewById(R.id.forgotPasswordLink);

        // Set styled text for sign up link
        String htmlText = "Don't have an account?  <font color='#3344DD'>Sign up here!</font>";
        signUpLink.setText(Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY));
    }

    /**
     * Initializes the database helper.
     */
    private void initializeDatabase() {
        databaseHelper = new DatabaseHelper(this);
    }

    /**
     * Initializes SharedPreferences for session management.
     */
    private void initializeSharedPreferences() {
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    }

    /**
     * Sets up click listeners for all interactive UI components.
     */
    private void setupClickListeners() {
        loginButton.setOnClickListener(v -> handleLogin());
        signUpLink.setOnClickListener(v -> navigateToSignup());
        forgotPasswordLink.setOnClickListener(v -> navigateToForgotPassword());
        passwordLayout.setEndIconOnClickListener(v -> togglePasswordVisibility());
    }

    /**
     * Checks for an existing user session and auto-navigates if found.
     */
    private void checkExistingSession() {
        String savedEmail = sharedPreferences.getString(KEY_USER_EMAIL, null);

        if (savedEmail != null) {
            Log.d(TAG, "Existing session found for: " + savedEmail);
            navigateBasedOnSetupStatus(savedEmail);
        } else {
            Log.d(TAG, "No existing session found");
        }
    }

    /**
     * Handles the login process including validation and authentication.
     */
    private void handleLogin() {
        String email = getEmail();
        String password = getPassword();

        if (!validateInput(email, password)) {
            return;
        }

        if (authenticateUser(email, password)) {
            saveUserSession(email);
            showSuccessMessage();
            navigateBasedOnSetupStatus(email);
        } else {
            showErrorMessage("Invalid email or password");
        }
    }

    /**
     * Gets the email input value in lowercase.
     *
     * @return Email address as lowercase string
     */
    private String getEmail() {
        return emailInput.getText().toString().toLowerCase().trim();
    }

    /**
     * Gets the password input value.
     *
     * @return Password string
     */
    private String getPassword() {
        return passwordInput.getText().toString();
    }

    /**
     * Validates user input before attempting authentication.
     *
     * @param email User's email address
     * @param password User's password
     * @return true if input is valid, false otherwise
     */
    private boolean validateInput(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            showErrorMessage("Please fill out all fields");
            return false;
        }
        return true;
    }

    /**
     * Authenticates user credentials against the database.
     *
     * @param email User's email
     * @param password User's password
     * @return true if credentials are valid, false otherwise
     */
    private boolean authenticateUser(String email, String password) {
        return databaseHelper.validateUser(email, password);
    }

    /**
     * Saves user session information to SharedPreferences.
     *
     * @param email User's email to save
     */
    private void saveUserSession(String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Save email
        editor.putString(KEY_USER_EMAIL, email);

        // Save user ID
        String userID = databaseHelper.getUserIDByEmail(email);
        editor.putString(KEY_USER_ID, userID);

        editor.apply();
        Log.d(TAG, "User session saved for: " + email);
    }

    /**
     * Navigates to the appropriate screen based on user setup status.
     * - If setup incomplete: Navigate to UserProfileActivity
     * - If setup complete: Navigate to MainActivity
     *
     * @param email User's email to check setup status
     */
    private void navigateBasedOnSetupStatus(String email) {
        boolean isSetupComplete = databaseHelper.isSetUp(email);

        Intent intent;
        if (isSetupComplete) {
            intent = new Intent(this, MainActivity.class);
            Log.d(TAG, "Navigating to MainActivity - setup complete");
        } else {
            intent = new Intent(this, UserProfileActivity.class);
            Log.d(TAG, "Navigating to UserProfileActivity - setup incomplete");
        }

        startActivity(intent);
        finish();
    }

    /**
     * Navigates to the signup activity.
     */
    private void navigateToSignup() {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Navigates to the forgot password activity.
     */
    private void navigateToForgotPassword() {
        Intent intent = new Intent(this, ForgotPassword.class);
        startActivity(intent);
        finish();
    }

    /**
     * Toggles password visibility between masked and plain text.
     * Updates the eye icon accordingly.
     */
    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;

        if (isPasswordVisible) {
            // Show password in plain text
            passwordInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            passwordLayout.setEndIconDrawable(R.drawable.baseline_visibility_off_24);
        } else {
            // Hide password with dots
            passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
            passwordLayout.setEndIconDrawable(R.drawable.baseline_visibility_24);
        }

        // Move cursor to end of text after transformation
        passwordInput.setSelection(passwordInput.getText().length());
    }

    /**
     * Displays a success message to the user.
     */
    private void showSuccessMessage() {
        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
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
