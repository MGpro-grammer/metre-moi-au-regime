package be.esi.prj.repository;

import be.esi.prj.dto.FoodConsumedDto;
import be.esi.prj.enumeration.ConsumptionType;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class FoodConsumedDaoTest {

    private static Connection connection;
    private FoodConsumedDao instance;

    private final FoodConsumedDto breakfast;
    private final FoodConsumedDto lunch;

    public FoodConsumedDaoTest() {
        breakfast = new FoodConsumedDto(1, 1, 101, ConsumptionType.PER_PERCENT, 250.0);
        lunch = new FoodConsumedDto(2, 1, 102, ConsumptionType.PER_QUANTITY, 350.0);
    }

    @BeforeAll
    static void setupDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""                    
                    CREATE TABLE FoodConsumed (
                        food_consumed_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        diary_id INTEGER,
                        food_id INTEGER,
                        consumption_type TEXT CHECK (consumption_type IN ('PER_SERVING', 'PER_UNIT', 'PER_QUANTITY', 'PER_PERCENT')),
                        quantity REAL,
                        FOREIGN KEY (diary_id) REFERENCES Diary(diary_id),
                        FOREIGN KEY (food_id) REFERENCES Food(food_id)
                    );
                    """);
        }
    }

    @BeforeEach
    void setup() throws SQLException {
        instance = new FoodConsumedDao(connection);

        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                    INSERT INTO FoodConsumed (food_consumed_id, diary_id, food_id, consumption_type, quantity) VALUES
                    (1, 1, 101, 'PER_PERCENT', 250.0),
                    (2, 1, 102, 'PER_QUANTITY', 350.0);
                    """);
        }
    }

    @AfterEach
    void cleanDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                    DELETE FROM FoodConsumed
                    """);
        }
    }

    @AfterAll
    static void closeDatabase() throws SQLException {
        connection.close();
    }

    @Test
    void testFindByIdDoesExist() {
        System.out.println("Test : method findById - Exist");
        // Arrange
        Optional<FoodConsumedDto> expected = Optional.of(breakfast);
        // Act
        Optional<FoodConsumedDto> result = instance.findById(1);
        // Assert
        assertEquals(expected, result);
    }

    @Test
    void testFindByIdDoesNotExist() {
        System.out.println("Test : method findById - Not exist");
        // Arrange
        Optional<FoodConsumedDto> expected = Optional.empty();
        // Act
        Optional<FoodConsumedDto> result = instance.findById(100);
        // Assert
        assertEquals(expected, result);
    }

    @Test
    void testFindByDiaryIdAndFoodIdDoesExist() {
        System.out.println("Test : method findByDiaryIdAndFoodId - Exist");
        // Arrange
        Optional<FoodConsumedDto> expected = Optional.of(breakfast);
        // Act
        Optional<FoodConsumedDto> result = instance.findByDiaryIdAndFoodId(1, 101);
        // Assert
        assertEquals(expected, result);
    }

    @Test
    void testFindByDiaryIdAndFoodIdDoesNotExist() {
        System.out.println("Test : method findByDiaryIdAndFoodId - Not exist");
        // Arrange
        Optional<FoodConsumedDto> expected = Optional.empty();
        // Act
        Optional<FoodConsumedDto> result = instance.findByDiaryIdAndFoodId(1, 999);
        // Assert
        assertEquals(expected, result);
    }

    @Test
    void testFindAllWithTwoRecords() {
        System.out.println("Test : method findAll - With two records");
        // Arrange
        List<FoodConsumedDto> expected = Arrays.asList(breakfast, lunch);
        // Act
        List<FoodConsumedDto> result = instance.findAll();
        // Assert
        assertEquals(expected, result);
    }

    @Test
    void testFindAllFoodsByDiaryId() {
        System.out.println("Test : method findAllFoodsByDiaryId - With two records");
        // Arrange
        List<FoodConsumedDto> expected = Arrays.asList(breakfast, lunch);
        // Act
        List<FoodConsumedDto> result = instance.findAllFoodsByDiaryId(1);
        // Assert
        assertEquals(expected, result);
    }

    @Test
    void testFindAllFoodsByDiaryIdNoRecords() {
        System.out.println("Test : method findAllFoodsByDiaryId - No records");
        // Arrange
        List<FoodConsumedDto> expected = List.of();
        // Act
        List<FoodConsumedDto> result = instance.findAllFoodsByDiaryId(999);
        // Assert
        assertEquals(expected, result);
    }

    @Test
    void testInsertFoodConsumed() {
        System.out.println("Test : method save - Insert a new food consumed");
        // Arrange
        FoodConsumedDto dinner = new FoodConsumedDto(-1, 2, 103, ConsumptionType.PER_UNIT, 400.0);
        Integer expected = 3;
        // Act
        Integer actual = instance.save(dinner);
        // Assert
        assertEquals(expected, actual);
    }

    @Test
    void testUpdateFoodConsumed() {
        System.out.println("Test : method save - Update a food consumed");
        // Arrange
        FoodConsumedDto modifiedBreakfast = new FoodConsumedDto(1, 1, 101, ConsumptionType.PER_PERCENT, 300.0);
        Integer expected = 1;
        // Act
        Integer result = instance.save(modifiedBreakfast);
        // Assert
        assertEquals(expected, result);
    }

    @Test
    void testDeleteById() {
        System.out.println("Test : method deleteById - delete a food consumed");
        // Arrange
        Optional<FoodConsumedDto> expected = Optional.empty();
        // Act
        instance.deleteById(1);
        Optional<FoodConsumedDto> result = instance.findById(1);
        // Assert
        assertEquals(expected, result);
    }
}