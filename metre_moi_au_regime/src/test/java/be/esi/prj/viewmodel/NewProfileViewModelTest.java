package be.esi.prj.viewmodel;

import static org.junit.jupiter.api.Assertions.*;

import be.esi.prj.enumeration.ActivityLevel;
import be.esi.prj.enumeration.Gender;
import be.esi.prj.model.UserFacade;
import be.esi.prj.repository.UserRepository;
import be.esi.prj.service.RegistrationService;
import org.junit.jupiter.api.*;

import java.time.LocalDate;

class NewProfileViewModelTest {

    private UserRepository userRepository;
    private NewProfileViewModel viewModel;
    private UserFacade userFacade;
    private RegistrationService registrationService;
    private TestDataBuilder testDataBuilder;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepository();
        userFacade = new UserFacade(userRepository);
        RegistrationService.initialize(userFacade);
        viewModel = new NewProfileViewModel(userFacade);
        registrationService = RegistrationService.getInstance();
        testDataBuilder = new TestDataBuilder();
    }

    @AfterEach
    void tearDown() {
        try {
            registrationService.reset();
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }

    @Test
    void testConstructorInitializesUserFacade() {
        // Act
        NewProfileViewModel newViewModel = new NewProfileViewModel(userFacade);

        // Assert
        assertNotNull(newViewModel);
    }

    @Test
    void testSaveWithInvalidDataReturnsFalse() {
        // Arrange
        testDataBuilder.setupInvalidProfileData(viewModel);

        // Act
        boolean result = viewModel.save();

        // Assert
        assertFalse(result);
    }

    @Test
    void testSaveWithEmptyNameReturnsFalse() {
        // Arrange
        testDataBuilder.setupValidProfileData(viewModel);
        viewModel.nameProperty().set("");

        // Act
        boolean result = viewModel.save();

        // Assert
        assertFalse(result);
    }

    @Test
    void testSaveWithInvalidAgeReturnsFalse() {
        // Arrange
        testDataBuilder.setupValidProfileData(viewModel);
        viewModel.ageProperty().set(0);

        // Act
        boolean result = viewModel.save();

        // Assert
        assertFalse(result);
    }

    @Test
    void testSaveWithNullGenderReturnsFalse() {
        // Arrange
        testDataBuilder.setupValidProfileData(viewModel);
        viewModel.genderProperty().set(null);

        // Act
        boolean result = viewModel.save();

        // Assert
        assertFalse(result);
    }

    @Test
    void testSaveWithInvalidDatesReturnsFalse() {
        // Arrange
        testDataBuilder.setupValidProfileData(viewModel);
        viewModel.startDateProperty().set(LocalDate.now().plusDays(10));
        viewModel.endDateProperty().set(LocalDate.now());

        // Act
        boolean result = viewModel.save();

        // Assert
        assertFalse(result);
    }

    @Test
    void testSetAndGetProfileProperties() {
        // Act
        viewModel.nameProperty().set("John Doe");
        viewModel.ageProperty().set(25);
        viewModel.genderProperty().set(Gender.MALE);

        // Assert
        assertEquals("John Doe", viewModel.nameProperty().get());
        assertEquals(25, viewModel.ageProperty().get());
        assertEquals(Gender.MALE, viewModel.genderProperty().get());
    }

    @Test
    void testProfileValidationFromParent() {
        // Arrange
        testDataBuilder.setupValidProfileData(viewModel);

        // Act
        boolean isValid = viewModel.areProfileDetailsValid();

        // Assert
        assertTrue(isValid);
    }

    @Test
    void testSaveReturnsFalseWhenValidationFails() {
        // Arrange
        viewModel.nameProperty().set("");

        // Act
        boolean result = viewModel.save();

        // Assert
        assertFalse(result);
    }

    @Test
    void testSaveReturnsFalseAfterExceptionHandling() {
        // Arrange
        testDataBuilder.setupValidProfileData(viewModel);

        // Act & Assert
        assertThrows(Exception.class, () -> viewModel.save());
    }

    @Test
    void testSaveWithInvalidName() {
        // Arrange
        viewModel.setName("");
        viewModel.setAge(25);
        viewModel.setGender(Gender.FEMALE);
        viewModel.setHeight(165.0);
        viewModel.setWeight(60.0);
        viewModel.setGoalWeight(55.0);
        viewModel.setStartDate(LocalDate.now());
        viewModel.setEndDate(LocalDate.now().plusMonths(2));
        viewModel.setActivityLevel(ActivityLevel.SEDENTARY);

        // Act
        boolean result = viewModel.save();

        // Assert
        assertFalse(result);
        assertEquals("Name cannot be empty.", viewModel.getStatusMessage());
    }

    @Test
    void testSaveWithInvalidAge() {
        // Arrange
        viewModel.setName("Marie Martin");
        viewModel.setAge(-5);
        viewModel.setGender(Gender.FEMALE);
        viewModel.setHeight(160.0);
        viewModel.setWeight(55.0);
        viewModel.setGoalWeight(50.0);
        viewModel.setStartDate(LocalDate.now());
        viewModel.setEndDate(LocalDate.now().plusMonths(1));
        viewModel.setActivityLevel(ActivityLevel.ACTIVE);

        // Act
        boolean result = viewModel.save();

        // Assert
        assertFalse(result);
        assertEquals("Age must be a positive number.", viewModel.getStatusMessage());
    }

    @Test
    void testSaveWithInvalidDates() {
        // Arrange
        viewModel.setName("Pierre Durand");
        viewModel.setAge(30);
        viewModel.setGender(Gender.MALE);
        viewModel.setHeight(180.0);
        viewModel.setWeight(80.0);
        viewModel.setGoalWeight(75.0);
        viewModel.setStartDate(LocalDate.now().plusDays(10));
        viewModel.setEndDate(LocalDate.now());
        viewModel.setActivityLevel(ActivityLevel.MODERATE);

        // Act
        boolean result = viewModel.save();

        // Assert
        assertFalse(result);
        assertEquals("Start date must be before end date.", viewModel.getStatusMessage());
    }

    @Test
    void testSaveWithNullGender() {
        // Arrange
        viewModel.setName("Alex Lemoine");
        viewModel.setAge(28);
        viewModel.setGender(null);
        viewModel.setHeight(170.0);
        viewModel.setWeight(65.0);
        viewModel.setGoalWeight(60.0);
        viewModel.setStartDate(LocalDate.now());
        viewModel.setEndDate(LocalDate.now().plusWeeks(8));
        viewModel.setActivityLevel(ActivityLevel.SEDENTARY);

        // Act
        boolean result = viewModel.save();

        // Assert
        assertFalse(result);
        assertEquals("Gender must be selected.", viewModel.getStatusMessage());
    }

    private static class TestDataBuilder {

        public void setupValidProfileData(NewProfileViewModel viewModel) {
            viewModel.nameProperty().set("John Doe");
            viewModel.ageProperty().set(25);
            viewModel.genderProperty().set(Gender.MALE);
            viewModel.heightProperty().set(175.0);
            viewModel.weightProperty().set(70.0);
            viewModel.goalWeightProperty().set(65.0);
            viewModel.startDateProperty().set(LocalDate.now());
            viewModel.endDateProperty().set(LocalDate.now().plusMonths(3));
            viewModel.activityLevelProperty().set(ActivityLevel.MODERATE);
        }

        public void setupInvalidProfileData(NewProfileViewModel viewModel) {
            viewModel.nameProperty().set("");
            viewModel.ageProperty().set(-1);
            viewModel.genderProperty().set(null);
            viewModel.heightProperty().set(0.0);
            viewModel.weightProperty().set(0.0);
            viewModel.goalWeightProperty().set(0.0);
            viewModel.startDateProperty().set(null);
            viewModel.endDateProperty().set(null);
            viewModel.activityLevelProperty().set(null);
        }
    }
}