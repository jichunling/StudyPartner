package com.example.studybuddy.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studybuddy.R;
import com.example.studybuddy.data.database.DatabaseHelper;

public class DifficultyPreferenceActivity extends AppCompatActivity {

    private RadioGroup radioGroupDifficulty;
    private Button btnSaveDifficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty_preference);
        radioGroupDifficulty = findViewById(R.id.radioGroupDifficulty);

        findViewById(R.id.btnSaveDifficulty).setOnClickListener(v -> saveDifficulty());
    }

    private void saveDifficulty() {
        int selectedId = radioGroupDifficulty.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedId);
            String selectedDifficulty = selectedRadioButton.getText().toString();

            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            String userEmail = sharedPreferences.getString("userEmail", null);

            if (userEmail != null) {
                DatabaseHelper databaseHelper = new DatabaseHelper(this);
                boolean isUpdated = databaseHelper.updateUserStudyDifficultyLevel(userEmail, selectedDifficulty);

                if (isUpdated) {
                    Toast.makeText(this, "Study difficulty preferences updated successfully!", Toast.LENGTH_SHORT).show();
                   // databaseHelper.set_setUp(userEmail); //set setUp = 1
//                    startActivity(new Intent(this, MatchUserActivity.class));
                    startActivity(new Intent(this, InputSocials.class));

                    finish();
                } else {
                    Toast.makeText(this, "Failed to update preferences.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No difficulty level selected.", Toast.LENGTH_SHORT).show();
        }
    }
}
