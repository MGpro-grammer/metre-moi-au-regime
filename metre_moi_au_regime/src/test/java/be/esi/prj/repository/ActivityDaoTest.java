package be.esi.prj.repository;

import be.esi.prj.dto.ActivityDto;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ActivityDaoTest {

    private static Connection connection;
    private ActivityDao instance;

    private final ActivityDto running;
    private final ActivityDto swimming;

    public ActivityDaoTest() {
        running = new ActivityDto(1, "Running", 350.0, 1);
        swimming = new ActivityDto(2, "Swimming", 400.0, 2);
    }

    @BeforeAll
    static void setupDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                    CREATE TABLE Activity (
                        activity_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        title TEXT NOT NULL,
                        calories_burned REAL,
                        diary_id INTEGER
                        )
                    """);
        }
    }

    @BeforeEach
    void setup() throws SQLException {
        instance = new ActivityDao(connection);

        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                    INSERT INTO Activity (activity_id, title, calories_burned, diary_id) VALUES
                    (1, 'Running', 350.0, 1),
                    (2, 'Swimming', 400.0, 2);
                    """);
        }
    }

    @AfterEach
    void cleanDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                    DELETE FROM Activity
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
        Optional<ActivityDto> expected = Optional.of(running);
        //Action
        Optional<ActivityDto> result = instance.findById(1);
        //Assert
        assertEquals(expected, result);
    }

    @Test
    void testFindByIdDoesNotExist() {
        System.out.println("Test : method findById - Not exist");
        //Arrange
        Optional<ActivityDto> expected = Optional.empty();
        //Action
        Optional<ActivityDto> result = instance.findById(100);
        //Assert
        assertEquals(expected, result);
    }

    @Test
    void testFindAllWithTwoActivities() {
        System.out.println("Test : method findAll - With two activities");
        //Arrange
        List<ActivityDto> expected = Arrays.asList(running, swimming);
        //Action
        List<ActivityDto> result = instance.findAll();
        //Assert
        assertEquals(expected, result);
    }

    @Test
    void testFindAllActivitiesByDiaryId() {
        System.out.println("Test : method findAllActivitiesByDiaryId - With two activities");
        //Arrange
        List<ActivityDto> expected = List.of(running);
        //Action
        List<ActivityDto> result = instance.findAllActivitiesByDiaryId(1);
        //Assert
        assertEquals(expected, result);
    }

    @Test
    void testFindAllActivitiesByDiaryIdNoActivities() {
        System.out.println("Test : method findAllActivitiesByDiaryId - No activities");
        //Arrange
        List<ActivityDto> expected = List.of();
        //Action
        List<ActivityDto> result = instance.findAllActivitiesByDiaryId(3);
        //Assert
        assertEquals(expected, result);
    }

    @Test
    void testInsertActivity() {
        System.out.println("Test : method save - Insert a new activity");
        //Arrange
        ActivityDto cycling = new ActivityDto(-1, "Cycling", 280.0, 3);
        Integer expected = 3;
        //Action
        Integer actual = instance.save(cycling);
        //Assert
        assertEquals(expected, actual);
    }

    @Test
    void testUpdateActivity() {
        System.out.println("Test : method save - Update an activity");
        //Arrange
        ActivityDto modifiedRunning = new ActivityDto(1, "Fast Running", 400.0, 1);
        Integer expected = 1;
        //Action
        Integer result = instance.save(modifiedRunning);
        //Assert
        assertEquals(expected, result);
    }

    @Test
    void testDeleteById() {
        System.out.println("Test : method deleteById - Delete an activity");
        //Arrange
        Optional<ActivityDto> expected = Optional.empty();
        //Action
        instance.deleteById(1);
        Optional<ActivityDto> result = instance.findById(1);
        //Assert
        assertEquals(expected, result);
    }
}