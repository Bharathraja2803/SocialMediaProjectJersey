package com.bharath.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.bharath.MainCentralizedResource;

/**
 * This servlet is used to change password of the user by admin
 * @author bharath-22329
 *
 */
public class ChangePasswordAdminServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPut(request, response);
	}
	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String newPassword = request.getParameter("newPassword");
		String confirmPassword = request.getParameter("confirmPassword");
		HttpSession session = request.getSession();
		UsersService usersServiceImpl = UsersServiceImpl.getInstance();
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		if(session.getAttribute("loggedinUser") == null){
			session.setAttribute("isNotLoggedIn", "y");
			response.sendRedirect("index.jsp");
			return;
		}
		
		Users users = (Users) session.getAttribute("loggedinUser");
		
		boolean isAdminRoleCheck = usersServiceImpl.isAdminCheck(users.getUserId_());
		
		if(!isAdminRoleCheck){
			session.setAttribute("notAdminUser", "y");
			response.sendRedirect("allUsersList.jsp");
			return;
		}
		
		boolean isNewPasswordAndConfirmPasswordMatching = usersServiceImpl.isNewAndConfirmPasswordMatching(newPassword, confirmPassword);
		
		if(!isNewPasswordAndConfirmPasswordMatching){
			session.setAttribute("bothPasswordsAreNotMatching", "y");
			response.sendRedirect("allUsersList.jsp");
			return;
		}

		boolean isPasswordMatchingValid = usersServiceImpl.isValidPassword(newPassword);
		
		if(!isPasswordMatchingValid){
			session.setAttribute("isPasswordInValid", "y");
			response.sendRedirect("allUsersList.jsp");
			return;
		}
		
		
		int targetUserId = -1;
		try{
			targetUserId = Integer.parseInt(request.getParameter("targetUserIdFromForm"));
		}catch(NumberFormatException e){
			MainCentralizedResource.LOGGER.severe(e.toString());
			session.setAttribute("somethingWentWrong", "y");
			response.sendRedirect("allUsersList.jsp");
			return;
		}
		
		String apiUrl = "http://localhost:8080/BharathSocialMediaAppRestFullWebServ/webapi/user/"+targetUserId; 
        HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
        conn.setRequestMethod("GET");
        
        
        StringBuilder responseBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                responseBuilder.append(responseLine.trim());
            }
        }
        
        Users targetUser = null;
        if(!responseBuilder.toString().isEmpty()){
        	ObjectMapper objectMapperResponse = new ObjectMapper();
            objectMapperResponse.registerModule(new JavaTimeModule());
            targetUser = objectMapperResponse.readValue(responseBuilder.toString(), Users.class);	
        }
        conn.disconnect();

        if(targetUser == null){
            session.setAttribute("somethingWentWrong", "y");
			response.sendRedirect("allUsersList.jsp");
			return;
        }
		
		boolean isOldPasswordAndNewPasswordAreSame = usersServiceImpl.isOldPasswordAndNewPasswordAreSame(targetUser, newPassword);
		
		if(isOldPasswordAndNewPasswordAreSame){
			session.setAttribute("isOldPasswordAndNewPasswordAreSame", "y");
			response.sendRedirect("allUsersList.jsp");
			return;
		}
		
		targetUser.setPassword_(newPassword);
		
		ObjectMapper objectMapperForUpdatingPass = new ObjectMapper();
		objectMapperForUpdatingPass.registerModule(new JavaTimeModule());
        
        String jsonUser = objectMapperForUpdatingPass.writeValueAsString(targetUser);
        
        String apiUrlForUpdatingPass = "http://localhost:8080/BharathSocialMediaAppRestFullWebServ/webapi/user/"+targetUser.getUserId_(); 
        HttpURLConnection connForUpdatingPass = (HttpURLConnection) new URL(apiUrlForUpdatingPass).openConnection();
        connForUpdatingPass.setRequestMethod("PUT");
        connForUpdatingPass.setRequestProperty("Content-Type", "application/json");
        connForUpdatingPass.setDoOutput(true);

        
        try (OutputStream os = connForUpdatingPass.getOutputStream()) {
            byte[] input = jsonUser.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        
        int responseCodeForUpdatingPass = connForUpdatingPass.getResponseCode();
        connForUpdatingPass.disconnect();
        
		boolean isPasswordChangeSuccessful = responseCodeForUpdatingPass == HttpURLConnection.HTTP_OK;
		
        if(!isPasswordChangeSuccessful){
            session.setAttribute("somethingWentWrong", "y");
			response.sendRedirect("allUsersList.jsp");
			return;
        }
        
        session.setAttribute("isChangingThePasswordSuccessfull", "y");
        response.sendRedirect("allUsersList.jsp");
	}
	
	
}
