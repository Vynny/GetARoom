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


    }).controller('RoomsCtrl', function($scope, $state, $resource, Room) {
        //Controller for all rooms page 
    }).controller('RoomCtrl', function($scope, $state, $resource, id, RoomService) {
        //Controller for single room page, view

        var date = new Date();
        $scope.id = id;
        $scope.thisroom = RoomService.getRoom(id);

        $scope.events = [{ title: 'All Day Event', start: new Date(date.getFullYear(), date.getMonth(), 12) }];
        $scope.eventSources = [$scope.events];

        $scope.uiConfig = {
            calendar: {
                height: 800,
                editable: false,
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


    }).controller('ReserveCtrl', function($scope, $state, $resource, RoomService) {
        //Controller for single room page, reserve
        var date = new Date();

        $scope.startTime;
        $scope.endTime;
        $scope.hideReservationView = true;

        $scope.thisroom = RoomService.getCurrentRoom();

        //console.log("Thisroom is: " + JSON.stringify($scope.thisroom));

        $scope.events = [];
        $scope.eventSources = [$scope.events];

        $scope.select = function(start, end, jsEvent, view) {
            /*var eventData = {
                start: start,
                end: end
            };  
            $scope.events.push(eventData);*/
            $scope.startTime = start.format('lll');
            $scope.endTime = end.format('lll');
            $scope.hideReservationView = false;
        };

        $scope.uiConfig = {
            calendar: {
                height: 500,
                editable: true,
                selectable: true,
                selectHelper: true,
                unselectAuto: false, 
                select: $scope.select,
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
    });
