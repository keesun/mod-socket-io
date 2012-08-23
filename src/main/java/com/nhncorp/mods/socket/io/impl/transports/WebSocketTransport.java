package com.nhncorp.mods.socket.io.impl.transports;

import com.nhncorp.mods.socket.io.impl.ClientData;
import com.nhncorp.mods.socket.io.impl.Manager;
import com.nhncorp.mods.socket.io.impl.Transport;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.WebSocket;

import java.util.List;

/**
 * @author Keesun Baik
 */
public class WebSocketTransport extends Transport {

	private WebSocket webSocket;

	public WebSocketTransport(Manager manager, ClientData clientData) {
		super(manager, clientData);

		webSocket = clientData.getSocket();

		webSocket.exceptionHandler(new Handler<Exception>() {
			public void handle(Exception e) {
				end("socket error " + ((e != null) ? e.getMessage() : ""));
			}
		});

		webSocket.closedHandler(new Handler<Void>() {
			public void handle(Void event) {
				end("socket end");
			}
		});

		webSocket.dataHandler(new Handler<Buffer>() {
			public void handle(Buffer buffer) {
				onMessage(parser.decodePacket(buffer.toString()));
			}
		});
	}

	/**
	 * Closes the connection.
	 *
	 * @see "WebSocket.prototype.doClose"
	 */
	@Override
	protected void doClose() {
		webSocket.close();
	}

	@Override
	public void payload(List<Buffer> buffers) {
		this.write(parser.encodePayload(buffers));
	}

	@Override
	public void write(String encodedPacket) {
		webSocket.writeTextFrame(encodedPacket);
	}

	@Override
	protected String getName() {
		return "websocket";
	}
}
