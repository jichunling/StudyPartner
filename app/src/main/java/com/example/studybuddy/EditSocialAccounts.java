package com.example.studybuddy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.studybuddy.activities.MainActivity;
import com.example.studybuddy.data.database.DatabaseHelper;

public class EditSocialAccounts extends AppCompatActivity {

    private ImageButton backButton;
    private TextView linkedIn, github, personal;
    private Button buttonSaveProfile;
    private String userEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_social_accounts);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backButton = findViewById(R.id.backButton);
        linkedIn = findViewById(R.id.editLinkedIn);
        github = findViewById(R.id.editGithub);
        personal = findViewById(R.id.editPersonal);
        buttonSaveProfile = findViewById(R.id.buttonSaveProfile);

        Intent intent = getIntent();
        String linkedInLink = intent.getStringExtra("linkedIn");
        String githubLink = intent.getStringExtra("github");
        String personalLink = intent.getStringExtra("personal");
        userEmail = intent.getStringExtra("userEmail");

        linkedIn.setText(linkedInLink);
        github.setText(githubLink);
        personal.setText(personalLink);

        backButton.setOnClickListener(v -> {
            finish();
        });

        buttonSaveProfile.setOnClickListener(v -> {
            DatabaseHelper databaseHelper = new DatabaseHelper(this);

            String linkedInText = linkedIn.getText().toString().trim();
            String githubText = github.getText().toString().trim();
            String personalText = personal.getText().toString().trim();

            if (userEmail != null) {
                boolean isUpdated = databaseHelper.saveSocials(userEmail, linkedInText, githubText, personalText);

                if (isUpdated) {
                    Toast.makeText(this, "Socials updated successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Failed to update preferences.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
            }

        });


    }
}