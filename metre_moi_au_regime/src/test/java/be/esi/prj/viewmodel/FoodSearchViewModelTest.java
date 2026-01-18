package be.esi.prj.viewmodel;

import static org.junit.jupiter.api.Assertions.*;

import be.esi.prj.dto.FoodDto;
import be.esi.prj.dto.UserDto;
import be.esi.prj.enumeration.ActivityLevel;
import be.esi.prj.enumeration.ConsumptionType;
import be.esi.prj.enumeration.Gender;
import be.esi.prj.model.DiaryFacade;
import be.esi.prj.model.FoodFacade;
import be.esi.prj.repository.ActivityRepository;
import be.esi.prj.repository.DiaryRepository;
import be.esi.prj.repository.FoodConsumedRepository;
import be.esi.prj.repository.FoodRepository;
import be.esi.prj.service.SessionService;
import org.junit.jupiter.api.*;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

class FoodSearchViewModelTest {

    private FoodSearchViewModel viewModel;
    private FoodFacade foodFacade;
    private DiaryFacade diaryFacade;
    private DiaryRepository diaryRepository;
    private ActivityRepository activityRepository;
    private FoodRepository foodRepository;
    private FoodConsumedRepository foodConsumedRepository;
    private TestDataBuilder testDataBuilder;

    private final List<Integer> foodIdsToClean = new ArrayList<>();

    @BeforeEach
    void setUp() {
        diaryRepository = new DiaryRepository();
        foodRepository = new FoodRepository();
        activityRepository = new ActivityRepository();
        foodConsumedRepository = new FoodConsumedRepository();

        foodFacade = new FoodFacade(foodRepository);
        diaryFacade = new DiaryFacade(diaryRepository, activityRepository, foodRepository, foodConsumedRepository);
        viewModel = new FoodSearchViewModel(foodFacade, diaryFacade);

        testDataBuilder = new TestDataBuilder();
    }

    @AfterEach
    void tearDown() {
        foodIdsToClean.forEach(foodRepository::deleteById);
        foodIdsToClean.clear();
    }

    // ##### Tests de construction et initialisation #####

    @Test
    void testConstructor() {
        // Act
        FoodSearchViewModel newViewModel = new FoodSearchViewModel(foodFacade, diaryFacade);

        // Assert
        assertNotNull(newViewModel);
        assertNotNull(newViewModel.getSearchResults());
        assertTrue(newViewModel.getSearchResults().isEmpty());
        assertEquals("", newViewModel.getSearchStatusMessage());
    }

    // ##### Tests de la méthode scanFood() #####

    @Test
    void testScanFoodWithNullImageFile() {
        // Arrange
        viewModel.setImageFile(null);

        // Act
        viewModel.scanFood();

        // Assert
        assertEquals("Please select an image file to scan.", viewModel.getSearchStatusMessage());
        assertTrue(viewModel.getSearchResults().isEmpty());
    }

    @Test
    void testScanFoodSuccess() {
        // Arrange
        File testFile = testDataBuilder.createTestImageFile();
        viewModel.setImageFile(testFile);

        // Act
        viewModel.scanFood();

        // Assert
        assertFalse(viewModel.getSearchResults().isEmpty());
        assertTrue(viewModel.getSearchStatusMessage().contains("Food found:"));
    }

    @Test
    void testScanFoodNoFoodFound() {
        // Arrange
        File invalidFile = testDataBuilder.createImageWithInvalidEanCode();
        viewModel.setImageFile(invalidFile);

        // Act
        viewModel.scanFood();

        // Assert
        assertTrue(viewModel.getSearchStatusMessage().contains("Error while scanning food"));
    }

    @Test
    void testScanFoodException() {
        // Arrange
        File nonExistentFile = new File("non_existent_file.jpg");
        viewModel.setImageFile(nonExistentFile);

        // Act
        viewModel.scanFood();

        // Assert
        assertTrue(viewModel.getSearchStatusMessage().contains("Error while scanning food:"));
    }

    // ##### Tests de la méthode searchFood() #####

    @Test
    void testSearchFoodWithNullQuery() {
        // Arrange
        viewModel.setSearchQuery(null);

        // Act
        viewModel.searchFood();

        // Assert
        assertTrue(viewModel.getSearchResults().isEmpty());
    }

    @Test
    void testSearchFoodWithBlankQuery() {
        // Arrange
        viewModel.setSearchQuery("   ");

        // Act
        viewModel.searchFood();

        // Assert
        assertTrue(viewModel.getSearchResults().isEmpty());
    }

    @Test
    void testSearchFoodWithValidEanCode8Digits() {
        // Arrange
        FoodDto food = testDataBuilder.createFoodWithEanCode8();
        int foodId = foodRepository.save(food);
        foodIdsToClean.add(foodId);
        viewModel.setSearchQuery(food.ean_code());

        // Act
        viewModel.searchFood();

        // Assert
        assertFalse(viewModel.getSearchResults().isEmpty());
    }

