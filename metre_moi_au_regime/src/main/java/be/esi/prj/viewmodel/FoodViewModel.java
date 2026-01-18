package be.esi.prj.viewmodel;

import be.esi.prj.dto.FoodDto;

import java.util.Objects;

/**
 * This view model represents food for the user interface.
 * It wraps food data and provides easy access to food information for display.
 */
public record FoodViewModel(FoodDto foodDto) {

    /**
     * Gets the name of the food.
     * @return the name of the food
     */
    public String getName() {
        return foodDto.name();
    }

    /**
     * Creates a text description of the food.
     * Shows the food name and its unit quantity in a readable format.
     * If there is no name, it shows "Unknown food" instead.
     * @return a text description of the food with its unit information
     */
    @Override
    public String toString() {
        if (Objects.equals(foodDto.name(), "") || foodDto.name() == null) {
            return "Unknown food : " + foodDto.quantityUnit() + foodDto.unit();
        }
        return foodDto.name() + " : " + foodDto.quantityUnit() + foodDto.unit();
    }
}
