package controller;

import model.DataStore;
import model.Event;
import model.PriorityLevel;
import model.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

public class EventController {
    private DataStore dataStore;

    public EventController(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public void createEvent(User user, String title, LocalDateTime start, LocalDateTime end,
                            String participants, PriorityLevel priority) {
        String eventId = UUID.randomUUID().toString();
        // Simplified handling of participants
        Event newEvent = new Event(eventId, title, start, end,
                                   Arrays.asList(participants.split(",")), 
                                   priority);
        dataStore.addEvent(user.getUserId(), newEvent);
    }

    public void searchEventsByTitle(String title) {
        // Directly call dataStore method
        // UI can be updated later, this is just for demonstration
        dataStore.searchEventsByTitle(title).forEach(System.out::println);
    }
}
