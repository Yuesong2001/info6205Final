package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import adt.MyMap;
import adt.impl.ArrayStack;
import adt.impl.HashMap;
import adt.MyPriorityQueue;
import adt.MyStack;
import adt.impl.PriorityQueue;


/**
 * DataStore:
 *  - Maintains core data structures:
 *      1) userMap (String -> User)
 *      2) eventMap (String -> Event)
 *      3) userDailyEvents (String -> (LocalDate -> PriorityQueue<Event>))
 *      4) adjacencyList (String -> Set<String>) for friendships
 *      5) stack (Stack<Object>) for page transitions or undo actions
 */
public class DataStore {

    // ========= 1) USER MAP & 2) EVENT MAP =========
    private MyMap<String, User> userMap = new HashMap<>();
    private MyMap<String, Event> eventMap = new HashMap<>();

    // ========= 3) USER DAILY EVENTS (PriorityQueue) =========
    // userId -> (date -> priority queue of events)
    private MyMap<String, MyMap<LocalDate, PriorityQueue<Event>>> userDailyEvents = new HashMap<>();

    // ========= 4) ADJACENCY LIST for user relationships =========
    // userId -> set of friend userIds
    private MyMap<String, Set<String>> adjacencyList = new HashMap<>();

    // ========= 5) STACK for page transitions or undo operations =========
    // This can store different types depending on your usage:
    // e.g., Stack<Pane> for GUI pages, or Stack<Operation> for undo actions.
    private MyStack<Object> stack = new ArrayStack<>();

    /**
     * Constructor:
     *  Optionally initialize some default data.
     */
    public DataStore() {
        // Example: Add a default user to userMap
        User u1 = new User("u001", "alice", "123456");
        User u2 = new User("u002", "bob",   "pwd123");
        User u3 = new User("u003", "cathy", "abcxyz");
        User u4 = new User("u004", "dylan", "456456");
        addUser(u1);
        addUser(u2);
        addUser(u3);
        addUser(u4);

        // Some friend relations
        addFriendRelation("u001", "u002"); // alice & bob
        addFriendRelation("u002", "u003"); // bob & cathy
        addFriendRelation("u002", "u004");
        
    }

    // ===========================================
    // ============ USER RELATED METHODS =========
    // ===========================================

    /**
     * Add a new user to both userMap and adjacencyList.
     */
    public void addUser(User user) {
        userMap.put(user.getUserId(), user);
        adjacencyList.putIfAbsent(user.getUserId(), new HashSet<>());
    }

    /**
     * Find user by username (linear search).
     */
    public User findUserByUsername(String username) {
        for (User u : userMap.values()) {
            if (u.getUsername().equals(username)) {
                return u;
            }
        }
        return null;
    }

    /**
     * Get the userMap if needed externally.
     */
    public MyMap<String, User> getUserMap() {
        return userMap;
    }

    // ===========================================
    // =========== FRIEND RELATION METHODS =======
    // ===========================================

    /**
     * Add a two-way (undirected) friendship between two userIds.
     */
    public void addFriendRelation(String userId1, String userId2) {
        adjacencyList.putIfAbsent(userId1, new HashSet<>());
        adjacencyList.putIfAbsent(userId2, new HashSet<>());
        adjacencyList.get(userId1).add(userId2);
        adjacencyList.get(userId2).add(userId1);
    }

    /**
     * Simple friend recommendation: returns a Set of userIds at distance=2.
     */
    public Set<String> recommendFriends(String userId) {
        Set<String> directFriends = adjacencyList.get(userId);
        if (directFriends == null) {
            return Collections.emptySet();
        }
        Set<String> recommended = new HashSet<>();
        // For each friend, add their friends (excluding self & directFriends)
        for (String friendId : directFriends) {
            Set<String> friendsOfFriend = adjacencyList.get(friendId);
            if (friendsOfFriend == null) continue;

            for (String friendOfFriend : friendsOfFriend) {
                if (!friendOfFriend.equals(userId) && !directFriends.contains(friendOfFriend)) {
                    recommended.add(friendOfFriend);
                }
            }
        }
        return recommended;
    }

    /**
     * Expose adjacencyList if you need it externally.
     */
    public MyMap<String, Set<String>> getAdjacencyList() {
        return adjacencyList;
    }

    // ===========================================
    // ============ EVENT RELATED METHODS ========
    // ===========================================

