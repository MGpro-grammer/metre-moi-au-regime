package be.esi.prj.model;

import static org.junit.jupiter.api.Assertions.*;

import be.esi.prj.dto.*;
import be.esi.prj.enumeration.ActivityLevel;
import be.esi.prj.enumeration.ConsumptionType;
import be.esi.prj.enumeration.Gender;
import be.esi.prj.repository.*;
import be.esi.prj.service.SessionService;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.*;

class DiaryFacadeTest {

    private DiaryFacade diaryFacade;
    private UserRepository userRepository;
    private DiaryRepository diaryRepository;
    private ActivityRepository activityRepository;
    private FoodRepository foodRepository;
    private FoodConsumedRepository foodConsumedRepository;
    private SessionService sessionService;

    private TestDataBuilder testDataBuilder;

    private final List<Integer> userIdsToClean = new ArrayList<>();
    private final List<Integer> foodIdsToClean = new ArrayList<>();
    private final List<Integer> foodConsumedIdsToClean = new ArrayList<>();
    private final List<Integer> activityIdsToClean = new ArrayList<>();

    @BeforeEach
    void setUp() {
        userRepository = new UserRepository();
        diaryRepository = new DiaryRepository();
        activityRepository = new ActivityRepository();
        foodRepository = new FoodRepository();
        foodConsumedRepository = new FoodConsumedRepository();

        sessionService = SessionService.getInstance();

        diaryFacade = new DiaryFacade(diaryRepository, activityRepository,
                foodRepository, foodConsumedRepository);

        testDataBuilder = new TestDataBuilder();
        sessionService.logout();
    }

    @AfterEach
    void tearDown() {
        foodIdsToClean.forEach(foodRepository::deleteById);
        foodConsumedIdsToClean.forEach(foodConsumedRepository::deleteById);
        activityIdsToClean.forEach(activityRepository::deleteById);
        userIdsToClean.forEach(userRepository::deleteById);

        foodConsumedIdsToClean.clear();
        activityIdsToClean.clear();
        userIdsToClean.clear();
    }

    // ##### Diary Management Tests #####

    @Test
    void testAddFoodToDiaryTodayWithValidFood() {
        // Arrange
        UserDto user = testDataBuilder.createDefaultUser();
        int userId = userRepository.save(user);
        userIdsToClean.add(userId);
        sessionService.login(userRepository.findById(userId).get());
        FoodDto food = testDataBuilder.createDefaultFood();

        // Act
        int foodConsumedId = diaryFacade.addFoodToDiaryToday(food, 80.0, ConsumptionType.PER_QUANTITY);
        foodConsumedIdsToClean.add(foodConsumedId);

        // assert
        List<FoodConsumedDto> foods = diaryFacade.getAllFoodsToday();
        assertTrue(foods.stream().anyMatch(f -> f.foodConsumedId() == foodConsumedId));

        Optional<FoodConsumedDto> foodConsumed = foodConsumedRepository.findById(foodConsumedId);
        assertTrue(foodConsumed.isPresent());
    }

    @Test
    void testAddFoodToDiaryTodayWithNullFood() {
        // Arrange
        UserDto user = testDataBuilder.createDefaultUser();
        int userId = userRepository.save(user);
        userIdsToClean.add(userId);
        sessionService.login(userRepository.findById(userId).get());

        // Act & Assert
        assertThrows(Exception.class, () ->
                diaryFacade.addFoodToDiaryToday(null, 100.0, ConsumptionType.PER_QUANTITY));
    }

    @Test
    void testAddActivityToDiaryTodayWithValidActivity() {
        // Arrange
        UserDto user = testDataBuilder.createDefaultUser();
        int userId = userRepository.save(user);
        userIdsToClean.add(userId);
        sessionService.login(userRepository.findById(userId).get());
        ActivityDto activity = testDataBuilder.createDefaultActivity();

        // Act
        int activityId = diaryFacade.addActivityToDiaryToday(activity);
        activityIdsToClean.add(activityId);

        // Assert
        List<ActivityDto> activities = diaryFacade.getAllActivitiesToday();
        assertTrue(activities.stream().anyMatch(a -> a.activityId() == activityId));

        Optional<ActivityDto> activityOpt = activityRepository.findById(activityId);
        assertTrue(activityOpt.isPresent());
    }

