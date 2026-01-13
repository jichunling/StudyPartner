package com.example.studybuddy.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.studybuddy.R;
import com.example.studybuddy.data.database.DatabaseHelper;

public class InputSocials extends AppCompatActivity {

    EditText editLinkedIn, editGithub, editPersonal;
    Button buttonSaveProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_input_socials);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editLinkedIn = findViewById(R.id.editLinkedIn);
        editGithub = findViewById(R.id.editGithub);
        editPersonal = findViewById(R.id.editPersonal);
        buttonSaveProfile = findViewById(R.id.buttonSaveProfile);

        buttonSaveProfile.setOnClickListener(v -> {
            String linkedIn = editLinkedIn.getText().toString().trim();
            String github = editGithub.getText().toString().trim();
            String personal = editPersonal.getText().toString().trim();

            if((linkedIn.isEmpty() || linkedIn.isBlank()) && (github.isEmpty() || github.isBlank()) && (personal.isEmpty() || personal.isBlank())){
                callNewActivity();
            }else{
                DatabaseHelper databaseHelper = new DatabaseHelper(this);
                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                String userEmail = sharedPreferences.getString("userEmail", null);

                if (userEmail != null) {
                    boolean isUpdated = databaseHelper.saveSocials(userEmail, linkedIn, github, personal);

                    if (isUpdated) {
                        Toast.makeText(this, "Socials updated successfully!", Toast.LENGTH_SHORT).show();
                        databaseHelper.set_setUp(userEmail); //set setUp = 1
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to update preferences.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
                }


            }
        });


    }

    public void callNewActivity(){
        startActivity(new Intent(this, MainActivity.class));
    }
}