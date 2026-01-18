package be.esi.prj.model;

import be.esi.prj.dto.*;
import be.esi.prj.enumeration.ConsumptionType;
import be.esi.prj.enumeration.GoalType;
import be.esi.prj.repository.ActivityRepository;
import be.esi.prj.repository.DiaryRepository;
import be.esi.prj.repository.FoodConsumedRepository;
import be.esi.prj.repository.FoodRepository;
import be.esi.prj.service.NutritionalNeedsService;
import be.esi.prj.service.NutritionalValuesService;
import be.esi.prj.service.SessionService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This class manages diary entries for users.
 * It helps to add, remove and get food and activities from the diary.
 */
public class DiaryFacade {
    private final DiaryRepository diaryRepository;
    private final ActivityRepository activityRepository;
    private final FoodRepository foodRepository;
    private final FoodConsumedRepository foodConsumedRepository;

    /**
     * Creates a new DiaryFacade with repositories.
     * @param diaryRepository the repository for diary data
     * @param activityRepository the repository for activity data
     * @param foodRepository the repository for food data
     * @param foodConsumedRepository the repository for consumed food data
     */
    public DiaryFacade(DiaryRepository diaryRepository,
                       ActivityRepository activityRepository,
                       FoodRepository foodRepository,
                       FoodConsumedRepository foodConsumedRepository) {

        this.diaryRepository = diaryRepository;
        this.activityRepository = activityRepository;
        this.foodRepository = foodRepository;
        this.foodConsumedRepository = foodConsumedRepository;
    }

    // ##### Diary Management Methods #####

    /**
     * Adds food to today's diary.
     * @param foodDto the food to add
     * @param quantity how much food was eaten
     * @param consumptionType when the food was eaten (breakfast, lunch, etc.)
     * @return the ID of the added food entry
     */
    public int addFoodToDiaryToday(FoodDto foodDto, double quantity, ConsumptionType consumptionType) {
        LocalDate today = LocalDate.now();
        return addFoodToDiary(foodDto, quantity, consumptionType, today);
    }

    /**
     * Adds food to the diary on a specific date.
     * @param foodDto the food to add
     * @param quantity how much food was eaten
     * @param consumptionType when the food was eaten
     * @param date the date to add the food to
     * @return the ID of the added food entry
     */
    public int addFoodToDiary(FoodDto foodDto, double quantity, ConsumptionType consumptionType, LocalDate date) {
        try {
            int diaryId = getOrCreateDiaryId(date);
            int foodId = getOrCreateFoodId(foodDto);
            return foodConsumedRepository.save(new FoodConsumedDto(-1, diaryId, foodId, consumptionType, quantity));
        }
        catch (Exception e) {
            throw new RuntimeException("Error adding food to diary: " + e.getMessage(), e);
        }
    }

    /**
     * Adds an activity to today's diary.
     * @param activityDto the activity to add
     * @return the ID of the added activity
     */
    public int addActivityToDiaryToday(ActivityDto activityDto) {
        LocalDate today = LocalDate.now();
        return addActivityToDiary(activityDto, today);
    }

    /**
     * Adds an activity to the diary on a specific date.
     * @param activityDto the activity to add
     * @param date the date to add the activity to
     * @return the ID of the added activity
     */
    public int addActivityToDiary(ActivityDto activityDto, LocalDate date) {
        try {
            int diaryId = getOrCreateDiaryId(date);
            ActivityDto activityToSave = new ActivityDto(-1, activityDto.title(), activityDto.caloriesBurned(), diaryId);
            return activityRepository.save(activityToSave);
        }
        catch (Exception e) {
            throw new RuntimeException("Error adding activity to diary: " + e.getMessage(), e);
        }
    }

    /**
     * Removes food from the diary.
     * @param foodConsumedDto the food entry to remove
     */
    public void removeFoodFromDiary(FoodConsumedDto foodConsumedDto) {
        try {
            foodConsumedRepository.deleteById(foodConsumedDto.foodConsumedId());
        }
        catch (Exception e) {
            throw new RuntimeException("Error removing food from diary: " + e.getMessage(), e);
        }
    }

