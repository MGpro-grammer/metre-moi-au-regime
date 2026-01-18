package be.esi.prj.controller;

import be.esi.prj.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;

/**
 * Controller managing the info dashboard interface.
 * Handles navigation between different dashboard sections.
 */
public class DashboardInfoController {

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
    private Pane product;

    /**
     * Initializes the controller after FXML elements are injected.
     * Sets up navigation handlers for all dashboard sections.
     */
    @FXML
    public void initialize() {
        System.out.println("\tFXCONTROLLER | INITIALIZE DASHBOARD INFO CONTROLLER");

        navigateToDashboardHome();
        navigateToDashboardProfile();
        navigateToDashboardPhoto();
        navigateToDashboardSearch();
        navigateToDashboardActivity();
        navigateToDashboardProduct();
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
                throw new ControllerException("Error found (navigate to dashboard home)  : " + e.getMessage());
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
}
