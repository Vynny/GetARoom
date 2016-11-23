# GetARoom

How to start the GetARoom application locally
---

1. In src/main/resources/assets/js/app.js, make sure apisrc is set to 'http://localhost:8080'
2. Run `mvn clean install` to build your application
3. Create MySQL/MariaDB database from MySQL/dump.sql, configure config.yml to reflect your database information
	3.1. config.yml - url: jdbc:mysql://localhost/DATABASE_NAME
4. Start application with `java -jar target/getaroom-1.0-SNAPSHOT.jar server config.yml`
5. Access the application at `http://localhost:8080`

Hosted Service
---

1. The application may be accessed at http://getaroom.lvain.com
2. Username: user, Password: user
3. Username: user2, Password: user


Dynamic Configuration
---

1. Calendar timerange - src/main/resources/assets/js/services.js, RoomService
2. Maximum amount of reservable timeslots per reservation - src/main/resources/assets/js/services.js, ReservationService
3. Maximum amount of reservations a user can have active - config.yml, maxActiveReservations

Health Check
---

To see your applications health enter url `http://localhost:8081/healthcheck`
