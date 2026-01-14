package com.example.studypartner.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studypartner.R;
import com.example.studypartner.adapter.InterestsAdapter;
import com.example.studypartner.data.database.ConnectionsDB;
import com.example.studypartner.data.database.DatabaseHelper;
import com.example.studypartner.data.model.User;

/**
 * ShowOtherUserProfileActivity
 *
 * Displays detailed profile information for another user (potential study partner).
 * Accessed when viewing matched users or browsing potential connections.
 * Allows the current user to send connection requests to the displayed user.
 *
 * Features:
 * - View other user's profile information (name, email, occupation)
 * - View study interests via RecyclerView
 * - View social media links (LinkedIn, GitHub, Personal Website)
 * - Open social media links in browser
 * - Send connection request to the user
 * - Hide social media section if no links provided
 * - Back navigation via toolbar
 */
public class ShowOtherUserProfileActivity extends AppCompatActivity {

    private static final String TAG = "ShowOtherUserProfile";

    // SharedPreferences keys
    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_USER_EMAIL = "userEmail";

    // Intent extra keys
    private static final String EXTRA_EMAIL = "email";

    // UI Components
    private TextView userName;
    private TextView emailTextView;
    private TextView userOccupation;
    private TextView linkedIn;
    private TextView github;
    private TextView personalWeb;
    private Button connectBtn;
    private Toolbar toolbar;
    private RecyclerView interestsRecyclerView;
    private ConstraintLayout socialAccountsContainer;
    private LinearLayout linkedInContainer;
    private LinearLayout githubContainer;
    private LinearLayout personalContainer;

    // Business Logic
    private ConnectionsDB connectionsDB;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;

    // Data
    private String otherUserEmail;
    private String currentUserEmail;
    private User user;
    private User userSocials;

    /**
     * Initializes the show other user profile activity and sets up UI components.
     *
     * @param savedInstanceState Saved state from previous instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user_profile);

        initializeComponents();
        loadUserData();
        populateUserProfile();
        setupSocialMediaLinks();
        setupInterestsRecyclerView();
        setupToolbar();
        setupClickListeners();
    }

    /**
     * Initializes all components including views and database helpers.
     */
    private void initializeComponents() {
        initializeViews();
        initializeDatabase();
        initializeSharedPreferences();
    }

    /**
     * Initializes all view references.
     */
    private void initializeViews() {
        userName = findViewById(R.id.userName);
        emailTextView = findViewById(R.id.email_textView);
        userOccupation = findViewById(R.id.occupationText);
        linkedIn = findViewById(R.id.linkedin);
        github = findViewById(R.id.github);
        personalWeb = findViewById(R.id.personal);
        connectBtn = findViewById(R.id.connect);
        toolbar = findViewById(R.id.toolbar);
        interestsRecyclerView = findViewById(R.id.interestsRecyclerView);
        socialAccountsContainer = findViewById(R.id.socialAccountsContainer);
        linkedInContainer = findViewById(R.id.linkedInContainer);
        githubContainer = findViewById(R.id.githubContainer);
        personalContainer = findViewById(R.id.personalContainer);
    }

    /**
     * Initializes database helpers.
     */
    private void initializeDatabase() {
        connectionsDB = new ConnectionsDB(this);
        databaseHelper = new DatabaseHelper(this);
    }

    /**
     * Initializes SharedPreferences for accessing current user data.
     */
    private void initializeSharedPreferences() {
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        currentUserEmail = sharedPreferences.getString(KEY_USER_EMAIL, null);
    }

    /**
     * Loads user data from intent.
     */
    private void loadUserData() {
        Intent intent = getIntent();
        otherUserEmail = intent.getStringExtra(EXTRA_EMAIL);

        user = databaseHelper.getUserInfoByEmail(otherUserEmail);
        userSocials = databaseHelper.getUserDetailsForMyProfilePage(otherUserEmail);

        Log.d(TAG, "Loaded profile for user: " + otherUserEmail);
    }

    /**
     * Populates the UI with user profile information.
     */
    private void populateUserProfile() {
        String fullName = user.getFirstName() + " " + user.getLastName();
        userName.setText(fullName);
        emailTextView.setText(user.getEmail());
        userOccupation.setText(user.getOccupation());
    }

