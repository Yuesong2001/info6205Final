package controller;

import model.DataStore;
import model.User;
import view.CalendarView;
import view.LoginView;

public class MainController {

    private DataStore dataStore;
    private NavigationController navigationController;
    private User currentUser;

    public MainController(DataStore dataStore, NavigationController navigationController) {
        this.dataStore = dataStore;
        this.navigationController = navigationController;
        this.currentUser = null;
    }

    // Login logic
    public boolean login(String username, String password) {
        User user = dataStore.findUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {

        	this.currentUser=user;

            // Login successful -> switch to calendar view
            navigationController.pushPane(new CalendarView(user, dataStore, navigationController,currentUser));
            return true;
        }
        return false;
    }


    // Logout logic
    public void logout() {
    	this.currentUser = null; //clear user on logout
        // Return to login page
        navigationController.popPane();
    }
    
    public User getCurrentUser() {
    	return currentUser;
    }
}