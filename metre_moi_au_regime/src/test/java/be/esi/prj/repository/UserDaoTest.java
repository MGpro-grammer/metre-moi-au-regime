package be.esi.prj.repository;

import be.esi.prj.dto.UserDto;
import be.esi.prj.enumeration.ActivityLevel;
import be.esi.prj.enumeration.Gender;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserDaoTest {

    private static Connection connection;
    private UserDao instance;

    private final UserDto georges;
    private final UserDto ian;

    public UserDaoTest() {
        georges = new UserDto(1, "georges@gmail.com", "pass123",
                "Georges", 23, Gender.MALE, 1.73, 53.2,
                65.0, LocalDate.of(2025, 1,1),
                LocalDate.of(2025, 1, 31), ActivityLevel.SEDENTARY);
        ian = new UserDto(2, "ian@gmail.com", "pass456",
                "Ian", 23, Gender.MALE, 1.78, 90.0,
                70.0, LocalDate.of(2025, 2, 1),
                LocalDate.of(2025, 2, 27), ActivityLevel.VERY_ACTIVE);
    }

    @BeforeAll
    static void setupDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                    CREATE TABLE User (
                        user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        email TEXT NOT NULL UNIQUE,
                        password TEXT NOT NULL,
                        name TEXT NOT NULL,
                        age INTEGER,
                        gender TEXT CHECK (gender IN ('MALE', 'FEMALE')),
                        height REAL,
                        weight REAL,
                        goal_weight REAL,
                        start_date DATE,
                        end_date DATE,
                        activity_level TEXT CHECK (activity_level IN ('SEDENTARY', 'LIGHT', 'MODERATE', 'ACTIVE', 'VERY_ACTIVE'))
                        )
                    """);
        }
    }

    @BeforeEach
    void setup() throws SQLException {
        instance = new UserDao(connection);

        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                    INSERT INTO User (user_id, email, password, name, age, gender, height, weight, goal_weight, start_date, end_date, activity_level) VALUES
                    (1, 'georges@gmail.com', 'pass123', 'Georges', 23, 'MALE', 1.73, 53.2, 65.0, '2025-01-01', '2025-01-31', 'SEDENTARY'),
                    (2, 'ian@gmail.com', 'pass456', 'Ian', 23, 'MALE', 1.78, 90.0, 70.0, '2025-02-01', '2025-02-27', 'VERY_ACTIVE');
                    """);
        }
    }

    @AfterEach
    void cleanDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                    DELETE FROM User
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
        Optional<UserDto> expected = Optional.of(georges);
        //Action
        Optional<UserDto> result = instance.findById(1);
        //Assert
        assertEquals(expected, result);
    }

    @Test
    void testFindByIdDoesNotExist() {
        System.out.println("Test : method findById - Not exist");
        //Arrange
        Optional<UserDto> expected = Optional.empty();
        //Action
        Optional<UserDto> result = instance.findById(100);
        //Assert
        assertEquals(expected, result);
    }

    @Test
    void testFindByEmailDoesExist() {
        System.out.println("Test : method findByEmail - Exist");
        //Arrange
        Optional<UserDto> expected = Optional.of(georges);
        //Action
        Optional<UserDto> result = instance.findByEmail("georges@gmail.com");
        //Assert
        assertEquals(expected, result);
    }

    @Test
    void testFindByEmailDoesNotExist() {
        System.out.println("Test : method findByEmail - Not exist");
        //Arrange
        Optional<UserDto> expected = Optional.empty();
        //Action
        Optional<UserDto> result = instance.findByEmail("nonexistent@gmail.com");
        //Assert
        assertEquals(expected, result);
    }

    @Test
    void testFindByEmailSecondUser() {
        System.out.println("Test : method findByEmail - Second user");
        //Arrange
        Optional<UserDto> expected = Optional.of(ian);
        //Action
        Optional<UserDto> result = instance.findByEmail("ian@gmail.com");
        //Assert
        assertEquals(expected, result);
    }

    @Test
    void testFindByAllWithTwoUsers() {
        System.out.println("Test : method findByAll - With two users");
        //Arrange
        List<UserDto> expected = Arrays.asList(georges, ian);
        //Action
        List<UserDto> result = instance.findAll();
        //Assert
        assertEquals(expected, result);
    }

    @Test
    void testInsertUser() {
        System.out.println("Test : method save - Insert a new user");
        //Arrange
        UserDto anass = new UserDto(-1, "anass@gmail.com", "pass789",
                "Anass", 20, Gender.MALE, 1.86, 88.0,
                80.0, LocalDate.of(2025, 3, 1), LocalDate.of(2025, 3, 30),
                ActivityLevel.LIGHT);
        Integer expected = 3;
        //Action
        Integer actual = instance.save(anass);
        //Assert
        assertEquals(expected, actual);
    }

    @Test
    void testUpdateUser() {
        System.out.println("Test : method save - Update a user");
        //Arrange
        UserDto modifiedGeorges = new UserDto(1, "georges@gmail.com", "pass123",
                "Georges", 23, Gender.MALE, 1.73, 55.3,
                65.0, LocalDate.of(2025, 1,1),
                LocalDate.of(2025, 1, 31), ActivityLevel.MODERATE);
        Integer expected = 1;
        //Action
        Integer result = instance.save(modifiedGeorges);
        //Assert
        assertEquals(expected, result);
    }

    @Test
    void testDeleteById() {
        System.out.println("Test : method deleteById - delete an user");
        //Arrange
        Optional<UserDto> expected = Optional.empty();
        //Action
        instance.deleteById(1);
        Optional<UserDto> result = instance.findById(1);
        //Assert
        assertEquals(expected, result);
    }
}