package be.esi.prj.repository;

import be.esi.prj.dto.DiaryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DiaryRepositoryTest {

    private DiaryDao diaryDao;
    private DiaryRepository diaryRepository;

    private DiaryDto diary1;
    private DiaryDto diary2;

    @BeforeEach
    void setUp() {
        diary1 = new DiaryDto(1, 1, LocalDate.of(2025, 1, 15), 55.5);
        diary2 = new DiaryDto(2, 2, LocalDate.of(2025, 2, 20), 88.2);

        diaryDao = mock(DiaryDao.class);

        when(diaryDao.findAll()).thenReturn(List.of(diary1, diary2));

        diaryRepository = new DiaryRepository(diaryDao);
    }

    @Test
    void testFindByIdDoesExist() {
        System.out.println("Test : method findById - Exist");
        //Arrange
        when(diaryDao.findById(1)).thenReturn(Optional.of(diary1));
        //Action
        Optional<DiaryDto> result = diaryRepository.findById(1);
        //Assert
        assertTrue(result.isPresent());
        assertEquals(diary1, result.get());
        verify(diaryDao, times(0)).findById(1);
    }

    @Test
    void testFindByIdDoesNotExist() {
        System.out.println("Test : method findById - Not exist");
        //Arrange
        when(diaryDao.findById(100)).thenReturn(Optional.empty());
        //Action
        Optional<DiaryDto> result = diaryRepository.findById(100);
        //Assert
        assertTrue(result.isEmpty());
        verify(diaryDao, times(1)).findById(100);
    }

    @Test
    void testFindByUserIdAndDateExists() {
        System.out.println("Test : method findByUserIdAndDate - Exists");
        //Arrange
        when(diaryDao.findByUserIdAndDate(1, LocalDate.of(2025, 1, 15))).thenReturn(Optional.of(diary1));
        //Action
        Optional<DiaryDto> result = diaryRepository.findByUserIdAndDate(1, LocalDate.of(2025, 1, 15));
        //Assert
        assertTrue(result.isPresent());
        assertEquals(diary1, result.get());
        verify(diaryDao, times(0)).findByUserIdAndDate(1, LocalDate.of(2025, 1, 15));
    }

    @Test
    void testFindByUserIdAndDateDoesNotExist() {
        System.out.println("Test : method findByUserIdAndDate - Does not exist");
        //Arrange
        when(diaryDao.findByUserIdAndDate(1, LocalDate.of(2025, 2, 20))).thenReturn(Optional.empty());
        //Action
        Optional<DiaryDto> result = diaryRepository.findByUserIdAndDate(1, LocalDate.of(2025, 2, 20));
        //Assert
        assertTrue(result.isEmpty());
        verify(diaryDao, times(1)).findByUserIdAndDate(1, LocalDate.of(2025, 2, 20));
    }

    @Test
    void testFindAllWithTwoDiaries() {
        System.out.println("Test : method findAll - With two diaries");
        //Action
        List<DiaryDto> result = diaryRepository.findAll();
        //Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(diary1));
        assertTrue(result.contains(diary2));
        verify(diaryDao, times(1)).findAll();
    }

    @Test
    void testSaveInsertDiary() {
        System.out.println("Test : method save - Insert a new diary");
        //Arrange
        DiaryDto insertDiary = new DiaryDto(null, 3, LocalDate.of(2025, 3, 10), 56.3);
        when(diaryDao.save(insertDiary)).thenReturn(3);
        //Action
        Integer result = diaryRepository.save(insertDiary);
        //Assert
        assertEquals(3, result);
        Optional<DiaryDto> savedDiary = diaryRepository.findById(3);
        assertTrue(savedDiary.isPresent());
        assertEquals(56.3, savedDiary.get().recordedWeight());
        verify(diaryDao, times(1)).save(insertDiary);
    }

    @Test
    void testSaveUpdateDiary() {
        System.out.println("Test : method save - Update a diary");
        //Arrange
        DiaryDto updateDiary = new DiaryDto(1, 1, LocalDate.of(2025, 1, 15), 56.2);
        when(diaryDao.save(updateDiary)).thenReturn(1);
        //Action
        Integer result = diaryRepository.save(updateDiary);
        //Assert
        assertEquals(1, result);
        assertEquals(updateDiary, diaryRepository.findById(1).orElseThrow());
        verify(diaryDao, times(1)).save(updateDiary);
    }

    @Test
    void testDeleteById() {
        System.out.println("Test : method deleteById - delete a diary");
        //Arrange
        doNothing().when(diaryDao).deleteById(1);
        //Action
        diaryRepository.deleteById(1);
        //Assert
        assertTrue(diaryRepository.findById(1).isEmpty());
        assertEquals(1, diaryRepository.findAll().size());
        verify(diaryDao, times(1)).deleteById(1);
    }
}