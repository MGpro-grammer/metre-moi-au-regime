package be.esi.prj.service;

import be.esi.prj.dto.UserDto;
import be.esi.prj.enumeration.ActivityLevel;
import be.esi.prj.enumeration.Gender;
import be.esi.prj.model.UserFacade;

import java.time.LocalDate;

/**
 * This service handles user registration in multiple steps.
 * It stores temporary information while the user fills out the registration form.
 */
public class RegistrationService {

    private static RegistrationService instance;
    private final UserFacade userFacade;

    private String tempEmail;
    private String tempPassword;
    private String tempName;
    private int tempAge;
    private Gender tempGender;
    private double tempHeight;
    private double tempWeight;
    private double tempGoalWeight;
    private LocalDate tempStartDate;
    private LocalDate tempEndDate;
    private ActivityLevel tempActivityLevel;

    /**
     * Creates a new RegistrationService with a user facade.
     * This is private because the class uses the singleton pattern.
     * @param userFacade the service to handle user operations
     */
    private RegistrationService(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    /**
     * Sets up the registration service for the first time.
     * This must be called before using getInstance().
     * @param facade the user facade to use for registration
     */
    public static void initialize(UserFacade facade) {
        if (instance == null) {
            instance = new RegistrationService(facade);
        }
    }

    /**
     * Gets the single instance of this service.
     * The service must be initialized first before calling this method.
     * @return the registration service instance
     */
    public static RegistrationService getInstance() {
        if (instance == null) {
            throw new IllegalStateException("RegistrationService has not been initialized.");
        }
        return instance;
    }

    /**
     * Starts the registration process with basic login information.
     * Saves the email and password temporarily while waiting for more details.
     * @param email the user's email address
     * @param password the user's password
     */
    public void startRegistration(String email, String password) {
        this.tempEmail = email;
        this.tempPassword = password;
    }

    /**
     * Adds all the personal details for the registration.
     * Saves all the user information temporarily before completing registration.
     * @param name the user's full name
     * @param age the user's age in years
     * @param gender the user's gender
     * @param height the user's height in centimeters
     * @param weight the user's current weight in kilograms
     * @param goalWeight the user's target weight in kilograms
     * @param startDate when the user starts their goal
     * @param endDate when the user wants to reach their goal
     * @param activityLevel how active the user is
     */
    public void addDetails(String name, int age, Gender gender, double height, double weight, double goalWeight,
                           LocalDate startDate, LocalDate endDate, ActivityLevel activityLevel) {

        this.tempName = name;
        this.tempAge = age;
        this.tempGender = gender;
        this.tempHeight = height;
        this.tempWeight = weight;
        this.tempGoalWeight = goalWeight;
        this.tempStartDate = startDate;
        this.tempEndDate = endDate;
        this.tempActivityLevel = activityLevel;
    }

    /**
     * Finishes the registration by creating the user account.
     * Uses all the temporary information to create and register the new user.
     * Clears all temporary data after successful registration.
     */
    public void completeRegistration() {
        try {
            UserDto newUser = userFacade.createUser(
                    tempEmail, tempPassword, tempName, tempAge, tempGender, tempHeight,
                    tempWeight, tempGoalWeight, tempStartDate, tempEndDate, tempActivityLevel
            );
            userFacade.registerUser(newUser);

            reset();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Registration failed: " + e.getMessage(), e);
        }
    }

    /**
     * Clears all temporary registration data.
     * Sets all stored information back to empty values.
     */
    public void reset() {
        this.tempEmail = null;
        this.tempPassword = null;
        this.tempName = null;
        this.tempAge = 0;
        this.tempGender = null;
        this.tempHeight = 0.0;
        this.tempWeight = 0.0;
        this.tempGoalWeight = 0.0;
        this.tempStartDate = null;
        this.tempEndDate = null;
        this.tempActivityLevel = null;
    }
}
