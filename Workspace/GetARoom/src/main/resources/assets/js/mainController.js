angular.module('mainController', [])
    .controller('home', function($scope, $state, $resource, Room) {
        $scope.title = 'Project name';

        // returns true if the current router url matches the passed in url
        // so views can set 'active' on links easily
        $scope.isUrl = function(url) {
            if (url === '#') return false;
            return ('#' + $state.$current.url.source + '/').indexOf(url + '/') === 0;
        };

        $scope.rooms = Room.query(function() { /*console.log(JSON.stringify($scope.rooms));*/ });


    }).controller('LoginCtrl', function($scope, $state, $rootScope, $http, $localStorage) {
        //Contoller for Login page
        $rootScope.showSidebar = true;
        $scope.statusMessage;
        $scope.apisrc = $state.current.data.apisrc;

        $scope.loginButton = function() {
             $http.get($scope.apisrc + '/api/user/login?username=' + $scope.user.name + '&password=' + $scope.user.password)
                .success(function (response) {
                    console.log(JSON.stringify(response));
                    //Login Success
                    if (response.token) {
                        //Persist user into local storage
                        $localStorage.currentUser = { username: $scope.user.name, token: response.token };

                        //Set Authorization headers for user requests
                        $http.defaults.headers.common.Authorization = 'Bearer ' + response.token;

                        //Show sidebar and redirect to home view
                        $rootScope.showSidebar = false;
                        $state.go("home")
                    } else {
                        $scope.statusMessage = "Login Failed!";
                    }
                });
        };
    }).controller('RoomsCtrl', function($scope, $state, $resource, Room) {
        //Controller for all rooms page 
    }).controller('RoomCtrl', function($scope, $state, $resource, id, RoomService) {
        //Controller for single room page, view

        var date = new Date();
        $scope.id = id;
        $scope.thisroom = RoomService.getRoom(id);

        $scope.events = [{ title: 'All Day Event', start: new Date(date.getFullYear(), date.getMonth(), 12) }];
        $scope.eventSources = [$scope.events];
        $scope.hideReservationButton = true;

        $scope.reserveDay;
        $scope.reserveDayObj;

        $scope.dayClick = function(date, jsEvent, view) {
            console.log("Click on: " + date.format('MMM Do'));
            $scope.reserveDayObj = date;
            $scope.reserveDay = date.format('MMM Do');
            $scope.hideReservationButton = false;
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
                /*  eventClick: $scope.alertEventOnClick,
                  eventDrop: $scope.alertOnDrop,
                  eventResize: $scope.alertOnResize*/
            }
        };


    }).controller('ReserveCtrl', function($scope, $state, $resource, $stateParams, RoomService) {
        //Controller for single room page, reserve
        console.log("Date is: " + $stateParams.date);
        $scope.startTime;
        $scope.endTime;
        $scope.hideReservationView = true;

        $scope.thisroom = RoomService.getCurrentRoom();
        $scope.events = [];
        $scope.eventSources = [$scope.events];

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
                /*  eventClick: $scope.alertEventOnClick,
                  eventDrop: $scope.alertOnDrop,
                  eventResize: $scope.alertOnResize*/
            }
        };
    });
