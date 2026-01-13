package com.example.studybuddy.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studybuddy.R;
import com.example.studybuddy.adapter.InterestsAdapter;
import com.example.studybuddy.data.database.ConnectionsDB;
import com.example.studybuddy.data.database.DatabaseHelper;
import com.example.studybuddy.data.model.User;

import java.util.ArrayList;

public class ShowOtherUserProfileActivity extends AppCompatActivity {

    private String currentUserEmail;
    ConnectionsDB connectionsDB;
    DatabaseHelper db ;
    TextView userName, emailTextView, userOccupation, linkedIn, github, personalWeb;
    private String otherUserEmail;
    User user, userSocials; //This is the user who we want to send the connection request

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user_profile);
        connectionsDB = new ConnectionsDB(this);
        db = new DatabaseHelper(this);
        userName = findViewById(R.id.userName);
        emailTextView = findViewById(R.id.email_textView);
        userOccupation = findViewById(R.id.occupationText);
        linkedIn = findViewById(R.id.linkedin);
        github = findViewById(R.id.github);
        personalWeb = findViewById(R.id.personal);




        Intent intent = getIntent();
        otherUserEmail = intent.getStringExtra("email");

        Button connectBtn = findViewById(R.id.connect);
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendConnectionRequest(otherUserEmail);
            } //fixed argument
        });

        user = db.getUserInfoByEmail(intent.getStringExtra("email"));
        userSocials = db.getUserDetailsForMyProfilePage(intent.getStringExtra("email"));
        userName.setText(user.getFirstName() +" "+user.getLastName());
        emailTextView.setText(user.getEmail());
        userOccupation.setText(user.getOccupation());

        if(userSocials.getLinkedIn().isEmpty() && userSocials.getGithub().isEmpty() && userSocials.getPersonal().isEmpty()){
            androidx.constraintlayout.widget.ConstraintLayout layout =  findViewById(R.id.socialAccountsContainer);
            layout.setVisibility(View.INVISIBLE);
        }

        String linkedinLink = userSocials.getLinkedIn();
        String githubLink = userSocials.getGithub();
        String personalLink = userSocials.getPersonal();

        LinearLayout linkedInContainer = findViewById(R.id.linkedInContainer);
        LinearLayout githubContainer = findViewById(R.id.githubContainer);
        LinearLayout personalContainer = findViewById(R.id.personalContainer);

        linkedIn.setText(linkedinLink);
        github.setText(githubLink);
        personalWeb.setText(personalLink);

        linkedIn.setOnClickListener(v -> {
            String linkedInUrl = linkedIn.getText().toString();
        });

        github.setOnClickListener(v -> {
            String githubUrl = github.getText().toString();
            openLink(githubUrl);
        });

        personalWeb.setOnClickListener(v -> {
            String personalUrl = personalWeb.getText().toString();
            openLink(personalUrl);
        });





        if(linkedinLink.isEmpty() || linkedinLink.isBlank()){
            linkedIn.setVisibility(View.INVISIBLE);
            linkedInContainer.setVisibility(View.INVISIBLE);
        }

        if(githubLink.isEmpty() || githubLink.isBlank()){
            github.setVisibility(View.INVISIBLE);
            githubContainer.setVisibility(View.INVISIBLE);
        }

        if(personalLink.isEmpty() || personalLink.isBlank()){
            personalWeb.setVisibility(View.INVISIBLE);
            personalContainer.setVisibility(View.INVISIBLE);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationIcon(R.drawable.baseline_keyboard_backspace_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        //Setting the occupation


        RecyclerView recyclerView = findViewById(R.id.interestsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        InterestsAdapter adapter = new InterestsAdapter(user.getTopicInterested());
        recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void openLink(String link){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        startActivity(browserIntent);
    }





    private void sendConnectionRequest(String receiverEmail) {
        // Retrieve userEmail from Login Activity
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String myUserEmail = sharedPreferences.getString("userEmail", null);
        if (myUserEmail != null) {
            Log.d("ShowOtherProfileActivity", "userEmail: " + myUserEmail);
        } else {
            Toast.makeText(this, "Your Email not found!", Toast.LENGTH_SHORT).show();
        }
        boolean success = connectionsDB.insertConnectionRequest(myUserEmail, receiverEmail);
        if (success) {
            Toast.makeText(this, "Request Sent", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to Send Request", Toast.LENGTH_SHORT).show();
        }
    }
}