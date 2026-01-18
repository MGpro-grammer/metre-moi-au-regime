package be.esi.prj.repository;

import be.esi.prj.dto.DiaryDto;

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
 * Data Access Object implementation for diaries stored in a database.
 * Provides CRUD operations and specific queries for DiaryDto entities.
 */
public class DiaryDao implements Dao<Integer, DiaryDto> {
    private final DateTimeFormatter formatter;
    private final Connection connection;

    /**
     * Creates a new DiaryDao with the specified database connection.
     *
     * @param connection The database connection to use
     * @throws NullPointerException if connection is null
     */
    DiaryDao(Connection connection) {
        this.connection = Objects.requireNonNull(connection, "Connection required");
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }

    /**
     * Extracts diary data from a database result set.
     *
     * @param resultSet The result set containing diary data
     * @return A new DiaryDto populated with data from the result set
     * @throws SQLException if a database access error occurs
     */
    private DiaryDto extractDiaryFromResultSet(ResultSet resultSet) throws SQLException {
        Integer diaryId = resultSet.getInt("diary_id");
        Integer userId = resultSet.getInt("user_id");
        LocalDate date = LocalDate.parse(resultSet.getString("date"), formatter);
        Double recordedWeight = resultSet.getDouble("recorded_weight");

        return new DiaryDto(diaryId, userId, date, recordedWeight);
    }

    /**
     * Updates a prepared statement with diary data.
     *
     * @param preparedStatement The prepared statement to update
     * @param diary The diary data to set in the statement
     * @throws SQLException if a database access error occurs
     */
    private void updatePreparedStatement(PreparedStatement preparedStatement, DiaryDto diary) throws SQLException {
        preparedStatement.setInt(1, diary.userId());
        preparedStatement.setString(2, diary.date().format(formatter));
        preparedStatement.setDouble(3, diary.recordedWeight());
    }

    /**
     * Finds a diary by its ID.
     *
     * @param id The diary ID to search for
     * @return An Optional containing the diary if found, empty otherwise
     * @throws RepositoryException if a database error occurs
     */
    @Override
    public Optional<DiaryDto> findById(Integer id) {
        String sql = """
                SELECT * FROM Diary
                WHERE diary_id = ?
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    DiaryDto diary = extractDiaryFromResultSet(resultSet);
                    return Optional.of(diary);
                }
            }
        } catch (SQLException sqlException) {
            throw new RepositoryException("Error finding diary by ID: " + id, sqlException);
        }
        return Optional.empty();
    }

    /**
     * Finds a diary by user ID and date.
     *
     * @param userId The user ID to search for
     * @param date The date to search for
     * @return An Optional containing the diary if found, empty otherwise
     * @throws RepositoryException if a database error occurs
     */
    public Optional<DiaryDto> findByUserIdAndDate(Integer userId, LocalDate date) {
        String sql = """
                SELECT * FROM Diary
                WHERE user_id = ? AND date = ?
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, date.format(formatter));
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    DiaryDto diary = extractDiaryFromResultSet(resultSet);
                    return Optional.of(diary);
                }
            }
        } catch (SQLException sqlException) {
            throw new RepositoryException("Error finding diary by user ID and date", sqlException);
        }
        return Optional.empty();
    }

    /**
     * Retrieves all diaries from the database.
     *
     * @return A list of all diaries
     * @throws RepositoryException if a database error occurs
     */
    @Override
    public List<DiaryDto> findAll() {
        List<DiaryDto> diaries = new ArrayList<>();
        String sql = """
                SELECT * FROM Diary
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                DiaryDto diary = extractDiaryFromResultSet(resultSet);
                diaries.add(diary);
            }
        } catch (SQLException sqlException) {
            throw new RepositoryException("Error finding all diaries", sqlException);
        }
        return diaries;
    }

    /**
     * Inserts a new diary into the database.
     *
     * @param diary The diary to insert
     * @return The generated diary ID or -1 if insertion failed
     * @throws RepositoryException if a database error occurs
     */
    private Integer insert(DiaryDto diary) {
        String sql = """
                INSERT INTO Diary (user_id, date, recorded_weight)
                VALUES (?, ?, ?)
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            updatePreparedStatement(preparedStatement, diary);
            preparedStatement.executeUpdate();
            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException sqlException) {
            throw new RepositoryException("Error inserting diary", sqlException);
        }
        return -1; // Indicating failure to insert
    }

    /**
     * Updates an existing diary in the database.
     *
     * @param diary The diary with updated information
     * @return The number of rows updated
     * @throws RepositoryException if a database error occurs
     */
    private Integer update(DiaryDto diary) {
        String sql = """
                UPDATE Diary
                SET user_id = ?, date = ?, recorded_weight = ?
                WHERE diary_id = ?
                """;
        int updatedRows = 0;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            updatePreparedStatement(preparedStatement, diary);
            preparedStatement.setInt(4, diary.diaryId());
            updatedRows = preparedStatement.executeUpdate();
        } catch (SQLException sqlException) {
            throw new RepositoryException("Error updating diary", sqlException);
        }
        return updatedRows;
    }

    /**
     * Saves a diary by either updating it if it exists or inserting it if it doesn't.
     *
     * @param item The diary to save
     * @return The diary ID if successful, -1 otherwise
     * @throws RepositoryException if a database error occurs
     */
    @Override
    public Integer save(DiaryDto item) {
        Integer result = -1; // Default to -1 indicating failure
        String sql = """
                SELECT COUNT(*) FROM Diary
                WHERE diary_id = ?
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, item.diaryId());
            ResultSet resultSet = preparedStatement.executeQuery();
            boolean found = resultSet.next() && resultSet.getInt(1) > 0;
            if (found) {
                if (this.update(item) > 0) {
                    result = item.diaryId();
                }
            } else {
                result = this.insert(item);
            }
            return result;
        } catch (SQLException sqlException) {
            throw new RepositoryException("Error saving diary", sqlException);
        }
    }

    /**
     * Deletes a diary by its ID.
     *
     * @param id The ID of the diary to delete
     * @throws RepositoryException if a database error occurs
     */
    @Override
    public void deleteById(Integer id) {
        String sql = """
                DELETE FROM Diary
                WHERE diary_id = ?
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException sqlException) {
            throw new RepositoryException("Error deleting diary by ID: " + id, sqlException);
        }
    }
}