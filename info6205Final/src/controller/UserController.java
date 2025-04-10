package controller;

import model.DataStore;
import model.User;

import java.util.*;
import java.util.stream.Collectors;

public class UserController {

    private DataStore dataStore;

    public UserController(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    // Get all users list (can be used for display in UI)
    public List<User> getAllUsers() {
        return new ArrayList<>(dataStore.getUserMap().values());
    }

    // Get User object by userId
    public User getUserById(String userId) {
        return dataStore.getUserMap().get(userId);
    }

    // Add friend relationship
    public void addFriendRelation(String userId1, String userId2) {
        dataStore.addFriendRelation(userId1, userId2);
    }

    // Get friends list of a user (as User objects)
    public List<User> getFriendsOfUser(String userId) {
        // Get friend IDs set from adjacencyList, then map back to User objects
        Set<String> friendIds = dataStore.getAdjacencyList().get(userId);
        if(friendIds == null) return Collections.emptyList();

        return friendIds.stream()
                .map(fid -> dataStore.getUserMap().get(fid))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // Recommend friends
    public List<User> recommendFriends(String userId) {
        // dataStore.recommendFriends returns Set<String> (user IDs)
        Set<String> recommendedIds = dataStore.recommendFriends(userId);
        return recommendedIds.stream()
                .map(rid -> dataStore.getUserMap().get(rid))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}