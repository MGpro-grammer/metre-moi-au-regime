package be.esi.prj.viewmodel;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Map;

/**
 * This view model represents a nutritional goal for the user interface.
 * It shows progress towards daily goals for nutrients like calories, proteins, fats, and carbohydrates.
 * Displays the goal, consumed amount, percentage, and angle for visual gauges.
 */
public class NutrientGoalViewModel {
    private final StringProperty nutrientName;
    private final StringProperty unit;

    private final DoubleProperty gaugeAngle;
    private final DoubleProperty percentage;
    private final DoubleProperty goalValue;
    private final DoubleProperty consumedValue;

    /**
     * Creates a new NutrientGoalViewModel for a specific nutrient.
     * Sets up the name and unit for the nutrient, with all values starting at zero.
     * @param nutrientName the name of the nutrient (like "Calories" or "Proteins")
     * @param unit the unit of measurement (like "kcal" for calories or "g" for grams)
     */
    public NutrientGoalViewModel(String nutrientName, String unit) {
        this.nutrientName = new SimpleStringProperty(nutrientName);
        this.unit = new SimpleStringProperty(unit);

        this.gaugeAngle = new SimpleDoubleProperty(0.0);
        this.percentage = new SimpleDoubleProperty(0.0);
        this.goalValue = new SimpleDoubleProperty(0.0);
        this.consumedValue = new SimpleDoubleProperty(0.0);
    }

    /**
     * Updates all the progress details for this nutrient.
     * Takes information about goal, consumed amount, percentage, and angle from a map.
     * Only updates values that exist in the details map.
     * @param details a map containing progress information for this nutrient
     */
    public void setDetails(Map<String, Double> details) {
        if (details.containsKey("percentage")) {
            this.percentage.set(details.get("percentage"));
        }
        if (details.containsKey("gaugeAngle")) {
            this.gaugeAngle.set(details.get("gaugeAngle"));
        }
        if (details.containsKey("goalValue")) {
            this.goalValue.set(details.get("goalValue"));
        }
        if (details.containsKey("consumedValue")) {
            this.consumedValue.set(details.get("consumedValue"));
        }
    }

    // ##### Getters Methods #####

    /**
     * Gets the name of the nutrient.
     * @return the name of the nutrient (like "Calories" or "Proteins")
     */
    public String getNutrientName() {
        return nutrientName.get();
    }

    /**
     * Gets the angle for drawing a progress gauge.
     * Used by the user interface to show progress in a circular gauge.
     * @return the angle in degrees for the progress gauge
     */
    public Double getGaugeAngle() {
        return gaugeAngle.get();
    }

    /**
     * Gets the progress as a percentage text.
     * Shows how much of the daily goal has been reached.
     * @return the percentage with "%" symbol (like "75%")
     */
    public String getProgessPercentage() {
        return String.format("%.0f %%", percentage.get());
    }

    /**
     * Gets the consumed amount with its unit.
     * Shows how much of this nutrient was consumed today.
     * @return the consumed value with unit (like "150 kcal" or "25 g")
     */
    public String getConsumedValue() {
        return String.format("%.0f %s", consumedValue.get(), unit.get());
    }

    /**
     * Gets the daily goal amount with its unit.
     * Shows how much of this nutrient should be consumed per day.
     * @return the goal value with unit (like "2000 kcal" or "50 g")
     */
    public String getGoalValue() {
        return String.format("%.0f %s", goalValue.get(), unit.get());
    }
}
