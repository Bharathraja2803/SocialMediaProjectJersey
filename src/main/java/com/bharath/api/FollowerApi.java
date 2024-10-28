package com.bharath.api;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bharath.MainCentralizedResource;
import com.bharath.model.Users;
import com.bharath.service.FollowerService;
import com.bharath.service.FollowerServiceImpl;

@Path("/user/{userId}/follower")
public class FollowerApi {
	private static FollowerService followerServiceImpl = FollowerServiceImpl.getInstance();
	
	@GET
	@Path("/countFollowers")
	@Produces(MediaType.APPLICATION_JSON)
	public int getNumberOfFollowers(@PathParam("userId") int userId){
		int count = followerServiceImpl.countOfUsersWeAreFollowing(userId);
		if(count == -1){
			MainCentralizedResource.LOGGER.warning("Something went wrong in fetching the count of all followers");
		}else{
			MainCentralizedResource.LOGGER.info("Successfully fetched the count of all followers");
		}
		return count;
	}
	
	@GET
	@Path("/countFollowing")
	@Produces(MediaType.APPLICATION_JSON)
	public int getNumberOfUsersFollowingUs(@PathParam("userId") int userId){
		int count = followerServiceImpl.countOfUsersWhoAreFollowingUs(userId);
		if(count == -1){
			MainCentralizedResource.LOGGER.warning("Something went wrong in fetching the count of all users following us");
		}else{
			MainCentralizedResource.LOGGER.info("Successfully fetched the count of all users following us");
		}
		return count;
	}
	
	@GET
	@Path("/listOfAllFollowers")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Users> getAllFollowers(@PathParam("userId") int userId){
		List<Users> allFollowers = followerServiceImpl.getAllFollowedUsers(userId);
		if(allFollowers == null){
			MainCentralizedResource.LOGGER.warning("Something went wrong in fetching all the followers for the user: "+userId);
		}else{
			MainCentralizedResource.LOGGER.info("Successfully fetched all the followers for the user: "+userId);
		}
		return allFollowers;
	}
	
	@GET
	@Path("/listOfAllFollowing")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Users> getAllUsersFollowingUs(@PathParam("userId") int userId){
		List<Users> allFollowing = followerServiceImpl.getAllUsersFollowingYou(userId);
		
		if(allFollowing == null){
			MainCentralizedResource.LOGGER.warning("Something went wrong in fetching all the users who are following us for user : "+userId);
		}else{
			MainCentralizedResource.LOGGER.info("Successfully fetched all the users who are following us for the user: "+userId);
		}
		
		return allFollowing;
	}
	
	@GET
	@Path("/listOfNotFollowedUsers")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Users> getAllUsersWhoWeAreNotFollowing(@PathParam("userId") int userId){
		List<Users> allFollowing = followerServiceImpl.getAllNotFollowedUsers(userId);
		
		if(allFollowing == null){
			MainCentralizedResource.LOGGER.warning("Something went wrong in fetching all the users who we are not following for user : "+userId);
		}else{
			MainCentralizedResource.LOGGER.info("Successfully fetched all the users who we are not following for the user: "+userId);
		}
		
		return allFollowing;
	}
	
	@POST
	@Path("/{userIdToFollow}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response followUser(@PathParam("userId") int userId, @PathParam("userIdToFollow") int userIdToFollow){
		boolean isFollowSuccessful = followerServiceImpl.followUser(userId, userIdToFollow);
		if(isFollowSuccessful){
			MainCentralizedResource.LOGGER.info("User "+userId+ " follow request user "+userIdToFollow+" was successful");
			return Response.ok().build();
		}else{
			MainCentralizedResource.LOGGER.info("User "+userId+ " follow request user "+userIdToFollow+" was failed");
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}
	
	@DELETE
	@Path("/{userIdToUnFollow}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response unFollowUser(@PathParam("userId") int userId, @PathParam("userIdToUnFollow") int userIdToUnFollow){
		boolean isUnFollowSuccessful = followerServiceImpl.unFollowUser(userId, userIdToUnFollow);
		if(isUnFollowSuccessful){
			MainCentralizedResource.LOGGER.info("User "+userId+ " unfollow request user "+userIdToUnFollow+" was successful");
			return Response.ok().build();
		}else{
			MainCentralizedResource.LOGGER.info("User "+userId+ " unfollow request user "+userIdToUnFollow+" was failed");
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}
}
