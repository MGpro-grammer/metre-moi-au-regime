package be.esi.prj.repository;

import be.esi.prj.dto.FoodDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FoodRepositoryTest {

    private FoodDao foodDao;
    private FoodRepository foodRepository;

    private FoodDto apple;
    private FoodDto banana;

    @BeforeEach
    void setUp() {
        apple = new FoodDto(1, "Apple", "123456789", "piece", 52.0, 0.3, 0.2, 14.0, 1.0, 1.0);
        banana = new FoodDto(2, "Banana", "987654321", "piece", 89.0, 1.1, 0.3, 23.0, 1.0, 1.0);

        foodDao = mock(FoodDao.class);

        when(foodDao.findAll()).thenReturn(List.of(apple, banana));

        foodRepository = new FoodRepository(foodDao);
    }

    @Test
    void testFindByIdDoesExist() {
        System.out.println("Test : method findById - Exist");
        //Arrange
        when(foodDao.findById(1)).thenReturn(Optional.of(apple));
        //Action
        Optional<FoodDto> result = foodRepository.findById(1);
        //Assert
        assertTrue(result.isPresent());
        assertEquals(apple, result.get());
        verify(foodDao, times(0)).findById(1);
    }

    @Test
    void testFindByIdDoesNotExist() {
        System.out.println("Test : method findById - Not exist");
        //Arrange
        when(foodDao.findById(100)).thenReturn(Optional.empty());
        //Action
        Optional<FoodDto> result = foodRepository.findById(100);
        //Assert
        assertTrue(result.isEmpty());
        verify(foodDao, times(1)).findById(100);
    }

    @Test
    void testFindByEanCodeDoesExist() {
        System.out.println("Test : method findByEanCode - Exist");
        //Arrange
        when(foodDao.findByEanCode("123456789")).thenReturn(Optional.of(apple));
        //Action
        Optional<FoodDto> result = foodRepository.findByEanCode("123456789");
        //Assert
        assertTrue(result.isPresent());
        assertEquals(apple, result.get());
        verify(foodDao, times(0)).findByEanCode("123456789");
    }

    @Test
    void testFindByEanCodeDoesNotExist() {
        System.out.println("Test : method findByEanCode - Not exist");
        //Arrange
        when(foodDao.findByEanCode("000000000")).thenReturn(Optional.empty());
        //Action
        Optional<FoodDto> result = foodRepository.findByEanCode("000000000");
        //Assert
        assertTrue(result.isEmpty());
        verify(foodDao, times(1)).findByEanCode("000000000");
    }

    @Test
    void testFindAllWithTwoFoods() {
        System.out.println("Test : method findAll - With two foods");
        //Action
        List<FoodDto> result = foodRepository.findAll();
        //Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(apple));
        assertTrue(result.contains(banana));
        verify(foodDao, times(1)).findAll();
    }

    @Test
    void testSaveInsertFood() {
        System.out.println("Test : method save - Insert a new food");
        //Arrange
        FoodDto orange = new FoodDto(null, "Orange", "555555555", "piece", 47.0, 0.9, 0.1, 11.8, 1.0, 1.0);
        when(foodDao.save(orange)).thenReturn(3);
        //Action
        Integer result = foodRepository.save(orange);
        //Assert
        assertEquals(3, result);
        Optional<FoodDto> savedFood = foodRepository.findById(3);
        assertTrue(savedFood.isPresent());
        assertEquals("Orange", savedFood.get().name());
        verify(foodDao, times(1)).save(orange);
    }

    @Test
    void testSaveUpdateFood() {
        System.out.println("Test : method save - Update a food");
        //Arrange
        FoodDto modifiedApple = new FoodDto(1, "Green Apple", "123456789", "piece", 55.0, 0.4, 0.2, 14.5, 1.0, 1.0);
        when(foodDao.save(modifiedApple)).thenReturn(1);
        //Action
        Integer result = foodRepository.save(modifiedApple);
        //Assert
        assertEquals(1, result);
        assertEquals(modifiedApple, foodRepository.findById(1).orElseThrow());
        verify(foodDao, times(1)).save(modifiedApple);
    }

    @Test
    void testDeleteById() {
        System.out.println("Test : method deleteById - Delete a food");
        //Arrange
        doNothing().when(foodDao).deleteById(1);
        //Action
        foodRepository.deleteById(1);
        //Assert
        assertTrue(foodRepository.findById(1).isEmpty());
        assertEquals(1, foodRepository.findAll().size());
        verify(foodDao, times(1)).deleteById(1);
    }
}