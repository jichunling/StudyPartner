package com.example.studypartner.activities;

import android.content.Intent;
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
import com.example.studypartner.utils.ValidationUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * SignupActivity
 *
 * Handles new user registration for the studyPartner application.
 * Validates user input including email format, password strength, and password confirmation.
 * Creates new user accounts and navigates to login upon successful registration.
 *
 * Features:
 * - Email format validation
 * - Password strength validation (6+ chars, uppercase, lowercase, number)
 * - Password confirmation matching
 * - Password visibility toggle for both password fields
 * - Duplicate email detection
 * - Links to login for existing users
 *
 */
public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    // UI Components
    private TextInputEditText nameInput;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private TextInputEditText confirmPasswordInput;
    private TextInputLayout passwordLayout;
    private TextInputLayout confirmPasswordLayout;
    private Button signupButton;
    private TextView loginLink;

    // Business Logic
    private DatabaseHelper databaseHelper;

    // State
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    /**
     * Initializes the signup activity and sets up UI components.
     *
     * @param savedInstanceState Saved state from previous instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initializeComponents();
        setupClickListeners();
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
        nameInput = findViewById(R.id.userNameEditText);
        emailInput = findViewById(R.id.editTextEmail);
        passwordInput = findViewById(R.id.editTextPassword);
        confirmPasswordInput = findViewById(R.id.confirmPasswordEditText);
        passwordLayout = findViewById(R.id.password_layout);
        confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout);
        signupButton = findViewById(R.id.signup_button);
        loginLink = findViewById(R.id.loginLink);

        // Set styled text for login link
        String htmlText = "Already a member?  <font color='#3344DD'>Login here!</font>";
        loginLink.setText(Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY));
    }

    /**
     * Initializes the database helper.
     */
    private void initializeDatabase() {
        databaseHelper = new DatabaseHelper(this);
    }

    /**
     * Sets up click listeners for all interactive UI components.
     */
    private void setupClickListeners() {
        signupButton.setOnClickListener(v -> handleSignup());
        loginLink.setOnClickListener(v -> navigateToLogin());
        passwordLayout.setEndIconOnClickListener(v -> togglePasswordVisibility());
        confirmPasswordLayout.setEndIconOnClickListener(v -> toggleConfirmPasswordVisibility());
    }

    /**
     * Handles the signup process including validation and registration.
     */
    private void handleSignup() {
        String name = getName();
        String email = getEmail();
        String password = getPassword();
        String confirmPassword = getConfirmPassword();

        if (!validateInput(name, email, password, confirmPassword)) {
            return;
        }

        if (registerUser(email, password)) {
            showSuccessMessage();
            navigateToLogin();
        } else {
            showErrorMessage("The email already exists. Please login!");
        }
    }

    /**
     * Gets the name input value.
     *
     * @return User's full name
     */
    private String getName() {
        return nameInput.getText().toString().trim();
    }

    /**
     * Gets the email input value in lowercase.
     *
     * @return Email address as lowercase string
     */
    private String getEmail() {
        return emailInput.getText().toString().trim().toLowerCase();
    }

    /**
     * Gets the password input value.
     *
     * @return Password string
     */
    private String getPassword() {
        return passwordInput.getText().toString().trim();
    }

    /**
     * Gets the confirm password input value.
     *
     * @return Confirm password string
     */
    private String getConfirmPassword() {
        return confirmPasswordInput.getText().toString().trim();
    }

    /**
     * Validates all user input before attempting registration.
     * Checks for empty name, valid email format, password strength, and password match.
     *
     * @param name User's full name
     * @param email User's email address
     * @param password User's password
     * @param confirmPassword Password confirmation
     * @return true if all input is valid, false otherwise
     */
    private boolean validateInput(String name, String email, String password, String confirmPassword) {
        // Validate name
        if (!ValidationUtils.isValidUserName(name)) {
            showErrorMessage("Name can't be empty");
            return false;
        }

        // Validate email format
        if (!ValidationUtils.isValidEmail(email)) {
            showErrorMessage("Invalid email format");
            return false;
        }

        // Validate password strength
        if (!ValidationUtils.isValidPassword(password)) {
            showErrorMessage("Password must be at least 6 characters, include uppercase, lowercase, and a number");
            return false;
        }

        // Validate password confirmation
        if (!password.equals(confirmPassword)) {
            showErrorMessage("Passwords do not match");
            return false;
        }

        return true;
    }

    /**
     * Registers a new user in the database.
     *
     * @param email User's email
     * @param password User's password
     * @return true if registration successful, false if email already exists
     */
    private boolean registerUser(String email, String password) {
        boolean isInserted = databaseHelper.insertUser(email, password);

        if (isInserted) {
            Log.d(TAG, "User registered successfully: " + email);
        } else {
            Log.d(TAG, "Registration failed - email already exists: " + email);
        }

        return isInserted;
    }

    /**
     * Navigates to the login activity.
     */
    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Toggles password visibility between masked and plain text for the password field.
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
     * Toggles password visibility between masked and plain text for the confirm password field.
     * Updates the eye icon accordingly.
     */
    private void toggleConfirmPasswordVisibility() {
        isConfirmPasswordVisible = !isConfirmPasswordVisible;

        if (isConfirmPasswordVisible) {
            // Show password in plain text
            confirmPasswordInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            confirmPasswordLayout.setEndIconDrawable(R.drawable.baseline_visibility_off_24);
        } else {
            // Hide password with dots
            confirmPasswordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
            confirmPasswordLayout.setEndIconDrawable(R.drawable.baseline_visibility_24);
        }

        // Move cursor to end of text after transformation
        confirmPasswordInput.setSelection(confirmPasswordInput.getText().length());
    }

    /**
     * Displays a success message to the user.
     */
    private void showSuccessMessage() {
        Toast.makeText(this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
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
