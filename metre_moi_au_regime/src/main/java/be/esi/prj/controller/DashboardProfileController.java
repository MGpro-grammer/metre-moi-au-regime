package be.esi.prj.controller;

import be.esi.prj.Main;
import be.esi.prj.enumeration.ActivityLevel;
import be.esi.prj.enumeration.Gender;
import be.esi.prj.service.ViewModelService;
import be.esi.prj.viewmodel.UserProfileViewModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;

/**
 * Controller managing the user profile dashboard interface.
 * Handles navigation between dashboard sections and user profile management operations.
 */
public class DashboardProfileController {

    @FXML
    private Pane home;
    @FXML
    private Pane photo;
    @FXML
    private Pane search;
    @FXML
    private Pane activity;
    @FXML
    private Pane product;
    @FXML
    private Pane info;
    @FXML
    private TextField name;
    @FXML
    private Spinner<Integer> age;
    @FXML
    private ComboBox<String> gender;
    @FXML
    private Spinner<Double> height;
    @FXML
    private Spinner<Double> weight;
    @FXML
    private Spinner<Double> goalWeight;
    @FXML
    private DatePicker startDate;
    @FXML
    private DatePicker endDate;
    @FXML
    private ComboBox<String> activityLevel;
    @FXML
    private Button yesButton;
    @FXML
    private Button noButton;
    @FXML
    private Label statusModification;
    

    private final UserProfileViewModel userProfileViewModel;

    /**
     * Constructs a new DashboardProfileController.
     * Initializes the user profile view model from the ViewModelService.
     */
    public DashboardProfileController() {
        System.out.println("\tFXCONTROLLER | CONSTRUCTOR | DASHBOARD PROFIL");
        this.userProfileViewModel = ViewModelService.getInstance().getUserProfileViewModel();
    }

    /**
     * Initializes the controller after FXML elements are injected.
     * Sets up navigation handlers, combo boxes, button actions, and loads the user profile.
     */
    @FXML
    public void initialize() {
        System.out.println("\tFXCONTROLLER | INITIALIZE | DASHBOARD PROFILE");
        navigateToDashboardHome();
        navigateToDashboardPhoto();
        navigateToDashboardSearch();
        navigateToDashboardActivity();
        navigateToDashboardProduct();
        navigateToDashboardInfo();

        initializeCombo();
        initializeButtons();
        
        userProfileViewModel.loadProfile();
        displayProfile();
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
                throw new ControllerException("Error found (navigate to dashboard search): " + e.getMessage());
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
     * Initializes the combo boxes with enumeration values.
     * Populates gender and activity level combo boxes with respective enum values.
     */
    private void initializeCombo() {
        System.out.println("\tFXCONTROLLER | INITIALIZE COMBOBOXES");
        for (Gender genderType : Gender.values()) {
            gender.getItems().add(genderType.toString());
        }

        for (ActivityLevel activity: ActivityLevel.values()) {
            activityLevel.getItems().add(activity.toString());
        }
    }

    /**
     * Initializes button actions for profile management.
     * Sets up yes/no button handlers for saving or discarding profile changes.
     */
    private void initializeButtons() {
        System.out.println("\tFXCONTROLLER | INITIALIZE BUTTONS");
        yesButton.setOnAction(event -> {
            saveProfile();
            userProfileViewModel.save();
            statusModification.setText(userProfileViewModel.getStatusMessage());
        });
        noButton.setOnAction(event -> displayProfile());
    }

    /**
     * Displays the current user profile data in the UI.
     * Populates form fields with values from the user profile view model.
     */
    private void displayProfile() {
        System.out.println("\tFXCONTROLLER | DISPLAY PROFILE");
        name.setText(userProfileViewModel.getName());
        age.getValueFactory().setValue(userProfileViewModel.getAge());
        gender.getSelectionModel().select(userProfileViewModel.getGender().toString());
        height.getValueFactory().setValue(userProfileViewModel.getHeight());
        weight.getValueFactory().setValue(userProfileViewModel.getWeight());
        goalWeight.getValueFactory().setValue(userProfileViewModel.getGoalWeight());
        startDate.setValue(userProfileViewModel.getStartDate());
        endDate.setValue(userProfileViewModel.getEndDate());
        activityLevel.getSelectionModel().select(userProfileViewModel.getActivityLevel().toString());
    }

    /**
     * Saves the user profile data from UI to view model.
     * Transfers values from form fields to the user profile view model.
     */
    private void saveProfile() {
        System.out.println("\tFXCONTROLLER | SAVE PROFILE");
        userProfileViewModel.setName(name.getText());
        userProfileViewModel.setAge(age.getValue());
        userProfileViewModel.setGender(Gender.valueOf(gender.getSelectionModel().getSelectedItem()));
        userProfileViewModel.setHeight(height.getValue());
        userProfileViewModel.setWeight(weight.getValue());
        userProfileViewModel.setGoalWeight(goalWeight.getValue());
        userProfileViewModel.setStartDate(startDate.getValue());
        userProfileViewModel.setEndDate(endDate.getValue());
        userProfileViewModel.setActivityLevel(ActivityLevel.valueOf(activityLevel.getSelectionModel().getSelectedItem()));
    }
}
