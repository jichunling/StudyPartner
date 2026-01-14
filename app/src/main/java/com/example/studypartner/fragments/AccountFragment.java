package com.example.studypartner.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.studypartner.activities.EditSocialAccounts;
import com.example.studypartner.R;
import com.example.studypartner.activities.EditDifficultyLevel;
import com.example.studypartner.activities.EditMyTopicPreferences;
import com.example.studypartner.activities.EditPersonalInformation;
import com.example.studypartner.activities.EditPreferredTime;
import com.example.studypartner.activities.LoginActivity;
import com.example.studypartner.activities.ResetPassword;
import com.example.studypartner.data.database.DatabaseHelper;
import com.example.studypartner.data.model.User;

/**
 * AccountFragment
 *
 * Displays the user's account profile information and settings.
 * Accessed via the bottom navigation bar in MainActivity.
 * Provides access to edit various profile sections and logout functionality.
 *
 * Features:
 * - View all profile information (personal info, topics, time preferences, difficulty, social links)
 * - View and toggle password visibility
 * - Navigate to edit screens for each profile section:
 *   - Personal Information (name, age, gender, occupation)
 *   - Topic Preferences
 *   - Time Preferences
 *   - Difficulty Level
 *   - Social Media Links
 *   - Login Password
 * - Logout functionality that clears session and returns to login
 */
public class AccountFragment extends Fragment {

    private static final String TAG = "AccountFragment";

    // SharedPreferences keys
    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_USER_EMAIL = "userEmail";

    // Intent extra keys
    private static final String EXTRA_USER_EMAIL = "userEmail";
    private static final String EXTRA_TOPICS = "topics";
    private static final String EXTRA_USER_DIFFICULTY_LEVEL = "userDifficultyLevel";
    private static final String EXTRA_USER_FIRST_NAME = "userFirstName";
    private static final String EXTRA_USER_LAST_NAME = "userLastName";
    private static final String EXTRA_USER_AGE = "userAge";
    private static final String EXTRA_USER_OCCUPATION = "userOccupation";
    private static final String EXTRA_USER_GENDER = "userGender";
    private static final String EXTRA_LINKEDIN = "linkedIn";
    private static final String EXTRA_GITHUB = "github";
    private static final String EXTRA_PERSONAL = "personal";

    // UI Components
    private TextView userEmailTextView;
    private TextView myTopics;
    private TextView myTime;
    private TextView myDifficultyLevel;
    private TextView myFirstName;
    private TextView myLastName;
    private TextView myAge;
    private TextView myGender;
    private TextView myOccupation;
    private TextView myEmail;
    private TextView myLinkedIn;
    private TextView myGithub;
    private TextView myPersonal;
    private EditText passwordEditText;
    private CheckBox showPasswordCheckBox;
    private Button logoutButton;
    private ImageButton editMyTopics;
    private ImageButton editPersonalInfo;
    private ImageButton editTimePreference;
    private ImageButton editDifficultyPreference;
    private ImageButton editLoginInfo;
    private ImageButton editSocial;

    // Business Logic
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;

    // Data
    private String userEmail;
    private User currentUser;

    /**
     * Creates and initializes the account fragment view.
     *
     * @param inflater LayoutInflater to inflate views
     * @param container Parent view container
     * @param savedInstanceState Saved state from previous instance
     * @return The fragment view
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_account, container, false);

        initializeComponents(view);
        loadUserData();
        populateProfileInformation();
        setupPasswordVisibilityToggle();
        setupClickListeners();

        return view;
    }

    /**
     * Initializes all components including views and database helper.
     *
     * @param view The fragment's root view
     */
    private void initializeComponents(View view) {
        initializeViews(view);
        initializeDatabase();
        initializeSharedPreferences();
    }

    /**
     * Initializes all view references.
     *
     * @param view The fragment's root view
     */
    private void initializeViews(View view) {
        userEmailTextView = view.findViewById(R.id.user_email);
        myTopics = view.findViewById(R.id.my_topics);
        myTime = view.findViewById(R.id.my_time);
        myDifficultyLevel = view.findViewById(R.id.my_difficulty_level);
        myFirstName = view.findViewById(R.id.first_name);
        myLastName = view.findViewById(R.id.last_name);
        myAge = view.findViewById(R.id.my_age);
        myGender = view.findViewById(R.id.my_gender);
        myOccupation = view.findViewById(R.id.my_occupation);
        myEmail = view.findViewById(R.id.my_email);
        myLinkedIn = view.findViewById(R.id.my_linkedIn);
        myGithub = view.findViewById(R.id.my_github);
        myPersonal = view.findViewById(R.id.my_personal);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        showPasswordCheckBox = view.findViewById(R.id.showPasswordCheckBox);
        logoutButton = view.findViewById(R.id.logoutButton);
        editMyTopics = view.findViewById(R.id.editTopicPreference);
        editPersonalInfo = view.findViewById(R.id.editPersonalInfo);
        editTimePreference = view.findViewById(R.id.editTimePreference);
        editDifficultyPreference = view.findViewById(R.id.editDifficultyPreference);
        editLoginInfo = view.findViewById(R.id.editLoginInfo);
        editSocial = view.findViewById(R.id.editSocial);
    }

    /**
     * Initializes the database helper.
     */
    private void initializeDatabase() {
        databaseHelper = new DatabaseHelper(requireContext());
    }

    /**
     * Initializes SharedPreferences and retrieves user email.
     */
    private void initializeSharedPreferences() {
        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        userEmail = sharedPreferences.getString(KEY_USER_EMAIL, null);
    }

    /**
     * Loads user data from database.
     */
    private void loadUserData() {
        currentUser = databaseHelper.getUserDetailsForMyProfilePage(userEmail);
        Log.d(TAG, "Loaded profile data for user: " + userEmail);
    }

