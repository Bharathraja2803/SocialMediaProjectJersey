<%@page import="com.fasterxml.jackson.core.type.TypeReference"%>
<%@page import="com.fasterxml.jackson.datatype.jsr310.JavaTimeModule"%>
<%@page import="com.fasterxml.jackson.databind.ObjectMapper"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.net.HttpURLConnection"%>
<%@page import="java.net.URL"%>
<%@page import="com.bharath.service.FollowerServiceImpl"%>
<%@page import="com.bharath.service.FollowerService"%>
<%@page import="java.util.List"%>
<%@page import="com.bharath.MainCentralizedResource"%>
<%@page import="com.bharath.dao.FollowerDao"%>
<%@page import="com.bharath.model.Users"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>People page</title>
<%@ include file="header.jsp" %>
</head>
<body>
<h1>People page</h1>
<%
	response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
	if(session.getAttribute("loggedinUser") == null){
		session.setAttribute("isNotLoggedIn", "y");
		response.sendRedirect("index.jsp");
		return;
	}
	
%>

<table border="1" width="90%">  
<tr>
<th>Followers Count</th>
<th>View List of all Followers</th>
<%
	Users users = (Users) session.getAttribute("loggedinUser");
	if(users.getRoles_().equals("user")){
		
	
%>
<th>Following Count</th>
<th>View List of all Following</th>  
<th>Add Following</th>
</tr>  

<%
		String apiUrlForListOfFollowers = "http://localhost:8080/BharathSocialMediaAppRestFullWebServ/webapi/user/"+((Users) session.getAttribute("loggedinUser")).getUserId_()+"/follower/listOfAllFollowers";
		URL url = new URL(apiUrlForListOfFollowers);
		HttpURLConnection connectionForListOfFollowers = (HttpURLConnection) url.openConnection();
		connectionForListOfFollowers.setRequestMethod("GET");
		connectionForListOfFollowers.setRequestProperty("Accept", "application/json");
		
		// Read the response
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
		
	
	out.println(
			"<tr>"
			+"<td>"+(listOfAllFollowing == null ? 0 : listOfAllFollowing.size())+"</td>"
			+"<td><a href=\"viewAllFollowing.jsp\">Follower</a></td>"
			+"<td>"+(listOfAllFollowers == null ? 0 : listOfAllFollowers.size())+"</td>"
			+"<td><a href=\"viewAllFollowers.jsp\">Following</a></td>"
			+"<td><a href=\"addFollowing.jsp\">Add Following</a></td>"
			+"</tr>"
			);
	
	}else{
		

%>
<th>All Users List</th>
</tr>  
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
	
		out.println(
				"<tr>"
				+"<td>"+(listOfAllFollowing == null ? 0 : listOfAllFollowing.size())+"</td>"
				+"<td><a href=\"viewAllFollowing.jsp\">Follower</a></td>"
				+"<td><a href=\"allUsersList.jsp\">All user</a></td>"
				+"</tr>"
				);
	
	}
%>
</table>
</body>
</html>