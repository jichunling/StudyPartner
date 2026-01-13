package com.example.studybuddy.activities;

import android.content.Intent;
import android.os.Bundle;
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

import com.example.studybuddy.R;
import com.example.studybuddy.data.database.DatabaseHelper;

public class EditPersonalInformation extends AppCompatActivity {
    private EditText editFirstName, editLastName, editAge, editOccupation;
    private RadioGroup radioGroupGender;
    private Button btnSaveProfile;
    String userEmail="";
    private ImageButton backButton;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_personal_information);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        String firstName = intent.getStringExtra("userFirstName");
        String lastName = intent.getStringExtra("userLastName");
        String age = intent.getStringExtra("userAge");
        String occupation = intent.getStringExtra("userOccupation");
        String gender = intent.getStringExtra("userGender");
        userEmail = intent.getStringExtra("userEmail");

        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        editAge = findViewById(R.id.editAge);
        editOccupation = findViewById(R.id.editOccupationText);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        backButton = findViewById(R.id.backButton);
        btnSaveProfile = findViewById(R.id.buttonSaveProfile);

        databaseHelper = new DatabaseHelper(this);

        backButton.setOnClickListener(v -> {
            finish();
        });

        if(gender == null || gender.isEmpty() || gender.isBlank()){

        } else if(gender.equals("Female")){
            radioGroupGender.check(R.id.radioFemale);
        } else if(gender.equals("Male") ){
            radioGroupGender.check(R.id.radioMale);
        }else{
            radioGroupGender.check(R.id.radioOther);
        }

        editFirstName.setText(firstName);
        editLastName.setText(lastName);
        editOccupation.setText(occupation);
        editAge.setText(age);

        btnSaveProfile.setOnClickListener(v -> {
            String first_name = editFirstName.getText().toString().trim();
            String last_name = editLastName.getText().toString().trim();
            String ageString = editAge.getText().toString().trim();
            String occupationText = editOccupation.getText().toString().trim();
            int userAge = ageString.isEmpty() ? 0 : Integer.parseInt(ageString);

            int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();
            RadioButton selectedGenderButton = findViewById(selectedGenderId);
            String userGender = (selectedGenderButton != null) ? selectedGenderButton.getText().toString() : "";

            if (first_name.isEmpty() || first_name.isBlank() || last_name.isEmpty() || last_name.isBlank()) {
                Toast.makeText(this, "First name and Last name are required.", Toast.LENGTH_SHORT).show();
                return;
            }

            saveProfileToDatabase(first_name, last_name, userAge, userGender, occupationText);
        });
    }

    private void saveProfileToDatabase(String firstName, String lastName, int age, String gender, String occupation) {
            boolean isUpdated = databaseHelper.updateUserProfile(userEmail, firstName, lastName, age, gender, occupation);

            if (isUpdated) {
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                Intent startMain = new Intent(this, MainActivity.class);
                startActivity(startMain);
                finish();
            } else {
                Toast.makeText(this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
            }

    }
}