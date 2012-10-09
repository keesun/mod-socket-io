import com.nhncorp.mods.socket.io.SocketIOServer;
import com.nhncorp.mods.socket.io.SocketIOSocket;
import com.nhncorp.mods.socket.io.impl.DefaultSocketIOServer;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.deploy.Verticle;

/**
 * 성능, 안정성 테스트에 사용할 Socket.io 서버.
 *
 * @author Keesun Baik
 */
public class MessageServer extends Verticle {

	public MessageServer() {
	}

	public MessageServer(Vertx vertx) {
		this.vertx = vertx;
	}

	@Override
	public void start() throws Exception {
		int port = 19090;
		HttpServer server = vertx.createHttpServer();
		final SocketIOServer io = new DefaultSocketIOServer(vertx, server);

		io.sockets().onConnection(new Handler<SocketIOSocket>() {
			public void handle(final SocketIOSocket socket) {
				socket.on("send", new Handler<JsonObject>() {
					public void handle(JsonObject message) {
						socket.broadcast().emit("msg", message);
					}
				});
			}
		});

		server.listen(port);
	}

	public static void main(String[] args) throws Exception {
		Vertx vertx = Vertx.newVertx();
		MessageServer app = new MessageServer(vertx);
		app.start();
		Thread.sleep(Long.MAX_VALUE);
	}

}
