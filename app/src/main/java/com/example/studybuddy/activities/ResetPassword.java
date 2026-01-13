package com.example.studybuddy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.studybuddy.R;
import com.example.studybuddy.data.database.DatabaseHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ResetPassword extends AppCompatActivity {

    private TextInputEditText editTextEmail, editTextPassword, confirmPasswordEditText;
    private ImageButton backButton;
    private boolean isPasswordVisible = false; //Initializing to false as initially the password is not visible
    private boolean isConfirmPasswordVisible = false; //Initializing to false as initially the password is not visible
    private TextInputLayout passwordLayout, confirmPasswordLayout;
    private Button buttonSaveProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        backButton = findViewById(R.id.backButton);
        passwordLayout = findViewById(R.id.password_layout);
        confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout);
        buttonSaveProfile = findViewById(R.id.buttonSaveProfile);

        backButton.setOnClickListener(v -> {
            finish();
        });

        Intent intent = getIntent();
        String userEmail = intent.getStringExtra("userEmail");

        editTextEmail.setText(userEmail);

        buttonSaveProfile.setOnClickListener(v -> {
            String newPassword = editTextPassword.getText().toString().trim();
            String confirmNewPassword = confirmPasswordEditText.getText().toString().trim();

            if(newPassword.isEmpty() || newPassword.isBlank() || confirmNewPassword.isEmpty() || confirmNewPassword.isBlank()){
                Toast.makeText(this, "Passwords can't be empty", Toast.LENGTH_LONG).show();
                return;
            }else if(! newPassword.equals(confirmNewPassword)){
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_LONG).show();
                return;
            }else{
                DatabaseHelper db = new DatabaseHelper(this);
                boolean rowsUpdated = db.updatePassword(userEmail, newPassword);
                if(rowsUpdated){
                    Toast.makeText(this, "Password Updated Successfully", Toast.LENGTH_SHORT).show();
                    Intent startMain = new Intent(this, MainActivity.class);
                    startActivity(startMain);
                    finish();
                }else{
                    Toast.makeText(this, "Can't update password. Please try again", Toast.LENGTH_SHORT).show();
                }

            }
        });


        passwordLayout.setEndIconOnClickListener(new View.OnClickListener() {
            final String fieldName = getString(R.string.password);

            @Override
            public void onClick(View v) {
                togglePasswordVisibility(isPasswordVisible, passwordLayout, editTextPassword, fieldName);
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