    @Test
    void testAddActivityToDiaryTodayWithNullActivity() {
        // Arrange
        UserDto user = testDataBuilder.createDefaultUser();
        int userId = userRepository.save(user);
        userIdsToClean.add(userId);
        sessionService.login(userRepository.findById(userId).get());

        // Act & Assert
        assertThrows(Exception.class, () -> diaryFacade.addActivityToDiaryToday(null));
    }

    @Test
    void testRemoveFoodFromDiarySuccess() {
        // Arrange
        UserDto user = testDataBuilder.createDefaultUser();
        int userId = userRepository.save(user);
        userIdsToClean.add(userId);
        sessionService.login(userRepository.findById(userId).get());
        FoodDto food = testDataBuilder.createDefaultFood();
        int foodConsumedId = diaryFacade.addFoodToDiaryToday(food, 80.0, ConsumptionType.PER_QUANTITY);
        foodConsumedIdsToClean.add(foodConsumedId);

        List<FoodConsumedDto> foodsBefore = diaryFacade.getAllFoodsToday();
        assertTrue(foodsBefore.stream().anyMatch(f -> f.foodConsumedId() == foodConsumedId));

        // Act
        FoodConsumedDto foodConsumedDto = foodConsumedRepository.findById(foodConsumedId).orElseThrow();
        assertDoesNotThrow(() -> diaryFacade.removeFoodFromDiary(foodConsumedDto));

        // Assert
        List<FoodConsumedDto> foodsAfter = diaryFacade.getAllFoodsToday();
        assertFalse(foodsAfter.stream().anyMatch(f -> f.foodConsumedId() == foodConsumedId));
        foodConsumedIdsToClean.remove((Integer) foodConsumedId);
    }

    @Test
    void testRemoveFoodFromDiaryWithNullDto() {
        // Arrange
        UserDto user = testDataBuilder.createDefaultUser();
        int userId = userRepository.save(user);
        userIdsToClean.add(userId);
        sessionService.login(userRepository.findById(userId).get());

        // Act & Assert
        assertThrows(Exception.class, () -> diaryFacade.removeFoodFromDiary(null));
    }

    @Test
    void testRemoveActivityFromDiarySuccess() {
        // Arrange
        UserDto user = testDataBuilder.createDefaultUser();
        int userId = userRepository.save(user);
        userIdsToClean.add(userId);
        sessionService.login(userRepository.findById(userId).get());
        ActivityDto activity = testDataBuilder.createDefaultActivity();
        int activityId = diaryFacade.addActivityToDiaryToday(activity);
        activityIdsToClean.add(activityId);

        List<ActivityDto> activitiesBefore = diaryFacade.getAllActivitiesToday();
        assertTrue(activitiesBefore.stream().anyMatch(a -> a.activityId() == activityId));

        // Act
        ActivityDto activityDto = activityRepository.findById(activityId).orElseThrow();
        assertDoesNotThrow(() -> diaryFacade.removeActivityFromDiary(activityDto));

        // Assert
        List<ActivityDto> activitiesAfter = diaryFacade.getAllActivitiesToday();
        assertFalse(activitiesAfter.stream().anyMatch(a -> a.activityId() == activityId));
        activityIdsToClean.remove((Integer) activityId);
    }

    @Test
    void testRemoveActivityFromDiaryWithNullDto() {
        // Arrange
        UserDto user = testDataBuilder.createDefaultUser();
        int userId = userRepository.save(user);
        userIdsToClean.add(userId);
        sessionService.login(userRepository.findById(userId).get());

        // Act & Assert
        assertThrows(Exception.class, () -> diaryFacade.removeActivityFromDiary(null));
    }

    // ##### Fetching Methods Tests #####

    @Test
    void testGetAllFoodsTodayWithExistingFoods() {
        // Arrange
        UserDto user = testDataBuilder.createDefaultUser();
        int userId = userRepository.save(user);
        userIdsToClean.add(userId);
        sessionService.login(userRepository.findById(userId).get());
        FoodDto food = testDataBuilder.createDefaultFood();
        int foodConsumedId = diaryFacade.addFoodToDiaryToday(food, 80.0, ConsumptionType.PER_QUANTITY);
        foodConsumedIdsToClean.add(foodConsumedId);

        // Act
        List<FoodConsumedDto> foods = diaryFacade.getAllFoodsToday();

        // Assert
        assertFalse(foods.isEmpty());
        assertTrue(foods.stream().anyMatch(f -> f.foodConsumedId() == foodConsumedId));
    }

