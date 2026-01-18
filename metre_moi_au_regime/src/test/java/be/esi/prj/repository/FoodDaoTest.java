package be.esi.prj.repository;

import be.esi.prj.dto.FoodDto;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class FoodDaoTest {

    private static Connection connection;
    private FoodDao instance;

    private final FoodDto apple;
    private final FoodDto banana;

    public FoodDaoTest() {
        apple = new FoodDto(1, "Apple", "123456789", "piece", 52.0, 0.3, 0.2, 14.0, 1.0, 1.0);
        banana = new FoodDto(2, "Banana", "987654321", "piece", 89.0, 1.1, 0.3, 23.0, 1.0, 1.0);
    }

    @BeforeAll
    static void setupDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                    CREATE TABLE Food (
                        food_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        ean_code TEXT,
                        unit TEXT,
                        calories REAL,
                        proteins REAL,
                        fats REAL,
                        carbohydrates REAL,
                        quantity_serving REAL,
                        quantity_unit REAL
                    )
                    """);
        }
    }

    @BeforeEach
    void setup() throws SQLException {
        instance = new FoodDao(connection);

        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                    INSERT INTO Food (food_id, name, ean_code, unit, calories, proteins, fats, carbohydrates, quantity_serving, quantity_unit) VALUES
                    (1, 'Apple', '123456789', 'piece', 52.0, 0.3, 0.2, 14.0, 1.0, 1.0),
                    (2, 'Banana', '987654321', 'piece', 89.0, 1.1, 0.3, 23.0, 1.0, 1.0);
                    """);
        }
    }

    @AfterEach
    void cleanDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                    DELETE FROM Food
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
        //Arrange
        Optional<FoodDto> expected = Optional.of(apple);
        //Action
        Optional<FoodDto> result = instance.findById(1);
        //Assert
        assertEquals(expected, result);
    }

    @Test
    void testFindByIdDoesNotExist() {
        System.out.println("Test : method findById - Not exist");
        //Arrange
        Optional<FoodDto> expected = Optional.empty();
        //Action
        Optional<FoodDto> result = instance.findById(100);
        //Assert
        assertEquals(expected, result);
    }

    @Test
    void testFindByEanCodeDoesExist() {
        System.out.println("Test : method findByEanCode - Exist");
        //Arrange
        Optional<FoodDto> expected = Optional.of(apple);
        //Action
        Optional<FoodDto> result = instance.findByEanCode("123456789");
        //Assert
        assertEquals(expected, result);
    }

    @Test
    void testFindByEanCodeDoesNotExist() {
        System.out.println("Test : method findByEanCode - Not exist");
        //Arrange
        Optional<FoodDto> expected = Optional.empty();
        //Action
        Optional<FoodDto> result = instance.findByEanCode("000000000");
        //Assert
        assertEquals(expected, result);
    }

    @Test
    void testFindAllWithTwoFoods() {
        System.out.println("Test : method findAll - With two foods");
        //Arrange
        List<FoodDto> expected = Arrays.asList(apple, banana);
        //Action
        List<FoodDto> result = instance.findAll();
        //Assert
        assertEquals(expected, result);
    }

    @Test
    void testInsertFood() {
        System.out.println("Test : method save - Insert a new food");
        //Arrange
        FoodDto orange = new FoodDto(-1, "Orange", "555555555", "piece", 47.0, 0.9, 0.1, 11.8, 1.0, 1.0);
        Integer expected = 3;
        //Action
        Integer actual = instance.save(orange);
        //Assert
        assertEquals(expected, actual);
    }

    @Test
    void testUpdateFood() {
        System.out.println("Test : method save - Update a food");
        //Arrange
        FoodDto modifiedApple = new FoodDto(1, "Green Apple", "123456789", "piece", 55.0, 0.4, 0.2, 14.5, 1.0, 1.0);
        Integer expected = 1;
        //Action
        Integer result = instance.save(modifiedApple);
        //Assert
        assertEquals(expected, result);
    }

    @Test
    void testDeleteById() {
        System.out.println("Test : method deleteById - delete a food");
        //Arrange
        Optional<FoodDto> expected = Optional.empty();
        //Action
        instance.deleteById(1);
        Optional<FoodDto> result = instance.findById(1);
        //Assert
        assertEquals(expected, result);
    }
}