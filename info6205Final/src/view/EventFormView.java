package view;

import controller.NavigationController;

import controller.UserController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.DataStore;
import model.Event;
import model.PriorityLevel;
import model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class EventFormView extends VBox {
    private DataStore dataStore;
    private User currentUser;
    private List<User> selectedParticipants = new ArrayList<>();
    private ListView<User> participantListView;
    private TextField searchField;
    private ComboBox<String> startHourBox;
    private ComboBox<String> startMinuteBox;
    private ComboBox<String> endHourBox;
    private ComboBox<String> endMinuteBox;
    private DatePicker datePicker;

    /**
     * Constructor - Creates an event form with the current date
     * @param user The currently logged in user
     * @param dataStore The data storage object
     * @param navController The navigation controller
     */
    public EventFormView(User user, DataStore dataStore, NavigationController navController) {
        this(user, dataStore, navController, LocalDate.now()); // Call the other constructor with the current date
    }

    /**
     * Constructor - Creates an event form with the specified date
     * @param user The currently logged in user
     * @param dataStore The data storage object
     * @param navController The navigation controller
     * @param initialDate The initially selected date
     */
    public EventFormView(User user, DataStore dataStore, NavigationController navController, LocalDate initialDate) {
        this.dataStore = dataStore;
        this.currentUser = user;
        
        // Set layout properties
        setSpacing(20);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(30));
        setStyle("-fx-background-color: #f5f8ff;");

        // Title section
        Label titleLabel = new Label("Create New Event");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Event title field
        Label eventTitleLabel = new Label("Event Title:");
        TextField titleField = new TextField();
        titleField.setPromptText("Enter event title");
        titleField.setPrefWidth(300);

        // Date picker
        Label dateLabel = new Label("Date:");
        datePicker = new DatePicker(initialDate);
        
        // Start time selector
        Label startTimeLabel = new Label("Start Time:");
        startHourBox = createHourComboBox();
        startMinuteBox = createMinuteComboBox();
        HBox startTimeBox = new HBox(10);
        startTimeBox.getChildren().addAll(startHourBox, new Text(":"), startMinuteBox);
        
        // End time selector
        Label endTimeLabel = new Label("End Time:");
        endHourBox = createHourComboBox();
        endMinuteBox = createMinuteComboBox();
        HBox endTimeBox = new HBox(10);
        endTimeBox.getChildren().addAll(endHourBox, new Text(":"), endMinuteBox);
        
        // Set default time values (10:00 - 11:00)
        startHourBox.setValue("10");
        startMinuteBox.setValue("00");
        endHourBox.setValue("11");
        endMinuteBox.setValue("00");

        // Participant search and selection section
        VBox participantSection = createParticipantSection();

        // Priority selection
        Label priorityLabel = new Label("Priority:");
        ComboBox<PriorityLevel> priorityBox = new ComboBox<>();
        priorityBox.getItems().addAll(PriorityLevel.LOW, PriorityLevel.MEDIUM, PriorityLevel.HIGH);
        priorityBox.setValue(PriorityLevel.MEDIUM);

        // Button section
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setPrefWidth(100);
        cancelBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        
        Button saveBtn = new Button("Save");
        saveBtn.setPrefWidth(100);
        saveBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        
        buttonBox.getChildren().addAll(cancelBtn, saveBtn);

        // Create form layout
        GridPane formGrid = new GridPane();
        formGrid.setHgap(15);
        formGrid.setVgap(18);
        formGrid.setAlignment(Pos.CENTER);
        formGrid.setPadding(new Insets(10));
        formGrid.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-radius: 5;");

        // Set form label styles
        eventTitleLabel.setStyle("-fx-font-weight: bold;");
        dateLabel.setStyle("-fx-font-weight: bold;");
        startTimeLabel.setStyle("-fx-font-weight: bold;");
        endTimeLabel.setStyle("-fx-font-weight: bold;");
        priorityLabel.setStyle("-fx-font-weight: bold;");
        
        // Set form field styles
        titleField.setStyle("-fx-padding: 8; -fx-border-color: #ddd; -fx-border-radius: 3;");
        
        // Add form elements to the grid
        int row = 0;
        formGrid.add(eventTitleLabel, 0, row);
        formGrid.add(titleField, 1, row);
        
        row++;
        formGrid.add(dateLabel, 0, row);
        formGrid.add(datePicker, 1, row);
        
        row++;
        formGrid.add(startTimeLabel, 0, row);
        formGrid.add(startTimeBox, 1, row);
        
        row++;
        formGrid.add(endTimeLabel, 0, row);
        formGrid.add(endTimeBox, 1, row);
        
        row++;
        formGrid.add(new Label("Participants:"), 0, row);
        formGrid.add(participantSection, 1, row);
        
        row++;
        formGrid.add(priorityLabel, 0, row);
        formGrid.add(priorityBox, 1, row);

        // Set button actions
        cancelBtn.setOnAction(e -> navController.popPane());

        // Save button action handler
        saveBtn.setOnAction(e -> {
            // Validate inputs
            if (!validateInputs(titleField.getText())) {
                return;
            }
            
            String eventId = UUID.randomUUID().toString();
            String title = titleField.getText();
            LocalDate date = datePicker.getValue();
            
            LocalTime startTime = LocalTime.of(
                Integer.parseInt(startHourBox.getValue()),
                Integer.parseInt(startMinuteBox.getValue())
            );
            
            LocalTime endTime = LocalTime.of(
                Integer.parseInt(endHourBox.getValue()),
                Integer.parseInt(endMinuteBox.getValue())
            );
            
            LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
            LocalDateTime endDateTime = LocalDateTime.of(date, endTime);
            
            // Validate that end time is after start time
            if (!endDateTime.isAfter(startDateTime)) {
                showAlert(Alert.AlertType.ERROR, "Invalid Time", 
                    "The end time must be later than the start time.");
                return;
            }
            
            // Check for time conflicts
            if (checkTimeConflict(user.getUserId(), date, startTime, endTime)) {
                boolean confirmOverlap = showConfirmation("Time Conflict", 
                    "This event overlaps with existing events. Do you want to schedule it anyway?");
                if (!confirmOverlap) {
                    return;
                }
            }
            
            // Get all participant usernames
            List<String> participantNames = selectedParticipants.stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
            
            // Ensure the creator is also in the participant list
            if (!participantNames.contains(user.getUsername())) {
                participantNames.add(user.getUsername());
            }
            
            // Create event and add to data store
            Event newEvent = new Event(eventId, title, startDateTime, endDateTime,
                    participantNames, priorityBox.getValue());
            dataStore.addEvent(user.getUserId(), newEvent);

            // Display success message
            showAlert(Alert.AlertType.INFORMATION, "Success", "Event successfully created!");
            
            // Return to previous screen
            navController.popPane();
            
            // Print log information
            System.out.println("Save Event => " + newEvent.getTitle() 
                + ", startTime=" + newEvent.getStartTime() 
                + ", userId=" + user.getUserId());
        });

        // Add all components to the main VBox layout
        getChildren().addAll(
            titleLabel,
            new Separator(),
            formGrid,
            new Separator(),
            buttonBox
        );
    }
    
    /**
     * Create the participant search and selection section
     * @return VBox containing search field and participant list
     */
    private VBox createParticipantSection() {
        VBox section = new VBox(8);
        section.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 3; -fx-padding: 10;");
        
        // Search section
        HBox searchBox = new HBox(5);
        searchField = new TextField();
        searchField.setPromptText("Search users...");
        searchField.setPrefWidth(250);
        
        Button searchBtn = new Button("Search");
        searchBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        
        searchBox.getChildren().addAll(searchField, searchBtn);
        
        // Search results section
        Label resultsLabel = new Label("Search Results:");
        resultsLabel.setStyle("-fx-font-weight: bold;");
        
        // Create search results ListView
        ListView<User> searchResultsView = new ListView<>();
        searchResultsView.setPrefHeight(100);
        searchResultsView.setPlaceholder(new Label("No results found"));
        searchResultsView.setCellFactory(lv -> new UserListCell());
        
        // Selected participants section
        Label selectedLabel = new Label("Selected Participants:");
        selectedLabel.setStyle("-fx-font-weight: bold;");
        
        // Create participants ListView
        participantListView = new ListView<>();
        participantListView.setPrefHeight(100);
        participantListView.setPlaceholder(new Label("No participants selected"));
        participantListView.setCellFactory(lv -> new UserListCell());
        
        // Add and remove buttons
        HBox actionBox = new HBox(10);
        actionBox.setAlignment(Pos.CENTER);
        
        Button addBtn = new Button("Add Selected");
        addBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        
        Button removeBtn = new Button("Remove Selected");
        removeBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        
        actionBox.getChildren().addAll(addBtn, removeBtn);
        
        // Search logic implementation
        Runnable performSearch = () -> {
            String searchText = searchField.getText().trim();
            if (searchText.isEmpty()) {
                searchResultsView.getItems().clear();
                return;
            }
            
            System.out.println("Searching for: " + searchText);
            
            // Get all users from DataStore
            List<User> allUsers = new ArrayList<>(dataStore.getUserMap().values());
            System.out.println("Total users in system: " + allUsers.size());
            
            // Filter users that match search criteria, case insensitive
            List<User> filteredUsers = new ArrayList<>();
            
            for (User user : allUsers) {
                // Check if username contains search text
                boolean matchesSearch = user.getUsername().toLowerCase().contains(searchText.toLowerCase());
                
                // Check if user is already in selected list
                boolean alreadySelected = false;
                for (User selected : selectedParticipants) {
                    if (selected.getUserId().equals(user.getUserId())) {
                        alreadySelected = true;
                        break;
                    }
                }
                
                // Check if user is current user
                boolean isCurrentUser = user.getUserId().equals(currentUser.getUserId());
                
                // Only add users that meet all conditions
                if (matchesSearch && !alreadySelected && !isCurrentUser) {
                    filteredUsers.add(user);
                }
            }
            
            // Clear and set new search results
            searchResultsView.getItems().clear();
            
            if (filteredUsers.isEmpty()) {
                System.out.println("No matching users found");
            } else {
                System.out.println("Found " + filteredUsers.size() + " matching users");
                searchResultsView.getItems().addAll(filteredUsers);
            }
        };
        
        // Search button click event
        searchBtn.setOnAction(e -> performSearch.run());
        
        // Search field enter key event
        searchField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                performSearch.run();
            }
        });
        
        // Add user button event
        addBtn.setOnAction(e -> {
            User selectedUser = searchResultsView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                // Check if user is already in the list
                boolean alreadyExists = false;
                for (User participant : selectedParticipants) {
                    if (participant.getUserId().equals(selectedUser.getUserId())) {
                        alreadyExists = true;
                        break;
                    }
                }
                
                if (!alreadyExists) {
                    selectedParticipants.add(selectedUser);
                    participantListView.getItems().add(selectedUser);
                    searchResultsView.getItems().remove(selectedUser);
                }
            }
        });
        
        // Remove user button event
        removeBtn.setOnAction(e -> {
            User selectedUser = participantListView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                selectedParticipants.remove(selectedUser);
                participantListView.getItems().remove(selectedUser);
                
                // If current search results include this user, add it back to search results
                String searchText = searchField.getText().trim();
                if (!searchText.isEmpty() && 
                    selectedUser.getUsername().toLowerCase().contains(searchText.toLowerCase())) {
                    searchResultsView.getItems().add(selectedUser);
                }
            }
        });
        
        // Add all components to section
        section.getChildren().addAll(
            searchBox,
            resultsLabel,
            searchResultsView,
            selectedLabel,
            participantListView,
            actionBox
        );
        
        return section;
    }
    
    /**
     * Validate user inputs
     * @param title Event title
     * @return true if inputs are valid, false otherwise
     */
    private boolean validateInputs(String title) {
        if (title == null || title.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Invalid Title", 
                "Event title cannot be empty.");
            return false;
        }
        
        if (title.length() > 50) {
            showAlert(Alert.AlertType.ERROR, "Invalid Title", 
                "Event title cannot exceed 50 characters.");
            return false;
        }
        
        return true;
    }
    
