package com.example.studypartner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * ResetPassword
 *
 * Handles password reset functionality after successful email verification.
 * Users receive their email via intent from the ForgotPassword flow and can set a new password.
 * Validates password strength and confirmation matching before updating in the database.
 *
 * Features:
 * - Email pre-filled from intent (read-only)
 * - New password and confirmation input
 * - Password strength validation (6+ chars, uppercase, lowercase, number)
 * - Password matching verification
 * - Password visibility toggle for both fields
 * - Updates password in database upon successful validation
 * - Navigates to MainActivity after successful reset
 */
public class ResetPassword extends AppCompatActivity {

    private static final String TAG = "ResetPassword";
    private static final String EXTRA_USER_EMAIL = "userEmail";

    // UI Components
    private TextInputEditText editTextEmail;
    private TextInputEditText editTextPassword;
    private TextInputEditText confirmPasswordEditText;
    private ImageButton backButton;
    private TextInputLayout passwordLayout;
    private TextInputLayout confirmPasswordLayout;
    private Button buttonSaveProfile;

    // Business Logic
    private DatabaseHelper databaseHelper;

    // State
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    // Data
    private String userEmail;

    /**
     * Initializes the reset password activity and sets up UI components.
     *
     * @param savedInstanceState Saved state from previous instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password);

        setupEdgeToEdge();
        initializeComponents();
        loadUserEmail();
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
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        backButton = findViewById(R.id.backButton);
        passwordLayout = findViewById(R.id.password_layout);
        confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout);
        buttonSaveProfile = findViewById(R.id.buttonSaveProfile);
    }

    /**
     * Initializes the database helper.
     */
    private void initializeDatabase() {
        databaseHelper = new DatabaseHelper(this);
    }

    /**
     * Loads user email from intent and displays it in the email field.
     */
    private void loadUserEmail() {
        Intent intent = getIntent();
        userEmail = intent.getStringExtra(EXTRA_USER_EMAIL);

        if (userEmail != null) {
            editTextEmail.setText(userEmail);
            Log.d(TAG, "Loaded email for password reset: " + userEmail);
        } else {
            Log.e(TAG, "No email provided in intent");
            showErrorMessage("Error: No email provided");
            finish();
        }
    }

    /**
     * Sets up click listeners for interactive UI components.
     */
    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
        buttonSaveProfile.setOnClickListener(v -> handlePasswordReset());
        passwordLayout.setEndIconOnClickListener(v -> togglePasswordVisibility());
        confirmPasswordLayout.setEndIconOnClickListener(v -> toggleConfirmPasswordVisibility());
    }

    /**
     * Handles the password reset process including validation and database update.
     */
    private void handlePasswordReset() {
        String newPassword = getNewPassword();
        String confirmNewPassword = getConfirmPassword();

        if (!validateInput(newPassword, confirmNewPassword)) {
            return;
        }

        if (updatePassword(newPassword)) {
            showSuccessMessage("Password Updated Successfully");
            navigateToMainActivity();
        } else {
            showErrorMessage("Can't update password. Please try again");
        }
    }

    /**
     * Gets the new password input value.
     *
     * @return New password string
     */
    private String getNewPassword() {
        return editTextPassword.getText().toString().trim();
    }

    /**
     * Gets the confirm password input value.
     *
     * @return Confirm password string
     */
    private String getConfirmPassword() {
        return confirmPasswordEditText.getText().toString().trim();
    }

    /**
     * Validates password input including emptiness, strength, and matching.
     *
     * @param newPassword New password
     * @param confirmNewPassword Confirm password
     * @return true if validation passes, false otherwise
     */
    private boolean validateInput(String newPassword, String confirmNewPassword) {
        if (newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
            showErrorMessage("Passwords can't be empty");
            return false;
        }

        if (!ValidationUtils.isValidPassword(newPassword)) {
            showErrorMessage("Password must be at least 6 characters, include uppercase, lowercase, and a number");
            return false;
        }

        if (!newPassword.equals(confirmNewPassword)) {
            showErrorMessage("Passwords do not match");
            return false;
        }

        return true;
    }

    /**
     * Updates the password in the database for the current user.
     *
     * @param newPassword The new password to set
     * @return true if update successful, false otherwise
     */
    private boolean updatePassword(String newPassword) {
        boolean rowsUpdated = databaseHelper.updatePassword(userEmail, newPassword);

        if (rowsUpdated) {
            Log.d(TAG, "Password updated successfully for user: " + userEmail);
        } else {
            Log.e(TAG, "Failed to update password for user: " + userEmail);
        }

        return rowsUpdated;
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
     * Toggles password visibility between masked and plain text for the password field.
     * Updates the eye icon accordingly.
     */
    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;

        if (isPasswordVisible) {
            // Show password in plain text
            editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            passwordLayout.setEndIconDrawable(R.drawable.baseline_visibility_off_24);
        } else {
            // Hide password with dots
            editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            passwordLayout.setEndIconDrawable(R.drawable.baseline_visibility_24);
        }

        // Move cursor to end of text after transformation
        editTextPassword.setSelection(editTextPassword.getText().length());
    }

    /**
     * Toggles password visibility between masked and plain text for the confirm password field.
     * Updates the eye icon accordingly.
     */
    private void toggleConfirmPasswordVisibility() {
        isConfirmPasswordVisible = !isConfirmPasswordVisible;

        if (isConfirmPasswordVisible) {
            // Show password in plain text
            confirmPasswordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            confirmPasswordLayout.setEndIconDrawable(R.drawable.baseline_visibility_off_24);
        } else {
            // Hide password with dots
            confirmPasswordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            confirmPasswordLayout.setEndIconDrawable(R.drawable.baseline_visibility_24);
        }

        // Move cursor to end of text after transformation
        confirmPasswordEditText.setSelection(confirmPasswordEditText.getText().length());
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
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}