    /**
     * Add an event for a specific user. Also store in eventMap for quick ID lookups.
     */
    public void addEvent(String userId, Event event) {
        // 1) Put event into eventMap
        eventMap.put(event.getEventId(), event);

        // 2) PriorityQueue logic
        userDailyEvents.putIfAbsent(userId, new HashMap<>());
        LocalDate date = event.getStartTime().toLocalDate();

        // If no PQ for this date, create one
        userDailyEvents.get(userId).putIfAbsent(date, new PriorityQueue<>(new EventPriorityComparator()));
        // Add event
        userDailyEvents.get(userId).get(date).add(event);

        System.out.println("AddEvent: userId=" + userId + ", eventTitle=" + event.getTitle());
    }

    /**
     * Retrieve an event by ID.
     */
    public Event getEventById(String eventId) {
        return eventMap.get(eventId);
    }

    /**
     * Simple title-based search (linear).
     */
    public List<Event> searchEventsByTitle(String title) {
        List<Event> result = new ArrayList<>();
        for (Event e : eventMap.values()) {
            if (e.getTitle().toLowerCase().contains(title.toLowerCase())) {
                result.add(e);
            }
        }
        return result;
    }

    /**
     * Get events for a user on a specific date, sorted by priority/time in a PQ.
     */
    public List<Event> getUserEventsByDay(String userId, LocalDate day) {
        MyMap<LocalDate, PriorityQueue<Event>> dailyMap = userDailyEvents.get(userId);
        if (dailyMap == null) {
            return Collections.emptyList();
        }
        PriorityQueue<Event> pq = dailyMap.get(day);
        if (pq == null) {
            return Collections.emptyList();
        }
        // Copy to a list to avoid modifying the original PQ order
        return new ArrayList<>(pq.toList());
    }

    /**
     * Expose eventMap if needed externally.
     */
    public MyMap<String, Event> getEventMap() {
        return eventMap;
    }

    // ===========================================
    // ===== PRIORITY QUEUE STRUCTURE EXAMPLE ====
    // ===========================================
    // Already embedded above in addEvent/getUserEventsByDay

    // ===========================================
    // ========= STACK USAGE METHODS =============
    // ===========================================

    /**
     * For page transitions or undo operations, we store them in a stack.
     * You can store Pane objects, Operation objects, or any type you prefer.
     */
    public void pushToStack(Object item) {
        stack.push(item);
    }

    public Object popFromStack() {
        if (!stack.isEmpty()) {
            return stack.pop();
        }
        return null;
    }

    public boolean isStackEmpty() {
        return stack.isEmpty();
    }

    // Example usage: push an operation or page name into stack, pop to revert.
    
    // Remove events
    public boolean removeEvent(String userId, String eventId) {
        // Check if events exist
        Event eventToRemove = eventMap.get(eventId);
        if (eventToRemove == null) {
            System.out.println("RemoveEvent: Event not found, eventId=" + eventId);
            return false;
        }
        
        // Get the date of the event
        LocalDate eventDate = eventToRemove.getStartTime().toLocalDate();
        
        // Check if the user has any events
        MyMap<LocalDate, PriorityQueue<Event>> userEvents = userDailyEvents.get(userId);
        if (userEvents == null) {
            System.out.println("RemoveEvent: No events for userId=" + userId);
            return false;
        }
        
        // Check if the user has events on that date
        PriorityQueue<Event> eventsOnDate = userEvents.get(eventDate);
        if (eventsOnDate == null) {
            System.out.println("RemoveEvent: No events for date=" + eventDate);
            return false;
        }
        
        // Remove events from priority queue
        boolean removed = eventsOnDate.removeIf(event -> event.getEventId().equals(eventId));
        
        // If the priority queue is now empty, remove it from the map
        if (eventsOnDate.isEmpty()) {
            userEvents.remove(eventDate);
            
            // If the user has no more events, remove their entry from userDailyEvents
            if (userEvents.isEmpty()) {
                userDailyEvents.remove(userId);
            }
        }
        
        // Remove from eventMap
        if (removed) {
            eventMap.remove(eventId);
            System.out.println("RemoveEvent: Successfully removed eventId=" + eventId + ", title=" + eventToRemove.getTitle());
        } else {
            System.out.println("RemoveEvent: Event not found in user's events, eventId=" + eventId);
        }
        
        return removed;
    }
}


