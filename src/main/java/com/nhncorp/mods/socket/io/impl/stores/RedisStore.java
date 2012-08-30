package com.nhncorp.mods.socket.io.impl.stores;

import com.nhncorp.mods.socket.io.impl.Store;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.json.JsonObject;

/**
 * @author Keesun Baik
 */
public class RedisStore implements Store {
	@Override
	public void set(String key, JsonObject value, String prefix, EventBus eventBus) {
	}

	@Override
	public void get(String key, String prefix, EventBus eventBus) {
	}
}