    @Test
    void testSearchFoodWithValidEanCode13Digits() {
        // Arrange
        viewModel.setSearchQuery("3017620422003");

        // Act
        viewModel.searchFood();

        // Assert
        assertFalse(viewModel.getSearchResults().isEmpty());
    }

    @Test
    void testSearchFoodWithInvalidEanCode() {
        // Arrange
        viewModel.setSearchQuery("apple");

        // Act
        viewModel.searchFood();

        // Assert
        assertFalse(viewModel.getSearchResults().isEmpty());
    }

    // ##### Tests de la méthode addFoodToDiary() #####

    @Test
    void testAddFoodToDiaryWithNullSelectedFood() {
        // Arrange
        viewModel.setSelectedFood(null);
        viewModel.setQuantity(100.0);
        viewModel.setType(ConsumptionType.PER_QUANTITY);

        // Act
        viewModel.addFoodToDiary();

        // Assert
        assertEquals("Please select a food and enter a valid value and type.",
                viewModel.getSearchStatusMessage());
    }

    @Test
    void testAddFoodToDiaryWithZeroQuantity() {
        // Arrange
        FoodDto food = testDataBuilder.createDefaultFood();
        FoodViewModel foodViewModel = new FoodViewModel(food);
        viewModel.setSelectedFood(foodViewModel);
        viewModel.setQuantity(0.0);
        viewModel.setType(ConsumptionType.PER_QUANTITY);

        // Act
        viewModel.addFoodToDiary();

        // Assert
        assertEquals("Please select a food and enter a valid value and type.",
                viewModel.getSearchStatusMessage());
    }

    @Test
    void testAddFoodToDiaryWithNullType() {
        // Arrange
        FoodDto food = testDataBuilder.createDefaultFood();
        FoodViewModel foodViewModel = new FoodViewModel(food);
        viewModel.setSelectedFood(foodViewModel);
        viewModel.setQuantity(100.0);
        viewModel.setType(null);

        // Act
        viewModel.addFoodToDiary();

        // Assert
        assertEquals("Please select a food and enter a valid value and type.",
                viewModel.getSearchStatusMessage());
    }

    @Test
    void testAddFoodToDiarySuccess() {
        // Arrange
        SessionService sessionService = SessionService.getInstance();
        UserDto user = testDataBuilder.createDefaultUser();
        sessionService.login(user);

        FoodDto food = testDataBuilder.createDefaultFood();
        int foodId = foodRepository.save(food);
        foodIdsToClean.add(foodId);

        FoodViewModel foodViewModel = new FoodViewModel(food);
        viewModel.setSelectedFood(foodViewModel);
        viewModel.setQuantity(100.0);
        viewModel.setType(ConsumptionType.PER_QUANTITY);

        // Act
        viewModel.addFoodToDiary();

        // Assert
        assertEquals("Food added to diary successfully.", viewModel.getSearchStatusMessage());

        // Cleanup
        sessionService.logout();
    }

    @Test
    void testAddFoodToDiaryException() {
        // Arrange
        FoodDto invalidFood = new FoodDto(-1, "Invalid", "123", "g", 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
        FoodViewModel foodViewModel = new FoodViewModel(invalidFood);
        viewModel.setSelectedFood(foodViewModel);
        viewModel.setQuantity(100.0);
        viewModel.setType(ConsumptionType.PER_QUANTITY);

        // Act
        viewModel.addFoodToDiary();

        // Assert
        assertTrue(viewModel.getSearchStatusMessage().contains("Error while adding food to diary:"));
    }

    private static class TestDataBuilder {

        public File createTestImageFile() {
            String path = "src/main/resources/dataForUnitTests/test-barcode.jpg";
            return new File(path);
        }

        public File createImageWithInvalidEanCode() {
            String path = "src/main/resources/dataForUnitTests/test-noBarcode.jpg";
            return new File(path);
        }

        public FoodDto createDefaultFood() {
            return new FoodDto(
                    -1,
                    "Test Apple",
                    "1245136741259",
                    "g",
                    52.0,
                    0.3,
                    0.2,
                    14.0,
                    150.0,
                    100.0
            );
        }

        public FoodDto createFoodWithEanCode8() {
            return new FoodDto(
                    -1,
                    "Test Product",
                    "12345678",
                    "g",
                    100.0,
                    1.0,
                    1.0,
                    20.0,
                    200.0,
                    100.0
            );
        }

        public UserDto createDefaultUser() {
            return new UserDto(
                    -1,
                    "test@example.com",
                    "hashed_321drowssaP",
                    "Test User",
                    25,
                    Gender.MALE,
                    170.0,
                    70.0,
                    80.0,
                    LocalDate.now(),
                    LocalDate.now().plusMonths(3),
                    ActivityLevel.MODERATE
            );
        }
    }
}