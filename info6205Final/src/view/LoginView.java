package view;


import java.util.UUID;

import controller.MainController;
import controller.NavigationController;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import model.DataStore;
import model.User;
import javafx.geometry.HPos;


public class LoginView extends VBox {
	
    private MainController mainController;
    private NavigationController navController;
    private DataStore dataStore; // Store it for later use

    public LoginView(MainController mainController, NavigationController navController, DataStore dataStore) {
    	
        this.mainController = mainController;
        this.navController = navController;
        this.dataStore = dataStore;
    	
        setSpacing(10);
        setAlignment(Pos.CENTER);
        
        //background color
        setStyle("-fx-background-color: #f4f4f9; -fx-padding: 20px;");

        Label titleLabel = new Label("Login");
        titleLabel.setFont(new Font("Arial", 24));
        titleLabel.setTextFill(Color.DODGERBLUE);
        
     // Create a grid for form fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);


        Label userLabel = new Label("Username:");
        TextField userField = new TextField();
        userField.setPromptText("Enter your username");

        Label passLabel = new Label("Password:");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Enter your password");

        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;"); // Makes messageLabel clearly visible

        
        Button loginBtn = new Button("Login");
        loginBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        loginBtn.setMaxWidth(Double.MAX_VALUE);


        loginBtn.setOnAction(e -> {
            String username = userField.getText();
            String password = passField.getText();
            if (username.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Username/Password cannot be empty!");
                return;
            }
            boolean success = mainController.login(username, password);
            if(!success) {
                messageLabel.setText("Login failed! Invalid credentials.");
                messageLabel.setStyle("-fx-text-fill: red;");
            } else {
                // Simplified handling
                messageLabel.setText("Login successful!");
                messageLabel.setStyle("-fx-text-fill: green;");
                
                PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
                pause.setOnFinished(event -> {
                    navController.pushPane(new CalendarView(mainController.getCurrentUser(), dataStore, navController,mainController.getCurrentUser()));
                });
                pause.play();
            }
        });
        
        Button registerBtn = new Button("Register");
        registerBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        registerBtn.setMaxWidth(Double.MAX_VALUE);  // Makes the button take up the full width of the container
        registerBtn.setOnAction(e -> {
            String username = userField.getText().trim();
            String password = passField.getText().trim();
            
            // Simplified empty check
            if (username.isEmpty() || password.isEmpty()) {
                showErrorAlert("Input Error", "Username/Password cannot be empty");
                return;
            }
            
            // Check if user already exists
            if (dataStore.findUserByUsername(username) != null) {
                // Show error dialog
                showErrorAlert("Username Error", "Username already taken, please choose another");
                return;
            }
            
            // Otherwise create User and add it to userMap
            String newUserId = UUID.randomUUID().toString();
            User newUser = new User(newUserId, username, password);
            dataStore.addUser(newUser); 
            
            messageLabel.setText("Registration successful!");
            messageLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            
            // Show success notification
            showSuccessAlert("Registration Successful", "Your account has been created successfully!");
            
            // After registration, return to login page
            navController.pushPane(new LoginView(new MainController(dataStore, navController), navController, dataStore));
        });
     // Add elements to grid
        grid.add(userLabel, 0, 0);
        grid.add(userField, 1, 0);
        grid.add(passLabel, 0, 1);
        grid.add(passField, 1, 1);
        grid.add(loginBtn, 0, 2, 2, 1);
        grid.add(messageLabel, 0, 3, 2, 1);
        grid.add(registerBtn, 0, 4, 2, 1);
        

        // Center the buttons in the grid
        GridPane.setHalignment(loginBtn, HPos.CENTER);
        GridPane.setHalignment(registerBtn, HPos.CENTER);

        // Add all components to the VBox
        getChildren().addAll(titleLabel, grid);
    }
    
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}