package com.bharath.dao;


import com.bharath.model.Comment;
import com.bharath.MainCentralizedResource;
import com.bharath.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentDaoImpl implements CommentDao {
    private static CommentDaoImpl commentDao_ = null;
	private Connection connection_ = null;
	
    private CommentDaoImpl(){
		connection_ = DBConnection.getConnection();
    }

    /**
     * This method returns the instance of the CommentDeo class
     * @return
     */
    public static CommentDaoImpl getInstance(){
        if(commentDao_ == null){
            commentDao_ = new CommentDaoImpl();
        }
        return commentDao_;
    }
    
    /**
     * This method is used to get all the comments for the specific post
     * select * from comment where post_id = ?
     * @param postId
     * @return
     */
    @Override
    public List<Comment> getAllCommentsForThePost(int postId){
		List<Comment> commentList = new ArrayList<>();
		try {
			PreparedStatement selectQueryForFetchingAllCommentsForThePost = connection_.prepareStatement("select * from comment where post_id = ?");
			selectQueryForFetchingAllCommentsForThePost.setInt(1, postId);
			ResultSet resultSet = selectQueryForFetchingAllCommentsForThePost.executeQuery();
			while(resultSet.next()){
				Comment comment = new Comment();
				comment.setCommentId(resultSet.getInt("comment_id"));
				comment.setCommentDate(resultSet.getDate("comment_date").toLocalDate());
				comment.setCommentTime(resultSet.getTime("comment_time").toLocalTime());
				comment.setCommentUserId(resultSet.getInt("comment_user_id"));
				comment.setPostId(resultSet.getInt("post_id"));
				comment.setCommentText(resultSet.getString("comment_text"));
				commentList.add(comment);
			}

			if(commentList.isEmpty()){
				MainCentralizedResource.LOGGER.warning("There was no comments for the post with post id: " + postId);
				return null;
			}

			return commentList;
		} catch (SQLException e) {
			MainCentralizedResource.LOGGER.severe(e.toString());
			return null;
		}
    }
    
    /**
     * This method is used to insert record to the DB
     * INSERT INTO comment (comment_id, comment_date, comment_time, comment_user_id, post_id, comment_text) VALUES (nextval('comment_id_sequence'), CURRENT_DATE, CURRENT_TIME, ?, ?, ?)
     * @param userId
     * @param postId
     * @param commentText
     * @return
     */
    @Override
    public boolean commentThePost(int userId, int postId, String commentText){
		try {
			PreparedStatement insertQueryForCommentingThePost = connection_.prepareStatement("INSERT INTO comment (comment_id, comment_date, comment_time, comment_user_id, post_id, comment_text) VALUES (nextval('comment_id_sequence'), CURRENT_DATE, CURRENT_TIME, ?, ?, ?)");
			insertQueryForCommentingThePost.setInt(1, userId);
			insertQueryForCommentingThePost.setInt(2, postId);
			insertQueryForCommentingThePost.setString(3, commentText);
			insertQueryForCommentingThePost.execute();
			MainCentralizedResource.LOGGER.info("Successfully added the comment to the post id: " + postId);
			return true;
		} catch (SQLException e) {
			MainCentralizedResource.LOGGER.severe(e.toString());
			return false;
		}
    }
    
    /**
     * This method is used to delete all the comments for the specific post
     * delete from comment where post_id = ?
     * @param postId
     * @return
     */
    @Override
    public boolean deleteAllCommentsForThePost(int postId){
		try {
			PreparedStatement deleteQueryForSpecificPost = connection_.prepareStatement("delete from comment where post_id = ?");
			deleteQueryForSpecificPost.setInt(1, postId);
			deleteQueryForSpecificPost.execute();
			MainCentralizedResource.LOGGER.info("Comments deleted Successfully!.");
			return true;
		} catch (SQLException e) {
			MainCentralizedResource.LOGGER.severe(e.toString());
			return false;
		}
    }

    /**
     * This method is used to delete all comments for the specific user
     * delete from comment where comment_user_id = ?
     * @param userId
     * @return
     */
    @Override
    public boolean deleteAllCommentsForTheUser(int userId){
		try {
			PreparedStatement deleteQueryToRemoveComment = connection_.prepareStatement("delete from comment where comment_user_id = ?");
			deleteQueryToRemoveComment.setInt(1, userId);
			deleteQueryToRemoveComment.execute();
			MainCentralizedResource.LOGGER.info("Successfully deleted the comments for the user");
			return true;
		} catch (SQLException e) {
			MainCentralizedResource.LOGGER.severe(e.toString());
			return false;
		}
    }

}
