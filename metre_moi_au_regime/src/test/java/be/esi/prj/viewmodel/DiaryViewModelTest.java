package be.esi.prj.viewmodel;

import be.esi.prj.dto.ActivityDto;
import be.esi.prj.dto.FoodConsumedDto;
import be.esi.prj.dto.FoodDto;
import be.esi.prj.enumeration.ConsumptionType;
import be.esi.prj.model.DiaryFacade;
import be.esi.prj.model.FoodFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DiaryViewModelTest {

    @Mock
    private DiaryFacade diaryFacade;

    @Mock
    private FoodFacade foodFacade;

    @InjectMocks
    private DiaryViewModel diaryViewModel;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConstructorInitializesFields() {
        assertNotNull(diaryViewModel.getFoods(), "Foods list should be initialized.");
        assertTrue(diaryViewModel.getFoods().isEmpty(), "Foods list should be empty initially.");
        assertNotNull(diaryViewModel.getActivities(), "Activities list should be initialized.");
        assertTrue(diaryViewModel.getActivities().isEmpty(), "Activities list should be empty initially.");
        assertEquals("", diaryViewModel.getDiaryStatusMessage(), "Status message should be empty initially.");
    }

    @Test
    void testLoadListsCallsBothLoadMethods() {
        diaryViewModel.loadLists();

        verify(diaryFacade, times(1)).getAllFoodsToday();
        verify(diaryFacade, times(1)).getAllActivitiesToday();
    }

    @Test
    void testAddActivityWithValidData() {
        diaryViewModel.setTitle("Running");
        diaryViewModel.setCaloriesBurned(300.0);

        diaryViewModel.addActivity();

        verify(diaryFacade, times(1)).addActivityToDiaryToday(any(ActivityDto.class));
        assertEquals("Activity added successfully.", diaryViewModel.getDiaryStatusMessage());
    }

    @Test
    void testAddActivityWithNullTitle() {
        diaryViewModel.setTitle(null);
        diaryViewModel.setCaloriesBurned(300.0);

        diaryViewModel.addActivity();

        verify(diaryFacade, never()).addActivityToDiaryToday(any());
        assertEquals("Title cannot be empty.", diaryViewModel.getDiaryStatusMessage());
    }

    @Test
    void testAddActivityWithBlankTitle() {
        diaryViewModel.setTitle("   ");
        diaryViewModel.setCaloriesBurned(300.0);

        diaryViewModel.addActivity();

        verify(diaryFacade, never()).addActivityToDiaryToday(any());
        assertEquals("Title cannot be empty.", diaryViewModel.getDiaryStatusMessage());
    }

    @Test
    void testAddActivityWithZeroCalories() {
        diaryViewModel.setTitle("Walking");
        diaryViewModel.setCaloriesBurned(0.0);

        diaryViewModel.addActivity();

        verify(diaryFacade, never()).addActivityToDiaryToday(any());
        assertEquals("Calories burned must be a positive number.", diaryViewModel.getDiaryStatusMessage());
    }

    @Test
    void testAddActivityThrowsException() {
        doThrow(new RuntimeException("Database error")).when(diaryFacade).addActivityToDiaryToday(any(ActivityDto.class));
        diaryViewModel.setTitle("Cycling");
        diaryViewModel.setCaloriesBurned(400.0);

        diaryViewModel.addActivity();

        assertEquals("Error adding activity: Database error", diaryViewModel.getDiaryStatusMessage());
    }

    @Test
    void testRemoveItemWithFoodConsumedViewModel() {
        FoodConsumedViewModel foodItem = createDefaultFoodConsumedViewModel();
        diaryViewModel.setSelectedItem(foodItem);

        diaryViewModel.removeItem();

        verify(diaryFacade, times(1)).removeFoodFromDiary(foodItem.foodConsumedDto());
        assertEquals("Food removed successfully.", diaryViewModel.getDiaryStatusMessage());
    }

    @Test
    void testRemoveItemWithActivityViewModel() {
        ActivityViewModel activityItem = createDefaultActivityViewModel();
        diaryViewModel.setSelectedItem(activityItem);

        diaryViewModel.removeItem();

        verify(diaryFacade, times(1)).removeActivityFromDiary(activityItem.activityDto());
        assertEquals("Activity removed successfully.", diaryViewModel.getDiaryStatusMessage());
    }

    @Test
    void testRemoveItemWithNullSelected() {
        diaryViewModel.setSelectedItem(null);

        diaryViewModel.removeItem();

        verify(diaryFacade, never()).removeFoodFromDiary(any());
        verify(diaryFacade, never()).removeActivityFromDiary(any());
        assertEquals("Unknown item type.", diaryViewModel.getDiaryStatusMessage());
    }

    @Test
    void testLoadFoodsPopulatesList() {
        FoodConsumedDto foodConsumedDto = new FoodConsumedDto(1, 1, 1, ConsumptionType.PER_QUANTITY, 100.0);
        when(diaryFacade.getAllFoodsToday()).thenReturn(List.of(foodConsumedDto));
        when(diaryFacade.getFoodDetails(any())).thenReturn(createDefaultFoodDto());
        when(foodFacade.calculateNutritionalValues(any())).thenReturn(Map.of("calories", 52.0));

        diaryViewModel.loadFoods();

        assertFalse(diaryViewModel.getFoods().isEmpty());
        assertEquals(1, diaryViewModel.getFoods().size());
        assertEquals("Apple", diaryViewModel.getFoods().get(0).getName());
    }

    @Test
    void testLoadActivitiesPopulatesList() {
        ActivityDto activityDto = new ActivityDto(1, "Running", 300.0, 1);
        when(diaryFacade.getAllActivitiesToday()).thenReturn(List.of(activityDto));

        diaryViewModel.loadActivities();

        assertFalse(diaryViewModel.getActivities().isEmpty());
        assertEquals(1, diaryViewModel.getActivities().size());
        assertEquals("Running", diaryViewModel.getActivities().get(0).getTitle());
    }

    @Test
    void testLoadFoodsHandlesException() {
        when(diaryFacade.getAllFoodsToday()).thenThrow(new RuntimeException("Database connection failed"));

        diaryViewModel.loadFoods();

        assertEquals("Error loading foods: Database connection failed", diaryViewModel.getDiaryStatusMessage());
    }

    @Test
    void testSetCaloriesBurnedWithNegativeValue() {
        diaryViewModel.setCaloriesBurned(-50.0);
        diaryViewModel.setTitle("Test");
        diaryViewModel.addActivity();

        verify(diaryFacade, never()).addActivityToDiaryToday(any());
        assertEquals("Calories burned must be a positive number.", diaryViewModel.getDiaryStatusMessage());
    }

    @Test
    void testSetSelectedItemWithInvalidType() {
        assertThrows(IllegalArgumentException.class, () ->
            diaryViewModel.setSelectedItem("Invalid Type"), "Should throw for invalid item type.");
    }

    //  ##### Helper methods to create test data #####
    private FoodDto createDefaultFoodDto() {
        return new FoodDto(1, "Apple", "123", "g", 52.0, 0.3, 0.2, 14.0, 100.0, 1.0);
    }

    private FoodConsumedViewModel createDefaultFoodConsumedViewModel() {
        FoodConsumedDto foodConsumedDto = new FoodConsumedDto(1, 1, 1, ConsumptionType.PER_QUANTITY, 100.0);
        Map<String, Double> nutritionalValues = Map.of("calories", 52.0);
        return new FoodConsumedViewModel(foodConsumedDto, createDefaultFoodDto(), nutritionalValues);
    }

    private ActivityViewModel createDefaultActivityViewModel() {
        ActivityDto activityDto = new ActivityDto(1, "Running", 300.0, 1);
        return new ActivityViewModel(activityDto);
    }
}