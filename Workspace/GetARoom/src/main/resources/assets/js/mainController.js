angular.module('mainController', [])
    .controller('home', function($scope, $state, $resource, $rootScope, UserService, RoomService) {
        $scope.title = 'GetARoom';
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

        $scope.rooms = RoomService.getAllRooms();

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
    }).controller('UserPanelCtrl', function($scope, $state, $resource, UserService, ReservationService, RoomService) {
        $scope.userReservations = [];

        $scope.modifyButtonClass;
        $scope.canModify = true;

        ReservationService.getByUser(UserService.getCurrentUser().userId).then(function(response) {
            response.data.forEach(function(reservationItem) {
                $scope.userReservations.push({
                    id: reservationItem.id,
                    roomId: reservationItem.roomId,
                    roomDescription: RoomService.getRoomDescription(reservationItem.roomId),
                    day: moment(reservationItem.start_time).format('MMM Do'),
                    startTime: moment(reservationItem.start_time).format('h:mm a'),
                    endTime: moment(reservationItem.end_time).format('h:mm a'),
                    dayObj: moment(reservationItem.start_time),
                    canModify: true,
                    text: "Modify"
                });
            })
        });

        $scope.modifyClick = function(reservationItem) {
            if (reservationItem.canModify) {
                ReservationService.initiateReservationSession(UserService.getCurrentUser().userId, reservationItem.roomId, reservationItem.dayObj).then(function(response) {
                    $state.go('userpanel.modify', { date: reservationItem.dayObj, reservationId: reservationItem.id, roomId: reservationItem.roomId });
                });
            }
        };

        $scope.modifyMouseOver = function(reservationItem) {
            ReservationService.verifyReservationSession(UserService.getCurrentUser().userId, reservationItem.roomId, reservationItem.dayObj).then(function(response) {
                console.log(JSON.stringify(response));
                if (response.data.valid == true) {
                    reservationItem.canModify = true;
                    reservationItem.text = "Modify";
                } else {
                    reservationItem.canModify = false;
                    reservationItem.text = "Busy!";
                }
            });
        }

    }).controller('ModifyCtrl', function($scope, $state, $resource, $stateParams, $timeout, UserService, ReservationService, RoomService) {
        $scope.thisroom = RoomService.getRoomObj($stateParams.roomId);
        $scope.modifyButtonText = "Confirm Modification";
        $scope.deleteButtonText = "Cancel Reservation";

        $scope.sessionActive = true;
        $scope.withinTimeslotLimit = true;

        $scope.startTime;
        $scope.endTime;

        $scope.startTimeObj;
        $scope.endTimeObj;

        $scope.events = [];
        $scope.eventSources = [$scope.events];

        ReservationService.getByRoom($stateParams.roomId).then(function(response) {
            console.log(response);
            if (response.status !== 204) {
                response.data.forEach(function(reservation) {
                    console.log("Got: " + JSON.stringify(reservation));
                    var username;
                    UserService.getUsername(reservation.userId).then(function(r) {
                        username = r.data.username;
                        if ($stateParams.reservationId == reservation.id) {
                            var resToEdit;
                            if (!reservation.waitlisted) {
                                resToEdit = {
                                    title: username,
                                    start: moment(reservation.start_time),
                                    end: moment(reservation.end_time),
                                    editable: true
                                };
                            } else {
                                var resToEdit = {
                                    title: username,
                                    start: moment(reservation.start_time),
                                    end: moment(reservation.end_time),
                                    editable: true,
                                    backgroundColor: '#e67e22',
                                    borderColor: '#e67e22'
                                };
                            }
                            $scope.events.push(resToEdit);
                            $scope.startTime = resToEdit.start.format('lll');
                            $scope.endTime = resToEdit.end.format('lll');
                        } else {
                            if (!reservation.waitlisted) {
                                $scope.events.push({
                                    title: username,
                                    start: moment(reservation.start_time),
                                    end: moment(reservation.end_time)
                                });
                            } else {
                                $scope.events.push({
                                    title: username,
                                    start: moment(reservation.start_time),
                                    end: moment(reservation.end_time),
                                    backgroundColor: '#e67e22',
                                    borderColor: '#e67e22'
                                });
                            }

                        }

                    });
                });
            }
        });

        $scope.confirmModify = function() {
            ReservationService.modifyReservation(UserService.getCurrentUser().userId, $stateParams.reservationId, $scope.startTimeObj, $scope.endTimeObj).then(function(r) {
                $timeout(function() {
                    $scope.destroyReservationSession();
                    $scope.sessionActive = false;
                    $state.go('userpanel');
                }, 1500)
            });
        };

        $scope.confirmDelete = function() {
            ReservationService.deleteReservation($stateParams.reservationId).then(function(r) {
                $scope.destroyReservationSession();
                $scope.sessionActive = false;
                $state.go('userpanel');
            });
        };

        $scope.onMove = function(event, delta, revertFunc) {
            $scope.startTime = event.start.format('lll');
            $scope.endTime = event.end.format('lll');
            $scope.startTimeObj = event.start;
            $scope.endTimeObj = event.end;

            if (!ReservationService.isWithinAllowableTime(event.start, event.end)) {
                $scope.withinTimeslotLimit = false;
                $scope.modifyButtonText = "Reservation Exceeds " + ReservationService.maxReservationTime() + " hours!";
            } else {
                $scope.withinTimeslotLimit = true;
                $scope.modifyButtonText = "Confirm Modification";
            }
        };

        $scope.uiConfig = {
            calendar: {
                defaultDate: $stateParams.date,
                defaultView: 'agendaDay',
                height: 500,
                editable: false,
                selectOverlap: true,
                unselectAuto: false,
                allDaySlot: false,
                minTime: RoomService.getMinTime(),
                maxTime: RoomService.getMaxTime(),
                eventResize: $scope.onMove,
                eventDrop: $scope.onMove,
                eventConstraint: {
                    start: RoomService.getMinTimeString(),
                    end: RoomService.getMaxTimeString()
                },
                header: {
                    left: 'none',
                    center: 'title',
                    right: 'none'
                }
            }
        };

        $scope.destroyReservationSession = function() {
            console.log("Destroying Session");
            ReservationService.destroyReservationSession(UserService.getCurrentUser().userId, $stateParams.roomId, $stateParams.date);
            $scope.sessionActive = false;
        };

        $scope.$on("$destroy", function() {
            if ($scope.sessionActive)
                $scope.destroyReservationSession();
        });
    }).controller('RoomsCtrl', function($scope, $state, $resource, Room) {
        //Controller for all rooms page, this is dead but wasn't removed lol sry
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
                        if (!reservation.waitlisted) {
                            $scope.events.push({
                                title: username,
                                start: moment(reservation.start_time),
                                end: moment(reservation.end_time)
                            })
                        } else {
                            $scope.events.push({
                                title: username,
                                start: moment(reservation.start_time),
                                end: moment(reservation.end_time),
                                backgroundColor: '#e67e22',
                                borderColor: '#e67e22'
                            })
                        }
                    });
                });
            }
        });

        $scope.dayClick = function(date, jsEvent, view) {
            $scope.reserveDayObj = date;
            $scope.reserveDay = date.format('MMM Do');
            $scope.hideReservationButton = false;

            ReservationService.verifyReservationSession(UserService.getCurrentUser().userId, $scope.thisroom.id, $scope.reserveDayObj).then(function(response) {
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
                ReservationService.initiateReservationSession(UserService.getCurrentUser().userId, $scope.thisroom.id, $scope.reserveDayObj).then(function(response) {
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
                minTime: RoomService.getMinTime(),
                maxTime: RoomService.getMaxTime(),
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
        $scope.validReservation = true;
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
        console.log("This room: " + JSON.stringify($scope.thisroom));
        console.log("User id is: " + UserService.getCurrentUser().userId);
        $scope.events = $stateParams.events;
        $scope.eventSources = [$scope.events];


        $scope.destroyReservationSession = function() {
            console.log("Destroying Session");
            ReservationService.destroyReservationSession(UserService.getCurrentUser().userId, $scope.thisroom.id, $scope.reserveDayObj);
            $scope.sessionActive = false;
        };

        $scope.select = function(start, end, jsEvent, view) {
            $scope.startTime = start.format('lll');
            $scope.endTime = end.format('lll');
            $scope.startTimeObj = start;
            $scope.endTimeObj = end;
            $scope.hideReservationView = false;

            if (!ReservationService.isWithinAllowableTime(start, end)) {
                $scope.validReservation = false;
                $scope.buttonText = "Reservation Exceeds " + ReservationService.maxReservationTime() + " hours!";
            } else {
                $scope.validReservation = true;
                $scope.buttonText = "Reserve";
            }
        };

        $scope.confirmReservation = function() {
            if ($scope.validReservation) {
                ReservationService.createReservation(UserService.getCurrentUser().userId, $scope.thisroom.id, $scope.startTimeObj.format(), $scope.endTimeObj.format()).then(function(response) {
                    console.log(JSON.stringify(response));
                    if (response.data.reservationMade == "false") {
                        $scope.validReservation = false;
                        $scope.buttonText = response.data.message;
                    } else {
                        $scope.validReservation = true;
                        $scope.counterTimeout = $timeout($scope.countdown, 0);
                        $timeout(function() {
                            $state.go('rooms.room');
                            $scope.destroyReservationSession();
                        }, 4000)
                    }
                });
            }
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
                height: 650,
                editable: false,
                selectable: true,
                selectHelper: true,
                selectOverlap: true,
                unselectAuto: false,
                allDaySlot: false,
                minTime: RoomService.getMinTime(),
                maxTime: RoomService.getMaxTime(),
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
                $scope.destroyReservationSession();
        });
    });
