package be.esi.prj.repository;

import be.esi.prj.dto.FoodConsumedDto;
import be.esi.prj.enumeration.ConsumptionType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Data Access Object implementation for food consumption records stored in a database.
 * Provides CRUD operations and specific queries for FoodConsumedDto entities.
 */
public class FoodConsumedDao implements Dao<Integer, FoodConsumedDto> {
    private final Connection connection;

    /**
     * Creates a new FoodConsumedDao with the specified database connection.
     *
     * @param connection The database connection to use
     * @throws NullPointerException if connection is null
     */
    FoodConsumedDao(Connection connection) {
        this.connection = Objects.requireNonNull(connection, "Connection required");
    }

    /**
     * Extracts food consumption data from a database result set.
     *
     * @param resultSet The result set containing food consumption data
     * @return A new FoodConsumedDto populated with data from the result set
     * @throws SQLException if a database access error occurs
     */
    private FoodConsumedDto extractFoodConsumedFromResultSet(ResultSet resultSet) throws SQLException {
        Integer foodConsumedId = resultSet.getInt("food_consumed_id");
        Integer diaryId = resultSet.getInt("diary_id");
        Integer foodId = resultSet.getInt("food_id");
        ConsumptionType consumptionType = ConsumptionType.valueOf(resultSet.getString("consumption_type"));
        Double quantity = resultSet.getDouble("quantity");

        return new FoodConsumedDto(foodConsumedId, diaryId, foodId, consumptionType, quantity);
    }

    /**
     * Updates a prepared statement with food consumption data.
     *
     * @param preparedStatement The prepared statement to update
     * @param foodConsumed The food consumption data to set in the statement
     * @throws SQLException if a database access error occurs
     */
    private void updatePreparedStatement(PreparedStatement preparedStatement, FoodConsumedDto foodConsumed) throws SQLException {
        preparedStatement.setInt(1, foodConsumed.diaryId());
        preparedStatement.setInt(2, foodConsumed.foodId());
        preparedStatement.setString(3, String.valueOf(foodConsumed.consumptionType()));
        preparedStatement.setDouble(4, foodConsumed.quantity());
    }

