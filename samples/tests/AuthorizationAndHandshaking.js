var io = require('socket.io').listen(9090);

io.configure(function (){
	io.set('authorization', function (handshakeData, callback) {
		if(handshakeData.query.pass && handshakeData.query.pass === 'true') {
			handshakeData.foo = 'bar';
			callback(null, true);
		} else {
			callback('reason', false);
		}
	});
});

io.sockets.on('connection', function(socket){
	socket.on('get', function(){
		socket.emit('data', {foo:socket.handshake.foo});
	});
});

io.of('/private').authorization(function (handshakeData, callback) {
	console.dir(handshakeData);
	if(handshakeData.query.pass && handshakeData.query.pass === 'true') {
		handshakeData.foo = 'baz';
		callback(null, true);
	} else {
		callback('reason', false);
	}
}).on('connection', function (socket) {
	socket.on('get', function(){
		socket.emit('data', {foo:socket.handshake.foo});
	});
});
