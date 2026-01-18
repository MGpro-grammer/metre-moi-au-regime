package be.esi.prj.viewmodel;

import be.esi.prj.model.UserFacade;
import be.esi.prj.service.RegistrationService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * This view model manages the screen for creating new user profiles.
 * It extends the base profile functionality to handle new user registration.
 */
public class NewProfileViewModel extends AbstractProfileViewModel {

    /**
     * Creates a new NewProfileViewModel with a user facade.
     * Uses the parent class to set up all the profile properties.
     * @param userFacade the service to handle user operations
     */
    public NewProfileViewModel(UserFacade userFacade) {
        super(userFacade);
    }

    /**
     * Saves the new user profile and completes the registration.
     * Validates all profile information before creating the user account.
     * Shows error messages if registration fails.
     * @return true if the user was created successfully, false if there were errors
     */
    @Override
    public boolean save() {

        if (!areProfileDetailsValid()) {
            return false;
        }

        try {
            addDetailsToRegistration();

            RegistrationService.getInstance().completeRegistration();
            return true;
        }
        catch (IllegalArgumentException e) {
            setStatusMessage("An error occurred during registration. The email might already be in use.");
            return false;
        }
    }

    /**
     * Adds all profile details to the registration service.
     * Collects information from all the form fields and sends it to the registration service.
     * This prepares the data for creating the new user account.
     */
    private void addDetailsToRegistration() {
        RegistrationService.getInstance().addDetails(
                nameProperty().get(),
                ageProperty().get(),
                genderProperty().get(),
                heightProperty().get(),
                weightProperty().get(),
                goalWeightProperty().get(),
                startDateProperty().get(),
                endDateProperty().get(),
                activityLevelProperty().get()
        );
    }
}
