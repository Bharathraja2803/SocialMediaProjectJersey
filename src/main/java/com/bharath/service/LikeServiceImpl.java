package com.bharath.service;


import java.util.List;

import com.bharath.MainCentralizedResource;
import com.bharath.dao.LikeDao;
import com.bharath.dao.LikeDaoImpl;
import com.bharath.model.Like;

public class LikeServiceImpl implements LikeService{
	private static LikeServiceImpl likeServiceImpl  = null;
	private LikeDao likeDaoImpl = null;

	private LikeServiceImpl() {
		likeDaoImpl = LikeDaoImpl.getInstance();
	}
	
	/**
	 * This will provide the instance of LikeServiceImpl class to achieve Singletone
	 * @return
	 */
	public static LikeServiceImpl getInstance(){
		if(likeServiceImpl == null){
			likeServiceImpl = new LikeServiceImpl();
		}
		return likeServiceImpl;
	}
	
	/**
	 * This method is used to get the like object using specified userId and specified postId
	 */
	@Override
	public Like getLike(int userId, int postId) {
		Like like = likeDaoImpl.getLike(userId, postId);
		if(like == null){
			MainCentralizedResource.LOGGER.warning("User "+userId+" haven't likes the post "+postId);
		}else{
			MainCentralizedResource.LOGGER.info("User "+userId+" have liked the post "+postId);
		}
		
		return like;
	}

	/**
	 * This method is used to get all the likes for the specified post id
	 */
	@Override
	public List<Like> getAllLikesForThePost(int postId) {
		List<Like> allLikesForThePost = likeDaoImpl.getAllLikesForThePost(postId);
		if(allLikesForThePost == null){
			MainCentralizedResource.LOGGER.warning("No one liked the post with post id"+postId);
		}else{
			MainCentralizedResource.LOGGER.info("Successfully fetched the likes for the post");
		}
		return allLikesForThePost;
	}

	/**
	 * This method is used to like the specified post 
	 */
	@Override
	public boolean likeThePost(int userId, int postId) {
		Like like = this.getLike(userId, postId);
		if(like!=null){
			MainCentralizedResource.LOGGER.warning("User "+userId+" had already likes the post "+postId);
			return false;
		}
		
		boolean isLikeThePostSuccessfull = likeDaoImpl.likeThePost(userId, postId);
		
		if(isLikeThePostSuccessfull){
			MainCentralizedResource.LOGGER.info("User "+userId+" have liked the post "+postId);
		}else{
			MainCentralizedResource.LOGGER.warning("Something went wrong while User "+userId+" liked the post "+postId);
		}
		
		return isLikeThePostSuccessfull;
	}

	/**
	 * This method is used to unlike the specified post
	 */
	@Override
	public boolean unlikeThePost(int likeId) {
		boolean isUnlikeSuccessfull = likeDaoImpl.removeLike(likeId);
		if(isUnlikeSuccessfull){
			MainCentralizedResource.LOGGER.info("Removed the like entry with like id: " + likeId);
		}else{
			MainCentralizedResource.LOGGER.warning("Something went wrong in removing the like id: " + likeId);
		}
		return isUnlikeSuccessfull;
	}

	/**
	 * This method is used to remove all Like for specified post_id
	 */
	@Override
	public boolean removeAllLikeForSpecificPost(int postId) {
		List<Like> likeList = this.getAllLikesForThePost(postId);
		
		if(likeList == null){
			MainCentralizedResource.LOGGER.warning("There is no likes to delete for the post with post id: " + postId);
			return true;
		}
		
		boolean isLikesForThePostDeleted = likeDaoImpl.removeAllLikeForSpecificPost(postId);
		
		if(isLikesForThePostDeleted){
			MainCentralizedResource.LOGGER.info("All likes for the post with post id "+postId+" is deleted");
		}else{
			MainCentralizedResource.LOGGER.warning("Something went wrong in deleting likes for the post with post id "+postId);
		}
		
		return isLikesForThePostDeleted;
	}

	/**
	 * This method is used to remove all likes for all post by a specified user
	 */
	@Override
	public boolean removeAllLikesByUserId(int userId) {
		UsersService usersServiceImpl = UsersServiceImpl.getInstance();
		boolean isValidUser = usersServiceImpl.isValidUserId(userId);
		
		if(!isValidUser){
			MainCentralizedResource.LOGGER.warning("User "+userId+" is not valid");
			return false;
		}
		
		boolean isAllLikesOfTheUserRemoved = likeDaoImpl.removeAllLikesByUserId(userId);
		
		if(isAllLikesOfTheUserRemoved){
			MainCentralizedResource.LOGGER.info("All Likes of the user "+userId+" is removed");
		}else{
			MainCentralizedResource.LOGGER.info("Failure in removal of all Likes of the user "+userId);
		}
		return isAllLikesOfTheUserRemoved;
	}

	@Override
	public int countOfLikesPerPost(int postId) {
		int count = likeDaoImpl.countOfLikesPerPost(postId);
		if(count == 0){
			MainCentralizedResource.LOGGER.info("No likes for the post: "+postId);
		}else{
			MainCentralizedResource.LOGGER.info(count+" likes for the post: "+postId);
		}
		return count;
	}

	
	@Override
	public Like getLikeByLikeId(int likeId) {
		Like like = likeDaoImpl.getLikeByLikeId(likeId);
		if(like == null){
			MainCentralizedResource.LOGGER.warning("Unable to retrieve like for the like id: "+ likeId);
		}else{
			MainCentralizedResource.LOGGER.info("Retrieved Like for like id: "+ likeId);
		}
		
		return like;
	}
	
}
