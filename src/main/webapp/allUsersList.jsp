<%@page import="com.bharath.service.UsersServiceImpl"%>
<%@page import="com.bharath.service.UsersService"%>
<%@page import="com.fasterxml.jackson.core.type.TypeReference"%>
<%@page import="com.fasterxml.jackson.datatype.jsr310.JavaTimeModule"%>
<%@page import="com.fasterxml.jackson.databind.ObjectMapper"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.net.HttpURLConnection"%>
<%@page import="java.net.URL"%>
<%@page import="java.util.List"%>
<%@page import="com.bharath.dao.UsersDaoImpl"%>
<%@page import="com.bharath.dao.UsersDao"%>
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
<title>All Users Page</title>
<%@ include file="header.jsp" %>
</head>
<body>
<div>
<h1>All Users List:</h1>
<%
	response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
	if(session.getAttribute("loggedinUser") == null){
		session.setAttribute("isNotLoggedIn", "y");
		response.sendRedirect("index.jsp");
		return;
	}
	
	Users users = (Users) session.getAttribute("loggedinUser");
	if(!users.getRoles_().equals("admin")){
		out.println("<h3>No access to this page</h3>");
		return;
	}
	
	if(session.getAttribute("notAdminUser") != null){
		out.println("<h3>You are not the admin user to perform this action</h3>");
		session.removeAttribute("notAdminUser");
		return;
	}
	
	if(session.getAttribute("somethingWentWrong") != null){
		out.println("<h3>Something went wrong try logout and login again</h3>");
		session.removeAttribute("somethingWentWrong");
	}
	
	if(session.getAttribute("isRoleUpdateFailure") != null){
		out.println("<h3>There is a failure in updating the role</h3>");
		session.removeAttribute("isRoleUpdateFailure");
	}
	
	if(session.getAttribute("roleChangeSuccessful") != null){
		out.println("<h3>Successfully updated the role</h3>");
		session.removeAttribute("roleChangeSuccessful");
	}
	
	if(session.getAttribute("isRemovalFailed") != null){
		out.println("<h3>There is a failure in removing the user account</h3>");
		session.removeAttribute("isRemovalFailed");
	}
	
	if(session.getAttribute("removalAccountSuccessful") != null){
		out.println("<h3>Successfully removed the account</h3>");
		session.removeAttribute("removalAccountSuccessful");
	}
	
	if(session.getAttribute("isUnblockingFailed") != null){
		out.println("<h3>Unblocking the account is unsuccessful</h3>");
		session.removeAttribute("isUnblockingFailed");
	}
	
	if(session.getAttribute("isUnblockingSuccessful") != null){
		out.println("<h3>Successfully unblocked the account</h3>");
		session.removeAttribute("isUnblockingSuccessful");
	}
	
	if(session.getAttribute("isBlockingFailed") != null){
		out.println("<h3>Blocking the account is unsuccessful</h3>");
		session.removeAttribute("isBlockingFailed");
	}
	
	if(session.getAttribute("isBlockingSuccessful") != null){
		out.println("<h3>Successfully blocked the account</h3>");
		session.removeAttribute("isBlockingSuccessful");
	}
	
	if(session.getAttribute("isChangingThePasswordSuccessfull") != null){
		out.println("<h3>Successfully changed the password of the user</h3>");
		session.removeAttribute("isChangingThePasswordSuccessfull");
	}
	
	if(session.getAttribute("bothPasswordsAreNotMatching") != null){
		out.println("<h3>New Password and Confirm password are mismatching!..</h3>");
		session.removeAttribute("bothPasswordsAreNotMatching");
	}
	if(session.getAttribute("isPasswordInValid") != null){
		out.println("<h3>Password entered is not matcing the criteria!..</h3>");
		session.removeAttribute("isPasswordInValid");
	}
	if(session.getAttribute("isOldPasswordAndNewPasswordAreSame") != null){
		out.println("<h3>Old password and new password are same!..</h3>");
		session.removeAttribute("isOldPasswordAndNewPasswordAreSame");
	}
	
%>
</div>

<div>
<h2>My account</h2>
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
<%

		String apiUrlForListOfAllUsers = "http://localhost:8080/BharathSocialMediaAppRestFullWebServ/webapi/user/allUsers";
		URL url = new URL(apiUrlForListOfAllUsers);
		HttpURLConnection connectionForListOfAllUsers = (HttpURLConnection) url.openConnection();
		connectionForListOfAllUsers.setRequestMethod("GET");
		connectionForListOfAllUsers.setRequestProperty("Accept", "application/json");
		
		// Read the response
		StringBuilder responseBuilderForListOfAllUsers = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(connectionForListOfAllUsers.getInputStream()))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	responseBuilderForListOfAllUsers.append(line);
		    }
		}
		connectionForListOfAllUsers.disconnect();
		List<Users> listOfAllUsersExceptMe = null;
		if(!responseBuilderForListOfAllUsers.toString().isEmpty()){
			ObjectMapper objectMapperForListOfFollowers = new ObjectMapper();
			objectMapperForListOfFollowers.registerModule(new JavaTimeModule());
			listOfAllUsersExceptMe = objectMapperForListOfFollowers.readValue(responseBuilderForListOfAllUsers.toString(), new TypeReference<List<Users>>(){});
		}
		
		if(listOfAllUsersExceptMe == null){
			MainCentralizedResource.LOGGER.warning("Something went wrong in fetching all the users");
			out.println("<h3>Something went wrong try Logout and login again!</h3>");
			return;
		}
		
		listOfAllUsersExceptMe.removeIf(e -> e.getUserId_() == users.getUserId_());
		
		if(listOfAllUsersExceptMe.isEmpty()){
			MainCentralizedResource.LOGGER.warning("You are the only user for this application");
			out.println("<h3>You are the only user for this application</h3>");
			return;
		}
%>
<div>
<h2>Users</h2>
<table border="1" width="90%">  
<tr>
<th>User id</th>
<th>User name</th>
<th>Change Role</th>
<th>Remove Account</th>
<th>Block/Unblock Account</th>
<th>Change Password for Account</th>
</tr>

<%
		
		UsersService usersServiceImpl = UsersServiceImpl.getInstance();
		
		for(Users user : listOfAllUsersExceptMe){
			out.println(
					"<tr>"
					+"<td>"+user.getUserId_()+"</td>"
					+"<td>"+user.getUserName_()+"</td>"
					+"<td>"+(usersServiceImpl.isAdminCheck(user.getUserId_()) ? "<a href=\"ChangeRoleToUserServlet?userId="+user.getUserId_()+"\">Change To User</a>": "<a href=\"ChangeRoleToAdminServlet?userId="+user.getUserId_()+"\">Change To Admin</a>")+"</td>"
					+"<td><a href=\"RemoveAccountServlet?userId="+user.getUserId_()+"\">Remove</a></td>"
					+"<td>"+(usersServiceImpl.isUserBlocked(user.getUserId_()) ? "<a href=\"UnblockUserServlet?userId="+user.getUserId_()+"\">UnBlock</a>" : "<a href=\"BlockUserServlet?userId="+user.getUserId_()+"\">Block</a>")+"</td>"
					+"<td><a href=\"changePasswordAdmin.jsp?targetUserId="+user.getUserId_()+"\">Change Password</a></td>"
					+"</tr>"
					);
		}	
	
%>

</table>
</div>
</body>
</html>