package be.esi.prj.model;

import static org.junit.jupiter.api.Assertions.*;

import be.esi.prj.dto.FoodConsumedDto;
import be.esi.prj.dto.FoodDto;
import be.esi.prj.enumeration.ConsumptionType;
import be.esi.prj.repository.FoodRepository;
import org.junit.jupiter.api.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class FoodFacadeTest {

    private FoodFacade foodFacade;
    private FoodRepository foodRepository;
    private TestDataBuilder testDataBuilder;

    private final List<Integer> foodIdsToClean = new ArrayList<>();

    @BeforeEach
    void setUp() {
        foodRepository = new FoodRepository();
        foodFacade = new FoodFacade(foodRepository);
        testDataBuilder = new TestDataBuilder();
    }

    @AfterEach
    void tearDown() {
        foodIdsToClean.forEach(foodRepository::deleteById);
        foodIdsToClean.clear();
    }

    @Test
    void testFindFoodByEanCode_ExistsInRepository_ReturnsFood() {
        // Arrange
        FoodDto food = testDataBuilder.createDefaultFood();
        int foodId = foodRepository.save(food);
        foodIdsToClean.add(foodId);

        // Act
        FoodDto result = foodFacade.findFoodByEanCode(food.ean_code());

        // Assert
        assertNotNull(result);
        assertEquals("Test Apple", result.name());
    }

    @Test
    void testFindFoodByEanCode_ExistsInOpenFoodFacts_ReturnsFood() {
        // Act & Assert
        assertDoesNotThrow(() -> foodFacade.findFoodByEanCode("3017620422003"));
    }

    @Test
    void testFindFoodByEanCode_NotFoundAnywhere_ThrowsIllegalArgumentException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            foodFacade.findFoodByEanCode("1414141414141"));
    }

    @Test
    void testFindFoodByEanCode_NullCode_ThrowsException() {
        // Act & Assert
        assertThrows(Exception.class, () -> foodFacade.findFoodByEanCode(null));
    }

    @Test
    void testFindFoodByEanCode_EmptyCode_ThrowsException() {
        // Act & Assert
        assertThrows(Exception.class, () -> foodFacade.findFoodByEanCode(""));
    }

    @Test
    void testFindFoodByImage_ValidImage_ReturnsFood() {
        // Arrange
        File imageFile = testDataBuilder.createTestImageFile();

        // Act & Assert
        assertDoesNotThrow(() -> foodFacade.findFoodByImage(imageFile));
    }

    @Test
    void testFindFoodByImage_OcrServiceThrowsException_ThrowsRuntimeException() {
        // Arrange
        File invalidFile = new File("invalid_path.jpg");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> foodFacade.findFoodByImage(invalidFile));
    }

    @Test
    void testFindFoodByImage_NullFile_ThrowsRuntimeException() {
        // Act & Assert
        assertThrows(RuntimeException.class, () -> foodFacade.findFoodByImage(null));
    }

    @Test
    void testFindFoodByImage_InvalidImageFile_ThrowsRuntimeException() {
        // Arrange
        File textFile = new File("test.txt");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> foodFacade.findFoodByImage(textFile));
    }

    @Test
    void testFindFoodByImage_EanCodeNotFound_ThrowsRuntimeException() {
        // Arrange
        File imageWithInvalidCode = testDataBuilder.createImageWithInvalidEanCode();

        // Act & Assert
        assertThrows(RuntimeException.class, () -> foodFacade.findFoodByImage(imageWithInvalidCode));
    }

    @Test
    void testFindFoodByNameNoSync_ValidName_ReturnsAllPages() {
        // Act
        List<FoodDto> result = foodFacade.findFoodByNameNoSync("apple");

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testFindFoodByNameNoSync_EmptyName() {
        // Act
        List<FoodDto> result = foodFacade.findFoodByNameNoSync("");

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testFindFoodByName_ValidName_ReturnsThreadedResults() {
        // Act
        List<FoodDto> result = foodFacade.findFoodByName("apple");

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testFindFoodByName_ThreadInterrupted_ThrowsRuntimeException() {
        // Arrange
        Thread.currentThread().interrupt();

        // Act & Assert
        assertThrows(RuntimeException.class, () -> foodFacade.findFoodByName("apple"));
    }

    @Test
    void testFindFoodByName_SomeThreadsFail_ReturnsPartialResults() {
        // Act
        List<FoodDto> result = foodFacade.findFoodByName("test");

        // Assert
        assertNotNull(result);
    }

    @Test
    void testFindFoodByName_EmptyName() {
        // Act
        List<FoodDto> result = foodFacade.findFoodByName("");

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testCalculateNutritionalValues_ValidFoodConsumedDto_ReturnsNutritionalMap() {
        // Arrange
        FoodDto food = testDataBuilder.createDefaultFood();
        int foodId = foodRepository.save(food);
        foodIdsToClean.add(foodId);

        FoodConsumedDto foodConsumedDto = new FoodConsumedDto(-1, 1, foodId,
            ConsumptionType.PER_QUANTITY, 100.0);

        // Act
        Map<String, Double> result = foodFacade.calculateNutritionalValues(foodConsumedDto);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testCalculateNutritionalValues_InvalidFoodConsumedDto_ThrowsException() {
        // Arrange
        FoodConsumedDto invalidDto = new FoodConsumedDto(-1, 1, -999,
            ConsumptionType.PER_QUANTITY, 100.0);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            foodFacade.calculateNutritionalValues(invalidDto));
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
    }
}
