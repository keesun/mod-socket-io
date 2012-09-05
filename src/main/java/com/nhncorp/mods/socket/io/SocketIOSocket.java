package com.nhncorp.mods.socket.io;

import com.nhncorp.mods.socket.io.impl.HandshakeData;
import org.vertx.java.core.Handler;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.shareddata.Shareable;

import java.util.Map;

/**
 * @author Keesun Baik
 */
public interface SocketIOSocket extends Shareable {
	String getId();

	void emit(String event, JsonObject message);

	void on(String event, Handler<JsonObject> handler);

	Map<String,Handler<JsonArray>> getAcks();

	void packet(JsonObject packet);

	void onConnection();

	boolean isReadable();

	void onDisconnect(String reason);

	void emit(JsonObject packet);

	void emitDisconnect(String reason);

	SocketIOSocket join(String room);

	SocketIOSocket join(String room, Handler<Void> handler);

	SocketIOSocket leave(String room);

	SocketIOSocket leave(String room, Handler<Void> handler);

	SocketIOSocket json();

	SocketIOSocket volatilize();

	SocketIOSocket broadcast();

	SocketIOSocket to(final String room);

	SocketIOSocket in(final String room);

	void onDisconnect(Handler<JsonObject> disconnected);

	void emit(String event);

	void send(String message);

	void set(String key, JsonObject value, Handler<Void> handler);

	void get(String key, Handler<JsonObject> handler);

	void has(String key, Handler<Boolean> handler);

	void del(String key, Handler<Void> handler);

	void emit(String event, String data);

	HandshakeData handshakeData();
}
