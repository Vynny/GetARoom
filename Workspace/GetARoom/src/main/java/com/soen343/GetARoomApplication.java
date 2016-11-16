package com.soen343;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration.Dynamic;

import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.skife.jdbi.v2.DBI;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.soen343.client.ReservationController;
import com.soen343.client.RoomController;
import com.soen343.db.ReservationTDG;
import com.soen343.db.RoomTDG;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.server.DefaultServerFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

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
		
		//Controller Initialization
		RoomController roomController = new RoomController(roomTDG);
		ReservationController reservationController = new ReservationController(reservationTDG);
		
		//Controller Registration
		environment.jersey().register(reservationController);
	    environment.jersey().register(roomController);
	    
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
