<%@page import="com.fasterxml.jackson.databind.DeserializationFeature"%>
<%@page import="com.fasterxml.jackson.core.type.TypeReference"%>
<%@page import="com.fasterxml.jackson.core.JsonParser.Feature"%>
<%@page import="com.fasterxml.jackson.datatype.jsr310.JavaTimeModule"%>
<%@page import="java.net.HttpURLConnection"%>
<%@page import="java.net.URL"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="com.fasterxml.jackson.databind.ObjectMapper"%>
<%@page import="com.bharath.service.UsersServiceImpl"%>
<%@page import="com.bharath.service.UsersService"%>
<%@page import="com.bharath.service.CommentServiceImpl"%>
<%@page import="com.bharath.service.CommentService"%>
<%@page import="com.bharath.service.LikeServiceImpl"%>
<%@page import="com.bharath.service.LikeService"%>
<%@page import="com.bharath.model.Comment"%>
<%@page import="com.bharath.dao.CommentDaoImpl"%>
<%@page import="com.bharath.dao.CommentDao"%>
<%@page import="com.bharath.model.Like"%>
<%@page import="com.bharath.MainCentralizedResource"%>
<%@page import="com.bharath.dao.LikeDaoImpl"%>
<%@page import="com.bharath.dao.LikeDao"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.time.LocalDateTime"%>
<%@page import="com.bharath.model.Post"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="com.bharath.model.Users" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Post page</title>
<%@ include file="header.jsp" %>
</head>
<body>
<%
	response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
	if(session.getAttribute("loggedinUser") == null){
		session.setAttribute("isNotLoggedIn", "y");
		response.sendRedirect("index.jsp");
		return;
	}

	if(session.getAttribute("isPostCreationSuccessful") != null){
		out.println("<h3>Post created successfully!</h3>");
		session.removeAttribute("isPostCreationSuccessful");
	}
	
	if(session.getAttribute("isThereProblemInFetchingAllUsers") != null){
		out.println("<h3>Something went wrong! try logout and login again</h3>");
		session.removeAttribute("isThereProblemInFetchingAllUsers");
	}
	
	if(session.getAttribute("noPostByAllUsers") != null){
		out.println("<h3>No one posted anything</h3>");
		session.removeAttribute("noPostByAllUsers");
	}
	
	if(session.getAttribute("isThereProblemInFetchingFollower") != null){
		out.println("<h3>You have followed no one yet</h3>");
		session.removeAttribute("isThereProblemInFetchingFollower");
	}
	
	if(session.getAttribute("somethingWentWrong") != null){
		out.println("<h3>Something went wrong! Try Logout and Login again</h3>");
		session.removeAttribute("somethingWentWrong");
	}
	
	if(session.getAttribute("likeActionSuccessful") != null){
		out.println("<h3>You have liked the post</h3>");
		session.removeAttribute("likeActionSuccessful");
	}
	
	if(session.getAttribute("unlikeCompleted") != null){
		out.println("<h3>You have unliked the post</h3>");
		session.removeAttribute("unlikeCompleted");
	}
	
	if(session.getAttribute("isCommentingThePostSuccessful") != null){
		out.println("<h3>You have Commented the post</h3>");
		session.removeAttribute("isCommentingThePostSuccessful");
	}
	
	if(session.getAttribute("noLikesForThePost") != null){
		out.println("<h3>No Likes for the post</h3>");
		session.removeAttribute("noLikesForThePost");
	}
	
	if(session.getAttribute("noCommentsForThePost") != null){
		out.println("<h3>No Comments for the post</h3>");
		session.removeAttribute("noCommentsForThePost");
	}
	
	if(session.getAttribute("postDeletedSuccessfully") != null){
		out.println("<h3>Post Was deleted Successfully</h3>");
		session.removeAttribute("postDeletedSuccessfully");
	}
	
	if(session.getAttribute("invalidComment") != null){
		out.println("<h3>Entered comment is Invalid</h3>");
		session.removeAttribute("invalidComment");
	}
	
%>

