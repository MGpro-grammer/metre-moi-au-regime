package be.esi.prj.dto;

/**
 * Data Transfer Object (DTO) representing a food item.
 * Immutable record class used for transferring food data between application layers.
 * Contains comprehensive nutritional information about a food item, including identification,
 * descriptive properties, and detailed macronutrient composition.
 *
 * @param foodId           Unique identifier for the food item
 * @param name             Name or description of the food
 * @param ean_code         European Article Number code for product identification
 * @param unit             Unit of measurement for the food (e.g., g, ml)
 * @param calories         Caloric content per standard serving
 * @param proteins         Protein content per standard serving
 * @param fats             Fat content per standard serving
 * @param carbohydrates    Carbohydrate content per standard serving
 * @param quantityServing  Standard serving size quantity
 * @param quantityUnit     Quantity of the unit (typically in grams or milliliters)
 */
public record FoodDto(
        Integer foodId,
        String name,
        String ean_code,
        String unit,
        Double calories,
        Double proteins,
        Double fats,
        Double carbohydrates,
        Double quantityServing,
        Double quantityUnit
)
{}