    @Test
    void testGetAllActivitiesTodayWithExistingActivities() {
        // Arrange
        UserDto user = testDataBuilder.createDefaultUser();
        int userId = userRepository.save(user);
        userIdsToClean.add(userId);
        sessionService.login(userRepository.findById(userId).get());
        ActivityDto activity = testDataBuilder.createDefaultActivity();
        int activityId = diaryFacade.addActivityToDiaryToday(activity);
        activityIdsToClean.add(activityId);

        // Act
        List<ActivityDto> activities = diaryFacade.getAllActivitiesToday();

        // Assert
        assertFalse(activities.isEmpty());
        assertTrue(activities.stream().anyMatch(a -> a.activityId() == activityId));
    }

    @Test
    void testGetAllActivitiesTodayWithEmptyDiary() {
        // Arrange
        UserDto user = testDataBuilder.createDefaultUser();
        int userId = userRepository.save(user);
        userIdsToClean.add(userId);
        sessionService.login(userRepository.findById(userId).get());

        // Act
        List<ActivityDto> activities = diaryFacade.getAllActivitiesToday();

        // Assert
        assertTrue(activities.isEmpty());
    }

    @Test
    void testGetFoodsByDiaryIdWithValidId() {
        // Arrange
        UserDto user = testDataBuilder.createDefaultUser();
        int userId = userRepository.save(user);
        userIdsToClean.add(userId);
        sessionService.login(userRepository.findById(userId).get());
        FoodDto food = testDataBuilder.createDefaultFood();
        int foodConsumedId = diaryFacade.addFoodToDiaryToday(food, 80.0, ConsumptionType.PER_QUANTITY);
        foodConsumedIdsToClean.add(foodConsumedId);

        int diaryId = diaryRepository.findByUserIdAndDate(userId, LocalDate.now()).orElseThrow().diaryId();

        // Act
        List<FoodConsumedDto> foods = diaryFacade.getFoodsByDiaryId(diaryId);

        // Assert
        assertFalse(foods.isEmpty());
        assertTrue(foods.stream().anyMatch(f -> f.foodConsumedId() == foodConsumedId));
    }

    @Test
    void testGetActivitiesByDiaryIdWithValidId() {
        // Arrange
        UserDto user = testDataBuilder.createDefaultUser();
        int userId = userRepository.save(user);
        userIdsToClean.add(userId);
        sessionService.login(userRepository.findById(userId).get());

        ActivityDto activity = testDataBuilder.createDefaultActivity();
        int activityId = diaryFacade.addActivityToDiaryToday(activity);
        activityIdsToClean.add(activityId);

        int diaryId = diaryRepository.findByUserIdAndDate(userId, LocalDate.now()).get().diaryId();

        // Act
        List<ActivityDto> activities = diaryFacade.getActivitiesByDiaryId(diaryId);

        // Assert
        assertFalse(activities.isEmpty());
        assertTrue(activities.stream().anyMatch(a -> a.activityId() == activityId));
    }

    @Test
    void testGetFoodDetailsWithExistingFood() {
        // Arrange
        UserDto user = testDataBuilder.createDefaultUser();
        int userId = userRepository.save(user);
        userIdsToClean.add(userId);
        sessionService.login(userRepository.findById(userId).get());
        FoodDto food = testDataBuilder.createDefaultFood();
        int foodId = foodRepository.save(food);
        foodIdsToClean.add(foodId);
        FoodConsumedDto foodConsumedDto = new FoodConsumedDto(-1, 1, foodId, ConsumptionType.PER_QUANTITY, 100.0);

        // Act & Assert
        assertDoesNotThrow(() -> diaryFacade.getFoodDetails(foodConsumedDto));
    }

    @Test
    void testGetFoodDetailsWithNonExistingFood() {
        // Arrange
        int invalidFoodId = -999;
        FoodConsumedDto invalidFoodConsumedDto = new FoodConsumedDto(-1, 1, invalidFoodId, ConsumptionType.PER_QUANTITY, 100.0);

        // Act & Assert
        assertThrows(Exception.class, () -> diaryFacade.getFoodDetails(invalidFoodConsumedDto));
    }

    // ##### Tests for calculateConsumedNutrients() #####

