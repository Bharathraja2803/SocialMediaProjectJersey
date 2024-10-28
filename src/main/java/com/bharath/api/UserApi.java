package com.bharath.api;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bharath.MainCentralizedResource;
import com.bharath.model.Users;
import com.bharath.service.UsersService;
import com.bharath.service.UsersServiceImpl;


@Path("user")
public class UserApi {
	private static UsersService usersServiceImpl = UsersServiceImpl.getInstance();
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Users addUser(Users users){
		usersServiceImpl = UsersServiceImpl.getInstance();
		int userIdCreated = usersServiceImpl.addNewUser(users);
		users.setUserId_(userIdCreated);
		return users;
	}
	
	@GET
	@Path("/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Users getUserByUserId(@PathParam("userId") int userId){
		Users users = usersServiceImpl.getUser(userId);
		return users;
	}
	
	@PUT
	@Path("/{userId}")
	public Response updateUserPassword(@PathParam("userId") int userId, Users users){
		boolean isPasswordChangeSuccessful = usersServiceImpl.resetOwnPassword(userId, users.getPassword_());
		if(!isPasswordChangeSuccessful){
			return Response.status(Response.Status.BAD_REQUEST).entity("Something went wrong in changing the password of the user").build();
		}
		return Response.ok().build();
	}

	@GET
	@Path("/allUsers")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Users> getAllUsersInSocialMedia(){
		List<Users> allUsers = usersServiceImpl.getAllUsers();
		
		if(allUsers == null){
			MainCentralizedResource.LOGGER.warning("Something went wrong in fetching all the users in the Social media");
		}else{
			MainCentralizedResource.LOGGER.info("Successfully fetched all the users in the Social media");
		}
		
		return allUsers;
	}
	
	@PUT
	@Path("/{userId}/changeRoleToUser/{targetUserId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response changeRoleToUser(@PathParam("userId") int userId, @PathParam("targetUserId") int targetUserId){
		boolean isUserRoleUpdated = usersServiceImpl.updateTheRoleOfTheUser(userId, targetUserId, "user");
		if(isUserRoleUpdated){
			MainCentralizedResource.LOGGER.info("Successfully updated the role of user "+targetUserId+" to user role by user: "+userId);
			return Response.ok().build();
		}else{
			MainCentralizedResource.LOGGER.info("Failure in updating the role of user "+targetUserId+" to user role by user: "+userId);
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}
	
	@PUT
	@Path("/{userId}/changeRoleToAdmin/{targetUserId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response changeRoleToAdmin(@PathParam("userId") int userId, @PathParam("targetUserId") int targetUserId){
		boolean isUserRoleUpdated = usersServiceImpl.updateTheRoleOfTheUser(userId, targetUserId, "admin");
		if(isUserRoleUpdated){
			MainCentralizedResource.LOGGER.info("Successfully updated the role of user "+targetUserId+" to admin role by user: "+userId);
			return Response.ok().build();
		}else{
			MainCentralizedResource.LOGGER.info("Failure in updating the role of user "+targetUserId+" to admin role by user: "+userId);
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}
	
	@DELETE
	@Path("/{userId}/removeAccount/{targetUserId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeUserAccount(@PathParam("userId")int userId, @PathParam("targetUserId")int targetUserId){
		boolean isUserRemoved = usersServiceImpl.removeUser(userId, targetUserId);
		if(isUserRemoved){
			MainCentralizedResource.LOGGER.info("User "+userId+" have removed the user "+targetUserId);
			return Response.ok().build();
		}else{
			MainCentralizedResource.LOGGER.info("User "+userId+" have failed removing the user "+targetUserId);
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}
	
	@PUT
	@Path("/{userId}/blockUser/{targetUserId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response blockUser(@PathParam("userId") int userId, @PathParam("targetUserId") int targetUserId){
		boolean isUserBlocked = usersServiceImpl.blockUser(userId, targetUserId);
		if(isUserBlocked){
			MainCentralizedResource.LOGGER.info("User "+userId+" blocked the user "+targetUserId);
			return Response.ok().build();
		}else{
			MainCentralizedResource.LOGGER.warning("Something went wrong in blocking the user "+targetUserId+" by user "+userId);
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}
	
	@PUT
	@Path("/{userId}/unBlockUser/{targetUserId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response unBlockUser(@PathParam("userId") int userId, @PathParam("targetUserId") int targetUserId){
		boolean isUserUnBlocked = usersServiceImpl.unBlockUser(userId, targetUserId);
		if(isUserUnBlocked){
			MainCentralizedResource.LOGGER.info("User "+userId+" unblocked the user "+targetUserId);
			return Response.ok().build();
		}else{
			MainCentralizedResource.LOGGER.warning("Something went wrong in unblocking the user "+targetUserId+" by user "+userId);
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}
	
}
