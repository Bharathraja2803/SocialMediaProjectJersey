package com.bharath.api;



import com.bharath.MainCentralizedResource;
import com.bharath.model.Post;
import com.bharath.model.Users;
import com.bharath.service.FollowerService;
import com.bharath.service.FollowerServiceImpl;
import com.bharath.service.PostService;
import com.bharath.service.PostServiceImpl;
import com.bharath.service.UsersService;
import com.bharath.service.UsersServiceImpl;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("user/{userId}/post")
public class PostApi {
	private static PostService postServiceImpl = PostServiceImpl.getInstance();
	private static UsersService usersServiceImpl = UsersServiceImpl.getInstance();
	private static FollowerService followerServiceImpl = FollowerServiceImpl.getInstance();
	
	@GET
	@Path("/targetUser/{targetUserId}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Post> getAllPostByUser(@PathParam("targetUserId") int targetUserId){
		List<Post> allPostOfUser = postServiceImpl.getAllUserPost(targetUserId);
		
		if(allPostOfUser == null){
			MainCentralizedResource.LOGGER.warning("Something went wrong in fetching all the post of the user: "+targetUserId);
		}else{
			MainCentralizedResource.LOGGER.info("Successfully fetched all post by the user: "+targetUserId);
		}
		return allPostOfUser;
	}
	
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Post> getAllPost(@PathParam("userId") int userId) {
        Users users = usersServiceImpl.getUser(userId);
        String role = users.getRoles_();
        if (role.equals("admin")) {
            List<Post> allPost = postServiceImpl.getAllPostInSocialMedia();
            if (allPost == null) {
                MainCentralizedResource.LOGGER.warning("No post posted by any user");
                return null;
            }
            return allPost;

        } else {
            List<Users> listOfAllFollowers = followerServiceImpl.getAllFollowedUsers(userId);
            List<Post> allFollowersPost = new ArrayList<>();
            if (listOfAllFollowers == null) {
                MainCentralizedResource.LOGGER.warning("Something went wrong in fetching the followers!");
            } else {
                for (Users userinList : listOfAllFollowers) {
                    List<Post> allMyPostPerUser = postServiceImpl.getAllUserPost(userinList.getUserId_());
                    if (allMyPostPerUser == null) {
                        MainCentralizedResource.LOGGER.warning("Something went wrong in fetching post of follower "+ userinList.getUserId_());
                        continue;
                    }
                    allFollowersPost.addAll(allMyPostPerUser);
                }
            }


            List<Post> myPosts = postServiceImpl.getAllUserPost(userId);
            if (myPosts == null) {
                MainCentralizedResource.LOGGER.warning("Something went wrong in fetching my post");
            } else {
                allFollowersPost.addAll(myPosts);
            }

            if (allFollowersPost.isEmpty()) {
                MainCentralizedResource.LOGGER.warning("No post posted by any follower");
                return null;
            }

            return allFollowersPost;
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPost(@PathParam("userId") int userId, String postContent){
    	postContent = postContent.substring(1, postContent.length()-1);
    	boolean isPostCreatedSuccessfully = postServiceImpl.createPost(postContent, userId);
        if(isPostCreatedSuccessfully){
        	MainCentralizedResource.LOGGER.info("Post created successfully by the user: "+userId);
        	return Response.ok().build();
        }else{
        	MainCentralizedResource.LOGGER.warning("Post creation was unSuccessful by the user: "+ userId);
        	return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }


    @GET
    @Path("/{postId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Post getPost(@PathParam("postId") int postId){
        Post post = postServiceImpl.getPostByPostId(postId);
        if(post == null){
            MainCentralizedResource.LOGGER.warning("Post id "+postId+" is invalid");
            return null;
        }
        MainCentralizedResource.LOGGER.info("Post id "+postId+"is valid");
        return post;
    }

    @DELETE
    @Path("/{postId}")
    @Produces(MediaType.APPLICATION_XML)
    public Response deletePost(@PathParam("postId") int postId){
        boolean isPostDeleted = postServiceImpl.removePost(postId);
        
        if(isPostDeleted){
        	MainCentralizedResource.LOGGER.info("Post with post id: "+postId+" successfully deleted");
        	return Response.ok().build();
        }
        MainCentralizedResource.LOGGER.warning("Post with post id: "+postId+" deletion failed");
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

//    @PUT
//    @Path("/post")
//    @Consumes(MediaType.APPLICATION_XML)
//    @Produces(MediaType.APPLICATION_XML)
//    public Post updatePost(Post post){
//        PostDao postDaoImpl = PostDaoImpl.getInstance();
//        boolean isUpdateSuccessful = postDaoImpl.updatePost(post);
//
//        if(!isUpdateSuccessful){
//            MainCentralizedResource.LOGGER.warning("Failed to update the post");
//            return null;
//        }
//        Post postNew = postDaoImpl.getPost(post.getPostId());
//        if(postNew == null){
//            MainCentralizedResource.LOGGER.warning("Post id "+post.getPostId()+" is invalid");
//            return null;
//        }
//        return postNew;
//    }
}
