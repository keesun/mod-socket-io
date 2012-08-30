package com.nhncorp.mods.socket.io.impl.stores;

import com.nhncorp.mods.socket.io.impl.Store;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.json.JsonObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Keesun Baik
 */
public class MemoryStore implements Store {

	private Map<String, JsonObject> data = new ConcurrentHashMap<>();

//	/**
//	 * Sets a key
//	 *
//	 * @see "Client.prototype.set"
//	 * @param key
//	 * @param value
//	 * @param prefix
//	 * @param eventBus
//	 */
//	@Override
//	public void set(String key, JsonObject value, String prefix, EventBus eventBus) {
//		data.put(key, value);
//		eventBus.send(prefix + key, value);
//	}
//
//	/**
//	 * Gets a key
//	 *
//	 * @see "Client.prototype.get"
//	 * @param key
//	 * @param prefix
//	 * @param eventBus
//	 */
//	@Override
//	public void get(String key, String prefix, EventBus eventBus) {
//		eventBus.send(prefix + key, data.get(key));
//	}
//
//	/**
//	 * Has a key
//	 *
//	 * @see "Client.prototype.has"
//	 * @param key
//	 * @param prefix
//	 * @param eventBus
//	 */
//	@Override
//	public void has(String key, String prefix, EventBus eventBus) {
//		eventBus.send(prefix + key, data.keySet().contains(key));
//	}
//
//	/**
//	 * Deletes a key
//	 *
//	 * @see "Client.prototype.del"
//	 * @param key
//	 * @param prefix
//	 * @param eventBus
//	 */
//	@Override
//	public void del(String key, String prefix, EventBus eventBus) {
//		eventBus.send(prefix + key, data.remove(key));
//	}

	@Override
	public void del(String key, Handler<Void> handler) {
		data.remove(key);
		handler.handle(null);
	}

	@Override
	public void has(String key, Handler<Boolean> handler) {
		handler.handle(data.keySet().contains(key));
	}

	@Override
	public void get(String key, Handler<JsonObject> handler) {
		handler.handle(data.get(key));
	}

	@Override
	public void set(String key, JsonObject value, Handler<Void> handler) {
		data.put(key, value);
		handler.handle(null);
	}
}
