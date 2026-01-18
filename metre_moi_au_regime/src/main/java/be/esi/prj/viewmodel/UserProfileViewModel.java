package be.esi.prj.viewmodel;

import be.esi.prj.dto.UserDto;
import be.esi.prj.model.UserFacade;
import be.esi.prj.service.SessionService;

/**
 * This view model manages the user profile screen for existing users.
 * It allows users to view and edit their personal information like weight, height, and goals.
 * It extends the base profile functionality to handle profile updates.
 */
public class UserProfileViewModel extends AbstractProfileViewModel {

    private Integer currentUserId;

    /**
     * Creates a new UserProfileViewModel with a user facade.
     * Uses the parent class to set up all the profile properties.
     * @param userFacade the service to handle user operations
     */
    public UserProfileViewModel(UserFacade userFacade) {
        super(userFacade);
    }

    /**
     * Loads the current user's profile information into the form.
     * Gets the logged-in user's data and fills all the input fields.
     * Shows an error message if no user is logged in.
     */
    public void loadProfile() {
        if (!SessionService.getInstance().isUserLoggedIn()) {
            setStatusMessage("No user is logged in. Cannot load profile.");
            return;
        }

        UserDto currentUser = SessionService.getInstance().getCurrentUser();
        this.currentUserId = currentUser.userId();

        setName(currentUser.name());
        setAge(currentUser.age());
        setGender(currentUser.gender());
        setHeight(currentUser.height());
        setWeight(currentUser.weight());
        setGoalWeight(currentUser.goalWeight());
        setStartDate(currentUser.startDate());
        setEndDate(currentUser.endDate());
        setActivityLevel(currentUser.activityLevel());
    }

    /**
     * Saves the updated user profile information.
     * Validates all profile information before updating the user account.
     * Keeps the same email and password while updating other details.
     * Shows success or error messages based on the result.
     * @return true if the profile was updated successfully, false if there were errors
     */
    @Override
    public boolean save() {
        if (currentUserId == null) {
            setStatusMessage("Cannot save profile. No user loaded.");
            return false;
        }

        if (!areProfileDetailsValid()) {
            return false;
        }

        try {
            UserDto currentUser = SessionService.getInstance().getCurrentUser();
            UserDto updatedUserDto = new UserDto(
                    this.currentUserId,
                    currentUser.email(),
                    currentUser.password(),
                    getName(),
                    getAge(),
                    getGender(),
                    getHeight(),
                    getWeight(),
                    getGoalWeight(),
                    getStartDate(),
                    getEndDate(),
                    getActivityLevel()
            );

            userFacade.saveUser(updatedUserDto);

            setStatusMessage("Profile updated successfully!");
            return true;
        } catch (Exception e) {
            setStatusMessage("Error saving profile: " + e.getMessage());
            return false;
        }
    }
}
