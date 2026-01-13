package com.example.studybuddy.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
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

public class EditDifficultyLevel extends AppCompatActivity {

    private RadioGroup radioGroupDifficulty;
    private Button btnSaveDifficulty;
    String userEmail = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_difficulty_level);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        radioGroupDifficulty = findViewById(R.id.radioGroupDifficulty);
        findViewById(R.id.buttonSaveProfile).setOnClickListener(v -> saveDifficulty());

        Intent intent = getIntent();
        userEmail = intent.getStringExtra("userEmail");
        String difficulty = intent.getStringExtra("userDifficultyLevel");

        if(difficulty.isEmpty() || difficulty.isBlank()){

        }else if(difficulty.equals("Easy")){
            radioGroupDifficulty.check(R.id.radioEasy);
        }else if(difficulty.equals("Medium")) {
            radioGroupDifficulty.check(R.id.radioMedium);
        }else {
            radioGroupDifficulty.check(R.id.radioHard);
        }

        ImageButton backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> {
            finish();
        });

    }

    private void saveDifficulty() {
        int selectedId = radioGroupDifficulty.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedId);
            String selectedDifficulty = selectedRadioButton.getText().toString();

            if (userEmail != null) {
                DatabaseHelper databaseHelper = new DatabaseHelper(this);
                boolean isUpdated = databaseHelper.updateUserStudyDifficultyLevel(userEmail, selectedDifficulty);

                if (isUpdated) {
                    Toast.makeText(this, "Study difficulty preferences updated successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));
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