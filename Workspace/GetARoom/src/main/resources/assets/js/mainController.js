angular.module('mainController', [])
    .controller('home', function($scope, $state, $resource, $rootScope, UserService, Room) {
        $scope.title = 'Project name';
        $scope.username;
        console.log($scope.username);

        if (UserService.getCurrentUser()) {
            $scope.username = UserService.getCurrentUser().username;
            console.log($scope.username);
        }

        $rootScope.$on('login-success', function() {
            $scope.username = UserService.getCurrentUser().username;
            console.log($scope.username);
        });

        // returns true if the current router url matches the passed in url
        // so views can set 'active' on links easily
        $scope.isUrl = function(url) {
            if (url === '#') return false;
            return ('#' + $state.$current.url.source + '/').indexOf(url + '/') === 0;
        };

        $scope.rooms = Room.query();

        $scope.logout = function() {
            UserService.logoutUser();
            $rootScope.hideBars = true;
            $state.go('login');
        };


    }).controller('LoginCtrl', function($scope, $rootScope, $state, UserService) {
        //Contoller for Login page
        $rootScope.hideBars = true;
        $scope.statusMessage;

        $scope.loginButton = function() {
            UserService.authenticateUser($scope.user.name, $scope.user.password).then(function(d) {
                console.log(JSON.stringify(d));
                if (d.data.token) {
                    $rootScope.$emit('login-success');
                    $state.go("home", { reload: true });
                } else {
                    $scope.statusMessage = "Login Failed!";
                }
            });
        };
    }).controller('RoomsCtrl', function($scope, $state, $resource, Room) {
        //Controller for all rooms page 
    }).controller('RoomCtrl', function($scope, $state, $resource, id, RoomService, ReservationService, UserService) {
        //Controller for single room page, view
        $scope.id = id;
        $scope.thisroom = RoomService.getRoom(id);
        $scope.hideReservationButton = true;

        $scope.events = [];
        $scope.eventSources = [$scope.events];
        
        $scope.buttonText;
        $scope.buttonValid = true;

        $scope.reserveDay;
        $scope.reserveDayObj;

        ReservationService.getByRoom(id).then(function(response) {
            console.log(response);
            if (response.status !== 204) {
                response.data.forEach(function(reservation) {
                    console.log("Got: " + JSON.stringify(reservation));
                    var username;

                    UserService.getUsername(reservation.userId).then(function(r) {
                        username = r.data.username;
                        console.log("username is " + username);
                        $scope.events.push({
                            title: username,
                            start: reservation.start_time,
                            end: reservation.end_time
                        })
                    });
                });
            }
        });

        $scope.dayClick = function(date, jsEvent, view) {
            $scope.reserveDayObj = date;
            $scope.reserveDay = date.format('MMM Do');
            $scope.hideReservationButton = false;

            ReservationService.verifyReservationSession(UserService.getCurrentUser(), $scope.thisroom, $scope.reserveDayObj).then(function(response) {
                console.log(JSON.stringify(response));
                if (response.data.valid == true) {
                    $scope.buttonText = "Reserve This Room On " + $scope.reserveDay;
                    $scope.buttonValid = true;
                } else {
                    $scope.buttonText = "Someone is Already Reserving " + $scope.reserveDay + "!";
                    $scope.buttonValid = false;
                }
            });
        };

        $scope.buttonClick = function() {
            if ($scope.buttonValid) {
                ReservationService.initiateReservationSession(UserService.getCurrentUser(), $scope.thisroom, $scope.reserveDayObj).then(function(response) {
                    console.log(JSON.stringify(response));
                    $state.go('rooms.room.reserve', { date: $scope.reserveDayObj, events: $scope.events });
                });
            }
        };

        $scope.uiConfig = {
            calendar: {
                height: 800,
                editable: false,
                selectable: true,
                allDaySlot: false,
                dayClick: $scope.dayClick,
                header: {
                    left: 'month agendaWeek agendaDay',
                    center: 'title',
                    right: 'today prev,next'
                }
            }
        };


    }).controller('ReserveCtrl', function($scope, $state, $resource, $stateParams, $timeout, RoomService, ReservationService, UserService) {
        //Controller for single room page, reserve
        $scope.buttonText = "Reserve";
        $scope.counter = 4;
        $scope.counterTimeout;

        $scope.reserveDayObj = $stateParams.date;
        $scope.sessionActive = true;

        $scope.startTime;
        $scope.endTime;
        $scope.startTimeObj;
        $scope.endTimeObj;
        $scope.hideReservationView = true;

        $scope.thisroom = RoomService.getCurrentRoom();
        $scope.events = $stateParams.events;
        $scope.eventSources = [$scope.events];

        $scope.events.

        $scope.destroyReservationSession = function() {
            console.log("Destroying Session");
            ReservationService.destroyReservationSession(UserService.getCurrentUser(), $scope.thisroom, $scope.reserveDayObj);
            $scope.sessionActive = false;
        };

        $scope.select = function(start, end, jsEvent, view) {
            $scope.startTime = start.format('lll');
            $scope.endTime = end.format('lll');
            $scope.startTimeObj = start;
            $scope.endTimeObj = end;
            $scope.hideReservationView = false;
        };

        $scope.confirmReservation = function() {
            ReservationService.createReservation(UserService.getCurrentUser(), $scope.thisroom, $scope.startTimeObj.format(), $scope.endTimeObj.format()).then(function(response) {
                console.log(JSON.stringify(response));
            });
            $scope.counterTimeout = $timeout($scope.countdown, 0);
            $timeout(function() {
                $state.go('rooms.room');
                $scope.destroyReservationSession();
            }, 4000)
        }

        $scope.countdown = function() {
            if ($scope.counter > 1) {
                $scope.counter--;
                $scope.buttonText = $scope.counter;
            } else {
                $scope.buttonText = "Reservation Made!";
            }
            
            $scope.counterTimeout = $timeout($scope.countdown, 1000)
        }

        $scope.uiConfig = {
            calendar: {
                defaultDate: $stateParams.date,
                defaultView: 'agendaDay',
                height: 500,
                editable: true,
                selectable: true,
                selectHelper: true,
                unselectAuto: false,
                allDaySlot: false,
                select: $scope.select,
                header: {
                    left: 'none',
                    center: 'title',
                    right: 'none'
                }
            }
        };
        $scope.$on("$destroy", function() {
            if ($scope.sessionActive)
                ReservationService.destroyReservationSession(UserService.getCurrentUser(), $scope.thisroom, $scope.reserveDayObj);
        });
    });
