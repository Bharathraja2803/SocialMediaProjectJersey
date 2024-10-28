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
 * This servlet is used to unfollow the user
 * @author bharath-22329
 *
 */
public class UnfollowServlet extends HttpServlet {
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
		
		int userIdToUnfollow = -1;
		String pageId = request.getParameter("pageId");
		try{
			userIdToUnfollow = Integer.parseInt(request.getParameter("userId"));
			
		}catch(NumberFormatException e){
			MainCentralizedResource.LOGGER.severe(e.toString());
			session.setAttribute("somethingWentWrong", "y");
			if(pageId.equals("1")){
				response.sendRedirect("viewAllFollowers.jsp");
			}else{
				response.sendRedirect("viewAllFollowing.jsp");
			}
			return;
		}
		
		Users users = (Users) session.getAttribute("loggedinUser");
		
		String apiUrl = "http://localhost:8080/BharathSocialMediaAppRestFullWebServ/webapi/user/"+users.getUserId_()+"/follower/"+userIdToUnfollow; 
        HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
        conn.setRequestMethod("DELETE");
        
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
        	session.setAttribute("isUnfollowSuccessful", "y");
    		if(pageId.equals("1")){
    			response.sendRedirect("viewAllFollowers.jsp");
    		}else{
    			response.sendRedirect("viewAllFollowing.jsp");
    		}
        } else {
			session.setAttribute("somethingWentWrong", "y");
			if(pageId.equals("1")){
				response.sendRedirect("viewAllFollowers.jsp");
			}else{
				response.sendRedirect("viewAllFollowing.jsp");
			}
			
        }
        conn.disconnect();
	}
}
