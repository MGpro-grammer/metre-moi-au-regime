package be.esi.prj.service;

/**
 * This service checks if email addresses and passwords are correct.
 * It makes sure they follow the right rules before allowing users to register or login.
 */
public class ValidationAuthService {

    /**
     * Checks if an email address is written correctly.
     * Makes sure it has the right format like "user@example.com".
     * @param email the email address to check
     * @return true if the email is correct, false if it has mistakes
     */
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email != null && email.matches(emailRegex);
    }

    /**
     * Checks if a password is strong enough and safe to use.
     * The password must have at least 8 characters, uppercase letters, lowercase letters, numbers, and special characters.
     * @param password the password to check
     * @return true if the password is strong enough, false if it needs to be better
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean hasUpperCase = !password.equals(password.toLowerCase());
        boolean hasLowerCase = !password.equals(password.toUpperCase());
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecialChar = password.matches(".*[!@#$%^&*(),.?\":{}|<>+=-].*");

        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar;
    }
}
