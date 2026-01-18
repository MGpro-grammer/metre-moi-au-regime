package be.esi.prj.controller;

import be.esi.prj.Main;
import be.esi.prj.service.ViewModelService;
import be.esi.prj.viewmodel.DiaryViewModel;
import be.esi.prj.viewmodel.FoodConsumedViewModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;

/**
 * Controller managing the product dashboard interface.
 * Handles navigation between dashboard sections and food product management operations.
 */
public class DashboardProductController {

    @FXML
    private Pane home;
    @FXML
    private Pane user;
    @FXML
    private Pane photo;
    @FXML
    private Pane search;
    @FXML
    private Pane activity;
    @FXML
    private Pane info;
    @FXML
    private ListView<FoodConsumedViewModel> listProduct;
    @FXML
    private Button removeProduct;
    @FXML
    private Label statusProduct;

    private final DiaryViewModel diaryViewModel;

    /**
     * Constructs a new DashboardProductController.
     * Initializes the diary view model from the ViewModelService.
     */
    public DashboardProductController() {
        System.out.println("\tFXCONTROLLER | CONSTRUCTOR | DASHBOARD PRODUCT");
        this.diaryViewModel = ViewModelService.getInstance().getDiaryViewModel();
    }

    /**
     * Initializes the controller after FXML elements are injected.
     * Sets up navigation handlers and food product management components.
     */
    @FXML
    public void initialize() {
        System.out.println("\tFXCONTROLLER | INITIALIZE | DASHBOARD PRODUCT");
        navigateToDashboardHome();
        navigateToDashboardProfile();
        navigateToDashboardPhoto();
        navigateToDashboardSearch();
        navigateToDashboardActivity();
        navigateToDashboardInfo();

        loadFoodList();
        clickOnFoodConsumedList();
        removeFoodButton();
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
                throw new ControllerException("Error found (navigate to dashboard profile)  : " + e.getMessage());
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
                throw new ControllerException("Error found (navigate to dashboard photo)  : " + e.getMessage());
            }
        });
    }

    /**
     * Sets up navigation to the search dashboard.
     * Handles mouse click event on the search pane.
     */
    private void navigateToDashboardSearch() {
        System.out.println("\tFXCONTROLLER | NAVIGATE TO DASHBOARD SEARCH");
        search.setOnMouseClicked(event -> {
            try {
                URL resource = Main.class.getResource("/view/dashboard-search.fxml");
                FXMLLoader fxmlLoader = new FXMLLoader(resource);
                Parent root = fxmlLoader.load();

                Scene currentScene = search.getScene();
                currentScene.setRoot(root);
            } catch (IOException e) {
                throw new ControllerException("Error found (navigate to dashboard search)  : " + e.getMessage());
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
     * Loads and displays the food product list.
     * Updates the list view with consumed foods from the diary view model.
     */
    private void loadFoodList() {
        System.out.println("\tFXCONTROLLER | LOAD FOOD LIST");
        diaryViewModel.loadLists();
        listProduct.setItems(diaryViewModel.getFoods());
        statusProduct.setText(diaryViewModel.getDiaryStatusMessage());
    }

    /**
     * Sets up food selection in the list view.
     * Updates the selected item in the diary view model when a food is clicked.
     */
    private void clickOnFoodConsumedList() {
        System.out.println("\tFXCONTROLLER | CLICK ON FOOD CONSUMED LIST");
        listProduct.setOnMouseClicked(event -> {
            FoodConsumedViewModel selectedFood = listProduct.getSelectionModel().getSelectedItem();
            if (selectedFood != null) {
                diaryViewModel.setSelectedItem(selectedFood);
            }
        });
    }

    /**
     * Configures the remove food button action.
     * Removes the selected food product and updates the status message.
     */
    private void removeFoodButton() {
        System.out.println("\tFXCONTROLLER | REMOVE BUTTON");
        removeProduct.setOnAction(event -> {
            diaryViewModel.removeItem();
            statusProduct.setText(diaryViewModel.getDiaryStatusMessage());
            loadFoodList();
        });
    }
}
