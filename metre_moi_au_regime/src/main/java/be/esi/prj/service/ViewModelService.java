package be.esi.prj.service;

import be.esi.prj.viewmodel.*;

/**
 * This service manages all the view models in the application.
 * It stores and provides access to different view models used by the user interface.
 */
public class ViewModelService {
    private static ViewModelService instance;
    private AuthentificationViewModel authentificationViewModel;
    private NewProfileViewModel newProfileViewModel;
    private FoodSearchViewModel foodSearchViewModel;
    private UserProfileViewModel userProfileViewModel;
    private DiaryViewModel diaryViewModel;
    private DashBoardViewModel dashBoardViewModel;

    /**
     * Creates a new ViewModelService.
     * This is private because the class uses the singleton pattern.
     */
    private ViewModelService() {}

    /**
     * Gets the single instance of this service.
     * Creates a new instance if one doesn't exist yet.
     * @return the view model service instance
     */
    public static ViewModelService getInstance() {
        if (instance == null) {
            instance = new ViewModelService();
        }
        return instance;
    }

    /**
     * Sets the view model for user login and registration screens.
     * @param authentificationViewModel the view model that handles login and signup
     */
    public void setAuthentificationViewModel(AuthentificationViewModel authentificationViewModel) {
        this.authentificationViewModel = authentificationViewModel;
    }

    /**
     * Sets the view model for creating new user profiles.
     * @param newProfileViewModel the view model that handles new user registration forms
     */
    public void setNewProfileViewModel(NewProfileViewModel newProfileViewModel) {
        this.newProfileViewModel = newProfileViewModel;
    }

    /**
     * Sets the view model for searching and finding food items.
     * @param foodSearchViewModel the view model that handles food search functionality
     */
    public void setFoodSearchViewModel(FoodSearchViewModel foodSearchViewModel) {
        this.foodSearchViewModel = foodSearchViewModel;
    }

    /**
     * Sets the view model for user profile management.
     * @param userProfileViewModel the view model that handles user settings and profile
     */
    public void setUserProfileViewModel(UserProfileViewModel userProfileViewModel) {
        this.userProfileViewModel = userProfileViewModel;
    }

    /**
     * Sets the view model for the daily food and activity diary.
     * @param diaryViewModel the view model that handles diary entries and tracking
     */
    public void setDiaryViewModel(DiaryViewModel diaryViewModel) {
        this.diaryViewModel = diaryViewModel;
    }

    /**
     * Sets the view model for the main dashboard screen.
     * @param dashBoardViewModel the view model that handles the main overview screen
     */
    public void setDashBoardViewModel(DashBoardViewModel dashBoardViewModel) {
        this.dashBoardViewModel = dashBoardViewModel;
    }

    /**
     * Gets the view model for searching and finding food items.
     * @return the food search view model
     */
    public FoodSearchViewModel getFoodSearchViewModel() {
        return foodSearchViewModel;
    }

    /**
     * Gets the view model for user login and registration screens.
     * @return the authentication view model
     */
    public AuthentificationViewModel getAuthentificationViewModel() {
        return authentificationViewModel;
    }

    /**
     * Gets the view model for creating new user profiles.
     * @return the new profile view model
     */
    public NewProfileViewModel getNewProfileViewModel() {
        return newProfileViewModel;
    }

    /**
     * Gets the view model for user profile management.
     * @return the user profile view model
     */
    public UserProfileViewModel getUserProfileViewModel() {
        return userProfileViewModel;
    }

    /**
     * Gets the view model for the daily food and activity diary.
     * @return the diary view model
     */
    public DiaryViewModel getDiaryViewModel() {
        return diaryViewModel;
    }

    /**
     * Gets the view model for the main dashboard screen.
     * @return the dashboard view model
     */
    public DashBoardViewModel getDashBoardViewModel() {
        return dashBoardViewModel;
    }
}
