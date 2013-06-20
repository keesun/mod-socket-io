package tests;

import com.nhncorp.mods.socket.io.SocketIOServer;
import com.nhncorp.mods.socket.io.SocketIOSocket;
import com.nhncorp.mods.socket.io.impl.Configurer;
import com.nhncorp.mods.socket.io.impl.DefaultSocketIOServer;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.impl.DefaultVertx;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

/**
 * Test server for the <a href="https://github.com/learnboost/socket.io#sending-and-receiving-events">Sending and receiving events</a>
 *
 * @author Keesun Baik
 */
public class SendingAndReceivingEvents extends Verticle {

	public SendingAndReceivingEvents() {
	}

	public SendingAndReceivingEvents(Vertx vertx) {
		this.vertx = vertx;
	}

	@Override
	public void start() {
		int port = 9191;
		HttpServer server = vertx.createHttpServer();
		final SocketIOServer io = new DefaultSocketIOServer(vertx, server);

		io.configure(new Configurer() {
			public void configure(JsonObject config) {
				config.putString("transports", "websocket,flashsocket,xhr-polling,jsonp-polling,htmlfile");
//				config.putString("transports", "xhr-polling,jsonp-polling,htmlfile");
			}
		});

		io.sockets().onConnection(new Handler<SocketIOSocket>() {
			public void handle(final SocketIOSocket socket) {
				io.sockets().emit("this", new JsonObject().putString("will", "be received by everyone"));

				socket.on("private message", new Handler<JsonObject>() {
					public void handle(JsonObject msg) {
						System.out.println("I received " + msg);
					}
				});

				socket.on("msg", new Handler<JsonObject>() {
					public void handle(JsonObject event) {
						socket.emit("msg", event);
					}
				});

				socket.on("event", new Handler<JsonObject>() {
					public void handle(JsonObject event) {
						socket.emit("event");
					}
				});

				socket.on("global event", new Handler<JsonObject>() {
					public void handle(JsonObject event) {
						io.sockets().emit("global event");
					}
				});

				socket.on("message", new Handler<JsonObject>() {
					public void handle(JsonObject event) {
						String message = event.getString("message");
						System.out.println(message);
						socket.send(message);
					}
				});

				socket.on("broadcast", new Handler<JsonObject>() {
					public void handle(JsonObject event) {
						socket.volatilize().emit("broadcast", new JsonObject().putString("msg", "hello"));
					}
				});

				socket.on("broadcast", new Handler<JsonObject>() {
					public void handle(JsonObject event) {
						socket.broadcast().emit("broadcast");
					}
				});

				socket.onDisconnect(new Handler<JsonObject>() {
					public void handle(JsonObject event) {
						System.out.println("disconnect");
						io.sockets().emit("user disconnected");
					}
				});
			}
		});

		server.listen(port);
	}

	public static void main(String[] args) throws Exception {
		Vertx vertx = new DefaultVertx();
		SendingAndReceivingEvents app = new SendingAndReceivingEvents(vertx);
		app.start();
		Thread.sleep(Long.MAX_VALUE);
	}

}
