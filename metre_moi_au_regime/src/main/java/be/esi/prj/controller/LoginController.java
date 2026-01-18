package be.esi.prj.controller;

import be.esi.prj.Main;
import be.esi.prj.service.ViewModelService;
import be.esi.prj.viewmodel.AuthentificationViewModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import java.io.IOException;
import java.net.URL;

/**
 * Controller managing the login and registration interface.
 * Handles user authentication, registration, and navigation to other application screens.
 */
public class LoginController {
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button togglePasswordButton;
    @FXML
    private PasswordField registerPasswordField;
    @FXML
    private Button toggleRegisterPasswordButton;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Button toggleConfirmPasswordButton;
    @FXML
    private Pane loginPane;
    @FXML
    private Pane registerPane;
    @FXML
    private Button signInButton;
    @FXML
    private Button backToLoginButton;
    @FXML
    private Button registerButton;
    @FXML
    private Button loginButton;
    @FXML
    private TextField emailField;
    @FXML
    private TextField registerEmailField;

    private TextField passwordTextField;
    private HBox passwordContainer;
    private TextField registerPasswordTextField;
    private TextField confirmPasswordTextField;
    private HBox registerPasswordContainer;
    private HBox confirmPasswordContainer;

    private FontIcon loginEyeIcon;
    private FontIcon loginEyeOffIcon;
    private FontIcon registerEyeIcon;
    private FontIcon registerEyeOffIcon;
    private FontIcon confirmEyeIcon;
    private FontIcon confirmEyeOffIcon;

    private final AuthentificationViewModel authentificationViewModel;

    /**
     * Constructs a new LoginController.
     * Initializes the authentication view model from the ViewModelService.
     */
    public LoginController() {
        System.out.println("\tFXCONTROLLER | CONSTRUCTOR | LOGIN");
        this.authentificationViewModel = ViewModelService.getInstance().getAuthentificationViewModel();
    }

    /**
     * Initializes the controller after FXML elements are injected.
     * Sets up icons, password fields, and button actions.
     */
    @FXML
    public void initialize() {
        System.out.println("\tFXCONTROLLER | INITIALIZE | LOGIN");
        initializeIcons();
        initializeLoginPasswordField();
        initializeRegisterPasswordFields();
        initializeLoginRegisterButtons();
        initializeRegisterToDataUser();
    }

    /**
     * Initializes the login/register toggle buttons.
     * Sets up event handlers for switching between login and registration views.
     */
    private void initializeLoginRegisterButtons() {
        System.out.println("\tFXCONTROLLER | INITIALIZE LOGIN/REGISTER BUTTONS | LOGIN");
        signInButton.setOnAction(event -> switchToRegister());
        backToLoginButton.setOnAction(event -> switchToLogin());
    }

    /**
     * Initializes the register and login action buttons.
     * Sets up event handlers for registration and login processes.
     */
    private void initializeRegisterToDataUser() {
        System.out.println("\tFXCONTROLLER | INITIALIZE REGISTER TO DATA USER | LOGIN");
        registerButton.setOnAction(event ->
                {
                    startRegistration();
                    navigateToDataUserView();
                }

        );
        loginButton.setOnAction(event ->
                {
                    startLogin();
                    navigateToDashboardView();
                }
        );
    }

    /**
     * Prepares login data for authentication.
     * Transfers email and password from UI fields to the view model.
     */
    private void startLogin() {
        System.out.println("\tFXCONTROLLER | INITIALIZE LOGIN | LOGIN");
        authentificationViewModel.setEmail(emailField.getText());
        authentificationViewModel.setPassword(passwordField.getText());
    }

    /**
     * Prepares registration data.
     * Transfers email, password, and password confirmation from UI fields to the view model.
     */
    private void startRegistration() {
        System.out.println("\tFXCONTROLLER | INITIALIZE REGISTRATION | LOGIN");
        authentificationViewModel.setEmail(registerEmailField.getText());
        authentificationViewModel.setPassword(registerPasswordField.getText());
        authentificationViewModel.setConfirmPassword(confirmPasswordField.getText());
    }

    /**
     * Navigates to the data user view after successful registration.
     * Attempts to start the registration process and redirects if successful.
     */
    private void navigateToDataUserView() {
        System.out.println("\tFXCONTROLLER | NAVIGATE TO DATA USER VIEW | LOGIN");
        try {
            if (authentificationViewModel.startRegistrationProcess()) {
                URL resource = Main.class.getResource("/view/datauser.fxml");
                FXMLLoader fxmlLoader = new FXMLLoader(resource);
                Parent root = fxmlLoader.load();

                Scene currentScene = registerPane.getScene();
                currentScene.setRoot(root);
            } else {
                String message = authentificationViewModel.loginStatusMessage();
                showAlert(message);
            }
        } catch (IOException e) {
            throw new ControllerException("Error found (navigate to data user view): " + e.getMessage());
        }
    }

    /**
     * Navigates to the dashboard view after successful login.
     * Attempts to authenticate and redirects to the home dashboard if successful.
     */
    private void navigateToDashboardView() {
        System.out.println("\tFXCONTROLLER | NAVIGATE TO DASHBOARD VIEW | LOGIN");
        try {
            if (authentificationViewModel.login()) {
                URL resource = Main.class.getResource("/view/dashboard-home.fxml");
                FXMLLoader fxmlLoader = new FXMLLoader(resource);
                Parent root = fxmlLoader.load();

                Scene currentScene = loginPane.getScene();
                currentScene.setRoot(root);
            } else {
                String message = authentificationViewModel.loginStatusMessage();
                showAlert(message);
            }
        } catch (IOException e) {
            throw new ControllerException("Error found (navigate to dashboard view): " + e.getMessage());
        }
    }

