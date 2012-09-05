import com.nhncorp.mods.socket.io.SocketIOServer;
import com.nhncorp.mods.socket.io.SocketIOSocket;
import com.nhncorp.mods.socket.io.impl.DefaultSocketIOServer;
import com.nhncorp.mods.socket.io.impl.Room;
import com.nhncorp.mods.socket.io.impl.RoomClient;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.deploy.Verticle;

import java.util.Map;

/**
 * @author Keesun Baik
 */
public class UsingRooms extends Verticle {

	public UsingRooms() {
	}

	public UsingRooms(Vertx vertx) {
		this.vertx = vertx;
	}

	@Override
	public void start() throws Exception {
		int port = 9191;
		HttpServer server = vertx.createHttpServer();
		final SocketIOServer io = new DefaultSocketIOServer(vertx, server);
		io.sockets().onConnection(new Handler<SocketIOSocket>() {
			public void handle(final SocketIOSocket socket) {
				socket.on("subscribe", new Handler<JsonObject>() {
					public void handle(JsonObject data) {
						String room = data.getString("room");
						socket.join(room);
						socket.emit("join:" + room);

						print(io.sockets().manager().rooms());
						print(io.sockets().clients(room));
						print(io.sockets().manager().roomClients(socket.getId()));
					}


				});
				socket.on("emit", new Handler<JsonObject>() {
					public void handle(JsonObject data) {
						String room = data.getString("room");
						io.sockets().in(room).emit("emit", data);
					}
				});
				socket.on("unsubscribe", new Handler<JsonObject>() {
					public void handle(JsonObject data) {
						String room = data.getString("room");
						socket.leave(room);
						socket.emit("leave:" + room);

						print(io.sockets().manager().rooms());
						print(io.sockets().clients(room));
						print(io.sockets().manager().roomClients(socket.getId()));
					}
				});
			}
		});
		server.listen(port);
	}

	private void print(RoomClient roomClient) {
		System.out.println("===================== RoomClient ===================");
		System.out.println(roomClient.toString());
	}

	private void print(String[] clients) {
		System.out.println("===================== Clients ===================");
		if(clients == null) {
			return;
		}
		for(String client : clients) {
			System.out.println(client);
		}
	}

	private void print(Map<String, Room> rooms) {
		System.out.println("===================== Rooms ===================");
		for(Map.Entry<String, Room> entry : rooms.entrySet()){
			System.out.println(entry.getKey());
			Room room  = entry.getValue();
			for(String client : room.values()) {
				System.out.println(client);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		Vertx vertx = Vertx.newVertx();
		UsingRooms app = new UsingRooms(vertx);
		app.start();
		Thread.sleep(Long.MAX_VALUE);
	}
}
