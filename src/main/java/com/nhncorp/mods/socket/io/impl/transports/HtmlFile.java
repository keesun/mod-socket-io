package com.nhncorp.mods.socket.io.impl.transports;

import com.nhncorp.mods.socket.io.impl.ClientData;
import com.nhncorp.mods.socket.io.impl.Manager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.json.impl.Json;

/**
 * @see <a href="https://github.com/LearnBoost/socket.io/blob/master/lib/transports/htmlfile.js">htmlfile.js</a>
 * @author Keesun Baik
 */
public class HtmlFile extends Http {

	private static final Logger log = LoggerFactory.getLogger(HtmlFile.class);

	public HtmlFile(Manager manager, ClientData clientData) {
		super(manager, clientData);
	}

	@Override
	protected boolean isPostEncoded() {
		return false;
	}

	@Override
	protected String getName() {
		return "htmlfile";
	}

	/**
	 * Handles the request.
	 *
	 * @see "HTMLFile.prototype.handleRequest"
	 */
	@Override
	protected void handleRequest() {
		super.handleRequest();

		MultiMap headers = response.headers();
		if(request.method().equals("GET")) {
			response.setStatusCode(200);
			headers.add("Content-Type", "text/html; charset=UTF-8");
			headers.add("Connection", "keep-alive");
			headers.add("Transfer-Encoding", "chunked");
		}

		String body = "<html><body><script>var _ = function (msg) { parent.s._(msg, document); };</script>";
		for(int i = body.length() ; i < 256 ; i++) {
			body += " ";
		}
		headers.add("Content-Length", String.valueOf(body.length()));
		response.write(body);
	}

	/**
	 * Performs the write.
	 *
	 * @see "HTMLFile.prototype.write"
	 * @param encodedPacket
	 */
	@Override
	public void write(String encodedPacket) {
		String data = "<script>_(" + Json.encode(encodedPacket) + ");</script>";
        response.drainHandler(new Handler<Void>() {
            @Override
            public void handle(Void aVoid) {
                isDrained = true;
            }
        });
        response.write(data);

		log.debug(this.getName() + " writing " + data);
	}
}
