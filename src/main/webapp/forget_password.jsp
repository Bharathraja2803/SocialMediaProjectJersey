<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Forget Password</title>
</head>
<body>
<div>
<h1>ABC Social Media</h1>
<h2><a href="index.jsp">LoginPage</a></h2>
</div>
<div>
<%
	if(session.getAttribute("isEmailInvaid") != null){
		out.println("<h3>Invalid Email id!</h3>");
		session.removeAttribute("isEmailInvaid");
	}

	if(session.getAttribute("isForgetPassordUnsuccessfull") != null){
		out.println("<h3>Something went wrong try again!</h3>");
		session.removeAttribute("isForgetPassordUnsuccessfull");
	}
	
	if(session.getAttribute("isDobInvalid") != null){
		out.println("<h3>Date of birth is invalid!</h3>");
		session.removeAttribute("isDobInvalid");
	}
%>
</div>
<div>
<form action="change_password.jsp" method="post">
<table border="1">
<tr>
<td><label for="mailid">Mail id: </label></td>
<td><input name="mailid" id="mailid" type="text"></td>
</tr>
<tr>
<td><label for="dob">Date of birth: </label></td>
<td><input type="date" id="dob" name="dob"></td>
</tr>
<tr>
<td colspan="2"><input type="submit"></td>
</tr>
</table>
</form>
</div>
</body>
</html>