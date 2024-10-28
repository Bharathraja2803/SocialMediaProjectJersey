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


public class AuthenticationFilter implements Filter {

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpSession session = ((HttpServletRequest) request).getSession();
		if(session.getAttribute("loggedinUser") != null){
			chain.doFilter(request, response);
			return;
		}
		String userid = request.getParameter("userid");
		String password = request.getParameter("password");
		
        int userIdInteger = -1;
        try{
            userIdInteger = Integer.parseInt(userid);
        }catch(NumberFormatException e){
            MainCentralizedResource.LOGGER.warning("User id incorrect\nuserid: " + userid);
            MainCentralizedResource.LOGGER.severe(e.toString());
            session.setAttribute("inValidUserId", "y");
            RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
            rd.forward(request, response);
            return;
        }

        UsersService usersServiceImpl = UsersServiceImpl.getInstance();
        boolean isExits = usersServiceImpl.isValidUserId(userIdInteger);
        if(!isExits){
            session.setAttribute("inValidUserId", "y");
            RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
            rd.forward(request, response);
            return;
        }

        
        boolean isPasswordCorrect = usersServiceImpl.isCorrectCredentials(userIdInteger, password);

        if(!isPasswordCorrect){
            session.setAttribute("inCorrectPassword", "y");
            RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
            rd.forward(request, response);
            return;
        }

        
        boolean isUserBlocked = usersServiceImpl.isUserBlocked(userIdInteger);
        
        if(isUserBlocked){
            session.setAttribute("isAccountBlocked", "y");
            RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
            rd.forward(request, response);
            return;
        }
        
        String apiUrl = "http://localhost:8080/BharathSocialMediaAppRestFullWebServ/webapi/user/"+userIdInteger; 
        HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
        conn.setRequestMethod("GET");
        
        
        StringBuilder responseBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                responseBuilder.append(responseLine.trim());
            }
        }
        
        Users loggedinUser = null;
        if(!responseBuilder.toString().isEmpty()){
        	ObjectMapper objectMapperResponse = new ObjectMapper();
            objectMapperResponse.registerModule(new JavaTimeModule());
            loggedinUser = objectMapperResponse.readValue(responseBuilder.toString(), Users.class);
        }
        conn.disconnect();
        
        if(loggedinUser == null){
        	session.setAttribute("somethingWentWrong", "y");
        	RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
            rd.forward(request, response);
            return;
        }
        MainCentralizedResource.LOGGER.info("User with id "+userIdInteger+" had logged in");
        
        session.setAttribute("loggedinUser", loggedinUser);
		chain.doFilter(request, response);
		
	}

}
