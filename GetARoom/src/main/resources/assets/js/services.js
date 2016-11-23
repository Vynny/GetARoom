angular.module('getaroom.services', ['app'])

.factory('RoomService', ['Room', '$localStorage', function(Room, $localStorage) {
    var currentRoom;

    var allRooms = $localStorage.rooms;

    //Time range for calendar plugin, will only display times from minTimeDuration to maxTimeDuration
    var minTimeDuration = moment.duration('07:00:00');
    var maxTimeDuration = moment.duration('22:00:00');
    var minTimeString = "07:00";
    var maxTimeString = "22:00";

    return {
        getAllRooms: function() {
            $localStorage.rooms = Room.query();
            allRooms = $localStorage.rooms;
            return allRooms;
        },
        getRoom: function(id) {
            currentRoom = Room.get({ id: id });
            return currentRoom;
        },
        getCurrentRoom: function() {
            return currentRoom;
        },
        getRoomDescription: function(roomId) {
            var res;
            $localStorage.rooms.forEach(function(room) {
                if (room.id == roomId) {
                    res = room.description;
                }
            })
            return res;
        },
        getRoomObj: function(roomId) {
            var res;
            console.log("LS: " + JSON.stringify($localStorage.rooms));
            $localStorage.rooms.forEach(function(room) {
                if (room.id == roomId) {
                    res = room;
                }
            })
            return res;
        },
        getMinTime: function() {
            return minTimeDuration;
        },
        getMaxTime: function() {
            return maxTimeDuration;
        },
        getMinTimeString: function() {
            return minTimeString;
        },
        getMaxTimeString: function() {
            return maxTimeString;
        }
    };
}]).factory('UserService', ['$rootScope', '$localStorage', '$http', function($rootScope, $localStorage, $http) {

    return {
        authenticateUser: function(username, password) {
            return $http.get($rootScope.apisrc + '/api/user/login?username=' + username + '&password=' + password)
                .success(function(response) {
                    console.log(JSON.stringify(response));
                    //Login Success
                    if (response.token) {
                        //Persist user into local storage
                        $localStorage.currentUser = { userId: response.userId, username: username, token: response.token };
                        //currentUser = $localStorage.currentUser;
                        //Set Authorization headers for user requests
                        $http.defaults.headers.common.Authorization = 'Bearer ' + response.token;
                        //Show sidebar and redirect to home view
                        $rootScope.hideBars = false;
                    }
                });
        },
        logoutUser: function() {
            delete $localStorage.currentUser;
            //delete currentUser;
        },
        getCurrentUser: function() {
            return $localStorage.currentUser;
        },
        getUsername: function(userId) {
            return $http.get($rootScope.apisrc + '/api/user/' + userId);
        }
    };
}]).factory('ReservationService', ['$rootScope', '$localStorage', '$http', function($rootScope, $localStorage, $http) {

    //Maximum amount of hours a user can reserve at a time
    var maxReservationTime = 3;

    return {
        verifyReservationSession: function(userId, roomId, day) {
            return $http.post($rootScope.apisrc + '/api/room/verifyReservationSession', { userId: userId, roomId: roomId, day: day.format('YYYY-MM-DD') });
        },
        initiateReservationSession: function(userId, roomId, day) {
            return $http.post($rootScope.apisrc + '/api/room/initateReservationSession', { userId: userId, roomId: roomId, day: day.format('YYYY-MM-DD') });
        },
        destroyReservationSession: function(userId, roomId, day) {
            return $http.post($rootScope.apisrc + '/api/room/destroyReservationSession', { userId: userId, roomId: roomId, day: day.format('YYYY-MM-DD') });
        },
        createReservation: function(userId, roomId, startTime, endTime) {
            return $http.post($rootScope.apisrc + '/api/reservation', { userId: userId, roomId: roomId, startTime: startTime, endTime: endTime });
        },
        modifyReservation: function(userId, reservationId, startTime, endTime) {
            return $http.post($rootScope.apisrc + '/api/reservation/modify', { userId: userId, reservationId: reservationId, startTime: startTime, endTime: endTime });
        },
        deleteReservation: function(reservationId) {
            return $http.get($rootScope.apisrc + '/api/reservation/' + reservationId + '/cancel');
        },
        getByRoom: function(roomId) {
            return $http.get($rootScope.apisrc + '/api/reservation/getbyroom/' + roomId);
        },
        getByUser: function(userId) {
            return $http.get($rootScope.apisrc + '/api/reservation/getbyuser/' + userId);
        },
        getWaitlistPosition: function(reservationId) {
            return $http.get($rootScope.apisrc + '/api/reservation/' + reservationId + '/position');
        },
        isWithinAllowableTime: function(start, end) {
            if (moment.duration(end.diff(start)).asHours() > maxReservationTime)
                return false;
            return true;
        },
        maxReservationTime: function() {
            return maxReservationTime;
        }
    };
}]);
