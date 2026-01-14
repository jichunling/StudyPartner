package com.example.studypartner.data.model;

import java.util.ArrayList;
import java.util.List;

/**
 * User Model Class
 *
 * Represents a study partner user in the studyPartner application.
 * This class encapsulates all user-related information including personal details,
 * study preferences, and social media links.
 *
 */
public class User {
    // Personal Information
    private String userID;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private int age;
    private String gender;
    private String occupation;

    // Study Preferences
    private ArrayList<String> preferredStudyTime;
    private ArrayList<String> topicInterested;
    private String studyDifficultyLevel;

    // Social Media Links
    private String linkedIn;
    private String github;
    private String personal;

    // Study Partner Connections
    private List<String> connections;

    /**
     * Primary constructor for creating a new user with essential information.
     *
     * @param email User's email address (used as unique identifier)
     * @param password User's password (should be hashed in production)
     * @param firstName User's first name
     * @param lastName User's last name
     * @param age User's age
     * @param gender User's gender
     * @param preferredStudyTime List of preferred study time slots
     * @param topicInterested List of academic topics the user is interested in
     * @param studyDifficultyLevel User's preferred difficulty level (Beginner/Intermediate/Advanced)
     */
    public User(String email, String password, String firstName, String lastName, int age,
                String gender, ArrayList<String> preferredStudyTime, ArrayList<String> topicInterested,
                String studyDifficultyLevel) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.gender = gender;
        this.preferredStudyTime = preferredStudyTime != null ? preferredStudyTime : new ArrayList<>();
        this.topicInterested = topicInterested != null ? topicInterested : new ArrayList<>();
        this.studyDifficultyLevel = studyDifficultyLevel != null ? studyDifficultyLevel : "";
        this.connections = new ArrayList<>();
        this.linkedIn = "";
        this.github = "";
        this.personal = "";
    }

    /**
     * Constructor with occupation field included.
     *
     * @param email User's email address
     * @param password User's password
     * @param firstName User's first name
     * @param lastName User's last name
     * @param age User's age
     * @param gender User's gender
     * @param preferredStudyTime List of preferred study time slots
     * @param topicInterested List of academic topics
     * @param studyDifficultyLevel Preferred difficulty level
     * @param occupation User's occupation or major
     */
    public User(String email, String password, String firstName, String lastName, int age,
                String gender, ArrayList<String> preferredStudyTime, ArrayList<String> topicInterested,
                String studyDifficultyLevel, String occupation) {
        this(email, password, firstName, lastName, age, gender, preferredStudyTime, topicInterested, studyDifficultyLevel);
        this.occupation = occupation != null ? occupation : "";
    }

    /**
     * Full constructor including social media links.
     *
     * @param email User's email address
     * @param password User's password
     * @param firstName User's first name
     * @param lastName User's last name
     * @param age User's age
     * @param gender User's gender
     * @param preferredStudyTime List of preferred study time slots
     * @param topicInterested List of academic topics
     * @param studyDifficultyLevel Preferred difficulty level
     * @param occupation User's occupation or major
     * @param linkedIn LinkedIn profile URL
     * @param github GitHub profile URL
     * @param personal Personal website URL
     */
    public User(String email, String password, String firstName, String lastName, int age,
                String gender, ArrayList<String> preferredStudyTime, ArrayList<String> topicInterested,
                String studyDifficultyLevel, String occupation, String linkedIn, String github, String personal) {
        this(email, password, firstName, lastName, age, gender, preferredStudyTime, topicInterested, studyDifficultyLevel, occupation);
        this.linkedIn = linkedIn != null ? linkedIn : "";
        this.github = github != null ? github : "";
        this.personal = personal != null ? personal : "";
    }

    /**
     * Minimal constructor for creating a user reference by ID only.
     *
     * @param userID The unique user identifier
     */
    public User(String userID) {
        this.userID = userID;
        this.connections = new ArrayList<>();
        this.linkedIn = "";
        this.github = "";
        this.personal = "";
    }

    // ==================== Getters ====================

    /**
     * Gets the user's email address.
     *
     * @return User's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the user's password.
     * Note: In production, passwords should never be returned in plain text.
     *
     * @return User's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the user's age.
     *
     * @return User's age
     */
    public int getAge() {
        return age;
    }

    /**
     * Gets the user's gender.
     *
     * @return User's gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * Gets the user's first name.
     *
     * @return User's first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Gets the user's last name.
     *
     * @return User's last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Gets the user's occupation.
     *
     * @return User's occupation or empty string if not set
     */
    public String getOccupation() {
        return occupation != null ? occupation : "";
    }

    /**
     * Gets the user's unique identifier.
     *
     * @return User ID
     */
    public String getUserID() {
        return userID;
    }

    /**
     * Gets the user's preferred study time as a formatted string.
     * Joins all study times with commas for display purposes.
     *
     * @return Comma-separated string of study times
     */
    public String getFormattedStudyTime() {
        return preferredStudyTime != null ? String.join(", ", preferredStudyTime) : "";
    }

    /**
     * Gets the user's preferred study difficulty level.
     *
     * @return Difficulty level (Beginner/Intermediate/Advanced)
     */
    public String getStudyDifficultyLevel() {
        return studyDifficultyLevel != null ? studyDifficultyLevel : "";
    }

    /**
     * Gets the list of preferred study times.
     *
     * @return ArrayList of study time slots
     */
    public ArrayList<String> getPreferredStudyTime() {
        return preferredStudyTime;
    }

    /**
     * Gets the list of topics the user is interested in.
     *
     * @return ArrayList of academic topics
     */
    public ArrayList<String> getTopicInterested() {
        return topicInterested;
    }

    /**
     * Gets the list of user's study partner connections.
     *
     * @return List of connected user emails
     */
    public List<String> getConnections() {
        return connections;
    }

    /**
     * Gets the user's LinkedIn profile URL.
     *
     * @return LinkedIn URL or empty string if not set
     */
    public String getLinkedIn() {
        return linkedIn != null ? linkedIn : "";
    }

    /**
     * Gets the user's GitHub profile URL.
     *
     * @return GitHub URL or empty string if not set
     */
    public String getGithub() {
        return github != null ? github : "";
    }

    /**
     * Gets the user's personal website URL.
     *
     * @return Personal website URL or empty string if not set
     */
    public String getPersonal() {
        return personal != null ? personal : "";
    }

    // ==================== Setters ====================

    /**
     * Sets the user's study difficulty level.
     *
     * @param difficulty New difficulty level (Beginner/Intermediate/Advanced)
     */
    public void setStudyDifficultyLevel(String difficulty) {
        this.studyDifficultyLevel = difficulty != null ? difficulty : "";
    }

    /**
     * Sets the user's preferred study time slots.
     *
     * @param time ArrayList of study time preferences
     */
    public void setPreferredStudyTime(ArrayList<String> time) {
        this.preferredStudyTime = time != null ? time : new ArrayList<>();
    }

    /**
     * Sets the user's interested topics.
     *
     * @param updateTopic ArrayList of academic topics
     */
    public void setTopicInterested(ArrayList<String> updateTopic) {
        this.topicInterested = updateTopic != null ? updateTopic : new ArrayList<>();
    }

    /**
     * Sets the user's occupation.
     *
     * @param occupation User's occupation or major
     */
    public void setOccupation(String occupation) {
        this.occupation = occupation != null ? occupation : "";
    }

    // ==================== Connection Management ====================

    /**
     * Adds a study partner connection to the user's network.
     * Prevents duplicate connections by checking if the email already exists.
     *
     * @param email Email of the user to connect with
     */
    public void addConnection(String email) {
        if (email != null && !email.isEmpty() && !connections.contains(email)) {
            connections.add(email);
        }
    }

    /**
     * Removes a study partner connection from the user's network.
     *
     * @param email Email of the user to disconnect from
     */
    public void removeConnection(String email) {
        if (email != null) {
            connections.remove(email);
        }
    }
}
