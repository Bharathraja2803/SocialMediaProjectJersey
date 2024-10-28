package com.bharath.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


import com.bharath.model.Users;
import com.bharath.service.UsersService;
import com.bharath.service.UsersServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.bharath.MainCentralizedResource;

/**
 * This password will validate the email id and date of birth of the user when they have forget their password
 * @author bharath-22329
 *
 */
public class ForgetPasswordEmailDobValidationFilter implements Filter {
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		String emailid = request.getParameter("mailid");
		String dob = request.getParameter("dob");
		
		
		UsersService usersServiceImpl = UsersServiceImpl.getInstance();
        int userId = usersServiceImpl.getUserIdByEmailId(emailid);
        HttpSession session = ((HttpServletRequest) request).getSession();
        
        if(userId == -1){
            session.setAttribute("isEmailInvaid", "y");
            RequestDispatcher rd = request.getRequestDispatcher("forget_password.jsp");
            rd.forward(request, response);
			return;
        }
        
        String apiUrl = "http://localhost:8080/BharathSocialMediaAppRestFullWebServ/webapi/user/"+userId; 
        HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
        conn.setRequestMethod("GET");
        
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            MainCentralizedResource.LOGGER.info("User Retrieved successfully from API");
        } else {
            response.getWriter().write("User Retrieval Failure from API: " + responseCode);
            RequestDispatcher rd = request.getRequestDispatcher("forget_password.jsp");
			session.setAttribute("isForgetPassordUnsuccessfull", "y");
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
        objectMapperResponse.registerModule(new JavaTimeModule());
        Users userResponse = objectMapperResponse.readValue(responseBuilder.toString(), Users.class);
        
        conn.disconnect();
        
        

        if(userResponse == null){
            session.setAttribute("isForgetPassordUnsuccessfull", "y");
            RequestDispatcher rd = request.getRequestDispatcher("forget_password.jsp");
            rd.forward(request, response);
			return;
        }

        boolean isValidDob = usersServiceImpl.isDobValid(dob); 
        boolean isEmailAndBirthdayDateMatching = usersServiceImpl.isValidEmailAndDobOfUser(emailid, dob);
        
        if( (!isValidDob) || (!isEmailAndBirthdayDateMatching)){
            MainCentralizedResource.LOGGER.warning("Entered date of birth is invalid");
            session.setAttribute("isDobInvalid", "y");
            RequestDispatcher rd = request.getRequestDispatcher("forget_password.jsp");
            rd.forward(request, response);
			return;
        }

        
        session.setAttribute("userDetailsWhoFogetPasword", userResponse);
		chain.doFilter(request, response);
	}
	
}
