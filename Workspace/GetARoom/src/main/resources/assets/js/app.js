(function() {
	var app = angular.module('app', ['ui.router', 'ngAnimate', 'ui.bootstrap', 'ngResource', 'ui.calendar', 'mainController'])
	var apisrc = "http://getaroom.lvain.com";

	app.config(function($stateProvider, $urlRouterProvider, $controllerProvider){
		var origController = app.controller
		app.controller = function (name, constructor){
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
			})
			.state('rooms', {
				url: "/rooms",
				templateUrl: viewsPrefix + "rooms.html",
				controller: 'RoomsCtrl',
				data: {
					pageTitle: 'All Rooms'
				}
			})
			.state('userpanel', {
				url: "/userpanel",
				templateUrl: viewsPrefix + "userpanel.html",
				data: {
					pageTitle: 'User Panel'
				}
			})
			.state('rooms.room', {
				url: "/:id",
				//templateUrl: viewsPrefix + "room.html",
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
			.state('contact.list', {
				url: "/list",
				templateUrl: viewsPrefix + "contact-list.html",
				controller: function($scope){
					$scope.things = ["A", "Set", "Of", "Things"];
				}
			})
			.state('theme', {
				url: "/theme",
				templateUrl: viewsPrefix + "theme.html",
				data: {
					pageTitle: 'Theme Example'
				}
			})
			.state('blog', {
				url: "/blog",
				templateUrl: viewsPrefix + "blog.html",
				data: {
					pageTitle: 'Blog'
				}
			})
			.state('grid', {
				url: "/grid",
				templateUrl: viewsPrefix + "grid.html",
				data: {
					pageTitle: 'Grid'
				}
			})
			.state('ui', {
				url: "/ui",
				templateUrl: viewsPrefix + "ui.html",
				data: {
					pageTitle: 'UI'
				}
			})
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
			return $resource(apisrc + "/api/room/:id", {id: "@id"});
		});
	}());