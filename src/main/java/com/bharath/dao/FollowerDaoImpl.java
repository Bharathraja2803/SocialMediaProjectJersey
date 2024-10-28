package com.bharath.dao;


import com.bharath.model.Users;
import com.bharath.MainCentralizedResource;
import com.bharath.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FollowerDaoImpl implements FollowerDao {
    private static FollowerDaoImpl followerDao_ = null;
	private Connection connection_ = null;
    
    private FollowerDaoImpl(){
    	connection_ = DBConnection.getConnection();
    }
    
    /**
     * This method will return the instance of the FollowerDeo
     * @return
     */
    public static FollowerDaoImpl getInstance(){
        if(followerDao_ == null){
            followerDao_ = new FollowerDaoImpl();
        }
        return followerDao_;
    }

    /**
     * This method is used to list all the followed users by the userId
     * select * from users where user_id in (select following_user_id from follower where user_id = ?
     * @param userId
     * @return
     */
    @Override
    public List<Users> listAllFollowedUsers(int userId){
		List<Users> usersList = new ArrayList<>();
		try {
			PreparedStatement selectQueryForAllFollowerIdByUserId = connection_.prepareStatement("select * from users where user_id in (select following_user_id from follower where user_id = ?)");
			selectQueryForAllFollowerIdByUserId.setInt(1, userId);
			ResultSet resultSet = selectQueryForAllFollowerIdByUserId.executeQuery();
			while(resultSet.next()){
				Users users = new Users();
				users.setUserId_(resultSet.getInt("user_id"));
				users.setUserName_(resultSet.getString("user_name"));
				users.setPassword_(resultSet.getString("password"));
				users.setBirthday_(resultSet.getString("birthdate"));
				users.setEmailId_(resultSet.getString("email_id"));
				users.setSignupDate_(resultSet.getDate("signup_date").toLocalDate());
				users.setSignupTime_(resultSet.getTime("signup_time").toLocalTime());
				users.setBlocked(resultSet.getString("is_blocked").charAt(0));
				users.setRoles_(resultSet.getString("roles"));
				usersList.add(users);
			}
		} catch (SQLException e) {
			MainCentralizedResource.LOGGER.severe(e.toString());
			return null;
		}

		if(usersList.isEmpty()){
			MainCentralizedResource.LOGGER.warning("There were no users you are following");
			return null;
		}

		MainCentralizedResource.LOGGER.info("Successfully retrieved the following users list");
		return usersList;
    }
    
    /**
     * This method is used to list all the user following you
     * select * from users where user_id in (select user_id from follower where following_user_id = ?)
     * @param userId
     * @return
     */
    @Override
    public List<Users> listOfAllUsersFollowingYou(int userId){
		List<Users> usersList = new ArrayList<>();
		try {
			PreparedStatement selectQueryForAllFollowerIdByUserId = connection_.prepareStatement("select * from users where user_id in (select user_id from follower where following_user_id = ?)");
			selectQueryForAllFollowerIdByUserId.setInt(1, userId);
			ResultSet resultSet = selectQueryForAllFollowerIdByUserId.executeQuery();
			while(resultSet.next()){
				Users users = new Users();
				users.setUserId_(resultSet.getInt("user_id"));
				users.setUserName_(resultSet.getString("user_name"));
				users.setPassword_(resultSet.getString("password"));
				users.setBirthday_(resultSet.getString("birthdate"));
				users.setEmailId_(resultSet.getString("email_id"));
				users.setSignupDate_(resultSet.getDate("signup_date").toLocalDate());
				users.setSignupTime_(resultSet.getTime("signup_time").toLocalTime());
				users.setBlocked(resultSet.getString("is_blocked").charAt(0));
				users.setRoles_(resultSet.getString("roles"));
				usersList.add(users);
			}
		} catch (SQLException e) {
			MainCentralizedResource.LOGGER.severe(e.toString());
			return null;
		}

		if(usersList.isEmpty()){
			MainCentralizedResource.LOGGER.warning("There were no followers following you");
			return null;
		}

		MainCentralizedResource.LOGGER.info("Successfully retrieved the followers list");
		return usersList;
    }
    
    /**
     * This method is used to check the user is following the target user id
     * select * from follower where user_id = ? and following_user_id = ?
     * @param userId
     * @param followerId
     * @return
     */
    @Override
    public boolean isFollowing(int userId, int followerId){
		try {
			PreparedStatement selectQueryForUser = connection_.prepareStatement("select * from follower where user_id = ? and following_user_id = ?");
			selectQueryForUser.setInt(1, userId);
			selectQueryForUser.setInt(2, followerId);
			ResultSet resultSet = selectQueryForUser.executeQuery();
			return resultSet.next();
		} catch (SQLException e) {
			MainCentralizedResource.LOGGER.severe(e.toString());
			return false;
		}
    }
    
    /**
     * This method is used to delete the record in the follower table
     * delete from follower where user_id = ? and following_user_id = ?
     * @return
     */
    @Override
    public boolean unFollowUser(int userId, int unFollowId){
		try{
			PreparedStatement deleteQueryToUnFollowUser = connection_.prepareStatement("delete from follower where user_id = ? and following_user_id = ?");
			deleteQueryToUnFollowUser.setInt(1, userId);
			deleteQueryToUnFollowUser.setInt(2, unFollowId);
			deleteQueryToUnFollowUser.execute();
			MainCentralizedResource.LOGGER.info("Successfully removed the record from the follower table");
			return true;
		}catch(SQLException e){
			MainCentralizedResource.LOGGER.severe(e.toString());
			return false;
		}
    }
    
    /**
     * This method is used to insert the record in the DB
     * INSERT INTO follower (user_id, following_user_id) VALUES (?, ?)
     * @param userId
     * @param followerId
     * @return
     */
    @Override
    public boolean followUser(int userId, int followerId){
		try {
			PreparedStatement insertQueryToAddFollowers = connection_.prepareStatement("INSERT INTO follower (user_id, following_user_id) VALUES (?, ?)");
			insertQueryToAddFollowers.setInt(1, userId);
			insertQueryToAddFollowers.setInt(2, followerId);
			insertQueryToAddFollowers.execute();
			MainCentralizedResource.LOGGER.info("Added the record to the follower table");
			return true;
		} catch (SQLException e) {
			MainCentralizedResource.LOGGER.severe(e.toString());
			return false;
		}
    }

	@Override
	public int countOfUsersWeAreFollowing(int userId) {
		try{
			PreparedStatement getCountOfAllFollowersQuery = connection_.prepareStatement("select count(*) from follower where user_id = ?");
			getCountOfAllFollowersQuery.setInt(1, userId);
			ResultSet resultSet = getCountOfAllFollowersQuery.executeQuery();
			if(!resultSet.next()){
				return -1;
			}
			
			return resultSet.getInt(1);
		}catch(SQLException e){
			MainCentralizedResource.LOGGER.severe(e.toString());
			return -1;
		}
	}
	
	@Override
	public int countOfUsersWhoAreFollowingUs(int userId) {
		try{
			PreparedStatement getCountOfAllFollowersQuery = connection_.prepareStatement("select count(*) from follower where following_user_id = ?");
			getCountOfAllFollowersQuery.setInt(1, userId);
			ResultSet resultSet = getCountOfAllFollowersQuery.executeQuery();
			if(!resultSet.next()){
				return -1;
			}
			
			return resultSet.getInt(1);
		}catch(SQLException e){
			MainCentralizedResource.LOGGER.severe(e.toString());
			return -1;
		}
	}

}
