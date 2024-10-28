package com.bharath.dao;

import com.bharath.model.Users;
import java.util.List;

public interface UsersDao {

    List<Users> listAllUsers();
    boolean resetOwnPassword(int userId, String password);
    boolean removeUser(int userId);
    boolean isAdminCheck(int userId);
    int addNewUser(Users users);
    boolean updateTheRoleOfTheUserToAdmin(int userId);
    boolean updateTheRoleOfTheAdminToUser(int userId);
    Users getUser(int userId);
    boolean isUserIdExits(int userId);
    boolean isEmailAlreadyExits(String emailId);
    int getUserIdByEmailId(String emailId);
    boolean unBlockUser(int userId);
    boolean blockUser(int userId);
}
