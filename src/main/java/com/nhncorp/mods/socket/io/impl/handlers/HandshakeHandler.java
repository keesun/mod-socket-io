package com.nhncorp.mods.socket.io.impl.handlers;

import com.nhncorp.mods.socket.io.impl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.http.HttpServerRequest;

import java.util.Map;
import java.util.UUID;

/**
 * Handles a handshake request.
 *
 * @see <a href="https://github.com/LearnBoost/socket.io/blob/master/lib/manager.js">manager.js</a>
 * @see "Manager.prototype.handleHandshake"
 * @author Keesun Baik
 */
public class HandshakeHandler {

	private static final Logger log = LoggerFactory.getLogger(HandshakeHandler.class);

	private Manager manager;

	public HandshakeHandler(Manager manager) {
		this.manager = manager;
	}

	/**
	 * Handles a handshake request.
	 *
	 * @see "Manager.prototype.handleHandshake"
	 * @param clientData
	 */
	public void handle(final ClientData clientData) {
		final HttpServerRequest request = clientData.getRequest();
		final Settings settings = manager.getSettings();

		log.trace("In handshake handler");
		Map<String, String> reqHeaders = request.headers();
		final Map<String, Object> resHeaders = request.response.headers();
		String reqOrigin = reqHeaders.get("Origin");
		resHeaders.put("CONTENT_TYPE", "text/plain; charset=UTF-8");

		if (!this.verifyOrigin(reqOrigin)) {
			manager.writeError(request, 403, "handshake bad origin");
			return;
		}

		final HandshakeData handshakeData = new HandshakeData(clientData);

		if (reqOrigin != null) {
			// https://developer.mozilla.org/En/HTTP_Access_Control
			resHeaders.put("Access-Control-Allow-Origin", reqOrigin);
			resHeaders.put("Access-Control-Allow-Credentials", "true");
		}

		authorize(request, handshakeData, new AuthorizationCallback() {
			public void handle(Exception e, boolean isAuthorized) {
				if(e != null) {
					manager.writeError(request, e);
					return;
				}

				if(isAuthorized) {
					String id = UUID.randomUUID().toString();
					String result = id + ":"
							+ (settings.isHeartbeats() ? settings.getHeartbeatTimeout() : "") + ":"
							+ settings.getCloseTimeout() + ":"
							+ settings.getTransports();

					request.response.statusCode = 200;
					String jsonp = clientData.getParams().get("jsonp");
					if(jsonp != null) {
						result = "io.j[" + jsonp + "](\"" + result + "\");";
						resHeaders.put("Content-Type", "application/javascript");
					}
					request.response.end(result);

					manager.getHandshaken().put(id, handshakeData);
					// self.store.publish('handshake', id, newData || handshakeData);
					log.info("handshake authorized " + id);
				} else {
					manager.writeError(request, 403, "handshake unauthorized");
					log.info("handshake unauthorized");
				}
			}
		});
	}

	/**
	 * Performs authentication.
	 *
	 * @see "Manager.prototype.authorize"
	 * @param request
	 * @param handshakeData
	 * @param authorizeCallback
	 */
	private void authorize(HttpServerRequest request, HandshakeData handshakeData, final AuthorizationCallback authorizeCallback) {
		AuthorizationHandler globalAuthHandler = manager.getGlobalAuthorizationHandler();
		if(manager.getSettings().isAuthorization()) {
			if(globalAuthHandler == null) {
				manager.writeError(request, 500, "global authorization callback is requireed");
				return;
			}
			globalAuthHandler.handle(handshakeData, new AuthorizationCallback() {
				@Override
				public void handle(Exception e, boolean isAuthorized) {
					log.debug("client " + (isAuthorized ? "authorized" : "unauthorized"));
					authorizeCallback.handle(e, isAuthorized);
				}
			});
		} else {
			log.debug("client authorized");
			authorizeCallback.handle(null, true);
		}
	}

	/**
	 * Verifies the origin of a request.
	 *
	 * @see "Manager.prototype.verifyOrigin"
	 * @param origin
	 * @return
	 */
	private boolean verifyOrigin(String origin) {
		String origins = manager.getSettings().getOrigins();
		if(origin == null) {
			origin = "*";
		}

		if(origins.indexOf("*:*") != -1) {
			return true;
		}

		String hostname = origin.split("://")[1];
		String port = null;
		int semicolonIndex = origin.indexOf(":");
		if(semicolonIndex != -1) {
			String[] parts = hostname.split(":");
			hostname = parts[0];
			port = parts[1];
		}

		if(origins.indexOf(hostname + ":" + port) != -1) return true;
		if(origins.indexOf(hostname + ":*") != -1) return true;
		if(origins.indexOf("*:" + port) != -1) return true;

		return false;
	}
}
