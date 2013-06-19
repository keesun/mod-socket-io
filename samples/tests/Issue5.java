package tests;

import com.nhncorp.mods.socket.io.SocketIOServer;
import com.nhncorp.mods.socket.io.SocketIOSocket;
import com.nhncorp.mods.socket.io.impl.DefaultSocketIOServer;
import com.nhncorp.mods.socket.io.impl.Namespace;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.impl.DefaultVertx;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

/**
 * @author Keesun Baik
 */
public class Issue5 extends Verticle {

	public Issue5() {
	}

	public Issue5(Vertx vertx) {
		this.vertx = vertx;
	}

	@Override
	public void start() {
		int port = 9191;
		HttpServer server = vertx.createHttpServer();
		final SocketIOServer io = new DefaultSocketIOServer(vertx, server);

		io.sockets().onConnection(new Handler<SocketIOSocket>() {
			@Override
			public void handle(SocketIOSocket socket) {
				socket.on("newchannel", new Handler<JsonObject>() {
					@Override
					public void handle(JsonObject event) {
						System.out.println(event);
						newChannel(event.getString("channel"));
					}


				});
			}

			private void newChannel(String channel) {
				final Namespace namespace = io.of("/" + channel);
				namespace.onConnection(new Handler<SocketIOSocket>() {
					@Override
					public void handle(SocketIOSocket socket) {
						socket.on("message", new Handler<JsonObject>() {
							@Override
							public void handle(JsonObject event) {
								System.out.println(event);
								namespace.emit("message", event.getString("data"));
							}
						});
					}
				});
			}
		});

		server.listen(port);
	}

	public static void main(String[] args) throws Exception {
		Vertx vertx = new DefaultVertx();
		Issue5 app = new Issue5(vertx);
		app.start();
		Thread.sleep(Long.MAX_VALUE);
	}

}
