package com.nhncorp.mods.socket.io.impl.handlers;

import com.nhncorp.mods.socket.io.SocketIOSocket;
import com.nhncorp.mods.socket.io.impl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonObject;

import java.util.List;
import java.util.Map;

/**
 * Handles a normal handshaken HTTP request (eg: long-polling)
 *
 * @see <a href="https://github.com/LearnBoost/socket.io/blob/master/lib/manager.js">manager.js</a>
 * @see "Manager.prototype.handleHTTPRequest"
 * @author Keesun Baik
 */
public class HttpRequestHandler {

	private static final Logger log = LoggerFactory.getLogger(HttpRequestHandler.class);

	private Manager manager;

	public HttpRequestHandler(Manager manager) {
		this.manager = manager;
	}

	/**
	 * Intantiantes a new client.
	 *
	 * @see "Manager.prototype.handleClient"
	 * @param clientData
	 */
	public void handle(ClientData clientData) {
		HttpServerRequest request = clientData.getRequest();
		Settings settings = manager.getSettings();

		// handle sync disconnect xhrs
		Map<String,String> params = clientData.getParams();
		if(params != null && params.get("disconnect") != null) {
			Transport transport = manager.transport(clientData.getId());
			if(transport != null && transport.isOpen()) {
				transport.onForcedDisconnect();
			} else {
//				this.store.publish('disconnect-force:' + data.id);
			}
			request.response.statusCode = 200;
			request.response.end();
			return;
		}

		if(!settings.getTransports().contains(clientData.getTransport())) {
			String message = "unknown transport: '" + clientData.getTransport() + '"';
			log.warn(message);
			manager.writeError(request, 500, message);
			return;
		}

		Transport transport = manager.newTransport(clientData);
		HandshakeData handshakeData = manager.handshakeData(clientData.getId());
		if (transport.isDisconnected()) {
			// failed during transport setup
			request.response.end();
			return;
		}

		if(handshakeData == null) {
			if(transport.isOpen()) {
				transport.error("client not handshaken", "reconnect");
			}
			transport.discard();
		}

		if (transport.isOpen()) {
			String sessionId = clientData.getId();
			List<Buffer> buffers = manager.closed(sessionId);
			if (buffers != null && buffers.size() > 0) {
				transport.payload(buffers);
				manager.removeClosed(sessionId);
			}

			manager.onOpen(sessionId);
//			this.store.publish('open', data.id);
			manager.putTransport(sessionId, transport);
		}

		String sessionId = clientData.getId();
		Boolean connected = manager.connected(sessionId);
		if(connected == null || !connected) {
			manager.onConnect(sessionId);
//			this.store.publish('connect', data.id);

			// flag as used
			handshakeData.setIssued(-1l);
			manager.onHandshake(sessionId, handshakeData);
//			this.store.publish('handshake', data.id, handshaken);

			// initialize the socket for all namespaces
//			Handler<SocketIOSocket> socketHandler = manager.getSocketHandler();
			for(Namespace namespace : manager.getNamespaceValues()) {
				SocketIOSocket socket = namespace.socket(sessionId, true);
				// echo back connect packet and fire connection event
				if (namespace.getName().equals(Manager.DEFAULT_NSP)) {
					JsonObject jsonObject = new JsonObject();
					jsonObject.putString("type", "connect");
					namespace.handlePacket(sessionId, jsonObject);
				}
			}
//			this.store.subscribe('message:' + data.id, function (packet) {
//				self.onClientMessage(data.id, packet);
//			});
//
//			this.store.subscribe('disconnect:' + data.id, function (reason) {
//				self.onClientDisconnect(data.id, reason);
//			});
		}
	}

}
