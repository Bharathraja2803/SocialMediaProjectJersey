package com.bharath.filter;

import java.io.IOException;


import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


import com.bharath.service.UsersService;
import com.bharath.service.UsersServiceImpl;



/**
 * This filter will validate all  the fields entered by user during their sign up
 * @author bharath-22329
 *
 */
public class ValidateUserDetailsForSignUpFilter implements Filter {

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpSession session = ((HttpServletRequest) request).getSession();
		
		String username = request.getParameter("username");
		UsersService usersServiceImpl = UsersServiceImpl.getInstance();
		
		boolean isValidUserName = usersServiceImpl.isValidUserName(username); 
		
		if(!isValidUserName){
			RequestDispatcher rd = request.getRequestDispatcher("signupPage.jsp");
			session.setAttribute("isUserNameInvalid", "y");
			rd.forward(request, response);
			return;
		}
		
		String password = request.getParameter("password");
		
		boolean isValidPassword = usersServiceImpl.isValidPassword(password); 
				
		if(!isValidPassword){
			RequestDispatcher rd = request.getRequestDispatcher("signupPage.jsp");
			session.setAttribute("isPasswordInValid", "y");
			rd.forward(request, response);
			return;
		}
		
		
		String dob = request.getParameter("dob");
		
		boolean isDobValid = usersServiceImpl.isDobValid(dob);
		
		if(!isDobValid){
			RequestDispatcher rd = request.getRequestDispatcher("signupPage.jsp");
			session.setAttribute("isDobInValid", "y");
			rd.forward(request, response);
			return;
		}
	
		
		String emailid = request.getParameter("mailid");
		
		boolean isValidEmailId = usersServiceImpl.isEmailIdValid(emailid);
		
		if(!isValidEmailId){
			RequestDispatcher rd = request.getRequestDispatcher("signupPage.jsp");
			session.setAttribute("isEmailIdInValid", "y");
			rd.forward(request, response);
			return;
		}
		
		boolean isEmailIdExits = usersServiceImpl.isEmailIdExists(emailid);
		if(isEmailIdExits){
			RequestDispatcher rd = request.getRequestDispatcher("signupPage.jsp");
			session.setAttribute("isMailIdAlreadyExists", "y");
			rd.forward(request, response);
			return;
		}
		
		chain.doFilter(request, response);
	}

}
