package be.esi.prj.repository;

import be.esi.prj.dto.ActivityDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ActivityRepositoryTest {

    private ActivityDao activityDao;
    private ActivityRepository activityRepository;

    private ActivityDto activity1;
    private ActivityDto activity2;

    @BeforeEach
    void setUp() {
        activity1 = new ActivityDto(1, "Running", 350.0, 1);
        activity2 = new ActivityDto(2, "Swimming", 400.0, 2);

        activityDao = mock(ActivityDao.class);

        when(activityDao.findAll()).thenReturn(List.of(activity1, activity2));

        activityRepository = new ActivityRepository(activityDao);
    }

    @Test
    void testFindByIdDoesExist() {
        System.out.println("Test : method findById - Exist");
        //Arrange
        when(activityDao.findById(1)).thenReturn(Optional.of(activity1));
        //Action
        Optional<ActivityDto> result = activityRepository.findById(1);
        //Assert
        assertTrue(result.isPresent());
        assertEquals(activity1, result.get());
        verify(activityDao, times(0)).findById(1);
    }

    @Test
    void testFindByIdDoesNotExist() {
        System.out.println("Test : method findById - Not exist");
        //Arrange
        when(activityDao.findById(100)).thenReturn(Optional.empty());
        //Action
        Optional<ActivityDto> result = activityRepository.findById(100);
        //Assert
        assertTrue(result.isEmpty());
        verify(activityDao, times(1)).findById(100);
    }

    @Test
    void testFindAllWithTwoActivities() {
        System.out.println("Test : method findAll - With two activities");
        //Action
        List<ActivityDto> result = activityRepository.findAll();
        //Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(activity1));
        assertTrue(result.contains(activity2));
        verify(activityDao, times(1)).findAll();
    }

    @Test
    void testFindAllActivitiesByDiaryId() {
        System.out.println("Test : method findAllActivitiesByDiaryId - With two activities");
        //Arrange
        when(activityDao.findAllActivitiesByDiaryId(1)).thenReturn(List.of(activity1));
        //Action
        List<ActivityDto> result = activityRepository.findAllActivitiesByDiaryId(1);
        //Assert
        assertEquals(1, result.size());
        assertTrue(result.contains(activity1));
        verify(activityDao, times(0)).findAllActivitiesByDiaryId(1);
    }

    @Test
    void testFindAllActivitiesByDiaryIdNoActivities() {
        System.out.println("Test : method findAllActivitiesByDiaryId - No activities");
        //Arrange
        when(activityDao.findAllActivitiesByDiaryId(3)).thenReturn(List.of());
        //Action
        List<ActivityDto> result = activityRepository.findAllActivitiesByDiaryId(3);
        //Assert
        assertTrue(result.isEmpty());
        verify(activityDao, times(1)).findAllActivitiesByDiaryId(3);
    }

    @Test
    void testSaveInsertActivity() {
        System.out.println("Test : method save - Insert a new activity");
        //Arrange
        ActivityDto insertActivity = new ActivityDto(null, "Cycling", 280.0, 3);
        when(activityDao.save(insertActivity)).thenReturn(3);
        //Action
        Integer result = activityRepository.save(insertActivity);
        //Assert
        assertEquals(3, result);
        Optional<ActivityDto> savedActivity = activityRepository.findById(3);
        assertTrue(savedActivity.isPresent());
        assertEquals("Cycling", savedActivity.get().title());
        verify(activityDao, times(1)).save(insertActivity);
    }

    @Test
    void testSaveUpdateActivity() {
        System.out.println("Test : method save - Update an activity");
        //Arrange
        ActivityDto updateActivity = new ActivityDto(1, "Fast Running", 400.0, 1);
        when(activityDao.save(updateActivity)).thenReturn(1);
        //Action
        Integer result = activityRepository.save(updateActivity);
        //Assert
        assertEquals(1, result);
        assertEquals(updateActivity, activityRepository.findById(1).orElseThrow());
        verify(activityDao, times(1)).save(updateActivity);
    }

    @Test
    void testDeleteById() {
        System.out.println("Test : method deleteById - delete an activity");
        //Arrange
        doNothing().when(activityDao).deleteById(1);
        //Action
        activityRepository.deleteById(1);
        //Assert
        assertTrue(activityRepository.findById(1).isEmpty());
        assertEquals(1, activityRepository.findAll().size());
        verify(activityDao, times(1)).deleteById(1);
    }
}