package com.bharath.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bharath.model.Post;
import com.bharath.model.Users;
import com.bharath.service.UsersService;
import com.bharath.service.UsersServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.bharath.MainCentralizedResource;


/**
 * This servlet is used to remove the user account from the application - which is the admin action
 * @author bharath-22329
 *
 */
public class RemoveAccountServlet extends HttpServlet {
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
		
		Users users = (Users) session.getAttribute("loggedinUser");
		
		
		UsersService usersServiceImpl = UsersServiceImpl.getInstance();
		boolean isAdminRoleCheck = usersServiceImpl.isAdminCheck(users.getUserId_());
		
		if(!isAdminRoleCheck){
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
		
		String apiUrlForAllPostByUser = "http://localhost:8080/BharathSocialMediaAppRestFullWebServ/webapi/user/"+users.getUserId_()+"/post/targetUser/"+targetUserId; 
        HttpURLConnection connForAllPostByUser = (HttpURLConnection) new URL(apiUrlForAllPostByUser).openConnection();
        connForAllPostByUser.setRequestMethod("GET");
		
        int responseCode = connForAllPostByUser.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            MainCentralizedResource.LOGGER.info("All posts retrieved successfully");
        } else {
        
        }
        
        StringBuilder responseBuilderForAllPostByUser = new StringBuilder();
        try (BufferedReader brForAllPostByUser = new BufferedReader(new InputStreamReader(connForAllPostByUser.getInputStream(), "utf-8"))) {
            String responseLine;
            while ((responseLine = brForAllPostByUser.readLine()) != null) {
                responseBuilderForAllPostByUser.append(responseLine.trim());
            }
        }
        
        List<Post> allPostOfUser = null;
        if(!responseBuilderForAllPostByUser.toString().isEmpty()){
        	ObjectMapper objectMapperResponseForAllPostByUser = new ObjectMapper();
            objectMapperResponseForAllPostByUser.registerModule(new JavaTimeModule());
            allPostOfUser = objectMapperResponseForAllPostByUser.readValue(responseBuilderForAllPostByUser.toString(), new TypeReference<List<Post>>() {});	
        }
        
		
        connForAllPostByUser.disconnect();
        
		

        if(allPostOfUser != null){
            boolean isCommentInterrupted = false;
            boolean isLikeInterrupted = false;
            boolean isPostInterrupted = false;

            for(Post currentPost : allPostOfUser){
            	String apiUrlForCommentsDelete = "http://localhost:8080/BharathSocialMediaAppRestFullWebServ/webapi/user/"+users.getUserId_()+"/post/"+currentPost.getPostId()+"/comment/deleteCommentByPost"; 
    	        HttpURLConnection connForCommentsDelete = (HttpURLConnection) new URL(apiUrlForCommentsDelete).openConnection();
    	        connForCommentsDelete.setRequestMethod("DELETE");
    	        
    	        int responseCodeForCommentsDelete = connForCommentsDelete.getResponseCode();
    	        connForCommentsDelete.disconnect();
    	        
                boolean isCommentsDeleted = responseCodeForCommentsDelete == HttpURLConnection.HTTP_OK;
                if(!isCommentsDeleted){
                    MainCentralizedResource.LOGGER.warning("Something went wrong in deleting the comments!");
                    isCommentInterrupted = true;
                    break;
                }

                String apiUrlForLikesDelete = "http://localhost:8080/BharathSocialMediaAppRestFullWebServ/webapi/user/"+users.getUserId_()+"/post/"+currentPost.getPostId()+"/like/deleteLikeByPost"; 
    	        HttpURLConnection connForLikesDelete = (HttpURLConnection) new URL(apiUrlForLikesDelete).openConnection();
    	        connForLikesDelete.setRequestMethod("DELETE");
    	        
    	        int responseCodeForLikesDelete = connForLikesDelete.getResponseCode();
    	        connForLikesDelete.disconnect();
    	        
                boolean isLikesDeleted = responseCodeForLikesDelete == HttpURLConnection.HTTP_OK;
                if(!isLikesDeleted){
                    MainCentralizedResource.LOGGER.warning("Something went wrong in deleting all the likes of the user!");
                    isLikeInterrupted = true;
                    break;
                }

                
                String apiUrlForPostDelete = "http://localhost:8080/BharathSocialMediaAppRestFullWebServ/webapi/user/"+users.getUserId_()+"/post/"+currentPost.getPostId(); 
    	        HttpURLConnection connForPostDelete = (HttpURLConnection) new URL(apiUrlForPostDelete).openConnection();
    	        connForPostDelete.setRequestMethod("DELETE");
    	        
    	        int responseCodeForPostDelete = connForPostDelete.getResponseCode();
    	        connForPostDelete.disconnect();	  
    	        
                boolean isPostDeleted = responseCodeForPostDelete == HttpURLConnection.HTTP_OK;

                if(!isPostDeleted){
                    MainCentralizedResource.LOGGER.warning("Something went wrong");
                    isPostInterrupted = true;
                    break;
                }
            }
            if(isCommentInterrupted || isLikeInterrupted || isPostInterrupted){
                MainCentralizedResource.LOGGER.warning("Something went wrong");
                session.setAttribute("somethingWentWrong", "y");
    			response.sendRedirect("allUsersList.jsp");
    			return;
            }
        }

