import com.nhncorp.mods.socket.io.SocketIOServer;
import com.nhncorp.mods.socket.io.SocketIOSocket;
import com.nhncorp.mods.socket.io.impl.DefaultSocketIOServer;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.deploy.Verticle;

/**
 * @author Keesun Baik
 */
public class StoringDataAssociatedToClient extends Verticle {

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
						socket.set("nickname", name, new Handler<JsonObject>() {
							public void handle(JsonObject event) {
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
