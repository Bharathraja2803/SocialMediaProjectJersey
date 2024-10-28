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

import com.bharath.model.Users;
import com.bharath.service.UsersService;
import com.bharath.service.UsersServiceImpl;
import com.bharath.MainCentralizedResource;

/**
 * This filter will validate the new password and confirm password while changing the password of the user
 * @author bharath-22329
 *
 */
public class ChangePasswordValidateFilter implements Filter {
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		String newPassword = request.getParameter("newPassword");
		String confirmPassword = request.getParameter("confirmPassword");
		HttpSession session = ((HttpServletRequest) request).getSession();
		
		UsersService usersServiceImpl = UsersServiceImpl.getInstance();
		
		boolean isNewPasswordAndConfirmPasswordMatching = usersServiceImpl.isNewAndConfirmPasswordMatching(newPassword, confirmPassword);
		
		if(!isNewPasswordAndConfirmPasswordMatching){
			RequestDispatcher rd = request.getRequestDispatcher("change_password.jsp");
			session.setAttribute("bothPasswordsAreNotMatching", "y");
			rd.forward(request, response);
			return;
		}
		
		boolean isPasswordMatchingValid = usersServiceImpl.isValidPassword(newPassword);
		
		if(!isPasswordMatchingValid){
			RequestDispatcher rd = request.getRequestDispatcher("change_password.jsp");
			session.setAttribute("isPasswordInValid", "y");
			rd.forward(request, response);
			return;
		}
		
		Users users = (Users) session.getAttribute("userDetailsWhoFogetPasword");
		
		if(users == null){
			MainCentralizedResource.LOGGER.warning("Something went wrong in geting the passsword forgotten user details!");
            session.setAttribute("isForgetPassordUnsuccessfull", "y");
            RequestDispatcher rd = request.getRequestDispatcher("forget_password.jsp");
            rd.forward(request, response);
			return;
		}
		
		boolean isOldPasswordAndNewPasswordAreSame = usersServiceImpl.isOldPasswordAndNewPasswordAreSame(users, newPassword);
		
		if(isOldPasswordAndNewPasswordAreSame){
			RequestDispatcher rd = request.getRequestDispatcher("change_password.jsp");
			session.setAttribute("isOldPasswordAndNewPasswordAreSame", "y");
			rd.forward(request, response);
			return;
		}
		
		MainCentralizedResource.LOGGER.info("New Password and Confirm password meets the criteria");
		chain.doFilter(request, response);
	}

}
