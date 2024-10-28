package com.bharath.dao;


import com.bharath.model.Post;
import com.bharath.MainCentralizedResource;
import com.bharath.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostDaoImpl implements PostDao {
    private static PostDaoImpl postDao_ = null;
	private Connection connection_ = null;

    private PostDaoImpl(){
    	connection_ = DBConnection.getConnection();
    }

    /**
     * This method is used to get the instance of the PostDeo class
     * @return
     */
    public static PostDaoImpl getInstance(){
        if(postDao_ == null){
            postDao_ = new PostDaoImpl();
        }
        return postDao_;
    }
    
    /**
     * This method gives all the post of the specified user
     * select * from post where user_id = ?
     * @param userId - specified user id
     */
    @Override
    public List<Post> getAllMyPost(int userId){
		List<Post> myAllPosts = new ArrayList<>();
		try {
			PreparedStatement selectQueryToFetchAllMyPost = connection_.prepareStatement("select * from post where user_id = ?");
			selectQueryToFetchAllMyPost.setInt(1, userId);
			ResultSet resultSet = selectQueryToFetchAllMyPost.executeQuery();
			while(resultSet.next()){
				Post post = new Post();
				post.setPostId(resultSet.getInt("post_id"));
				post.setUserId(resultSet.getInt("user_id"));
				post.setPostedDate(resultSet.getDate("posted_date").toLocalDate());
				post.setPostedTime(resultSet.getTime("posted_time").toLocalTime());
				post.setPostContent(resultSet.getString("post_content"));
				myAllPosts.add(post);
			}

			if(myAllPosts.isEmpty()){
				MainCentralizedResource.LOGGER.warning(String.format("User %s have not posted any post", userId));
				return null;
			}
			return myAllPosts;
		} catch (SQLException e) {
			MainCentralizedResource.LOGGER.severe(e.toString());
			return null;
		}
    }
    
    /**
     * This method is used to check whether the post exists in the database
     * select post_id from post where post_id = ?
     * @param postId
     * @return
     */
    @Override
    public boolean isPostExists(int postId){
		try {
			PreparedStatement selectQueryToCheckPostExits = connection_.prepareStatement("select post_id from post where post_id = ?");
			selectQueryToCheckPostExits.setInt(1, postId);
			ResultSet resultSet = selectQueryToCheckPostExits.executeQuery();
			return resultSet.next();
		} catch (SQLException e) {
			MainCentralizedResource.LOGGER.severe(e.toString());
			return false;
		}
    }



    /**
     * This method is used to get all the post by the specific post
     * select * from post where post_id = ?
     * @param postId
     * @return
     */
    @Override
    public Post getPost(int postId){
		try {
			PreparedStatement selectQueryToCheckPostExits = connection_.prepareStatement("select * from post where post_id = ?");
			selectQueryToCheckPostExits.setInt(1, postId);
			ResultSet resultSet = selectQueryToCheckPostExits.executeQuery();
			resultSet.next();
			Post post = new Post(postId, resultSet.getInt("user_id"), resultSet.getDate("posted_date").toLocalDate(), resultSet.getTime("posted_time").toLocalTime(), resultSet.getString("post_content"));
			return post;
		} catch (SQLException e) {
			MainCentralizedResource.LOGGER.severe(e.toString());
			return null;
		}
    }

    /**
     * This method is used to delete the post from the database
     * delete from post where post_id = ?
     * @param postId
     * @return
     */
    @Override
    public boolean removePost(int postId){
		try {
			PreparedStatement removePostByPostId = connection_.prepareStatement("delete from post where post_id = ?");
			removePostByPostId.setInt(1, postId);
			removePostByPostId.execute();
			MainCentralizedResource.LOGGER.info("Successfully deleted the post!");
			return true;
		} catch (SQLException e) {
			MainCentralizedResource.LOGGER.severe(e.toString());
			return false;
		}
    }
    
    
    /**
     * This method is used to insert the post into the database
     * INSERT INTO post (post_id, user_id, posted_date, posted_time, post_content) VALUES (nextval('post_id_sequence'), ?, CURRENT_DATE, CURRENT_TIME, ?)
     * @param postContent
     * @param userId
     * @return
     */
    @Override
    public boolean createPost(String postContent, int userId){
		try {
			PreparedStatement createNewPostStatement = connection_.prepareStatement("INSERT INTO post (post_id, user_id, posted_date, posted_time, post_content) VALUES (nextval('post_id_sequence'), ?, CURRENT_DATE, CURRENT_TIME, ?)");
			createNewPostStatement.setInt(1, userId);
			createNewPostStatement.setString(2, postContent);
			createNewPostStatement.execute();
			MainCentralizedResource.LOGGER.info("Successfully posted the message.");
			return true;
		} catch (SQLException e) {
			MainCentralizedResource.LOGGER.severe(e.toString());
			return false;
		}
    }

    /**
     * This method returns all post in the social media
     */
	@Override
	public List<Post> getAllPostInSocialMedia() {
		List<Post> allPost = new ArrayList<>();
		try {
			PreparedStatement selectQueryToFetchAllMyPost = connection_.prepareStatement("select * from post");
			ResultSet resultSet = selectQueryToFetchAllMyPost.executeQuery();
			while(resultSet.next()){
				Post post = new Post(resultSet.getInt("post_id"), resultSet.getInt("user_id"), resultSet.getDate("posted_date").toLocalDate(), resultSet.getTime("posted_time").toLocalTime(), resultSet.getString("post_content"));
				allPost.add(post);
			}

			if(allPost.isEmpty()){
				MainCentralizedResource.LOGGER.warning("No user have posted any post");
				return null;
			}
			return allPost;
		} catch (SQLException e) {
			MainCentralizedResource.LOGGER.severe(e.toString());
			return null;
		}
	}




}
