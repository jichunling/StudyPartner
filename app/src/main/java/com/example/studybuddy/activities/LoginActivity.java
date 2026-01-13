package com.example.studybuddy.activities;

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

import com.example.studybuddy.R;
import com.example.studybuddy.data.database.DatabaseHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {
    //private EditText editTextLoginPassword;
    private TextInputEditText editTextLoginEmail, editTextLoginPassword;
    private boolean passwordVisible = false; //initially the password is not visible
    private TextInputLayout passwordLayout;
    private TextView signUpLink;
    private TextView forgetPasswordLink;
    private DatabaseHelper dbHelper;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);
        editTextLoginEmail = findViewById(R.id.emailEditText);
        editTextLoginPassword = findViewById(R.id.passwordEditText);
        forgetPasswordLink = findViewById(R.id.forgotPasswordLink);

        forgetPasswordLink.setOnClickListener(v -> {
            Intent intent = new Intent(this, ForgotPassword.class);
            startActivity(intent);
            finish();
        });

        Button buttonLogin = findViewById(R.id.buttonLogin);

        //Click on this link will enable user to signup
        signUpLink = findViewById(R.id.signUpLink);
        String htmlText = "Don't have an account?  <font color='#3344DD'>Sign up here!</font>";
        signUpLink.setText(Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY));

        signUpLink = findViewById(R.id.signUpLink);
        signUpLink.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignupActivity.class);
            startActivity(intent);
            finish();
        });

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("userEmail", null);

        passwordLayout = findViewById(R.id.confirmPasswordLayout);

        passwordLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });



        if (userEmail != null) {
            Log.d("LoginActivity", "User is logged in with email: " + userEmail);
            navigateToNextActivity(userEmail);
        } else {
            Log.d("LoginActivity", "No logged-in user found.");
        }

        buttonLogin.setOnClickListener(view -> {
            email = editTextLoginEmail.getText().toString().toLowerCase();
            String password = editTextLoginPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isValid = dbHelper.validateUser(email, password);
            if (isValid) {

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("userEmail", email);
                editor.apply();


                String userID = dbHelper.getUserIDByEmail(email);
                editor.putString("userID", userID);
                editor.apply();

                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                boolean isSetUp = dbHelper.isSetUp(email);
                Intent intent;
                if (!isSetUp) {
                    intent = new Intent(LoginActivity.this, UserProfileActivity.class);
                } else {
//                    intent = new Intent(LoginActivity.this, MatchUserActivity.class);
                    intent = new Intent(LoginActivity.this, MainActivity.class);
                }
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void navigateToNextActivity(String email) {
        boolean isSetUp = dbHelper.isSetUp(email);
        Intent intent;
        if (!isSetUp) {
            intent = new Intent(LoginActivity.this, UserProfileActivity.class);
//            intent = new Intent(LoginActivity.this, MainActivity.class);

        } else {
//            intent = new Intent(LoginActivity.this, MatchUserActivity.class);
            intent = new Intent(LoginActivity.this, MainActivity.class);
        }
        startActivity(intent);
    }

    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;
        if (passwordVisible) {
            // Show password
            //The function HideReturnsTransformationMethod - shows password in a readable plain format
            editTextLoginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            passwordLayout.setEndIconDrawable(R.drawable.baseline_visibility_off_24);
        } else {
            // Hide password
            editTextLoginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            passwordLayout.setEndIconDrawable(R.drawable.baseline_visibility_24);
        }
        // Move cursor to the end of the text
        editTextLoginPassword.setSelection(editTextLoginPassword.getText().length());
    }
}

