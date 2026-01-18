package be.esi.prj.repository;

import be.esi.prj.dto.DiaryDto;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repository implementation for diaries with caching capabilities.
 * Provides CRUD operations for DiaryDto entities and maintains an in-memory cache.
 */
public class DiaryRepository implements Repository<Integer, DiaryDto> {
    private final DiaryDao diaryDao;
    private final Map<Integer, DiaryDto> diaryCache;

    /**
     * Creates a default DiaryRepository with persistent database connection.
     */
    public DiaryRepository() {
        this(false);
    }

    /**
     * Creates a DiaryRepository with specified database type.
     *
     * @param memActive If true, uses in-memory database, otherwise uses persistent database
     */
    public DiaryRepository(boolean memActive) {
        Connection connection;
        if (memActive) {
            connection = ConnectionManager.getMemConnection();
        } else {
            connection = ConnectionManager.getConnection();
        }
        this.diaryDao = new DiaryDao(connection);
        this.diaryCache = new ConcurrentHashMap<>();
        loadCache();
    }

    /**
     * Creates a DiaryRepository with a specific DAO implementation.
     *
     * @param diaryDao The DAO to use for data access
     * @throws NullPointerException if diaryDao is null
     */
    DiaryRepository(DiaryDao diaryDao) {
        this.diaryDao = Objects.requireNonNull(diaryDao, "DiaryDao is required");
        this.diaryCache = new ConcurrentHashMap<>();
        loadCache();
    }

    /**
     * Loads all diaries from the database into the cache.
     */
    private void loadCache() {
        diaryDao.findAll().forEach(diary ->
                diaryCache.put(diary.diaryId(), diary));
    }

    /**
     * Finds a diary by its ID, first in cache then in database.
     *
     * @param key The diary ID to search for
     * @return An Optional containing the diary if found, empty otherwise
     */
    @Override
    public Optional<DiaryDto> findById(Integer key) {
        return Optional.ofNullable(diaryCache.get(key))
                .or(() -> diaryDao.findById(key));
    }

    /**
     * Finds a diary by user ID and date, first in cache then in database.
     *
     * @param userId The user ID to search for
     * @param date The date to search for
     * @return An Optional containing the diary if found, empty otherwise
     */
    public Optional<DiaryDto> findByUserIdAndDate(Integer userId, LocalDate date) {
        return diaryCache.values().stream()
                .filter(diary -> diary.userId().equals(userId) && diary.date().equals(date))
                .findFirst()
                .or(() -> diaryDao.findByUserIdAndDate(userId, date));
    }

    /**
     * Retrieves all diaries from the cache.
     *
     * @return A list of all diaries
     */
    @Override
    public List<DiaryDto> findAll() {
        return new ArrayList<>(diaryCache.values());
    }

    /**
     * Saves a diary in the database and updates the cache.
     *
     * @param item The diary to save
     * @return The diary ID if successful, -1 otherwise
     */
    @Override
    public Integer save(DiaryDto item) {
        int generatedId = diaryDao.save(item);
        if (generatedId != -1) {
            DiaryDto updatedDiary;
            if (item.diaryId() != null && item.diaryId().equals(generatedId)) {
                updatedDiary = item;
            } else {
                updatedDiary = new DiaryDto(generatedId, item.userId(), item.date(), item.recordedWeight());
            }
            diaryCache.put(generatedId, updatedDiary);
        }
        return generatedId;
    }

    /**
     * Deletes a diary by its ID from both database and cache.
     *
     * @param key The ID of the diary to delete
     */
    @Override
    public void deleteById(Integer key) {
        diaryDao.deleteById(key);
        diaryCache.remove(key);
    }

    /**
     * Closes the database connection.
     */
    @Override
    public void close() {
        ConnectionManager.close();
    }
}