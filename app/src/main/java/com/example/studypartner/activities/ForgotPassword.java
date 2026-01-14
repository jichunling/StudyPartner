package com.example.studypartner.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * ForgotPassword Activity
 *
 * Handles password reset functionality for the studyPartner application.
 * Sends password reset tokens to users via email using Gmail SMTP.
 * Temporarily updates the user's password to the reset token which can be used to login.
 *
 * Features:
 * - Email validation before sending reset link
 * - Generates unique time-based reset tokens
 * - Sends reset instructions via email asynchronously
 * - Security-conscious messaging (doesn't reveal if email exists)
 * - Navigation back to login screen
 *
 * Security Notes:
 * - Email credentials are hardcoded (should be moved to secure configuration)
 * - AsyncTask is deprecated but functional (consider migrating to coroutines)
 * - Reset tokens have no expiration (should implement time-based expiry)
 *
 */
public class ForgotPassword extends AppCompatActivity {

    private static final String TAG = "ForgotPassword";

    // Email Configuration
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String EMAIL_USERNAME = "studybuddy1701@gmail.com";
    private static final String EMAIL_PASSWORD = "ozqd elrr nboa ppgr";

    // UI Components
    private TextInputEditText emailInput;
    private TextView emailConfirmationInfo;
    private Button sendEmailButton;
    private TextView backToLoginLink;

    // Business Logic
    private DatabaseHelper databaseHelper;

    /**
     * Initializes the forgot password activity and sets up UI components.
     *
     * @param savedInstanceState Saved state from previous instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        setupWindowInsets();

        initializeComponents();
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
        emailInput = findViewById(R.id.emailEditText);
        emailConfirmationInfo = findViewById(R.id.emailConfirmation);
        sendEmailButton = findViewById(R.id.sendResetPasswordEmail);
        backToLoginLink = findViewById(R.id.backToLogin);

        // Hide confirmation message initially
        emailConfirmationInfo.setVisibility(View.INVISIBLE);
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
        sendEmailButton.setOnClickListener(v -> handlePasswordReset());
        backToLoginLink.setOnClickListener(v -> navigateToLogin());
    }

    /**
     * Handles the password reset process including validation and email sending.
     */
    private void handlePasswordReset() {
        String email = getEmail();

        if (!validateInput(email)) {
            return;
        }

        // Generate reset token and update database
        String resetToken = generateResetToken();
        databaseHelper.updatePassword(email, resetToken);

        // Send reset email asynchronously
        new SendEmailTask().execute(email, resetToken);

        // Show confirmation message and disable input
        showConfirmationMessage();
        disableEmailInput();
    }

    /**
     * Gets the email input value.
     *
     * @return Email address as string
     */
    private String getEmail() {
        return emailInput.getText().toString().trim();
    }

    /**
     * Validates user input before attempting password reset.
     * Uses security-conscious messaging that doesn't reveal if email exists.
     *
     * @param email User's email address
     * @return true if input is valid, false otherwise
     */
    private boolean validateInput(String email) {
        // Check if email is empty
        if (email.isEmpty() || email.isBlank()) {
            showErrorMessage("Email can not be empty!");
            return false;
        }

        // Validate email format using ValidationUtils
        if (!ValidationUtils.isValidEmail(email)) {
            showErrorMessage("Please enter a valid email address");
            return false;
        }

        return true;
    }

    /**
     * Generates a unique reset token based on current timestamp and random value.
     * Note: In production, this should use UUID and include expiration time.
     *
     * @return Reset token as string
     */
    private String generateResetToken() {
        return String.valueOf(System.currentTimeMillis()) + Math.random() * 1000;
    }

    /**
     * Displays confirmation message after processing reset request.
     * Uses security-conscious messaging that doesn't reveal if email exists in system.
     */
    private void showConfirmationMessage() {
        String htmlText = "<font color='#000000'>We've processed your request. If the email address is linked to an account, you'll receive reset instructions soon.</font>";
        emailConfirmationInfo.setText(Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY));
        emailConfirmationInfo.setVisibility(View.VISIBLE);
    }

    /**
     * Displays an error message in red text.
     *
     * @param message Error message to display
     */
    private void showErrorMessage(String message) {
        String htmlText = "<font color='#CF6679'>" + message + "</font>";
        emailConfirmationInfo.setText(Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY));
        emailConfirmationInfo.setVisibility(View.VISIBLE);
    }

    /**
     * Disables email input field after reset request is submitted.
     */
    private void disableEmailInput() {
        emailInput.setEnabled(false);
    }

    /**
     * Navigates back to the login activity.
     */
    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * AsyncTask for sending password reset email in background thread.
     * Uses Gmail SMTP to send reset token to user's email address.
     *
     * Note: AsyncTask is deprecated since Android 11. Consider migrating to:
     * - Kotlin Coroutines with viewModelScope
     * - WorkManager for guaranteed background execution
     * - ExecutorService with handlers
     */
    private class SendEmailTask extends AsyncTask<String, Void, Boolean> {

        /**
         * Sends password reset email in background thread.
         *
         * @param params Array containing [0] email address and [1] reset token
         * @return true if email sent successfully, false otherwise
         */
        @Override
        protected Boolean doInBackground(String... params) {
            final String recipientEmail = params[0];
            final String resetToken = params[1];

            try {
                // Configure Gmail SMTP properties
                Properties props = configureSmtpProperties();

                // Create authenticated email session
                Session session = createEmailSession(props);

                // Compose and send email message
                Message message = composeResetEmail(session, recipientEmail, resetToken);
                Transport.send(message);

                Log.d(TAG, "Password reset email sent successfully to: " + recipientEmail);
                return true;

            } catch (MessagingException e) {
                Log.e(TAG, "Failed to send password reset email", e);
                e.printStackTrace();
                return false;
            }
        }

        /**
         * Configures SMTP properties for Gmail server.
         *
         * @return Properties object with SMTP configuration
         */
        private Properties configureSmtpProperties() {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            return props;
        }

        /**
         * Creates an authenticated email session using configured credentials.
         *
         * @param props SMTP properties
         * @return Authenticated Session object
         */
        private Session createEmailSession(Properties props) {
            return Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
                }
            });
        }

        /**
         * Composes the password reset email message.
         *
         * @param session Email session
         * @param recipientEmail Recipient's email address
         * @param resetToken Password reset token
         * @return Composed MimeMessage ready to send
         * @throws MessagingException if message composition fails
         */
        private Message composeResetEmail(Session session, String recipientEmail, String resetToken) throws MessagingException {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Password Reset Request");
            message.setText("Your password reset token is: " + resetToken +
                    "\n\nPlease use this token to login and then reset your password by visiting your profile.");
            return message;
        }

        /**
         * Updates UI based on email sending result.
         * Shows appropriate toast message on main thread.
         *
         * @param success true if email sent successfully, false otherwise
         */
        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(ForgotPassword.this,
                        "Password reset email sent successfully",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ForgotPassword.this,
                        "Failed to send reset email",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
