package be.esi.prj.viewmodel;

import be.esi.prj.model.DiaryFacade;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Map;

/**
 * This view model manages the main dashboard screen.
 * It shows nutritional goals and progress for calories, proteins, fats and carbohydrates.
 */
public class DashBoardViewModel {
    private final DiaryFacade diaryFacade;

    private final ObservableList<NutrientGoalViewModel> nutrientGoals;
    private final StringProperty DashboardStatusMessage;

    /**
     * Creates a new DashBoardViewModel with a diary facade.
     * Sets up the lists and properties needed for the dashboard screen.
     * @param diaryFacade the service to get nutritional progress information
     */
    public  DashBoardViewModel(DiaryFacade diaryFacade) {
        this.diaryFacade = diaryFacade;

        this.nutrientGoals = FXCollections.observableArrayList();
        this.DashboardStatusMessage = new SimpleStringProperty("");
    }

    /**
     * Loads the nutritional goals and progress from the diary.
     * Gets information about calories, proteins, carbohydrates and fats.
     * Shows how much the user has consumed compared to their daily goals.
     */
    public void loadNutrientGoals() {
        try {
            nutrientGoals.clear();
            Map<String, Map<String, Double>> progression = diaryFacade.getProgressOfNutritionalValues();

            NutrientGoalViewModel calorieGoal = new NutrientGoalViewModel("Calories", "kcal");
            NutrientGoalViewModel proteinGoal = new NutrientGoalViewModel("Proteins", "g");
            NutrientGoalViewModel carbGoal = new NutrientGoalViewModel("Carbohydrates", "g");
            NutrientGoalViewModel fatGoal = new NutrientGoalViewModel("Fats", "g");

            calorieGoal.setDetails(progression.get("calories"));
            proteinGoal.setDetails(progression.get("proteins"));
            carbGoal.setDetails(progression.get("carbohydrates"));
            fatGoal.setDetails(progression.get("fats"));

            nutrientGoals.addAll(calorieGoal, proteinGoal, carbGoal, fatGoal);
        } catch (Exception e) {
            DashboardStatusMessage.set("Failed to load nutrient goals: " + e.getMessage());
        }
    }

    // ##### Getters Methods #####

    /**
     * Gets the list of nutritional goals to display on the dashboard.
     * Contains progress information for calories, proteins, carbohydrates and fats.
     * @return a list of nutrient goal view models for the user interface
     */
    public ObservableList<NutrientGoalViewModel> getNutrientGoals() {
        return nutrientGoals;
    }
}
