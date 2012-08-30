// note, io.listen(<port>) will create a http server for you
var io = require('socket.io').listen(9090);

io.sockets.on('connection', function (socket) {
	io.sockets.emit('this', { will: 'be received by everyone' });

	socket.on('private message', function (msg) {
		console.log('I received a private message saying ', msg);
	});

	socket.on('this', function(msg) {
		console.log("this received ", msg);
	});

	socket.on('disconnect', function () {
		io.sockets.emit('user disconnected');
	});

	socket.on('msg', function(msg) {
		socket.emit('msg', msg);
	});

	socket.on('event', function(msg) {
		socket.emit('event');
	});

	socket.on('global event', function(){
		io.sockets.emit('global event');
	});

	socket.on('message', function(msg){
		console.log(msg);
		socket.send(msg);
	});
});