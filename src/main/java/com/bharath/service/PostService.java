package com.bharath.service;

import java.util.List;

import com.bharath.model.Post;

public interface PostService {
	List<Post> getAllUserPost(int userId);
	Post getPostByPostId(int postId);
	boolean removePost(int postId);
	boolean isValidPostContent(String postContent);
	boolean createPost(String postContent, int userId);
	List<Post> getAllPostInSocialMedia();
}
