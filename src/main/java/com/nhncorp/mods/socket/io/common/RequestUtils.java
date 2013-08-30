package com.nhncorp.mods.socket.io.common;

import org.vertx.java.core.http.HttpServerRequest;

/**
 * @author Keesun Baik
 *
 * @deprecated use {@link org.vertx.java.core.http.HttpServerRequest#remoteAddress()}
 */
public class RequestUtils {

	public static String getRemoteAddress(HttpServerRequest req) {
		return req.remoteAddress().getHostName();
	}

}
