package be.esi.prj.repository;

import be.esi.prj.dto.DiaryDto;
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

class DiaryDaoTest {

    private static Connection connection;
    private DiaryDao instance;

    private final DiaryDto diary1;
    private final DiaryDto diary2;

    public DiaryDaoTest() {
        diary1 = new DiaryDto(1, 1, LocalDate.of(2025, 1, 15), 55.5);
        diary2 = new DiaryDto(2, 2, LocalDate.of(2025, 2, 20), 88.2);
    }

    @BeforeAll
    static void setupDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                    CREATE TABLE Diary (
                        diary_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        user_id INTEGER NOT NULL,
                        date DATE NOT NULL,
                        recorded_weight REAL NOT NULL
                        )
                    """);
        }
    }

    @BeforeEach
    void setup() throws SQLException {
        instance = new DiaryDao(connection);

        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                    INSERT INTO Diary (diary_id, user_id, date, recorded_weight) VALUES
                    (1, 1, '2025-01-15', 55.5),
                    (2, 2, '2025-02-20', 88.2);
                    """);
        }
    }

    @AfterEach
    void cleanDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                    DELETE FROM Diary
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
        Optional<DiaryDto> expected = Optional.of(diary1);
        //Action
        Optional<DiaryDto> result = instance.findById(1);
        //Assert
        assertEquals(expected, result);
    }

    @Test
    void testFindByIdDoesNotExist() {
        System.out.println("Test : method findById - Not exist");
        //Arrange
        Optional<DiaryDto> expected = Optional.empty();
        //Action
        Optional<DiaryDto> result = instance.findById(100);
        //Assert
        assertEquals(expected, result);
    }

    @Test
    void testFindByUserIdAndDateExists() {
        System.out.println("Test : method findByUserIdAndDate - Exists");
        //Arrange
        Optional<DiaryDto> expected = Optional.of(diary1);
        //Action
        Optional<DiaryDto> result = instance.findByUserIdAndDate(1, LocalDate.of(2025, 1, 15));
        //Assert
        assertEquals(expected, result);
    }

    @Test
    void testFindByUserIdAndDateDoesNotExist() {
        System.out.println("Test : method findByUserIdAndDate - Does not exist");
        //Arrange
        Optional<DiaryDto> expected = Optional.empty();
        //Action
        Optional<DiaryDto> result = instance.findByUserIdAndDate(1, LocalDate.of(2025, 2, 20));
        //Assert
        assertEquals(expected, result);
    }

    @Test
    void testFindAllWithTwoDiaries() {
        System.out.println("Test : method findAll - With two diaries");
        //Arrange
        List<DiaryDto> expected = Arrays.asList(diary1, diary2);
        //Action
        List<DiaryDto> result = instance.findAll();
        //Assert
        assertEquals(expected, result);
    }

    @Test
    void testInsertDiary() {
        System.out.println("Test : method save - Insert a new diary");
        //Arrange
        DiaryDto newDiary = new DiaryDto(-1, 1, LocalDate.of(2025, 3, 10), 56.3);
        Integer expected = 3;
        //Action
        Integer actual = instance.save(newDiary);
        //Assert
        assertEquals(expected, actual);
    }

    @Test
    void testUpdateDiary() {
        System.out.println("Test : method save - Update a diary");
        //Arrange
        DiaryDto modifiedDiary = new DiaryDto(1, 1, LocalDate.of(2025, 1, 15), 56.2);
        Integer expected = 1;
        //Action
        Integer result = instance.save(modifiedDiary);
        //Assert
        assertEquals(expected, result);
    }

    @Test
    void testDeleteById() {
        System.out.println("Test : method deleteById - delete a diary");
        //Arrange
        Optional<DiaryDto> expected = Optional.empty();
        //Action
        instance.deleteById(1);
        Optional<DiaryDto> result = instance.findById(1);
        //Assert
        assertEquals(expected, result);
    }
}