    /**
     * Sets up social media links and visibility.
     */
    private void setupSocialMediaLinks() {
        String linkedInLink = userSocials.getLinkedIn();
        String githubLink = userSocials.getGithub();
        String personalLink = userSocials.getPersonal();

        if (areAllSocialLinksEmpty(linkedInLink, githubLink, personalLink)) {
            socialAccountsContainer.setVisibility(View.INVISIBLE);
            return;
        }

        setupLinkedInLink(linkedInLink);
        setupGithubLink(githubLink);
        setupPersonalLink(personalLink);
    }

    /**
     * Checks if all social media links are empty.
     *
     * @param linkedIn LinkedIn URL
     * @param github GitHub URL
     * @param personal Personal website URL
     * @return true if all links are empty, false otherwise
     */
    private boolean areAllSocialLinksEmpty(String linkedIn, String github, String personal) {
        return linkedIn.isEmpty() && github.isEmpty() && personal.isEmpty();
    }

    /**
     * Sets up LinkedIn link and visibility.
     *
     * @param linkedInLink LinkedIn URL
     */
    private void setupLinkedInLink(String linkedInLink) {
        if (linkedInLink.isEmpty()) {
            linkedIn.setVisibility(View.INVISIBLE);
            linkedInContainer.setVisibility(View.INVISIBLE);
        } else {
            linkedIn.setText(linkedInLink);
            linkedIn.setOnClickListener(v -> openLink(linkedInLink));
        }
    }

    /**
     * Sets up GitHub link and visibility.
     *
     * @param githubLink GitHub URL
     */
    private void setupGithubLink(String githubLink) {
        if (githubLink.isEmpty()) {
            github.setVisibility(View.INVISIBLE);
            githubContainer.setVisibility(View.INVISIBLE);
        } else {
            github.setText(githubLink);
            github.setOnClickListener(v -> openLink(githubLink));
        }
    }

    /**
     * Sets up personal website link and visibility.
     *
     * @param personalLink Personal website URL
     */
    private void setupPersonalLink(String personalLink) {
        if (personalLink.isEmpty()) {
            personalWeb.setVisibility(View.INVISIBLE);
            personalContainer.setVisibility(View.INVISIBLE);
        } else {
            personalWeb.setText(personalLink);
            personalWeb.setOnClickListener(v -> openLink(personalLink));
        }
    }

    /**
     * Sets up the interests RecyclerView with user's study topics.
     */
    private void setupInterestsRecyclerView() {
        interestsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        InterestsAdapter adapter = new InterestsAdapter(user.getTopicInterested());
        interestsRecyclerView.setAdapter(adapter);
    }

    /**
     * Sets up the toolbar with back navigation.
     */
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        toolbar.setTitle("");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationIcon(R.drawable.baseline_keyboard_backspace_24);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    /**
     * Sets up click listeners for interactive UI components.
     */
    private void setupClickListeners() {
        connectBtn.setOnClickListener(v -> handleSendConnectionRequest());
    }

    /**
     * Handles the send connection request button click.
     */
    private void handleSendConnectionRequest() {
        if (currentUserEmail == null) {
            showErrorMessage("Your Email not found!");
            Log.e(TAG, "Current user email is null");
            return;
        }

        sendConnectionRequest(otherUserEmail);
    }

    /**
     * Sends a connection request to the specified receiver.
     *
     * @param receiverEmail Email of the user to send connection request to
     */
    private void sendConnectionRequest(String receiverEmail) {
        Log.d(TAG, "Sending connection request from " + currentUserEmail + " to " + receiverEmail);

        boolean success = connectionsDB.insertConnectionRequest(currentUserEmail, receiverEmail);

        if (success) {
            showSuccessMessage("Request Sent");
            Log.d(TAG, "Connection request sent successfully");
        } else {
            showErrorMessage("Failed to Send Request");
            Log.e(TAG, "Failed to send connection request");
        }
    }

    /**
     * Opens a URL in the device's default browser.
     *
     * @param link URL to open
     */
    private void openLink(String link) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        startActivity(browserIntent);
    }

    /**
     * Handles toolbar back navigation.
     *
     * @return true if navigation handled
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Displays a success message to the user.
     *
     * @param message Success message to display
     */
    private void showSuccessMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Displays an error message to the user.
     *
     * @param message Error message to display
     */
    private void showErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}