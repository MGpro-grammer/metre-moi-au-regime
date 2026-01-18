package be.esi.prj.controller;

import be.esi.prj.Main;
import be.esi.prj.enumeration.ConsumptionType;
import be.esi.prj.service.ViewModelService;
import be.esi.prj.viewmodel.FoodSearchViewModel;
import be.esi.prj.viewmodel.FoodViewModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;

/**
 * Controller managing the search dashboard interface.
 * Handles navigation between dashboard sections, food search functionality, and food diary additions.
 */
public class DashboardSearchController {

    @FXML
    private Pane home;
    @FXML
    private Pane user;
    @FXML
    private Pane photo;
    @FXML
    private Pane activity;
    @FXML
    private Pane product;
    @FXML
    private Pane info;
    @FXML
    private ListView<FoodViewModel> foodList;
    @FXML
    private Label foodSelected;
    @FXML
    private TextField foodField;
    @FXML
    private Button yesButton;
    @FXML
    private Label statusValidation;
    @FXML
    private ComboBox<String> consumptionTypeCombo;
    @FXML
    private Spinner<Double> quantitySpinner;

    private final FoodSearchViewModel foodSearchViewModel;

    /**
     * Constructs a new DashboardSearchController.
     * Initializes the food search view model from the ViewModelService.
     */
    public DashboardSearchController() {
        System.out.println("\tFXCONTROLLER | CONSTRUCTOR | DASHBOARD SEARCH");
        this.foodSearchViewModel = ViewModelService.getInstance().getFoodSearchViewModel();
    }

    /**
     * Initializes the controller after FXML elements are injected.
     * Sets up navigation handlers, resets the interface, and initializes UI components.
     */
    @FXML
    public void initialize() {
        System.out.println("\tFXCONTROLLER | INITIALIZE | DASHBOARD SEARCH");
        navigateToDashboardHome();
        navigateToDashboardProfile();
        navigateToDashboardPhoto();
        navigateToDashboardActivity();
        navigateToDashboardProduct();
        navigateToDashboardInfo();

        resetInterface();

        initializeCombo();
        clickOnFoodList();
        addFoodButton();
    }

    /**
     * Initializes the consumption type combo box.
     * Populates the combo box with values from the ConsumptionType enumeration.
     */
    public void initializeCombo() {
        System.out.println("\tFXCONTROLLER | INITIALIZE UTILS | DASHBOARD SEARCH");
        for (ConsumptionType consumptionType : ConsumptionType.values()) {
            consumptionTypeCombo.getItems().add(consumptionType.toString());
        }
    }

    /**
     * Sets up navigation to the home dashboard.
     * Handles mouse click event on the home pane.
     */
    private void navigateToDashboardHome() {
        System.out.println("\tFXCONTROLLER | NAVIGATE TO DASHBOARD HOME");
        home.setOnMouseClicked(event -> {
            try {
                URL resource = Main.class.getResource("/view/dashboard-home.fxml");
                FXMLLoader fxmlLoader = new FXMLLoader(resource);
                Parent root = fxmlLoader.load();

                Scene currentScene = home.getScene();
                currentScene.setRoot(root);
            } catch (IOException e) {
                throw new ControllerException("Error found (navigate to dashboard home): " + e.getMessage());
            }
        });
    }

    /**
     * Sets up navigation to the profile dashboard.
     * Handles mouse click event on the user pane.
     */
    private void navigateToDashboardProfile() {
        System.out.println("\tFXCONTROLLER | NAVIGATE TO DASHBOARD PROFILE");
        user.setOnMouseClicked(event -> {
            try {
                URL resource = Main.class.getResource("/view/dashboard-profile.fxml");
                FXMLLoader fxmlLoader = new FXMLLoader(resource);
                Parent root = fxmlLoader.load();

                Scene currentScene = user.getScene();
                currentScene.setRoot(root);
            } catch (IOException e) {
                throw new ControllerException("Error found (navigate to dashboard profile): " + e.getMessage());
            }
        });
    }

    /**
     * Sets up navigation to the photo dashboard.
     * Handles mouse click event on the photo pane.
     */
    private void navigateToDashboardPhoto() {
        System.out.println("\tFXCONTROLLER | NAVIGATE TO DASHBOARD PHOTO");
        photo.setOnMouseClicked(event -> {
            try {
                URL resource = Main.class.getResource("/view/dashboard-photo.fxml");
                FXMLLoader fxmlLoader = new FXMLLoader(resource);
                Parent root = fxmlLoader.load();

                Scene currentScene = photo.getScene();
                currentScene.setRoot(root);
            } catch (IOException e) {
                throw new ControllerException("Error found (navigate to dashboard photo): " + e.getMessage());
            }
        });
    }