    @Test
    void testCalculateConsumedNutrientsWithEmptyList() {
        // Arrange
        UserDto user = testDataBuilder.createDefaultUser();
        int userId = userRepository.save(user);
        userIdsToClean.add(userId);
        sessionService.login(userRepository.findById(userId).get());

        // Act
        Map<String, Map<String, Double>> progress = diaryFacade.getProgressOfNutritionalValues();

        // Assert
        assertNotNull(progress);
        assertTrue(progress.containsKey("calories"));
        assertTrue(progress.containsKey("proteins"));
        assertTrue(progress.containsKey("fats"));
        assertTrue(progress.containsKey("carbohydrates"));

        // Vérifier que les valeurs consommées sont à 0
        assertEquals(0.0, progress.get("calories").get("consumedValue"));
        assertEquals(0.0, progress.get("proteins").get("consumedValue"));
        assertEquals(0.0, progress.get("fats").get("consumedValue"));
        assertEquals(0.0, progress.get("carbohydrates").get("consumedValue"));
    }

    @Test
    void testCalculateConsumedNutrientsWithSingleFood() {
        // Arrange
        UserDto user = testDataBuilder.createDefaultUser();
        int userId = userRepository.save(user);
        userIdsToClean.add(userId);
        sessionService.login(userRepository.findById(userId).get());

        FoodDto food = testDataBuilder.createDefaultFood();
        int foodConsumedId = diaryFacade.addFoodToDiaryToday(food, 100.0, ConsumptionType.PER_QUANTITY);
        foodConsumedIdsToClean.add(foodConsumedId);

        // Act
        Map<String, Map<String, Double>> progress = diaryFacade.getProgressOfNutritionalValues();

        // Assert
        assertNotNull(progress);
        assertTrue(progress.get("calories").get("consumedValue") > 0);
        assertTrue(progress.get("proteins").get("consumedValue") >= 0);
        assertTrue(progress.get("fats").get("consumedValue") >= 0);
        assertTrue(progress.get("carbohydrates").get("consumedValue") >= 0);
    }

    @Test
    void testCalculateConsumedNutrientsWithMultipleFoods() {
        // Arrange
        UserDto user = testDataBuilder.createDefaultUser();
        int userId = userRepository.save(user);
        userIdsToClean.add(userId);
        sessionService.login(userRepository.findById(userId).get());

        FoodDto food1 = testDataBuilder.createDefaultFood();
        FoodDto food2 = testDataBuilder.createSecondaryFood();

        int foodConsumedId1 = diaryFacade.addFoodToDiaryToday(food1, 100.0, ConsumptionType.PER_QUANTITY);
        int foodConsumedId2 = diaryFacade.addFoodToDiaryToday(food2, 150.0, ConsumptionType.PER_QUANTITY);

        foodConsumedIdsToClean.add(foodConsumedId1);
        foodConsumedIdsToClean.add(foodConsumedId2);

        // Act
        Map<String, Map<String, Double>> progress = diaryFacade.getProgressOfNutritionalValues();

        // Assert
        assertNotNull(progress);
        double totalCalories = progress.get("calories").get("consumedValue");
        double totalProteins = progress.get("proteins").get("consumedValue");

        // Vérifier que les valeurs sont additionnées (doit être > aux valeurs individuelles)
        assertTrue(totalCalories > food1.calories());
        assertTrue(totalProteins >= food1.proteins());
    }

    @Test
    void testCalculateConsumedNutrientsVerifyAllNutrients() {
        // Arrange
        UserDto user = testDataBuilder.createDefaultUser();
        int userId = userRepository.save(user);
        userIdsToClean.add(userId);
        sessionService.login(userRepository.findById(userId).get());

        FoodDto food = testDataBuilder.createDefaultFood();
        int foodConsumedId = diaryFacade.addFoodToDiaryToday(food, 100.0, ConsumptionType.PER_QUANTITY);
        foodConsumedIdsToClean.add(foodConsumedId);

        // Act
        Map<String, Map<String, Double>> progress = diaryFacade.getProgressOfNutritionalValues();

        // Assert
        String[] expectedNutrients = {"calories", "proteins", "fats", "carbohydrates"};
        for (String nutrient : expectedNutrients) {
            assertTrue(progress.containsKey(nutrient));
            assertTrue(progress.get(nutrient).containsKey("consumedValue"));
            assertTrue(progress.get(nutrient).containsKey("goalValue"));
            assertTrue(progress.get(nutrient).containsKey("percentage"));
            assertTrue(progress.get(nutrient).containsKey("gaugeAngle"));
            assertNotNull(progress.get(nutrient).get("consumedValue"));
            assertNotNull(progress.get(nutrient).get("goalValue"));
        }
    }

