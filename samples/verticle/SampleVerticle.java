import com.nhncorp.mods.socket.io.SocketIOServer;
import com.nhncorp.mods.socket.io.SocketIOSocket;
import com.nhncorp.mods.socket.io.impl.DefaultSocketIOServer;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.deploy.Verticle;

/**
 * @author Keesun Baik
 */
public class SampleVerticle extends Verticle {

	@Override
	public void start() throws Exception {
		int port = 9090;
		HttpServer server = vertx.createHttpServer();
		SocketIOServer io = new DefaultSocketIOServer(vertx, server);

		io.sockets().onConnection(new Handler<SocketIOSocket>() {
			public void handle(final SocketIOSocket socket) {
				socket.on("timer", new Handler<JsonObject>() {
					public void handle(JsonObject event) {
						socket.emit("timer", event);
					}
				});
			}
		});

		System.out.println("server is running on http://localshot:" + port);
		server.listen(port);
	}
}
