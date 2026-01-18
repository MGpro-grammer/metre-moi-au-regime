package be.esi.prj.viewmodel;

import be.esi.prj.dto.FoodDto;
import be.esi.prj.enumeration.ConsumptionType;
import be.esi.prj.model.DiaryFacade;
import be.esi.prj.model.FoodFacade;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.List;

/**
 * This view model manages the food search screen.
 * It handles searching for food by name, EAN code, or image scanning.
 * Users can add found foods to their daily diary.
 */
public class FoodSearchViewModel {
    private final FoodFacade foodFacade;
    private final DiaryFacade diaryFacade;

    private final ObjectProperty<File> imageFile;
    private final StringProperty searchQuery;
    private final ObservableList<FoodViewModel> searchResults;
    private final ObjectProperty<FoodViewModel> selectedFood;
    private final StringProperty searchStatusMessage;

    private final DoubleProperty quantity;
    private final ObjectProperty<ConsumptionType> type;

    /**
     * Creates a new FoodSearchViewModel with food and diary services.
     * Sets up all the properties needed for the food search screen.
     * @param foodFacade the service to search for food information
     * @param diaryFacade the service to add food to the diary
     */
    public FoodSearchViewModel(FoodFacade foodFacade, DiaryFacade diaryFacade) {
        this.foodFacade = foodFacade;
        this.diaryFacade = diaryFacade;

        this.imageFile = new SimpleObjectProperty<>(null);
        this.searchQuery = new SimpleStringProperty("");
        this.searchResults = FXCollections.observableArrayList();
        this.selectedFood = new SimpleObjectProperty<>(null);
        this.searchStatusMessage = new SimpleStringProperty("");

        this.quantity = new SimpleDoubleProperty(0);
        this.type = new SimpleObjectProperty<>(null);
    }

    /**
     * Scans an image to find food information using OCR.
     * Looks for barcodes in the selected image and finds the matching food.
     * Updates the search results with the found food.
     */
    public void scanFood() {
        if (imageFile.get() == null) {
            searchStatusMessage.set("Please select an image file to scan.");
            return;
        }

        searchResults.clear();

        try {
            FoodDto foodDto = foodFacade.findFoodByImage(imageFile.get());
            if (foodDto != null) {
                searchResults.add(new FoodViewModel(foodDto));
                searchStatusMessage.set("Food found: " + foodDto.name());
            } else {
                searchStatusMessage.set("No food found in the image.");
            }
        } catch (Exception e) {
            searchStatusMessage.set("Error while scanning food: " + e.getMessage());
        }
    }

    /**
     * Searches for food using the entered search query.
     * Can search by EAN code (barcode numbers) or by food name.
     * Updates the search results with all matching foods.
     */
    public void searchFood() {
        if (searchQuery.get() == null || searchQuery.get().isBlank()) {
            return;
        }
        searchResults.clear();

        if (isValidEanCode(searchQuery.get())) {
            toSearchEanCode();
        }
        else {
            toListFood();
        }
    }

    /**
     * Adds the selected food to today's diary.
     * Checks if food is selected and quantity and type are valid before adding.
     * Shows success message when food is added successfully.
     */
    public void addFoodToDiary() {
        try {
            if (selectedFood.get() == null || quantity.get() <= 0 || type.get() == null) {
                searchStatusMessage.set("Please select a food and enter a valid value and type.");
                return;
            }
            FoodDto foodDto = selectedFood.get().foodDto();
            diaryFacade.addFoodToDiaryToday(foodDto, quantity.get(), type.get());
            searchStatusMessage.set("Food added to diary successfully.");
        }
        catch (Exception e) {
            searchStatusMessage.set("Error while adding food to diary: " + e.getMessage());
        }
    }

    /**
     * Checks if the search text is a valid EAN code.
     * EAN codes are 8 or 13 digit numbers used on barcodes.
     * @param code the text to check
     * @return true if the text is a valid EAN code, false otherwise
     */
    private boolean isValidEanCode(String code) {
        return code.matches("\\d{8}|\\d{13}");
    }

    /**
     * Searches for food by name from the online database.
     * Gets a list of foods that match the search query.
     * Adds all matching foods to the search results.
     */
    private void toListFood() {
        try {
            List<FoodDto> list = foodFacade.findFoodByName(searchQuery.get());
            for (FoodDto foodDto : list) {
                FoodViewModel foodViewModel = new FoodViewModel(foodDto);
                searchResults.add(foodViewModel);
            }
        }
        catch (Exception e) {
            searchStatusMessage.set("Error while searching for food: " + e.getMessage());
        }
    }

    /**
     * Searches for food using its EAN code (barcode number).
     * Finds the specific food that matches the barcode.
     * Adds the found food to the search results.
     */
    private void toSearchEanCode() {
        try {
            FoodDto foodDto = foodFacade.findFoodByEanCode(searchQuery.get());
            FoodViewModel foodViewModel = new FoodViewModel(foodDto);
            searchResults.add(foodViewModel);
        }
        catch (IllegalArgumentException e) {
            searchStatusMessage.set("Food with EAN code " + searchQuery.get() + " not found.");
        }
        catch (Exception e) {
            searchStatusMessage.set("Error while searching for food: " + e.getMessage());
        }
    }

    // ##### Getters and Setters Methods #####

    /**
     * Sets the image file to scan for food barcodes.
     * @param file the image file containing a barcode
     */
    public void setImageFile(File file) {
        this.imageFile.set(file);
    }

    /**
     * Sets the text to search for (food name or EAN code).
     * @param query the search text entered by the user
     */
    public void setSearchQuery(String query) {
        this.searchQuery.set(query);
    }

    /**
     * Sets which food the user has selected from the search results.
     * @param food the food selected by the user
     */
    public void setSelectedFood(FoodViewModel food) {
        this.selectedFood.set(food);
    }

    /**
     * Sets how much of the food was consumed.
     * @param value the quantity of food consumed
     */
    public void setQuantity(double value) {
        this.quantity.set(value);
    }

    /**
     * Sets how the food quantity is measured (grams, servings, units, etc.).
     * @param type the way the food quantity is measured
     */
    public void setType(ConsumptionType type) {
        this.type.set(type);
    }

    /**
     * Gets the list of foods found by the search.
     * @return the observable list of search results for display
     */
    public ObservableList<FoodViewModel> getSearchResults() {
        return searchResults;
    }

    /**
     * Gets the current status message for the search screen.
     * @return the message showing search results or error information
     */
    public String getSearchStatusMessage() {
        return searchStatusMessage.get();
    }
}