    // ##### Tests for calculateNutritionalNeeds() #####

    @Test
    void testCalculateNutritionalNeedsForLoseWeight() {
        // Arrange
        UserDto user = testDataBuilder.createUserForWeightLoss();
        int userId = userRepository.save(user);
        userIdsToClean.add(userId);
        sessionService.login(userRepository.findById(userId).get());

        // Act
        Map<String, Map<String, Double>> progress = diaryFacade.getProgressOfNutritionalValues();

        // Assert
        assertNotNull(progress);
        assertTrue(progress.get("calories").get("goalValue") > 0);
        assertTrue(progress.get("proteins").get("goalValue") > 0);
        assertTrue(progress.get("fats").get("goalValue") > 0);
        assertTrue(progress.get("carbohydrates").get("goalValue") > 0);
    }

    @Test
    void testCalculateNutritionalNeedsForGainWeight() {
        // Arrange
        UserDto user = testDataBuilder.createUserForWeightGain();
        int userId = userRepository.save(user);
        userIdsToClean.add(userId);
        sessionService.login(userRepository.findById(userId).get());

        // Act
        Map<String, Map<String, Double>> progress = diaryFacade.getProgressOfNutritionalValues();

        // Assert
        assertNotNull(progress);
        assertTrue(progress.get("calories").get("goalValue") > 0);
        assertTrue(progress.get("proteins").get("goalValue") > 0);
        assertTrue(progress.get("fats").get("goalValue") > 0);
        assertTrue(progress.get("carbohydrates").get("goalValue") > 0);
    }

    @Test
    void testCalculateNutritionalNeedsForMaintainWeight() {
        // Arrange
        UserDto user = testDataBuilder.createUserForWeightMaintenance();
        int userId = userRepository.save(user);
        userIdsToClean.add(userId);
        sessionService.login(userRepository.findById(userId).get());

        // Act
        Map<String, Map<String, Double>> progress = diaryFacade.getProgressOfNutritionalValues();

        // Assert
        assertNotNull(progress);
        assertTrue(progress.get("calories").get("goalValue") > 0);
        assertTrue(progress.get("proteins").get("goalValue") > 0);
        assertTrue(progress.get("fats").get("goalValue") > 0);
        assertTrue(progress.get("carbohydrates").get("goalValue") > 0);
    }

    // ##### Tests for buildProgressMap() #####

    @Test
    void testBuildProgressMapStructure() {
        // Arrange
        UserDto user = testDataBuilder.createDefaultUser();
        int userId = userRepository.save(user);
        userIdsToClean.add(userId);
        sessionService.login(userRepository.findById(userId).get());

        // Act
        Map<String, Map<String, Double>> progress = diaryFacade.getProgressOfNutritionalValues();

        // Assert
        assertEquals(4, progress.size());
        String[] expectedNutrients = {"calories", "proteins", "fats", "carbohydrates"};

        for (String nutrient : expectedNutrients) {
            assertTrue(progress.containsKey(nutrient));
            Map<String, Double> nutrientDetails = progress.get(nutrient);
            assertTrue(nutrientDetails.containsKey("consumedValue"));
            assertTrue(nutrientDetails.containsKey("goalValue"));
            assertTrue(nutrientDetails.containsKey("percentage"));
            assertTrue(nutrientDetails.containsKey("gaugeAngle"));
        }
    }

    @Test
    void testBuildProgressMapNutrientMapping() {
        // Arrange
        UserDto user = testDataBuilder.createDefaultUser();
        int userId = userRepository.save(user);
        userIdsToClean.add(userId);
        sessionService.login(userRepository.findById(userId).get());

        // Act
        Map<String, Map<String, Double>> progress = diaryFacade.getProgressOfNutritionalValues();

        // Assert
        String[] expectedNutrients = {"calories", "proteins", "fats", "carbohydrates"};
        for (String nutrient : expectedNutrients) {
            assertTrue(progress.containsKey(nutrient));
            assertNotNull(progress.get(nutrient));
            assertFalse(progress.get(nutrient).isEmpty());
        }
    }

