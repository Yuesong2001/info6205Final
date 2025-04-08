package view;

import java.util.UUID;

import controller.NavigationController;
import controller.MainController;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import model.DataStore;
import model.User;

public class RegisterView extends VBox {
    
    public RegisterView(DataStore dataStore, NavigationController navController) {
        setSpacing(10);
        setAlignment(Pos.CENTER);
        
        //background color
        setStyle("-fx-background-color: #f4f4f9; -fx-padding: 20px;");
        
        Label titleLabel = new Label("Register a New Account");
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

        
        Label messageLabel = new Label(); // 用于显示“注册成功 / 失败”消息
        
        Button registerBtn = new Button("Register");
        registerBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        registerBtn.setMaxWidth(Double.MAX_VALUE);  // Makes the button take up the full width of the container
        registerBtn.setOnAction(e -> {
            String username = userField.getText().trim();
            String password = passField.getText().trim();
            
            // 简化的判空检查
            if (username.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Username/Password cannot be empty");
                return;
            }
            
            // 检查是否已存在
            if (dataStore.findUserByUsername(username) != null) {
                messageLabel.setText("Username already taken, please choose another");
                return;
            }
            
            // 否则创建User，并放入userMap
            String newUserId = UUID.randomUUID().toString();
            User newUser = new User(newUserId, username, password);
            dataStore.addUser(newUser); 
            
            messageLabel.setText("Registration successful!");
            
            // 注册完成后，你可以直接回到登录页面
            // navController.popPane(); // 如果上一个页面是登录
            // 或者 navController.pushPane(new LoginView(...));
            
            //navController.pushPane(new LoginView(null, navController, dataStore));
            navController.pushPane(new LoginView(new MainController(dataStore, navController), navController, dataStore));

        });
        
        Button backBtn = new Button("Back to Login");
        backBtn.setStyle("-fx-background-color: #007BFF; -fx-text-fill: white;");
        backBtn.setMaxWidth(Double.MAX_VALUE);  // Makes the button take up the full width of the container
        backBtn.setOnAction(e -> {
            navController.popPane(); 
            // 或者 pushPane(new LoginView(dataStore, navController)) 
            // 看你具体怎么设计流程
        });
        
     // Add elements to grid
        grid.add(userLabel, 0, 0);
        grid.add(userField, 1, 0);
        grid.add(passLabel, 0, 1);
        grid.add(passField, 1, 1);
        grid.add(registerBtn, 0, 2, 2, 1);
        grid.add(backBtn, 0, 3, 2, 1);
        grid.add(messageLabel, 0, 4, 2, 1);

        // Add all components to the VBox
        getChildren().addAll(titleLabel, grid);
    }
}
