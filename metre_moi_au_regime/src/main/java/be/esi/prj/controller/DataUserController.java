package be.esi.prj.controller;

import be.esi.prj.Main;
import be.esi.prj.enumeration.ActivityLevel;
import be.esi.prj.enumeration.Gender;
import be.esi.prj.service.ViewModelService;
import be.esi.prj.viewmodel.NewProfileViewModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Arc;

import java.io.IOException;
import java.net.URL;

/**
 * Controller managing the user data collection interface.
 * Handles step-by-step progression through various profile information screens.
 */
public class DataUserController {
    @FXML
    private Arc progressionDataUser;
    @FXML
    private Pane leftButton;
    @FXML
    private Pane rightButton;
    @FXML
    private Pane namePane;
    @FXML
    private Pane agePane;
    @FXML
    private Pane genderPane;
    @FXML
    private Pane weightHeightPane;
    @FXML
    private Pane goalWeightPane;
    @FXML
    private Pane startDatePane;
    @FXML
    private Pane endDatePane;
    @FXML
    private Pane activityLevelPane;
    @FXML
    private Pane validation;
    @FXML
    private Button validationButton;
    @FXML
    private TextField dataName;
    @FXML
    private Spinner<Integer> dataAge;
    @FXML
    private Button dataMaleGender;
    @FXML
    private Button dataFemaleGender;
    @FXML
    private Spinner<Double> dataWeight;
    @FXML
    private Spinner<Double> dataHeight;
    @FXML
    private Spinner<Double> dataGoalWeight;
    @FXML
    private DatePicker dataStartDate;
    @FXML
    private DatePicker dataEndDate;
    @FXML
    private ComboBox<String> dataActivityLevel;

    private Integer progressionIndex;
    private final NewProfileViewModel newProfileViewModel;
    private String selectedGender;

    /**
     * Constructs a new DataUserController.
     * Initializes progression index, gets the new profile view model from service,
     * and sets default gender selection.
     */
    public DataUserController() {
        System.out.println("\tFXCONTROLLER | CONSTRUCTOR | DATAUSER");
        this.progressionIndex = 0;
        this.newProfileViewModel = ViewModelService.getInstance().getNewProfileViewModel();
        this.selectedGender = "MALE";
    }

    /**
     * Initializes the controller after FXML elements are injected.
     * Sets up the initial view state, configures UI components, and initializes event handlers.
     */
    public void initialize() {
        System.out.println("\tFXCONTROLLER | INITIALIZE | DATAUSER");
        hideAllPanes();
        adjustment();
        progressionDataUserButton();
        initializeDataUserToLogin();
        initializeComboBox();
        initializeGenderButtons();
    }

    /**
     * Performs initial UI adjustments.
     * Shows the name pane as the first step and hides the validation pane.
     */
    private void adjustment() {
        System.out.println("\tFXCONTROLLER | ADJUSTMENT");
        namePane.setVisible(true);
        validation.setVisible(false);
    }

    /**
     * Hides all data collection panes.
     * Called before showing a specific pane during navigation.
     */
    private void hideAllPanes() {
        System.out.println("\tFXCONTROLLER | HIDE ALL PANES");
        namePane.setVisible(false);
        agePane.setVisible(false);
        genderPane.setVisible(false);
        weightHeightPane.setVisible(false);
        goalWeightPane.setVisible(false);
        startDatePane.setVisible(false);
        endDatePane.setVisible(false);
        activityLevelPane.setVisible(false);
    }

    /**
     * Updates the progress indicator arc.
     * Increases or decreases the arc length to show current progression.
     *
     * @param progression The amount to modify the arc length (positive or negative)
     */
    private void updateProgressionDataUser(double progression) {
        System.out.println("\tFXCONTROLLER | UPDATE PROGRESSION DATAUSER");
        progressionDataUser.setLength(progressionDataUser.getLength() + progression);
    }

    /**
     * Sets up the navigation button event handlers.
     * Configures left and right buttons for navigating through data collection steps.
     */
    private void progressionDataUserButton() {
        System.out.println("\tFXCONTROLLER | PROGRESSION DATAUSER BUTTON");
        leftButton.setOnMouseClicked(event -> {
            if (progressionDataUser.getLength() > 0) {
                updateProgressionDataUser(-45.0);
                previousDataUser();
                switchDataUser(progressionIndex);
            }
        });

        rightButton.setOnMouseClicked(event -> {
            if (progressionDataUser.getLength() < 360) {
                updateProgressionDataUser(45.0);
                nextDataUser();
                switchDataUser(progressionIndex);
            }
        });
    }

