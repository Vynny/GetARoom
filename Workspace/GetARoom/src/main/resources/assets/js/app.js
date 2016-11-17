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
                        pageTitle: 'Room'
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
                        pageTitle: 'Reserve'
                    },
                    params: {
                        date: null
                    }
                }).state('userpanel', {
                    url: "/userpanel",
                    templateUrl: viewsPrefix + "userpanel.html",
                    data: {
                        pageTitle: 'User Panel'
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
                var loginPage = ['/login'];
                var publicPages = ['/login'];
                var restrictedPage = publicPages.indexOf($location.path()) === -1;
                if (restrictedPage && !$localStorage.currentUser) {
                    $location.path('/login');
                }
                if (loginPage && $localStorage.currentUser) {
                    $location.path('/');
                }
            });
        })
        .directive('updateTitle', ['$rootScope', '$timeout',
            function($rootScope, $timeout) {
                return {
                    link: function(scope, element) {
                        var listener = function(event, toState) {
                            var title = 'Project Name';
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
        }).factory("User", function($resource) {
            return $resource(apisrc + "/api/user/:id", { id: "@id" });
        });
}());
