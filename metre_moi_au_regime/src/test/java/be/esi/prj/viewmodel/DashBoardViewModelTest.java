package be.esi.prj.viewmodel;

import be.esi.prj.model.DiaryFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashBoardViewModelTest {

    @Mock
    private DiaryFacade diaryFacade;

    @InjectMocks
    private DashBoardViewModel viewModel;

    private Map<String, Map<String, Double>> validProgressionData;

    @BeforeEach
    void setUp() {
        validProgressionData = createValidProgressionData();
    }

    @Test
    void testConstructorInitialization() {
        assertNotNull(viewModel.getNutrientGoals());
        assertTrue(viewModel.getNutrientGoals().isEmpty());
    }

    @Test
    void testLoadNutrientGoalsSuccess() {
        // Arrange
        when(diaryFacade.getProgressOfNutritionalValues()).thenReturn(validProgressionData);

        // Act
        viewModel.loadNutrientGoals();

        // Assert
        assertEquals(4, viewModel.getNutrientGoals().size());
        assertEquals("Calories", viewModel.getNutrientGoals().get(0).getNutrientName());
        assertEquals("Proteins", viewModel.getNutrientGoals().get(1).getNutrientName());
        assertEquals("Carbohydrates", viewModel.getNutrientGoals().get(2).getNutrientName());
        assertEquals("Fats", viewModel.getNutrientGoals().get(3).getNutrientName());
    }

    @Test
    void testLoadNutrientGoalsDataMapping() {
        // Arrange
        when(diaryFacade.getProgressOfNutritionalValues()).thenReturn(validProgressionData);

        // Act
        viewModel.loadNutrientGoals();

        // Assert
        NutrientGoalViewModel calorieGoal = viewModel.getNutrientGoals().get(0);
        assertEquals("1500 kcal", calorieGoal.getConsumedValue());
        assertEquals("2000 kcal", calorieGoal.getGoalValue());
        assertEquals("75 %", calorieGoal.getProgessPercentage());
        assertEquals(270.0, calorieGoal.getGaugeAngle());

        NutrientGoalViewModel proteinGoal = viewModel.getNutrientGoals().get(1);
        assertTrue(proteinGoal.getConsumedValue().contains("g"));
        assertTrue(proteinGoal.getGoalValue().contains("g"));
    }

    @Test
    void testLoadNutrientGoalsWithException() {
        // Arrange
        RuntimeException exception = new RuntimeException("Database error");
        when(diaryFacade.getProgressOfNutritionalValues()).thenThrow(exception);

        // Act
        viewModel.loadNutrientGoals();

        // Assert
        assertTrue(viewModel.getNutrientGoals().isEmpty());
    }

    @Test
    void testLoadNutrientGoalsWithNullProgression() {
        // Arrange
        when(diaryFacade.getProgressOfNutritionalValues()).thenReturn(null);

        // Act & Assert
        assertDoesNotThrow(() -> viewModel.loadNutrientGoals());
    }

    @Test
    void testLoadNutrientGoalsWithMissingData() {
        // Arrange
        Map<String, Map<String, Double>> partialData = createValidProgressionData();
        partialData.put("proteins", new HashMap<>());
        when(diaryFacade.getProgressOfNutritionalValues()).thenReturn(partialData);

        // Act
        viewModel.loadNutrientGoals();

        // Assert
        assertEquals(4, viewModel.getNutrientGoals().size());
        NutrientGoalViewModel proteinGoal = viewModel.getNutrientGoals().get(1);
        assertEquals("0 g", proteinGoal.getConsumedValue());
        assertEquals("0 g", proteinGoal.getGoalValue());
    }

    @Test
    void testGetNutrientGoals() {
        // Arrange
        var initialList = viewModel.getNutrientGoals();

        // Act
        when(diaryFacade.getProgressOfNutritionalValues()).thenReturn(validProgressionData);
        viewModel.loadNutrientGoals();
        var afterLoadList = viewModel.getNutrientGoals();

        // Assert
        assertSame(initialList, afterLoadList);
        assertNotNull(afterLoadList);
    }

    @Test
    void testLoadNutrientGoalsMultipleCalls() {
        // Arrange
        when(diaryFacade.getProgressOfNutritionalValues()).thenReturn(validProgressionData);
        viewModel.loadNutrientGoals();
        assertEquals(4, viewModel.getNutrientGoals().size());

        // Act
        viewModel.loadNutrientGoals();

        // Assert
        assertEquals(4, viewModel.getNutrientGoals().size());
        verify(diaryFacade, times(2)).getProgressOfNutritionalValues();
    }

    @Test
    void testNutrientGoalViewModelCreation() {
        // Arrange
        when(diaryFacade.getProgressOfNutritionalValues()).thenReturn(validProgressionData);

        // Act
        viewModel.loadNutrientGoals();

        // Assert
        assertEquals("Calories", viewModel.getNutrientGoals().get(0).getNutrientName());
        assertTrue(viewModel.getNutrientGoals().get(0).getConsumedValue().contains("kcal"));

        assertEquals("Proteins", viewModel.getNutrientGoals().get(1).getNutrientName());
        assertTrue(viewModel.getNutrientGoals().get(1).getConsumedValue().contains("g"));

        assertEquals("Carbohydrates", viewModel.getNutrientGoals().get(2).getNutrientName());
        assertTrue(viewModel.getNutrientGoals().get(2).getConsumedValue().contains("g"));

        assertEquals("Fats", viewModel.getNutrientGoals().get(3).getNutrientName());
        assertTrue(viewModel.getNutrientGoals().get(3).getConsumedValue().contains("g"));
    }

    @Test
    void testLoadNutrientGoalsWithPartialDetails() {
        // Arrange
        Map<String, Map<String, Double>> partialDetailsData = createValidProgressionData();
        partialDetailsData.get("calories").remove("percentage");
        when(diaryFacade.getProgressOfNutritionalValues()).thenReturn(partialDetailsData);

        // Act
        viewModel.loadNutrientGoals();

        // Assert
        NutrientGoalViewModel calorieGoal = viewModel.getNutrientGoals().get(0);
        assertEquals("0 %", calorieGoal.getProgessPercentage());
    }

    @Test
    void testLoadNutrientGoalsClearsListFirst() {
        // Arrange
        when(diaryFacade.getProgressOfNutritionalValues()).thenReturn(validProgressionData);
        viewModel.getNutrientGoals().add(new NutrientGoalViewModel("Test", "unit"));
        assertEquals(1, viewModel.getNutrientGoals().size());

        // Act
        viewModel.loadNutrientGoals();

        // Assert
        assertEquals(4, viewModel.getNutrientGoals().size());
        assertEquals("Calories", viewModel.getNutrientGoals().get(0).getNutrientName());
    }

    @Test
    void testLoadNutrientGoalsWithEmptyDetails() {
        // Arrange
        Map<String, Map<String, Double>> emptyDetailsData = new HashMap<>();
        emptyDetailsData.put("calories", new HashMap<>());
        emptyDetailsData.put("proteins", new HashMap<>());
        emptyDetailsData.put("carbohydrates", new HashMap<>());
        emptyDetailsData.put("fats", new HashMap<>());
        when(diaryFacade.getProgressOfNutritionalValues()).thenReturn(emptyDetailsData);

        // Act
        viewModel.loadNutrientGoals();

        // Assert
        assertEquals(4, viewModel.getNutrientGoals().size());
        NutrientGoalViewModel calorieGoal = viewModel.getNutrientGoals().get(0);
        assertEquals("0 kcal", calorieGoal.getConsumedValue());
        assertEquals("0 kcal", calorieGoal.getGoalValue());
    }

    @Test
    void testLoadNutrientGoalsCorrectOrder() {
        // Arrange
        when(diaryFacade.getProgressOfNutritionalValues()).thenReturn(validProgressionData);

        // Act
        viewModel.loadNutrientGoals();

        // Assert
        assertEquals("Calories", viewModel.getNutrientGoals().get(0).getNutrientName());
        assertEquals("Proteins", viewModel.getNutrientGoals().get(1).getNutrientName());
        assertEquals("Carbohydrates", viewModel.getNutrientGoals().get(2).getNutrientName());
        assertEquals("Fats", viewModel.getNutrientGoals().get(3).getNutrientName());
    }

    @Test
    void testLoadNutrientGoalsVerifyUnits() {
        // Arrange
        when(diaryFacade.getProgressOfNutritionalValues()).thenReturn(validProgressionData);

        // Act
        viewModel.loadNutrientGoals();

        // Assert
        assertTrue(viewModel.getNutrientGoals().get(0).getConsumedValue().endsWith("kcal"));
        assertTrue(viewModel.getNutrientGoals().get(1).getConsumedValue().endsWith("g"));
        assertTrue(viewModel.getNutrientGoals().get(2).getConsumedValue().endsWith("g"));
        assertTrue(viewModel.getNutrientGoals().get(3).getConsumedValue().endsWith("g"));
    }

    private Map<String, Map<String, Double>> createValidProgressionData() {
        Map<String, Map<String, Double>> data = new HashMap<>();

        Map<String, Double> calories = new HashMap<>();
        calories.put("consumedValue", 1500.0);
        calories.put("goalValue", 2000.0);
        calories.put("percentage", 75.0);
        calories.put("gaugeAngle", 270.0);
        data.put("calories", calories);

        Map<String, Double> proteins = new HashMap<>();
        proteins.put("consumedValue", 60.0);
        proteins.put("goalValue", 100.0);
        proteins.put("percentage", 60.0);
        proteins.put("gaugeAngle", 216.0);
        data.put("proteins", proteins);

        Map<String, Double> carbs = new HashMap<>();
        carbs.put("consumedValue", 200.0);
        carbs.put("goalValue", 250.0);
        carbs.put("percentage", 80.0);
        carbs.put("gaugeAngle", 288.0);
        data.put("carbohydrates", carbs);

        Map<String, Double> fats = new HashMap<>();
        fats.put("consumedValue", 40.0);
        fats.put("goalValue", 70.0);
        fats.put("percentage", 57.0);
        fats.put("gaugeAngle", 205.0);
        data.put("fats", fats);

        return data;
    }
}