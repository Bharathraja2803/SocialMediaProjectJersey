
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
<title>List All Followers</title>
<%@ include file="header.jsp" %>
</head>
<body>
<div>
<h1>All Followers</h1>
<%
	if(session.getAttribute("somethingWentWrong") != null){
		out.println("<h3>Something went wrong try log out and login again!</h3>");
		session.removeAttribute("somethingWentWrong");
	}

	if(session.getAttribute("isUnfollowSuccessful") != null){
		out.println("<h3>Successfully unfollowed the user!</h3>");
		session.removeAttribute("isUnfollowSuccessful");
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
<h2>Followers</h2>
<%
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
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
		
	if(listOfAllFollowers == null){
		out.println("<h3>You have not followed any people</h3>");
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
		for(Users user : listOfAllFollowers){
			out.println(
					"<tr>"
					+"<td>"+user.getUserId_()+"</td>"
					+"<td>"+user.getUserName_()+"</td>"
					+"<td><a href=\"UnfollowServlet?userId="+user.getUserId_()+"&pageId=1\">Unfollow</a></td>"
					+"</tr>"
					);
		}	
	}
%>

</table>
</div>
</body>
</html>