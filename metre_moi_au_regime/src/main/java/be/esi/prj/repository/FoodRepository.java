package be.esi.prj.repository;

import be.esi.prj.dto.FoodDto;

import java.sql.Connection;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repository implementation for food items with caching capabilities.
 * Provides CRUD operations for FoodDto entities and maintains an in-memory cache.
 */
public class FoodRepository implements Repository<Integer, FoodDto> {
    private final FoodDao foodDao;
    private final Map<Integer, FoodDto> foodCache;

    /**
     * Creates a default FoodRepository with persistent database connection.
     */
    public FoodRepository() {
        this(false);
    }

    /**
     * Creates a FoodRepository with specified database type.
     *
     * @param memActive If true, uses in-memory database, otherwise uses persistent database
     */
    public FoodRepository(boolean memActive) {
        Connection connection;
        if (memActive) {
            connection = ConnectionManager.getMemConnection();
        } else {
            connection = ConnectionManager.getConnection();
        }
        this.foodDao = new FoodDao(connection);
        this.foodCache = new ConcurrentHashMap<>();
        loadCache();
    }

    /**
     * Creates a FoodRepository with a specific DAO implementation.
     *
     * @param foodDao The DAO to use for data access
     * @throws NullPointerException if foodDao is null
     */
    FoodRepository(FoodDao foodDao) {
        this.foodDao = Objects.requireNonNull(foodDao, "FoodDao is required");
        this.foodCache = new ConcurrentHashMap<>();
        loadCache();
    }

    /**
     * Loads all food items from the database into the cache.
     */
    private void loadCache() {
        foodDao.findAll().forEach(food ->
                foodCache.put(food.foodId(), food));
    }

    /**
     * Finds a food item by its ID, first in cache then in database.
     *
     * @param key The food ID to search for
     * @return An Optional containing the food item if found, empty otherwise
     */
    @Override
    public Optional<FoodDto> findById(Integer key) {
        return Optional.ofNullable(foodCache.get(key))
                .or(() -> foodDao.findById(key));
    }

    /**
     * Finds a food item by its EAN code, first in cache then in database.
     *
     * @param eanCode The EAN code to search for
     * @return An Optional containing the food item if found, empty otherwise
     */
    public Optional<FoodDto> findByEanCode(String eanCode) {
        return foodCache.values().stream()
                .filter(food -> food.ean_code().equals(eanCode))
                .findFirst()
                .or(() -> foodDao.findByEanCode(eanCode));
    }

    /**
     * Retrieves all food items from the cache.
     *
     * @return A list of all food items
     */
    @Override
    public List<FoodDto> findAll() {
        return new ArrayList<>(foodCache.values());
    }

    /**
     * Saves a food item in the database and updates the cache.
     * If the item has a new ID, creates an updated copy with the generated ID.
     *
     * @param item The food item to save
     * @return The food ID if successful, -1 otherwise
     */
    @Override
    public Integer save(FoodDto item) {
        int generatedId = foodDao.save(item);
        if (generatedId != -1) {
            FoodDto updatedFood;
            if (item.foodId() != null && item.foodId().equals(generatedId)) {
                updatedFood = item;
            } else {
                updatedFood = new FoodDto(generatedId, item.name(), item.ean_code(), item.unit(),
                        item.calories(), item.proteins(), item.fats(), item.carbohydrates(),
                        item.quantityServing(), item.quantityUnit());
            }
            foodCache.put(generatedId, updatedFood);
        }
        return generatedId;
    }

    /**
     * Deletes a food item by its ID from both database and cache.
     *
     * @param key The ID of the food item to delete
     */
    @Override
    public void deleteById(Integer key) {
        foodDao.deleteById(key);
        foodCache.remove(key);
    }

    /**
     * Closes the database connection.
     */
    @Override
    public void close() {
        ConnectionManager.close();
    }
}