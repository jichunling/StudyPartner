package com.example.studybuddy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.studybuddy.R;
import com.example.studybuddy.data.database.DatabaseHelper;

public class EditPreferredTime extends AppCompatActivity {

    private CheckBox checkWeekdayMorning;
    private CheckBox checkWeekdayAfternoon;
    private CheckBox checkWeekdayEvening;
    private CheckBox checkWeekendMorning;
    private CheckBox checkWeekendAfternoon;
    private CheckBox checkWeekendEvening;
    private DatabaseHelper databaseHelper;
    String userEmail = "";
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_preferred_time);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        checkWeekdayMorning = findViewById(R.id.checkWeekdayMorning);
        checkWeekdayAfternoon = findViewById(R.id.checkWeekdayAfternoon);
        checkWeekdayEvening = findViewById(R.id.checkWeekdayEvening);
        checkWeekendMorning = findViewById(R.id.checkWeekendMorning);
        checkWeekendAfternoon = findViewById(R.id.checkWeekendAfternoon);
        checkWeekendEvening = findViewById(R.id.checkWeekendEvening);

        databaseHelper = new DatabaseHelper(this);

        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> {
            finish();
        });

        Intent intent = getIntent();
        userEmail = intent.getStringExtra("userEmail");

        String timePreference = databaseHelper.getUserStudyTimeString(userEmail);

        CheckBox checkBoxes[] = {checkWeekdayMorning, checkWeekdayAfternoon, checkWeekdayEvening, checkWeekendMorning, checkWeekendAfternoon, checkWeekendEvening};

        for (CheckBox checkBox : checkBoxes) {
            Log.println(Log.WARN, "time preference and checkbox text ", timePreference);
            Log.println(Log.WARN, "time preference and checkbox text checkkkbboooxxx ", checkBox.getText().toString());
            checkBox.setChecked(timePreference.contains(checkBox.getText().toString()));
        }

        findViewById(R.id.buttonSaveProfile).setOnClickListener(v -> savePreferences());
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

            DatabaseHelper databaseHelper = new DatabaseHelper(this);
            boolean isUpdated = databaseHelper.updateUserStudyTime(userEmail, selectedTimes);

            if (isUpdated) {
                Intent startMain = new Intent(this, MainActivity.class);
                startActivity(startMain);
                finish();
            } else {
                Toast.makeText(this, "Failed to update preferences.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
        }

    }
}