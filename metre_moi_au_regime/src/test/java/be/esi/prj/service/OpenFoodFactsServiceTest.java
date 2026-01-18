package be.esi.prj.service;

import be.esi.prj.dto.FoodDto;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

class OpenFoodFactsServiceTest {

    @Test
    void testGetFoodByEanCode() throws Exception {
        Optional<FoodDto> foodOpt = OpenFoodFactsService.findFoodByEanCode("6111259343344");

        FoodDto food = foodOpt.get();

        System.out.println("Food Name: " + food);
    }

    @Test
    void testGetFoodByName() throws Exception {
        String foodName = "cola";
        List<FoodDto> foodList = OpenFoodFactsService.findFoodByName(foodName, -1, -1);

        if (foodList.isEmpty()) {
            System.out.println("No food found with name: " + foodName);
        } else {
            foodList.forEach(food -> System.out.println("Found Food: " + food));
        }
    }
}