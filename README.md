# GetARoom

## About
GetARoom is a room reservation system built with Dropwizard and AngularJS. It was built as a SOEN 343 Project in fall 2016.


## How to start the GetARoom application locally

1. In src/main/resources/assets/js/app.js, make sure apisrc is set to 'http://localhost:8080'
2. Run `mvn clean install` to build your application
3. Create MySQL/MariaDB database from MySQL/dump.sql, configure config.yml to reflect your database information
	3.1. config.yml - url: jdbc:mysql://localhost/DATABASE_NAME
4. Start application with `java -jar target/getaroom-1.0-SNAPSHOT.jar server config.yml`
5. Access the application at `http://localhost:8080`


## Dynamic Configuration

1. Calendar timerange - src/main/resources/assets/js/services.js, RoomService
2. Maximum amount of reservable timeslots per reservation - src/main/resources/assets/js/services.js, ReservationService
3. Maximum amount of reservations a user can have active - config.yml, maxActiveReservations


## Team Members

- Mihai Damaschin
- Jesus Imery
- Meetaz Alshbli
- Samuel Dufresne
- Samuel Campbell
- Sylvain Czyzewski
- Mark Snidal