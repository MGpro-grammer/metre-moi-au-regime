package be.esi.prj.service;

import be.esi.prj.dto.FoodConsumedDto;
import be.esi.prj.dto.FoodDto;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This service calculates nutritional values for consumed food.
 * It works out calories, proteins, fats and carbohydrates based on how the food was eaten.
 */
public class NutritionalValuesService {

    /**
     * Calculates nutritional values for food that was consumed.
     * Uses different methods based on how the food was measured (percent, quantity, serving, unit).
     * @param foodConsumedDto information about how much food was eaten
     * @param foodDto the food's nutritional information
     * @return a map with calories, proteins, fats and carbohydrates consumed
     */
    public static Map<String, Double> calculateNutritionalValues(FoodConsumedDto foodConsumedDto, FoodDto foodDto) {
        return switch (foodConsumedDto.consumptionType()) {
            case PER_PERCENT -> consumedPerPercent(foodDto, foodConsumedDto.quantity());
            case PER_QUANTITY -> consumedPerQuantity(foodDto, foodConsumedDto.quantity());
            case PER_SERVING -> consumedPerServing(foodDto, foodConsumedDto.quantity());
            case PER_UNIT -> consumedPerUnit(foodDto, foodConsumedDto.quantity());
        };
    }

    // ##### Helper methods #####

    /**
     * Calculates nutrition when food is measured as a percentage.
     * For example, if someone ate 50% of a food item.
     * @param foodDto the food's nutritional information
     * @param percent the percentage of food eaten (like 50 for 50%)
     * @return nutritional values for the percentage eaten
     */
    private static Map<String, Double> consumedPerPercent(FoodDto foodDto, Double percent) {
        Map<String, Double> valuesPerUnit = getNutritionalValuesPerUnit(foodDto);
        return scaleNutritionalValues(valuesPerUnit, percent / 100.0);
    }

    /**
     * Calculates nutrition when food is measured by weight or volume.
     * For example, 150 grams of food or 200 ml of drink.
     * @param foodDto the food's nutritional information
     * @param quantity how much food was eaten (in grams or ml)
     * @return nutritional values for the quantity eaten
     */
    private static Map<String, Double> consumedPerQuantity(FoodDto foodDto, Double quantity) {
        Map<String, Double> nutritionalValues = new HashMap<>();
        nutritionalValues.put("calories", foodDto.calories() / 100 * quantity);
        nutritionalValues.put("proteins", foodDto.proteins() / 100 * quantity);
        nutritionalValues.put("fats", foodDto.fats() / 100 * quantity);
        nutritionalValues.put("carbohydrates", foodDto.carbohydrates() / 100 * quantity);
        return nutritionalValues;
    }

    /**
     * Calculates nutrition when food is measured in servings.
     * For example, 2 servings of cereal or 1.5 servings of rice.
     * @param foodDto the food's nutritional information
     * @param quantity how many servings were eaten
     * @return nutritional values for the servings eaten
     */
    private static Map<String, Double> consumedPerServing(FoodDto foodDto, Double quantity) {
        Map<String, Double> valuesPerServing = getNutritionalValuesPerServing(foodDto);
        return scaleNutritionalValues(valuesPerServing, quantity);
    }

    /**
     * Calculates nutrition when food is measured in units.
     * For example, 3 apples or 2 slices of bread.
     * @param foodDto the food's nutritional information
     * @param quantity how many units were eaten
     * @return nutritional values for the units eaten
     */
    private static Map<String, Double> consumedPerUnit(FoodDto foodDto, Double quantity) {
        Map<String, Double> valuesPerUnit = getNutritionalValuesPerUnit(foodDto);
        return scaleNutritionalValues(valuesPerUnit, quantity);
    }

    /**
     * Multiplies all nutritional values by a number.
     * Used to scale nutrition based on how much was eaten.
     * @param nutritionalValues the original nutritional values
     * @param factor the number to multiply by
     * @return the scaled nutritional values
     */
    private static Map<String, Double> scaleNutritionalValues(Map<String, Double> nutritionalValues, Double factor) {
        nutritionalValues.replaceAll((key, value) -> value * factor);
        return nutritionalValues;
    }

    /**
     * Gets nutritional values for one unit of food.
     * Uses the unit quantity defined in the food data.
     * @param foodDto the food's information
     * @return nutritional values for one unit
     */
    private static Map<String, Double> getNutritionalValuesPerUnit(FoodDto foodDto) {
        double quantity = foodDto.quantityUnit();
        return consumedPerQuantity(foodDto, quantity);
    }

    /**
     * Gets nutritional values for one serving of food.
     * Uses the serving size defined in the food data.
     * @param foodDto the food's information
     * @return nutritional values for one serving
     */
    private static Map<String, Double> getNutritionalValuesPerServing(FoodDto foodDto) {
        double quantity = foodDto.quantityServing();
        return consumedPerQuantity(foodDto, quantity);
    }
}
