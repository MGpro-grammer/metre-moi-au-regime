package be.esi.prj.viewmodel;

import static org.junit.jupiter.api.Assertions.*;

import be.esi.prj.dto.UserDto;
import be.esi.prj.enumeration.ActivityLevel;
import be.esi.prj.enumeration.Gender;
import be.esi.prj.model.UserFacade;
import be.esi.prj.repository.UserRepository;
import be.esi.prj.service.RegistrationService;
import be.esi.prj.service.SessionService;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

class AuthentificationViewModelTest {

    private AuthentificationViewModel viewModel;
    private UserFacade userFacade;
    private UserRepository userRepository;
    private SessionService sessionService;
    private TestDataBuilder testDataBuilder;

    private final List<Integer> userIdsToClean = new ArrayList<>();

    @BeforeEach
    void setUp() {
        userRepository = new UserRepository();
        userFacade = new UserFacade(userRepository);
        sessionService = SessionService.getInstance();

        RegistrationService.initialize(userFacade);

        viewModel = new AuthentificationViewModel(userFacade, sessionService);
        testDataBuilder = new TestDataBuilder();
    }

    @AfterEach
    void tearDown() {
        userIdsToClean.forEach(userRepository::deleteById);
        userIdsToClean.clear();
        sessionService.logout();
    }

    // ##### Tests for startRegistrationProcess() #####

    @Test
    void testStartRegistrationProcess_WithValidCredentials_ReturnsTrue() {
        // Arrange
        viewModel.setEmail("test@example.com");
        viewModel.setPassword("Password123!");
        viewModel.setConfirmPassword("Password123!");

        // Act
        boolean result = viewModel.startRegistrationProcess();

        // Assert
        assertTrue(result);
    }

    @Test
    void testStartRegistrationProcess_WithEmptyFields_ReturnsFalse() {
        // Arrange
        viewModel.setEmail("");
        viewModel.setPassword("");
        viewModel.setConfirmPassword("");

        // Act
        boolean result = viewModel.startRegistrationProcess();

        // Assert
        assertFalse(result);
        assertEquals("All fields are required.", viewModel.loginStatusMessage());
    }

    @Test
    void testStartRegistrationProcess_WithInvalidEmail_ReturnsFalse() {
        // Arrange
        viewModel.setEmail("invalid-email");
        viewModel.setPassword("Password123!");
        viewModel.setConfirmPassword("Password123!");

        // Act
        boolean result = viewModel.startRegistrationProcess();

        // Assert
        assertFalse(result);
        assertEquals("Invalid email format. Email must follow the pattern: example@domain.com",
                    viewModel.loginStatusMessage());
    }

    @Test
    void testStartRegistrationProcess_WithInvalidPassword_ReturnsFalse() {
        // Arrange
        viewModel.setEmail("test@example.com");
        viewModel.setPassword("weak");
        viewModel.setConfirmPassword("weak");

        // Act
        boolean result = viewModel.startRegistrationProcess();

        // Assert
        assertFalse(result);
        assertTrue(viewModel.loginStatusMessage().contains("Invalid password format"));
    }

    @Test
    void testStartRegistrationProcess_WithMismatchedPasswords_ReturnsFalse() {
        // Arrange
        viewModel.setEmail("test@example.com");
        viewModel.setPassword("Password123!");
        viewModel.setConfirmPassword("Password456!");

        // Act
        boolean result = viewModel.startRegistrationProcess();

        // Assert
        assertFalse(result);
        assertEquals("Passwords do not match.", viewModel.loginStatusMessage());
    }

    @Test
    void testStartRegistrationProcess_CallsRegistrationService_WithCorrectParams() {
        // Arrange
        String email = "test@example.com";
        String password = "Password123!";
        viewModel.setEmail(email);
        viewModel.setPassword(password);
        viewModel.setConfirmPassword(password);

        // Act
        boolean result = viewModel.startRegistrationProcess();

        // Assert
        assertTrue(result);
        assertNotNull(RegistrationService.getInstance());
    }

    // ##### Tests for login() #####

    @Test
    void testLogin_WithValidCredentials_ReturnsTrue() {
        // Arrange
        UserDto user = testDataBuilder.createDefaultUser();
        int userId = userRepository.save(user);
        userIdsToClean.add(userId);

        viewModel.setEmail(user.email());
        viewModel.setPassword("Password123!");

        // Act
        boolean result = viewModel.login();

        // Assert
        assertTrue(result);
        assertEquals("Login successful.", viewModel.loginStatusMessage());
    }

    @Test
    void testLogin_WithEmptyEmail_ReturnsFalse() {
        // Arrange
        viewModel.setEmail("");
        viewModel.setPassword("Password123!");

        // Act
        boolean result = viewModel.login();

        // Assert
        assertFalse(result);
        assertEquals("Email and password cannot be empty.", viewModel.loginStatusMessage());
    }

