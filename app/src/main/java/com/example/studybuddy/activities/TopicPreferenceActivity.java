package com.example.studybuddy.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studybuddy.R;
import com.example.studybuddy.data.database.DatabaseHelper;

public class TopicPreferenceActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private TextView textProgress;
    private Button buttonNext;
    private CheckBox checkComputerScience, checkBiology, checkChemistry, checkMathematics,
            checkEngineering, checkPhysics, checkEnglish, checkFrench, checkHistory, checkPhilosophy;
    private int currentQuestion = 1;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_preference);

        progressBar = findViewById(R.id.progressBar);
        textProgress = findViewById(R.id.textProgress);
        buttonNext = findViewById(R.id.buttonNext);

        checkComputerScience = findViewById(R.id.checkComputerScience);
        checkBiology = findViewById(R.id.checkBiology);
        checkChemistry = findViewById(R.id.checkChemistry);
        checkMathematics = findViewById(R.id.checkMathematics);
        checkEngineering = findViewById(R.id.checkEngineering);
        checkPhysics = findViewById(R.id.checkPhysics);
        checkEnglish = findViewById(R.id.checkEnglish);
        checkFrench = findViewById(R.id.checkFrench);
        checkHistory = findViewById(R.id.checkHistory);
        checkPhilosophy = findViewById(R.id.checkPhilosophy);

        databaseHelper = new DatabaseHelper(this);

        buttonNext.setOnClickListener(v -> {
            StringBuilder selectedTopics = new StringBuilder();

            if (checkComputerScience.isChecked()) selectedTopics.append("Computer Science, ");
            if (checkBiology.isChecked()) selectedTopics.append("Biology, ");
            if (checkChemistry.isChecked()) selectedTopics.append("Chemistry, ");
            if (checkMathematics.isChecked()) selectedTopics.append("Mathematics, ");
            if (checkEngineering.isChecked()) selectedTopics.append("Engineering, ");
            if (checkPhysics.isChecked()) selectedTopics.append("Physics, ");
            if (checkEnglish.isChecked()) selectedTopics.append("English, ");
            if (checkFrench.isChecked()) selectedTopics.append("French, ");
            if (checkHistory.isChecked()) selectedTopics.append("History, ");
            if (checkPhilosophy.isChecked()) selectedTopics.append("Philosophy, ");

            if (selectedTopics.length() > 0) {
                selectedTopics.setLength(selectedTopics.length() - 2);
            }

            if (selectedTopics.length() > 0) {
                Toast.makeText(TopicPreferenceActivity.this, "Selected Topics: " + selectedTopics.toString(), Toast.LENGTH_SHORT).show();
                saveTopicToDatabase(selectedTopics.toString());

                Intent intent = new Intent(TopicPreferenceActivity.this, StudyTimePreferenceActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(TopicPreferenceActivity.this, "No topics selected.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveTopicToDatabase(String topics) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("userEmail", null);

        if (userEmail != null) {
            boolean isSaved = databaseHelper.updateUserTopic(userEmail, topics);
            if (isSaved) {
                Toast.makeText(this, "Topics saved!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to save topics.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
        }
    }
}
