package be.esi.prj.controller;

import be.esi.prj.Main;
import be.esi.prj.service.ViewModelService;
import be.esi.prj.viewmodel.AuthentificationViewModel;
import be.esi.prj.viewmodel.DashBoardViewModel;
import be.esi.prj.viewmodel.NutrientGoalViewModel;
import be.esi.prj.viewmodel.UserProfileViewModel;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Arc;

import java.io.IOException;
import java.net.URL;

/**
 * Controller managing the dashboard home interface.
 * Handles navigation between dashboard sections and displays user nutrient goals with progress indicators.
 */
public class DashboardHomeController {

    @FXML
    private Pane user;
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
    private Label userName;
    @FXML
    private Button logout;
    @FXML
    private Arc gaugeCircle1;
    @FXML
    private Arc gaugeCircle2;
    @FXML
    private Arc gaugeCircle3;
    @FXML
    private Arc gaugeCircle4;
    @FXML
    private Label info1;
    @FXML
    private Label info2;
    @FXML
    private Label info3;
    @FXML
    private Label info4;
    @FXML
    private Label current1;
    @FXML
    private Label current2;
    @FXML
    private Label current3;
    @FXML
    private Label current4;
    @FXML
    private Label max1;
    @FXML
    private Label max2;
    @FXML
    private Label max3;
    @FXML
    private Label max4;
    @FXML
    private Label percentage1;
    @FXML
    private Label percentage2;
    @FXML
    private Label percentage3;
    @FXML
    private Label percentage4;

    private final UserProfileViewModel userProfileViewModel;
    private final DashBoardViewModel dashBoardViewModel;
    private final AuthentificationViewModel authentificationViewModel;
    private final ObservableList<NutrientGoalViewModel> nutrientGoals;

    /**
     * Constructs a new DashboardHomeController.
     * Initializes view models and nutrient goals from the ViewModelService.
     */
    public DashboardHomeController() {
        System.out.println("\tFXCONTROLLER | CONSTRUCTOR | DASHBOARD HOME");
        this.userProfileViewModel = ViewModelService.getInstance().getUserProfileViewModel();
        this.dashBoardViewModel = ViewModelService.getInstance().getDashBoardViewModel();
        this.authentificationViewModel = ViewModelService.getInstance().getAuthentificationViewModel();
        this.nutrientGoals = dashBoardViewModel.getNutrientGoals();
    }

    /**
     * Initializes the controller after FXML elements are injected.
     * Sets up navigation handlers and loads profile and nutrient data.
     */
    @FXML
    public void initialize() {
        System.out.println("\tFXCONTROLLER | INITIALIZE | DASHBOARD");
        navigateToDashboardProfile();
        navigateToDashboardPhoto();
        navigateToDashboardSearch();
        navigateToDashboardActivity();
        navigateToDashboardProduct();
        navigateToDashboardInfo();
        navigateToLogIn();

        userProfileViewModel.loadProfile();
        dashBoardViewModel.loadNutrientGoals();
        updateUserName();

        initializeCurrent();
        initializeMax();
        initializeName();
        initializeGauge();
        initializePercentage();
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
     * Sets up navigation to the login screen.
     * Logs out the current user and redirects to login view.
     */
    private void navigateToLogIn() {
        System.out.println("\tFXCONTROLLER | NAVIGATE TO LOG IN");
        logout.setOnMouseClicked(event -> {
            try {
                authentificationViewModel.logout();

                URL resource = Main.class.getResource("/view/login.fxml");
                FXMLLoader fxmlLoader = new FXMLLoader(resource);
                Parent root = fxmlLoader.load();

                Scene currentScene = logout.getScene();
                currentScene.setRoot(root);
            } catch (IOException e) {
                throw new ControllerException("Error found (navigate to dashboard log in)  : " + e.getMessage());
            }
        });
    }

    /**
     * Updates the displayed user name.
     * Uses the name from user profile or displays a default placeholder if empty.
     */
    private void updateUserName() {
        String name = userProfileViewModel.getName();
        if (name != null && !name.isEmpty()) {
            userName.setText(name);
        } else {
            userName.setText("USER_NAME");
        }
    }

    /**
     * Initializes nutrient names in the UI.
     * Populates the name labels with values from the nutrient goals.
     */
    private void initializeName() {
        System.out.println("\tFXCONTROLLER | INITIALIZE NAME");
        info1.setText(nutrientGoals.getFirst().getNutrientName());
        info2.setText(nutrientGoals.get(1).getNutrientName());
        info3.setText(nutrientGoals.get(2).getNutrientName());
        info4.setText(nutrientGoals.get(3).getNutrientName());
    }

    /**
     * Initializes current nutrient values in the UI.
     * Populates the current value labels with consumed values from nutrient goals.
     */
    private void initializeCurrent() {
        System.out.println("\tFXCONTROLLER | INITIALIZE CURRENT");
        current1.setText(nutrientGoals.getFirst().getConsumedValue());
        current2.setText(nutrientGoals.get(1).getConsumedValue());
        current3.setText(nutrientGoals.get(2).getConsumedValue());
        current4.setText(nutrientGoals.get(3).getConsumedValue());
    }

    /**
     * Initializes maximum nutrient values in the UI.
     * Populates the maximum value labels with goal values from nutrient goals.
     */
    private void initializeMax() {
        System.out.println("\tFXCONTROLLER | INITIALIZE MAX");
        max1.setText(nutrientGoals.getFirst().getGoalValue());
        max2.setText(nutrientGoals.get(1).getGoalValue());
        max3.setText(nutrientGoals.get(2).getGoalValue());
        max4.setText(nutrientGoals.get(3).getGoalValue());
    }

    /**
     * Initializes gauge arc lengths in the UI.
     * Sets the arc lengths based on progress towards nutrient goals.
     */
    private void initializeGauge() {
        System.out.println("\tFXCONTROLLER | INITIALIZE GAUGE");
        gaugeCircle1.setLength(nutrientGoals.getFirst().getGaugeAngle());
        gaugeCircle2.setLength(nutrientGoals.get(1).getGaugeAngle());
        gaugeCircle3.setLength(nutrientGoals.get(2).getGaugeAngle());
        gaugeCircle4.setLength(nutrientGoals.get(3).getGaugeAngle());
    }

    /**
     * Initializes percentage labels in the UI.
     * Populates the percentage labels with progress percentages from nutrient goals.
     */
    private void initializePercentage() {
        System.out.println("\tFXCONTROLLER | INITIALIZE PERCENTAGE");
        percentage1.setText(nutrientGoals.getFirst().getProgessPercentage());
        percentage2.setText(nutrientGoals.get(1).getProgessPercentage());
        percentage3.setText(nutrientGoals.get(2).getProgessPercentage());
        percentage4.setText(nutrientGoals.get(3).getProgessPercentage());
    }

}
