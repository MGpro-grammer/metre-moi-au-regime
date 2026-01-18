package be.esi.prj.repository;

import be.esi.prj.dto.FoodConsumedDto;
import be.esi.prj.enumeration.ConsumptionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FoodConsumedRepositoryTest {

    private FoodConsumedDao foodConsumedDao;
    private FoodConsumedRepository foodConsumedRepository;

    private FoodConsumedDto foodConsumed1;
    private FoodConsumedDto foodConsumed2;

    @BeforeEach
    void setUp() {
        foodConsumed1 = new FoodConsumedDto(1, 1, 101, ConsumptionType.PER_PERCENT, 250.0);
        foodConsumed2 = new FoodConsumedDto(2, 1, 102, ConsumptionType.PER_QUANTITY, 350.0);

        foodConsumedDao = mock(FoodConsumedDao.class);

        when(foodConsumedDao.findAll()).thenReturn(List.of(foodConsumed1, foodConsumed2));

        foodConsumedRepository = new FoodConsumedRepository(foodConsumedDao);
    }

    @Test
    void testFindByIdDoesExist() {
        System.out.println("Test : method findById - Exist");
        // Arrange
        when(foodConsumedDao.findById(1)).thenReturn(Optional.of(foodConsumed1));
        // Act
        Optional<FoodConsumedDto> result = foodConsumedRepository.findById(1);
        // Assert
        assertTrue(result.isPresent());
        assertEquals(foodConsumed1, result.get());
        verify(foodConsumedDao, times(0)).findById(1);
    }

    @Test
    void testFindByIdDoesNotExist() {
        System.out.println("Test : method findById - Not exist");
        // Arrange
        when(foodConsumedDao.findById(100)).thenReturn(Optional.empty());
        // Act
        Optional<FoodConsumedDto> result = foodConsumedRepository.findById(100);
        // Assert
        assertTrue(result.isEmpty());
        verify(foodConsumedDao, times(1)).findById(100);
    }

    @Test
    void testFindByDiaryIdAndFoodIdDoesExist() {
        System.out.println("Test : method findByDiaryIdAndFoodId - Exist");
        // Arrange
        when(foodConsumedDao.findByDiaryIdAndFoodId(1, 101)).thenReturn(Optional.of(foodConsumed1));
        // Act
        Optional<FoodConsumedDto> result = foodConsumedRepository.findByDiaryIdAndFoodId(1, 101);
        // Assert
        assertTrue(result.isPresent());
        assertEquals(foodConsumed1, result.get());
        verify(foodConsumedDao, times(0)).findByDiaryIdAndFoodId(1, 101);
    }

    @Test
    void testFindByDiaryIdAndFoodIdDoesNotExist() {
        System.out.println("Test : method findByDiaryIdAndFoodId - Not exist");
        // Arrange
        when(foodConsumedDao.findByDiaryIdAndFoodId(1, 999)).thenReturn(Optional.empty());
        // Act
        Optional<FoodConsumedDto> result = foodConsumedRepository.findByDiaryIdAndFoodId(1, 999);
        // Assert
        assertTrue(result.isEmpty());
        verify(foodConsumedDao, times(1)).findByDiaryIdAndFoodId(1, 999);
    }

    @Test
    void testFindAllWithTwoRecords() {
        System.out.println("Test : method findAll - With two records");
        // Act
        List<FoodConsumedDto> result = foodConsumedRepository.findAll();
        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(foodConsumed1));
        assertTrue(result.contains(foodConsumed2));
        verify(foodConsumedDao, times(1)).findAll();
    }

    @Test
    void testFindAllFoodsByDiaryId() {
        System.out.println("Test : method findAllFoodsByDiaryId - With two records");
        // Arrange
        when(foodConsumedDao.findAllFoodsByDiaryId(1)).thenReturn(List.of(foodConsumed1, foodConsumed2));
        // Act
        List<FoodConsumedDto> result = foodConsumedRepository.findAllFoodsByDiaryId(1);
        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(foodConsumed1));
        assertTrue(result.contains(foodConsumed2));
        verify(foodConsumedDao, times(0)).findAllFoodsByDiaryId(1);
    }

    @Test
    void testFindAllFoodsByDiaryIdEmpty() {
        System.out.println("Test : method findAllFoodsByDiaryId - Empty list");
        // Arrange
        when(foodConsumedDao.findAllFoodsByDiaryId(2)).thenReturn(List.of());
        // Act
        List<FoodConsumedDto> result = foodConsumedRepository.findAllFoodsByDiaryId(2);
        // Assert
        assertTrue(result.isEmpty());
        verify(foodConsumedDao, times(1)).findAllFoodsByDiaryId(2);
    }

    @Test
    void testSaveInsertFoodConsumed() {
        System.out.println("Test : method save - Insert a new food consumed");
        // Arrange
        FoodConsumedDto insertFoodConsumed = new FoodConsumedDto(null, 2, 103, ConsumptionType.PER_UNIT, 400.0);
        when(foodConsumedDao.save(insertFoodConsumed)).thenReturn(3);
        // Act
        Integer result = foodConsumedRepository.save(insertFoodConsumed);
        // Assert
        assertEquals(3, result);
        Optional<FoodConsumedDto> savedFoodConsumed = foodConsumedRepository.findById(3);
        assertTrue(savedFoodConsumed.isPresent());
        assertEquals(2, savedFoodConsumed.get().diaryId());
        assertEquals(103, savedFoodConsumed.get().foodId());
        verify(foodConsumedDao, times(1)).save(insertFoodConsumed);
    }

    @Test
    void testSaveUpdateFoodConsumed() {
        System.out.println("Test : method save - Update a food consumed");
        // Arrange
        FoodConsumedDto updateFoodConsumed = new FoodConsumedDto(1, 1, 101, ConsumptionType.PER_PERCENT, 300.0);
        when(foodConsumedDao.save(updateFoodConsumed)).thenReturn(1);
        // Act
        Integer result = foodConsumedRepository.save(updateFoodConsumed);
        // Assert
        assertEquals(1, result);
        assertEquals(updateFoodConsumed, foodConsumedRepository.findById(1).orElseThrow());
        verify(foodConsumedDao, times(1)).save(updateFoodConsumed);
    }

    @Test
    void testDeleteById() {
        System.out.println("Test : method deleteById - delete a food consumed");
        // Arrange
        doNothing().when(foodConsumedDao).deleteById(1);
        // Act
        foodConsumedRepository.deleteById(1);
        // Assert
        assertTrue(foodConsumedRepository.findById(1).isEmpty());
        assertEquals(1, foodConsumedRepository.findAll().size());
        verify(foodConsumedDao, times(1)).deleteById(1);
    }
}