package be.esi.prj.controller;

import be.esi.prj.Main;
import be.esi.prj.enumeration.ConsumptionType;
import be.esi.prj.service.ViewModelService;
import be.esi.prj.viewmodel.FoodSearchViewModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Controller managing the photo dashboard interface.
 * Handles image drop functionality, food scanning from images, and food product addition to diary.
 */
public class DashboardPhotoController {

    @FXML
    private Pane home;
    @FXML
    private Pane user;
    @FXML
    private Pane search;
    @FXML
    private Pane activity;
    @FXML
    private Pane product;
    @FXML
    private Pane info;
    @FXML
    private Pane photoZone;
    @FXML
    private Label photoName;
    @FXML
    private Label statusScanner;
    @FXML
    private Label productName;
    @FXML
    private Button yesScan;
    @FXML
    private Button noScan;
    @FXML
    private Button yesProduct;
    @FXML
    private Button noProduct;
    @FXML
    private ComboBox<String> consumptionTypeCombo;
    @FXML
    private Spinner<Double> quantitySpinner;

    private File selectedFile;
    private ImageView imageView;
    private final FoodSearchViewModel foodSearchViewModel;

    /**
     * Constructs a new DashboardPhotoController.
     * Initializes the food search view model from the ViewModelService.
     */
    public DashboardPhotoController() {
        System.out.println("\tFXCONTROLLER | CONSTRUCTOR | DASHBOARD PHOTO");
        selectedFile = null;
        imageView = null;
        this.foodSearchViewModel = ViewModelService.getInstance().getFoodSearchViewModel();
    }

    /**
     * Initializes the controller after FXML elements are injected.
     * Sets up navigation handlers, photo zone, and UI components.
     */
    @FXML
    private void initialize() {
        System.out.println("\tFXCONTROLLER | INITIALIZE | DASHBOARD PHOTO");
        navigateToDashboardHome();
        navigateToDashboardProfile();
        navigateToDashboardSearch();
        navigateToDashboardActivity();
        navigateToDashboardProduct();
        navigateToDashboardInfo();

        initializeCombo();
        setupPhotoZone();
        setupButtons();
        resetInterface();
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
     * Configures the photo drop zone for image handling.
     * Sets up drag-and-drop functionality and initializes the image view.
     */
    private void setupPhotoZone() {
        System.out.println("\tFXCONTROLLER | SETUP PHOTO ZONE");

        photoZone.setOnDragOver(this::handleDragOver);
        photoZone.setOnDragDropped(this::handleDragDropped);

        imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(200);
        imageView.setFitWidth(200);
        photoZone.getChildren().add(imageView);
    }

    /**
     * Configures button actions for scanning, confirming, and canceling operations.
     * Sets event handlers for yes/no buttons for both scanning and product confirmation.
     */
    private void setupButtons() {
        System.out.println("\tFXCONTROLLER | SETUP BUTTONS");
        yesScan.setOnAction(event -> scanImage());
        yesProduct.setOnAction(event -> confirmProduct());
        noScan.setOnAction(event -> resetInterface());
        noProduct.setOnAction(event -> resetInterface());
    }

    /**
     * Handles the drag over event for image files.
     * Accepts the transfer mode when files are being dragged over the drop zone.
     *
     * @param event The drag event containing transfer data
     */
    private void handleDragOver(DragEvent event) {
        System.out.println("\tFXCONTROLLER | HANDLE DRAG OVER");
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
    }

    /**
     * Handles the file drop event for images.
     * Processes the dropped file, verifies it's a PNG, and displays it if valid.
     *
     * @param event The drag event containing the dropped file
     */
    private void handleDragDropped(DragEvent event) {
        System.out.println("\tFXCONTROLLER | HANDLE DRAG DROPPED");
        Dragboard db = event.getDragboard();
        boolean success = false;

        if (db.hasFiles() && !db.getFiles().isEmpty()) {
            selectedFile = db.getFiles().getFirst();

            if (isPngFile(selectedFile)) {
                displayImage(selectedFile);
                photoName.setText(selectedFile.getName());
                statusScanner.setText("Image PNG détectée");
                success = true;
            } else {
                statusScanner.setText("Le fichier n'est pas un PNG");
                resetImage();
            }
        }

        event.setDropCompleted(success);
        event.consume();
    }

    /**
     * Checks if a file is a PNG image.
     * Verifies the file extension to determine if it's a PNG file.
     *
     * @param file The file to check
     * @return True if the file is a PNG, false otherwise
     */
    private boolean isPngFile(File file) {
        System.out.println("\tFXCONTROLLER | CHECK IF FILE IS PNG");
        return file.getName().toLowerCase().endsWith(".png");
    }

    /**
     * Displays an image in the image view.
     * Loads the file as an image and sets it to the image view.
     *
     * @param file The image file to display
     */
    private void displayImage(File file) {
        System.out.println("\tFXCONTROLLER | DISPLAY IMAGE");
        try {
            Image image = new Image(file.toURI().toString());
            imageView.setImage(image);
        } catch (Exception e) {
            throw new ControllerException("Error found (display image): " + e.getMessage());
        }
    }

    /**
     * Initializes the consumption type combo box.
     * Populates the combo box with values from the ConsumptionType enumeration.
     */
    private void initializeCombo() {
        System.out.println("\tFXCONTROLLER | INITIALIZE COMBO");
        for (ConsumptionType consumptionType : ConsumptionType.values()) {
            consumptionTypeCombo.getItems().add(consumptionType.toString());
        }
    }

    /**
     * Processes the image scanning for food recognition.
     * Uses the food search view model to scan the image and identify food.
     */
    private void scanImage() {
        System.out.println("\tFXCONTROLLER | SCAN IMAGE");
            foodSearchViewModel.setImageFile(selectedFile);
            foodSearchViewModel.scanFood();
            foodSearchViewModel.setSelectedFood(foodSearchViewModel.getSearchResults().getFirst());
            statusScanner.setText(foodSearchViewModel.getSearchStatusMessage());
            productName.setText(foodSearchViewModel.getSearchResults().getFirst().getName());
    }

    /**
     * Confirms the identified product and adds it to the food diary.
     * Uses quantity and consumption type from UI controls to add the food item.
     */
    private void confirmProduct() {
        System.out.println("\tFXCONTROLLER | CONFIRM PRODUCT");
            foodSearchViewModel.setQuantity(quantitySpinner.getValue());
            foodSearchViewModel.setType(ConsumptionType.valueOf(consumptionTypeCombo.getValue()));
            foodSearchViewModel.addFoodToDiary();
            statusScanner.setText(foodSearchViewModel.getSearchStatusMessage());
    }

    /**
     * Resets the image display.
     * Clears the image view and resets the selected file.
     */
    private void resetImage() {
        System.out.println("\tFXCONTROLLER | RESET IMAGE");
        imageView.setImage(null);
        selectedFile = null;
    }

    /**
     * Resets the entire interface to its default state.
     * Clears the image, labels, and input controls.
     */
    private void resetInterface() {
        System.out.println("\tFXCONTROLLER | RESET INTERFACE");
        resetImage();
        photoName.setText("NAME_PHOTO");
        statusScanner.setText("STATUS_SCANNER");
        productName.setText("NAME_PRODUCT");
        quantitySpinner.getValueFactory().setValue(1.0);
        consumptionTypeCombo.getSelectionModel().clearSelection();
    }
}
