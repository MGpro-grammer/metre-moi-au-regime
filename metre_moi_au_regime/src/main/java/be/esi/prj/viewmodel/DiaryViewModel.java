package be.esi.prj.viewmodel;

import be.esi.prj.dto.ActivityDto;
import be.esi.prj.dto.FoodConsumedDto;
import be.esi.prj.model.DiaryFacade;
import be.esi.prj.model.FoodFacade;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * This view model manages the daily diary screen.
 * It handles food and activity entries for the user's daily tracking.
 */
public class DiaryViewModel {
    private final DiaryFacade diaryFacade;
    private final FoodFacade foodFacade;

    private final ObservableList<ActivityViewModel> activities;
    private final ObservableList<FoodConsumedViewModel> foods;

    private final StringProperty title;
    private final DoubleProperty caloriesBurned;

    private final StringProperty diaryStatusMessage;

    private final ObjectProperty<Object> selectedItem;

    /**
     * Creates a new DiaryViewModel with diary and food services.
     * Sets up all the lists and properties needed for the diary screen.
     * @param diaryFacade the service to manage diary entries
     * @param foodFacade the service to handle food operations
     */
    public DiaryViewModel(DiaryFacade diaryFacade, FoodFacade foodFacade) {
        this.diaryFacade = diaryFacade;
        this.foodFacade = foodFacade;

        this.activities = FXCollections.observableArrayList();
        this.foods = FXCollections.observableArrayList();

        this.title = new SimpleStringProperty("");
        this.caloriesBurned = new SimpleDoubleProperty(0.0);

        this.diaryStatusMessage = new SimpleStringProperty("");

        this.selectedItem = new SimpleObjectProperty<>(null);

    }

    /**
     * Loads both food and activity lists from the diary.
     * Updates the display with all entries for today.
     */
    public void loadLists() {
        loadFoods();
        loadActivities();
    }

    /**
     * Adds a new activity to today's diary.
     * Checks if the title and calories burned are valid before adding.
     * Updates the activity list after successful addition.
     */
    public void addActivity() {
        try {
            if (title.get() == null || title.get().isBlank()) {
                diaryStatusMessage.set("Title cannot be empty.");
                return;
            }
            if (caloriesBurned.get() <= 0) {
                diaryStatusMessage.set("Calories burned must be a positive number.");
                return;
            }
            ActivityDto activityDto = new ActivityDto(-1, title.get(), caloriesBurned.get(), -1);
            diaryFacade.addActivityToDiaryToday(activityDto);
            loadLists();
            diaryStatusMessage.set("Activity added successfully.");
        }
        catch (Exception e) {
            diaryStatusMessage.set("Error adding activity: " + e.getMessage());
        }
    }

    /**
     * Removes the currently selected item from the diary.
     * Can remove either food or activity entries.
     * Updates the display after successful removal.
     */
    public void removeItem() {
        try {
            Object item = selectedItem.get();
            if (item instanceof FoodConsumedViewModel foodItem) {
                removeFood(foodItem);
            } else if (item instanceof ActivityViewModel activityItem) {
                removeActivity(activityItem);
            } else {
                diaryStatusMessage.set("Unknown item type.");
            }
            selectedItem.set(null);
        } catch (Exception e) {
            diaryStatusMessage.set("Error removing item: " + e.getMessage());
        }
    }

    /**
     * Removes a specific food entry from the diary.
     * Deletes the food from the database and updates the food list.
     * @param foodItem the food entry to remove
     */
    private void removeFood(FoodConsumedViewModel foodItem) {
        diaryFacade.removeFoodFromDiary(foodItem.foodConsumedDto());
        foods.remove(foodItem);
        diaryStatusMessage.set("Food removed successfully.");
    }

    /**
     * Removes a specific activity from the diary.
     * Deletes the activity from the database and updates the activity list.
     * @param activityItem the activity to remove
     */
    private void removeActivity(ActivityViewModel activityItem) {
        diaryFacade.removeActivityFromDiary(activityItem.activityDto());
        activities.remove(activityItem);
        diaryStatusMessage.set("Activity removed successfully.");
    }

    /**
     * Loads all food entries for today from the diary.
     * Gets food details and nutritional values for each entry.
     * Updates the food list for display.
     */
    public void loadFoods() {
        try {
            foods.clear();
            List<FoodConsumedDto> listOfFoods = diaryFacade.getAllFoodsToday();
            for (FoodConsumedDto food : listOfFoods) {
                foods.add(new FoodConsumedViewModel(food, diaryFacade.getFoodDetails(food), foodFacade.calculateNutritionalValues(food)));
            }
        }
        catch (Exception e) {
            diaryStatusMessage.set("Error loading foods: " + e.getMessage());
        }
    }

    /**
     * Loads all activities for today from the diary.
     * Updates the activity list for display.
     */
    public void loadActivities() {
        try {
            activities.clear();
            List<ActivityDto> listOfActivities = diaryFacade.getAllActivitiesToday();
            for (ActivityDto activity : listOfActivities) {
                activities.add(new ActivityViewModel(activity));
            }
        } catch (Exception e) {
            diaryStatusMessage.set("Error loading activities: " + e.getMessage());
        }
    }

    // ##### Setters and Getters Methods #####

    /**
     * Sets the title for a new activity.
     * Clears empty or whitespace-only titles.
     * @param title the name of the activity
     */
    public void setTitle(String title) {
        if (title == null || title.matches("\\s*")) {
            this.title.set("");
        } else {
            this.title.set(title);
        }
    }

    /**
     * Sets the calories burned for a new activity.
     * Ensures the value is not negative.
     * @param caloriesBurned the number of calories burned during the activity
     */
    public void setCaloriesBurned(double caloriesBurned) {
        if (caloriesBurned < 0) {
            this.caloriesBurned.set(0.0);
        } else {
            this.caloriesBurned.set(caloriesBurned);
        }
    }

    /**
     * Sets the currently selected item in the diary list.
     * Can be either a food entry or an activity.
     * @param selectedItem the item selected by the user
     */
    public void setSelectedItem(Object selectedItem) {
        if (selectedItem == null) {
            this.selectedItem.set(null);
        } else if (selectedItem instanceof FoodConsumedViewModel || selectedItem instanceof ActivityViewModel) {
            this.selectedItem.set(selectedItem);
        } else {
            throw new IllegalArgumentException("Selected item must be of type FoodConsumedViewModel or ActivityViewModel.");
        }
    }

    /**
     * Gets the list of food entries for display.
     * @return the observable list of consumed foods
     */
    public ObservableList<FoodConsumedViewModel> getFoods() {
        return foods;
    }

    /**
     * Gets the list of activities for display.
     * @return the observable list of activities
     */
    public ObservableList<ActivityViewModel> getActivities() {
        return activities;
    }

    /**
     * Gets the current status message for the diary screen.
     * @return the message showing success or error information
     */
    public String getDiaryStatusMessage() {
        return diaryStatusMessage.get();
    }
}
