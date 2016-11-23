package com.soen343.auth;

import io.dropwizard.auth.*;
import com.soen343.core.User;

public class GetARoomAuthorizer implements Authorizer<User> {
    @Override
    public boolean authorize(User user, String role) {
        return user != null;
    	//return user.getName().equals("good-guy") && role.equals("ADMIN");
    }
}