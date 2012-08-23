package com.nhncorp.mods.socket.io.impl;

import org.vertx.java.core.json.JsonObject;

/**
 * @author Keesun Baik
 */
public interface Configurer {

	public void configure(JsonObject config);
}