    /**
     * Removes an activity from the diary.
     * @param activityDto the activity to remove
     */
    public  void removeActivityFromDiary(ActivityDto activityDto) {
        try {
            activityRepository.deleteById(activityDto.activityId());
        }
        catch (Exception e) {
            throw new RuntimeException("Error removing activity from diary: " + e.getMessage(), e);
        }
    }


    // ##### Fetching Methods #####

    /**
     * Gets all foods eaten today.
     * @return a list of foods eaten today
     */
    public List<FoodConsumedDto> getAllFoodsToday() {
        try {
            LocalDate today = LocalDate.now();
            return getFoodsByDiaryId(getOrCreateDiaryId(today));
        }
        catch (Exception e) {
            throw new RuntimeException("Error fetching foods for today: " + e.getMessage(), e);
        }
    }

    /**
     * Gets all activities done today.
     * @return a list of activities done today
     */
    public List<ActivityDto> getAllActivitiesToday() {
        try {
            LocalDate today = LocalDate.now();
            return getActivitiesByDiaryId(getOrCreateDiaryId(today));
        }
        catch (Exception e) {
            throw new RuntimeException("Error fetching activities for today: " + e.getMessage(), e);
        }
    }

    /**
     * Gets all foods from a diary by its ID.
     * @param diaryId the ID of the diary
     * @return a list of foods from the diary
     */
    public List<FoodConsumedDto> getFoodsByDiaryId(int diaryId) {
        try {
            return foodConsumedRepository.findAllFoodsByDiaryId(diaryId);
        }
        catch (Exception e) {
            throw new RuntimeException("Error fetching foods by diary ID: " + e.getMessage(), e);
        }
    }