    /**
     * Sets up navigation to the activity dashboard.
     * Handles mouse click event on the activity pane.
     */
    private void navigateToDashboardActivity() {
        System.out.println("\tFXCONTROLLER | NAVIGATE TO DASHBOARD ACTIVITY");
        activity.setOnMouseClicked(event -> {
            try {
                URL resource = Main.class.getResource("/view/dashboard-activity.fxml");
                FXMLLoader fxmlLoader = new FXMLLoader(resource);
                Parent root = fxmlLoader.load();

                Scene currentScene = activity.getScene();
                currentScene.setRoot(root);
            } catch (IOException e) {
                throw new ControllerException("Error found (navigate to dashboard activity)  : " + e.getMessage());
            }
        });
    }

    /**
     * Sets up navigation to the product dashboard.
     * Handles mouse click event on the product pane.
     */
    private void navigateToDashboardProduct() {
        System.out.println("\tFXCONTROLLER | NAVIGATE TO DASHBOARD PRODUCT");
        product.setOnMouseClicked(event -> {
            try {
                URL resource = Main.class.getResource("/view/dashboard-product.fxml");
                FXMLLoader fxmlLoader = new FXMLLoader(resource);
                Parent root = fxmlLoader.load();

                Scene currentScene = product.getScene();
                currentScene.setRoot(root);
            } catch (IOException e) {
                throw new ControllerException("Error found (navigate to dashboard product)  : " + e.getMessage());
            }
        });
    }

    /**
     * Sets up navigation to the info dashboard.
     * Handles mouse click event on the info pane.
     */
    private void navigateToDashboardInfo() {
        System.out.println("\tFXCONTROLLER | NAVIGATE TO DASHBOARD INFO");
        info.setOnMouseClicked(event -> {
            try {
                URL resource = Main.class.getResource("/view/dashboard-info.fxml");
                FXMLLoader fxmlLoader = new FXMLLoader(resource);
                Parent root = fxmlLoader.load();

                Scene currentScene = info.getScene();
                currentScene.setRoot(root);
            } catch (IOException e) {
                throw new ControllerException("Error found (navigate to dashboard info)  : " + e.getMessage());
            }
        });
    }

    /**
     * Loads and displays the food search results.
     * Updates the list view with food items from search results.
     */
    private void loadFoodList() {
        System.out.println("\tFXCONTROLLER | LOAD FOOD LIST");
        foodList.setItems(foodSearchViewModel.getSearchResults());
    }

    /**
     * Sets up food selection in the list view.
     * Updates the selected food label and the selected food in view model when clicked.
     */
    private void clickOnFoodList() {
        System.out.println("\tFXCONTROLLER | CLICK ON FOOD LIST");
        foodList.setOnMouseClicked(event -> {
            FoodViewModel selectedFood = foodList.getSelectionModel().getSelectedItem();
            if (selectedFood != null) {
                foodSelected.setText(String.valueOf(selectedFood));
                foodSearchViewModel.setSelectedFood(selectedFood);
            }
        });
    }

    /**
     * Resets the interface to its default state.
     * Clears input fields, selections, and resets spinner values.
     */
    private void resetInterface() {
        System.out.println("\tFXCONTROLLER | RESET INTERFACE");
        foodField.clear();
        consumptionTypeCombo.getSelectionModel().clearSelection();
        quantitySpinner.getValueFactory().setValue(0.0);
    }

    /**
     * Handles the search action when the search button is clicked.
     * Initiates food search and updates the list with results.
     */
    @FXML
    private void handleSearch() {
        System.out.println("\tFXCONTROLLER | HANDLE SEARCH");
        searchFood();
        loadFoodList();
    }

    /**
     * Executes the food search functionality.
     * Gets the search query from the text field and passes it to the view model.
     */
    private void searchFood() {
        System.out.println("\tFXCONTROLLER | SEARCH FOOD");
        String search = foodField.getText();

        foodSearchViewModel.setSearchQuery(search);
        foodSearchViewModel.searchFood();
    }

    /**
     * Configures the add food button action.
     * Sets up consumption type and quantity, then adds the selected food to diary.
     */
    private void addFoodButton() {
        System.out.println("\tFXCONTROLLER | ADD FOOD BUTTON");
        yesButton.setOnAction(event -> {
            foodSearchViewModel.setType(ConsumptionType.valueOf(consumptionTypeCombo.getValue()));
            foodSearchViewModel.setQuantity(Double.parseDouble(String.valueOf(quantitySpinner.getValue())));
            foodSearchViewModel.addFoodToDiary();
            String statusMessage = foodSearchViewModel.getSearchStatusMessage();
            statusValidation.setText(statusMessage);
            resetInterface();
        });
    }
}
