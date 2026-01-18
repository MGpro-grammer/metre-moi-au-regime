package be.esi.prj.repository;

import be.esi.prj.dto.FoodConsumedDto;

import java.sql.Connection;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repository implementation pour la gestion des aliments consommés avec capacités de mise en cache.
 * Fournit des opérations CRUD pour les entités FoodConsumedDto et maintient un cache en mémoire.
 */
public class FoodConsumedRepository implements Repository<Integer, FoodConsumedDto> {
    private final FoodConsumedDao foodConsumedDao;
    private final Map<Integer, FoodConsumedDto> foodConsumedCache;

    /**
     * Creates a default FoodConsumedRepository with persistent database connection.
     */
    public FoodConsumedRepository() {
        this(false);
    }

    /**
     * Creates a FoodConsumedRepository with specified database type.
     *
     * @param memActive If true, uses in-memory database, otherwise uses persistent database
     */
    public FoodConsumedRepository(boolean memActive) {
        Connection connection;
        if (memActive) {
            connection = ConnectionManager.getMemConnection();
        } else {
            connection = ConnectionManager.getConnection();
        }
        this.foodConsumedDao = new FoodConsumedDao(connection);
        this.foodConsumedCache = new ConcurrentHashMap<>();
        loadCache();
    }

    /**
     * Creates a FoodConsumedRepository with a specific DAO implementation.
     *
     * @param foodConsumedDao The DAO to use for data access
     * @throws NullPointerException if foodConsumedDao is null
     */
    FoodConsumedRepository(FoodConsumedDao foodConsumedDao) {
        this.foodConsumedDao = Objects.requireNonNull(foodConsumedDao, "FoodConsumedDao is required");
        this.foodConsumedCache = new ConcurrentHashMap<>();
        loadCache();
    }

    /**
     * Loads all food consumption records from the database into the cache.
     */
    private void loadCache() {
        foodConsumedDao.findAll().forEach(foodConsumed ->
                foodConsumedCache.put(foodConsumed.foodConsumedId(), foodConsumed));
    }

    /**
     * Finds a food consumption record by its ID, first in cache then in database.
     *
     * @param key The food consumption ID to search for
     * @return An Optional containing the record if found, empty otherwise
     */
    @Override
    public Optional<FoodConsumedDto> findById(Integer key) {
        return Optional.ofNullable(foodConsumedCache.get(key))
                .or(() -> foodConsumedDao.findById(key));
    }

    /**
     * Finds a food consumption record by diary ID and food ID.
     *
     * @param diaryId The diary ID to search for
     * @param foodId The food ID to search for
     * @return An Optional containing the record if found, empty otherwise
     */
    public Optional<FoodConsumedDto> findByDiaryIdAndFoodId(Integer diaryId, Integer foodId) {
        return foodConsumedCache.values().stream()
                .filter(foodConsumed -> foodConsumed.diaryId().equals(diaryId)
                        && foodConsumed.foodId().equals(foodId))
                .findFirst()
                .or(() -> foodConsumedDao.findByDiaryIdAndFoodId(diaryId, foodId));
    }

    /**
     * Retrieves all food consumption records from the cache.
     *
     * @return A list of all food consumption records
     */
    @Override
    public List<FoodConsumedDto> findAll() {
        return new ArrayList<>(foodConsumedCache.values());
    }

    /**
     * Finds all food consumption records for a specific diary.
     *
     * @param diaryId The diary ID to search for
     * @return A list of food consumption records associated with the specified diary
     */
    public List<FoodConsumedDto> findAllFoodsByDiaryId(Integer diaryId) {
        List<FoodConsumedDto> cachedResults =  foodConsumedCache.values().stream()
                .filter(foodConsumed -> foodConsumed.diaryId().equals(diaryId))
                .toList();

        if (cachedResults.isEmpty()) {
            return foodConsumedDao.findAllFoodsByDiaryId(diaryId);
        }

        return cachedResults;
    }

    /**
     * Saves a food consumption record in the database and updates the cache.
     *
     * @param item The food consumption record to save
     * @return The record ID if successful, -1 otherwise
     */
    @Override
    public Integer save(FoodConsumedDto item) {
        int generatedId = foodConsumedDao.save(item);
        if (generatedId != -1) {
            FoodConsumedDto updatedFoodConsumed;
            if (item.foodConsumedId() != null && item.foodConsumedId().equals(generatedId)) {
                updatedFoodConsumed = item;
            } else {
                updatedFoodConsumed = new FoodConsumedDto(
                        generatedId,
                        item.diaryId(),
                        item.foodId(),
                        item.consumptionType(),
                        item.quantity()
                );
            }
            foodConsumedCache.put(generatedId, updatedFoodConsumed);
        }
        return generatedId;
    }

    /**
     * Deletes a food consumption record by its ID from both database and cache.
     *
     * @param key The ID of the food consumption record to delete
     */
    @Override
    public void deleteById(Integer key) {
        foodConsumedDao.deleteById(key);
        foodConsumedCache.remove(key);
    }

    /**
     * Closes the database connection.
     */
    @Override
    public void close() {
        ConnectionManager.close();
    }
}