package be.esi.prj.viewmodel;

import be.esi.prj.dto.ActivityDto;

/**
 * This view model represents an activity for the user interface.
 * It wraps activity data and provides easy access to activity information.
 */
public record ActivityViewModel(ActivityDto activityDto) {

    /**
     * Gets the name of the activity.
     * @return the title or name of the activity
     */
    public String getTitle() {
        return activityDto.title();
    }

    /**
     * Gets how many calories were burned doing this activity.
     * @return the number of calories burned during the activity
     */
    public double getCaloriesBurned() {
        return activityDto.caloriesBurned();
    }

    /**
     * Creates a text description of the activity.
     * Shows the activity name and calories burned in a readable format.
     * If there is no title, it shows "An activity" instead.
     * @return a text description of the activity with calories burned
     */
    @Override
    public String toString() {
        if (getTitle() == null || getTitle().matches("\\s*")) {
            return "An activity : " + getCaloriesBurned() + " calories burned";
        }
        return getTitle() + " : " + getCaloriesBurned() + " calories burned";
    }
}
