package view;

import controller.MainController;

import controller.NavigationController;
import controller.UserController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.DataStore;
import model.Event;
import model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class CalendarView extends VBox {
	
	 // Class member variables
    private User currentUser ; // Currently logged in user
    private DataStore dataStore; // Data storage object, used to get and modify event data
    private NavigationController navController; // Navigation controller, used for page transitions
    private MainController mainController;
    
    
	
	
    
    /**
     * Helper method to display alert dialog
     * @param title Dialog title
     * @param message Message content to display
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null); // Don't display header text
        alert.setContentText(message);
        alert.showAndWait(); // Display dialog and wait for user response
    }
    
    /**
     * Helper method to display confirmation dialog
     * @param title Dialog title
     * @param message Confirmation message content
     * @return Returns true if user clicked the confirmation button, otherwise false
     */
    private boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null); // Don't display header text
        alert.setContentText(message);
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK; // Check if user clicked the confirmation button
    }

   

    /**
     * Constructor - Create calendar view
     * @param user Currently logged in user
     * @param dataStore Data storage object
     * @param navController Navigation controller
     */
    public CalendarView(User user, DataStore dataStore, NavigationController navController,User currentUser) {
        
        this.dataStore = dataStore;
        this.navController = navController;
        this.currentUser = currentUser;
        

        // Set layout properties
        setSpacing(15); // Set spacing between child elements
        setAlignment(Pos.CENTER); // Center alignment
        setPadding(new Insets(20)); // Set padding
        setStyle("-fx-background-color: #f8f8f8;"); // Set background color

        // Create welcome label
        Label welcomeLabel = new Label("Welcome, " + user.getUsername());
        welcomeLabel.setFont(Font.font("System", FontWeight.BOLD, 20)); // Set font style
        welcomeLabel.setStyle("-fx-text-fill: #3366cc;"); // Set text color

        // Date selection section
        Label dateLabel = new Label("Select Date:");
        DatePicker datePicker = new DatePicker(LocalDate.now()); // Create date picker, default to current date
        datePicker.setStyle("-fx-pref-width: 200px;"); // Set date picker width
        
        // Create date selection bar
        javafx.scene.layout.HBox dateBar = new javafx.scene.layout.HBox(10); // Horizontal layout, spacing 10
        dateBar.setAlignment(Pos.CENTER); // Center alignment
        dateBar.getChildren().addAll(dateLabel, datePicker); // Add label and date picker to layout

        // For displaying event list
        Button showEventsBtn = new Button("Show My Events"); // Show events button
        ListView<String> eventListView = new ListView<>(); // Event list view
        
        // Track selected event
        Label selectedEventLabel = new Label("No event selected"); // Label to display selected event
        selectedEventLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #555555;"); // Set label style
        Event[] selectedEvent = new Event[1]; // Use array to allow modification of reference in lambda expressions

        // Set click event handler for show events button
        showEventsBtn.setOnAction(e -> {
            LocalDate selectedDate = datePicker.getValue(); // Get selected date
            List<Event> events = dataStore.getUserEventsByDay(user.getUserId(), selectedDate); // Get event list for that date
            
            // First check if the retrieved event list is empty
            if (events.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("No Events");
                alert.setHeaderText(null);
                alert.setContentText("You have no events on this date!");
                alert.showAndWait();
                
                // Reset selection state and list
                selectedEvent[0] = null;
                selectedEventLabel.setText("No event selected");
                eventListView.getItems().clear();
                eventListView.setUserData(null);
                return; // If no events, return directly, don't execute code below
            }
            
            // Reset selection state
            selectedEvent[0] = null;
            selectedEventLabel.setText("No event selected");
            
            // -- Test information output --
            System.out.println("------ Show My Events ------");
            System.out.println("Selected date: " + selectedDate);
            System.out.println("Current user ID: " + user.getUserId());
            System.out.println("Found events: " + events.size());
            for (Event ev : events) {
                System.out.println("Event: " + ev.getTitle() + " " + ev.getStartTime());
            }
            System.out.println("----------------------------");

            // Clear and update list view, display detailed event information
            eventListView.getItems().clear();
            for (Event event : events) {
                String participants = String.join(", ", event.getParticipants()); // Convert participant list to comma-separated string
                String displayText = String.format("%s\nTime: %s - %s\nPriority: %s\nParticipants: %s", 
                    event.getTitle(),
                    event.getStartTime().toLocalTime(),
                    event.getEndTime().toLocalTime(),
                    event.getPriority(),
                    participants);
                eventListView.getItems().add(displayText); // Add formatted event information to list
            }
            
            // Store original event list for later reference
            eventListView.setUserData(events);
        });
        
        // Add selection listener to list view
        eventListView.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.intValue() >= 0 && eventListView.getUserData() != null) {
                @SuppressWarnings("unchecked") // Suppress type conversion warning
                List<Event> events = (List<Event>) eventListView.getUserData();
                if (newVal.intValue() < events.size()) {
                    selectedEvent[0] = events.get(newVal.intValue()); // Update selected event
                    selectedEventLabel.setText("Selected: " + selectedEvent[0].getTitle()); // Update display label
                }
            } else {
                selectedEvent[0] = null;
                selectedEventLabel.setText("No event selected");
            }
        });
        
        // Add selection listener to list view
        eventListView.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.intValue() >= 0 && eventListView.getUserData() != null) {
                @SuppressWarnings("unchecked")
                List<Event> events = (List<Event>) eventListView.getUserData();
                if (newVal.intValue() < events.size()) {
                    selectedEvent[0] = events.get(newVal.intValue());
                    selectedEventLabel.setText("Selected: " + selectedEvent[0].getTitle());
                }
            } else {
                selectedEvent[0] = null;
                selectedEventLabel.setText("No event selected");
            }
        });

        // Add event button
        Button addEventBtn = new Button("Add Event");
        addEventBtn.setOnAction(e -> {
            // Pass the selected date to the event form view
            LocalDate selectedDate = datePicker.getValue();
            navController.pushPane(new EventFormView(currentUser, dataStore, navController, selectedDate)); // Navigate to event form page
        });
        
        // Cancel event button
        Button cancelEventBtn = new Button("Cancel Event");
        cancelEventBtn.setStyle("-fx-base: #ff9999;"); // Set light red background
        
        // Set click event handler for cancel event button
        cancelEventBtn.setOnAction(e -> {
            if (selectedEvent[0] == null) {
                showAlert("Error", "Please Choose an Event first!"); // If no event selected, show error prompt
                return;
            }
            
            // Confirm deletion
            boolean confirmDelete = showConfirmation("Cancel Event", 
                "Are you sure you want to cancel the event: " + selectedEvent[0].getTitle() + "?");
                
            if (confirmDelete) {
                // Remove event
                dataStore.removeEvent(currentUser.getUserId(), selectedEvent[0].getEventId());
                
                // Refresh view
                showEventsBtn.fire(); // Trigger click event of show events button
                
                // Show success message
                showAlert("Success", "Event successfully cancelled!");
            }
        });
        
        Button addFriendBtn = new Button("Add Friend");
        addFriendBtn.setStyle("-fx-background-color: #5cb85c; -fx-text-fill: white;"); // Green background, white text
        addFriendBtn.setOnAction(e -> {
            // Create dialog to get username
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add Friend");
            dialog.setHeaderText("Search for a user to add as friend");
            dialog.setContentText("Enter username:");
            
            // Wait for user input
            Optional<String> result = dialog.showAndWait();
            
            // Process result
            if (result.isPresent() && !result.get().trim().isEmpty()) {
                String username = result.get().trim();
                
                // Use UserController to find user
                User foundUser = dataStore.findUserByUsername(username);
                
                if (foundUser != null) {
                    // Confirm whether to add as friend
                    boolean confirm = showConfirmation("Add Friend", 
                        "Do you want to add " + username + " as a friend?");
                    
                    if (confirm) {
                        try {
                            // Add friend relationship - void method, no return value
                            dataStore.addFriendRelation(
                                currentUser.getUserId(), 
                                foundUser.getUserId()
                            );
                            
                            // If no exception, addition successful
                            showAlert("Success", username + " has been added as your friend!");
                        } catch (Exception ex) {
                            // If exception occurs, possibly already a friend or other error
                            System.out.println("Error adding friend: " + ex.getMessage());
                            showAlert("Note", "Could not add " + username + " as a friend. They may already be your friend.");
                        }
                    }
                } else {
                    // User does not exist
                    showAlert("User Not Found", "No user found with username: " + username);
                }
            }
        });
        
        // Logout button
        Button showUserFriendBtn = new Button("Manage Friends");
        showUserFriendBtn.setOnAction(e -> {
            UserController userController = new UserController(dataStore);
            UserFriendView friendView = new UserFriendView(userController, navController,currentUser,mainController);
            navController.pushPane(friendView);
        });
        Button logoutBtn = new Button("Logout");
        logoutBtn.setOnAction(e -> {
            navController.popPane(); // Return to previous view (login view)
        });
        
        
        
        // Create button bar
        javafx.scene.layout.HBox buttonBar = new javafx.scene.layout.HBox(10); // Horizontal layout, spacing 10
        buttonBar.setAlignment(Pos.CENTER); // Center alignment
        buttonBar.getChildren().addAll(addEventBtn, cancelEventBtn, logoutBtn); // Add all buttons to layout

        getChildren().addAll(welcomeLabel, datePicker, showEventsBtn, eventListView,showUserFriendBtn);
        // Style settings
        eventListView.setPrefHeight(300); // Set preferred height for list view
        eventListView.setStyle("-fx-pref-width: 400px; -fx-font-family: 'System'; -fx-font-size: 13px;"); // Set list view style
        
        // Set button style
        showEventsBtn.setStyle("-fx-background-color: #4488cc; -fx-text-fill: white;"); // Blue background, white text
        
        // Add title for events section
        Label eventsTitle = new Label("My Events");
        eventsTitle.setFont(Font.font("System", FontWeight.BOLD, 16)); // Set font style
        
        // Add separators to improve visual organization
        Separator separator1 = new Separator();
        Separator separator2 = new Separator();
        separator1.setPrefWidth(400);
        separator2.setPrefWidth(400);

        // Add all components to main layout
        getChildren().addAll(
            new Separator(), // Separator
            dateBar,
            eventsTitle,
            selectedEventLabel,
            separator2,
            buttonBar,
            addFriendBtn
        );
        }
}