        String apiUrlForCommentsDeleteByUser = "http://localhost:8080/BharathSocialMediaAppRestFullWebServ/webapi/user/"+users.getUserId_()+"/post/"+0+"/comment/deleteCommentByUser/"+targetUserId; 
        HttpURLConnection connForCommentsDeleteByUser = (HttpURLConnection) new URL(apiUrlForCommentsDeleteByUser).openConnection();
        connForCommentsDeleteByUser.setRequestMethod("DELETE");
        
        int responseCodeForCommentsDeleteByUser = connForCommentsDeleteByUser.getResponseCode();
        connForCommentsDeleteByUser.disconnect();
        
        boolean isCommentsForTheUsersDeleted = responseCodeForCommentsDeleteByUser == HttpURLConnection.HTTP_OK;

        if(!isCommentsForTheUsersDeleted){
            MainCentralizedResource.LOGGER.warning("Something went wrong");
            session.setAttribute("somethingWentWrong", "y");
			response.sendRedirect("allUsersList.jsp");
			return;
        }
        
        String apiUrlForLikesDeleteByUser = "http://localhost:8080/BharathSocialMediaAppRestFullWebServ/webapi/user/"+users.getUserId_()+"/post/"+0+"/like/deleteLikeByUser/"+targetUserId; 
        HttpURLConnection connForLikesDeleteByUser = (HttpURLConnection) new URL(apiUrlForLikesDeleteByUser).openConnection();
        connForLikesDeleteByUser.setRequestMethod("DELETE");
        
        int responseCodeForLikesDeleteByUser = connForLikesDeleteByUser.getResponseCode();
        connForLikesDeleteByUser.disconnect();
        
        boolean isLikesDeletedUsingUserId = responseCodeForLikesDeleteByUser == HttpURLConnection.HTTP_OK;

        if(!isLikesDeletedUsingUserId){
            MainCentralizedResource.LOGGER.warning("Something went wrong");
            session.setAttribute("somethingWentWrong", "y");
			response.sendRedirect("allUsersList.jsp");
			return;
        }

        String apiUrlForListOfFollowers = "http://localhost:8080/BharathSocialMediaAppRestFullWebServ/webapi/user/"+targetUserId+"/follower/listOfAllFollowers";
		URL url = new URL(apiUrlForListOfFollowers);
		HttpURLConnection connectionForListOfFollowers = (HttpURLConnection) url.openConnection();
		connectionForListOfFollowers.setRequestMethod("GET");
		connectionForListOfFollowers.setRequestProperty("Accept", "application/json");
		
