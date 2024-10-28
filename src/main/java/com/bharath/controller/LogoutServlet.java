package com.bharath.controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * This servlet is used to logout the logged in user
 * @author bharath-22329
 *
 */
public class LogoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		HttpSession session = request.getSession();
		if(session.getAttribute("loggedinUser") == null){
			session.setAttribute("isNotLoggedIn", "y");
		}else{

			session.removeAttribute("loggedinUser");
			session.removeAttribute("postsToView");
			Cookie cUser = new Cookie("loggedinUser", null);
		    Cookie cPost = new Cookie("postsToView", null);
		    cUser.setMaxAge(0);
		    cPost.setMaxAge(0);
		    response.addCookie(cUser);
		    response.addCookie(cPost);
			session.invalidate();
		}
		RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
        rd.forward(request, response);
	}

}
