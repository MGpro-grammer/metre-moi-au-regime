package be.esi.prj.repository;

import be.esi.prj.dto.UserDto;

import java.sql.Connection;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repository for User entities, providing CRUD operations with caching mechanism.
 * Implements Repository interface to manage UserDto objects.
 */
public class UserRepository implements Repository<Integer, UserDto> {
    private final UserDao userDao;
    private final Map<Integer, UserDto> userCache;

    /**
     * Creates a UserRepository with default database connection.
     */
    public UserRepository(){
        this(false);
    }

    /**
     * Creates a UserRepository with specified database connection type.
     *
     * @param memActive If true, uses in-memory connection; otherwise, persistent connection
     */
    public UserRepository(boolean memActive) {
        Connection connection;
        if (memActive) {
            connection = ConnectionManager.getMemConnection();
        } else {
            connection = ConnectionManager.getConnection();
        }
        this.userDao = new UserDao(connection);
        this.userCache = new ConcurrentHashMap<>();
        loadCache();
    }

    /**
     * Creates a UserRepository with a specific UserDao implementation.
     *
     * @param userDao Data access object to use for database operations
     */
    UserRepository(UserDao userDao) {
        this.userDao = Objects.requireNonNull(userDao, "UserDao is required");
        this.userCache = new ConcurrentHashMap<>();
        loadCache();
    }

    /**
     * Loads all users from database into the cache.
     */
    private void loadCache() {
        userDao.findAll().forEach(user ->
                userCache.put(user.userId(), user));
    }

    /**
     * Finds a user by ID, first checking cache then database.
     *
     * @param key User ID to search for
     * @return Optional containing the user if found, empty otherwise
     */
    @Override
    public Optional<UserDto> findById(Integer key) {
        return Optional.ofNullable(userCache.get(key))
                .or(() -> userDao.findById(key));
    }

    /**
     * Finds a user by email address, first checking cache then database.
     *
     * @param email Email to search for
     * @return Optional containing the user if found, empty otherwise
     */
    public Optional<UserDto> findByEmail(String email) {
        return userCache.values().stream()
                .filter(user -> user.email().equals(email))
                .findFirst()
                .or(() -> userDao.findByEmail(email));
    }

    /**
     * Retrieves all users from the cache.
     *
     * @return List of all cached users
     */
    @Override
    public List<UserDto> findAll() {
        return new ArrayList<>(userCache.values());
    }

    /**
     * Saves user data to database and updates cache accordingly.
     *
     * @param item User data to save
     * @return User ID if operation succeeded, -1 if failed
     */
    @Override
    public Integer save(UserDto item) {
        int generatedId = userDao.save(item);
        if (generatedId != -1) {
            UserDto updatedUser;
            if (item.userId() != null && item.userId().equals(generatedId)) {
                updatedUser = item;
            } else {
                updatedUser = new UserDto(generatedId, item.email(), item.password(), item.name(),
                        item.age(), item.gender(), item.height(), item.weight(), item.goalWeight(),
                        item.startDate(), item.endDate(), item.activityLevel());
            }
            userCache.put(generatedId, updatedUser);
        }
        return generatedId;
    }

    /**
     * Deletes a user by ID from both database and cache.
     *
     * @param key ID of user to delete
     */
    @Override
    public void deleteById(Integer key) {
        userDao.deleteById(key);
        userCache.remove(key);
    }

    /**
     * Closes database connection.
     */
    @Override
    public void close() {
        ConnectionManager.close();
    }
}