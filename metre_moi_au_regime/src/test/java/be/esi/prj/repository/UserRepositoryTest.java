package be.esi.prj.repository;

import be.esi.prj.dto.UserDto;
import be.esi.prj.enumeration.ActivityLevel;
import be.esi.prj.enumeration.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserRepositoryTest {

    private UserDao userDao;
    private UserRepository userRepository;

    private UserDto user;
    private UserDto user2;

    @BeforeEach
    void setUp() {
        user = new UserDto(1, "georges@gmail.com", "pass123",
                "Georges", 23, Gender.MALE, 1.73, 53.2,
                65.0, LocalDate.of(2025, 1,1),
                LocalDate.of(2025, 1, 31), ActivityLevel.SEDENTARY);
        user2 = new UserDto(2, "ian@gmail.com", "pass456",
                "Ian", 23, Gender.MALE, 1.78, 90.0,
                70.0, LocalDate.of(2025, 2, 1),
                LocalDate.of(2025, 2, 27), ActivityLevel.ACTIVE);

        userDao = mock(UserDao.class);

        when(userDao.findAll()).thenReturn(List.of(user, user2));

        userRepository = new UserRepository(userDao);
    }

    @Test
    void testFindByIdDoesExist() {
        System.out.println("Test : method findById - Exist");
        //Arrange
        when(userDao.findById(1)).thenReturn(Optional.of(user));
        //Action
        Optional<UserDto> result = userRepository.findById(1);
        //Assert
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        verify(userDao, times(0)).findById(1);
    }

    @Test
    void testFindByIdDoesNotExist() {
        System.out.println("Test : method findById - Not exist");
        //Arrange
        when(userDao.findById(100)).thenReturn(Optional.empty());
        //Action
        Optional<UserDto> result = userRepository.findById(100);
        //Assert
        assertTrue(result.isEmpty());
        verify(userDao, times(1)).findById(100);
    }

    @Test
    void testFindByEmailDoesExist() {
        System.out.println("Test : method findByEmail - Exist");
        //Arrange
        when(userDao.findByEmail("georges@gmail.com")).thenReturn(Optional.of(user));
        //Action
        Optional<UserDto> result = userRepository.findByEmail("georges@gmail.com");
        //Assert
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        verify(userDao, times(0)).findByEmail("georges@gmail.com");
    }

    @Test
    void testFindByEmailDoesNotExist() {
        System.out.println("Test : method findByEmail - Not exist");
        //Arrange
        when(userDao.findByEmail("nonexistent@gmail.com")).thenReturn(Optional.empty());
        //Action
        Optional<UserDto> result = userRepository.findByEmail("nonexistent@gmail.com");
        //Assert
        assertTrue(result.isEmpty());
        verify(userDao, times(1)).findByEmail("nonexistent@gmail.com");
    }

    @Test
    void testFindByEmailSecondUser() {
        System.out.println("Test : method findByEmail - Second user");
        //Arrange
        when(userDao.findByEmail("ian@gmail.com")).thenReturn(Optional.of(user2));
        //Action
        Optional<UserDto> result = userRepository.findByEmail("ian@gmail.com");
        //Assert
        assertTrue(result.isPresent());
        assertEquals(user2, result.get());
        verify(userDao, times(0)).findByEmail("ian@gmail.com");
    }

    @Test
    void testFindAllWithTwoUsers() {
        System.out.println("Test : method findAll - With two users");
        //Action
        List<UserDto> result = userRepository.findAll();
        //Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(user));
        assertTrue(result.contains(user2));
        verify(userDao, times(1)).findAll();
    }

    @Test
    void testSaveInsertUser() {
        System.out.println("Test : method save - Insert a new user");
        //Arrange
        UserDto insertUser = new UserDto(null, "anass@gmail.com", "pass789",
                "Anass", 20, Gender.MALE, 1.86, 88.0,
                80.0, LocalDate.of(2025, 3, 1), LocalDate.of(2025, 3, 30),
                ActivityLevel.LIGHT);
        when(userDao.save(insertUser)).thenReturn(3);
        //Action
        Integer result = userRepository.save(insertUser);
        //Assert
        assertEquals(3, result);
        Optional<UserDto> savedUser = userRepository.findById(3);
        assertTrue(savedUser.isPresent());
        assertEquals("anass@gmail.com", savedUser.get().email());
        verify(userDao, times(1)).save(insertUser);
    }

    @Test
    void testSaveUpdateUser() {
        System.out.println("Test : method save - Update a user");
        //Arrange
        UserDto updateUser = new UserDto(1, "georges@gmail.com", "pass123",
                "Georges", 23, Gender.MALE, 1.73, 53.2,
                65.0, LocalDate.of(2025, 1,1),
                LocalDate.of(2025, 1, 31), ActivityLevel.MODERATE);
        when(userDao.save(updateUser)).thenReturn(1);
        //Action
        Integer result = userRepository.save(updateUser);
        //Assert
        assertEquals(1, result);
        assertEquals(updateUser, userRepository.findById(1).orElseThrow());
        verify(userDao, times(1)).save(updateUser);
    }

    @Test
    void testDeleteById() {
        System.out.println("Test : method deleteById - delete a user");
        //Arrange
        doNothing().when(userDao).deleteById(1);
        //Action
        userRepository.deleteById(1);
        //Assert
        assertTrue(userRepository.findById(1).isEmpty());
        assertEquals(1, userRepository.findAll().size());
        verify(userDao, times(1)).deleteById(1);
    }
}