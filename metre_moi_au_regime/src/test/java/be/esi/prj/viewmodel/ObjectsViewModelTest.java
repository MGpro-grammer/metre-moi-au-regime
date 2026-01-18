package be.esi.prj.viewmodel;

            import be.esi.prj.dto.ActivityDto;
            import be.esi.prj.dto.FoodConsumedDto;
            import be.esi.prj.dto.FoodDto;
            import be.esi.prj.enumeration.ConsumptionType;
            import org.junit.jupiter.api.DisplayName;
            import org.junit.jupiter.api.Nested;
            import org.junit.jupiter.api.Test;

            import java.util.Map;

            import static org.junit.jupiter.api.Assertions.*;

            class ObjectsViewModelTest {

                @Nested
                @DisplayName("ActivityViewModel Tests")
                class ActivityViewModelTest {

                    @Test
                    void testToStringWithTitle() {
                        // Arrange
                        ActivityDto activityDto = new ActivityDto(1, "Morning Run", 250.5, 1);
                        ActivityViewModel viewModel = new ActivityViewModel(activityDto);

                        // Act
                        String result = viewModel.toString();

                        // Assert
                        assertEquals("Morning Run : 250.5 calories burned", result);
                    }

                    @Test
                    void testToStringWithEmptyTitle() {
                        // Arrange
                        ActivityDto activityDto = new ActivityDto(1, "   ", 150.0, 1);
                        ActivityViewModel viewModel = new ActivityViewModel(activityDto);

                        // Act
                        String result = viewModel.toString();

                        // Assert
                        assertEquals("An activity : 150.0 calories burned", result);
                    }

                    @Test
                    void testGetters() {
                        // Arrange
                        ActivityDto activityDto = new ActivityDto(1, "Cycling", 300.0, 1);
                        ActivityViewModel viewModel = new ActivityViewModel(activityDto);

                        // Assert
                        assertEquals("Cycling", viewModel.getTitle());
                        assertEquals(300.0, viewModel.getCaloriesBurned());
                    }
                }

                @Nested
                @DisplayName("FoodViewModel Tests")
                class FoodViewModelTest {

                    @Test
                    void testToStringWithName() {
                        // Arrange
                        FoodDto foodDto = new FoodDto(1, "Apple", "12345", "g", 52.0, 0.3, 0.2, 14.0, 100.0, 150.0);
                        FoodViewModel viewModel = new FoodViewModel(foodDto);

                        // Act
                        String result = viewModel.toString();

                        // Assert
                        assertEquals("Apple : 150.0g", result);
                    }

                    @Test
                    void testToStringWithEmptyName() {
                        // Arrange
                        FoodDto foodDto = new FoodDto(1, "", "12345", "g", 52.0, 0.3, 0.2, 14.0, 100.0, 150.0);
                        FoodViewModel viewModel = new FoodViewModel(foodDto);

                        // Act
                        String result = viewModel.toString();

                        // Assert
                        assertEquals("Unknown food : 150.0g", result);
                    }

                    @Test
                    void testGetName() {
                        // Arrange
                        FoodDto foodDto = new FoodDto(1, "Banana", "54321", "g", 89.0, 1.1, 0.3, 23.0, 120.0, 180.0);
                        FoodViewModel viewModel = new FoodViewModel(foodDto);

                        // Assert
                        assertEquals("Banana", viewModel.getName());
                    }
                }

                @Nested
                @DisplayName("FoodConsumedViewModel Tests")
                class FoodConsumedViewModelTest {

                    private final FoodDto foodDto = new FoodDto(1, "Chicken Breast", "54321", "g", 165.0, 31.0, 3.6, 0.0, 100.0, 0.0);
                    private final Map<String, Double> nutritionalValues = Map.of(
                            "calories", 165.0,
                            "proteins", 31.0,
                            "carbohydrates", 0.0,
                            "fats", 3.6
                    );

                    @Test
                    void testGetters() {
                        // Arrange
                        FoodConsumedDto consumedDto = new FoodConsumedDto(1, 1, 1, ConsumptionType.PER_QUANTITY, 1.0);
                        FoodConsumedViewModel viewModel = new FoodConsumedViewModel(consumedDto, foodDto, nutritionalValues);

                        // Assert
                        assertEquals("Chicken Breast", viewModel.getName());
                        assertEquals(1.0, viewModel.getQuantity());
                        assertEquals("165,00kcal, 31,00g proteins, 0,00g carbohydrates, 3,60g fats", viewModel.getNutritionalValues());
                    }

                    @Test
                    void testToStringPerQuantity() {
                        // Arrange
                        FoodConsumedDto consumedDto = new FoodConsumedDto(1, 1, 1, ConsumptionType.PER_QUANTITY, 1.0);
                        FoodConsumedViewModel viewModel = new FoodConsumedViewModel(consumedDto, foodDto, nutritionalValues);

                        // Act
                        String result = viewModel.toString();

                        // Assert
                        assertTrue(result.contains("consumed at 1,00 grams"));
                    }

                    @Test
                    void testToStringPerPercent() {
                        // Arrange
                        FoodConsumedDto consumedDto = new FoodConsumedDto(1, 1, 1, ConsumptionType.PER_PERCENT, 50.0);
                        FoodConsumedViewModel viewModel = new FoodConsumedViewModel(consumedDto, foodDto, nutritionalValues);

                        // Act
                        String result = viewModel.toString();

                        // Assert
                        assertTrue(result.contains("consumed at 50,00%"));
                    }

                    @Test
                    void testToStringPerServing() {
                        // Arrange
                        FoodConsumedDto consumedDto = new FoodConsumedDto(1, 1, 1, ConsumptionType.PER_SERVING, 2.0);
                        FoodConsumedViewModel viewModel = new FoodConsumedViewModel(consumedDto, foodDto, nutritionalValues);

                        // Act
                        String result = viewModel.toString();

                        // Assert
                        assertTrue(result.contains("consumed at 2,00 servings"));
                    }

                    @Test
                    void testToStringPerUnit() {
                        // Arrange
                        FoodConsumedDto consumedDto = new FoodConsumedDto(1, 1, 1, ConsumptionType.PER_UNIT, 3.0);
                        FoodConsumedViewModel viewModel = new FoodConsumedViewModel(consumedDto, foodDto, nutritionalValues);

                        // Act
                        String result = viewModel.toString();

                        // Assert
                        assertTrue(result.contains("consumed at 3,00 units"));
                    }
                }
            }