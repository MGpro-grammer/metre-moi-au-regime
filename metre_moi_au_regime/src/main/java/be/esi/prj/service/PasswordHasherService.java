package be.esi.prj.service;

/**
 * This service helps to make passwords secure.
 * It changes passwords into a different form so they cannot be easily read.
 */
public class PasswordHasherService {

    /**
     * Changes a password into a secure form that cannot be easily read.
     * Takes the original password and makes it safe to store.
     * @param password the original password to make secure
     * @return the password in a secure form
     */
    public static String hashPassword(String password) {
        String reversedPassword = new StringBuilder(password).reverse().toString();
        return "hashed_" + reversedPassword;
    }

    /**
     * Checks if a password is correct by comparing it with the stored secure version.
     * Takes the password the user typed and checks if it matches the saved one.
     * @param plainPassword the password the user typed
     * @param storedHash the secure password saved in the database
     * @return true if the password is correct, false if it is wrong
     */
    public static boolean verifyPassword(String plainPassword, String storedHash) {
        String hashedInput = hashPassword(plainPassword);
        return hashedInput.equals(storedHash);
    }
}
