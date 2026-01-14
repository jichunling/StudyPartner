package com.example.studypartner.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ValidationUtils
 *
 * Utility class providing static validation methods for user input in the studyPartner application.
 * Ensures data integrity through comprehensive validation of emails, passwords, names, ages, and URLs.
 *
 * Validation Features:
 * - Email format validation (RFC-compliant basic pattern)
 * - Password strength validation (uppercase, lowercase, digit, minimum length)
 * - User name non-empty validation
 * - Age range validation (13-120 years)
 * - URL format validation for social media links
 *
 * Design Pattern:
 * This is a utility class with only static methods and no state. It uses the Singleton pattern
 * through a private constructor to prevent instantiation.
 *
 * Performance Optimization:
 * All regex patterns are compiled once as static final fields to avoid repeated compilation overhead.
 *
 */
public final class ValidationUtils {

    // Age constraints
    private static final int MIN_AGE = 13;  // Minimum age for COPPA compliance
    private static final int MAX_AGE = 120; // Maximum reasonable age

    // Password constraints
    private static final int MIN_PASSWORD_LENGTH = 6;

    /**
     * Email validation pattern.
     * Validates standard email format: localpart@domain.tld
     * - Local part: alphanumeric and common symbols (. _ % + -)
     * - Domain: alphanumeric with dots and hyphens
     * - TLD: 2-6 alphabetic characters
     */
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");

    /**
     * Password strength pattern.
     * Requires all of the following:
     * - (?=.*[A-Z]): At least one uppercase letter
     * - (?=.*[a-z]): At least one lowercase letter
     * - (?=.*\\d): At least one digit
     * - .{6,}: Minimum 6 characters total
     */
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{6,}$");

    /**
     * URL validation pattern for social media links.
     * Supports:
     * - Optional protocol (http:// or https://)
     * - Optional www subdomain
     * - Domain name with alphanumeric characters and hyphens
     * - TLD with at least 2 characters
     * - Optional path components
     */
    private static final Pattern URL_PATTERN =
            Pattern.compile("^(https?://)?(www\\.)?[a-zA-Z0-9-]+(\\.[a-zA-Z]{2,})+(/.*)?$");

    /**
     * Private constructor to prevent instantiation of utility class.
     * This class should only be used through its static methods.
     *
     * @throws AssertionError if instantiation is attempted
     */
    private ValidationUtils() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    /**
     * Validates email address format.
     *
     * Checks if the email follows standard email format rules:
     * - Must not be null or empty
     * - Must contain @ symbol
     * - Must have valid domain and top-level domain
     * - Only allows alphanumeric characters and common email symbols (. _ % + -)
     *
     * Examples:
     * - Valid: "user@example.com", "john.doe@university.edu", "student+notes@gmail.com"
     * - Invalid: "user@", "@example.com", "user@.com", "user name@example.com"
     *
     * @param email The email address to validate
     * @return true if email format is valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        // Check for null or empty string
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        // Match against email pattern
        Matcher matcher = EMAIL_PATTERN.matcher(email.trim());
        return matcher.matches();
    }

    /**
     * Validates password strength.
     *
     * Password must meet all of these requirements:
     * - Minimum 6 characters long
     * - At least one uppercase letter (A-Z)
     * - At least one lowercase letter (a-z)
     * - At least one digit (0-9)
     *
     * Examples:
     * - Valid: "Password1", "MyPass123", "Secure99"
     * - Invalid: "pass123" (no uppercase), "PASSWORD1" (no lowercase), "Password" (no digit)
     *
     * Note: For production, consider requiring special characters and longer minimum length.
     *
     * @param password The password to validate
     * @return true if password meets strength requirements, false otherwise
     */
    public static boolean isValidPassword(String password) {
        // Check for null or empty string
        if (password == null || password.trim().isEmpty()) {
            return false;
        }

        // Match against password pattern
        Matcher matcher = PASSWORD_PATTERN.matcher(password);
        return matcher.matches();
    }

    /**
     * Validates that a user name is not empty.
     *
     * Checks if the provided name contains at least one non-whitespace character.
     * This is used for first name, last name, and general name fields.
     *
     * Examples:
     * - Valid: "John", "Mary Jane", "O'Brien"
     * - Invalid: "", "   ", null
     *
     * @param name The user name to validate
     * @return true if name contains at least one non-whitespace character, false otherwise
     */
    public static boolean isValidUserName(String name) {
        return name != null && !name.trim().isEmpty();
    }

    /**
     * Validates that age is within acceptable range for study partners.
     *
     * Age range: 13-120 years
     * - Minimum 13: COPPA (Children's Online Privacy Protection Act) compliance
     * - Maximum 120: Reasonable upper bound for human age
     *
     * Examples:
     * - Valid: 13, 18, 25, 50, 120
     * - Invalid: 0, 5, 12, 121, -1
     *
     * @param age The age to validate
     * @return true if age is between 13 and 120 (inclusive), false otherwise
     */
    public static boolean isValidAge(int age) {
        return age >= MIN_AGE && age <= MAX_AGE;
    }

    /**
     * Validates URL format for social media links and personal websites.
     *
     * Accepts:
     * - Empty or null URLs (treated as optional field)
     * - URLs with or without protocol (http:// or https://)
     * - URLs with or without www subdomain
     * - URLs with path components
     *
     * Examples:
     * - Valid: "", "https://github.com/user", "linkedin.com/in/profile", "www.example.com/page"
     * - Invalid: "not a url", "example.", "http://"
     *
     * Note: This is basic validation. For production, consider using URLConnection to verify
     * the URL is actually reachable, or use more sophisticated URL parsing libraries.
     *
     * @param url The URL to validate
     * @return true if URL format is valid or empty (optional field), false otherwise
     */
    public static boolean isValidUrl(String url) {
        // Allow empty URLs (optional field)
        if (url == null || url.trim().isEmpty()) {
            return true;
        }

        // Match against URL pattern
        return URL_PATTERN.matcher(url.trim()).matches();
    }

    /**
     * Gets the minimum age requirement.
     *
     * @return Minimum age (13 years for COPPA compliance)
     */
    public static int getMinAge() {
        return MIN_AGE;
    }

    /**
     * Gets the maximum age allowed.
     *
     * @return Maximum age (120 years)
     */
    public static int getMaxAge() {
        return MAX_AGE;
    }

    /**
     * Gets the minimum password length requirement.
     *
     * @return Minimum password length (6 characters)
     */
    public static int getMinPasswordLength() {
        return MIN_PASSWORD_LENGTH;
    }
}
