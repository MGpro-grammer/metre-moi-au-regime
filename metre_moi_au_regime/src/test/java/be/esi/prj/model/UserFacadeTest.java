package be.esi.prj.model;

import static org.junit.jupiter.api.Assertions.*;

import be.esi.prj.dto.UserDto;
import be.esi.prj.enumeration.ActivityLevel;
import be.esi.prj.enumeration.Gender;
import be.esi.prj.repository.UserRepository;
import be.esi.prj.service.SessionService;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static be.esi.prj.service.PasswordHasherService.hashPassword;

class UserFacadeTest {

    private UserFacade userFacade;
    private UserRepository userRepository;
    private SessionService sessionService;
    private TestDataBuilder testDataBuilder;

    private final List<Integer> userIdsToClean = new ArrayList<>();

    @BeforeEach
    void setUp() {
        userRepository = new UserRepository();
        sessionService = SessionService.getInstance();
        userFacade = new UserFacade(userRepository);
        testDataBuilder = new TestDataBuilder();
        sessionService.logout();
    }

    @AfterEach
    void tearDown() {
        userIdsToClean.forEach(userRepository::deleteById);
        userIdsToClean.clear();
        sessionService.logout();
    }

    // ##### User Management Tests #####

    @Test
    void testLoginUserWithValidCredentialsReturnsUser() {
        // Arrange
        UserDto user = testDataBuilder.createDefaultUser();
        UserDto hashedUser = testDataBuilder.createHashedUser(user);
        int userId = userRepository.save(hashedUser);
        userIdsToClean.add(userId);

        // Act
        UserDto loggedInUser = userFacade.loginUser(user.email(), "Password123+");

        // Assert
        assertNotNull(loggedInUser);
        assertEquals(user.email(), loggedInUser.email());
        assertEquals(user.name(), loggedInUser.name());
    }

    @Test
    void testLoginUserWithExpiredGoalExtendsGoalAndReturnsUpdatedUser() {
        // Arrange
        UserDto user = testDataBuilder.createUserWithExpiredGoal();
        UserDto hashedUser = testDataBuilder.createHashedUser(user);
        int userId = userRepository.save(hashedUser);
        userIdsToClean.add(userId);

        // Act
        UserDto loggedInUser = userFacade.loginUser(user.email(), "Password123+");

        // Assert
        assertNotNull(loggedInUser);
        assertTrue(loggedInUser.endDate().isAfter(LocalDate.now()));
        assertEquals(LocalDate.now(), loggedInUser.startDate());
    }

    @Test
    void testLoginUserWithNonExpiredGoalReturnsUserWithoutUpdate() {
        // Arrange
        UserDto user = testDataBuilder.createUserWithNonExpiredGoal();
        UserDto hashedUser = testDataBuilder.createHashedUser(user);
        int userId = userRepository.save(hashedUser);
        userIdsToClean.add(userId);
        LocalDate originalEndDate = user.endDate();

        // Act
        UserDto loggedInUser = userFacade.loginUser(user.email(), "Password123+");

        // Assert
        assertNotNull(loggedInUser);
        assertEquals(originalEndDate, loggedInUser.endDate());
    }

