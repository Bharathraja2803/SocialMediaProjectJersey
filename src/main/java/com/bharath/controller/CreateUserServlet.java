package com.bharath.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
 * This servlet will create the user when they are signing up
 * @author bharath-22329
 *
 */
public class CreateUserServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		
		Users users = new Users();
		users.setUserName_(request.getParameter("username"));
		users.setPassword_(MainCentralizedResource.generateHashedPassword(request.getParameter("password")));
		users.setBirthday_(request.getParameter("dob"));
		users.setEmailId_(request.getParameter("mailid"));
		
		
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        String jsonUser = objectMapper.writeValueAsString(users);
        
        String apiUrl = "http://localhost:8080/BharathSocialMediaAppRestFullWebServ/webapi/user"; 
        HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonUser.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            MainCentralizedResource.LOGGER.info("User submitted successfully");
        } else {
            MainCentralizedResource.LOGGER.warning("Error submitting user: " + responseCode);
            RequestDispatcher rd = request.getRequestDispatcher("signupPage.jsp");
			session.setAttribute("isAccountNotCreated", "y");
			rd.forward(request, response);
			return;
            
        }
        
        StringBuilder responseBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                responseBuilder.append(responseLine.trim());
            }
        }
        
        ObjectMapper objectMapperResponse = new ObjectMapper();
        Users userResponse = objectMapperResponse.readValue(responseBuilder.toString(), Users.class);
        
        conn.disconnect();
        
        if(userResponse.getUserId_() <= 0){
        	RequestDispatcher rd = request.getRequestDispatcher("signupPage.jsp");
			session.setAttribute("isAccountNotCreated", "y");
			rd.forward(request, response);
			return;
        }
        
		session.setAttribute("newUserCreatedId", userResponse.getUserId_());
		RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
		rd.forward(request, response);
	}

}
