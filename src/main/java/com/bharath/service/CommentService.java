package com.bharath.service;

import java.util.List;

import com.bharath.model.Comment;

public interface CommentService {
	List<Comment> getAllCommentsForThePost(int postId);
	boolean commentThePost(int userId, int postId, String comment);
	boolean deleteAllCommentsForThePost(int postId);
	boolean deleteAllCommentsForTheUser(int userId);
}
