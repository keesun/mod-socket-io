package com.nhncorp.mods.socket.io;

import com.nhncorp.mods.socket.io.impl.Configurer;
import com.nhncorp.mods.socket.io.impl.DefaultSocketIOServer;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.impl.VertxInternal;
import org.vertx.java.core.json.JsonObject;

/**
 * @author Keesun Baik
 */
public class SocketIOServerTest {

	public static void main(String[] args) throws InterruptedException {
		int port = 9090;
		Vertx vertx = Vertx.newVertx();
		HttpServer httpServer = vertx.createHttpServer();

		SocketIOServer io = new DefaultSocketIOServer((VertxInternal) vertx, httpServer);
		io.configure(new Configurer() {
			public void configure(JsonObject config) {
				config.putString("transports", "websocket,flashsocket,xhr-polling,jsonp-polling,htmlfile");
			}
		});

		io.sockets().onConnection(new Handler<SocketIOSocket>() {
			public void handle(final SocketIOSocket socket) {
				System.out.println(socket.getId() + " is connected.");

				socket.on("timer", new Handler<JsonObject>() {
					public void handle(JsonObject data) {
						socket.emit("timer", data);
					}
				});

				socket.onDisconnect(socket.getId() + " is disconnected");
			}
		});

		httpServer.listen(port);
		System.out.println("Server is running on http://localhost:" + port);
		Thread.sleep(Long.MAX_VALUE);
	}

}
