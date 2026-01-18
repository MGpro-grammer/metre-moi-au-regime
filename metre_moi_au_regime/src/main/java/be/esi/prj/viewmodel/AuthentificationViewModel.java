package be.esi.prj.viewmodel;

import be.esi.prj.dto.UserDto;
import be.esi.prj.model.UserFacade;
import be.esi.prj.service.RegistrationService;
import be.esi.prj.service.SessionService;
import be.esi.prj.service.ValidationAuthService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * This view model handles user login and registration for the user interface.
 * It manages email, password, and status messages for authentication screens.
 */
public class AuthentificationViewModel {
    private final UserFacade userFacade;
    private final SessionService sessionService;

    private final StringProperty email;
    private final StringProperty password;
    private final StringProperty confirmPassword;
    private final StringProperty loginStatusMessage;

    private final BooleanProperty isLoggedIn;

    /**
     * Creates a new AuthentificationViewModel with user and session services.
     * Sets up all the properties needed for login and registration screens.
     * @param userFacade the service to handle user operations
     * @param sessionService the service to manage user sessions
     */
    public AuthentificationViewModel(UserFacade userFacade, SessionService sessionService) {
        this.userFacade = userFacade;
        this.sessionService = sessionService;

        this.email = new SimpleStringProperty("");
        this.password = new SimpleStringProperty("");
        this.confirmPassword = new SimpleStringProperty("");
        this.loginStatusMessage = new SimpleStringProperty("");
        this.isLoggedIn = new SimpleBooleanProperty(false);
    }

    /**
     * Starts the user registration process with email and password.
     * Validates the registration information and begins the signup process.
     * @return true if registration can start, false if there are validation errors
     */
    public boolean startRegistrationProcess() {
        String emailValue = email.get();
        String passwordValue = password.get();
        String confirmPasswordValue = confirmPassword.get();

        if (!isRegisterValid(emailValue, passwordValue, confirmPasswordValue)) {
            return false;
        }

        RegistrationService.getInstance().startRegistration(emailValue, passwordValue);
        return true;
    }

    /**
     * Tries to log in a user with their email and password.
     * Checks the credentials and starts a new session if login is successful.
     * @return true if login was successful, false if there was an error
     */
    public boolean login() {
        try {
            String emailValue = email.get();
            String passwordValue = password.get();

            if (emailValue.isBlank() || passwordValue.isBlank()) {
                loginStatusMessage.set("Email and password cannot be empty.");
                return false;
            }

            UserDto user = userFacade.loginUser(emailValue, passwordValue);
            return processLoginResult(user);

        } catch (IllegalArgumentException e) {
            loginStatusMessage.set("Login error: " + e.getMessage());
            return false;
        } catch (Exception e) {
            loginStatusMessage.set("Unexpected login error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Logs out the current user and ends their session.
     * Clears all stored information and updates the login status.
     * @return true if logout was successful, false if there was an error
     */
    public boolean logout() {       // TODO : add this methode to View
        try {
            if (!isLoggedIn.get() || !sessionService.isUserLoggedIn()) {
                loginStatusMessage.set("You are not logged in.");
                return false;
            }

            sessionService.logout();
            isLoggedIn.set(false);
            clean();
            loginStatusMessage.set("Logout successful.");
            return true;
        } catch (Exception e) {
            loginStatusMessage.set("Error during logout: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if the registration information is correct and complete.
     * Validates email format, password strength, and password confirmation.
     * Shows error messages if something is wrong.
     * @param emailValue the email address to check
     * @param passwordValue the password to check
     * @param confirmPasswordValue the password confirmation to check
     * @return true if all information is valid, false if there are errors
     */
    private boolean isRegisterValid(String emailValue, String passwordValue, String confirmPasswordValue) {
        if (emailValue.isEmpty() || passwordValue.isEmpty() || confirmPasswordValue.isEmpty()) {
            loginStatusMessage.set("All fields are required.");
            return false;
        }

        if (!ValidationAuthService.isValidEmail(emailValue)) {
            loginStatusMessage.set("Invalid email format. Email must follow the pattern: example@domain.com");
            return false;
        }

        if (!ValidationAuthService.isValidPassword(passwordValue)) {
            loginStatusMessage.set("Invalid password format. Password must be at least 8 characters long and contain: \n" +
                    "uppercase letter, lowercase letter, digit, and special character (!@#$%^&*(),.?\":{}|<>)");
            return false;
        }

        if (!passwordValue.equals(confirmPasswordValue)) {
            loginStatusMessage.set("Passwords do not match.");
            return false;
        }
        return true;
    }

    /**
     * Processes the result of a login attempt.
     * If login is successful, starts a new session and updates the status.
     * @param user the user information returned from login attempt
     * @return true if login was successful, false if login failed
     */
    private boolean processLoginResult(UserDto user) {
        if (user != null) {
            sessionService.login(user);
            isLoggedIn.set(true);
            loginStatusMessage.set("Login successful.");
            return true;
        }
        loginStatusMessage.set("Invalid email or password.");
        return false;
    }

    /**
     * Clears all input fields and status messages.
     * Resets the form to its initial empty state.
     */
    private void clean() {
        email.set("");
        password.set("");
        confirmPassword.set("");
        loginStatusMessage.set("");
    }

    // ##### Setters and Getters Methods #####

    /**
     * Sets the email address.
     * @param email the user's email address
     */
    public void setEmail(String email) {
        this.email.set(email);
    }

    /**
     * Sets the password.
     * @param password the user's password
     */
    public void setPassword(String password) {
        this.password.set(password);
    }

    /**
     * Sets the password confirmation.
     * @param confirmPassword the password confirmation for registration
     */
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword.set(confirmPassword);
    }

    /**
     * Gets the current login status message.
     * @return the message showing login or registration status
     */
    public String loginStatusMessage() {
        return loginStatusMessage.get();
    }
}
