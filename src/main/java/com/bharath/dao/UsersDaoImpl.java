package com.bharath.dao;


import com.bharath.model.Users;



import com.bharath.MainCentralizedResource;
import com.bharath.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsersDaoImpl implements UsersDao {
	private static UsersDaoImpl usersDaoImp_ = null;
	private Connection connection_ = null;
    


    private UsersDaoImpl(){
		connection_ = DBConnection.getConnection();
    }

    /**
     * This method returns the instance of the UserDeoImpl class
     * @return
     */
    public static UsersDaoImpl getInstance(){
        if(usersDaoImp_ == null){
            usersDaoImp_ = new UsersDaoImpl();
            return usersDaoImp_;
        }else{
            return usersDaoImp_;
        }
    }

    /**
     * This method is used to check whether the userid is present in the database
     * select user_id from bharat_users where user_id = ?
     * @param userId
     * @return
     */
    @Override
    public boolean isUserIdExits(int userId) {
		try {
			PreparedStatement selectUserIdByFilteringUserId = connection_.prepareStatement("select user_id from users where user_id = ?");
			selectUserIdByFilteringUserId.setInt(1, userId);
			ResultSet resultSet = selectUserIdByFilteringUserId.executeQuery();
			return resultSet.next();
		} catch (SQLException e) {
			MainCentralizedResource.LOGGER.severe(e.toString());
			return false;
		}
    }

    /**
     * This method is used to retrieve the user data from DB by user id
     * select * from users where user_id = ?
     * @param userId
     * @return
     */
    @Override
    public Users getUser(int userId) {
		Users users = null;
		try {
			PreparedStatement selectQuery = connection_.prepareStatement("select * from users where user_id = ?");
			selectQuery.setInt(1, userId);
			ResultSet resultSet = selectQuery.executeQuery();

			if(!resultSet.next()){
				MainCentralizedResource.LOGGER.warning("Unable to fetch the user details");
				return null;
			}

			users = new Users(resultSet.getInt("user_id"), resultSet.getString("user_name"), resultSet.getString("password"), resultSet.getString("birthdate"), resultSet.getString("email_id"), resultSet.getDate("signup_date").toLocalDate(), resultSet.getTime("signup_time").toLocalTime(), resultSet.getString("roles"), resultSet.getString("is_blocked").charAt(0));
		} catch (SQLException e) {
			MainCentralizedResource.LOGGER.severe(e.toString());
			return null;
		}
		return users;
    }

    /**
     * This method fetches all the users data for this application
     * Select * from users
     * @return
     */
    @Override
    public List<Users> listAllUsers() {
		List<Users> allUsersData = new ArrayList<>();

		try {
			PreparedStatement preparedStatement = connection_.prepareStatement("Select * from users");
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){
				Users users = new Users(resultSet.getInt("user_id"), resultSet.getString("user_name"), resultSet.getString("password"), resultSet.getString("birthdate"), resultSet.getString("email_id"), resultSet.getDate("signup_date").toLocalDate(), resultSet.getTime("signup_time").toLocalTime(), resultSet.getString("roles"), resultSet.getString("is_blocked").charAt(0));
				allUsersData.add(users);
			}

			if(allUsersData.isEmpty()){
				MainCentralizedResource.LOGGER.warning("No user records to fetch!");
				return null;
			}
		} catch (SQLException e) {
			MainCentralizedResource.LOGGER.severe(e.toString());
			return null;
		}
		return allUsersData;
    }


    /**
     * This method is used to check if the email is already exists
     * select user_id from users where email_id = ?
     * @param emailId
     * @return
     */
    @Override
    public boolean isEmailAlreadyExits(String emailId){
		try {
			PreparedStatement checkIsEmailPresentStatement = connection_.prepareStatement("select user_id from users where email_id = ?");
			checkIsEmailPresentStatement.setString(1, emailId);
			ResultSet resultSet = checkIsEmailPresentStatement.executeQuery();
			return resultSet.next();
		} catch (SQLException e) {
			MainCentralizedResource.LOGGER.severe(e.toString());
			return false;
		}
    }
    
    /**
     * This method adds new user to the database
     * INSERT INTO users(user_id, user_name, password, birthdate, email_id, signup_date, signup_time, roles)
     				VALUES (nextval('user_id_sequence'), ?, ?, ?, ?, CURRENT_DATE, CURRENT_TIME, 'user')
     * @param users - User entered details for signup
     * @return
     */
    @Override
    public int addNewUser(Users users) {
		try {
			PreparedStatement addingUserStatement = connection_.prepareStatement("INSERT INTO users(\n" +
					"\tuser_id, user_name, password, birthdate, email_id, signup_date, signup_time, roles)\n" +
					"VALUES \n" +
					"\t(nextval('user_id_sequence'), ?, ?, ?, ?, CURRENT_DATE, CURRENT_TIME, 'user')");
			addingUserStatement.setString(1, users.getUserName_());
			addingUserStatement.setString(2, users.getPassword_());
			addingUserStatement.setDate(3, Date.valueOf(users.getBirthday_()));
			addingUserStatement.setString(4, users.getEmailId_());
			addingUserStatement.execute();
			int userId = getUserIdByEmailId(users.getEmailId_());
			if(userId == -1){
				MainCentralizedResource.LOGGER.warning("unable to create the user!");
				return -1;
			}
			MainCentralizedResource.LOGGER.info("User created successfully with user id: " + userId);
			return userId;
		} catch (SQLException e) {
			MainCentralizedResource.LOGGER.severe(e.toString());
			return -1;
		}
    }
    
    /**
     * This method is used to get the user id by email_id
     * select user_id from user where email_id = ?
     * @param emailId
     * @return
     */
    @Override
    public int getUserIdByEmailId(String emailId){
		try{
			PreparedStatement selectStatementToFetchIdByEmailId = connection_.prepareStatement("select user_id from users where email_id = ?");
			selectStatementToFetchIdByEmailId.setString(1, emailId);
			ResultSet resultSet = selectStatementToFetchIdByEmailId.executeQuery();
			if(!resultSet.next()){
				return -1;
			}
			return resultSet.getInt("user_id");
		}catch(SQLException e){
			MainCentralizedResource.LOGGER.severe(e.toString());
			return -1;
		}
    }
    
    /**
     * This method is used to reset the password of the user
     * UPDATE users SET password = ? WHERE user_id = ?
     * @param targetUser - userId of the targetUser
     * @param password - new password to reset
     */
    @Override
    public boolean resetOwnPassword(int targetUser, String password) {
		try {
			PreparedStatement updatePasswordStatement = connection_.prepareStatement("UPDATE users SET password = ? WHERE user_id = ?");
			updatePasswordStatement.setString(1, password);
			updatePasswordStatement.setInt(2, targetUser);
			updatePasswordStatement.executeUpdate();
			MainCentralizedResource.LOGGER.info("Successfully Update the password for user id " + targetUser);
			return true;
		} catch (SQLException e) {
			MainCentralizedResource.LOGGER.severe(e.toString());
			return false;
		}
    	
    }
    
    /**
     * This method is used to check whether the user is admin user
     * select user_id from users where role = 'admin' and user_id = ?
     * @param userId
     * @return
     */
    @Override
    public boolean isAdminCheck(int userId){
		try {
			PreparedStatement selectQueryToCheckForRoleAdmin = connection_.prepareStatement("select user_id from users where roles = 'admin' and user_id = ?");
			selectQueryToCheckForRoleAdmin.setInt(1, userId);
			ResultSet resultSet = selectQueryToCheckForRoleAdmin.executeQuery();
			return resultSet.next();
		} catch (SQLException e) {
			MainCentralizedResource.LOGGER.severe(e.toString());
			return false;
		}

    }
    
    /**
     * This method will update the normal user role to admin role
     * UPDATE users SET roles = 'admin' WHERE user_id = ?
     */
	@Override
	public boolean updateTheRoleOfTheUserToAdmin(int userId) {
		try {
			PreparedStatement updateRoleToAdminQuery = connection_.prepareStatement("update users set roles = 'admin' where user_id = ?");
			updateRoleToAdminQuery.setInt(1, userId);
			updateRoleToAdminQuery.executeUpdate();
			return true;
		} catch (SQLException e) {
			MainCentralizedResource.LOGGER.severe(e.toString());
			return false;
		}
	}

	/**
	 * This method is used to update the admin role to normal user role
	 * UPDATE users SET roles = 'user' WHERE user_id = ?
	 */
	@Override
	public boolean updateTheRoleOfTheAdminToUser(int userId) {
		try {
			PreparedStatement updateRoleToAdminQuery = connection_.prepareStatement("update users set roles = 'user' where user_id = ?");
			updateRoleToAdminQuery.setInt(1, userId);
			updateRoleToAdminQuery.executeUpdate();
			return true;
		} catch (SQLException e) {
			MainCentralizedResource.LOGGER.severe(e.toString());
			return false;
		}
	}

    /**
     * This method removes the user and related information from the db
     * DELETE FROM users WHERE user_id = ?
     * @param userId
     * @return
     */
    @Override
    public boolean removeUser(int userId) {

		try {
			PreparedStatement deleteUserStatement = connection_.prepareStatement("DELETE FROM users WHERE user_id = ?");
			deleteUserStatement.setInt(1, userId);
			deleteUserStatement.execute();
			MainCentralizedResource.LOGGER.info("Successfully deleted user_id: " + userId);
			return true;
		} catch (SQLException e) {
			MainCentralizedResource.LOGGER.severe(e.toString());
			return false;
		}
    	
    }
	
    /**
     * This method will update the is_blocked column to false
     * update users set is_blocked=false where user_id =?
     */
	@Override
	public boolean unBlockUser(int userId) {
		try {
			PreparedStatement updateIsBlockedStatusOfUser = connection_.prepareStatement("update users set is_blocked = 'n' where user_id = ?");
			updateIsBlockedStatusOfUser.setInt(1, userId);
			updateIsBlockedStatusOfUser.executeUpdate();
			return true;
		} catch (SQLException e) {
			MainCentralizedResource.LOGGER.severe(e.toString());
			return false;
		}
	}
	
	
    /**
     * This method will update the is_blocked column to true
     * update users set is_blocked=true where user_id =?
     */
    @Override
	public boolean blockUser(int userId) {
		try {
			PreparedStatement updateIsBlockedStatusOfUser = connection_.prepareStatement("update users set is_blocked = 'y' where user_id = ?");
			updateIsBlockedStatusOfUser.setInt(1, userId);
			updateIsBlockedStatusOfUser.executeUpdate();
			return true;
		} catch (SQLException e) {
			MainCentralizedResource.LOGGER.severe(e.toString());
			return false;
		}
	}


}
