import com.nhncorp.mods.socket.io.SocketIOServer;
import com.nhncorp.mods.socket.io.SocketIOSocket;
import com.nhncorp.mods.socket.io.impl.DefaultSocketIOServer;
import com.nhncorp.mods.socket.io.impl.Namespace;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.deploy.Verticle;

/**
 * @author Keesun Baik
 */
public class RestrictingYourselfToANamespace extends Verticle {

	public RestrictingYourselfToANamespace() {
	}

	public RestrictingYourselfToANamespace(Vertx vertx) {
		this.vertx = vertx;
	}

	@Override
	public void start() throws Exception {
		int port = 9191;
		HttpServer server = vertx.createHttpServer();
		SocketIOServer io = new DefaultSocketIOServer(vertx, server);

		final Namespace chat = io.of("/chat");
		chat.onConnection(new Handler<SocketIOSocket>() {
			public void handle(SocketIOSocket socket) {
				socket.on("namespace emit", new Handler<JsonObject>() {
					public void handle(JsonObject event) {
						chat.emit("msg", new JsonObject().putString("everyone", "in").putString("/chat", "will get"));
					}
				});
			}
		});

		final Namespace news = io.of("/news");
		news.onConnection(new Handler<SocketIOSocket>() {
			public void handle(SocketIOSocket socket) {
				socket.emit("item", new JsonObject().putString("news", "item"));
			}
		});

		server.listen(port);
	}

	public static void main(String[] args) throws Exception {
		Vertx vertx = Vertx.newVertx();
		RestrictingYourselfToANamespace app = new RestrictingYourselfToANamespace(vertx);
		app.start();
		Thread.sleep(Long.MAX_VALUE);
	}

}