    @Test
    void testBuildProgressMapGoalValuesSetting() {
        // Arrange
        UserDto user = testDataBuilder.createDefaultUser();
        int userId = userRepository.save(user);
        userIdsToClean.add(userId);
        sessionService.login(userRepository.findById(userId).get());

        // Act
        Map<String, Map<String, Double>> progress = diaryFacade.getProgressOfNutritionalValues();

        // Assert
        String[] expectedNutrients = {"calories", "proteins", "fats", "carbohydrates"};
        for (String nutrient : expectedNutrients) {
            assertTrue(progress.get(nutrient).get("goalValue") > 0);
        }
    }

    // ##### Tests for calculateCaloriesBurnedToday() #####

    @Test
    void testCalculateCaloriesBurnedWithNoActivities() {
        // Arrange
        UserDto user = testDataBuilder.createDefaultUser();
        int userId = userRepository.save(user);
        userIdsToClean.add(userId);
        sessionService.login(userRepository.findById(userId).get());

        // Act
        Map<String, Map<String, Double>> progress = diaryFacade.getProgressOfNutritionalValues();

        // Assert
        assertEquals(0.0, progress.get("calories").get("consumedValue"));
    }

    @Test
    void testCalculateCaloriesBurnedWithSingleActivity() {
        // Arrange
        UserDto user = testDataBuilder.createDefaultUser();
        int userId = userRepository.save(user);
        userIdsToClean.add(userId);
        sessionService.login(userRepository.findById(userId).get());

        ActivityDto activity = testDataBuilder.createDefaultActivity();
        int activityId = diaryFacade.addActivityToDiaryToday(activity);
        activityIdsToClean.add(activityId);

        // Act
        Map<String, Map<String, Double>> progress = diaryFacade.getProgressOfNutritionalValues();

        // Assert
        assertEquals(-activity.caloriesBurned(), progress.get("calories").get("consumedValue"));
    }

    @Test
    void testCalculateCaloriesBurnedWithMultipleActivities() {
        // Arrange
        UserDto user = testDataBuilder.createDefaultUser();
        int userId = userRepository.save(user);
        userIdsToClean.add(userId);
        sessionService.login(userRepository.findById(userId).get());

        ActivityDto activity1 = testDataBuilder.createDefaultActivity();
        ActivityDto activity2 = testDataBuilder.createSecondaryActivity();

        int activityId1 = diaryFacade.addActivityToDiaryToday(activity1);
        int activityId2 = diaryFacade.addActivityToDiaryToday(activity2);

        activityIdsToClean.add(activityId1);
        activityIdsToClean.add(activityId2);

        // Act
        Map<String, Map<String, Double>> progress = diaryFacade.getProgressOfNutritionalValues();

        // Assert
        double expectedTotalBurned = activity1.caloriesBurned() + activity2.caloriesBurned();
        assertEquals(-expectedTotalBurned, progress.get("calories").get("consumedValue"));
    }

    @Test
    void testCalculateConsumedNutrientsWithFoodAndActivity() {
        // Arrange
        UserDto user = testDataBuilder.createDefaultUser();
        int userId = userRepository.save(user);
        userIdsToClean.add(userId);
        sessionService.login(userRepository.findById(userId).get());

        FoodDto food = testDataBuilder.createDefaultFood();
        int foodConsumedId = diaryFacade.addFoodToDiaryToday(food, 100.0, ConsumptionType.PER_QUANTITY);
        foodConsumedIdsToClean.add(foodConsumedId);

        ActivityDto activity = testDataBuilder.createDefaultActivity();
        int activityId = diaryFacade.addActivityToDiaryToday(activity);
        activityIdsToClean.add(activityId);

        // Act
        Map<String, Map<String, Double>> progress = diaryFacade.getProgressOfNutritionalValues();

        // Assert
        double expectedCalories = food.calories() - activity.caloriesBurned();
        assertEquals(expectedCalories, progress.get("calories").get("consumedValue"), 0.01);

        assertTrue(progress.get("proteins").get("consumedValue") >= 0);
        assertTrue(progress.get("fats").get("consumedValue") >= 0);
        assertTrue(progress.get("carbohydrates").get("consumedValue") >= 0);
    }

    // ##### Tests for NutritionalValuesService integration #####

