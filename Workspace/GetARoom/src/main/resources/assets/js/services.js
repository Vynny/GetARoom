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
}]);
