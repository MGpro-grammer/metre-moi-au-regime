package be.esi.prj.repository;

import be.esi.prj.dto.FoodDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Data Access Object implementation for food items stored in a database.
 * Provides CRUD operations and specific queries for FoodDto entities.
 */
public class FoodDao implements Dao<Integer, FoodDto> {
    private final Connection connection;

    /**
     * Creates a new FoodDao with the specified database connection.
     *
     * @param connection The database connection to use
     * @throws NullPointerException if connection is null
     */
    FoodDao(Connection connection) {
        this.connection = Objects.requireNonNull(connection, "Connection required");
    }

    /**
     * Extracts food data from a database result set.
     *
     * @param resultSet The result set containing food data
     * @return A new FoodDto populated with data from the result set
     * @throws SQLException if a database access error occurs
     */
    private FoodDto extractFoodFromResultSet(ResultSet resultSet) throws SQLException {
        Integer foodId = resultSet.getInt("food_id");
        String name = resultSet.getString("name");
        String eanCode = resultSet.getString("ean_code");
        String unit = resultSet.getString("unit");
        Double calories = resultSet.getDouble("calories");
        Double proteins = resultSet.getDouble("proteins");
        Double fats = resultSet.getDouble("fats");
        Double carbohydrates = resultSet.getDouble("carbohydrates");
        Double quantityServing = resultSet.getDouble("quantity_serving");
        Double quantityUnit = resultSet.getDouble("quantity_unit");

        return new FoodDto(foodId, name, eanCode, unit, calories, proteins, fats, carbohydrates, quantityServing, quantityUnit);
    }

    /**
     * Updates a prepared statement with food data.
     *
     * @param preparedStatement The prepared statement to update
     * @param food The food data to set in the statement
     * @throws SQLException if a database access error occurs
     */
    private void updatePreparedStatement(PreparedStatement preparedStatement, FoodDto food) throws SQLException {
        preparedStatement.setString(1, food.name());
        preparedStatement.setString(2, food.ean_code());
        preparedStatement.setString(3, food.unit());
        preparedStatement.setDouble(4, food.calories());
        preparedStatement.setDouble(5, food.proteins());
        preparedStatement.setDouble(6, food.fats());
        preparedStatement.setDouble(7, food.carbohydrates());
        preparedStatement.setDouble(8, food.quantityServing());
        preparedStatement.setDouble(9, food.quantityUnit());
    }

    /**
     * Inserts a new food item into the database.
     *
     * @param food The food item to insert
     * @return The generated food ID or -1 if insertion failed
     * @throws RepositoryException if a database error occurs
     */
    private Integer insert(FoodDto food) {
        String sql = """
                INSERT INTO Food (name, ean_code, unit, calories, proteins, fats, carbohydrates, quantity_serving, quantity_unit)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            updatePreparedStatement(preparedStatement, food);
            preparedStatement.executeUpdate();
            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException sqlException) {
            throw new RepositoryException("Erreur lors de l'insertion d'un aliment", sqlException);
        }
        return -1; // Échec de l'insertion
    }

    /**
     * Updates an existing food item in the database.
     *
     * @param food The food item with updated information
     * @return The number of rows updated
     * @throws RepositoryException if a database error occurs
     */
    private Integer update(FoodDto food) {
        String sql = """
                UPDATE Food
                SET name = ?, ean_code = ?, unit = ?, calories = ?, proteins = ?, fats = ?, carbohydrates = ?, quantity_serving = ?, quantity_unit = ?
                WHERE food_id = ?
                """;
        int updatedRows = 0;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            updatePreparedStatement(preparedStatement, food);
            preparedStatement.setInt(10, food.foodId());
            updatedRows = preparedStatement.executeUpdate();
        } catch (SQLException sqlException) {
            throw new RepositoryException("Erreur lors de la mise à jour d'un aliment", sqlException);
        }
        return updatedRows;
    }

    /**
     * Finds a food item by its ID.
     *
     * @param id The food ID to search for
     * @return An Optional containing the food item if found, empty otherwise
     * @throws RepositoryException if a database error occurs
     */
    @Override
    public Optional<FoodDto> findById(Integer id) {
        String sql = """
                SELECT * FROM Food
                WHERE food_id = ?
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    FoodDto food = extractFoodFromResultSet(resultSet);
                    return Optional.of(food);
                }
            }
        } catch (SQLException sqlException) {
            throw new RepositoryException("Erreur lors de la recherche d'un aliment par ID: " + id, sqlException);
        }
        return Optional.empty();
    }

    /**
     * Finds a food item by its EAN code.
     *
     * @param eanCode The EAN code to search for
     * @return An Optional containing the food item if found, empty otherwise
     * @throws RepositoryException if a database error occurs
     */
    public Optional<FoodDto> findByEanCode(String eanCode) {
        String sql = """
                SELECT * FROM Food
                WHERE ean_code = ?
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, eanCode);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    FoodDto food = extractFoodFromResultSet(resultSet);
                    return Optional.of(food);
                }
            }
        } catch (SQLException sqlException) {
            throw new RepositoryException("Erreur lors de la recherche d'un aliment par EAN code: " + eanCode, sqlException);
        }
        return Optional.empty();
    }

    /**
     * Retrieves all food items from the database.
     *
     * @return A list of all food items
     * @throws RepositoryException if a database error occurs
     */
    @Override
    public List<FoodDto> findAll() {
        List<FoodDto> foods = new ArrayList<>();
        String sql = """
                SELECT * FROM Food
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                FoodDto food = extractFoodFromResultSet(resultSet);
                foods.add(food);
            }
        } catch (SQLException sqlException) {
            throw new RepositoryException("Erreur lors de la récupération de tous les aliments", sqlException);
        }
        return foods;
    }

    /**
     * Saves a food item by either updating it if it exists or inserting it if it doesn't.
     *
     * @param item The food item to save
     * @return The food ID if successful, -1 otherwise
     * @throws RepositoryException if a database error occurs
     */
    @Override
    public Integer save(FoodDto item) {
        Integer result = -1; // Par défaut -1 indiquant un échec
        String sql = """
                SELECT COUNT(*) FROM Food
                WHERE food_id = ?
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, item.foodId());
            ResultSet resultSet = preparedStatement.executeQuery();
            boolean found = resultSet.next() && resultSet.getInt(1) > 0;
            if (found) {
                if (this.update(item) > 0) {
                    result = item.foodId();
                }
            } else {
                result = this.insert(item);
            }
            return result;
        } catch (SQLException sqlException) {
            throw new RepositoryException("Erreur lors de la sauvegarde d'un aliment", sqlException);
        }
    }

    /**
     * Deletes a food item by its ID.
     *
     * @param id The ID of the food item to delete
     * @throws RepositoryException if a database error occurs
     */
    @Override
    public void deleteById(Integer id) {
        String sql = """
                DELETE FROM Food
                WHERE food_id = ?
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException sqlException) {
            throw new RepositoryException("Erreur lors de la suppression d'un aliment par ID: " + id, sqlException);
        }
    }
}