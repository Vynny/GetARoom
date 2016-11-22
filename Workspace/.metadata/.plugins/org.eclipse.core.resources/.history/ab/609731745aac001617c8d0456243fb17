package com.soen343;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration.Dynamic;

import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.skife.jdbi.v2.DBI;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.soen343.client.ReservationController;
import com.soen343.client.RoomController;
import com.soen343.client.UserController;
import com.soen343.db.ReservationTDG;
import com.soen343.db.RoomTDG;
import com.soen343.db.UserTDG;
import com.soen343.auth.GetARoomAuthenticator;
import com.soen343.auth.GetARoomAuthorizer;
import com.soen343.core.User;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.server.DefaultServerFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import io.dropwizard.auth.*;
import io.dropwizard.auth.basic.*;
import io.dropwizard.auth.chained.*;
import io.dropwizard.auth.oauth.*;

public class GetARoomApplication extends Application<GetARoomConfiguration> {

	public static void main(final String[] args) throws Exception {
		new GetARoomApplication().run(args);
	}

	@Override
	public String getName() {
		return "GetARoom";
	}

	@Override
	public void initialize(final Bootstrap<GetARoomConfiguration> bootstrap) {
		bootstrap.addBundle(new AssetsBundle("/assets/", "/", "index.html"));
	}

	@Override
	public void run(final GetARoomConfiguration configuration, final Environment environment) {
		//Set root path to /api/*
		((DefaultServerFactory) configuration.getServerFactory()).setJerseyRootPath("/api/*");

		//Database Initialization
		final DBIFactory factory = new DBIFactory();
		environment.getObjectMapper().configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		final DBI jdbi = factory.build(environment, configuration.getDataSourceFactory(), "database");
		
		//TDG Initialization
		final RoomTDG roomTDG = jdbi.onDemand(RoomTDG.class);
		final ReservationTDG reservationTDG = jdbi.onDemand(ReservationTDG.class);
		final UserTDG userTDG = jdbi.onDemand(UserTDG.class);
		
		//Controller Initialization
		RoomController roomController = new RoomController(roomTDG);
		ReservationController reservationController = new ReservationController(reservationTDG);
		UserController userController = new UserController(userTDG);
				
		//Controller Registration
		environment.jersey().register(reservationController);
	    environment.jersey().register(roomController);
	    environment.jersey().register(userController);

	    //Authentication
	    environment.jersey().register(new AuthDynamicFeature(
	            new BasicCredentialAuthFilter.Builder<User>()
	                .setAuthenticator(new GetARoomAuthenticator(userController))
	                .setAuthorizer(new GetARoomAuthorizer())
	                .setRealm("mordor")
	                .buildAuthFilter()));
	    environment.jersey().register(RolesAllowedDynamicFeature.class);
	    environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));
	    
	    //Configure Cross Origin Resource Sharing
	    configureCors(environment);	    
	}
	
	private void configureCors(Environment environment) {
        Dynamic filter = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
        filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        filter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,PUT,POST,DELETE,OPTIONS");
        filter.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        filter.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        filter.setInitParameter("allowedHeaders", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
        filter.setInitParameter("allowCredentials", "true");
      }

}
