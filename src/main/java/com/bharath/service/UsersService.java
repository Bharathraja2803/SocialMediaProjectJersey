package com.bharath.service;

import java.util.List;

import com.bharath.model.Users;

public interface UsersService {
	boolean isValidUserId(int userId);
	boolean isValidPassword(String password);
	boolean isUserBlocked(int userId);
	Users getUser(int userId);
	List<Users> getAllUsers();
	boolean isValidUserName(String userName);
	boolean isCorrectCredentials(int userId, String password);
	boolean isDobValid(String dob);
	boolean isEmailIdValid(String emailId);
	boolean isEmailIdExists(String emailId);
	int addNewUser(Users users);
	int getUserIdByEmailId(String emailId);
	boolean isValidEmailAndDobOfUser(String emailId, String dob);
	boolean isNewAndConfirmPasswordMatching(String newPassword, String confirmPassword);
	boolean isOldPasswordAndNewPasswordAreSame(Users users, String newPassword);
	boolean resetOwnPassword(int userId, String password);
	boolean isAdminCheck(int userId);
	boolean updateTheRoleOfTheUser(int userId, int targetUserId, String role);
	boolean removeUser(int userId, int targetUserId);
	boolean unBlockUser(int userId, int targetUserId);
	boolean blockUser(int userId, int targetUserId);
}
