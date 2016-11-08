package com.soen343;

import org.skife.jdbi.v2.DBI;

import com.soen343.client.RoomController;
import com.soen343.db.RoomTDG;
import com.soen343.client.ReservationController;
import com.soen343.db.ReservationTDG;


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
		// TODO: application initialization
		bootstrap.addBundle(new AssetsBundle("/assets/", "/", "index.html"));
	}

	@Override
	public void run(final GetARoomConfiguration configuration, final Environment environment) {
		// TODO: implement application
		((DefaultServerFactory) configuration.getServerFactory()).setJerseyRootPath("/api/*");

		final DBIFactory factory = new DBIFactory();
		final DBI jdbi = factory.build(environment, configuration.getDataSourceFactory(), "database");
		
		final RoomTDG roomTDG = jdbi.onDemand(RoomTDG.class);
		final ReservationTDG reservationTDG = jdbi.onDemand(ReservationTDG.class);
		
		ReservationController reservationController = new ReservationController(reservationTDG);
		RoomController roomController = new RoomController(roomTDG);
		environment.jersey().register(reservationController);
	    environment.jersey().register(roomController);
	}

}
