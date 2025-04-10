package view;
import controller.MainController;
import controller.NavigationController;
import controller.UserController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.User;
import java.util.List;

public class UserFriendView extends VBox {
    private UserController userController;
    private NavigationController navController;
    private User currentUser;
    private MainController mainController;
    
    private ListView<User> friendListView;
    private ListView<User> recommendListView;
    
    public UserFriendView(UserController userController, NavigationController navController, 
                         User currentUser, MainController mainController) {
        this.userController = userController;
        this.navController = navController;
        this.currentUser = currentUser;
        this.mainController = mainController;
        
        // Main container styling
        setPadding(new Insets(20));
        setSpacing(15);
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: #f5f5f5;");
        
        // Header
        Label headerLabel = new Label("Friend Management");
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        headerLabel.setTextFill(Color.web("#2c3e50"));
        headerLabel.setPadding(new Insets(0, 0, 10, 0));
        
        // Friend List Section
        VBox friendSection = createFriendListSection();
        
        // Recommendation Section
        VBox recommendationSection = createRecommendationSection();
        
        // Buttons Section
        HBox buttonBar = createButtonBar();
        
        // Add all components to the main container
        getChildren().addAll(
            headerLabel,
            friendSection,
            recommendationSection,
            buttonBar
        );
        
        // Load initial data
        refreshLists();
    }
    
    private VBox createFriendListSection() {
        VBox section = new VBox(8);
        section.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 1);");
        section.setPadding(new Insets(15));
        
        Label friendLabel = new Label("My Friends");
        friendLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        friendLabel.setTextFill(Color.web("#34495e"));
        
        friendListView = new ListView<>();
        friendListView.setPrefHeight(150);
        friendListView.setStyle("-fx-background-radius: 3; -fx-border-color: #e0e0e0; -fx-border-radius: 3;");
        VBox.setVgrow(friendListView, Priority.ALWAYS);
        
        // Custom cell factory for better display
        friendListView.setCellFactory(param -> new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                
                // Ensure old content is cleared
                setText(null);
                setGraphic(null);
                
                if (empty || user == null) {
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setText(user.toString());
                    setStyle("-fx-padding: 5 10;");
                    setFont(Font.font("Arial", 12));
                }
            }
        });
        
        section.getChildren().addAll(friendLabel, friendListView);
        return section;
    }
    
    private VBox createRecommendationSection() {
        VBox section = new VBox(8);
        section.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 1);");
        section.setPadding(new Insets(15));
        
        Label recLabel = new Label("Recommended Friends");
        recLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        recLabel.setTextFill(Color.web("#34495e"));
        
        recommendListView = new ListView<>();
        recommendListView.setPrefHeight(150);
        recommendListView.setStyle("-fx-background-radius: 3; -fx-border-color: #e0e0e0; -fx-border-radius: 3;");
        VBox.setVgrow(recommendListView, Priority.ALWAYS);
        
        // Custom cell factory for better display
        recommendListView.setCellFactory(param -> new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                
                // Ensure old content is cleared
                setText(null);
                setGraphic(null);
                
                if (empty || user == null) {
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setText(user.toString());
                    setStyle("-fx-padding: 5 10;");
                    setFont(Font.font("Arial", 12));
                }
            }
        });
        
        section.getChildren().addAll(recLabel, recommendListView);
        return section;
    }
    
    private HBox createButtonBar() {
        HBox buttonBar = new HBox(10);
        buttonBar.setAlignment(Pos.CENTER);
        buttonBar.setPadding(new Insets(10, 0, 0, 0));
        
        Button addFriendBtn = new Button("Add Selected Friend");
        styleButton(addFriendBtn, "#3498db", "#2980b9");
        addFriendBtn.setOnAction(e -> {
            User selectedRec = recommendListView.getSelectionModel().getSelectedItem();
            if (selectedRec != null) {
                // Check if already friends
                List<User> existingFriends = userController.getFriendsOfUser(currentUser.getUserId());
                boolean alreadyFriend = false;
                
                for (User friend : existingFriends) {
                    if (friend.getUserId().equals(selectedRec.getUserId())) {
                        alreadyFriend = true;
                        break;
                    }
                }
                
                if (alreadyFriend) {
                    // User is already a friend, display error message
                    showAlert(Alert.AlertType.ERROR, "Already Friends", 
                              selectedRec.getUsername() + " is already your friend!");
                    return;
                }
            	
                // Save the selected index
                int selectedIndex = recommendListView.getSelectionModel().getSelectedIndex();
                
                // Add friend relation
                userController.addFriendRelation(currentUser.getUserId(), selectedRec.getUserId());
                
                // Refresh lists
                refreshLists();
                
                // If the index is still valid, try to reselect that item
                if (selectedIndex >= 0 && selectedIndex < recommendListView.getItems().size()) {
                    recommendListView.getSelectionModel().select(selectedIndex);
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "Selection Required", 
                        "Please select a friend recommendation first.");
            }
        });
        
        Button backBtn = new Button("Back");
        styleButton(backBtn, "#7f8c8d", "#636e72");
        backBtn.setOnAction(e -> navController.popPane());
        
        buttonBar.getChildren().addAll(addFriendBtn, backBtn);
        return buttonBar;
    }
    
    private void styleButton(Button button, String baseColor, String hoverColor) {
        button.setPrefWidth(150);
        button.setPadding(new Insets(8, 15, 8, 15));
        button.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        button.setTextFill(Color.WHITE);
        button.setStyle(
            "-fx-background-color: " + baseColor + ";" +
            "-fx-background-radius: 3;" +
            "-fx-cursor: hand;"
        );
        
        // Hover effect
        button.setOnMouseEntered(e -> 
            button.setStyle(
                "-fx-background-color: " + hoverColor + ";" +
                "-fx-background-radius: 3;" +
                "-fx-cursor: hand;"
            )
        );
        
        button.setOnMouseExited(e -> 
            button.setStyle(
                "-fx-background-color: " + baseColor + ";" +
                "-fx-background-radius: 3;" +
                "-fx-cursor: hand;"
            )
        );
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void refreshLists() {
        if (currentUser != null) {
            // Get data
            List<User> friends = userController.getFriendsOfUser(currentUser.getUserId());
            List<User> recommendations = userController.recommendFriends(currentUser.getUserId());
            
            // Clear and set items
            friendListView.getItems().clear();
            friendListView.getItems().addAll(friends);
            
            recommendListView.getItems().clear();
            recommendListView.getItems().addAll(recommendations);
            
            // Refresh views
            friendListView.refresh();
            recommendListView.refresh();
            
            // Set empty list placeholders
            if (friends.isEmpty()) {
                Label emptyLabel = new Label("You don't have any friends yet");
                emptyLabel.setStyle("-fx-text-fill: #95a5a6; -fx-font-style: italic;");
                friendListView.setPlaceholder(emptyLabel);
            }
            
            if (recommendations.isEmpty()) {
                Label emptyLabel = new Label("No recommendations available");
                emptyLabel.setStyle("-fx-text-fill: #95a5a6; -fx-font-style: italic;");
                recommendListView.setPlaceholder(emptyLabel);
            }
        }
    }
}