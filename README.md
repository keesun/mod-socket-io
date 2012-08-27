# Socket.io for Vert.x

This module allows the Vert.x users can make a socket.io server as node.js users do.
Now, This module supports the latest version of the socket.io, 0.9.10.

## Name

The module name is `socket-io`.

## Configuration

You can configure everything that you can configure in the [Socket.io](https://github.com/LearnBoost/Socket.IO/wiki/Configuring-Socket.IO) like:

	io.configure(new Configurer() {
		public void configure(JsonObject config) {
			config.putString("transports", "websocket,flashsocket,htmlfile,xhr-polling,jsonp-polling");
			config.putBoolean("authorization", true);
		}
	});

## Examples

First, you should include this module's resource by `includes:`.

	{
		"main": "your.runnable.ClassName",
		"includes": "nhn.socket-io-v0.9"
	}

And, after you put the module's jar file to you module's classpath. You can code like:

	public class ClassName extends BusModBase {

		@Override
		public void start() {
			super.start();
			HttpServer server = vertx.createHttpServer();
			SocketIOServer io = new DefaultSocketIOServer(vertx, server);

			io.sockets().onConnection(new Handler<SocketIOSocket>() {
				public void handle(final SocketIOSocket socket) {
					JsonObject data = new JsonObject();
					data.putString("hello", "world");
					socket.emit("news", data);

					socket.on("my other event", new Handler<JsonObject>() {
						public void handle(JsonObject data) {
							System.out.println(data);
						}
					});
				}
			});

			server.listen(9090);
		}
	}

In the view, you can use the same socket.io javascript like:

	<script type="text/javascript" src="/socket.io/socket.io.js"></script>
	<script>
		var socket = io.connect('http://localhost:9090');
		socket.on('news', function (data) {
			console.log(data);
			socket.emit('my other event', { my: 'data' });
		});
	</script>