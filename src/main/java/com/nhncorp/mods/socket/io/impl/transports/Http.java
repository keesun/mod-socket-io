package com.nhncorp.mods.socket.io.impl.transports;

import com.nhncorp.mods.socket.io.common.RequestUtils;
import com.nhncorp.mods.socket.io.impl.ClientData;
import com.nhncorp.mods.socket.io.impl.HandshakeData;
import com.nhncorp.mods.socket.io.impl.Manager;
import com.nhncorp.mods.socket.io.impl.Transport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServerResponse;
import org.vertx.java.core.json.JsonObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

/**
 * @see <a href="https://github.com/LearnBoost/socket.io/blob/master/lib/transports/http.js">http.js</a>
 * @author Keesun Baik
 */
public abstract class Http extends Transport {

	private static final Logger log = LoggerFactory.getLogger(Http.class);
	private static final long REQUEST_TTL = 10 * 1000; // 요청의 유효 시간을 10초로 지정한다.
	protected HttpServerResponse response;
	protected boolean isValid = true;

	public Http(Manager manager, ClientData clientData) {
		super(manager, clientData);
	}

	/**
	 * Handles a request.
	 *
	 * @see "HTTPTransport.prototype.handleRequest"
	 */
	@Override
	protected void handleRequest() {
		// Always set the response in case an error is returned to the client
		this.response = request.response;

		this.isValid = isValidRequest();
		if (!this.isValid) {
			String clientIp = RequestUtils.getRemoteAddress(request);
			log.debug("invalid client ip is '" + clientIp + "'");
			response.statusCode = 200;
			response.end();
			response.close();
			return;
		}

		if (!request.method.toUpperCase().equals("POST")) {
			super.handleRequest();
		} else {
			final Buffer buffer = new Buffer(0);
			final HttpServerResponse res = request.response;
			String origin = request.headers().get("ORIGIN");
			Map<String, Object> resHeaders = res.headers();
			resHeaders.put("Content-Length", 1);
			resHeaders.put("Content-Type", "text/plain; charset=UTF-8");

			if (origin != null) {
				// https://developer.mozilla.org/En/HTTP_Access_Control
				resHeaders.put("Access-Control-Allow-Origin", origin);
				resHeaders.put("Access-Control-Allow-Credentials", "true");
			}

			request.dataHandler(new Handler<Buffer>() {
				public void handle(Buffer data) {
					buffer.appendBuffer(data);
					if (buffer.length() >= manager.getSettings().getDestryBufferSize()) {
						resetBuffer(buffer);
						request.response.end();
						request.response.close();
					}
				}
			});

			request.endHandler(new Handler<Void>() {
				public void handle(Void event) {
					res.statusCode = 200;
					res.end("1");
					res.close();

					onData(isPostEncoded() ? parseeData(buffer) : buffer);
				}
			});

			// req.on('close', function () {
			request.exceptionHandler(new Handler<Exception>() {
				public void handle(Exception event) {
					resetBuffer(buffer);
					onClose();
				}
			});
		}

	}

	protected boolean isValidRequest() {
		String tValue = request.params().get("t");
		if (tValue == null) {
			log.debug("[Http] Invalid request. 'it doesn't have 't' parameter', uri=" + request.uri);
			return false;
		}

		try {
			long newTime = Long.parseLong(tValue);
			HandshakeData handshakeData = manager.handshakeData(clientData.getId());

			if(handshakeData == null) {
				return false;
			}

			long oldTime = handshakeData.getLastRequestTime();

			if (oldTime != 0 && oldTime - newTime > REQUEST_TTL) {
				// 먼저 요청보다 TTL 시간보다 더 이전이라면  잘못된 요청으로 무시한다.
				log.debug("[Http] Invalid request, 'it's 't' parameter value(" + newTime
						+ ") is older then the last time(" + oldTime + ")', uri=" + request.uri);
				return false;
			}
			handshakeData.setLastRequestTime(newTime);
		} catch (NumberFormatException nfe) {
			log.debug("[Http] Invalid request. 'it doesn't have 't' parameter', uri=" + request.uri);
			return false;
		}

		return true;
	}

	private Buffer parseeData(Buffer buffer) {
		String d = null;
		try {
			d = URLDecoder.decode(buffer.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e);
		}
		d = d.replaceFirst("d=\"", "");
		d = d.substring(0, d.length() - 1);
		d = d.replace("\\\\", "\\");
		d = d.replace("\\\"", "\"");
		return new Buffer(d);
	}

	protected abstract boolean isPostEncoded();

	/**
	 * Handles data payload.
	 *
	 * @see "HTTPTransport.prototype.onData"
	 * @param data
	 */
	protected void onData(Buffer data) {
		List<JsonObject> messages = parser.decodePayload(data);
		log.debug(getName() + " received data packet " + data);
		for(JsonObject message : messages) {
			onMessage(message);
		}
	}

	private void resetBuffer(Buffer buffer) {
		buffer = new Buffer(0);
	}

	/**
	 * Writes a payload of messages
	 *
	 * @see "HTTPTransport.prototype.payload"
	 * @param buffers
	 */
	@Override
	public void payload(List<Buffer> buffers) {
		this.write(parser.encodePayload(buffers));
	}

	/**
	 * Closes the request-response cycle
	 *
	 * @see "HTTPTransport.prototype.doClose"
	 */
	@Override
	protected void doClose() {
		this.response.end();
	}
}
