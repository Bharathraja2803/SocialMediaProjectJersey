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
<%@page import="com.bharath.dao.FollowerDaoImpl"%>
<%@page import="com.bharath.dao.FollowerDao"%>
<%@page import="com.bharath.model.Users"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Add following</title>
<%@ include file="header.jsp" %>
</head>
<body>
<div>
<h1>Add Followers</h1>
<%
	response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
	if(session.getAttribute("somethingWentWrong") != null){
		out.println("<h3>Something went wrong try log out and login again!</h3>");
		session.removeAttribute("somethingWentWrong");
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
<h2>Peoples</h2>
<%

	String apiUrlForListOfNonFollowers = "http://localhost:8080/BharathSocialMediaAppRestFullWebServ/webapi/user/"+((Users) session.getAttribute("loggedinUser")).getUserId_()+"/follower/listOfNotFollowedUsers";
	URL url = new URL(apiUrlForListOfNonFollowers);
	HttpURLConnection connectionForListOfNonFollowers = (HttpURLConnection) url.openConnection();
	connectionForListOfNonFollowers.setRequestMethod("GET");
	connectionForListOfNonFollowers.setRequestProperty("Accept", "application/json");
	
	// Read the response
	StringBuilder responseBuilderForListOfNonFollowers = new StringBuilder();
	try (BufferedReader br = new BufferedReader(new InputStreamReader(connectionForListOfNonFollowers.getInputStream()))) {
	    String line;
	    while ((line = br.readLine()) != null) {
	    	responseBuilderForListOfNonFollowers.append(line);
	    }
	}
	connectionForListOfNonFollowers.disconnect();
	List<Users> listOfAllNotFollowedUser = null;
	if(!responseBuilderForListOfNonFollowers.toString().isEmpty()){
		ObjectMapper objectMapperForListOfNonFollowers = new ObjectMapper();
		objectMapperForListOfNonFollowers.registerModule(new JavaTimeModule());
		listOfAllNotFollowedUser = objectMapperForListOfNonFollowers.readValue(responseBuilderForListOfNonFollowers.toString(), new TypeReference<List<Users>>(){});
	}

	if(listOfAllNotFollowedUser == null){
		out.println("<h3>Something went wrong</h3>");
		return;
	}else if(listOfAllNotFollowedUser.isEmpty()){
		out.println("<h3>You have followed every one</h3>");
		return;
	}else{
		
	
%>
<table border="1" width="90%">  
<tr>
<th>User id</th>
<th>User name</th>
<th>Action</th>
</tr>

<%
		for(Users user : listOfAllNotFollowedUser){
			out.println(
					"<tr>"
					+"<td>"+user.getUserId_()+"</td>"
					+"<td>"+user.getUserName_()+"</td>"
					+"<td><a href=\"FollowServlet?userId="+user.getUserId_()+"&pageId=2\">Follow</a></td>"
					+"</tr>"
					);
		}	
	}
%>

</table>
</div>

</body>
</html>