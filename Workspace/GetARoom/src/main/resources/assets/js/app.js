(function() {
    var app = angular.module('app', ['ui.router', 'ngAnimate', 'ui.bootstrap', 'ngResource', 'ui.calendar', 'getaroom.services', 'mainController', 'ngStorage'])
    var apisrc = "http://localhost:8080";

    app.config(function($stateProvider, $urlRouterProvider, $controllerProvider) {
            var origController = app.controller
            app.controller = function(name, constructor) {
                $controllerProvider.register(name, constructor);
                return origController.apply(this, arguments);
            }

            var viewsPrefix = 'views/';

            // For any unmatched url, send to /
            $urlRouterProvider.otherwise("/")

            $stateProvider
            // you can set this to no template if you just want to use the html in the page
                .state('home', {
                    url: "/",
                    templateUrl: viewsPrefix + "home.html",
                    data: {
                        pageTitle: 'Home'
                    }
                }).state('rooms', {
                    url: "/rooms",
                    templateUrl: viewsPrefix + "rooms.html",
                    controller: 'RoomsCtrl',
                    data: {
                        pageTitle: 'All Rooms'
                    }
                })
                .state('rooms.room', {
                    url: "/:id",
                    views: {
                        '@': {
                            templateUrl: viewsPrefix + "room.html",
                            controller: 'RoomCtrl'
                        }
                    },
                    data: {
                        pageTitle: 'Room',
                        apisrc: apisrc
                    },
                    resolve: {
                        id: function($stateParams) {
                            return $stateParams.id;
                        }
                    }
                })
                .state('rooms.room.reserve', {
                    url: "/reserve",
                    views: {
                        '@': {
                            templateUrl: viewsPrefix + "reserve.html",
                            controller: 'ReserveCtrl'
                        }
                    },
                    data: {
                        pageTitle: 'Reserve',
                        apisrc: apisrc
                    },
                    params: {
                        date: null,
                        events: null
                    }
                }).state('userpanel', {
                    url: "/userpanel",
                    views: {
                        '@': {
                            templateUrl: viewsPrefix + "userpanel.html",
                            controller: 'UserPanelCtrl'
                        }
                    },
                    data: {
                        pageTitle: 'User Panel'
                    }
                }).state('userpanel.modify', {
                    url: "/modify",
                    views: {
                        '@': {
                            templateUrl: viewsPrefix + "modify.html",
                            controller: 'ModifyCtrl'
                        }
                    },
                    data: {
                        pageTitle: 'Modify Reservation'
                    },
                    params: {
                        date: null,
                        reservationId: null,
                        roomId: null
                    }
                }).state('login', {
                    url: "/login",
                    controller: 'LoginCtrl',
                    templateUrl: viewsPrefix + "login.html",
                    data: {
                        pageTitle: 'Log In',
                        apisrc: apisrc
                    }
                })
                .state('theme', {
                    url: "/theme",
                    templateUrl: viewsPrefix + "theme.html",
                    data: {
                        pageTitle: 'Theme Example'
                    }
                })
                .state('ui', {
                    url: "/ui",
                    templateUrl: viewsPrefix + "ui.html",
                    data: {
                        pageTitle: 'UI'
                    }
                })
        }).run(function($rootScope, $http, $location, $localStorage) {
            //Keep user logged in between page refreshes
            if ($localStorage.currentUser) {
                $http.defaults.headers.common.Authorization = 'Bearer ' + $localStorage.currentUser.token;
            }

            //If non logged in user tries to access restricted page, deny request
            $rootScope.$on('$locationChangeStart', function(event, next, current) {
                var publicPages = ['/login'];
                var restrictedPage = publicPages.indexOf($location.path()) === -1;
                if (restrictedPage && !$localStorage.currentUser) {
                    $location.path('/login');
                } else if (!restrictedPage && $localStorage.currentUser) {
                    $location.path('/');
                }
            });
            $rootScope.apisrc = apisrc;
        })
        .directive('updateTitle', ['$rootScope', '$timeout',
            function($rootScope, $timeout) {
                return {
                    link: function(scope, element) {
                        var listener = function(event, toState) {
                            var title = 'GetARoom';
                            if (toState.data && toState.data.pageTitle) title = toState.data.pageTitle + ' - ' + title;
                            $timeout(function() {
                                element.text(title);
                            }, 0, false);
                        };
                        $rootScope.$on('$stateChangeSuccess', listener);
                    }
                };
            }
        ]).factory("Room", function($resource) {
            return $resource(apisrc + "/api/room/:id", { id: "@id" });
        });
}());
