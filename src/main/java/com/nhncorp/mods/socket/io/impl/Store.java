package com.nhncorp.mods.socket.io.impl;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.json.JsonObject;

/**
 * @author Keesun Baik
 */
public interface Store {
//	void set(String key, JsonObject value, String prefix, EventBus eventBus);
//
//	void get(String key, String prefix, EventBus eventBus);
//
//	void has(String key, String prefix, EventBus eventBus);
//
//	void del(String key, String prefix, EventBus eventBus);

	void del(String key, Handler<Void> handler);

	void has(String key, Handler<Boolean> handler);

	void get(String key, Handler<JsonObject> handler);

	void set(String key, JsonObject value, Handler<Void> handler);
}
