package be.esi.prj.dto;

/**
 * Data Transfer Object (DTO) representing a physical activity.
 * Immutable record class used for transferring activity data between application layers.
 * Contains essential information about a user's physical activity including identification,
 * title, calorie expenditure, and associated diary reference.
 *
 * @param activityId      Unique identifier for the activity
 * @param title           Name or description of the activity
 * @param caloriesBurned  Number of calories burned during this activity
 * @param diaryId         Identifier of the diary this activity belongs to
 */
public record ActivityDto(
        Integer activityId,
        String title,
        Double caloriesBurned,
        Integer diaryId
) {}