		StringBuilder responseBuilderForListOfFollowers = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(connectionForListOfFollowers.getInputStream()))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		        responseBuilderForListOfFollowers.append(line);
		    }
		}
		connectionForListOfFollowers.disconnect();
		
		List<Users> listOfAllFollowers = null;
		
		if(!responseBuilderForListOfFollowers.toString().isEmpty()){
			ObjectMapper objectMapperForListOfFollowers = new ObjectMapper();
			objectMapperForListOfFollowers.registerModule(new JavaTimeModule());
			listOfAllFollowers = objectMapperForListOfFollowers.readValue(responseBuilderForListOfFollowers.toString(), new TypeReference<List<Users>>(){});
		}
        

        if(listOfAllFollowers != null){
            boolean isFollowingUserUnFollowInterrupted = false;
            for(Users user : listOfAllFollowers){
            	
            	String apiUrlForFollowerUnfollow = "http://localhost:8080/BharathSocialMediaAppRestFullWebServ/webapi/user/"+targetUserId+"/follower/"+user.getUserId_(); 
                HttpURLConnection connForFollowerUnfollow = (HttpURLConnection) new URL(apiUrlForFollowerUnfollow).openConnection();
                connForFollowerUnfollow.setRequestMethod("DELETE");
                
                int responseCodeForFollowerUnfollow = connForFollowerUnfollow.getResponseCode();
                connForFollowerUnfollow.disconnect();
            	
                boolean isUnfollowSuccessfull = responseCodeForFollowerUnfollow == HttpURLConnection.HTTP_OK;
                
                if(!isUnfollowSuccessfull){
                    isFollowingUserUnFollowInterrupted = true;
                    break;
                }
            }
            if(isFollowingUserUnFollowInterrupted){
                MainCentralizedResource.LOGGER.warning("Something went wrong");
                session.setAttribute("somethingWentWrong", "y");
    			response.sendRedirect("allUsersList.jsp");
    			return;
            }
        }

        String apiUrlForListOfFollowing = "http://localhost:8080/BharathSocialMediaAppRestFullWebServ/webapi/user/"+targetUserId+"/follower/listOfAllFollowing";
    	URL urlForListOfFollowing = new URL(apiUrlForListOfFollowing);
    	HttpURLConnection connectionForListOfFollowing = (HttpURLConnection) urlForListOfFollowing.openConnection();
    	connectionForListOfFollowing.setRequestMethod("GET");
    	connectionForListOfFollowing.setRequestProperty("Accept", "application/json");
    	
    	StringBuilder responseBuilderForListOfFollowing = new StringBuilder();
    	try (BufferedReader br1 = new BufferedReader(new InputStreamReader(connectionForListOfFollowing.getInputStream()))) {
    	    String line;
    	    while ((line = br1.readLine()) != null) {
    	    	responseBuilderForListOfFollowing.append(line);
    	    }
    	}
    	connectionForListOfFollowing.disconnect();
    	List<Users> listOfAllUsersFollowing = null;
    	if(!responseBuilderForListOfFollowing.toString().isEmpty()){
    		ObjectMapper objectMapperForListOfFollowing = new ObjectMapper();
    		objectMapperForListOfFollowing.registerModule(new JavaTimeModule());
    		listOfAllUsersFollowing = objectMapperForListOfFollowing.readValue(responseBuilderForListOfFollowing.toString(), new TypeReference<List<Users>>(){});
    	}
        

        if(listOfAllUsersFollowing != null){
            boolean isFollowingUserUnFollowInterrupted = false;
            for(Users user : listOfAllUsersFollowing){
            	String apiUrlForFollowingUnfollow = "http://localhost:8080/BharathSocialMediaAppRestFullWebServ/webapi/user/"+user.getUserId_()+"/follower/"+targetUserId; 
                HttpURLConnection connForFollowingUnfollow = (HttpURLConnection) new URL(apiUrlForFollowingUnfollow).openConnection();
                connForFollowingUnfollow.setRequestMethod("DELETE");
                
                int responseCodeForFollowingUnfollow = connForFollowingUnfollow.getResponseCode();
                connForFollowingUnfollow.disconnect();
            	
                boolean isUnfollowSuccessfull = responseCodeForFollowingUnfollow == HttpURLConnection.HTTP_OK;
                
                if(!isUnfollowSuccessfull){
                    isFollowingUserUnFollowInterrupted = true;
                    break;
                }
            }
            if(isFollowingUserUnFollowInterrupted){
                MainCentralizedResource.LOGGER.warning("Something went wrong");
                session.setAttribute("somethingWentWrong", "y");
    			response.sendRedirect("allUsersList.jsp");
    			return;
            }
        }
		
        String apiUrlForRemovingUserAccount = "http://localhost:8080/BharathSocialMediaAppRestFullWebServ/webapi/user/"+users.getUserId_()+"/removeAccount/"+targetUserId; 
        HttpURLConnection connForRemovingUserAccount = (HttpURLConnection) new URL(apiUrlForRemovingUserAccount).openConnection();
        connForRemovingUserAccount.setRequestMethod("DELETE");
        
        int responseCodeForFollowingUnfollow = connForRemovingUserAccount.getResponseCode();
        connForRemovingUserAccount.disconnect();
        
		boolean isAccountRemoved = responseCodeForFollowingUnfollow == HttpURLConnection.HTTP_OK;
		
		if(!isAccountRemoved){
			MainCentralizedResource.LOGGER.warning("Something went wrong in removing the user account");
			session.setAttribute("isRemovalFailed", "y");
			response.sendRedirect("allUsersList.jsp");
			return;
		}
		
		MainCentralizedResource.LOGGER.info("Successfully removed the account");
		session.setAttribute("removalAccountSuccessful", "y");
		response.sendRedirect("allUsersList.jsp");
	}

	
}
