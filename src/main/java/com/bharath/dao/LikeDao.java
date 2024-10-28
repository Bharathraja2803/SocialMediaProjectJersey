package com.bharath.dao;

import com.bharath.model.Like;

import java.util.List;

public interface LikeDao {
    boolean likeThePost(int userId, int postId);
    List<Like> getAllLikesForThePost(int postId);
    boolean removeLike(int likeId);
    boolean removeAllLikesByUserId(int userId);
    boolean removeAllLikeForSpecificPost(int postId);
    Like getLike(int userId, int postId);
    int countOfLikesPerPost(int postId);
    Like getLikeByLikeId(int likeId);
}
