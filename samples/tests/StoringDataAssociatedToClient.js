var io = require('socket.io').listen(9090);

io.sockets.on('connection', function (socket) {
	socket.on('set nickname', function (name) {
		console.log("name: " + name);
		socket.set('nickname', name, function () {
			console.log("set name");
			socket.emit('ready');
		});
	});

	socket.on('get nickname', function () {
		socket.get('nickname', function (err, name) {
			console.log('Chat message by ', name);
			socket.emit('get', name);
		});
	});
});