    /**
     * Displays an error alert dialog with a specific message.
     * Used to show authentication or validation errors.
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
     * Toggles password visibility between masked and plain text.
     * Switches between password field and text field, updating the eye icon.
     *
     * @param passField Password field containing the masked password
     * @param textField Text field for displaying the unmasked password
     * @param container Container holding the password field
     * @param toggleButton Button that triggers visibility toggle
     * @param eyeIcon Icon for showing the password is visible
     * @param eyeOffIcon Icon for showing the password is masked
     */
    private void togglePasswordVisibility(PasswordField passField, TextField textField, HBox container,
                                         Button toggleButton, FontIcon eyeIcon, FontIcon eyeOffIcon) {
        System.out.println("\tFXCONTROLLER | TOGGLE PASSWORD VISIBILITY | LOGIN");
        if (container.getChildren().contains(passField)) {
            textField.setText(passField.getText());
            container.getChildren().remove(passField);
            container.getChildren().addFirst(textField);
            toggleButton.setGraphic(eyeIcon);
        } else {
            passField.setText(textField.getText());
            container.getChildren().remove(textField);
            container.getChildren().addFirst(passField);
            toggleButton.setGraphic(eyeOffIcon);
        }
    }

    /**
     * Initializes the eye icons for password visibility toggle.
     * Creates and configures font icons for all password fields.
     */
    private void initializeIcons() {
        System.out.println("\tFXCONTROLLER | INITIALIZE ICONS | LOGIN");

        loginEyeIcon = new FontIcon(MaterialDesign.MDI_EYE);
        loginEyeOffIcon = new FontIcon(MaterialDesign.MDI_EYE_OFF);
        registerEyeIcon = new FontIcon(MaterialDesign.MDI_EYE);
        registerEyeOffIcon = new FontIcon(MaterialDesign.MDI_EYE_OFF);
        confirmEyeIcon = new FontIcon(MaterialDesign.MDI_EYE);
        confirmEyeOffIcon = new FontIcon(MaterialDesign.MDI_EYE_OFF);

        loginEyeIcon.setIconSize(18);
        loginEyeOffIcon.setIconSize(18);
        registerEyeIcon.setIconSize(18);
        registerEyeOffIcon.setIconSize(18);
        confirmEyeIcon.setIconSize(18);
        confirmEyeOffIcon.setIconSize(18);
    }

    /**
     * Initializes the login password field and its visibility toggle.
     * Sets up the password text field and toggle button action.
     */
    private void initializeLoginPasswordField() {
        System.out.println("\tFXCONTROLLER | INITIALIZE LOGIN PASSWORD FIELD | LOGIN");

        passwordTextField = new TextField();
        createPasswordField(passwordTextField, "Entrez votre mot de passe");

        passwordContainer = (HBox) passwordField.getParent();
        togglePasswordButton.setGraphic(loginEyeOffIcon);

        togglePasswordButton.setOnAction(event -> togglePasswordVisibility(
            passwordField, passwordTextField, passwordContainer,
            togglePasswordButton, loginEyeIcon, loginEyeOffIcon));
    }

    /**
     * Initializes registration password fields and their visibility toggles.
     * Sets up both the password and confirm password fields with toggle buttons.
     */
    private void initializeRegisterPasswordFields() {
        System.out.println("\tFXCONTROLLER | INITIALIZE REGISTER PASSWORD FIELDS | LOGIN");

        registerPasswordTextField = new TextField();
        createPasswordField(registerPasswordTextField, "Entrez votre mot de passe");

        registerPasswordContainer = (HBox) registerPasswordField.getParent();
        toggleRegisterPasswordButton.setGraphic(registerEyeOffIcon);
        toggleRegisterPasswordButton.setOnAction(e -> togglePasswordVisibility(
                registerPasswordField, registerPasswordTextField, registerPasswordContainer,
                toggleRegisterPasswordButton, registerEyeIcon, registerEyeOffIcon));

        confirmPasswordTextField = new TextField();
        createPasswordField(confirmPasswordTextField, "Confirmez votre mot de passe");

        confirmPasswordContainer = (HBox) confirmPasswordField.getParent();
        toggleConfirmPasswordButton.setGraphic(confirmEyeOffIcon);
        toggleConfirmPasswordButton.setOnAction(e -> togglePasswordVisibility(
                confirmPasswordField, confirmPasswordTextField, confirmPasswordContainer,
                toggleConfirmPasswordButton, confirmEyeIcon, confirmEyeOffIcon));
    }

    /**
     * Switches the display to the registration view.
     * Hides the login pane and shows the registration pane.
     */
    private void switchToRegister() {
        System.out.println("\tFXCONTROLLER | SWITCH TO REGISTER | LOGIN");
        loginPane.setVisible(false);
        registerPane.setVisible(true);
    }

    /**
     * Switches the display to the login view.
     * Hides the registration pane and shows the login pane.
     */
    private void switchToLogin() {
        System.out.println("\tFXCONTROLLER | SWITCH TO LOGIN | LOGIN");
        registerPane.setVisible(false);
        loginPane.setVisible(true);
    }

    /**
     * Creates and configures a password text field.
     * Sets up style classes and properties for consistent appearance.
     *
     * @param textField The text field to configure
     * @param promptText The placeholder text to display
     */
    private void createPasswordField(TextField textField, String promptText) {
        System.out.println("\tFXCONTROLLER | CREATE PASSWORD FIELD | LOGIN");
        textField.getStyleClass().add("input-field");
        textField.setPromptText(promptText);
        HBox.setHgrow(textField, Priority.ALWAYS);
    }
}