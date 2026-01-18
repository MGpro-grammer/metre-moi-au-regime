package be.esi.prj.service;

import be.esi.prj.dto.UserDto;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * This service manages user login sessions.
 * It keeps track of which user is currently logged in to the application.
 */
public class SessionService {

    private static SessionService instance;

    private final ObjectProperty<UserDto> currentUser = new SimpleObjectProperty<>(null);

    /**
     * Creates a new SessionService.
     * This is private because the class uses the singleton pattern.
     */
    private SessionService() {
    }

    /**
     * Gets the single instance of this service.
     * Creates a new instance if one doesn't exist yet.
     * @return the session service instance
     */
    public static SessionService getInstance() {
        if (instance == null) {
            instance = new SessionService();
        }
        return instance;
    }

    /**
     * Logs in a user to the application.
     * Saves the user information for the current session.
     * @param user the user to log in
     */
    public void login(UserDto user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null for login.");
        }
        this.currentUser.set(user);
    }

    /**
     * Logs out the current user from the application.
     * Removes the user information from the current session.
     */
    public void logout() {
        this.currentUser.set(null);
    }

    /**
     * Checks if any user is currently logged in.
     * @return true if someone is logged in, false if no one is logged in
     */
    public boolean isUserLoggedIn() {
        return this.currentUser.get() != null;
    }

    /**
     * Gets the user who is currently logged in.
     * Only works if someone is actually logged in.
     * @return the current user's information
     */
    public UserDto getCurrentUser() {
        if (!isUserLoggedIn()) {
            throw new IllegalStateException("No user is currently logged in.");
        }
        return this.currentUser.get();
    }

    /**
     * Updates the current user's information in the session.
     * Changes the stored user data with new information.
     * @param updatedUser the new user information to save
     */
    public void updateUserInSession(UserDto updatedUser) {
        if (isUserLoggedIn() && updatedUser != null) {
            this.currentUser.set(updatedUser);
        }
    }
}
