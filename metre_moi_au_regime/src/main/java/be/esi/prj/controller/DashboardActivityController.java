package be.esi.prj.controller;

import be.esi.prj.Main;
import be.esi.prj.service.ViewModelService;
import be.esi.prj.viewmodel.ActivityViewModel;
import be.esi.prj.viewmodel.DiaryViewModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;

/**
 * Controller managing the activities dashboard activity interface.
 * Handles navigation between dashboard sections and activity management operations.
 */
public class DashboardActivityController {

    @FXML
    private Pane home;
    @FXML
    private Pane user;
    @FXML
    private Pane photo;
    @FXML
    private Pane search;
    @FXML
    private Pane product;
    @FXML
    private Pane info;
    @FXML
    private Button addActivity;
    @FXML
    private TextField nameActivity;
    @FXML
    private Spinner<Double> spendCalories;
    @FXML
    private Button removeActivity;
    @FXML
    private ListView<ActivityViewModel> listActivity;
    @FXML
    private Label statusActivity;

    private final DiaryViewModel diaryViewModel;

    /**
     * Constructs a new DashboardActivityController.
     * Initializes the diary view model from the ViewModelService.
     */
    public DashboardActivityController() {
        System.out.println("\tFXCONTROLLER | CONSTRUCTOR | DASHBOARD ACTIVITY");
        this.diaryViewModel = ViewModelService.getInstance().getDiaryViewModel();
    }

    /**
     * Initializes the controller after FXML elements are injected.
     * Sets up navigation handlers and activity management components.
     */
    @FXML
    public void initialize() {
        System.out.println("\tFXCONTROLLER | INITIALIZE | DASHBOARD ACTIVITY");
        navigateToDashboardHome();
        navigateToDashboardProfile();
        navigateToDashboardPhoto();
        navigateToDashboardSearch();
        navigateToDashboardProduct();
        navigateToDashboardInfo();

        loadActivityList();
        addActivityButton();
        clickOnActivityList();
        removeActivityButton();
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
     * Sets up navigation to the product dashboard.
     * Handles mouse click event on the product pane.
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
     * Sets up navigation to the product dashboard.
     * Handles mouse click event on the product pane.
     */
    private void addActivityButton() {
        System.out.println("\tFXCONTROLLER | ADD ACTIVITY BUTTON");
        addActivity.setOnAction(event -> {
            diaryViewModel.setTitle(nameActivity.getText());
            diaryViewModel.setCaloriesBurned(spendCalories.getValue());
            diaryViewModel.addActivity();
            statusActivity.setText(diaryViewModel.getDiaryStatusMessage());
            loadActivityList();
        });
    }

    /**
     * Sets up navigation to the product dashboard.
     * Handles mouse click event on the product pane.
     */
    private void clickOnActivityList() {
        System.out.println("\tFXCONTROLLER | CLICK ON ACTIVITY LIST");
        listActivity.setOnMouseClicked(event -> {
            ActivityViewModel selectedActivity = listActivity.getSelectionModel().getSelectedItem();
            if (selectedActivity != null) {
                diaryViewModel.setSelectedItem(selectedActivity);
            }
        });
    }

    /**
     * Sets up navigation to the product dashboard.
     * Handles mouse click event on the product pane.
     */
    private void removeActivityButton() {
        System.out.println("\tFXCONTROLLER | REMOVE ACTIVITY BUTTON");
        removeActivity.setOnAction(event -> {

            diaryViewModel.removeItem();
            statusActivity.setText(diaryViewModel.getDiaryStatusMessage());
            loadActivityList();
        });
    }

    /**
     * Sets up navigation to the product dashboard.
     * Handles mouse click event on the product pane.
     */
    private void loadActivityList() {
        System.out.println("\tFXCONTROLLER | LOAD ACTIVITY LIST");
        diaryViewModel.loadLists();
        listActivity.setItems(diaryViewModel.getActivities());
        statusActivity.setText(diaryViewModel.getDiaryStatusMessage());
    }
}
