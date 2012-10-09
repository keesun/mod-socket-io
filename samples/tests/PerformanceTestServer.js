var io = require('socket.io').listen(19191);

io.sockets.on('connection', function (socket) {
	socket.on('send', function (msg) {
		socket.broadcast.emit("msg", msg);
	});
});