    @Test
    void testCalculateNutritionalValuesPerPercent() {
        // Arrange
        UserDto user = testDataBuilder.createDefaultUser();
        int userId = userRepository.save(user);
        userIdsToClean.add(userId);
        sessionService.login(userRepository.findById(userId).get());

        FoodDto food = testDataBuilder.createDefaultFood();
        int foodConsumedId = diaryFacade.addFoodToDiaryToday(food, 50.0, ConsumptionType.PER_PERCENT);
        foodConsumedIdsToClean.add(foodConsumedId);

        // Act
        Map<String, Map<String, Double>> progress = diaryFacade.getProgressOfNutritionalValues();

        // Assert
        assertEquals(food.calories() * 0.5, progress.get("calories").get("consumedValue"));
        assertEquals(food.proteins() * 0.5, progress.get("proteins").get("consumedValue"));
    }

    @Test
    void testCalculateNutritionalValuesPerServing() {
        // Arrange
        UserDto user = testDataBuilder.createDefaultUser();
        int userId = userRepository.save(user);
        userIdsToClean.add(userId);
        sessionService.login(userRepository.findById(userId).get());

        FoodDto food = new FoodDto(-1, "Banana", "456789", "g", 89.0, 1.1, 0.3, 23.0, 100.0, 120.0);
        int foodConsumedId = diaryFacade.addFoodToDiaryToday(food, 2.0, ConsumptionType.PER_SERVING);
        foodConsumedIdsToClean.add(foodConsumedId);

        // Act
        Map<String, Map<String, Double>> progress = diaryFacade.getProgressOfNutritionalValues();

        // Assert
        double expectedCalories = (food.calories() / 100) * food.quantityServing() * 2.0;
        assertEquals(expectedCalories, progress.get("calories").get("consumedValue"));
    }

    @Test
    void testCalculateNutritionalValuesPerUnit() {
        // Arrange
        UserDto user = testDataBuilder.createDefaultUser();
        int userId = userRepository.save(user);
        userIdsToClean.add(userId);
        sessionService.login(userRepository.findById(userId).get());

        FoodDto food = testDataBuilder.createDefaultFood();
        int foodConsumedId = diaryFacade.addFoodToDiaryToday(food, 3.0, ConsumptionType.PER_UNIT);
        foodConsumedIdsToClean.add(foodConsumedId);

        // Act
        Map<String, Map<String, Double>> progress = diaryFacade.getProgressOfNutritionalValues();

        // Assert
        double expectedCalories = (food.calories() / 100) * food.quantityUnit() * 3.0;
        assertEquals(expectedCalories, progress.get("calories").get("consumedValue"));
    }

    @Test
    void testNutritionalValuesWithZeroQuantity() {
        // Arrange
        UserDto user = testDataBuilder.createDefaultUser();
        int userId = userRepository.save(user);
        userIdsToClean.add(userId);
        sessionService.login(userRepository.findById(userId).get());

        FoodDto food = testDataBuilder.createDefaultFood();
        int foodConsumedId = diaryFacade.addFoodToDiaryToday(food, 0.0, ConsumptionType.PER_QUANTITY);
        foodConsumedIdsToClean.add(foodConsumedId);

        // Act
        Map<String, Map<String, Double>> progress = diaryFacade.getProgressOfNutritionalValues();

        // Assert
        assertEquals(0.0, progress.get("calories").get("consumedValue"));
        assertEquals(0.0, progress.get("proteins").get("consumedValue"));
        assertEquals(0.0, progress.get("fats").get("consumedValue"));
        assertEquals(0.0, progress.get("carbohydrates").get("consumedValue"));
    }

    @Test
    void testNutritionalValuesAccumulation() {
        // Arrange
        UserDto user = testDataBuilder.createDefaultUser();
        int userId = userRepository.save(user);
        userIdsToClean.add(userId);
        sessionService.login(userRepository.findById(userId).get());

        FoodDto food = testDataBuilder.createDefaultFood();
        int foodConsumedId1 = diaryFacade.addFoodToDiaryToday(food, 50.0, ConsumptionType.PER_QUANTITY);
        int foodConsumedId2 = diaryFacade.addFoodToDiaryToday(food, 30.0, ConsumptionType.PER_QUANTITY);

        foodConsumedIdsToClean.add(foodConsumedId1);
        foodConsumedIdsToClean.add(foodConsumedId2);

        // Act
        Map<String, Map<String, Double>> progress = diaryFacade.getProgressOfNutritionalValues();

        // Assert
        double expectedCalories = (food.calories() / 100) * (50.0 + 30.0);
        assertEquals(expectedCalories, progress.get("calories").get("consumedValue"));
    }

