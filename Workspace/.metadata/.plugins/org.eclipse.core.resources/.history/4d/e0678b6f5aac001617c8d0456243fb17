package com.soen343.client;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.codahale.metrics.annotation.Timed;
import com.soen343.core.User;
import com.soen343.db.UserTDG;
import com.soen343.idmappers.UserIdentityMap;
import com.soen343.mappers.UserMapper;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
public class UserController {
	
	private UserMapper userMapper;
	private UserTDG userTDG;

    public UserController(UserTDG userTDG) {
    	this.userTDG = userTDG;
    	this.userMapper = new UserMapper(userTDG);
    }
    
    @GET
    @Path("/")
    @Timed
    public List<User> getAllUsers() {
    	List<User> users = userMapper.getAll();
    	if (users != null) {
            return users;
        } else {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    public User getUserByName(@PathParam("username") String username) {
    	return userMapper.get(username);
    }
}
