package be.esi.prj.model;

import be.esi.prj.dto.FoodDto;
import be.esi.prj.repository.FoodRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FoodFacadePerformanceTest {

    private FoodFacade foodFacade;

    @BeforeEach
    void setUp() {
        FoodRepository foodRepository = new FoodRepository();
        foodFacade = new FoodFacade(foodRepository);
    }

    @Test
    void findFoodByName_threadedShouldBeFasterThanSequential() {
        // Arrange
        String searchTerm = "chicken";

        // Act - Measure sequential version
        long startNoSync = System.currentTimeMillis();
        List<FoodDto> noSyncResults = foodFacade.findFoodByNameNoSync(searchTerm);
        long noSyncTime = System.currentTimeMillis() - startNoSync;

        // Act - Measure threaded version
        long startThreaded = System.currentTimeMillis();
        List<FoodDto> threadedResults = foodFacade.findFoodByName(searchTerm);
        long threadedTime = System.currentTimeMillis() - startThreaded;

        // Log results
        System.out.println("--- Performance Comparison: findFoodByName ---");
        System.out.println("Sequential search time: " + noSyncTime + "ms (" + noSyncResults.size() + " results)");
        System.out.println("Threaded search time: " + threadedTime + "ms (" + threadedResults.size() + " results)");
        if (threadedTime > 0) {
            System.out.printf("Performance ratio (Sequential/Threaded): %.2fx faster%n", (double) noSyncTime / threadedTime);
        }
        System.out.println("---------------------------------------------");

        // Assert
        assertNotNull(noSyncResults);
        assertNotNull(threadedResults);
        assertTrue(noSyncTime > 0, "Sequential search should take some time.");
        assertTrue(threadedTime > 0, "Threaded search should take some time.");
        assertTrue(threadedTime < noSyncTime, "Threaded version should be faster than sequential version.");
    }
}