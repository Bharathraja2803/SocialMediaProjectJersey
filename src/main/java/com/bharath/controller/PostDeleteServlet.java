package com.bharath.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bharath.model.Post;
import com.bharath.model.Users;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.bharath.MainCentralizedResource;

/**
 * This servlet is used to delete the post of the user
 * @author bharath-22329
 *
 */
public class PostDeleteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doDelete(request, response);
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		if(session.getAttribute("loggedinUser") == null){
			session.setAttribute("isNotLoggedIn", "y");
			response.sendRedirect("index.jsp");
			return;
		}
		

		int postId = -1;
		
		try{
			postId = Integer.parseInt(request.getParameter("postId"));
		}catch(NumberFormatException e){
			MainCentralizedResource.LOGGER.severe(e.toString());
			session.setAttribute("somethingWentWrong", "y");
			response.sendRedirect("post.jsp");
			return;
		}
		
		String apiUrlForPost = String.format("http://localhost:8080/BharathSocialMediaAppRestFullWebServ/webapi/user/%d/post/%d", ((Users) session.getAttribute("loggedinUser")).getUserId_(), postId); 
		HttpURLConnection connForPost = (HttpURLConnection) new URL(apiUrlForPost).openConnection();
		connForPost.setRequestMethod("GET");
		
		int responseCodeForPost = connForPost.getResponseCode();
		if (responseCodeForPost == HttpURLConnection.HTTP_OK) {
		    MainCentralizedResource.LOGGER.info("Post retrieved successfully");
		} else {
		    MainCentralizedResource.LOGGER.warning( String.format("Error retrieving post for post id %d : %d", postId, responseCodeForPost));
		}
		
		StringBuilder responseBuilderForPost = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(connForPost.getInputStream(), "utf-8"))) {
		    String responseLine;
		    while ((responseLine = br.readLine()) != null) {
		    	responseBuilderForPost.append(responseLine.trim());
		    }
		}
		
		MainCentralizedResource.LOGGER.info(responseBuilderForPost.toString());
		Post post = null;
		if(!responseBuilderForPost.toString().isEmpty()){
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.registerModule(new JavaTimeModule());
			post = objectMapper.readValue(responseBuilderForPost.toString(), Post.class);
		}
		connForPost.disconnect();
		
		if(post == null){
			session.setAttribute("somethingWentWrong", "y");
			response.sendRedirect("post.jsp");
			return;
		}
		
		
		Users users = (Users)session.getAttribute("loggedinUser");
		if(post.getUserId() == users.getUserId_() || users.getRoles_().equals("admin")){
			
			String apiUrlForCommentsDelete = "http://localhost:8080/BharathSocialMediaAppRestFullWebServ/webapi/user/"+users.getUserId_()+"/post/"+postId+"/comment/deleteCommentByPost"; 
	        HttpURLConnection connForCommentsDelete = (HttpURLConnection) new URL(apiUrlForCommentsDelete).openConnection();
	        connForCommentsDelete.setRequestMethod("DELETE");
	        
	        int responseCodeForCommentsDelete = connForCommentsDelete.getResponseCode();
	        if (!(responseCodeForCommentsDelete == HttpURLConnection.HTTP_OK)) {
	        	session.setAttribute("somethingWentWrong", "y");
				response.sendRedirect("post.jsp");
	            return;
	        }
	        connForCommentsDelete.disconnect();

	        
			String apiUrlForLikesDelete = "http://localhost:8080/BharathSocialMediaAppRestFullWebServ/webapi/user/"+users.getUserId_()+"/post/"+postId+"/like/deleteLikeByPost"; 
	        HttpURLConnection connForLikesDelete = (HttpURLConnection) new URL(apiUrlForLikesDelete).openConnection();
	        connForLikesDelete.setRequestMethod("DELETE");
	        
	        int responseCodeForLikesDelete = connForLikesDelete.getResponseCode();
	        if (!(responseCodeForLikesDelete == HttpURLConnection.HTTP_OK)) {
	        	session.setAttribute("somethingWentWrong", "y");
				response.sendRedirect("post.jsp");
	            return;
	        }
	        connForLikesDelete.disconnect();
	        
			String apiUrlForPostDelete = "http://localhost:8080/BharathSocialMediaAppRestFullWebServ/webapi/user/"+users.getUserId_()+"/post/"+postId; 
	        HttpURLConnection connForPostDelete = (HttpURLConnection) new URL(apiUrlForPostDelete).openConnection();
	        connForPostDelete.setRequestMethod("DELETE");
	        
	        int responseCodeForPostDelete = connForPostDelete.getResponseCode();
	        if (!(responseCodeForPostDelete == HttpURLConnection.HTTP_OK)) {
	        	session.setAttribute("somethingWentWrong", "y");
				response.sendRedirect("post.jsp");
	            return;
	        }
	        connForPostDelete.disconnect();	        
	        
	        
	        session.setAttribute("postDeletedSuccessfully", "y");
	        response.sendRedirect("post.jsp");
		}
	}
	
	

}
