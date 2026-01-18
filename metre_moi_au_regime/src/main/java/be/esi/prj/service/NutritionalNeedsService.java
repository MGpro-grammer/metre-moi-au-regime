package be.esi.prj.service;

import be.esi.prj.dto.UserDto;
import be.esi.prj.enumeration.ActivityLevel;
import be.esi.prj.enumeration.Gender;
import be.esi.prj.enumeration.GoalType;

import java.util.HashMap;
import java.util.Map;

/**
 * This service calculates how much food a person needs each day.
 * It works out calories, proteins, fats and carbohydrates based on the user's goals.
 */
public class NutritionalNeedsService {

    private static final double PROTEIN_RATIO = 0.30;
    private static final double FAT_RATIO = 0.25;
    private static final double CARB_RATIO = 0.45;

    private static final int CALORIE_ADJUSTMENT_FOR_GOAL = 500;

    /**
     * Calculates all the nutritional needs for a user.
     * Takes into account their body, activity level and weight goals.
     * @param userDto the user's information (age, weight, height, etc.)
     * @param goalType what the user wants to do (lose, gain, or keep weight)
     * @return a map with daily needs for calories, proteins, fats and carbohydrates
     */
    public static Map<String, Double> calculateNutritionalNeeds(UserDto userDto, GoalType goalType) {
        double basalMetabolicRate = calculateBmr(userDto);
        double activityMultiplier = getActivityMultiplier(userDto.activityLevel());

        double maintenanceCalories = basalMetabolicRate * activityMultiplier;
        double targetCalories = adjustCaloriesForGoal(maintenanceCalories, goalType);

        return calculateMacronutrients(targetCalories);
    }

    // ##### Helper methods #####

    /**
     * Calculates how many calories the body burns at rest.
     * Uses different formulas for men and women.
     * @param userDto the user's information
     * @return the number of calories burned without any activity
     */
    private static double calculateBmr(UserDto userDto) {
        if (userDto.gender() == Gender.MALE) {
            return (10 * userDto.weight()) + (6.25 * userDto.height()) - (5 * userDto.age()) + 5;
        } else { // FEMALE
            return (10 * userDto.weight()) + (6.25 * userDto.height()) - (5 * userDto.age()) - 161;
        }
    }

    /**
     * Gets the number to multiply calories based on how active someone is.
     * More active people need more calories.
     * @param level how active the person is
     * @return a number to multiply the basic calorie needs
     */
    private static double getActivityMultiplier(ActivityLevel level) {
        return switch (level) {
            case LIGHT -> 1.375;
            case MODERATE -> 1.55;
            case ACTIVE -> 1.725;
            case VERY_ACTIVE -> 1.9;
            default -> 1.2;
        };
    }

    /**
     * Changes the calorie amount based on weight goals.
     * Reduces calories to lose weight, adds calories to gain weight.
     * @param maintenanceCalories calories needed to keep current weight
     * @param goal what the user wants to do with their weight
     * @return the target calories for the goal
     */
    private static double adjustCaloriesForGoal(double maintenanceCalories, GoalType goal) {
        return switch (goal) {
            case LOSE_WEIGHT -> maintenanceCalories - CALORIE_ADJUSTMENT_FOR_GOAL;
            case GAIN_WEIGHT -> maintenanceCalories + CALORIE_ADJUSTMENT_FOR_GOAL;
            default -> maintenanceCalories;
        };
    }

    /**
     * Calculates how many grams of proteins, fats and carbohydrates are needed.
     * Converts calories into grams using standard conversion rates.
     * @param targetCalories the total calories needed per day
     * @return a map with calories and grams of each macronutrient
     */
    private static Map<String, Double> calculateMacronutrients(double targetCalories) {
        double proteinsInGrams = (targetCalories * PROTEIN_RATIO) / 4.0;
        double fatsInGrams = (targetCalories * FAT_RATIO) / 9.0;
        double carbsInGrams = (targetCalories * CARB_RATIO) / 4.0;

        Map<String, Double> nutritionalNeeds = new HashMap<>();
        nutritionalNeeds.put("calories", Math.round(targetCalories * 100.0) / 100.0);
        nutritionalNeeds.put("proteins", Math.round(proteinsInGrams * 100.0) / 100.0);
        nutritionalNeeds.put("fats", Math.round(fatsInGrams * 100.0) / 100.0);
        nutritionalNeeds.put("carbohydrates", Math.round(carbsInGrams * 100.0) / 100.0);

        return nutritionalNeeds;
    }
}
