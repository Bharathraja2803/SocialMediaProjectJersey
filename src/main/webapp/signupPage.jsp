<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Signup Page</title>
</head>
<body>
<div>
<h1>ABC Social media</h1>
<h2><a href="index.jsp">LoginPage</a></h2>
</div>
<div>
<%
	if(session.getAttribute("isPasswordInValid") != null){
		out.println("<h3>Entered password does not meet the criteria</h3>");
		session.removeAttribute("isPasswordInValid");
	}

	if(session.getAttribute("isDobInValid") != null){
		out.println("<h3>Entered date of birth is not vaild</h3>");
		session.removeAttribute("isDobInValid");
	}
	
	if(session.getAttribute("isMailIdAlreadyExists") != null){
		out.println("<h3>Email id already exists</h3>");
		session.removeAttribute("isMailIdAlreadyExists");
	}
	
	if(session.getAttribute("isAccountNotCreated") != null){
		out.println("Something went wrong in creating the user. Try again!..");
		session.removeAttribute("isAccountNotCreated");
	}
	
	if(session.getAttribute("isUserNameInvalid") != null){
		out.println("Username is invalid");
		session.removeAttribute("isUserNameInvalid");
	}
	
	if(session.getAttribute("isEmailIdInValid") != null){
		out.println("Entered email id is invalid!");
		session.removeAttribute("isEmailIdInValid");
	}
%>
</div>
<div>
<form action="CreateUserServlet" method="post">
<table border="1">
<tr>
<td><label for="username">User name: </label></td>
<td><input type="text" name="username" id="username"></td>
</tr>
<tr>
<td><label for="password">Password: </label></td>
<td><input type="password" name="password" id="password"></td>
</tr>
<tr>
<td><label for="dob">Enter Date of birth: </label></td>
<td><input type="date" name="dob" id="dob"></td>
</tr>
<tr>
<td><label for="mailid">Enter email id: </label></td>
<td><input type="text" name="mailid" id="mailid"></td>
</tr>
<tr>
<td colspan="2"><input type="submit"></td>
</tr>
</table>
</form>
</div>
<br>
<div>
<h3>Password Criteria:</h3>
<p>
<ol>
<li>Minimum length of 8 characters</li>
<li>Maximum length of 20 characters</li>
<li>At least one lowercase letter</li>
<li>At least one uppercase letter</li>
<li>At least one special character(Allowed special Characters: @$!%*?&)</li>
<li>At least one number</li>
</ol>
</p>
</div>

<div>
<h3>Age Criteria:</h3>
<p>
<ol>
<li>Your age should be greater than 18</li>
<li>Your age should be less than 125</li>
</ol>
</p>
</div>
</body>
</html>