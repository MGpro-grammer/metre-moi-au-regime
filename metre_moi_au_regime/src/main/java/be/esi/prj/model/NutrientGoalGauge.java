package be.esi.prj.model;

import java.util.HashMap;
import java.util.Map;

/**
 * This class tracks progress towards nutritional goals.
 * It shows how much food was eaten compared to the daily goal.
 */
public class NutrientGoalGauge {
    private double gaugeAngle;
    private double percentage;
    private double goalValue;
    private double consumedValue;

    /**
     * Creates a new NutrientGoalGauge with zero values.
     * All values start at 0.0.
     */
    protected NutrientGoalGauge() {
        this.gaugeAngle = 0.0;
        this.percentage = 0.0;
        this.goalValue = 0.0;
        this.consumedValue = 0.0;
    }

    /**
     * Adds more consumed value to the current total.
     * Updates the progress after adding the value.
     * @param consumedValue the amount to add to consumed food
     */
    protected void addConsumedValue(double consumedValue) {
        this.consumedValue += consumedValue;
        updateProgress();
    }

    /**
     * Updates the percentage and gauge angle based on current values.
     * Calculates how much of the goal has been reached.
     */
    private void updateProgress() {
        if (goalValue > 0) {
            this.percentage = (consumedValue / goalValue) * 100;
            this.gaugeAngle = percentage / 100 * 360;
        }
        else {
            this.percentage = 0.0;
            this.gaugeAngle = 0.0;
        }
    }

    /**
     * Removes calories burned from the consumed value.
     * Used for calories to subtract exercise calories.
     * @param caloriesBurned the amount of calories burned through exercise
     */
    protected void caloriesBurned(double caloriesBurned) {
        this.consumedValue -= caloriesBurned;
        updateProgress();
    }

    /**
     * Sets the daily goal for this nutrient.
     * Updates the progress after setting the new goal.
     * @param goalValue the target amount for this nutrient
     */
    protected void setGoalValue(double goalValue) {
        this.goalValue = goalValue;
        updateProgress();
    }

    /**
     * Gets all the details about this nutrient progress.
     * Returns information about angle, percentage, goal and consumed values.
     * @return a map with all progress details
     */
    protected Map<String, Double> getDetailsMap() {
        Map<String, Double> details = new HashMap<>();
        details.put("gaugeAngle", gaugeAngle);
        details.put("percentage", percentage);
        details.put("goalValue", goalValue);
        details.put("consumedValue", consumedValue);
        return details;
    }
}
