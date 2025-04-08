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

    // 获取所有用户列表（可以用来在界面显示）
    public List<User> getAllUsers() {
        return new ArrayList<>(dataStore.getUserMap().values());
    }

    // 根据 userId 拿到对应的User对象
    public User getUserById(String userId) {
        return dataStore.getUserMap().get(userId);
    }

    // 添加好友关系
    public void addFriendRelation(String userId1, String userId2) {
        dataStore.addFriendRelation(userId1, userId2);
    }

    // 获取某个用户的好友列表（User对象形式）
    public List<User> getFriendsOfUser(String userId) {
        // 从 adjacencyList 拿到好友ID集，再映射回User对象
        Set<String> friendIds = dataStore.getAdjacencyList().get(userId);
        if(friendIds == null) return Collections.emptyList();

        return friendIds.stream()
                .map(fid -> dataStore.getUserMap().get(fid))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // 推荐好友
    public List<User> recommendFriends(String userId) {
        // dataStore.recommendFriends 返回的是Set<String> (用户ID)
        Set<String> recommendedIds = dataStore.recommendFriends(userId);
        return recommendedIds.stream()
                .map(rid -> dataStore.getUserMap().get(rid))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}

