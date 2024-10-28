package com.bharath.service;

import java.util.List;

import com.bharath.model.Users;

public interface FollowerService {
	List<Users> getAllFollowedUsers(int userId);
	List<Users> getAllUsersFollowingYou(int userId);
	boolean isFollowing(int userId, int followingUserId);
	boolean unFollowUser(int userId, int unFollowUserId);
	boolean followUser(int userId, int userIdToFollow);
	List<Users> getAllNotFollowedUsers(int userId);
	int countOfUsersWeAreFollowing(int userId);
	int countOfUsersWhoAreFollowingUs(int userId);
}