<div>
<h1>Welcome <%=((Users)session.getAttribute("loggedinUser")).getUserName_()%></h1>
</div>
<div>
<h2><a href="addpost.jsp">Create post</a></h2>
</div>
<div>
<%
	
	List<Post> allPostToView = (List<Post>)session.getAttribute("postsToView");
	if(allPostToView == null || allPostToView.isEmpty()){
		out.println("<h3>No post in your queue. Follow people to get post</h3>");
		
	}else{

%>
<table border="1" width="90%">  
<tr>
<th>Post Creation Date Time</th>
<th>User Id</th>
<th>User Name</th>
<th>Post</th>
<th>Like</th>  
<th>Comment</th>
<th>View All Likes</th>
<th>View All Comments</th>
<th>Delete the post</th>
</tr>  

<%
	LikeService likeServiceImpl = LikeServiceImpl.getInstance();
	CommentService commentServiceImpl = CommentServiceImpl.getInstance();
	UsersService usersServiceImpl = UsersServiceImpl.getInstance();
	for(Post postOneByOne : allPostToView){
		
		String apiUrl = "http://localhost:8080/BharathSocialMediaAppRestFullWebServ/webapi/user/"+((Users) session.getAttribute("loggedinUser")).getUserId_()+"/post/"+postOneByOne.getPostId()+"/like"; 
        HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
        conn.setRequestMethod("GET");
        
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            MainCentralizedResource.LOGGER.info("Like retrieved successfully");
        } else {
            MainCentralizedResource.LOGGER.warning( String.format("Error retrieving like for user %d and post %d : %d", ((Users) session.getAttribute("loggedinUser")).getUserId_(), postOneByOne.getPostId(), responseCode));
        }
        
        StringBuilder responseBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                responseBuilder.append(responseLine.trim());
            }
        }
        
        Like like = null;
        if(!responseBuilder.toString().isEmpty()){
        	ObjectMapper objectMapperResponse = new ObjectMapper();
            objectMapperResponse.registerModule(new JavaTimeModule());
            like = objectMapperResponse.readValue(responseBuilder.toString(), Like.class);	 
        }
        conn.disconnect();
        //int likesCount = 0;
		
        String apiUrlForAllLikesPerPost = String.format("http://localhost:8080/BharathSocialMediaAppRestFullWebServ/webapi/user/%d/post/%d/like/allLikesPerPost", ((Users) session.getAttribute("loggedinUser")).getUserId_(), postOneByOne.getPostId()); 
        HttpURLConnection connForAllLikesPerPost = (HttpURLConnection) new URL(apiUrlForAllLikesPerPost).openConnection();
        connForAllLikesPerPost.setRequestMethod("GET");
        
        int responseCodeForAllLikesPerPost = connForAllLikesPerPost.getResponseCode();
        if (responseCodeForAllLikesPerPost == HttpURLConnection.HTTP_OK) {
            MainCentralizedResource.LOGGER.info("Likes retrieved successfully");
        } else {
            MainCentralizedResource.LOGGER.warning( String.format("Error retrieving likes for post %d : %d", postOneByOne.getPostId(), responseCodeForAllLikesPerPost));
        }
        
        StringBuilder responseBuilderForAllLikesPerPost = new StringBuilder();
        try (BufferedReader br1 = new BufferedReader(new InputStreamReader(connForAllLikesPerPost.getInputStream(), "utf-8"))) {
            String responseLine;
            while ((responseLine = br1.readLine()) != null) {
            	responseBuilderForAllLikesPerPost.append(responseLine.trim());
            }
        }
        
        MainCentralizedResource.LOGGER.info(responseBuilderForAllLikesPerPost.toString());
        List<Like> likesCount = null;
        if(!responseBuilderForAllLikesPerPost.toString().isEmpty()){
        	ObjectMapper objectMapperForAllLikesPerPost = new ObjectMapper();
        	objectMapperForAllLikesPerPost.registerModule(new JavaTimeModule());
        	likesCount = objectMapperForAllLikesPerPost.readValue(responseBuilderForAllLikesPerPost.toString(), new TypeReference<List<Like>>(){});
        }
        connForAllLikesPerPost.disconnect();
		
        String apiUrlForAllCommentsPerPost = String.format("http://localhost:8080/BharathSocialMediaAppRestFullWebServ/webapi/user/%d/post/%d/comment/allCommentsPerPost", ((Users) session.getAttribute("loggedinUser")).getUserId_(), postOneByOne.getPostId()); 
        HttpURLConnection connForAllCommentsPerPost = (HttpURLConnection) new URL(apiUrlForAllCommentsPerPost).openConnection();
        connForAllCommentsPerPost.setRequestMethod("GET");
        
        int responseCodeForAllCommentsPerPost = connForAllCommentsPerPost.getResponseCode();
        if (responseCodeForAllCommentsPerPost == HttpURLConnection.HTTP_OK) {
            MainCentralizedResource.LOGGER.info("Comments retrieved successfully");
        } else {
            MainCentralizedResource.LOGGER.warning( String.format("Error retrieving comments for post %d : %d", postOneByOne.getPostId(), responseCodeForAllCommentsPerPost));
        }
        
        StringBuilder responseBuilderForAllCommentsPerPost = new StringBuilder();
        try (BufferedReader br2 = new BufferedReader(new InputStreamReader(connForAllCommentsPerPost.getInputStream(), "utf-8"))) {
            String responseLine;
            while ((responseLine = br2.readLine()) != null) {
            	responseBuilderForAllCommentsPerPost.append(responseLine.trim());
            }
        }
        
        MainCentralizedResource.LOGGER.info(responseBuilderForAllCommentsPerPost.toString());
        List<Comment> comments = null;
        if(!responseBuilderForAllCommentsPerPost.toString().isEmpty()){
        	ObjectMapper objectMapperForAllCommentsPerPost = new ObjectMapper();
        	objectMapperForAllCommentsPerPost.registerModule(new JavaTimeModule());
        	comments = objectMapperForAllCommentsPerPost.readValue(responseBuilderForAllCommentsPerPost.toString(), new TypeReference<List<Comment>>(){});
        }
        connForAllCommentsPerPost.disconnect();
		
		out.println("<tr>"
	+"<td>"+LocalDateTime.of(postOneByOne.getPostedDate(), postOneByOne.getPostedTime()).format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))+"</td>"
	+"<td>"+postOneByOne.getUserId()+"</td>"
	+"<td>"+usersServiceImpl.getUser(postOneByOne.getUserId()).getUserName_()+"</td>"
	+"<td>"+postOneByOne.getPostContent()+"</td>"
	+"<td>"+(likesCount == null ? "" : likesCount.size()) + (like == null ? "<a href=\"LikeThePostServlet?postId="+postOneByOne.getPostId()+"\">Like</a>": "<a href=\"UnlikeThePostServlet?likeId="+like.getLikeId()+"&postId="+postOneByOne.getPostId()+"\">Unlike</a>") +"</td>"
	+"<td>"+(comments == null ? "" : comments.size())+"<a href=\"comment.jsp?postId="+postOneByOne.getPostId()+"\">Comment</a></td>"
	+"<td><a href=\"viewLikes.jsp?postId="+postOneByOne.getPostId()+"\">View Likes</a></td>"
	+"<td><a href=\"viewComments.jsp?postId="+postOneByOne.getPostId()+"\">View Comments</a></td>"
	+"<td>"+(postOneByOne.getUserId() == ((Users)session.getAttribute("loggedinUser")).getUserId_() ? "<a href=\"PostDeleteServlet?postId="+postOneByOne.getPostId()+"\">Delete</a>" : ((Users)session.getAttribute("loggedinUser")).getRoles_().equals("admin") ? "<a href=\"PostDeleteServlet?postId="+postOneByOne.getPostId()+"\">Delete</a>" : "" )+"</td>"
	+"</tr>");
	}
	}
%>
</table>
</div>

</body>
</html>