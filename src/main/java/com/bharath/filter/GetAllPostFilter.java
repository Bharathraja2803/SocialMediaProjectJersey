package com.bharath.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.bharath.model.Post;
import com.bharath.model.Users;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.bharath.MainCentralizedResource;

/**
 * This filter will fetch all the post of the follower and the user's post and redirect them to post.jsp page -> normal user role
 * This filter will fetch all the post in the application to the home page of the admin user post.jsp -> admin user role
 * @author bharath-22329
 *
 */
public class GetAllPostFilter implements Filter {

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		HttpSession session = ((HttpServletRequest)request).getSession();
		
		if(session.getAttribute("loggedinUser") == null){
			session.setAttribute("isNotLoggedIn", "y");
			((HttpServletResponse)response).sendRedirect("index.jsp");
			return;
		}
		
		Users users = (Users) session.getAttribute("loggedinUser");
		
		String apiUrl = "http://localhost:8080/BharathSocialMediaAppRestFullWebServ/webapi/user/"+users.getUserId_()+"/post"; 
        HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
        conn.setRequestMethod("GET");
		
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            MainCentralizedResource.LOGGER.info("All posts retrieved successfully");
        } else {
            MainCentralizedResource.LOGGER.warning("Error retrieving post: " + responseCode);
            chain.doFilter(request, response);
			return;
            
        }
        
        StringBuilder responseBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                responseBuilder.append(responseLine.trim());
            }
        }
        
        List<Post> allPost = null;
        if(!responseBuilder.toString().isEmpty()){
        	ObjectMapper objectMapperResponse = new ObjectMapper();
            objectMapperResponse.registerModule(new JavaTimeModule());
            allPost = objectMapperResponse.readValue(responseBuilder.toString(), new TypeReference<List<Post>>() {});	
        }
        
        MainCentralizedResource.LOGGER.info("All Post: "+allPost);
        conn.disconnect();
        
        if(allPost == null){
        	MainCentralizedResource.LOGGER.warning("Error retrieving post");
            chain.doFilter(request, response);
 			return;
        }
		
		
		session.setAttribute("postsToView", allPost);
	    chain.doFilter(request, response);    
			
	}

}
