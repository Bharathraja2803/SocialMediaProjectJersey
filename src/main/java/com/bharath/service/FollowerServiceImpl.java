package com.bharath.service;

import java.util.List;

import com.bharath.MainCentralizedResource;
import com.bharath.dao.FollowerDao;
import com.bharath.dao.FollowerDaoImpl;
import com.bharath.model.Users;

public class FollowerServiceImpl implements FollowerService{
	private static FollowerServiceImpl followerServiceImpl = null;
	private FollowerDao followerDaoImpl = null;
	
	private FollowerServiceImpl() {
		followerDaoImpl = FollowerDaoImpl.getInstance();
	}
	/**
	 * This will provide the instance of FollowerServiceImpl class to achieve Singletone
	 * @return
	 */
	public static FollowerServiceImpl getInstance(){
		if(followerServiceImpl == null){
			followerServiceImpl = new FollowerServiceImpl();
		}
		return followerServiceImpl;
	}
	
	/**
	 * This method will get all the users who the specified user is following
	 */
	@Override
	public List<Users> getAllFollowedUsers(int userId){
		
    	UsersService usersServiceImpl = UsersServiceImpl.getInstance();

    	boolean isUserIdExits = usersServiceImpl.isValidUserId(userId);
    	if(!isUserIdExits){
    		MainCentralizedResource.LOGGER.warning("You're user id is not valid!");
    		return null;
		}
		
		List<Users> listOfAllFollowers = followerDaoImpl.listAllFollowedUsers(userId);
		
		if(listOfAllFollowers == null){
			MainCentralizedResource.LOGGER.warning("Something went wrong in fetching the followers for user id: "+userId);
		}else{
			MainCentralizedResource.LOGGER.info("Successfully fetched the details of people followed by user id: "+ userId);
		}
		
		return listOfAllFollowers;
	}

	/**
	 * This method will get all users who are following the specified user
	 */
	@Override
	public List<Users> getAllUsersFollowingYou(int userId) {
		
    	UsersService usersServiceImpl = UsersServiceImpl.getInstance();

    	boolean isUserIdExits = usersServiceImpl.isValidUserId(userId);
    	if(!isUserIdExits){
    		MainCentralizedResource.LOGGER.warning("You're user id is not valid!");
    		return null;
		}
		
		List<Users> listOfAllUsersFollowingYou = followerDaoImpl.listOfAllUsersFollowingYou(userId);
		if(listOfAllUsersFollowingYou == null){
			MainCentralizedResource.LOGGER.warning("Something went wrong in fetching details of all users following user id: "+ userId);
		}else{
			MainCentralizedResource.LOGGER.info("Successfully fetched the details of all users following user id: "+ userId);
		}
		return listOfAllUsersFollowingYou;
	}

	/**
	 * This method check whether the userId user is following the followingUserId user
	 */
	@Override
	public boolean isFollowing(int userId, int followingUserId) {
		boolean isFollowing = followerDaoImpl.isFollowing(userId, followingUserId);
		if(isFollowing){
			MainCentralizedResource.LOGGER.info("User id "+ userId+" is following user id: "+ followingUserId);
		}else{
			MainCentralizedResource.LOGGER.info("User id "+ userId+" is not following user id: "+ followingUserId);
		}
		return isFollowing;
	}

	/**
	 * This method is used to unfollow the unFollowUserId user by userId user
	 */
	@Override
	public boolean unFollowUser(int userId, int unFollowUserId) {
		
    	UsersService usersServiceImpl = UsersServiceImpl.getInstance();
    	

    	boolean isUserIdExits = usersServiceImpl.isValidUserId(userId);
    	boolean isUnFollowUserIDExists = usersServiceImpl.isValidUserId(unFollowUserId);
    	
    	if(!isUserIdExits){
    		MainCentralizedResource.LOGGER.warning("You're user id "+userId+" is not valid!");
    		return false;
		}
    	
    	if(!isUnFollowUserIDExists){
    		MainCentralizedResource.LOGGER.warning("Unfollow user id "+unFollowUserId+" is not valid!");
    		return false;
		}
    	
    	boolean isUserFollowingUnfollowingUser = this.isFollowing(userId, unFollowUserId);
    	
    	if(!isUserFollowingUnfollowingUser){
    		MainCentralizedResource.LOGGER.warning("User "+userId+" is not following user "+unFollowUserId);
    		return false;
    	}
		
		boolean isUnfollowSuccessfull = followerDaoImpl.unFollowUser(userId, unFollowUserId);
		if(isUnfollowSuccessfull){
			MainCentralizedResource.LOGGER.info("User "+userId+" successfully unfollowed the user "+unFollowUserId);
		}else{
			MainCentralizedResource.LOGGER.warning("User "+userId+" unfollowing the user "+unFollowUserId+" is failed");
		}
		return isUnfollowSuccessfull;
	}

