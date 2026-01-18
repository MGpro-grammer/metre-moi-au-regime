package be.esi.prj.viewmodel;

import be.esi.prj.dto.FoodConsumedDto;
import be.esi.prj.dto.FoodDto;
import be.esi.prj.enumeration.ConsumptionType;

import java.util.Map;
import java.util.Objects;

/**
 * This view model represents consumed food for the user interface.
 * It combines food information, consumption details, and nutritional values in one place.
 */
public record FoodConsumedViewModel(FoodConsumedDto foodConsumedDto, FoodDto foodDto, Map<String, Double> nutritionalValues) {

    /**
     * Gets the name of the food.
     * Returns "Unknown food" if the name is empty or null.
     * @return the name of the food, or "Unknown food" if no name is available
     */
    public String getName() {
        if (Objects.equals(foodDto.name(), "")) {
            return "Unknown food";
        }
        return foodDto.name();
    }

    /**
     * Gets the nutritional values as a formatted text.
     * Shows calories, proteins, carbohydrates, and fats in a readable format.
     * @return a text description of the nutritional values
     */
    public String getNutritionalValues() {
        return String.format("%.2f", nutritionalValues.getOrDefault("calories", 0.0)) + "kcal, " +
                String.format("%.2f", nutritionalValues.getOrDefault("proteins", 0.0)) + "g proteins, " +
                String.format("%.2f", nutritionalValues.getOrDefault("carbohydrates", 0.0)) + "g carbohydrates, " +
                String.format("%.2f", nutritionalValues.getOrDefault("fats", 0.0)) + "g fats";
    }

    /**
     * Gets how much of the food was consumed.
     * The meaning depends on the consumption type (percentage, grams, servings, or units).
     * @return the quantity of food consumed
     */
    public Double getQuantity() {
        return foodConsumedDto.quantity();
    }

    /**
     * Creates a complete text description of the consumed food.
     * Shows the food name, quantity with the right unit, and nutritional values.
     * The format changes based on how the food was measured.
     * @return a detailed description of the consumed food
     */
    @Override
    public String toString() {
        ConsumptionType consumptionType = foodConsumedDto.consumptionType();

        switch (consumptionType) {
            case PER_PERCENT -> {;
                return String.format("%s consumed at %.2f%%: %s", getName(), getQuantity(), getNutritionalValues());
            }
            case PER_QUANTITY -> {
                return String.format("%s consumed at %.2f grams: %s", getName(), getQuantity(), getNutritionalValues());
            }
            case PER_SERVING -> {
                return String.format("%s consumed at %.2f servings: %s", getName(), getQuantity(), getNutritionalValues());
            }
            case PER_UNIT -> {
                return String.format("%s consumed at %.2f units: %s", getName(), getQuantity(), getNutritionalValues());
            }
            default -> throw new IllegalArgumentException("Unknown consumption type: " + consumptionType);
        }
    }
}
