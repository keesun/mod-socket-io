package com.nhncorp.mods.socket.io.impl.handlers;

import com.nhncorp.mods.socket.io.SocketIOSocket;
import com.nhncorp.mods.socket.io.impl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.ServerWebSocket;
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
		MultiMap params = clientData.getParams();
		if (params != null && params.get("disconnect") != null) {
			Transport transport = manager.transport(clientData.getId());
			if (transport != null && transport.isOpen()) {
				transport.onForcedDisconnect();
			} else {
				// this.store.publish('disconnect-force:' + data.id);
			}
			request.response().setStatusCode(200);
			request.response().end();
			request.response().close();
			return;
		}

		if (!settings.getTransports().contains(clientData.getTransport())) {
			String message = "unknown transport: '" + clientData.getTransport() + '"';
			log.warn(message);
			manager.writeError(request, 500, message);
			return;
		}

		// handle invalid websocket requests
		if(clientData.getTransport().equals("websocket")) {
			String sessionId = clientData.getId();
			// sessionId에 해당하는 Transport가 이미 있다면... Invalid
			if(sessionId != null && manager.transport(sessionId) != null) {
				log.debug("[Manager] Invalid websocket request. " + clientData.getPath());
				ServerWebSocket socket = clientData.getSocket();
				if(socket != null) {
					socket.close();
				}
				if(request != null) {
					request.response().setStatusCode(200);
					request.response().end();
					request.response().close();
				}
				return;
			}
		}

		Transport transport = manager.newTransport(clientData);
		HandshakeData handshakeData = manager.handshakeData(clientData.getId());
		if (transport.isDisconnected()) {
			// failed during transport setup
			request.response().end();
			request.response().close();
			return;
		}

		if (handshakeData == null) {
			if (transport.isOpen()) {
				transport.error("client not handshaken", "reconnect");
			}
			transport.discard();
			return;
		}

		if (transport.isOpen()) {
			String sessionId = clientData.getId();
			List<Buffer> buffers = manager.closed(sessionId);
			log.debug("[HttpRequestHandler] Check closed session=" + sessionId + ", buffers=" + buffers);
			if (buffers != null && buffers.size() > 0) {
				log.debug("[HttpRequestHandler] Payload closed " + buffers);
				manager.removeClosed(sessionId);
				transport.payload(buffers);
			}

			manager.onOpen(sessionId);
			//			this.store.publish('open', data.id);
			manager.putTransport(sessionId, transport);
		}

		String sessionId = clientData.getId();
		Boolean connected = manager.connected(sessionId);
		if (connected == null || !connected) {
			manager.onConnect(sessionId);
			//			this.store.publish('connect', data.id);

			// flag as used
			if (handshakeData != null) {
				handshakeData.setIssued(-1l);
			}

			manager.onHandshake(sessionId, handshakeData);
			//			this.store.publish('handshake', data.id, handshaken);

			// initialize the socket for all namespaces
			//			Handler<SocketIOSocket> socketHandler = manager.getSocketHandler();
			for (Namespace namespace : manager.getNamespaceValues()) {
				namespace.socket(sessionId, true);
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
