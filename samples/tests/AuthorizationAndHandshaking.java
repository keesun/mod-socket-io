import com.nhncorp.mods.socket.io.SocketIOServer;
import com.nhncorp.mods.socket.io.SocketIOSocket;
import com.nhncorp.mods.socket.io.impl.*;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.impl.DefaultVertx;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

/**
 * @author Keesun Baik
 */
public class AuthorizationAndHandshaking extends Verticle {

	public AuthorizationAndHandshaking() {
	}

	public AuthorizationAndHandshaking(Vertx vertx) {
		this.vertx = vertx;
	}

	@Override
	public void start() {
		int port = 9191;
		HttpServer server = vertx.createHttpServer();
		SocketIOServer io = new DefaultSocketIOServer(vertx, server);

		io.setAuthHandler(new AuthorizationHandler() {
			public void handle(HandshakeData handshakeData, AuthorizationCallback callback) {
				boolean isPass = Boolean.parseBoolean(handshakeData.getQueryParams().get("pass"));
				if(isPass) {
					handshakeData.putString("foo", "bar");
					callback.handle(null, true);
				} else {
					callback.handle(new RuntimeException("reason"), false);
				}
			}
		});

		io.sockets().onConnection(new Handler<SocketIOSocket>() {
			public void handle(final SocketIOSocket socket) {
				socket.on("get", new Handler<JsonObject>() {
					public void handle(JsonObject event) {
						String foo = socket.handshakeData().getString("foo");
						socket.emit("data", new JsonObject().putString("foo", foo));
					}
				});
			}
		});

		io.of("/private").setAuthHandler(new AuthorizationHandler(){
			public void handle(HandshakeData handshakeData, AuthorizationCallback callback) {
				boolean isPass = Boolean.parseBoolean(handshakeData.getQueryParams().get("pass"));
				if(isPass) {
					handshakeData.putString("foo", "baz");
					callback.handle(null, true);
				} else {
					callback.handle(new RuntimeException("reason"), false);
				}
			}
		}).onConnection(new Handler<SocketIOSocket>() {
			public void handle(final SocketIOSocket socket) {
				socket.on("get", new Handler<JsonObject>() {
					public void handle(JsonObject event) {
						String foo = socket.handshakeData().getString("foo");
						socket.emit("data", new JsonObject().putString("foo", foo));
					}
				});
			}
		});

		server.listen(port);
	}

	public static void main(String[] args) throws Exception {
		Vertx vertx = new DefaultVertx();
		AuthorizationAndHandshaking app = new AuthorizationAndHandshaking(vertx);
		app.start();
		Thread.sleep(Long.MAX_VALUE);

	}
}
