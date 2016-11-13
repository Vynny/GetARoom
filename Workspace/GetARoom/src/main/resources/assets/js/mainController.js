angular.module('mainController', [])
    .controller('home', function($scope, $state, $resource, Room) {
        $scope.title = 'Project name';

        // returns true if the current router url matches the passed in url
        // so views can set 'active' on links easily
        $scope.isUrl = function(url) {
            if (url === '#') return false;
            return ('#' + $state.$current.url.source + '/').indexOf(url + '/') === 0;
        };

        $scope.rooms = Room.query(function() { console.log(JSON.stringify($scope.rooms)); });


    }).controller('RoomCtrl', function($scope, $state, $resource, Room, id) {
    	var date = new Date();
        $scope.id = id;
        $scope.thisroom = Room.get({ id: $scope.id }, function() { console.log(JSON.stringify($scope.thisroom)); });

        $scope.events = [{title: 'All Day Event',start: new Date(date.getFullYear(), date.getMonth(), 12)}];
        $scope.eventSources = [$scope.events];

        $scope.uiConfig = {
            calendar: {
                height: 450,
                editable: true,
                header: {
                    left: 'month basicWeek basicDay agendaWeek agendaDay',
                    center: 'title',
                    right: 'today prev,next'
                }
                /*  eventClick: $scope.alertEventOnClick,
                  eventDrop: $scope.alertOnDrop,
                  eventResize: $scope.alertOnResize*/
            }
        };


    }).controller('RoomsCtrl', function($scope, $state, $resource, Room) {

    });
