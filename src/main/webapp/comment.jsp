<%@page import="com.bharath.service.FollowerService"%>
<%@page import="com.bharath.service.FollowerServiceImpl"%>
<%@page import="com.bharath.service.UsersServiceImpl"%>
<%@page import="com.bharath.service.UsersService"%>
<%@page import="com.fasterxml.jackson.datatype.jsr310.JavaTimeModule"%>
<%@page import="com.fasterxml.jackson.databind.ObjectMapper"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="com.bharath.model.Users"%>
<%@page import="java.net.URL"%>
<%@page import="java.net.HttpURLConnection"%>
<%@page import="com.bharath.service.PostServiceImpl"%>
<%@page import="com.bharath.service.PostService"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="com.bharath.dao.PostDaoImpl"%>
<%@page import="com.bharath.dao.PostDao"%>
<%@page import="com.bharath.model.Post"%>
<%@page import="java.time.LocalDateTime"%>
<%@page import="com.bharath.MainCentralizedResource"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Add Comments</title>
<%@ include file="header.jsp" %>
</head>
<body>
<div>
<h1>Add Comment</h1>
<%
	response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");

	if(session.getAttribute("loggedinUser") == null){
		session.setAttribute("isNotLoggedIn", "y");
		response.sendRedirect("index.jsp");
		return;
	}
	
	int postId	= -1;
	try{
		postId = Integer.parseInt(request.getParameter("postId"));
	}catch(NumberFormatException e){
		MainCentralizedResource.LOGGER.severe(e.toString());
		session.setAttribute("somethingWentWrong", "y");
		response.sendRedirect("post.jsp");
		return;
	}
	
	String apiUrlForPost = String.format("http://localhost:8080/BharathSocialMediaAppRestFullWebServ/webapi/user/%d/post/%d", ((Users) session.getAttribute("loggedinUser")).getUserId_(), postId); 
	HttpURLConnection connForPost = (HttpURLConnection) new URL(apiUrlForPost).openConnection();
	connForPost.setRequestMethod("GET");
	
	int responseCodeForPost = connForPost.getResponseCode();
	if (responseCodeForPost == HttpURLConnection.HTTP_OK) {
	    MainCentralizedResource.LOGGER.info("Post retrieved successfully");
	} else {
	    MainCentralizedResource.LOGGER.warning( String.format("Error retrieving post for post id %d : %d", postId, responseCodeForPost));
	}
	
	StringBuilder responseBuilderForPost = new StringBuilder();
	try (BufferedReader br = new BufferedReader(new InputStreamReader(connForPost.getInputStream(), "utf-8"))) {
	    String responseLine;
	    while ((responseLine = br.readLine()) != null) {
	    	responseBuilderForPost.append(responseLine.trim());
	    }
	}
	
	MainCentralizedResource.LOGGER.info(responseBuilderForPost.toString());
	Post post = null;
	if(!responseBuilderForPost.toString().isEmpty()){
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		post = objectMapper.readValue(responseBuilderForPost.toString(), Post.class);
	}
	connForPost.disconnect();

	if(post == null){
		MainCentralizedResource.LOGGER.warning("Something went wrong in feting the post to view likes!");
	    session.setAttribute("somethingWentWrong", "y");
	    response.sendRedirect("post.jsp");
		return;
	}
	
	Users users = (Users)session.getAttribute("loggedinUser");
	UsersService usersServiceImpl = UsersServiceImpl.getInstance();
	if((!usersServiceImpl.isAdminCheck(users.getUserId_())) && (post.getUserId() != users.getUserId_())){
		FollowerService followerServiceImpl = FollowerServiceImpl.getInstance();
		int postedUserId = post.getUserId();
		boolean isFollowing = followerServiceImpl.isFollowing(users.getUserId_(), postedUserId);
		if(!isFollowing){
			out.println("<h3>Your are not supposed to view this page</h3>");
			return;
		}
	}
	
%>
</div>
<div>
<h2>Post: </h2>
<table border="1" width="90%">  
<tr>
<th>Post Creation Date Time</th>
<th>User</th>
<th>Post</th>
</tr>
<tr>
<td><%=LocalDateTime.of(post.getPostedDate(), post.getPostedTime()).format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")) %></td>
<td><%=post.getUserId() %></td>
<td><%=post.getPostContent() %></td>
</tr>
</table>
</div>
<div>
<form action="AddCommentServlet" method="post">
<label for="comment">Enter the comment:</label>
<input type="text" name="comment" id="comment">
<input type="hidden" name="postid" value="<%=postId %>">
<input type="submit" value="Post Comment">
</form>
</div>
</body>
</html>