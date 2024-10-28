package com.bharath.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bharath.model.Users;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.bharath.MainCentralizedResource;

/**
 * This servlet is used to add the comment to the existing post
 * @author bharath-22329
 *
 */
public class AddCommentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		if(session.getAttribute("loggedinUser") == null){
			session.setAttribute("isNotLoggedIn", "y");
			response.sendRedirect("index.jsp");
			return;
		}
		
		String comment = request.getParameter("comment");
		
		if(comment == null || comment.trim().isEmpty()){
			MainCentralizedResource.LOGGER.warning("Invalid Comment : "+comment);
	        session.setAttribute("invalidComment", "y");
	        response.sendRedirect("post.jsp");
			return;
		}
		
		int postId = -1;
		try{
			postId = Integer.parseInt(request.getParameter("postid")) ;	
		}catch(NumberFormatException e){
			MainCentralizedResource.LOGGER.warning("Something went wrong in commenting the post!");
	        session.setAttribute("somethingWentWrong", "y");
	        response.sendRedirect("post.jsp");
			return;
		}
		
		Users users = ((Users) session.getAttribute("loggedinUser"));
		
        ObjectMapper objectMapper = new ObjectMapper();
        
        String jsonUser = objectMapper.writeValueAsString(comment);
        
        String apiUrl = "http://localhost:8080/BharathSocialMediaAppRestFullWebServ/webapi/user/"+users.getUserId_()+"/post/"+postId+"/comment"; 
        HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonUser.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        
        int responseCode = conn.getResponseCode();
        if (!(responseCode == HttpURLConnection.HTTP_OK)) {
        	session.setAttribute("somethingWentWrong", "y");
	        response.sendRedirect("post.jsp");
			return;
        }
		
		session.setAttribute("isCommentingThePostSuccessful", "y");
		response.sendRedirect("post.jsp");
	}

}
