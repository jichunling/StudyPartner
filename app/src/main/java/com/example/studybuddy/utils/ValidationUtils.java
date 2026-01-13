package com.example.studybuddy.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validation methods for email is valid and password
 */
public class ValidationUtils {

    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty() || email.isBlank()) {
            return false;
        }

        Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
        Matcher matcher = emailPattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isValidPassword(String password) {
        if (password == null || password.isEmpty() || password.isBlank()) {
            return false;
        }
        Pattern passwordPattern = Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{6,}$");
        Matcher matcher = passwordPattern.matcher(password);
        return matcher.matches();
    }

    public static boolean isUserNameEmpty(String name){
        if(name.isEmpty() || name.isBlank())
            return false;
        return true;
    }


}