    /**
     * Gets details about a food.
     * @param foodConsumedDto the food entry
     * @return the details of the food
     */
    public FoodDto getFoodDetails(FoodConsumedDto foodConsumedDto) {
        try {
            Optional<FoodDto> foodOpt = foodRepository.findById(foodConsumedDto.foodId());
            if (foodOpt.isPresent()) {
                return foodOpt.get();
            }
            else {
                throw new IllegalArgumentException("Food with ID " + foodConsumedDto.foodId() + " not found.");
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Error fetching food details: " + e.getMessage(), e);
        }
    }

    /**
     * Gets all activities from a diary by its ID.
     * @param diaryId the ID of the diary
     * @return a list of activities from the diary
     */
    public List<ActivityDto> getActivitiesByDiaryId(int diaryId) {
        try {
            return activityRepository.findAllActivitiesByDiaryId(diaryId);
        }
        catch (Exception e) {
            throw new RuntimeException("Error fetching activities by diary ID: " + e.getMessage(), e);
        }
    }

    /**
     * Gets the user who is logged in now.
     * @return the current user
     */
    public UserDto getCurrentUser() {
        SessionService sessionService = SessionService.getInstance();
        if (!sessionService.isUserLoggedIn()) {
            throw new IllegalStateException("No user is currently logged in.");
        }
        return sessionService.getCurrentUser();
    }

    /**
     * Gets the progress of nutritional values for today.
     * Shows how much nutrients were consumed and how much is needed.
     * @return a map with nutritional progress information
     */
    public Map<String, Map<String, Double>> getProgressOfNutritionalValues() {
        try {
            NutrientGoalGauge[] gauges = calculateConsumedNutrients();
            double caloriesburned = calculateCaloriesBurnedToday();
            gauges[0].caloriesBurned(caloriesburned); // calories gauge

            Map<String, Double> nutritionalNeed = calculateNutritionalNeeds();

            return buildProgressMap(gauges, nutritionalNeed);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching progress of nutritional values: " + e.getMessage(), e);
        }
    }

    // ##### Helper Methods #####

    /**
     * Gets the food ID or creates a new food if it doesn't exist.
     * @param foodDto the food to find or create
     * @return the ID of the food
     */
    private int getOrCreateFoodId(FoodDto foodDto) {
        try {
            Optional<FoodDto> foodOpt = foodRepository.findByEanCode(foodDto.ean_code());
            if (foodOpt.isPresent()) {
                return foodOpt.get().foodId();
            }
            else {
                return foodRepository.save(foodDto);
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Error getting or creating food: " + e.getMessage(), e);
        }
    }

    /**
     * Gets the diary ID for a date or creates a new diary if it doesn't exist.
     * @param date the date for the diary
     * @return the ID of the diary
     */
    private int getOrCreateDiaryId(LocalDate date) {
        try {
            Optional<DiaryDto> diaryOpt = diaryRepository.findByUserIdAndDate(getCurrentUser().userId(), date);
            if (diaryOpt.isPresent()) {
                return diaryOpt.get().diaryId();
            }
            else {
                DiaryDto newDiary = new DiaryDto(-1, getCurrentUser().userId(), date, getCurrentUser().weight());
                return diaryRepository.save(newDiary);
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Error getting or creating diary: " + e.getMessage(), e);
        }
    }

    /**
     * Calculates how much nutrients were consumed today.
     * @return an array of nutrient gauges with consumed values
     */
    private NutrientGoalGauge[] calculateConsumedNutrients() {
        String[] nutrients = {"calories", "proteins", "fats", "carbohydrates"};
        NutrientGoalGauge[] gauges = {
                new NutrientGoalGauge(), new NutrientGoalGauge(),
                new NutrientGoalGauge(), new NutrientGoalGauge()
        };
        for (FoodConsumedDto food : getAllFoodsToday()) {
            Map<String, Double> nutritionalValues = NutritionalValuesService.calculateNutritionalValues(food, getFoodDetails(food));
            for (int i = 0; i < nutrients.length; i++) {
                gauges[i].addConsumedValue(nutritionalValues.get(nutrients[i]));
            }
        }
        return gauges;
    }

    /**
     * Calculates the nutritional needs for the current user.
     * @return a map with the needed amounts of nutrients
     */
    private Map<String, Double> calculateNutritionalNeeds() {
        UserDto currentUser = getCurrentUser();
        GoalType goalType = determineGoalType(currentUser);
        return NutritionalNeedsService.calculateNutritionalNeeds(currentUser, goalType);
    }

    /**
     * Creates a progress map with consumed and needed nutrients.
     * @param gauges the nutrient gauges with consumed values
     * @param nutritionalNeed the needed amounts of nutrients
     * @return a map with progress information
     */
    private Map<String, Map<String, Double>> buildProgressMap(NutrientGoalGauge[] gauges, Map<String, Double> nutritionalNeed) {
        String[] nutrients = {"calories", "proteins", "fats", "carbohydrates"};
        Map<String, Map<String, Double>> progressMap = new HashMap<>();
        for (int i = 0; i < nutrients.length; i++) {
            gauges[i].setGoalValue(nutritionalNeed.get(nutrients[i]));
            progressMap.put(nutrients[i], gauges[i].getDetailsMap());
        }
        return progressMap;
    }

    /**
     * Calculates how many calories were burned today.
     * @return the total calories burned today
     */
    private double calculateCaloriesBurnedToday() {
        double caloriesBurned = 0;

        for (ActivityDto activity : getAllActivitiesToday()) {
            caloriesBurned += activity.caloriesBurned();
        }
        return caloriesBurned;
    }

    /**
     * Determines what the user's goal is based on their current and goal weight.
     * @param userDto the user information
     * @return the type of goal (lose, gain, or maintain weight)
     */
    private GoalType determineGoalType(UserDto userDto) {
        if (userDto.goalWeight() < userDto.weight()) {
            return GoalType.LOSE_WEIGHT;
        } else if (userDto.goalWeight() > userDto.weight()) {
            return GoalType.GAIN_WEIGHT;
        } else {
            return GoalType.MAINTAIN_WEIGHT;
        }
    }
}
