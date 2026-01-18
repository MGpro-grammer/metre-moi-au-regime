package be.esi.prj.repository;

import be.esi.prj.dto.ActivityDto;

import java.sql.Connection;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repository implementation for activities with caching capabilities.
 * Provides CRUD operations for ActivityDto entities and maintains an in-memory cache.
 */
public class ActivityRepository implements Repository<Integer, ActivityDto> {
    private final ActivityDao activityDao;
    private final Map<Integer, ActivityDto> activityCache;

    /**
     * Creates a default ActivityRepository with persistent database connection.
     */
    public ActivityRepository() {
        this(false);
    }

    /**
     * Creates an ActivityRepository with specified database type.
     *
     * @param menActive If true, uses in-memory database, otherwise uses persistent database
     */
    public ActivityRepository(boolean menActive) {
        Connection connection;
        if (menActive) {
            connection = ConnectionManager.getMemConnection();
        } else {
            connection = ConnectionManager.getConnection();
        }
        this.activityDao = new ActivityDao(connection);
        this.activityCache = new ConcurrentHashMap<>();
        loadCache();
    }

    /**
     * Creates an ActivityRepository with a specific DAO implementation.
     *
     * @param activityDao The DAO to use for data access
     * @throws NullPointerException if activityDao is null
     */
    ActivityRepository(ActivityDao activityDao) {
        this.activityDao = Objects.requireNonNull(activityDao, "ActivityDao is required");
        this.activityCache = new ConcurrentHashMap<>();
        loadCache();
    }

    /**
     * Loads all activities from the database into the cache.
     */
    private void loadCache() {
        activityDao.findAll().forEach(activity ->
                activityCache.put(activity.activityId(), activity));
    }

    /**
     * Finds an activity by its ID, first in cache then in database.
     *
     * @param key The activity ID to search for
     * @return An Optional containing the activity if found, empty otherwise
     */
    @Override
    public Optional<ActivityDto> findById(Integer key) {
        return Optional.ofNullable(activityCache.get(key))
                .or(() -> activityDao.findById(key));
    }

    /**
     * Retrieves all activities from the cache.
     *
     * @return A list of all activities
     */
    @Override
    public List<ActivityDto> findAll() {
        return new ArrayList<>(activityCache.values());
    }

    /**
     * Finds all activities associated with a specific diary.
     *
     * @param diaryId The diary ID to search for
     * @return A list of activities associated with the specified diary
     */
    public List<ActivityDto> findAllActivitiesByDiaryId(Integer diaryId) {
        List<ActivityDto> activities = activityCache.values().stream()
                .filter(activity -> activity.diaryId().equals(diaryId))
                .toList();

        if (activities.isEmpty()) {
            return activityDao.findAllActivitiesByDiaryId(diaryId);
        }

        return activities;
    }

    /**
     * Saves an activity in the database and updates the cache.
     *
     * @param item The activity to save
     * @return The activity ID if successful, -1 otherwise
     */
    @Override
    public Integer save(ActivityDto item) {
        int generatedId = activityDao.save(item);
        if (generatedId != -1) {
            ActivityDto updatedActivity;
            if (item.activityId() != null && item.activityId().equals(generatedId)) {
                updatedActivity = item;
            } else {
                updatedActivity = new ActivityDto(generatedId, item.title(), item.caloriesBurned(), item.diaryId());
            }
            activityCache.put(updatedActivity.activityId(), updatedActivity);
        }
        return generatedId;
    }

    /**
     * Deletes an activity by its ID from both database and cache.
     *
     * @param key The ID of the activity to delete
     */
    @Override
    public void deleteById(Integer key) {
        activityDao.deleteById(key);
        activityCache.remove(key);
    }

    /**
     * Closes the database connection.
     */
    @Override
    public void close() {
        ConnectionManager.close();
    }
}
