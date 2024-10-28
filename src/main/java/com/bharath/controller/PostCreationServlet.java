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
import com.bharath.service.PostService;
import com.bharath.service.PostServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * This servlet is used to create the post of the user
 * @author bharath-22329
 *
 */
public class PostCreationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		if(session.getAttribute("loggedinUser") == null){
			session.setAttribute("isNotLoggedIn", "y");
			response.sendRedirect("index.jsp");
			return;
		}
		
		
		String postContent = request.getParameter("postContent");
		PostService postServiceImpl = PostServiceImpl.getInstance();
		
		boolean isPostContentValid = postServiceImpl.isValidPostContent(postContent);
		
		if(!isPostContentValid){
			session.setAttribute("postContentIsInvalid", "y");
			response.sendRedirect("addpost.jsp");
			return;
		}
		
		Users users = (Users)session.getAttribute("loggedinUser");
		
        ObjectMapper objectMapper = new ObjectMapper();
        
        String jsonUser = objectMapper.writeValueAsString(postContent);
        
        String apiUrl = "http://localhost:8080/BharathSocialMediaAppRestFullWebServ/webapi/user/"+users.getUserId_()+"/post"; 
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
        	session.setAttribute("isProblemInPostAddition", "y");
			response.sendRedirect("addpost.jsp");
			return;
        }
		
		session.setAttribute("isPostCreationSuccessful", "y");
		response.sendRedirect("post.jsp");
	}

}
