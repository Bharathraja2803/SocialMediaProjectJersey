package com.bharath.dao;


import com.bharath.model.Like;
import com.bharath.MainCentralizedResource;
import com.bharath.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LikeDaoImpl implements LikeDao {
    private static LikeDaoImpl likeDao_ = null;
	private Connection connection_ = null;

    private LikeDaoImpl(){
    	connection_ = DBConnection.getConnection();
    }
    
    /**
     * This method is used to get the instance of LikeDao
     * @return
     */
    public static LikeDaoImpl getInstance(){
        if(likeDao_ == null){
            likeDao_ = new LikeDaoImpl();
        }
        return likeDao_;
    }

    /**
     * This method is used to get the like object by userId and postId
     * select * from likes where user_id = ? and post_id = ?
     * @param userId
     * @param postId
     * @return
     */
    @Override
    public Like getLike(int userId, int postId){
		try {
			PreparedStatement selectQueryForLikeId = connection_.prepareStatement("select * from likes where user_id = ? and post_id = ?");
			selectQueryForLikeId.setInt(1, userId);
			selectQueryForLikeId.setInt(2, postId);
			ResultSet resultSet = selectQueryForLikeId.executeQuery();
			if(!resultSet.next()){
				return null;
			}
			Like like = new Like();
			like.setUserId(userId);
			like.setPostId(postId);
			like.setLikeDate(resultSet.getDate("like_date").toLocalDate());
			like.setLikeTime(resultSet.getTime("like_time").toLocalTime());
			like.setLikeId(resultSet.getInt("like_id"));
			return like;
		} catch (SQLException e) {
			MainCentralizedResource.LOGGER.severe(e.toString());
			return null;
		}
    }
    
    /**
     * This method is used to fetch all the likes for the post specified
     * select * from likes where post_id = ?
     * @param postId
     * @return
     */
    @Override
    public List<Like> getAllLikesForThePost(int postId){
		List<Like> likeList = new ArrayList<>();
		try {
			PreparedStatement selectQueryToFetchAllLikesForThePost = connection_.prepareStatement("select * from likes where post_id = ?");
			selectQueryToFetchAllLikesForThePost.setInt(1, postId);
			ResultSet resultSet = selectQueryToFetchAllLikesForThePost.executeQuery();
			while(resultSet.next()){
				Like like = new Like();
				like.setLikeId(resultSet.getInt("like_id"));
				like.setUserId(resultSet.getInt("user_id"));
				like.setPostId(resultSet.getInt("post_id"));
				like.setLikeDate(resultSet.getDate("like_date").toLocalDate());
				like.setLikeTime(resultSet.getTime("like_time").toLocalTime());
				likeList.add(like);
			}

			if(likeList.isEmpty()){
				MainCentralizedResource.LOGGER.warning("No likes received for the post with post id: " + postId);
				return null;
			}
			MainCentralizedResource.LOGGER.info("Fetched all likes for the post with post id: " + postId);
			return likeList;
		} catch (SQLException e) {
			MainCentralizedResource.LOGGER.severe(e.toString());
			return null;
		}
    }
    
    /**
     * This method is used to insert the like record into the database
     * INSERT INTO likes (like_id, user_id, post_id, like_date, like_time) VALUES (nextVal('like_id_sequence'), ?, ?, CURRENT_DATE, CURRENT_TIME)
     * @param userId
     * @param postId
     * @return
     */
    @Override
    public boolean likeThePost(int userId, int postId){
		try {
			PreparedStatement addLikeEntryToPost = connection_.prepareStatement("INSERT INTO likes (like_id, user_id, post_id, like_date, like_time) VALUES (nextVal('like_id_sequence'), ?, ?, CURRENT_DATE, CURRENT_TIME)");
			addLikeEntryToPost.setInt(1, userId);
			addLikeEntryToPost.setInt(2, postId);
			addLikeEntryToPost.execute();
			MainCentralizedResource.LOGGER.info("You have liked the record");
			return true;
		} catch (SQLException e) {
			MainCentralizedResource.LOGGER.severe(e.toString());
			return false;
		}
    }



    /**
     * This method is used to delete the like record from the database
     * delete from likes where like_id = ?
     * @param likeId
     * @return
     */
    @Override
    public boolean removeLike(int likeId){
	try{
		PreparedStatement removeLikeQuery = connection_.prepareStatement("delete from likes where like_id = ?");
		removeLikeQuery.setInt(1, likeId);
		removeLikeQuery.execute();
		MainCentralizedResource.LOGGER.info(String.format("The like id %d is removed for the post: ", likeId));
		return true;
	} catch (SQLException e) {
		MainCentralizedResource.LOGGER.severe(e.toString());
		return false;
	}

}
    
    /**
     * This method is used to remove all the like record for the specific post
     * delete from likes where post_id = ?
     * @param postId
     * @return
     */
    @Override
    public boolean removeAllLikeForSpecificPost(int postId){
		try {
			PreparedStatement removeAllLikedByPostId = connection_.prepareStatement("delete from likes where post_id = ?");
			removeAllLikedByPostId.setInt(1, postId);
			removeAllLikedByPostId.execute();
			MainCentralizedResource.LOGGER.info("All liked were removed for the post");
			return true;
		} catch (SQLException e) {
			MainCentralizedResource.LOGGER.warning(e.toString());
			return false;
		}
    }

    /**
     * This method is used to remove all the like record for the specific user
     * delete from likes where user_id = ?
     * @param userId
     * @return
     */
    @Override
    public boolean removeAllLikesByUserId(int userId){
		try {
			PreparedStatement deleteQueryToRemoveLikesByUser = connection_.prepareStatement("delete from likes where user_id = ?");
			deleteQueryToRemoveLikesByUser.setInt(1, userId);
			deleteQueryToRemoveLikesByUser.execute();
			MainCentralizedResource.LOGGER.info("Successfully deleted the likes using userid");
			return true;
		} catch (SQLException e) {
			MainCentralizedResource.LOGGER.severe(e.toString());
			return false;
		}
    }

	@Override
	public int countOfLikesPerPost(int postId) {
		try {
			PreparedStatement selectQueryForLikeId = connection_.prepareStatement("select count(*) from likes where post_id = ?");
			selectQueryForLikeId.setInt(1, postId);
			ResultSet resultSet = selectQueryForLikeId.executeQuery();
			if(!resultSet.next()){
				return 0;
			}
			return resultSet.getInt(1);
		} catch (SQLException e) {
			MainCentralizedResource.LOGGER.severe(e.toString());
			return 0;
		}
		
	}

	@Override
	public Like getLikeByLikeId(int likeId) {
		try {
			PreparedStatement selectQueryForLikeId = connection_.prepareStatement("select * from likes where like_id = ?");
			selectQueryForLikeId.setInt(1, likeId);
			ResultSet resultSet = selectQueryForLikeId.executeQuery();
			if(!resultSet.next()){
				return null;
			}
			Like like = new Like(resultSet.getInt("like_id"), resultSet.getInt("user_id"), resultSet.getInt("post_id"), resultSet.getDate("like_date").toLocalDate(), resultSet.getTime("like_time").toLocalTime());
			return like;
		} catch (SQLException e) {
			MainCentralizedResource.LOGGER.severe(e.toString());
			return null;
		}
	}

}
