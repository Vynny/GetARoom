package com.soen343.auth;

import java.util.Optional;

import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.JwtContext;

import com.soen343.core.*;
import com.soen343.client.UserController;
import io.dropwizard.auth.*;

public class GetARoomAuthenticator implements Authenticator<JwtContext, User>{
	private UserController controller;
	
	public GetARoomAuthenticator(UserController controller) {
		this.controller = controller;
	}
	
	@Override
	public Optional<User> authenticate(JwtContext context) throws AuthenticationException {
        try {
            final String subject = context.getJwtClaims().getSubject();
            User session = controller.getUser(Integer.parseInt(subject));
            
            if (session != null) {
            	return Optional.of(session);
            }
            return Optional.empty();
        }
        catch (MalformedClaimException e) {
        	return Optional.empty();
        }
	}
}
