package com.soen343.auth;

import java.util.Optional;
import com.soen343.core.*;
import com.soen343.client.UserController;
import io.dropwizard.auth.*;
import io.dropwizard.auth.basic.*;

public class GetARoomAuthenticator implements Authenticator<BasicCredentials, User>{
	private UserController controller;
	
	public GetARoomAuthenticator(UserController controller) {
		this.controller = controller;
	}
	
	@Override
	public Optional<User> authenticate(BasicCredentials credentials) throws AuthenticationException {
		User session = controller.getUserByName(credentials.getUsername());
		
		if (session != null && session.getPassword().equals(credentials.getPassword())) {
			return Optional.of(session);
		}
		return Optional.empty();
	}
}
