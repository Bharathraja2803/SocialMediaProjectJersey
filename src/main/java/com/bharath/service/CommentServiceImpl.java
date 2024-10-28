package com.bharath.service;

import java.util.List;

import com.bharath.MainCentralizedResource;
import com.bharath.dao.CommentDao;
import com.bharath.dao.CommentDaoImpl;
import com.bharath.model.Comment;

public class CommentServiceImpl implements CommentService{
	private static CommentServiceImpl commentServiceImpl = null;
	private CommentDao commentDaoImpl = null;
	
	private CommentServiceImpl(){
		commentDaoImpl = CommentDaoImpl.getInstance();
	}
	
	/**
	 * This will provide the instance of CommentServiceImpl class to achieve Singletone 
	 * @return
	 */
	public static CommentServiceImpl getInstance(){
		if(commentServiceImpl == null){
			commentServiceImpl = new CommentServiceImpl();
		}
		return commentServiceImpl;
	}

	/**
	 * This will fetch all comments for the specified post
	 */
	@Override
	public List<Comment> getAllCommentsForThePost(int postId) {
		List<Comment> commentList = commentDaoImpl.getAllCommentsForThePost(postId);
		if(commentList == null){
			MainCentralizedResource.LOGGER.warning("No comments for the post with post id: "+postId);
		}else{
			MainCentralizedResource.LOGGER.warning("All comments fetched for the post with post id: "+postId+" Successfully");
		}
		return commentList;
	}

	/**
	 * This will add new comment to the specified post by the specified user
	 */
	@Override
	public boolean commentThePost(int userId, int postId, String comment) {
		boolean isCommentSuccessFul = commentDaoImpl.commentThePost(userId, postId, comment);
		if(isCommentSuccessFul){
			MainCentralizedResource.LOGGER.info("Commenting the post id "+postId+" by user id "+ userId+" with comment : "+comment+" is successfull");
		}else{
			MainCentralizedResource.LOGGER.info("Commenting the post id "+postId+" by user id "+ userId+" with comment : "+comment+" is failed");
		}
		return isCommentSuccessFul;
	}

	/**
	 * This method will delete all comments for the specified post
	 */
	@Override
	public boolean deleteAllCommentsForThePost(int postId) {
		List<Comment> commentsList = this.getAllCommentsForThePost(postId);
		
		if(commentsList == null){
			MainCentralizedResource.LOGGER.warning("There is no comments to delete for post id: " + postId);
			return true;
		}
		
		boolean isCommentsDeletedForThePost = commentDaoImpl.deleteAllCommentsForThePost(postId);
		
		if(isCommentsDeletedForThePost){
			MainCentralizedResource.LOGGER.info("All comments for the post with post id "+ postId +" deleted ");
		}else{
			MainCentralizedResource.LOGGER.info("Something went wrong in deleting the comments for the post with post id "+ postId);
		}
		
		return isCommentsDeletedForThePost;
	}

	/**
	 * This method will delete all comments entered by the specified user on all post
	 */
	@Override
	public boolean deleteAllCommentsForTheUser(int userId) {
		UsersService usersServiceImpl = UsersServiceImpl.getInstance();
		boolean isUserIdValid = usersServiceImpl.isValidUserId(userId);
		
		if(!isUserIdValid){
			MainCentralizedResource.LOGGER.warning("User id "+userId+" is invalid");
			return false;
		}
		
		boolean isAllCommentsOfUserDeleted = commentDaoImpl.deleteAllCommentsForTheUser(userId);
		
		if(isAllCommentsOfUserDeleted){
			MainCentralizedResource.LOGGER.info("All comments by the user "+userId+" was deleted");
		}else{
			MainCentralizedResource.LOGGER.info("Failed in deleting all comments by the user "+userId);
		}
		
		return isAllCommentsOfUserDeleted;
	}
}
