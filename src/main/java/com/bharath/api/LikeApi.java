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
import com.bharath.model.Like;
import com.bharath.service.LikeService;
import com.bharath.service.LikeServiceImpl;

@Path("user/{userId}/post/{postId}/like")
public class LikeApi {
	private static LikeService likeServiceImpl = LikeServiceImpl.getInstance();
	
	@DELETE
	@Path("/{likeId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteLike(@PathParam("likeId") int likeId){
		boolean isUnlikeSuccessful = likeServiceImpl.unlikeThePost(likeId);
		if(isUnlikeSuccessful){
			MainCentralizedResource.LOGGER.info("Successfully removed the like: "+likeId);
			return Response.ok().build();
		}else{
			MainCentralizedResource.LOGGER.info("Failed in remnoving like: "+likeId);
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Like getLike(@PathParam("userId") int userId, @PathParam("postId") int postId){
		Like like = likeServiceImpl.getLike(userId, postId);
		if(like == null){
			MainCentralizedResource.LOGGER.info("User "+userId+" haven't liked the post "+postId);
		}else{
			MainCentralizedResource.LOGGER.info("User "+userId+" have liked the post "+postId);
		}
		return like;
	}
	
	@GET
	@Path("/allLikesPerPost")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Like> getAllLikesForPost(@PathParam("postId") int postId){
		List<Like> likes = likeServiceImpl.getAllLikesForThePost(postId);
		if(likes == null){
			MainCentralizedResource.LOGGER.info("No likes for the post: " + postId);
		}else{
			MainCentralizedResource.LOGGER.info("Successfully retrieved all likes for the post: " + postId);
		}
		return likes;
	}
	
	@POST
	public Response likeThePost(@PathParam("userId")int userId, @PathParam("postId") int postId){
		boolean isLikeSuccessful = likeServiceImpl.likeThePost(userId, postId);
		if(isLikeSuccessful){
			MainCentralizedResource.LOGGER.info(String.format("User %d liked the post %d", userId, postId));
			return Response.ok().build();
		}
		MainCentralizedResource.LOGGER.info(String.format("User %d liking the post %d was failed", userId, postId));
		return Response.status(Response.Status.BAD_REQUEST).build();
	}
	
	@GET
	@Path("/countPerPost")
	@Produces(MediaType.APPLICATION_JSON)
	public Integer countOfLikesForThePost(@PathParam("userId") int userId, @PathParam("postId") int postId){
		try{
			int count = likeServiceImpl.countOfLikesPerPost(postId);
			return new Integer(count);
		}catch(Exception e){
			MainCentralizedResource.LOGGER.severe(e.toString());
			return new Integer(0);
		}
		
	}

    @DELETE
    @Path("/deleteLikeByPost")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteLikeByPost(@PathParam("postId") int postId){
        boolean isLikesForPostDeleted = likeServiceImpl.removeAllLikeForSpecificPost(postId);
        
        if(isLikesForPostDeleted){
        	MainCentralizedResource.LOGGER.info("Likes for the Post with post id: "+postId+" successfully deleted");
        	return Response.ok().build();
        }
        MainCentralizedResource.LOGGER.warning("Likes for the Post with post id: "+postId+" deletion failed");
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
    
    @DELETE
    @Path("/deleteLikeByUser/{targetUserId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteLikeByUser(@PathParam("targetUserId") int targetUserId){
        boolean isLikesForUserDeleted = likeServiceImpl.removeAllLikesByUserId(targetUserId);
        
        if(isLikesForUserDeleted){
        	MainCentralizedResource.LOGGER.info("Likes by the User with user id: "+targetUserId+" successfully deleted");
        	return Response.ok().build();
        }
        MainCentralizedResource.LOGGER.warning("Likes by the User with user id: "+targetUserId+" deletion failed");
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
    
}
