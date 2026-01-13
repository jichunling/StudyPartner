package com.example.studybuddy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studybuddy.R;
import com.example.studybuddy.data.database.DatabaseHelper;
import com.example.studybuddy.utils.ValidationUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SignupActivity extends AppCompatActivity {
    private TextInputEditText editTextEmail;
    private DatabaseHelper dbHelper;
    private boolean isPasswordVisible = false; //Initializing to false as initially the password is not visible
    private boolean isConfirmPasswordVisible = false; //Initializing to false as initially the password is not visible
    private Button buttonSignup;
    private TextInputLayout passwordLayout;
    private TextInputEditText passwordEditText;

    private TextInputLayout confirmPasswordLayout;
    private TextInputEditText confirmPasswordEditText, userFullName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        dbHelper = new DatabaseHelper(this);

        editTextEmail = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        buttonSignup = findViewById(R.id.signup_button);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        userFullName = findViewById(R.id.userNameEditText);

        TextView loginLink = findViewById(R.id.loginLink);
        String htmlText = "Already a member?  <font color='#3344DD'>Login here!</font>";
        loginLink.setText(Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY));


        buttonSignup.setOnClickListener(view -> {
            String email = editTextEmail.getText().toString().trim().toLowerCase();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();
            String name = userFullName.getText().toString().trim();

            if(!ValidationUtils.isUserNameEmpty(name)){
                Toast.makeText(SignupActivity.this, "Name can't be empty", Toast.LENGTH_SHORT).show();
            }

            if (!ValidationUtils.isValidEmail(email)){
                Toast.makeText(SignupActivity.this, "Invalid email format", Toast.LENGTH_SHORT).show();
               return;
            }


            if (!ValidationUtils.isValidPassword(password)) {
                Toast.makeText(SignupActivity.this, "Password must be at least 6 characters, include uppercase, lowercase, and a number", Toast.LENGTH_LONG).show();
                return;
            }

            if(!password.equals(confirmPassword)){
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isInserted = dbHelper.insertUser(email, password);
            if (isInserted) {
                Toast.makeText(SignupActivity.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(SignupActivity.this, "The email already exists. Please login!", Toast.LENGTH_SHORT).show();
            }
        });

        loginLink.setOnClickListener(view -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        //The below code helps to toggle the password field
        passwordLayout = findViewById(R.id.password_layout);
        passwordEditText = findViewById(R.id.editTextPassword);

        confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);

        passwordLayout.setEndIconOnClickListener(new View.OnClickListener() {
            final String fieldName = getString(R.string.password);

            @Override
            public void onClick(View v) {
                togglePasswordVisibility(isPasswordVisible, passwordLayout, passwordEditText, fieldName);
            }
        });

        confirmPasswordLayout.setEndIconOnClickListener(new View.OnClickListener() {
            final String fieldName = getString(R.string.confirm_password);

            @Override
            public void onClick(View v) {
                togglePasswordVisibility(isConfirmPasswordVisible, confirmPasswordLayout, confirmPasswordEditText, fieldName);
            }
        });





    }

    public void togglePasswordVisibility(Boolean isPasswordVisible, TextInputLayout layout, TextInputEditText editText, String fieldName) {
        isPasswordVisible = !isPasswordVisible;

        if (fieldName.equals(getString(R.string.password))) {
            this.isPasswordVisible = !this.isPasswordVisible;
        } else {
            this.isConfirmPasswordVisible = !this.isConfirmPasswordVisible;
        }

        if (isPasswordVisible) { //This means that password is currently visible and we need to hide it
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            layout.setEndIconDrawable(R.drawable.baseline_visibility_off_24);
        } else { //Password is hidden and we need to show it
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            layout.setEndIconDrawable(R.drawable.baseline_visibility_24);
        }
        editText.setSelection(editText.getText().length());
    }

}
