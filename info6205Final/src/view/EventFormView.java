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
     * 构造函数 - 使用当前日期创建事件表单
     * @param user 当前登录的用户
     * @param dataStore 数据存储对象
     * @param navController 导航控制器
     */
    public EventFormView(User user, DataStore dataStore, NavigationController navController) {
        this(user, dataStore, navController, LocalDate.now()); // 调用另一个构造函数，使用当前日期
    }

    /**
     * 构造函数 - 使用指定日期创建事件表单
     * @param user 当前登录的用户
     * @param dataStore 数据存储对象
     * @param navController 导航控制器
     * @param initialDate 初始选中的日期
     */
    public EventFormView(User user, DataStore dataStore, NavigationController navController, LocalDate initialDate) {
        this.dataStore = dataStore;
        this.currentUser = user;
        
        // 设置布局属性
        setSpacing(20);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(30));
        setStyle("-fx-background-color: #f5f8ff;");

        // 标题部分
        Label titleLabel = new Label("Create New Event");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // 事件标题字段
        Label eventTitleLabel = new Label("Event Title:");
        TextField titleField = new TextField();
        titleField.setPromptText("Enter event title");
        titleField.setPrefWidth(300);

        // 日期选择器
        Label dateLabel = new Label("Date:");
        datePicker = new DatePicker(initialDate);
        
        // 开始时间选择器
        Label startTimeLabel = new Label("Start Time:");
        startHourBox = createHourComboBox();
        startMinuteBox = createMinuteComboBox();
        HBox startTimeBox = new HBox(10);
        startTimeBox.getChildren().addAll(startHourBox, new Text(":"), startMinuteBox);
        
        // 结束时间选择器
        Label endTimeLabel = new Label("End Time:");
        endHourBox = createHourComboBox();
        endMinuteBox = createMinuteComboBox();
        HBox endTimeBox = new HBox(10);
        endTimeBox.getChildren().addAll(endHourBox, new Text(":"), endMinuteBox);
        
        // 设置默认时间值 (10:00 - 11:00)
        startHourBox.setValue("10");
        startMinuteBox.setValue("00");
        endHourBox.setValue("11");
        endMinuteBox.setValue("00");

        // 参与者搜索和选择部分
        VBox participantSection = createParticipantSection();

        // 优先级选择
        Label priorityLabel = new Label("Priority:");
        ComboBox<PriorityLevel> priorityBox = new ComboBox<>();
        priorityBox.getItems().addAll(PriorityLevel.LOW, PriorityLevel.MEDIUM, PriorityLevel.HIGH);
        priorityBox.setValue(PriorityLevel.MEDIUM);

        // 按钮部分
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setPrefWidth(100);
        cancelBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        
        Button saveBtn = new Button("Save");
        saveBtn.setPrefWidth(100);
        saveBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        
        buttonBox.getChildren().addAll(cancelBtn, saveBtn);

        // 创建表单布局
        GridPane formGrid = new GridPane();
        formGrid.setHgap(15);
        formGrid.setVgap(18);
        formGrid.setAlignment(Pos.CENTER);
        formGrid.setPadding(new Insets(10));
        formGrid.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-radius: 5;");

        // 设置表单标签样式
        eventTitleLabel.setStyle("-fx-font-weight: bold;");
        dateLabel.setStyle("-fx-font-weight: bold;");
        startTimeLabel.setStyle("-fx-font-weight: bold;");
        endTimeLabel.setStyle("-fx-font-weight: bold;");
        priorityLabel.setStyle("-fx-font-weight: bold;");
        
        // 设置表单字段样式
        titleField.setStyle("-fx-padding: 8; -fx-border-color: #ddd; -fx-border-radius: 3;");
        
        // 将表单元素添加到网格中
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

        // 设置按钮动作
        cancelBtn.setOnAction(e -> navController.popPane());

        // 保存按钮的动作处理
        saveBtn.setOnAction(e -> {
            // 验证输入
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
            
            // 验证结束时间是否晚于开始时间
            if (!endDateTime.isAfter(startDateTime)) {
                showAlert(Alert.AlertType.ERROR, "Invalid Time", 
                    "The end time must be later than the start time.");
                return;
            }
            
            // 检查时间冲突
            if (checkTimeConflict(user.getUserId(), date, startTime, endTime)) {
                boolean confirmOverlap = showConfirmation("Time Conflict", 
                    "This event overlaps with existing events. Do you want to schedule it anyway?");
                if (!confirmOverlap) {
                    return;
                }
            }
            
            // 获取所有参与者的用户名
            List<String> participantNames = selectedParticipants.stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
            
            // 确保创建者自己也在参与者列表中
            if (!participantNames.contains(user.getUsername())) {
                participantNames.add(user.getUsername());
            }
            
            // 创建事件并添加到数据存储中
            Event newEvent = new Event(eventId, title, startDateTime, endDateTime,
                    participantNames, priorityBox.getValue());
            dataStore.addEvent(user.getUserId(), newEvent);

            // 显示成功消息
            showAlert(Alert.AlertType.INFORMATION, "Success", "Event successfully created!");
            
            // 返回上一个界面
            navController.popPane();
            
            // 打印日志信息
            System.out.println("Save Event => " + newEvent.getTitle() 
                + ", startTime=" + newEvent.getStartTime() 
                + ", userId=" + user.getUserId());
        });

        // 将所有组件添加到主VBox布局中
        getChildren().addAll(
            titleLabel,
            new Separator(),
            formGrid,
            new Separator(),
            buttonBox
        );
    }
    
    /**
     * 创建参与者搜索和选择部分
     * @return 包含搜索字段和参与者列表的VBox
     */
    private VBox createParticipantSection() {
        VBox section = new VBox(8);
        section.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 3; -fx-padding: 10;");
        
        // 搜索部分
        HBox searchBox = new HBox(5);
        searchField = new TextField();
        searchField.setPromptText("Search users...");
        searchField.setPrefWidth(250);
        
        Button searchBtn = new Button("Search");
        searchBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        
        searchBox.getChildren().addAll(searchField, searchBtn);
        
        // 搜索结果部分
        Label resultsLabel = new Label("Search Results:");
        resultsLabel.setStyle("-fx-font-weight: bold;");
        
        // 创建搜索结果ListView
        ListView<User> searchResultsView = new ListView<>();
        searchResultsView.setPrefHeight(100);
        searchResultsView.setPlaceholder(new Label("No results found"));
        searchResultsView.setCellFactory(lv -> new UserListCell());
        
        // 已选参与者部分
        Label selectedLabel = new Label("Selected Participants:");
        selectedLabel.setStyle("-fx-font-weight: bold;");
        
        // 创建参与者ListView
        participantListView = new ListView<>();
        participantListView.setPrefHeight(100);
        participantListView.setPlaceholder(new Label("No participants selected"));
        participantListView.setCellFactory(lv -> new UserListCell());
        
        // 添加和移除按钮
        HBox actionBox = new HBox(10);
        actionBox.setAlignment(Pos.CENTER);
        
        Button addBtn = new Button("Add Selected");
        addBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        
        Button removeBtn = new Button("Remove Selected");
        removeBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        
        actionBox.getChildren().addAll(addBtn, removeBtn);
        
        // 搜索逻辑实现
        Runnable performSearch = () -> {
            String searchText = searchField.getText().trim();
            if (searchText.isEmpty()) {
                searchResultsView.getItems().clear();
                return;
            }
            
            System.out.println("Searching for: " + searchText);
            
            // 从DataStore中获取所有用户
            List<User> allUsers = new ArrayList<>(dataStore.getUserMap().values());
            System.out.println("Total users in system: " + allUsers.size());
            
            // 过滤符合搜索条件的用户，忽略大小写
            List<User> filteredUsers = new ArrayList<>();
            
            for (User user : allUsers) {
                // 检查用户名是否包含搜索文本
                boolean matchesSearch = user.getUsername().toLowerCase().contains(searchText.toLowerCase());
                
                // 检查用户是否已在选定列表中
                boolean alreadySelected = false;
                for (User selected : selectedParticipants) {
                    if (selected.getUserId().equals(user.getUserId())) {
                        alreadySelected = true;
                        break;
                    }
                }
                
                // 检查是否为当前用户
                boolean isCurrentUser = user.getUserId().equals(currentUser.getUserId());
                
                // 只有满足所有条件的用户才添加到结果中
                if (matchesSearch && !alreadySelected && !isCurrentUser) {
                    filteredUsers.add(user);
                }
            }
            
            // 清空并设置新的搜索结果
            searchResultsView.getItems().clear();
            
            if (filteredUsers.isEmpty()) {
                System.out.println("No matching users found");
            } else {
                System.out.println("Found " + filteredUsers.size() + " matching users");
                searchResultsView.getItems().addAll(filteredUsers);
            }
        };
        
        // 搜索按钮点击事件
        searchBtn.setOnAction(e -> performSearch.run());
        
        // 搜索字段回车键事件
        searchField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                performSearch.run();
            }
        });
        
        // 添加用户按钮事件
        addBtn.setOnAction(e -> {
            User selectedUser = searchResultsView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                // 检查用户是否已在列表中
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
        
        // 移除用户按钮事件
        removeBtn.setOnAction(e -> {
            User selectedUser = participantListView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                selectedParticipants.remove(selectedUser);
                participantListView.getItems().remove(selectedUser);
                
                // 如果当前搜索结果包含此用户的条件，把它重新加到搜索结果中
                String searchText = searchField.getText().trim();
                if (!searchText.isEmpty() && 
                    selectedUser.getUsername().toLowerCase().contains(searchText.toLowerCase())) {
                    searchResultsView.getItems().add(selectedUser);
                }
            }
        });
        
        // 添加所有组件到部分
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
     * 验证用户输入
     * @param title 事件标题
     * @return 如果输入有效则返回true，否则返回false
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
    
    /**
     * 检查时间是否与现有事件冲突
     * @param userId 用户ID
     * @param date 事件日期
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 如果有冲突则返回true，否则返回false
     */
    private boolean checkTimeConflict(String userId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        List<Event> existingEvents = dataStore.getUserEventsByDay(userId, date);
        
        LocalDateTime newStartDateTime = LocalDateTime.of(date, startTime);
        LocalDateTime newEndDateTime = LocalDateTime.of(date, endTime);
        
        for (Event event : existingEvents) {
            // 检查新事件是否与现有事件重叠
            LocalDateTime existingStart = event.getStartTime();
            LocalDateTime existingEnd = event.getEndTime();
            
            // 重叠条件：新事件的开始时间在现有事件的时间范围内
            // 或新事件的结束时间在现有事件的时间范围内
            // 或新事件完全包含现有事件
            boolean overlaps = 
                (newStartDateTime.isBefore(existingEnd) && newStartDateTime.isAfter(existingStart)) || 
                (newEndDateTime.isAfter(existingStart) && newEndDateTime.isBefore(existingEnd)) ||
                (newStartDateTime.isBefore(existingStart) && newEndDateTime.isAfter(existingEnd));
            
            if (overlaps) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 用户列表单元格
     */
    private static class UserListCell extends ListCell<User> {
        @Override
        protected void updateItem(User user, boolean empty) {
            super.updateItem(user, empty);
            
            // 清除旧内容
            setText(null);
            setGraphic(null);
            
            if (empty || user == null) {
                // 单元格为空不显示内容
            } else {
                // 设置单元格文本和样式
                setText(user.getUsername() + " (ID: " + user.getUserId() + ")");
                setStyle("-fx-padding: 5; -fx-font-size: 12px;");
            }
        }
    }
    
    /**
     * 创建小时选择下拉框
     * @return 包含0-23小时选项的ComboBox
     */
    private ComboBox<String> createHourComboBox() {
        ComboBox<String> hourBox = new ComboBox<>();
        for (int i = 0; i < 24; i++) {
            hourBox.getItems().add(String.format("%02d", i));
        }
        return hourBox;
    }
    
    /**
     * 创建分钟选择下拉框
     * @return 包含0-59分钟选项的ComboBox
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
     * 显示提示对话框
     * @param alertType 对话框类型
     * @param title 对话框标题
     * @param message 显示的消息内容
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * 显示确认对话框
     * @param title 对话框标题
     * @param message 确认消息内容
     * @return 如果用户点击了确认按钮则返回true，否则返回false
     */
    private boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        return alert.showAndWait().filter(r -> r == ButtonType.OK).isPresent();
    }
}