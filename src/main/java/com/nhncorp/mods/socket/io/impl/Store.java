package com.nhncorp.mods.socket.io.impl;

import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.json.JsonObject;

/**
 * @author Keesun Baik
 */
public interface Store {
	void set(String key, JsonObject value, String prefix, EventBus eventBus);

	void get(String key, String prefix, EventBus eventBus);
}
