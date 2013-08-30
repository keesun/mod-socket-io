package whiteship;

import com.nhncorp.mods.socket.io.SocketIOServer;
import com.nhncorp.mods.socket.io.SocketIOSocket;
import com.nhncorp.mods.socket.io.impl.DefaultSocketIOServer;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

/**
 * @author Keesun Baik
 */
public class EchoServer extends Verticle {

    @Override
    public void start() {
        int port = 9999;
        HttpServer server = vertx.createHttpServer();
        SocketIOServer io = new DefaultSocketIOServer(vertx, server);

        io.sockets().onConnection(new Handler<SocketIOSocket>() {
            @Override
            public void handle(final SocketIOSocket socket) {
                socket.emit("Hello");
                socket.on("/news", new Handler<JsonObject>() {
                    @Override
                    public void handle(JsonObject data) {
                        System.out.println(data);
                        socket.emit(data);
                    }
                });
            }
        });

        server.listen(port);
    }
}
