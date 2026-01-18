package be.esi.prj.repository;

import be.esi.prj.dto.UserDto;
import be.esi.prj.enumeration.ActivityLevel;
import be.esi.prj.enumeration.Gender;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Data Access Object for User entities, providing CRUD operations with database.
 */
public class UserDao implements Dao<Integer, UserDto> {
    private final DateTimeFormatter formatter;
    private final Connection connection;

    /**
     * Creates a UserDao with the specified database connection.
     *
     * @param connection Database connection to use for operations
     */
    UserDao(Connection connection) {
        this.connection = Objects.requireNonNull(connection, "Connection required");
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }

    /**
     * Extracts user information from a database result set.
     *
     * @param resultSet Database result containing user data
     * @return UserDto object with extracted data
     * @throws SQLException If data extraction fails
     */
    private UserDto extractUserFromResultSet(ResultSet resultSet) throws SQLException {
        Integer userId = resultSet.getInt("user_id");
        String email = resultSet.getString("email");
        String password = resultSet.getString("password");
        String name = resultSet.getString("name");
        Integer age = resultSet.getInt("age");
        Gender gender = Gender.valueOf(resultSet.getString("gender"));
        Double height = resultSet.getDouble("height");
        Double weight = resultSet.getDouble("weight");
        Double goalWeight = resultSet.getDouble("goal_weight");
        LocalDate startDate = LocalDate.parse(resultSet.getString("start_date"), formatter);
        LocalDate endDate = LocalDate.parse(resultSet.getString("end_date"), formatter);
        ActivityLevel activityLevel = ActivityLevel.valueOf(resultSet.getString("activity_level"));

        return new UserDto(userId, email, password, name, age,
                gender, height, weight, goalWeight, startDate,
                endDate, activityLevel);
    }

    /**
     * Updates a prepared statement with user data for database operations.
     *
     * @param preparedStatement Statement to populate with user data
     * @param user User data to insert into statement
     * @throws SQLException If statement update fails
     */
    private void updatePreparedStatement(PreparedStatement preparedStatement, UserDto user) throws SQLException {
        preparedStatement.setString(1, user.email());
        preparedStatement.setString(2, user.password());
        preparedStatement.setString(3, user.name());
        preparedStatement.setInt(4, user.age());
        preparedStatement.setString(5, String.valueOf(user.gender()));
        preparedStatement.setDouble(6, user.height());
        preparedStatement.setDouble(7, user.weight());
        preparedStatement.setDouble(8, user.goalWeight());
        preparedStatement.setString(9, user.startDate().format(formatter));
        preparedStatement.setString(10, user.endDate().format(formatter));
        preparedStatement.setString(11, String.valueOf(user.activityLevel()));
    }

    /**
     * Finds a user by their unique ID.
     *
     * @param id User ID to search for
     * @return Optional containing the user if found, empty otherwise
     */
    @Override
    public Optional<UserDto> findById(Integer id) {
        String sql = """
                SELECT * FROM User
                WHERE user_id = ?
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    UserDto user = extractUserFromResultSet(resultSet);
                    return Optional.of(user);
                }
            }
        } catch (SQLException sqlException) {
            throw new RepositoryException("Error finding user by ID: " + id, sqlException);
        }
        return Optional.empty();
    }

    /**
     * Finds a user by their email address.
     *
     * @param email Email to search for
     * @return Optional containing the user if found, empty otherwise
     */
    public Optional<UserDto> findByEmail(String email) {
        String sql = """
                SELECT * FROM User
                WHERE email = ?
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    UserDto user = extractUserFromResultSet(resultSet);
                    return Optional.of(user);
                }
            }
        } catch (SQLException sqlException) {
            throw new RepositoryException("Error finding user by email: " + email, sqlException);
        }
        return Optional.empty();
    }

    /**
     * Retrieves all users from the database.
     *
     * @return List of all users
     */
    @Override
    public List<UserDto> findAll() {
        List<UserDto> users = new ArrayList<>();
        String sql = """
                SELECT * FROM User
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                UserDto user = extractUserFromResultSet(resultSet);
                users.add(user);
            }
        } catch (SQLException sqlException) {
            throw new RepositoryException("Error finding all users", sqlException);
        }
        return users;
    }

    /**
     * Inserts a new user into the database.
     *
     * @param user User data to insert
     * @return Generated user ID if successful, -1 if failed
     */
    private Integer insert(UserDto user) {
        String sql = """
                INSERT INTO User (email, password, name, age, gender, height, weight, goal_weight, start_date, end_date, activity_level)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            updatePreparedStatement(preparedStatement, user);
            preparedStatement.executeUpdate();
            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException sqlException) {
            throw new RepositoryException("Error inserting user", sqlException);
        }
        return -1; // Indicating failure to insert
    }

    /**
     * Updates an existing user in the database.
     *
     * @param user User data to update
     * @return Number of rows updated
     */
    private Integer update(UserDto user) {
        String sql = """
                UPDATE User
                SET email = ?, password = ?, name = ?, age = ?, gender = ?, height = ?, weight = ?, goal_weight = ?, start_date = ?, end_date = ?, activity_level = ?
                WHERE user_id = ?
                """;
        int updatedRows = 0;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            updatePreparedStatement(preparedStatement, user);
            preparedStatement.setInt(12, user.userId());
            updatedRows = preparedStatement.executeUpdate();
        } catch (SQLException sqlException) {
            throw new RepositoryException("Error updating user", sqlException);
        }
        return updatedRows;
    }

    /**
     * Saves user data by inserting a new record or updating an existing one.
     *
     * @param item User data to save
     * @return User ID if operation succeeded, -1 if failed
     */
    @Override
    public Integer save(UserDto item) {
        Integer result = -1; // Default to -1 indicating failure
        String sql = """
                SELECT COUNT(*) FROM User
                WHERE user_id = ?
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, item.userId());
            ResultSet resultSet = preparedStatement.executeQuery();
            boolean found = resultSet.next() && resultSet.getInt(1) > 0;
            if (found) {
                if (this.update(item) > 0) {
                    result = item.userId();
                }
            } else {
                result = this.insert(item);
            }
            return result;
        } catch (SQLException sqlException) {
            throw new RepositoryException("Error saving user", sqlException);
        }
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id ID of user to delete
     */
    @Override
    public void deleteById(Integer id) {
        String sql = """
                DELETE FROM User
                WHERE user_id = ?
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException sqlException) {
            throw new RepositoryException("Error deleting user by ID: " + id, sqlException);
        }
    }
}
