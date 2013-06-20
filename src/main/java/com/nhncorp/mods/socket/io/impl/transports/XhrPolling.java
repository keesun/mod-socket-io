package com.nhncorp.mods.socket.io.impl.transports;

import com.nhncorp.mods.socket.io.impl.ClientData;
import com.nhncorp.mods.socket.io.impl.Manager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.MultiMap;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * @see <a href="https://github.com/LearnBoost/socket.io/blob/master/lib/transports/xhr-polling.js">xhr-polling.js</a>
 * @author Keesun Baik
 */
public class XhrPolling extends HttpPolling {

	private static final Logger log = LoggerFactory.getLogger(XhrPolling.class);

	public XhrPolling(Manager manager, ClientData clientData) {
		super(manager, clientData);
	}

	@Override
	protected boolean isPostEncoded() {
		return false;
	}

	@Override
	protected String getName() {
		return "xhr-polling";
	}

	/**
	 * Frames data prior to write.
	 *
	 * @see "XHRPolling.prototype.doWrite"
	 * @param encodedPacket
	 */
	@Override
	protected void doWrite(String encodedPacket) {
		super.doWrite(encodedPacket);

		String origin = request.headers().get("Origin");
		MultiMap resHeaders = response.headers();
		resHeaders.add("Content-Type", "text/plain; charset=UTF-8");
		resHeaders.add("Content-Length", encodedPacket == null ? "0" : String.valueOf(encodedPacket.getBytes(Charset.forName("UTF-8")).length));
		resHeaders.add("Connection", "Keep-Alive");

		if(origin != null) {
			// https://developer.mozilla.org/En/HTTP_Access_Control
			resHeaders.add("Access-Control-Allow-Origin", origin);
			resHeaders.add("Access-Control-Allow-Credentials", "true");
		}

		response.setStatusCode(200);
		response.write(encodedPacket);
		log.debug(this.getName() + " writing " + encodedPacket);
	}
}