<<<<<<< HEAD
=======
    /**
     * Check if time conflicts with existing events
     * @param userId User ID
     * @param date Event date
     * @param startTime Start time
     * @param endTime End time
     * @return true if there is a conflict, false otherwise
     */
>>>>>>> branch 'master' of https://github.com/Yuesong2001/info6205Final.git
    private boolean checkTimeConflict(String userId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        List<Event> existingEvents = dataStore.getUserEventsByDay(userId, date);
        
        LocalDateTime newStartDateTime = LocalDateTime.of(date, startTime);
        LocalDateTime newEndDateTime = LocalDateTime.of(date, endTime);
        
        for (Event event : existingEvents) {
            // Check if new event overlaps with existing events
            LocalDateTime existingStart = event.getStartTime();
            LocalDateTime existingEnd = event.getEndTime();
            
<<<<<<< HEAD
            // 完整的重叠条件：
            // 1. 如果新事件的开始时间小于等于现有事件的结束时间，且
            // 2. 新事件的结束时间大于等于现有事件的开始时间
            // 则两个事件重叠
=======
            // Overlap conditions: new event start time is within existing event time range
            // or new event end time is within existing event time range
            // or new event completely encompasses the existing event
>>>>>>> branch 'master' of https://github.com/Yuesong2001/info6205Final.git
            boolean overlaps = 
                (newStartDateTime.isBefore(existingEnd) || newStartDateTime.isEqual(existingEnd)) && 
                (newEndDateTime.isAfter(existingStart) || newEndDateTime.isEqual(existingStart));
            
            if (overlaps) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * User list cell
     */
    private static class UserListCell extends ListCell<User> {
        @Override
        protected void updateItem(User user, boolean empty) {
            super.updateItem(user, empty);
            
            // Clear old content
            setText(null);
            setGraphic(null);
            
            if (empty || user == null) {
                // Cell is empty, don't display content
            } else {
                // Set cell text and style
                setText(user.getUsername() + " (ID: " + user.getUserId() + ")");
                setStyle("-fx-padding: 5; -fx-font-size: 12px;");
            }
        }
    }
    
    /**
     * Create hour selection combo box
     * @return ComboBox with 0-23 hour options
     */
    private ComboBox<String> createHourComboBox() {
        ComboBox<String> hourBox = new ComboBox<>();
        for (int i = 0; i < 24; i++) {
            hourBox.getItems().add(String.format("%02d", i));
        }
        return hourBox;
    }
    
    /**
     * Create minute selection combo box
     * @return ComboBox with 0-59 minute options
     */
    private ComboBox<String> createMinuteComboBox() {
        ComboBox<String> minuteBox = new ComboBox<>();
        for (int i = 0; i < 60; i += 5) {
            minuteBox.getItems().add(String.format("%02d", i));
        }
        if (!minuteBox.getItems().contains("01")) {
            for (int i = 1; i < 60; i++) {
                if (i % 5 != 0) {
                    minuteBox.getItems().add(String.format("%02d", i));
                }
            }
        }
        return minuteBox;
    }
    
    /**
     * Display alert dialog
     * @param alertType Alert type
     * @param title Dialog title
     * @param message Display message content
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Display confirmation dialog
     * @param title Dialog title
     * @param message Confirmation message content
     * @return true if user clicked OK button, false otherwise
     */
    private boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        return alert.showAndWait().filter(r -> r == ButtonType.OK).isPresent();
    }
}