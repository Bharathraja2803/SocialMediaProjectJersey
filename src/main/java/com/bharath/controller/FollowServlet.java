package com.bharath.controller;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bharath.model.Users;
import com.bharath.MainCentralizedResource;

/**
 * This servlet is used to add the following entry to the table
 * @author bharath-22329
 *
 */
public class FollowServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		if(session.getAttribute("loggedinUser") == null){
			session.setAttribute("isNotLoggedIn", "y");
			response.sendRedirect("index.jsp");
			return;
		}
		
		int userToFollow = -1;
		String pageId = request.getParameter("pageId");
		try{
			userToFollow = Integer.parseInt(request.getParameter("userId"));
			
		}catch(NumberFormatException e){
			MainCentralizedResource.LOGGER.severe(e.toString());
			session.setAttribute("somethingWentWrong", "y");
			if(pageId.equals("1")){
				response.sendRedirect("viewAllFollowing.jsp");
			}else{
				response.sendRedirect("addFollowing.jsp");
			}
			return;
		}
		
		Users users = (Users) session.getAttribute("loggedinUser");
		
		String apiUrl = "http://localhost:8080/BharathSocialMediaAppRestFullWebServ/webapi/user/"+users.getUserId_()+"/follower/"+userToFollow; 
        HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
        conn.setRequestMethod("POST");
        
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
        	session.setAttribute("isFollowingTheUserCompleted", "y");
    		if(pageId.equals("1")){
    			response.sendRedirect("viewAllFollowing.jsp");
    		}else{
    			response.sendRedirect("addFollowing.jsp");
    		}
        } else {
        	session.setAttribute("somethingWentWrong", "y");
			if(pageId.equals("1")){
				response.sendRedirect("viewAllFollowing.jsp");
			}else{
				response.sendRedirect("addFollowing.jsp");
			}
			
        }
        conn.disconnect();
	}
	
	
}
