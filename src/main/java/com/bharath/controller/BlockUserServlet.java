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
import com.bharath.service.UsersService;
import com.bharath.service.UsersServiceImpl;
import com.bharath.MainCentralizedResource;

/**
 * This servlet will block the user which is an admin action
 * @author bharath-22329
 *
 */
public class BlockUserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPut(request, response);
	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		if(session.getAttribute("loggedinUser") == null){
			session.setAttribute("isNotLoggedIn", "y");
			response.sendRedirect("index.jsp");
			return;
		}
		
		Users users = (Users) session.getAttribute("loggedinUser");
		UsersService usersServiceImpl = UsersServiceImpl.getInstance();
		boolean isAdminCheck = usersServiceImpl.isAdminCheck(users.getUserId_());
		
		if(!isAdminCheck){
			session.setAttribute("notAdminUser", "y");
			response.sendRedirect("allUsersList.jsp");
			return;
		}
		
		int targetUserId = -1;
		
		try{
			targetUserId = Integer.parseInt(request.getParameter("userId"));
		}catch(NumberFormatException e){
			MainCentralizedResource.LOGGER.severe(e.toString());
			session.setAttribute("somethingWentWrong", "y");
			response.sendRedirect("allUsersList.jsp");
			return;
		}
		
		String apiUrlForBlockUser = "http://localhost:8080/BharathSocialMediaAppRestFullWebServ/webapi/user/"+users.getUserId_()+"/blockUser/"+targetUserId; 
        HttpURLConnection connForBlockUser = (HttpURLConnection) new URL(apiUrlForBlockUser).openConnection();
        connForBlockUser.setRequestMethod("PUT");
        
        int responseCodeForBlockUser = connForBlockUser.getResponseCode();
        connForBlockUser.disconnect();	  
		
		boolean isAccountBlocked = responseCodeForBlockUser == HttpURLConnection.HTTP_OK;
		
		
		if(!isAccountBlocked){
			session.setAttribute("isBlockingFailed", "y");
			response.sendRedirect("allUsersList.jsp");
			return;
		}
		
		session.setAttribute("isBlockingSuccessful", "y");
		response.sendRedirect("allUsersList.jsp");
	}
	
	
}
