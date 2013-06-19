package com.nhncorp.mods.socket.io;

import com.nhncorp.mods.socket.io.impl.Configurer;
import com.nhncorp.mods.socket.io.impl.DefaultSocketIOServer;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.impl.DefaultVertx;
import org.vertx.java.core.impl.VertxInternal;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.shareddata.impl.SharedMap;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Keesun Baik
 */
public class SocketIOServerTest {

	public static void main(String[] args) throws InterruptedException {
		int port = 9090;
		final Vertx vertx = new DefaultVertx();
		HttpServer httpServer = vertx.createHttpServer();

		SocketIOServer io = new DefaultSocketIOServer(vertx, httpServer);
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

				socket.onDisconnect(new Handler<JsonObject>() {
					public void handle(JsonObject event) {
						System.out.println(socket.getId() + " is disconnected.");
					}
				});
			}
		});

		httpServer.listen(port);
		System.out.println("Server is running on http://localhost:" + port);
		Thread.sleep(Long.MAX_VALUE);
	}

}