	/**
	 * This method is used to follow userIdToFollow user by userId user 
	 */
	@Override
	public boolean followUser(int userId, int userIdToFollow) {
		
		UsersService usersServiceImpl = UsersServiceImpl.getInstance();

    	boolean isUserIdExits = usersServiceImpl.isValidUserId(userId);
    	boolean isFollowUserIDExists = usersServiceImpl.isValidUserId(userIdToFollow);
    	
    	if(!isUserIdExits){
    		MainCentralizedResource.LOGGER.warning("You're user id "+userId+" is not valid!");
    		return false;
		}
    	
    	if(!isFollowUserIDExists){
    		MainCentralizedResource.LOGGER.warning("Follow user id "+userIdToFollow+" is not valid!");
    		return false;
		}
    	
    	boolean isUserAlreadyFollowingFollowingUser = this.isFollowing(userId, userIdToFollow);
    	
    	if(isUserAlreadyFollowingFollowingUser){
    		MainCentralizedResource.LOGGER.warning("User "+userId+" is already following user "+userIdToFollow);
    		return false;
    	}
		
		boolean isFollowRequestSuccessful = followerDaoImpl.followUser(userId, userIdToFollow);
		
		if(isFollowRequestSuccessful){
			MainCentralizedResource.LOGGER.info("User "+userId+" successfully followed the user "+userIdToFollow);
		}else{
			MainCentralizedResource.LOGGER.warning("User "+userId+" following the user "+userIdToFollow+" is failed");
		}
		
		return isFollowRequestSuccessful;
	}

	/**
	 * This method will provide all the users who the specified user is not following
	 */
	@Override
	public List<Users> getAllNotFollowedUsers(int userId) {
		UsersService usersServiceImpl = UsersServiceImpl.getInstance();
		boolean isUserIdExits = usersServiceImpl.isValidUserId(userId);
		if(!isUserIdExits){
    		MainCentralizedResource.LOGGER.warning("You're user id "+userId+" is not valid!");
    		return null;
		}
		
		List<Users> listOfAllUsersInSocialMedia = usersServiceImpl.getAllUsers();
		
		if(listOfAllUsersInSocialMedia == null){
			MainCentralizedResource.LOGGER.warning("Something went wrong in fetching all users in Social Media Application");
			return null;
		}
		
		listOfAllUsersInSocialMedia.removeIf(u -> u.getUserId_() == userId);
		
		List<Users> listOfAllUsersYouAreFollowing = this.getAllFollowedUsers(userId);
		
		if(listOfAllUsersYouAreFollowing == null){
			MainCentralizedResource.LOGGER.warning("You have followed no one yet");
			return listOfAllUsersInSocialMedia;
		}
		
		for(Users users : listOfAllUsersYouAreFollowing){
			listOfAllUsersInSocialMedia.removeIf(u -> u.getUserId_() == users.getUserId_());
		}
		
		if(listOfAllUsersInSocialMedia.isEmpty()){
			MainCentralizedResource.LOGGER.warning("You have followed all peoples in the social media application");
		}
		
		MainCentralizedResource.LOGGER.info("Successfully fetched all users those you are not following");
		return listOfAllUsersInSocialMedia;
	}
	
	@Override
	public int countOfUsersWeAreFollowing(int userId) {
		UsersService usersServiceImpl = UsersServiceImpl.getInstance();
		boolean isUserIdExits = usersServiceImpl.isValidUserId(userId);
		if(!isUserIdExits){
    		MainCentralizedResource.LOGGER.warning("You're user id "+userId+" is not valid!");
    		return -1;
		}
		
		int count = followerDaoImpl.countOfUsersWeAreFollowing(userId);
		if(count == -1){
			MainCentralizedResource.LOGGER.warning("Something went wrong in fetching the count of all followers");
		}else{
			MainCentralizedResource.LOGGER.info("Successfully fetched the count of all the followers");
		}
		
		return count;
	}
	
	@Override
	public int countOfUsersWhoAreFollowingUs(int userId) {
		UsersService usersServiceImpl = UsersServiceImpl.getInstance();
		boolean isUserIdExits = usersServiceImpl.isValidUserId(userId);
		if(!isUserIdExits){
    		MainCentralizedResource.LOGGER.warning("You're user id "+userId+" is not valid!");
    		return -1;
		}
		
		int count = followerDaoImpl.countOfUsersWhoAreFollowingUs(userId);
		if(count == -1){
			MainCentralizedResource.LOGGER.warning("Something went wrong in fetching the count of all users following us");
		}else{
			MainCentralizedResource.LOGGER.info("Successfully fetched the count of all users following us");
		}
		
		return count;
	}
}
