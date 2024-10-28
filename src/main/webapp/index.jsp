<%@page import="com.bharath.model.Users"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Login Page</title>
</head>
<body>
<h1>ABC Social Media</h1>
<div>
<%
	response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
	if(session.getAttribute("invalidLogin") != null){
		out.println("<h3>Userid or Password is invalid!</h3>");
		session.removeAttribute("invalidLogin");
	}

	if(session.getAttribute("inValidUserId") != null){
		out.println("<h3>Userid is invalid!</h3>");
		session.removeAttribute("inValidUserId");
	}
	
	if(session.getAttribute("inCorrectPassword") != null){
		out.println("<h3>Password is incorrect!</h3>");
		session.removeAttribute("inCorrectPassword");
	}
	
	if(session.getAttribute("isAccountBlocked") != null){
		out.println("<h3>Your Account is blocked!</h3>");
		session.removeAttribute("isAccountBlocked");
	}

	if(session.getAttribute("newUserCreatedId") != null){
		int userid = (Integer)session.getAttribute("newUserCreatedId");
		out.println("<h3>User Account created with id: "+userid+" </h3>");
		session.removeAttribute("newUserCreatedId");
	}
	
	if(session.getAttribute("isNotLoggedIn") != null){
		out.println("<h3>You have not logged in yet</h3>");
		session.removeAttribute("isNotLoggedIn");
	}
	
	if(session.getAttribute("somethingWentWrong") != null){
		out.println("<h3>Something went wrong</h3>");
		session.removeAttribute("somethingWentWrong");
	}
	
	if(session.getAttribute("isChangingThePasswordSuccessfull") != null){
		Users users = (Users)session.getAttribute("userDetailsWhoFogetPasword");
		out.println("<h3>Password got changed for user id: "+users.getUserId_()+" </h3>");
		session.removeAttribute("isChangingThePasswordSuccessfull");
		session.removeAttribute("userDetailsWhoFogetPasword");
	}
	
%>
</div>

<div>
<table border="1">

<form action="post.jsp" method="post">

<tr>
<td><label for="userid">Userid: </label></td>
<td><input type="text" name="userid" id="userid" placeholder="userid"></td>
</tr>

<tr>
<td><label for="password">Password: </label></td>
<td><input type="password" name="password" id="password" placeholder="password"></td>
</tr>

<tr>
<td colspan="2"><input type="submit" value="Login"></td>
</tr>
</form>

<form action="signupPage.jsp" method="get">
<tr>
<td colspan="2"><input type="submit" value="Sign up"></td>
</tr>
</form>
<tr>

<td colspan="2"><a href="forget_password.jsp">forget password?</a></td>
</tr>
</table>

</div>
</body>
</html>
