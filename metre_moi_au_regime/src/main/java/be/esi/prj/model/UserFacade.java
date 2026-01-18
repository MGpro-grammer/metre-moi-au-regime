package be.esi.prj.model;

import be.esi.prj.dto.UserDto;
import be.esi.prj.enumeration.ActivityLevel;
import be.esi.prj.enumeration.Gender;
import be.esi.prj.repository.UserRepository;
import be.esi.prj.service.SessionService;

import java.time.LocalDate;
import java.util.Optional;

import static be.esi.prj.service.PasswordHasherService.hashPassword;
import static be.esi.prj.service.PasswordHasherService.verifyPassword;
import static be.esi.prj.service.ValidationAuthService.isValidEmail;
import static be.esi.prj.service.ValidationAuthService.isValidPassword;

/**
 * This class manages user accounts and authentication.
 * It helps with login, registration, and user data management.
 */
public class UserFacade {

    private final UserRepository userRepository;

    /**
     * Creates a new UserFacade with a user repository.
     * @param userRepository the repository to store and find user data
     */
    public UserFacade(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ##### User Management Methods #####

    /**
     * Logs in a user with email and password.
     * Checks if the password is correct and updates the goal if needed.
     * @param email the user's email address
     * @param password the user's password
     * @return the user information after successful login
     */
    public UserDto loginUser(String email, String password) {
        UserDto userDto = getUserDtoByEmail(email.toLowerCase());

        if (verifyPassword(password, userDto.password())) {
            return updateGoalStatus(userDto);
        }
        throw new IllegalArgumentException("Invalid password");
    }

    /**
     * Creates a new user account with the given information.
     * Checks if the email is already used and validates all data.
     * @param userDto the new user information to register
     */
    public void registerUser(UserDto userDto) {
        validateUserDto(userDto);

        if (!isValidPassword(userDto.password())) {
            throw new IllegalArgumentException("Password does not meet the security requirements.");
        }

        if (userRepository.findByEmail(userDto.email()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        UserDto hashedUserDto = hashPasswordUser(userDto);
        saveUser(hashedUserDto);
    }

    /**
     * Saves user information to the database.
     * Also updates the user session with the new information.
     * @param userDetails the user information to save
     */
    public void saveUser(UserDto userDetails) {
        validateUserDto(userDetails);
        userRepository.save(userDetails);

        SessionService.getInstance().updateUserInSession(userDetails);
    }

    // ##### User Creation Methods #####

    /**
     * Creates a new user with all the required information.
     * This method builds a complete user profile.
     * @param email the user's email address
     * @param password the user's password
     * @param name the user's full name
     * @param age the user's age in years
     * @param gender the user's gender
     * @param height the user's height in centimeters
     * @param weight the user's current weight in kilograms
     * @param goalWeight the user's target weight in kilograms
     * @param startDate when the user starts their goal
     * @param endDate when the user wants to reach their goal
     * @param activityLevel how active the user is
     * @return a new user with all the information
     */
    public UserDto createUser(
            String email, String password, String name, int age, Gender gender,
            double height, double weight, double goalWeight,
            LocalDate startDate, LocalDate endDate, ActivityLevel activityLevel) {

        UserDto newUser = new UserDto(
                -1, // User ID will be auto-generated
                email.toLowerCase(),
                password, // Password should be hashed later
                name,
                age,
                gender,
                height,
                weight,
                goalWeight,
                startDate,
                endDate,
                activityLevel
        );

        validateUserDto(newUser);

        return newUser;
    }

    // ##### Helper Methods #####

    /**
     * Checks if all user information is correct and complete.
     * Makes sure email, password, name, age, and other data are valid.
     * @param userDto the user information to check
     */
    private void validateUserDto(UserDto userDto) {
        if (!isValidEmail(userDto.email())) {
            throw new IllegalArgumentException("Invalid email");
        }
        if (userDto.password() == null || userDto.password().isEmpty()) {
            throw new IllegalArgumentException("Missing or empty password");
        }

        if (userDto.name() == null || userDto.name().isEmpty()) {
            throw new IllegalArgumentException("Missing or empty name");
        }
        if (userDto.age() <= 0) {
            throw new IllegalArgumentException("Invalid age");
        }
        if (userDto.height() <= 0 || userDto.weight() <= 0 || userDto.goalWeight() <= 0) {
            throw new IllegalArgumentException("Invalid height, weight, or goal weight");
        }
        if (userDto.activityLevel() == null) {
            throw new IllegalArgumentException("Missing activity level");
        }
        if (userDto.startDate() == null || userDto.endDate() == null) {
            throw new IllegalArgumentException("Missing start or end date");
        }
    }

    /**
     * Finds a user by their email address.
     * @param email the email address to search for
     * @return the user information if found
     */
    private UserDto getUserDtoByEmail(String email) {
        Optional<UserDto> userDtoOpt = userRepository.findByEmail(email);
        if (userDtoOpt.isPresent()) {
            return userDtoOpt.get();
        } else {
            throw new IllegalArgumentException("Invalid email or user not found");
        }
    }

    /**
     * Creates a new user with a hashed password.
     * Changes the plain text password to a secure hashed version.
     * @param userDto the user with plain text password
     * @return the same user but with hashed password
     */
    private UserDto hashPasswordUser(UserDto userDto) {
        return new UserDto(
                userDto.userId(),
                userDto.email(),
                hashPassword(userDto.password()), // Password hashed
                userDto.name(),
                userDto.age(),
                userDto.gender(),
                userDto.height(),
                userDto.weight(),
                userDto.goalWeight(),
                userDto.startDate(),
                userDto.endDate(),
                userDto.activityLevel()
        );
    }

    /**
     * Extends the user's goal by adding more days.
     * Creates a new goal period starting from today.
     * @param userDto the user whose goal needs to be extended
     * @param additionalDays how many days to add to the goal
     * @return the user with the extended goal dates
     */
    private UserDto extendGoal(UserDto userDto, int additionalDays) {
        return new UserDto(
                userDto.userId(),
                userDto.email(),
                userDto.password(),
                userDto.name(),
                userDto.age(),
                userDto.gender(),
                userDto.height(),
                userDto.weight(),
                userDto.goalWeight(),
                LocalDate.now(), // startDate updated
                LocalDate.now().plusDays(additionalDays), // endDate extended
                userDto.activityLevel()
        );
    }

    /**
     * Checks if the user's goal period has ended and extends it if needed.
     * If the goal end date is in the past, it creates a new 90-day goal.
     * @param userDto the user to check
     * @return the user with updated goal dates if needed
     */
    private UserDto updateGoalStatus(UserDto userDto) {
        try {
            UserDto userUpdated = userDto;
            if (userDto.endDate().isBefore(LocalDate.now())) {
                userUpdated = extendGoal(userDto, 90);
                saveUser(userUpdated);
            }
            return userUpdated;
        }
        catch (Exception e) {
            throw new RuntimeException("Error updating goal status");
        }
    }
}
