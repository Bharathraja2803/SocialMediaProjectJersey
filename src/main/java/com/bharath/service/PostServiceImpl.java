package com.bharath.service;

import java.util.List;

import com.bharath.MainCentralizedResource;
import com.bharath.dao.PostDao;
import com.bharath.dao.PostDaoImpl;
import com.bharath.model.Post;

public class PostServiceImpl implements PostService{
	private static PostServiceImpl postServiceImpl = null;
	private PostDao postDaoImpl = null;
	
	private PostServiceImpl(){
		postDaoImpl = PostDaoImpl.getInstance();
	}
	
	/**
	 * This will provide the instance of PostServiceImpl class to achieve Singletone
	 * @return
	 */
	public static PostServiceImpl getInstance(){
		if(postServiceImpl == null){
			postServiceImpl = new PostServiceImpl();
		}
		return postServiceImpl;
	}
	
	/**
	 * This method is used to get all the post of the specified user
	 */
	@Override
	public List<Post> getAllUserPost(int userId){
		List<Post> allMyPostPerUser = postDaoImpl.getAllMyPost(userId);
		if(allMyPostPerUser == null){
            MainCentralizedResource.LOGGER.warning("Something went wrong in fetching all post of user id: " + userId);
        }
		return allMyPostPerUser;
	}

	/**
	 * This method is used to get post of specified post id
	 */
	@Override
	public Post getPostByPostId(int postId) {
		boolean isPostExistsForPostId = postDaoImpl.isPostExists(postId);
		
		if(!isPostExistsForPostId){
			MainCentralizedResource.LOGGER.warning("Invalid post id: " + postId);
			return null;
		}
		
		Post post = postDaoImpl.getPost(postId);
		if(post == null){
			MainCentralizedResource.LOGGER.warning("Something went wrong in fetching the post by post id: " + postId);
		}else{
			MainCentralizedResource.LOGGER.info("Post details fetched successfully from the post id: " + postId);
		}
		return post;
	}

	/**
	 * This method is used to removed post of specified post id
	 */
	@Override
	public boolean removePost(int postId) {
		Post post = this.getPostByPostId(postId);
		
		if(post == null){
			MainCentralizedResource.LOGGER.warning("Post with post id "+postId+" does not exists");
			return true;
		}
		
		boolean isPostDeleted = postDaoImpl.removePost(postId);
		
		if(isPostDeleted){
			MainCentralizedResource.LOGGER.info("Post with post id: "+postId+" deleted successfully");
		}else{
			MainCentralizedResource.LOGGER.warning("Something went wrong in deleting the post with post id: "+postId);
		}
		
		return isPostDeleted;
	}

	/**
	 * This method is used to validate the post content while adding the post
	 */
	@Override
	public boolean isValidPostContent(String postContent) {
		if(postContent == null || postContent.trim().isEmpty()){
			MainCentralizedResource.LOGGER.warning("The post content \""+postContent+"\" is invalid");
			return false;
		}
		
		MainCentralizedResource.LOGGER.warning("The post content \""+postContent+"\" is valid");
		return true;
	}

	/**
	 * This method is used to create the post by a specified user
	 */
	@Override
	public boolean createPost(String postContent, int userId) {
		boolean isPostCreationSuccessful = postDaoImpl.createPost(postContent, userId);
		if(isPostCreationSuccessful){
			MainCentralizedResource.LOGGER.info("User with user id "+userId+" successfully posted the post content : "+postContent);
		}else{
			MainCentralizedResource.LOGGER.warning("Something went wrong when User with user id "+userId+" posted the post content : "+postContent);
		}
		return isPostCreationSuccessful;
	}

	@Override
	public List<Post> getAllPostInSocialMedia() {
		List<Post> allPost = postDaoImpl.getAllPostInSocialMedia();
		if(allPost == null){
			MainCentralizedResource.LOGGER.warning("No user have posted any post");
		}else{
			MainCentralizedResource.LOGGER.info("Successfully retrieved all the posts in social media");
		}
		return allPost;
	}
	
}
