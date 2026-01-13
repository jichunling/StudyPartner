package com.example.studybuddy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.Toast;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studybuddy.R;
import com.example.studybuddy.data.database.DatabaseHelper;

public class StudyTimePreferenceActivity extends AppCompatActivity {

    private CheckBox checkWeekdayMorning;
    private CheckBox checkWeekdayAfternoon;
    private CheckBox checkWeekdayEvening;
    private CheckBox checkWeekendMorning;
    private CheckBox checkWeekendAfternoon;
    private CheckBox checkWeekendEvening;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_time_preference);

        checkWeekdayMorning = findViewById(R.id.checkWeekdayMorning);
        checkWeekdayAfternoon = findViewById(R.id.checkWeekdayAfternoon);
        checkWeekdayEvening = findViewById(R.id.checkWeekdayEvening);
        checkWeekendMorning = findViewById(R.id.checkWeekendMorning);
        checkWeekendAfternoon = findViewById(R.id.checkWeekendAfternoon);
        checkWeekendEvening = findViewById(R.id.checkWeekendEvening);

        findViewById(R.id.buttonNext2).setOnClickListener(v -> savePreferences());
    }

    private void savePreferences() {
        StringBuilder timePreferences = new StringBuilder();

        if (checkWeekdayMorning.isChecked()) timePreferences.append("Weekday Morning, ");
        if (checkWeekdayAfternoon.isChecked()) timePreferences.append("Weekday Afternoon, ");
        if (checkWeekdayEvening.isChecked()) timePreferences.append("Weekday Evening, ");
        if (checkWeekendMorning.isChecked()) timePreferences.append("Weekend Morning, ");
        if (checkWeekendAfternoon.isChecked()) timePreferences.append("Weekend Afternoon, ");
        if (checkWeekendEvening.isChecked()) timePreferences.append("Weekend Evening, ");

        if (timePreferences.length() > 0) {
            timePreferences.setLength(timePreferences.length() - 2);
        }

        String selectedTimes = timePreferences.toString();
        if (!selectedTimes.isEmpty()) {
            Toast.makeText(this, "Selected Time: " + selectedTimes, Toast.LENGTH_SHORT).show();

            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            String userEmail = sharedPreferences.getString("userEmail", null);

            if (userEmail != null) {
                DatabaseHelper databaseHelper = new DatabaseHelper(this);
                boolean isUpdated = databaseHelper.updateUserStudyTime(userEmail, selectedTimes);

                if (isUpdated) {
                    Toast.makeText(this, "Study time preferences updated successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, DifficultyPreferenceActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Failed to update preferences.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No study time selected.", Toast.LENGTH_SHORT).show();
        }
    }
}
