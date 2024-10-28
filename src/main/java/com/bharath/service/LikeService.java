package com.bharath.service;

import java.util.List;
import com.bharath.model.Like;

public interface LikeService {
	Like getLike(int userId, int postId);
	List<Like> getAllLikesForThePost(int postId);
	boolean likeThePost(int userId, int postId);
	boolean unlikeThePost(int likeId);
	boolean removeAllLikeForSpecificPost(int postId);
	boolean removeAllLikesByUserId(int userId);
	int countOfLikesPerPost(int postId);
	Like getLikeByLikeId(int likeId);
}
