package com.example.studybuddy.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studybuddy.R;
import com.example.studybuddy.data.database.DatabaseHelper;

public class UserProfileActivity extends AppCompatActivity {

    private EditText editFirstName, editLastName, editAge, occupation;
    private RadioGroup radioGroupGender;
    private Button btnSaveProfile;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        editAge = findViewById(R.id.editAge);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        btnSaveProfile = findViewById(R.id.buttonSaveProfile);
        occupation = findViewById(R.id.editOccupation);

        databaseHelper = new DatabaseHelper(this);

        btnSaveProfile.setOnClickListener(v -> {
            String firstName = editFirstName.getText().toString().trim();
            String lastName = editLastName.getText().toString().trim();
            String ageString = editAge.getText().toString().trim();
            String occupationText = occupation.getText().toString().trim();
            int age = ageString.isEmpty() ? 0 : Integer.parseInt(ageString);

            int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();
            RadioButton selectedGenderButton = findViewById(selectedGenderId);
            String gender = (selectedGenderButton != null) ? selectedGenderButton.getText().toString() : "";


            if (firstName.isEmpty() || lastName.isEmpty()) {
                Toast.makeText(UserProfileActivity.this, "First name and Last name are required.", Toast.LENGTH_SHORT).show();
                return;
            }

            saveProfileToDatabase(firstName, lastName, age, gender, occupationText);
        });
    }

    private void saveProfileToDatabase(String firstName, String lastName, int age, String gender, String occupation) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("userEmail", null);

        if (userEmail != null) {
            boolean isUpdated = databaseHelper.updateUserProfile(userEmail, firstName, lastName, age, gender, occupation);

            if (isUpdated) {
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(UserProfileActivity.this, TopicPreferenceActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
        }
    }
}