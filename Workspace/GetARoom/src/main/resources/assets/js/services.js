angular.module('getaroom.services', ['app'])

.factory('RoomService', ['Room', function(Room) {
    console.log("test");
    var currentRoom;
    return {

        getRoom: function(id) {
            currentRoom = Room.get({ id: id });
            return currentRoom;
        },
        getCurrentRoom: function() {
            return currentRoom;
        }
    };
}]).factory('UserService', ['$rootScope', '$localStorage', '$http', function($rootScope, $localStorage, $http) {
    var currentUser = $localStorage.currentUser;

    return {
        authenticateUser: function(username, password) {
            return $http.get($rootScope.apisrc + '/api/user/login?username=' + username + '&password=' + password)
                .success(function(response) {
                    console.log(JSON.stringify(response));
                    //Login Success
                    if (response.token) {
                        //Persist user into local storage
                        $localStorage.currentUser = { userId: response.userId, username: username, token: response.token };
                        currentUser = $localStorage.currentUser;
                        //Set Authorization headers for user requests
                        $http.defaults.headers.common.Authorization = 'Bearer ' + response.token;
                        //Show sidebar and redirect to home view
                        $rootScope.hideSidebar = false;
                    }
                });
        },
        logoutUser: function() {
        	delete $localStorage.currentUser;
        	delete currentUser;
        },
        getCurrentUser: function() {
            return currentUser;
        }
    };
}]).factory('ReservationService', ['$rootScope', '$localStorage', '$http', function($rootScope, $localStorage, $http) {
   // var currentUser = $localStorage.currentUser;
    //console.log(currentUser);

    return {
        verifyReservationSession: function(currentUser, currentRoom, day) {
        	return $http.post($rootScope.apisrc + '/api/room/verifyReservationSession', { userId: currentUser.userId , roomId: currentRoom.id, day: day.format('YYYY-MM-DD') });
        },
        initiateReservationSession: function(currentUser, currentRoom, day) {
        	return $http.post($rootScope.apisrc + '/api/room/initateReservationSession', { userId: currentUser.userId , roomId: currentRoom.id, day: day.format('YYYY-MM-DD') });
        },
        destroyReservationSession: function(currentUser, currentRoom, day) {
        	return $http.post($rootScope.apisrc + '/api/room/destroyReservationSession', { userId: currentUser.userId , roomId: currentRoom.id, day: day.format('YYYY-MM-DD') });
        },
        getCurrentUser: function() {
            return currentUser;
        }
    };
}]);
