<%@page import="com.bharath.service.FollowerServiceImpl"%>
<%@page import="com.bharath.service.FollowerService"%>
<%@page import="com.fasterxml.jackson.core.type.TypeReference"%>
<%@page import="com.fasterxml.jackson.datatype.jsr310.JavaTimeModule"%>
<%@page import="com.fasterxml.jackson.databind.ObjectMapper"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.net.HttpURLConnection"%>
<%@page import="java.net.URL"%>
<%@page import="java.util.List"%>
<%@page import="com.bharath.MainCentralizedResource"%>
<%@page import="com.bharath.dao.FollowerDaoImpl"%>
<%@page import="com.bharath.dao.FollowerDao"%>
<%@page import="com.bharath.model.Users"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>List of all following</title>
<%@ include file="header.jsp" %>
</head>
<body>
<div>
<h1>All Following</h1>
<%
	response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
	if(session.getAttribute("somethingWentWrong") != null){
		out.println("<h3>Something went wrong try log out and login again!</h3>");
		session.removeAttribute("somethingWentWrong");
	}

	if(session.getAttribute("isUnfollowSuccessful") != null){
		out.println("<h3>Successfully unfollowed the user!</h3>");
		session.removeAttribute("isUnfollowSuccessful");
	}
	
	if(session.getAttribute("isFollowingTheUserCompleted") != null){
		out.println("<h3>Successfully followed the user!</h3>");
		session.removeAttribute("isFollowingTheUserCompleted");
	}
%>
</div>
<div>
<h2>My account</h2>
<%
	
	if(session.getAttribute("loggedinUser") == null){
		session.setAttribute("isNotLoggedIn", "y");
		response.sendRedirect("index.jsp");
		return;
	}
	
	Users users = (Users) session.getAttribute("loggedinUser");
%>

<table border="1" width="90%">  
<tr>
<th>User id</th>
<th>User name</th>
</tr>
<tr>
<td><%=users.getUserId_() %></td>
<td><%=users.getUserName_() %></td>
</tr>
</table>
</div>
<div>
<h2>Following</h2>
<%

	String apiUrlForListOfFollowing = "http://localhost:8080/BharathSocialMediaAppRestFullWebServ/webapi/user/"+((Users) session.getAttribute("loggedinUser")).getUserId_()+"/follower/listOfAllFollowing";
	URL urlForListOfFollowing = new URL(apiUrlForListOfFollowing);
	HttpURLConnection connectionForListOfFollowing = (HttpURLConnection) urlForListOfFollowing.openConnection();
	connectionForListOfFollowing.setRequestMethod("GET");
	connectionForListOfFollowing.setRequestProperty("Accept", "application/json");
	
	// Read the response
	StringBuilder responseBuilderForListOfFollowing = new StringBuilder();
	try (BufferedReader br1 = new BufferedReader(new InputStreamReader(connectionForListOfFollowing.getInputStream()))) {
	    String line;
	    while ((line = br1.readLine()) != null) {
	    	responseBuilderForListOfFollowing.append(line);
	    }
	}
	connectionForListOfFollowing.disconnect();
	List<Users> listOfAllFollowing = null;
	if(!responseBuilderForListOfFollowing.toString().isEmpty()){
		ObjectMapper objectMapperForListOfFollowing = new ObjectMapper();
		objectMapperForListOfFollowing.registerModule(new JavaTimeModule());
		listOfAllFollowing = objectMapperForListOfFollowing.readValue(responseBuilderForListOfFollowing.toString(), new TypeReference<List<Users>>(){});
	}
	
	if(listOfAllFollowing == null){
		out.println("<h3>No users following you</h3>");
		return;
	}else{
		
	
%>
<table border="1" width="90%">  
<tr>
<th>User id</th>
<th>User name</th>
<% if(!users.getRoles_().equals("admin")){ %>
<th>Action</th>
<%} %>
</tr>

<%
		FollowerService followerServiceImpl = FollowerServiceImpl.getInstance();
		for(Users user : listOfAllFollowing){
			out.println(
					"<tr>"
					+"<td>"+user.getUserId_()+"</td>"
					+"<td>"+user.getUserName_()+"</td>");
					if(!users.getRoles_().equals("admin")){
						out.println("<td>"+(followerServiceImpl.isFollowing(users.getUserId_(), user.getUserId_()) ? "<a href=\"UnfollowServlet?userId="+user.getUserId_()+"&pageId=2\">Unfollow</a>":"<a href=\"FollowServlet?userId="+user.getUserId_()+"&pageId=1\">Follow</a>")+"</td>");	
					}
			out.println("</tr>");
					
		}	
	}
%>

</table>
</div>
</body>
</html>