    /**
     * Switches between different data input panes based on current progression index.
     * Shows the appropriate pane for the current data collection step.
     *
     * @param currentIndex The current step index in the data collection process
     */
    private void switchDataUser(Integer currentIndex) {
        System.out.println("\tFXCONTROLLER | SWITCH DATAUSER");

        hideAllPanes();

        validation.setVisible(currentIndex == 7);

        switch (currentIndex) {
            case 0 -> namePane.setVisible(true);
            case 1 -> agePane.setVisible(true);
            case 2 -> genderPane.setVisible(true);
            case 3 -> weightHeightPane.setVisible(true);
            case 4 -> goalWeightPane.setVisible(true);
            case 5 -> startDatePane.setVisible(true);
            case 6 -> endDatePane.setVisible(true);
            case 7 -> activityLevelPane.setVisible(true);
        }
    }

    /**
     * Advances to the next data collection step.
     * Increments the progression index if not at the final step.
     */
    private void nextDataUser() {
        System.out.println("\tFXCONTROLLER | NEXT DATAUSER");
        if (progressionIndex < 7) {
            progressionIndex++;
        }
    }

    /**
     * Returns to the previous data collection step.
     * Decrements the progression index if not at the first step.
     */
    private void previousDataUser() {
        System.out.println("\tFXCONTROLLER | PREVIOUS DATAUSER");
        if (progressionIndex > 0) {
            progressionIndex--;
        }
    }

    /**
     * Navigates to the login screen after profile creation.
     * Saves the collected user data and redirects to the login page if successful.
     */
    private void navigateToLogin() {
        System.out.println("\tFXCONTROLLER | NAVIGATE TO DATA USER VIEW | LOGIN");
        try {
            if (newProfileViewModel.save()) {
                URL resource = Main.class.getResource("/view/login.fxml");
                FXMLLoader fxmlLoader = new FXMLLoader(resource);
                Parent root = fxmlLoader.load();

                Scene currentScene = validation.getScene();
                currentScene.setRoot(root);
            } else {
                String message = newProfileViewModel.getStatusMessage();
                showAlert(message);
            }
        } catch (IOException e) {
            throw new ControllerException("Error found (navigate to dashboard view): " + e.getMessage());
        }
    }

    /**
     * Displays an error alert dialog with a specific message.
     * Used to show validation errors during profile creation.
     *
     * @param message The error message to display
     */
    private void showAlert(String message) {
        System.out.println("\tFXCONTROLLER | SHOW ALERT | LOGIN");
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Sets up the validation button action.
     * Configures the event handler for submitting and saving user data.
     */
    private void initializeDataUserToLogin() {
        System.out.println("\tFXCONTROLLER | INITIALIZE DATA USER TO DASHBOARD");
        validationButton.setOnAction(event ->
            {
                startDataUser();
                navigateToLogin();
            }
        );
    }

    /**
     * Initializes the activity level combo box.
     * Populates the combo box with activity level options and sets default value.
     */
    private void initializeComboBox() {
        System.out.println("\tFXCONTROLLER | INITIALIZE COMBOBOX");
        dataActivityLevel.getItems().addAll("SEDENTARY", "LIGHT", "MODERATE", "ACTIVE","VERY_ACTIVE");
        dataActivityLevel.setValue("SEDENTARY");
    }

    /**
     * Configures gender selection button actions.
     * Sets up event handlers for male and female selection buttons.
     */
    private void initializeGenderButtons() {
        System.out.println("\tFXCONTROLLER | INITIALIZE GENDER BUTTONS");
        dataMaleGender.setOnAction(event -> {
            selectedGender = "MALE";
        });
        dataFemaleGender.setOnAction(event -> {
            selectedGender = "FEMALE";
        });
    }

    /**
     * Collects and saves user data to the profile view model.
     * Transfers values from all form fields to the new profile view model.
     */
    private void startDataUser() {
        System.out.println("\tFXCONTROLLER | START DATA USER");
        newProfileViewModel.setName(dataName.getText());
        newProfileViewModel.setAge(dataAge.getValue());
        newProfileViewModel.setGender(Gender.valueOf(selectedGender));
        newProfileViewModel.setHeight(dataHeight.getValue());
        newProfileViewModel.setWeight(dataWeight.getValue());
        newProfileViewModel.setGoalWeight(dataGoalWeight.getValue());
        newProfileViewModel.setStartDate(dataStartDate.getValue());
        newProfileViewModel.setEndDate(dataEndDate.getValue());
        newProfileViewModel.setActivityLevel(ActivityLevel.valueOf(dataActivityLevel.getValue()));
    }
}
