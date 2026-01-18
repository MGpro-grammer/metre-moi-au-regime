package be.esi.prj;

import be.esi.prj.model.DiaryFacade;
import be.esi.prj.model.FoodFacade;
import be.esi.prj.model.UserFacade;
import be.esi.prj.repository.*;
import be.esi.prj.service.RegistrationService;
import be.esi.prj.service.SessionService;
import be.esi.prj.service.ViewModelService;
import be.esi.prj.viewmodel.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        UserRepository userRepository = new UserRepository();
        FoodRepository foodRepository = new FoodRepository();
        FoodConsumedRepository foodConsumedRepository = new FoodConsumedRepository();
        DiaryRepository diaryRepository = new DiaryRepository();
        ActivityRepository activityRepository = new ActivityRepository();



        UserFacade userFacade = new UserFacade(userRepository);
        FoodFacade foodFacade = new FoodFacade(foodRepository);
        DiaryFacade diaryFacade = new DiaryFacade(diaryRepository, activityRepository,
                foodRepository, foodConsumedRepository);

        SessionService sessionService = SessionService.getInstance();
        RegistrationService.initialize(userFacade);

        AuthentificationViewModel authViewModel = new AuthentificationViewModel(userFacade, sessionService);
        UserProfileViewModel userProfileViewModel = new UserProfileViewModel(userFacade);
        NewProfileViewModel newProfileViewModel = new NewProfileViewModel(userFacade);
        FoodSearchViewModel foodSearchViewModel = new FoodSearchViewModel(foodFacade, diaryFacade);
        DiaryViewModel diaryViewModel = new DiaryViewModel(diaryFacade, foodFacade);
        DashBoardViewModel dashBoardViewModel = new DashBoardViewModel(diaryFacade);

        ViewModelService.getInstance().setAuthentificationViewModel(authViewModel);
        ViewModelService.getInstance().setUserProfileViewModel(userProfileViewModel);
        ViewModelService.getInstance().setNewProfileViewModel(newProfileViewModel);
        ViewModelService.getInstance().setFoodSearchViewModel(foodSearchViewModel);
        ViewModelService.getInstance().setDiaryViewModel(diaryViewModel);
        ViewModelService.getInstance().setDashBoardViewModel(dashBoardViewModel);

        URL resource = Main.class.getResource("/view/login.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(resource);
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root, 1000, 600);
        stage.setTitle("Mètre-moi au régime!");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
