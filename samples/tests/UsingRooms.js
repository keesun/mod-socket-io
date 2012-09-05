var io = require('socket.io').listen(9090);

io.sockets.on('connection', function (socket) {
	socket.on('subscribe', function(data){
		var room = data.room;
		socket.join(room);
		socket.emit('join:' + room);

		console.dir(io.sockets.manager.rooms);
		console.dir(io.sockets.clients(room));
		console.dir(io.sockets.manager.roomClients[socket.id]);
	});

	socket.on('emit', function(data){
		var room = data.room;
		io.sockets.in(room).emit('emit', data);
	});

	socket.on('unsubscribe', function(data){
		var room = data.room;
		socket.leave(room);
		socket.emit('leave:' + room);

		console.dir(io.sockets.manager.rooms);
		console.dir(io.sockets.clients(room));
		console.dir(io.sockets.manager.roomClients[socket.id]);
	});
});