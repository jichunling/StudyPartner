package com.example.studybuddy.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.studybuddy.R;
import com.example.studybuddy.data.database.DatabaseHelper;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ForgotPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView emailConfirmationInfo;
        Button sendEmail;
        TextInputEditText email;
        TextView backToLogin;

        emailConfirmationInfo = findViewById(R.id.emailConfirmation);
        emailConfirmationInfo.setVisibility(View.INVISIBLE);
        sendEmail = findViewById(R.id.sendResetPasswordEmail);
        email = findViewById(R.id.emailEditText);
        backToLogin = findViewById(R.id.backToLogin);


        backToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPassword.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        sendEmail.setOnClickListener(v -> {
            String emailInfo = email.getText().toString();
            String htmlText;

            if (emailInfo.isEmpty() || emailInfo.isBlank()) {
                htmlText = "<font color='#CF6679'>Email can not be empty!</font>";

            } else {
                //TODO: have email validation - check if the email format is correct.
                htmlText = "<font color='#000000'>We've processed your request. If the email address is linked to an account, you'll receive reset instructions soon.";
            }

            emailConfirmationInfo.setText(Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY));
            emailConfirmationInfo.setVisibility(View.VISIBLE);
            email.setEnabled(false);


            String resetToken = generateResetToken();
            String emailText = email.getText().toString().trim();
            DatabaseHelper db = new DatabaseHelper(this);
            db.updatePassword(emailText, resetToken);
            new SendEmailTask().execute(emailText, resetToken);

        });
    }

    private String generateResetToken() {
        // Generate a unique, time-limited reset token
        return String.valueOf(System.currentTimeMillis()) +
                Math.random() * 1000;
    }

    private class SendEmailTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            final String email = params[0];
            final String resetToken = params[1];

            // Email configuration (replace with your actual email settings)
            final String username = "studybuddy1701@gmail.com";
            final String password = "ozqd elrr nboa ppgr";

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(username));
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(email));
                message.setSubject("Password Reset Request");
                message.setText("Your password reset token is: " + resetToken +
                        "\n\nPlease use this token to login and then reset your password by visiting your profile.");

                Transport.send(message);
                return true;
            } catch (MessagingException e) {
                e.printStackTrace();
                return false;
            }
        }

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
