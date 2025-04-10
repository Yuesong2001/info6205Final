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
	
	 // 类的成员变量
    private User currentUser ; // 当前登录的用户
    private DataStore dataStore; // 数据存储对象，用于获取和修改事件数据
    private NavigationController navController; // 导航控制器，用于页面切换
    private MainController mainController;
    
    
	
	
    
    /**
     * 显示提示信息对话框的辅助方法
     * @param title 对话框标题
     * @param message 显示的消息内容
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null); // 不显示头部文本
        alert.setContentText(message);
        alert.showAndWait(); // 显示对话框并等待用户响应
    }
    
    /**
     * 显示确认对话框的辅助方法
     * @param title 对话框标题
     * @param message 确认消息内容
     * @return 如果用户点击了确认按钮则返回true，否则返回false
     */
    private boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null); // 不显示头部文本
        alert.setContentText(message);
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK; // 检查用户是否点击了确认按钮
    }

   

    /**
     * 构造函数 - 创建日历视图
     * @param user 当前登录的用户
     * @param dataStore 数据存储对象
     * @param navController 导航控制器
     */
    public CalendarView(User user, DataStore dataStore, NavigationController navController,User currentUser) {
        
        this.dataStore = dataStore;
        this.navController = navController;
        this.currentUser = currentUser;
        

        // 设置布局属性
        setSpacing(15); // 设置子元素之间的间距
        setAlignment(Pos.CENTER); // 居中对齐
        setPadding(new Insets(20)); // 设置内边距
        setStyle("-fx-background-color: #f8f8f8;"); // 设置背景颜色

        // 创建欢迎标签
        Label welcomeLabel = new Label("Welcome, " + user.getUsername());
        welcomeLabel.setFont(Font.font("System", FontWeight.BOLD, 20)); // 设置字体样式
        welcomeLabel.setStyle("-fx-text-fill: #3366cc;"); // 设置文字颜色

        // 日期选择部分
        Label dateLabel = new Label("Select Date:");
        DatePicker datePicker = new DatePicker(LocalDate.now()); // 创建日期选择器，默认显示当前日期
        datePicker.setStyle("-fx-pref-width: 200px;"); // 设置日期选择器宽度
        
        // 创建日期选择栏
        javafx.scene.layout.HBox dateBar = new javafx.scene.layout.HBox(10); // 水平布局，间距为10
        dateBar.setAlignment(Pos.CENTER); // 居中对齐
        dateBar.getChildren().addAll(dateLabel, datePicker); // 添加标签和日期选择器到布局中

        // 用于显示事件列表
        Button showEventsBtn = new Button("Show My Events"); // 显示事件按钮
        ListView<String> eventListView = new ListView<>(); // 事件列表视图
        
        // 跟踪选中的事件
        Label selectedEventLabel = new Label("No event selected"); // 显示选中事件的标签
        selectedEventLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #555555;"); // 设置标签样式
        Event[] selectedEvent = new Event[1]; // 使用数组来允许在lambda表达式中修改引用

        // 设置显示事件按钮的点击事件处理
        showEventsBtn.setOnAction(e -> {
            LocalDate selectedDate = datePicker.getValue(); // 获取选中的日期
            List<Event> events = dataStore.getUserEventsByDay(user.getUserId(), selectedDate); // 获取该日期的事件列表
            
            // 先检查获取到的事件列表是否为空
            if (events.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("No Events");
                alert.setHeaderText(null);
                alert.setContentText("You have no events on this date!");
                alert.showAndWait();
                
                // 重置选中状态和列表
                selectedEvent[0] = null;
                selectedEventLabel.setText("No event selected");
                eventListView.getItems().clear();
                eventListView.setUserData(null);
                return; // 如果没有事件，直接返回，不执行后面的代码
            }
            
            // 重置选中状态
            selectedEvent[0] = null;
            selectedEventLabel.setText("No event selected");
            
            // —— 测试信息输出 ——
            System.out.println("------ Show My Events ------");
            System.out.println("Selected date: " + selectedDate);
            System.out.println("Current user ID: " + user.getUserId());
            System.out.println("Found events: " + events.size());
            for (Event ev : events) {
                System.out.println("Event: " + ev.getTitle() + " " + ev.getStartTime());
            }
            System.out.println("----------------------------");

            // 清空并更新列表视图，显示详细的事件信息
            eventListView.getItems().clear();
            for (Event event : events) {
                String participants = String.join(", ", event.getParticipants()); // 将参与者列表转换为逗号分隔的字符串
                String displayText = String.format("%s\nTime: %s - %s\nPriority: %s\nParticipants: %s", 
                    event.getTitle(),
                    event.getStartTime().toLocalTime(),
                    event.getEndTime().toLocalTime(),
                    event.getPriority(),
                    participants);
                eventListView.getItems().add(displayText); // 添加格式化的事件信息到列表中
            }
            
            // 存储原始事件列表以供后续引用
            eventListView.setUserData(events);
        });
        
        // 为列表视图添加选择监听器
        eventListView.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.intValue() >= 0 && eventListView.getUserData() != null) {
                @SuppressWarnings("unchecked") // 抑制类型转换警告
                List<Event> events = (List<Event>) eventListView.getUserData();
                if (newVal.intValue() < events.size()) {
                    selectedEvent[0] = events.get(newVal.intValue()); // 更新选中的事件
                    selectedEventLabel.setText("Selected: " + selectedEvent[0].getTitle()); // 更新显示标签
                }
            } else {
                selectedEvent[0] = null;
                selectedEventLabel.setText("No event selected");
            }
        });
        
        // 为列表视图添加选择监听器
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

        // 添加事件按钮
        Button addEventBtn = new Button("Add Event");
        addEventBtn.setOnAction(e -> {
            // 将选中的日期传递给事件表单视图
            LocalDate selectedDate = datePicker.getValue();
            navController.pushPane(new EventFormView(currentUser, dataStore, navController, selectedDate)); // 导航到事件表单页面
        });
        
        // 取消事件按钮
        Button cancelEventBtn = new Button("Cancel Event");
        cancelEventBtn.setStyle("-fx-base: #ff9999;"); // 设置浅红色背景
        
        // 设置取消事件按钮的点击事件处理
        cancelEventBtn.setOnAction(e -> {
            if (selectedEvent[0] == null) {
                showAlert("Error", "Please Choose an Event first!"); // 如果未选择事件，显示错误提示
                return;
            }
            
            // 确认删除
            boolean confirmDelete = showConfirmation("Cancel Event", 
                "Are you sure you want to cancel the event: " + selectedEvent[0].getTitle() + "?");
                
            if (confirmDelete) {
                // 移除事件
                dataStore.removeEvent(currentUser.getUserId(), selectedEvent[0].getEventId());
                
                // 刷新视图
                showEventsBtn.fire(); // 触发显示事件按钮的点击事件
                
                // 显示成功信息
                showAlert("Success", "Event successfully cancelled!");
            }
        });
        
        Button addFriendBtn = new Button("Add Friend");
        addFriendBtn.setStyle("-fx-background-color: #5cb85c; -fx-text-fill: white;"); // 绿色背景，白色文字
        addFriendBtn.setOnAction(e -> {
            // 创建对话框来获取用户名
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add Friend");
            dialog.setHeaderText("Search for a user to add as friend");
            dialog.setContentText("Enter username:");
            
            // 等待用户输入
            Optional<String> result = dialog.showAndWait();
            
            // 处理结果
            if (result.isPresent() && !result.get().trim().isEmpty()) {
                String username = result.get().trim();
                
                // 使用UserController查找用户
                User foundUser = dataStore.findUserByUsername(username);
                
                if (foundUser != null) {
                    // 确认是否添加为好友
                    boolean confirm = showConfirmation("Add Friend", 
                        "Do you want to add " + username + " as a friend?");
                    
                    if (confirm) {
                        try {
                            // 添加好友关系 - void方法，没有返回值
                            dataStore.addFriendRelation(
                                currentUser.getUserId(), 
                                foundUser.getUserId()
                            );
                            
                            // 如果没有异常，则添加成功
                            showAlert("Success", username + " has been added as your friend!");
                        } catch (Exception ex) {
                            // 如果出现异常，可能是已经是好友或其他错误
                            System.out.println("Error adding friend: " + ex.getMessage());
                            showAlert("Note", "Could not add " + username + " as a friend. They may already be your friend.");
                        }
                    }
                } else {
                    // 用户不存在
                    showAlert("User Not Found", "No user found with username: " + username);
                }
            }
        });
        
        // 登出按钮
        Button showUserFriendBtn = new Button("Manage Friends");
        showUserFriendBtn.setOnAction(e -> {
            UserController userController = new UserController(dataStore);
            UserFriendView friendView = new UserFriendView(userController, navController,currentUser,mainController);
            navController.pushPane(friendView);
        });
        Button logoutBtn = new Button("Logout");
        logoutBtn.setOnAction(e -> {
            navController.popPane(); // 返回到上一个视图（登录视图）
        });
        
        
        
        // 创建按钮栏
        javafx.scene.layout.HBox buttonBar = new javafx.scene.layout.HBox(10); // 水平布局，间距为10
        buttonBar.setAlignment(Pos.CENTER); // 居中对齐
        buttonBar.getChildren().addAll(addEventBtn, cancelEventBtn, logoutBtn); // 添加所有按钮到布局中

        getChildren().addAll(welcomeLabel, datePicker, showEventsBtn, eventListView,showUserFriendBtn);
        // 样式设置
        eventListView.setPrefHeight(300); // 设置列表视图的首选高度
        eventListView.setStyle("-fx-pref-width: 400px; -fx-font-family: 'System'; -fx-font-size: 13px;"); // 设置列表视图样式
        
        // 设置按钮样式
        showEventsBtn.setStyle("-fx-background-color: #4488cc; -fx-text-fill: white;"); // 蓝色背景，白色文字
        
        // 添加事件部分的标题
        Label eventsTitle = new Label("My Events");
        eventsTitle.setFont(Font.font("System", FontWeight.BOLD, 16)); // 设置字体样式
        
        // 添加分隔线以提升视觉组织感
        Separator separator1 = new Separator();
        Separator separator2 = new Separator();
        separator1.setPrefWidth(400);
        separator2.setPrefWidth(400);

        // 将所有组件添加到主布局中
        getChildren().addAll(
            new Separator(), // 分隔线
            dateBar,
            eventsTitle,
            selectedEventLabel,
            separator2,
            buttonBar,
            addFriendBtn
        );
        }
}