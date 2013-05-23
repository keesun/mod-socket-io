var io = require('socket.io').listen(9090);

io.sockets.on('connection', function (socket) {
	socket.on('newchannel', function (data) {
		console.log(data);
		onNewNamespace(data.channel, io);
	});
});

function onNewNamespace(channel, io) {
	var space = io.of('/' + channel);
	space.on('connection', function (socket) {
		socket.on('message', function (data) {
			space.emit('message', data.data);
		});
	});
}