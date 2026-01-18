package be.esi.prj.dto;

import be.esi.prj.enumeration.ConsumptionType;

/**
 * Data Transfer Object (DTO) representing a food consumption record.
 * Immutable record class used for transferring food consumption data between application layers.
 * Contains essential information about a specific food item consumed by a user, including
 * identification, diary reference, food reference, consumption type, and quantity.
 *
 * @param foodConsumedId  Unique identifier for the food consumption record
 * @param diaryId         Identifier of the diary entry this consumption belongs to
 * @param foodId          Identifier of the consumed food item
 * @param consumptionType Type of consumption (e.g., BREAKFAST, LUNCH, DINNER, SNACK)
 * @param quantity        Amount of food consumed (typically in grams or servings)
 */
public record FoodConsumedDto(
        Integer foodConsumedId,
        Integer diaryId,
        Integer foodId,
        ConsumptionType consumptionType,
        Double quantity
) {}
