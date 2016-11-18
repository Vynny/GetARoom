angular.module('mainController', [])
    .controller('home', function($scope, $state, $resource, $rootScope, UserService, Room) {
        $scope.title = 'Project name';
        $scope.username;
        console.log($scope.username);

        $rootScope.$on('login-success', function() {
            if (UserService.getCurrentUser()) {
                $scope.username = UserService.getCurrentUser().username;
                console.log($scope.username);
            }
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
        /* var date = new Date();
         $scope.events = [{ title: 'All Day Event', start: new Date(date.getFullYear(), date.getMonth(), 12) }];
         $scope.eventSources = [$scope.events];*/
        $scope.hideReservationButton = true;

        $scope.buttonText;
        $scope.buttonValid = true;

        $scope.reserveDay;
        $scope.reserveDayObj;

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
                    $state.go('rooms.room.reserve', { date: $scope.reserveDayObj });
                });
            }
        };

        $scope.uiConfig = {
            calendar: {
                height: 800,
                editable: true,
                selectable: true,
                dayClick: $scope.dayClick,
                header: {
                    left: 'month agendaWeek agendaDay',
                    center: 'title',
                    right: 'today prev,next'
                }
            }
        };


    }).controller('ReserveCtrl', function($scope, $state, $resource, $stateParams, RoomService, ReservationService, UserService) {
        //Controller for single room page, reserve
        $scope.reserveDayObj = $stateParams.date;
        $scope.sessionActive = true;

        $scope.startTime;
        $scope.endTime;
        $scope.hideReservationView = true;

        $scope.thisroom = RoomService.getCurrentRoom();
        $scope.events = [];
        $scope.eventSources = [$scope.events];

        $scope.destroyReservationSession = function() {
            console.log("Destroying Session");
            ReservationService.destroyReservationSession(UserService.getCurrentUser(), $scope.thisroom, $scope.reserveDayObj);
            $scope.sessionActive = false;
        };

        $scope.select = function(start, end, jsEvent, view) {
            $scope.startTime = start.format('lll');
            $scope.endTime = end.format('lll');
            $scope.hideReservationView = false;
        };

        $scope.uiConfig = {
            calendar: {
                defaultDate: $stateParams.date,
                defaultView: 'agendaDay',
                height: 500,
                editable: true,
                selectable: true,
                selectHelper: true,
                unselectAuto: false,
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

        /* $scope.$on('$stateChangeStart', function(event) {
             var answer = confirm("Are you sure you want to leave this page?")
             if (!answer) {
                 event.preventDefault();
             }
         });*/
    });
