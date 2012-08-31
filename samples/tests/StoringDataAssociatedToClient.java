import com.nhncorp.mods.socket.io.SocketIOServer;
import com.nhncorp.mods.socket.io.SocketIOSocket;
import com.nhncorp.mods.socket.io.impl.DefaultSocketIOServer;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.deploy.Verticle;

/**
 * @author Keesun Baik
 */
public class StoringDataAssociatedToClient extends Verticle {

	public StoringDataAssociatedToClient() {
	}

	public StoringDataAssociatedToClient(Vertx vertx) {
		this.vertx = vertx;
	}

	@Override
	public void start() throws Exception {
		int port = 9191;
		HttpServer server = vertx.createHttpServer();
		SocketIOServer io = new DefaultSocketIOServer(vertx, server);

		io.sockets().onConnection(new Handler<SocketIOSocket>() {
			public void handle(final SocketIOSocket socket) {
				socket.on("set nickname", new Handler<JsonObject>() {
					public void handle(JsonObject name) {
						System.out.println("name: " + name);
						socket.set("nickname", name, new Handler<Void>() {
							public void handle(Void event) {
								System.out.println("set name");
								socket.emit("ready");
							}
						});
					}
				});

				socket.on("get nickname", new Handler<JsonObject>() {
					public void handle(JsonObject event) {
						socket.get("nickname", new Handler<JsonObject>(){
							public void handle(JsonObject data) {
								System.out.println("Chat message by " + data);
								socket.emit("get", data.getString("data"));
							}
						});
					}
				});

				socket.on("has nickname", new Handler<JsonObject>() {
					public void handle(JsonObject event) {
						socket.has("nickname", new Handler<Boolean>() {
							public void handle(Boolean has) {
								System.out.println("has nickname? " + has);
								socket.emit("has", has.toString());
							}
						});
					}
				});

				socket.on("del nickname", new Handler<JsonObject>() {
					public void handle(JsonObject event) {
						socket.del("nickname", new Handler<Void>() {
							public void handle(Void event) {
								System.out.println("del nickname");
								socket.emit("del");
							}
						});
					}
				});

				socket.on("confirm nickname", new Handler<JsonObject>() {
					public void handle(JsonObject event) {
						socket.has("nickname", new Handler<Boolean>() {
							public void handle(Boolean event) {
								System.out.println("has nickname? " + event);
								socket.emit("confirm", event.toString());
							}
						});
					}
				});
			}
		});

		server.listen(port);
	}

	public static void main(String[] args) throws Exception {
		Vertx vertx = Vertx.newVertx();
		StoringDataAssociatedToClient app = new StoringDataAssociatedToClient(vertx);
		app.start();
		Thread.sleep(Long.MAX_VALUE);
	}
}
