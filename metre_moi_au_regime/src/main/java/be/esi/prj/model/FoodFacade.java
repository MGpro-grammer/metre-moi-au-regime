package be.esi.prj.model;

import be.esi.prj.dto.FoodConsumedDto;
import be.esi.prj.dto.FoodDto;
import be.esi.prj.repository.FoodRepository;
import be.esi.prj.service.NutritionalValuesService;
import be.esi.prj.service.OcrScannerService;
import be.esi.prj.service.OpenFoodFactsService;

import java.io.File;
import java.util.*;

/**
 * This class helps to find and manage food information.
 * It can search for food using codes, images, or names.
 */
public class FoodFacade {
    private final FoodRepository foodRepository;

    /**
     * Creates a new FoodFacade with a food repository.
     * @param foodRepository the repository to store and find food data
     */
    public FoodFacade(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }

    // ##### Food Search Methods #####

    /**
     * Finds food using its EAN code (barcode number).
     * First looks in the local database, then in OpenFoodFacts if not found.
     * @param code the EAN code to search for
     * @return the food information
     */
    public FoodDto findFoodByEanCode(String code) {

        Optional<FoodDto> foodOpt = getFoodInRepository(code);
        if (foodOpt.isEmpty()) {
            foodOpt = getFoodInOpenFoodFacts(code);
            if (foodOpt.isEmpty()) {
                throw new IllegalArgumentException("Food with EAN code " + code + " not found in repository or OpenFoodFacts.");
            }
        }
        return foodOpt.get();
    }

    /**
     * Finds food by scanning an image with a barcode.
     * Uses OCR to read the EAN code from the image.
     * @param image the image file with the barcode
     * @return the food information
     */
    public FoodDto findFoodByImage(File image) {
        try {
            String eanCode = OcrScannerService.getInstance().getEanCode(image);
            return findFoodByEanCode(eanCode);
        }
        catch (Exception e) {
            throw new RuntimeException("Error processing image for EAN code: " + e.getMessage(), e);
        }
    }

    /**
     * Finds food by name without using threads (slower but simpler).
     * Searches through 10 pages of results from OpenFoodFacts.
     * @param name the name of the food to search for
     * @return a list of foods that match the name
     */
    public List<FoodDto> findFoodByNameNoSync(String name) {
        try {
            List<FoodDto> foodList = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                List<FoodDto> foodListPerPage = OpenFoodFactsService.findFoodByName(name, 100, i);
                foodList.addAll(foodListPerPage);
            }
            return foodList;
        }
        catch (Exception e) {
            throw new RuntimeException("Error fetching food by name: " + name, e);
        }
    }

    /**
     * Finds food by name using threads (faster).
     * Searches through 10 pages at the same time using multiple threads.
     * @param name the name of the food to search for
     * @return a list of foods that match the name
     */
    public List<FoodDto> findFoodByName(String name) {
        List<FoodDto> finalResults = Collections.synchronizedList(new ArrayList<>());
        List<Thread> threads = createAndStartThreads(name, finalResults);
        waitForThreads(threads);
        return finalResults;
    }

    // ##### Nutritional Values Calculation #####

    /**
     * Calculates the nutritional values for a consumed food.
     * Takes into account the quantity that was eaten.
     * @param foodConsumedDto the food that was consumed with its quantity
     * @return a map with nutritional values (calories, proteins, fats, etc.)
     */
    public Map<String, Double> calculateNutritionalValues(FoodConsumedDto foodConsumedDto) {
        Optional<FoodDto> foodOpt = foodRepository.findById(foodConsumedDto.foodId());
        if (foodOpt.isEmpty()) {
            throw new IllegalArgumentException("Food with ID " + foodConsumedDto.foodId() + " not found.");
        }
        FoodDto foodDto = foodOpt.get();

        return NutritionalValuesService.calculateNutritionalValues(foodConsumedDto, foodDto);
    }

    // ##### Helper Methods #####

    /**
     * Creates and starts threads to search for food by name.
     * Each thread searches one page of results.
     * @param name the food name to search for
     * @param finalResults the list to store all results
     * @return a list of threads that are running the searches
     */
    private List<Thread> createAndStartThreads(String name, List<FoodDto> finalResults) {
        List<Thread> threads = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            int pageNumber = i;
            Thread thread = new Thread(() -> {
                try {
                    List<FoodDto> pageResult = OpenFoodFactsService.findFoodByName(name, 100, pageNumber);
                    finalResults.addAll(pageResult);
                } catch (Exception e) {
                    System.err.println("Error for page " + pageNumber + ": " + e.getMessage());
                }
            });
            threads.add(thread);
            thread.start();
        }
        return threads;
    }

    /**
     * Waits for all threads to finish their work.
     * @param threads the list of threads to wait for
     */
    private void waitForThreads(List<Thread> threads) {
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread interrupted while waiting for results: ", e);
            }
        }
    }

    /**
     * Searches for food in the local repository using EAN code.
     * @param code the EAN code to search for
     * @return the food if found, or empty if not found
     */
    private Optional<FoodDto> getFoodInRepository(String code) {
        try {
            return foodRepository.findByEanCode(code);
        }
        catch (Exception e) {
            throw new RuntimeException("Error fetching food from repository: " + code, e);
        }
    }

    /**
     * Searches for food in OpenFoodFacts using EAN code.
     * @param code the EAN code to search for
     * @return the food if found, or empty if not found
     */
    private Optional<FoodDto> getFoodInOpenFoodFacts(String code) {
        try {
            return OpenFoodFactsService.findFoodByEanCode(code);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching food from OpenFoodFacts: " + code, e);
        }
    }
}
