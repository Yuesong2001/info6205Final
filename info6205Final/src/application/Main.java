package application;

import controller.MainController;


import controller.NavigationController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.DataStore;
import view.LoginView;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
	    // 1. First create data and controllers
	    DataStore dataStore = new DataStore();
	    NavigationController navController = new NavigationController();
	    MainController mainController = new MainController(dataStore, navController);

	    // 2. Create the initial LoginView (it needs mainController, navController)
	    LoginView loginView = new LoginView(mainController, navController,dataStore);
	    //   Assume LoginView extends VBox (or a Pane subclass)

	    // 3. Create Scene with loginView as the root node
	    Scene scene = new Scene(loginView, 800, 600);

	    // 4. Pass this Scene to navController
	    navController.setScene(scene);
        navController.pushPane(loginView);


	    // 5. Finally set the scene to the Stage and show it
	    primaryStage.setTitle("Calendar");
	    primaryStage.setScene(scene);
	    primaryStage.show();
	}


    public static void main(String[] args) {
        launch(args);
    }
}