    @Test
    void testLoginUserWithInvalidPasswordThrowsIllegalArgumentException() {
        // Arrange
        UserDto user = testDataBuilder.createDefaultUser();
        UserDto hashedUser = testDataBuilder.createHashedUser(user);
        int userId = userRepository.save(hashedUser);
        userIdsToClean.add(userId);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                userFacade.loginUser(user.email(), "wrongPassword"));
    }

    @Test
    void testLoginUserWithInvalidEmailThrowsIllegalArgumentException() {
        // Arrange
        UserDto user = testDataBuilder.createDefaultUser();
        UserDto hashedUser = testDataBuilder.createHashedUser(user);
        int userId = userRepository.save(hashedUser);
        userIdsToClean.add(userId);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                userFacade.loginUser("invalid-email", "Password123+"));
    }

    @Test
    void testLoginUserWithNonExistentEmailThrowsIllegalArgumentException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                userFacade.loginUser("nonexistent@example.com", "Password123+"));
    }

    // ##### User Registration Tests #####

    @Test
    void testRegisterUserWithValidDataSavesUser() {
        // Arrange
        UserDto user = testDataBuilder.createDefaultUser();

        // Act
        userFacade.registerUser(user);

        // Assert
        Optional<UserDto> savedUser = userRepository.findByEmail(user.email());
        assertTrue(savedUser.isPresent());
        assertEquals(user.email(), savedUser.get().email());
        assertEquals(user.name(), savedUser.get().name());

        // Cleanup
        userIdsToClean.add(savedUser.get().userId());
    }

    @Test
    void testRegisterUserWithValidDataHashesPassword() {
        // Arrange
        UserDto user = testDataBuilder.createDefaultUser();
        System.out.println("Original Password: " + user.password());
        String originalPassword = user.password();

        // Act
        userFacade.registerUser(user);

        // Assert
        Optional<UserDto> savedUser = userRepository.findByEmail(user.email());
        System.out.println("Saved User: " + savedUser);
        assertTrue(savedUser.isPresent());
        assertNotEquals(originalPassword, savedUser.get().password());

        // Cleanup
        userIdsToClean.add(savedUser.get().userId());
    }

    @Test
    void testRegisterUserWithInvalidEmailThrowsIllegalArgumentException() {
        // Arrange
        UserDto user = testDataBuilder.createUserWithInvalidEmail();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userFacade.registerUser(user));
        assertEquals("Invalid email", exception.getMessage());
    }

    @Test
    void testRegisterUserWithWeakPasswordThrowsIllegalArgumentException() {
        // Arrange
        UserDto user = testDataBuilder.createUserWithWeakPassword();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userFacade.registerUser(user));
        assertEquals("Password does not meet the security requirements.", exception.getMessage());
    }

    @Test
    void testRegisterUserWithExistingEmailThrowsIllegalArgumentException() {
        // Arrange
        UserDto existingUser = testDataBuilder.createDefaultUser();
        userFacade.registerUser(existingUser);
        Optional<UserDto> savedUser = userRepository.findByEmail(existingUser.email());
        savedUser.ifPresent(user -> userIdsToClean.add(user.userId()));

        UserDto duplicateUser = testDataBuilder.createUserWithExistingEmail();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userFacade.registerUser(duplicateUser));
        assertEquals("Email already exists", exception.getMessage());
    }

    @Test
    void testRegisterUserWithNullPasswordThrowsIllegalArgumentException() {
        // Arrange
        UserDto user = testDataBuilder.createUserWithNullPassword();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userFacade.registerUser(user));
        assertEquals("Missing or empty password", exception.getMessage());
    }

    @Test
    void testRegisterUserWithEmptyPasswordThrowsIllegalArgumentException() {
        // Arrange
        UserDto user = testDataBuilder.createUserWithEmptyPassword();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userFacade.registerUser(user));
        assertEquals("Missing or empty password", exception.getMessage());
    }

    private static class TestDataBuilder {

        public UserDto createDefaultUser() {
            return new UserDto(
                    -1,
                    "test@example.com",
                    "Password123+",
                    "Test User",
                    25,
                    Gender.MALE,
                    175.0,
                    70.0,
                    65.0,
                    LocalDate.of(2025, 1, 1),
                    LocalDate.of(2025, 12, 31),
                    ActivityLevel.MODERATE
            );
        }

        public UserDto createUserWithExpiredGoal() {
            return new UserDto(
                    -1,
                    "expired@example.com",
                    "Password123+",
                    "Expired User",
                    30,
                    Gender.FEMALE,
                    165.0,
                    60.0,
                    55.0,
                    LocalDate.of(2023, 1, 1),
                    LocalDate.of(2023, 12, 31),
                    ActivityLevel.LIGHT
            );
        }

        public UserDto createUserWithNonExpiredGoal() {
            return new UserDto(
                    -1,
                    "active@example.com",
                    "Password123+",
                    "Active User",
                    28,
                    Gender.MALE,
                    180.0,
                    75.0,
                    70.0,
                    LocalDate.now(),
                    LocalDate.now().plusMonths(6),
                    ActivityLevel.VERY_ACTIVE
            );
        }

        public UserDto createHashedUser(UserDto user) {
            return new UserDto(
                    user.userId(),
                    user.email(),
                    hashPassword(user.password()),
                    user.name(),
                    user.age(),
                    user.gender(),
                    user.height(),
                    user.weight(),
                    user.goalWeight(),
                    user.startDate(),
                    user.endDate(),
                    user.activityLevel()
            );
        }
        public UserDto createUserWithInvalidEmail() {
            return new UserDto(-1, "invalid-email", "Password123+", "Test User", 25, Gender.MALE, 175.0, 70.0, 65.0, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), ActivityLevel.MODERATE);
        }

        public UserDto createUserWithWeakPassword() {
            return new UserDto(-1, "test@example.com", "weak", "Test User", 25, Gender.MALE, 175.0, 70.0, 65.0, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), ActivityLevel.MODERATE);
        }

        public UserDto createUserWithExistingEmail() {
            return new UserDto(-1, "test@example.com", "Password123+", "Another User", 30, Gender.FEMALE, 165.0, 60.0, 55.0, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), ActivityLevel.LIGHT);
        }

        public UserDto createUserWithNullPassword() {
            return new UserDto(-1, "test@example.com", null, "Test User", 25, Gender.MALE, 175.0, 70.0, 65.0, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), ActivityLevel.MODERATE);
        }

        public UserDto createUserWithEmptyPassword() {
            return new UserDto(-1, "test@example.com", "", "Test User", 25, Gender.MALE, 175.0, 70.0, 65.0, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), ActivityLevel.MODERATE);
        }
    }
}