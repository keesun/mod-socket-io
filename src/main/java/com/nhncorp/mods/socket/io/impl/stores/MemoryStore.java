package com.nhncorp.mods.socket.io.impl.stores;

import com.nhncorp.mods.socket.io.impl.Store;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.json.JsonObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Keesun Baik
 */
public class MemoryStore implements Store {

	private Map<String, JsonObject> data = new ConcurrentHashMap<>();

	/**
	 * Sets a key
	 *
	 * @see "Client.prototype.set"
	 * @param key
	 * @param value
	 * @param prefix
	 * @param eventBus
	 */
	@Override
	public void set(String key, JsonObject value, String prefix, EventBus eventBus) {
		data.put(key, value);
		eventBus.send(prefix + key, value);
	}

	/**
	 * Gets a key
	 *
	 * @see "Client.prototype.get"
	 * @param key
	 * @param prefix
	 * @param eventBus
	 */
	@Override
	public void get(String key, String prefix, EventBus eventBus) {
		eventBus.publish(prefix + key, data.get(key));
	}

}