    @Test
    void testNutritionalValuesServiceIntegrationWithComplexFood() {
        // Arrange
        UserDto user = testDataBuilder.createDefaultUser();
        int userId = userRepository.save(user);
        userIdsToClean.add(userId);
        sessionService.login(userRepository.findById(userId).get());

        FoodDto complexFood = new FoodDto(-1, "Complex Food", "987654321", "g",
                                         250.0, 15.5, 8.2, 35.0, 80.0, 45.0);
        int foodConsumedId = diaryFacade.addFoodToDiaryToday(complexFood, 200.0, ConsumptionType.PER_QUANTITY);
        foodConsumedIdsToClean.add(foodConsumedId);

        // Act
        Map<String, Map<String, Double>> progress = diaryFacade.getProgressOfNutritionalValues();

        // Assert
        assertEquals(500.0, progress.get("calories").get("consumedValue"));
        assertEquals(31.0, progress.get("proteins").get("consumedValue"));
        assertEquals(16.4, progress.get("fats").get("consumedValue"));
        assertEquals(70.0, progress.get("carbohydrates").get("consumedValue"));
    }

    @Test
    void testNutritionalValuesWithDecimalQuantities() {
        // Arrange
        UserDto user = testDataBuilder.createDefaultUser();
        int userId = userRepository.save(user);
        userIdsToClean.add(userId);
        sessionService.login(userRepository.findById(userId).get());

        FoodDto food = testDataBuilder.createDefaultFood();
        int foodConsumedId = diaryFacade.addFoodToDiaryToday(food, 150.5, ConsumptionType.PER_QUANTITY);
        foodConsumedIdsToClean.add(foodConsumedId);

        // Act
        Map<String, Map<String, Double>> progress = diaryFacade.getProgressOfNutritionalValues();

        // Assert
        double expectedCalories = (food.calories() / 100) * 150.5;
        assertEquals(expectedCalories, progress.get("calories").get("consumedValue"));
    }

    private static class TestDataBuilder {

        public UserDto createDefaultUser() {
            return new UserDto(
                    -1,
                    "test@example.com",
                    "hashedPassword",
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

        public FoodDto createDefaultFood() {
            return new FoodDto(
                    -1,
                    "Test Apple",
                    "1234567890123",
                    "g",
                    52.0,
                    0.3,
                    0.2,
                    14.0,
                    150.0,
                    100.0
            );
        }

        public ActivityDto createDefaultActivity() {
            return new ActivityDto(
                    -1,
                    "Running",
                    300.0,
                    1
            );
        }
        public FoodDto createSecondaryFood() {
            return new FoodDto(
                    -1,
                    "Test Banana",
                    "1234567890124",
                    "g",
                    89.0,
                    1.1,
                    0.3,
                    23.0,
                    200.0,
                    120.0
            );
        }

        public ActivityDto createSecondaryActivity() {
            return new ActivityDto(
                    -1,
                    "Swimming",
                    450.0,
                    1
            );
        }

        public UserDto createUserForWeightLoss() {
            return new UserDto(
                    -1,
                    "weightloss@example.com",
                    "hashedPassword",
                    "Weight Loss User",
                    30,
                    Gender.FEMALE,
                    165.0,
                    80.0,
                    65.0, // goalWeight < weight
                    LocalDate.of(2025, 1, 1),
                    LocalDate.of(2025, 12, 31),
                    ActivityLevel.MODERATE
            );
        }

        public UserDto createUserForWeightGain() {
            return new UserDto(
                    -1,
                    "weightgain@example.com",
                    "hashedPassword",
                    "Weight Gain User",
                    25,
                    Gender.MALE,
                    180.0,
                    60.0,
                    75.0, // goalWeight > weight
                    LocalDate.of(2025, 1, 1),
                    LocalDate.of(2025, 12, 31),
                    ActivityLevel.VERY_ACTIVE
            );
        }

        public UserDto createUserForWeightMaintenance() {
            return new UserDto(
                    -1,
                    "maintain@example.com",
                    "hashedPassword",
                    "Maintain Weight User",
                    28,
                    Gender.MALE,
                    175.0,
                    70.0,
                    70.0, // goalWeight = weight
                    LocalDate.of(2025, 1, 1),
                    LocalDate.of(2025, 12, 31),
                    ActivityLevel.MODERATE
            );
        }
    }
}
