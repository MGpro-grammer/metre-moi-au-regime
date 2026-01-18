package be.esi.prj.repository;

import be.esi.prj.dto.ActivityDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Data Access Object implementation for activities stored in a database.
 * Provides CRUD operations and specific queries for Activity entities.
 */
public class ActivityDao implements Dao<Integer, ActivityDto> {
    private final Connection connection;

    /**
     * Creates a new ActivityDao with the specified database connection.
     *
     * @param connection The database connection to use
     * @throws NullPointerException if connection is null
     */
    ActivityDao(Connection connection) {
        this.connection = Objects.requireNonNull(connection, "Connection required");
    }

    /**
     * Extracts activity data from a database result set.
     *
     * @param resultSet The result set containing activity data
     * @return A new ActivityDto populated with data from the result set
     * @throws SQLException if a database access error occurs
     */
    private ActivityDto extractActivityFromResultSet(ResultSet resultSet) throws SQLException {
        Integer activityId = resultSet.getInt("activity_id");
        String title = resultSet.getString("title");
        Double caloriesBurned = resultSet.getDouble("calories_burned");
        Integer diaryId = resultSet.getInt("diary_id");

        return new ActivityDto(activityId, title, caloriesBurned, diaryId);
    }

    /**
     * Updates a prepared statement with activity data.
     *
     * @param preparedStatement The prepared statement to update
     * @param activity The activity data to set in the statement
     * @throws SQLException if a database access error occurs
     */
    private void updatePreparedStatement(PreparedStatement preparedStatement, ActivityDto activity) throws SQLException {
        preparedStatement.setString(1, activity.title());
        preparedStatement.setDouble(2, activity.caloriesBurned());
        preparedStatement.setInt(3, activity.diaryId());
    }

    /**
     * Finds an activity by its ID.
     *
     * @param id The activity ID to search for
     * @return An Optional containing the activity if found, empty otherwise
     * @throws RepositoryException if a database error occurs
     */
    @Override
    public Optional<ActivityDto> findById(Integer id) {
        String sql = """
                SELECT * FROM Activity
                WHERE activity_id = ?
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    ActivityDto activity = extractActivityFromResultSet(resultSet);
                    return Optional.of(activity);
                }
            }
        } catch (SQLException sqlException) {
            throw new RepositoryException("Error finding activity by ID: " + id, sqlException);
        }
        return Optional.empty();
    }

    /**
     * Retrieves all activities from the database.
     *
     * @return A list of all activities
     * @throws RepositoryException if a database error occurs
     */
    @Override
    public List<ActivityDto> findAll() {
        List<ActivityDto> activities = new ArrayList<>();
        String sql = """
                SELECT * FROM Activity
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                ActivityDto activity = extractActivityFromResultSet(resultSet);
                activities.add(activity);
            }
        } catch (SQLException sqlException) {
            throw new RepositoryException("Error finding all activities", sqlException);
        }
        return activities;
    }

    /**
     * Finds all activities associated with a specific diary.
     *
     * @param diaryId The diary ID to search for
     * @return A list of activities associated with the specified diary
     * @throws RepositoryException if a database error occurs
     */
    public List<ActivityDto> findAllActivitiesByDiaryId(Integer diaryId) {
        List<ActivityDto> activities = new ArrayList<>();
        String sql = """
                SELECT * FROM Activity
                WHERE diary_id = ?
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, diaryId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    ActivityDto activity = extractActivityFromResultSet(resultSet);
                    activities.add(activity);
                }
            }
        } catch (SQLException sqlException) {
            throw new RepositoryException("Error finding activities by diary ID: " + diaryId, sqlException);
        }
        return activities;
    }

    /**
     * Inserts a new activity into the database.
     *
     * @param activity The activity to insert
     * @return The generated activity ID or -1 if insertion failed
     * @throws RepositoryException if a database error occurs
     */
    private Integer insert(ActivityDto activity) {
        String sql = """
                INSERT INTO Activity (title, calories_burned, diary_id)
                VALUES (?, ?, ?)
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            updatePreparedStatement(preparedStatement, activity);
            preparedStatement.executeUpdate();
            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException sqlException) {
            throw new RepositoryException("Error inserting activity", sqlException);
        }
        return -1; // Indicating failure to insert
    }

    /**
     * Updates an existing activity in the database.
     *
     * @param activity The activity with updated information
     * @return The number of rows updated
     * @throws RepositoryException if a database error occurs
     */
    private Integer update(ActivityDto activity) {
        String url = """
                UPDATE Activity
                SET title = ?, calories_burned = ?, diary_id = ?
                WHERE activity_id = ?
                """;
        int updatedRows = 0;
        try (PreparedStatement preparedStatement = connection.prepareStatement(url)) {
            updatePreparedStatement(preparedStatement, activity);
            preparedStatement.setInt(4, activity.activityId());
            updatedRows = preparedStatement.executeUpdate();
        } catch (SQLException sqlException) {
            throw new RepositoryException("Error updating activity", sqlException);
        }
        return updatedRows;
    }

    /**
     * Saves an activity by either updating it if it exists or inserting it if it doesn't.
     *
     * @param item The activity to save
     * @return The activity ID if successful, -1 otherwise
     * @throws RepositoryException if a database error occurs
     */
    @Override
    public Integer save(ActivityDto item) {
        Integer result = -1; // Default to -1 indicating failure
        String sql = """
                SELECT COUNT(*) FROM Activity
                WHERE activity_id = ?
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, item.activityId());
            ResultSet resultSet = preparedStatement.executeQuery();
            boolean found = resultSet.next() && resultSet.getInt(1) > 0;
            if (found) {
                if (this.update(item) > 0) {
                    result = item.activityId();
                }
            } else {
                result = this.insert(item);
            }
            return result;
        } catch (SQLException sqlException) {
            throw new RepositoryException("Error saving activity", sqlException);
        }
    }

    /**
     * Deletes an activity by its ID.
     *
     * @param id The ID of the activity to delete
     * @throws RepositoryException if a database error occurs
     */
    @Override
    public void deleteById(Integer id) {
        String sql = """
                DELETE FROM Activity
                WHERE activity_id = ?
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException sqlException) {
            throw new RepositoryException("Error deleting activity by ID: " + id, sqlException);
        }
    }
}
