package com.bharath.api;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bharath.MainCentralizedResource;
import com.bharath.model.Comment;
import com.bharath.service.CommentService;
import com.bharath.service.CommentServiceImpl;



@Path("user/{userId}/post/{postId}/comment")
public class CommentApi {
	private static CommentService commentServiceImpl =  CommentServiceImpl.getInstance();
	
	@GET
	@Path("/allCommentsPerPost")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Comment> getAllCommentsPerPost(@PathParam("postId") int postId){
		List<Comment> comments = commentServiceImpl.getAllCommentsForThePost(postId);
		
		if(comments == null){
			MainCentralizedResource.LOGGER.info("No comments for the post : "+postId);
		}else{
			MainCentralizedResource.LOGGER.info(comments.size()+" comments for the post : "+postId);
		}
		
		return comments;
	}

    @DELETE
    @Path("/deleteCommentByPost")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCommentByPost(@PathParam("postId") int postId){
        boolean isCommentsForPostDeleted = commentServiceImpl.deleteAllCommentsForThePost(postId);
        
        if(isCommentsForPostDeleted){
        	MainCentralizedResource.LOGGER.info("Comments for the Post with post id: "+postId+" successfully deleted");
        	return Response.ok().build();
        }
        MainCentralizedResource.LOGGER.warning("Comments for the Post with post id: "+postId+" deletion failed");
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
    
    @DELETE
    @Path("/deleteCommentByUser/{targetUserId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCommentByUser(@PathParam("targetUserId") int targetUserId){
        boolean isCommentsForUserDeleted = commentServiceImpl.deleteAllCommentsForTheUser(targetUserId);
        
        if(isCommentsForUserDeleted){
        	MainCentralizedResource.LOGGER.info("Comments by the User with user id: "+targetUserId+" successfully deleted");
        	return Response.ok().build();
        }
        MainCentralizedResource.LOGGER.warning("Comments by the User with user id: "+targetUserId+" deletion failed");
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addComments(@PathParam("userId") int userId, @PathParam("postId") int postId, String comment){
    	comment = comment.substring(1, comment.length()-1);
    	boolean isCommentedSuccessful = commentServiceImpl.commentThePost(userId, postId, comment);
    	if(isCommentedSuccessful){
    		MainCentralizedResource.LOGGER.info("Commented the post "+postId+" by user "+userId);
    		return Response.ok().build();
    	}else{
    		MainCentralizedResource.LOGGER.warning("Commenting the post "+postId+" by user "+userId+" failed");
    		return Response.status(Response.Status.BAD_REQUEST).build();
    	}
    }
}