    /**
     * Finds a food consumption record by its ID.
     *
     * @param id The food consumption ID to search for
     * @return An Optional containing the record if found, empty otherwise
     * @throws RepositoryException if a database error occurs
     */
    @Override
    public Optional<FoodConsumedDto> findById(Integer id) {
        String sql = """
                SELECT * FROM FoodConsumed
                WHERE food_consumed_id = ?
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    FoodConsumedDto foodConsumed = extractFoodConsumedFromResultSet(resultSet);
                    return Optional.of(foodConsumed);
                }
            }
        } catch (SQLException sqlException) {
            throw new RepositoryException("Error finding food consumed by ID: " + id, sqlException);
        }
        return Optional.empty();
    }

    /**
     * Finds a food consumption record by diary ID and food ID.
     *
     * @param diaryId The diary ID to search for
     * @param foodId The food ID to search for
     * @return An Optional containing the record if found, empty otherwise
     * @throws RepositoryException if a database error occurs
     */
    public Optional<FoodConsumedDto> findByDiaryIdAndFoodId(Integer diaryId, Integer foodId) {
        String sql = """
                SELECT * FROM FoodConsumed
                WHERE diary_id = ? AND food_id = ?
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, diaryId);
            preparedStatement.setInt(2, foodId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    FoodConsumedDto foodConsumed = extractFoodConsumedFromResultSet(resultSet);
                    return Optional.of(foodConsumed);
                }
            }
        } catch (SQLException sqlException) {
            throw new RepositoryException("Error finding food consumed by diary ID and food ID", sqlException);
        }
        return Optional.empty();
    }

    /**
     * Retrieves all food consumption records from the database.
     *
     * @return A list of all food consumption records
     * @throws RepositoryException if a database error occurs
     */
    @Override
    public List<FoodConsumedDto> findAll() {
        List<FoodConsumedDto> foodsConsumed = new ArrayList<>();
        String sql = """
                SELECT * FROM FoodConsumed
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                FoodConsumedDto foodConsumed = extractFoodConsumedFromResultSet(resultSet);
                foodsConsumed.add(foodConsumed);
            }
        } catch (SQLException sqlException) {
            throw new RepositoryException("Error finding all foods consumed", sqlException);
        }
        return foodsConsumed;
    }

    /**
     * Finds all food consumption records for a specific diary.
     *
     * @param diaryId The diary ID to search for
     * @return A list of food consumption records associated with the specified diary
     * @throws RepositoryException if a database error occurs
     */
    public List<FoodConsumedDto> findAllFoodsByDiaryId(Integer diaryId) {
        List<FoodConsumedDto> foodsConsumed = new ArrayList<>();
        String sql = """
                SELECT * FROM FoodConsumed
                WHERE diary_id = ?
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, diaryId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    FoodConsumedDto foodConsumed = extractFoodConsumedFromResultSet(resultSet);
                    foodsConsumed.add(foodConsumed);
                }
            }
        } catch (SQLException sqlException) {
            throw new RepositoryException("Error finding all foods consumed by diary ID: " + diaryId, sqlException);
        }
        return foodsConsumed;
    }

    /**
     * Inserts a new food consumption record into the database.
     *
     * @param foodConsumed The food consumption record to insert
     * @return The generated record ID or -1 if insertion failed
     * @throws RepositoryException if a database error occurs
     */
    private Integer insert(FoodConsumedDto foodConsumed) {
        String sql = """
                INSERT INTO FoodConsumed (diary_id, food_id, consumption_type, quantity)
                VALUES (?, ?, ?, ?)
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            updatePreparedStatement(preparedStatement, foodConsumed);
            preparedStatement.executeUpdate();
            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException sqlException) {
            throw new RepositoryException("Error inserting food consumed", sqlException);
        }
        return -1; // Indicating failure to insert
    }

    /**
     * Updates an existing food consumption record in the database.
     *
     * @param foodConsumed The food consumption record with updated information
     * @return The number of rows updated
     * @throws RepositoryException if a database error occurs
     */
    private Integer update(FoodConsumedDto foodConsumed) {
        String sql = """
                UPDATE FoodConsumed
                SET diary_id = ?, food_id = ?, consumption_type = ?, quantity = ?
                WHERE food_consumed_id = ?
                """;
        int updatedRows = 0;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            updatePreparedStatement(preparedStatement, foodConsumed);
            preparedStatement.setInt(5, foodConsumed.foodConsumedId());
            updatedRows = preparedStatement.executeUpdate();
        } catch (SQLException sqlException) {
            throw new RepositoryException("Error updating food consumed", sqlException);
        }
        return updatedRows;
    }

    /**
     * Saves a food consumption record by either updating it if it exists or inserting it if it doesn't.
     *
     * @param item The food consumption record to save
     * @return The record ID if successful, -1 otherwise
     * @throws RepositoryException if a database error occurs
     */
    @Override
    public Integer save(FoodConsumedDto item) {
        Integer result = -1; // Default to -1 indicating failure
        String sql = """
                SELECT COUNT(*) FROM FoodConsumed
                WHERE food_consumed_id = ?
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, item.foodConsumedId());
            ResultSet resultSet = preparedStatement.executeQuery();
            boolean found = resultSet.next() && resultSet.getInt(1) > 0;
            if (found) {
                if (this.update(item) > 0) {
                    result = item.foodConsumedId();
                }
            } else {
                result = this.insert(item);
            }
            return result;
        } catch (SQLException sqlException) {
            throw new RepositoryException("Error saving food consumed", sqlException);
        }
    }

    /**
     * Deletes a food consumption record by its ID.
     *
     * @param id The ID of the food consumption record to delete
     * @throws RepositoryException if a database error occurs
     */
    @Override
    public void deleteById(Integer id) {
        String sql = """
                DELETE FROM FoodConsumed
                WHERE food_consumed_id = ?
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException sqlException) {
            throw new RepositoryException("Error deleting food consumed by ID: " + id, sqlException);
        }
    }
}