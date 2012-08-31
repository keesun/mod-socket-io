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

	socket.on('has nickname', function() {
		socket.has('nickname', function (err, has) {
			console.log('has nickname? ' + has);
			socket.emit('has', has);
		});
	})

	socket.on('del nickname', function(){
		socket.del('nickname', function(err){
			console.log('del nickname');
			socket.emit('del');
		});
	});

	socket.on('confirm nickname', function(){
		socket.has('nickname', function(err, has){
			console.log('confirm nickname');
			socket.emit('confirm', has);
		})
	});
});