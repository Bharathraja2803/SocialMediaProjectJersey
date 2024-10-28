package com.bharath.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import com.bharath.model.Users;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.bharath.MainCentralizedResource;

/**
 * This servlet is used to change password of the user by themself while clicking on forget password
 * @author bharath-22329
 *
 */
public class ChangePasswordServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPut(request, response);
	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		Users targetUser = (Users) session.getAttribute("userDetailsWhoFogetPasword");
		
		if(targetUser == null){
            MainCentralizedResource.LOGGER.warning("Something went wrong in geting the passsword forgotten user details!");
            session.setAttribute("isForgetPassordUnsuccessfull", "y");
            RequestDispatcher rd = request.getRequestDispatcher("forget_password.jsp");
            rd.forward(request, response);
			return;
        }
		
		String newPassword = request.getParameter("newPassword");
        
		targetUser.setPassword_(newPassword);
		
		ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        String jsonUser = objectMapper.writeValueAsString(targetUser);
        
        String apiUrl = "http://localhost:8080/BharathSocialMediaAppRestFullWebServ/webapi/user/"+targetUser.getUserId_(); 
        HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonUser.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            MainCentralizedResource.LOGGER.info("User password changed successfully");
        } else {
            MainCentralizedResource.LOGGER.warning("Error Changing User Password user: " + responseCode);
            RequestDispatcher rd = request.getRequestDispatcher("change_password.jsp");
			session.setAttribute("isChangingPasswordWithNewPasswordSuccessfull", "y");
			rd.forward(request, response);
            return;    
        }
        
        conn.disconnect();
        session.setAttribute("isChangingThePasswordSuccessfull", "y");
        RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
        rd.forward(request, response);
	}

	
	
	

}
