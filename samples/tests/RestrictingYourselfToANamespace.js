var io = require('socket.io').listen(9090);

var chat = io
		.of('/chat')
		.on('connection', function (socket) {
			socket.on('namespace emit', function(){
				chat.emit('msg', { everyone: 'in', '/chat': 'will get' });
			});
		});

var news = io
		.of('/news')
		.on('connection', function (socket) {
			socket.emit('item', { news: 'item' });
		});