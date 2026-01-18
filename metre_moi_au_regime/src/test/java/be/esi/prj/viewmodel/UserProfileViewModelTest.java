package be.esi.prj.viewmodel;

import static org.junit.jupiter.api.Assertions.*;

import be.esi.prj.dto.UserDto;
import be.esi.prj.enumeration.ActivityLevel;
import be.esi.prj.enumeration.Gender;
import be.esi.prj.model.UserFacade;
import be.esi.prj.repository.UserRepository;
import be.esi.prj.service.SessionService;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.Optional;

class UserProfileViewModelTest {

    private UserRepository userRepository;
    private UserProfileViewModel viewModel;
    private UserFacade userFacade;
    private SessionService sessionService;
    private TestDataBuilder testDataBuilder;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepository();
        userFacade = new UserFacade(userRepository);
        viewModel = new UserProfileViewModel(userFacade);
        sessionService = SessionService.getInstance();
        testDataBuilder = new TestDataBuilder();
    }

    @AfterEach
    void tearDown() {
        try {
            sessionService.logout();
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }

    @Test
    void testConstructorInitialization() {
        // Act
        UserProfileViewModel newViewModel = new UserProfileViewModel(userFacade);

        // Assert
        assertNotNull(newViewModel);
        assertNotNull(newViewModel.nameProperty());
        assertNotNull(newViewModel.ageProperty());
        assertNotNull(newViewModel.genderProperty());
    }

    @Test
    void testLoadProfileWhenUserLoggedIn() {
        // Arrange
        UserDto testUser = testDataBuilder.createDefaultUser();
        sessionService.login(testUser);

        // Act
        viewModel.loadProfile();

        // Assert
        assertEquals("John Doe", viewModel.getName());
        assertEquals(25, viewModel.getAge());
        assertEquals(Gender.MALE, viewModel.getGender());
    }

    @Test
    void testLoadProfileWhenNoUserLoggedIn() {
        // Act
        viewModel.loadProfile();

        // Assert
        assertEquals("No user is logged in. Cannot load profile.", viewModel.getStatusMessage());
    }

    @Test
    void testSaveWithValidProfile() {
        // Arrange
        UserDto testUser = testDataBuilder.createDefaultUser();
        sessionService.login(testUser);
        viewModel.loadProfile();

        // Act
        boolean result = viewModel.save();

        // Assert
        assertTrue(result);
        assertEquals("Profile updated successfully!", viewModel.getStatusMessage());

        // Cleanup
        Optional<UserDto> userOpt = userRepository.findByEmail(testUser.email());
        userRepository.deleteById(userOpt.get().userId());
    }

    @Test
    void testSaveWithNullCurrentUserId() {
        // Arrange
        testDataBuilder.setupValidProfileData(viewModel);

        // Act
        boolean result = viewModel.save();

        // Assert
        assertFalse(result);
        assertEquals("Cannot save profile. No user loaded.", viewModel.getStatusMessage());
    }

    @Test
    void testSaveWithEmptyName() {
        // Arrange
        UserDto testUser = testDataBuilder.createDefaultUser();
        sessionService.login(testUser);
        viewModel.loadProfile();
        viewModel.setName("");

        // Act
        boolean result = viewModel.save();

        // Assert
        assertFalse(result);
        assertEquals("Name cannot be empty.", viewModel.getStatusMessage());
    }

    @Test
    void testStatusMessageAfterSuccessfulSave() {
        // Arrange
        UserDto testUser = testDataBuilder.createDefaultUser();
        sessionService.login(testUser);
        viewModel.loadProfile();

        // Act
        viewModel.save();

        // Assert
        assertEquals("Profile updated successfully!", viewModel.getStatusMessage());

        // Cleanup
        Optional<UserDto> userOpt = userRepository.findByEmail(testUser.email());
        userRepository.deleteById(userOpt.get().userId());
    }

    @Test
    void testSetAndGetName() {
        // Act
        viewModel.setName("Jane Doe");

        // Assert
        assertEquals("Jane Doe", viewModel.getName());
        assertEquals("Jane Doe", viewModel.nameProperty().get());
    }

    @Test
    void testCurrentUserIdSetAfterLoadProfile() {
        // Arrange
        UserDto testUser = testDataBuilder.createDefaultUser();
        sessionService.login(testUser);

        // Act
        viewModel.loadProfile();

        // Assert
        assertTrue(viewModel.save());

        // Cleanup
        Optional<UserDto> userOpt = userRepository.findByEmail(testUser.email());
        userRepository.deleteById(userOpt.get().userId());
    }

    private static class TestDataBuilder {

        public UserDto createDefaultUser() {
            return new UserDto(
                    1,
                    "john@example.com",
                    "password123",
                    "John Doe",
                    25,
                    Gender.MALE,
                    175.0,
                    70.0,
                    65.0,
                    LocalDate.now(),
                    LocalDate.now().plusMonths(6),
                    ActivityLevel.MODERATE
            );
        }

        public void setupValidProfileData(UserProfileViewModel viewModel) {
            viewModel.setName("John Doe");
            viewModel.setAge(25);
            viewModel.setGender(Gender.MALE);
            viewModel.setHeight(175.0);
            viewModel.setWeight(70.0);
            viewModel.setGoalWeight(65.0);
            viewModel.setStartDate(LocalDate.now());
            viewModel.setEndDate(LocalDate.now().plusMonths(6));
            viewModel.setActivityLevel(ActivityLevel.MODERATE);
        }
    }
}