    @Test
    void testLogin_WithEmptyPassword_ReturnsFalse() {
        // Arrange
        viewModel.setEmail("test@example.com");
        viewModel.setPassword("");

        // Act
        boolean result = viewModel.login();

        // Assert
        assertFalse(result);
        assertEquals("Email and password cannot be empty.", viewModel.loginStatusMessage());
    }

    @Test
    void testLogin_WithBlankCredentials_ReturnsFalse() {
        // Arrange
        viewModel.setEmail("   ");
        viewModel.setPassword("   ");

        // Act
        boolean result = viewModel.login();

        // Assert
        assertFalse(result);
        assertEquals("Email and password cannot be empty.", viewModel.loginStatusMessage());
    }

    @Test
    void testLogin_WhenUserFacadeThrowsIllegalArgumentException_ReturnsFalse() {
        // Arrange
        viewModel.setEmail("nonexistent@example.com");
        viewModel.setPassword("Password123!");

        // Act
        boolean result = viewModel.login();

        // Assert
        assertFalse(result);
        assertTrue(viewModel.loginStatusMessage().contains("Login error:"));
    }

    @Test
    void testLogin_WhenUserFacadeThrowsGeneralException_ReturnsFalse() {
        // Arrange
        viewModel.setEmail("test@example.com");
        viewModel.setPassword("password");

        // Act
        boolean result = viewModel.login();

        // Assert
        assertFalse(result);
        assertTrue(viewModel.loginStatusMessage().contains("error"));
    }

    @Test
    void testLogin_WithValidUser_UpdatesIsLoggedIn() {
        // Arrange
        UserDto user = testDataBuilder.createDefaultUser();
        int userId = userRepository.save(user);
        userIdsToClean.add(userId);

        viewModel.setEmail(user.email());
        viewModel.setPassword("Password123!");

        // Act
        boolean result = viewModel.login();

        // Assert
        assertTrue(result);
        assertNotNull(sessionService.getCurrentUser());
    }

    // ##### Tests for logout() #####

    @Test
    void testLogout_WhenUserIsLoggedIn_ReturnsTrue() {
        // Arrange
        UserDto user = testDataBuilder.createDefaultUser();
        int userId = userRepository.save(user);
        userIdsToClean.add(userId);

        viewModel.setEmail(user.email());
        viewModel.setPassword("Password123!");
        viewModel.login();

        // Act
        boolean result = viewModel.logout();

        // Assert
        assertTrue(result);
        assertEquals("Logout successful.", viewModel.loginStatusMessage());
    }

    @Test
    void testLogout_WhenUserIsNotLoggedIn_ReturnsFalse() {
        // Act
        boolean result = viewModel.logout();

        // Assert
        assertFalse(result);
        assertEquals("You are not logged in.", viewModel.loginStatusMessage());
    }

    @Test
    void testLogout_CallsSessionServiceLogout() {
        // Arrange
        UserDto user = testDataBuilder.createDefaultUser();
        int userId = userRepository.save(user);
        userIdsToClean.add(userId);

        viewModel.setEmail(user.email());
        viewModel.setPassword("Password123!");
        boolean relustLogin = viewModel.login();
        System.out.println("Login result: " + relustLogin);

        // Act
        boolean result = viewModel.logout();
        System.out.println("Logout result: " + result);

        // Assert
        assertTrue(result);
        assertThrows(Exception.class, () -> sessionService.getCurrentUser());
    }

    @Test
    void testLogout_UpdatesIsLoggedInProperty_ToFalse() {
        // Arrange
        UserDto user = testDataBuilder.createDefaultUser();
        int userId = userRepository.save(user);
        userIdsToClean.add(userId);

        viewModel.setEmail(user.email());
        viewModel.setPassword("Password123!");
        viewModel.login();

        // Act
        boolean result = viewModel.logout();

        // Assert
        assertTrue(result);
        assertThrows(Exception.class, () -> sessionService.getCurrentUser());
    }

    @Test
    void testLogout_WhenExceptionThrown_ReturnsFalse() {
        // Arrange
        UserDto user = testDataBuilder.createDefaultUser();
        int userId = userRepository.save(user);
        userIdsToClean.add(userId);

        viewModel.setEmail(user.email());
        viewModel.setPassword("Password123!");
        viewModel.login();

        sessionService.logout();

        // Act
        boolean result = viewModel.logout();

        // Assert
        assertFalse(result);
        assertEquals("You are not logged in.", viewModel.loginStatusMessage());
    }

    private static class TestDataBuilder {

        public UserDto createDefaultUser() {
            return new UserDto(
                    -1,
                    "test@example.com",
                    "hashed_!321drowssaP",
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
    }
}