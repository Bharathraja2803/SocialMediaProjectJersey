package com.bharath.dao;

import com.bharath.model.Users;

import java.util.List;

public interface FollowerDao {
    boolean followUser(int userId, int followerId);
    List<Users> listAllFollowedUsers(int userId);
    List<Users> listOfAllUsersFollowingYou(int userId);
    boolean isFollowing(int userId, int followerId);
    boolean unFollowUser(int userId, int unFollowId);
    int countOfUsersWeAreFollowing(int userId);
    int countOfUsersWhoAreFollowingUs(int userId);
}