    /**
     * Populates all profile information fields with user data.
     */
    private void populateProfileInformation() {
        userEmailTextView.setText(userEmail);

        populatePersonalInfo();
        populateStudyPreferences();
        populateSocialLinks();
        populatePassword();
    }

    /**
     * Populates personal information fields.
     */
    private void populatePersonalInfo() {
        myFirstName.setText("First Name: " + currentUser.getFirstName());
        myLastName.setText("Last Name: " + currentUser.getLastName());
        myAge.setText("Age: " + currentUser.getAge());
        myGender.setText("Gender: " + currentUser.getGender());
        myOccupation.setText("Occupation: " + currentUser.getOccupation());
        myEmail.setText("Email: " + userEmail);
    }

    /**
     * Populates study preferences fields.
     */
    private void populateStudyPreferences() {
        myTopics.setText(databaseHelper.getUserTopicString(userEmail));
        myTime.setText(currentUser.getFormattedStudyTime());
        myDifficultyLevel.setText(currentUser.getStudyDifficultyLevel());
    }

    /**
     * Populates social media links fields.
     */
    private void populateSocialLinks() {
        myLinkedIn.setText("Linked In: " + currentUser.getLinkedIn());
        myGithub.setText("Github: " + currentUser.getGithub());
        myPersonal.setText("Personal: " + currentUser.getPersonal());
    }

    /**
     * Populates password field with masked password.
     */
    private void populatePassword() {
        String password = databaseHelper.getPassword(userEmail);
        passwordEditText.setText(password);
        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    /**
     * Sets up password visibility toggle functionality.
     */
    private void setupPasswordVisibilityToggle() {
        showPasswordCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            passwordEditText.setSelection(passwordEditText.length());
        });
    }

    /**
     * Sets up click listeners for all interactive elements.
     */
    private void setupClickListeners() {
        editMyTopics.setOnClickListener(v -> navigateToEditTopics());
        editDifficultyPreference.setOnClickListener(v -> navigateToEditDifficulty());
        editTimePreference.setOnClickListener(v -> navigateToEditTime());
        editPersonalInfo.setOnClickListener(v -> navigateToEditPersonalInfo());
        editLoginInfo.setOnClickListener(v -> navigateToResetPassword());
        editSocial.setOnClickListener(v -> navigateToEditSocialAccounts());
        logoutButton.setOnClickListener(v -> handleLogout());
    }

    /**
     * Navigates to the edit topics activity.
     */
    private void navigateToEditTopics() {
        Intent intent = new Intent(requireContext(), EditMyTopicPreferences.class);
        intent.putExtra(EXTRA_USER_EMAIL, userEmail);
        intent.putExtra(EXTRA_TOPICS, currentUser.getTopicInterested());
        startActivity(intent);
    }

    /**
     * Navigates to the edit difficulty level activity.
     */
    private void navigateToEditDifficulty() {
        Intent intent = new Intent(requireContext(), EditDifficultyLevel.class);
        intent.putExtra(EXTRA_USER_EMAIL, userEmail);
        intent.putExtra(EXTRA_USER_DIFFICULTY_LEVEL, currentUser.getStudyDifficultyLevel());
        startActivity(intent);
    }

    /**
     * Navigates to the edit time preferences activity.
     */
    private void navigateToEditTime() {
        Intent intent = new Intent(requireContext(), EditPreferredTime.class);
        intent.putExtra(EXTRA_USER_EMAIL, userEmail);
        startActivity(intent);
    }

    /**
     * Navigates to the edit personal information activity.
     */
    private void navigateToEditPersonalInfo() {
        Intent intent = new Intent(requireContext(), EditPersonalInformation.class);
        intent.putExtra(EXTRA_USER_EMAIL, userEmail);
        intent.putExtra(EXTRA_USER_FIRST_NAME, currentUser.getFirstName());
        intent.putExtra(EXTRA_USER_LAST_NAME, currentUser.getLastName());
        intent.putExtra(EXTRA_USER_AGE, String.valueOf(currentUser.getAge()));
        intent.putExtra(EXTRA_USER_OCCUPATION, String.valueOf(currentUser.getOccupation()));
        intent.putExtra(EXTRA_USER_GENDER, currentUser.getGender());
        startActivity(intent);
    }

    /**
     * Navigates to the reset password activity.
     */
    private void navigateToResetPassword() {
        Intent intent = new Intent(requireContext(), ResetPassword.class);
        intent.putExtra(EXTRA_USER_EMAIL, userEmail);
        startActivity(intent);
    }

    /**
     * Navigates to the edit social accounts activity.
     */
    private void navigateToEditSocialAccounts() {
        Intent intent = new Intent(requireContext(), EditSocialAccounts.class);
        intent.putExtra(EXTRA_USER_EMAIL, userEmail);
        intent.putExtra(EXTRA_LINKEDIN, currentUser.getLinkedIn());
        intent.putExtra(EXTRA_GITHUB, currentUser.getGithub());
        intent.putExtra(EXTRA_PERSONAL, currentUser.getPersonal());
        startActivity(intent);
    }

    /**
     * Handles the logout process.
     * Clears user session and navigates to login activity.
     */
    private void handleLogout() {
        Log.d(TAG, "Logging out user: " + userEmail);

        clearUserSession();
        navigateToLogin();
        finishActivity();

        Log.d(TAG, "Logout complete");
    }

    /**
     * Clears user session from SharedPreferences.
     */
    private void clearUserSession() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_USER_EMAIL);
        editor.apply();
    }

    /**
     * Navigates to the login activity and clears the back stack.
     */
    private void navigateToLogin() {
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    /**
     * Finishes the current activity.
     */
    private void finishActivity() {
        requireActivity().finish();
    }
}
