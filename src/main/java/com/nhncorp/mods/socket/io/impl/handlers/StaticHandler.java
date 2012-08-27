package com.nhncorp.mods.socket.io.impl.handlers;

import com.nhncorp.mods.socket.io.impl.ClientData;
import com.nhncorp.mods.socket.io.impl.Manager;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.HttpServerResponse;

import java.util.Arrays;
import java.util.List;

/**
 * @author Keesun Baik
 */
public class StaticHandler {

	private static final List<String> STAIC_FLIE_NAMES = Arrays.asList(new String[]{
			"/static/flashsocket/WebSocketMainInsecure.swf",
			"/static/flashsocket/WebSocketMain.swf",
			"/socket.io.js",
			"/socket.io.min.js"
	});

	private Manager manager;

	public StaticHandler(Manager manager) {
		this.manager = manager;
	}

	public void handle(ClientData clientData) {
		HttpServerRequest req = clientData.getRequest();
		HttpServerResponse res = req.response;
		String requestedFileName = clientData.getPath();
		String resourceRootDir = System.getProperty("user.dir") + "/mods/nhn.socket-io-v0.9.10/static";

		switch (requestedFileName) {
			case "/static/flashsocket/WebSocketMainInsecure.swf":
				res.sendFile(resourceRootDir + "/WebSocketMainInsecure.swf");
				break;
			case "/static/flashsocket/WebSocketMain.swf":
				res.sendFile(resourceRootDir + "/WebSocketMain.swf");
				break;
			case "/socket.io.js":
				res.sendFile(resourceRootDir + "/socket.io.js");
				break;
			case "/socket.io.min.js":
				res.sendFile(resourceRootDir + "/socket.io.min.js");
				break;
			default:
				throw new IllegalArgumentException(requestedFileName);
		}
		res.close();
	}

	public static boolean has(String path) {
		return STAIC_FLIE_NAMES.contains(path);
	}

}
