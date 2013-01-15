package com.nhncorp.mods.socket.io.impl;

import com.nhncorp.mods.socket.io.SocketIOSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.impl.VertxInternal;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @see <a href="https://github.com/LearnBoost/socket.io/blob/master/lib/socket.js">socket.js</a>
 * @author Keesun Baik
 */
public class DefaultSocketIOSocket implements SocketIOSocket {

	private static final Logger log = LoggerFactory.getLogger(DefaultSocketIOServer.class);

	private Manager manager;
	private String id;
	private Namespace namespace;
	private boolean readable;
	private JsonObject flags;
	private Parser parser;
	private VertxInternal vertx;
	private Handler<SocketIOSocket> socketHandler;
	private Map<String, Handler<JsonArray>> acks;
	private boolean disconnected;
	private Store store;
	private Map<String, Handler> handlerMap;

	public DefaultSocketIOSocket(Manager manager, String id, Namespace namespace, boolean readable, Handler<SocketIOSocket> socketHandler) {
		this.manager = manager;
		this.store = manager.getStore();
		this.vertx = manager.getVertx();
		this.id = id;
		this.namespace = namespace;
		this.readable = readable;
		this.parser = new Parser();
		this.socketHandler = socketHandler;
		this.acks = new ConcurrentHashMap<>();
		this.handlerMap = new ConcurrentHashMap<>();
		setupFlags();
	}

	/**
	 * Resets flags
	 *
	 * @see "Socket.prototype.setFlags"
	 */
	private void setupFlags() {
		this.flags = new JsonObject();
		flags.putString("endpoint", this.namespace.getName());
		flags.putString("room", Manager.DEFAULT_NSP);
	}

	/**
	 * JSON message flag.
	 */
	public SocketIOSocket json() {
		this.flags.putBoolean("json", true);
		return this;
	}

	public SocketIOSocket volatilize() {
		this.flags.putBoolean("volatile", true);
		return this;
	}

	public SocketIOSocket broadcast() {
		this.flags.putBoolean("broadcast", true);
		return this;
	}

	public SocketIOSocket to(final String room) {
		this.flags.putString("room", room);
		return this;
	}

	public SocketIOSocket in(final String room) {
		this.flags.putString("room", room);
		return this;
	}

	public void onDisconnect(Handler<JsonObject> handler) {
		this.on("disconnect", handler);
	}

	@Override
	public void emit(String event) {
		JsonObject packet = new JsonObject();
		packet.putString("type", "event");
		packet.putString("name", event);
		packet(packet);
	}

	@Override
	public void send(String message) {
		JsonObject packat = new JsonObject();
		packat.putString("type", "message");
		packat.putString("data", message);
		packet(packat);
	}

	/**
	 * Stores data for the client.
	 *
	 * @see "Socket.prototype.set"
	 * @param key
	 * @param value
	 * @param handler
	 */
	@Override
	public void set(String key, JsonObject value, final Handler<Void> handler) {
		this.store.set(key, value, handler);
	}

	/**
	 * Retrieves data for the client
	 *
	 * @see "Socket.prototype.get"
	 * @param key
	 * @param handler
	 */
	@Override
	public void get(String key, final Handler<JsonObject> handler) {
		this.store.get(key, handler);
	}

	@Override
	public void has(String key, final Handler<Boolean> handler) {
		this.store.has(key, handler);
	}

	@Override
	public void del(String key, final Handler<Void> handler) {
		this.store.del(key, handler);
	}

	@Override
	public void emit(String event, String data) {
		JsonObject packet = new JsonObject();
		packet.putString("type", "event");
		packet.putString("name", event);
		packet.putArray("args", new JsonArray().addString(data));
		packet(packet);
	}

	@Override
	public HandshakeData handshakeData() {
		return this.manager.handshakeData(this.id);
	}

	/**
	 * Transmits a packet.
	 *
	 * @see "Socktet.prototype.packet"
	 * @param packet
	 * @return
	 */
	public void packet(JsonObject packet) {
		if(this.flags.getBoolean("broadcast", false)) {
			log.debug("broadcasting packet");
			this.namespace.in(this.flags.getString("room")).except(this.getId()).packet(packet);
		} else {
			packet.putString("endpoint", this.flags.getString("endpoint"));
			String encodedPacket = parser.encodePacket(packet);
			this.dispatch(encodedPacket, this.flags.getBoolean("volatile", false));
		}

		this.setupFlags();
	}

	/**
	 * Dispatches a packet
	 *
	 * @see "Socket.prototype.dispatch"
	 * @param encodedPacket
	 * @param isVolatile
	 */
	private void dispatch(String encodedPacket, boolean isVolatile) {
		Transport transport = this.manager.getTranport(this.id);
		if( transport != null && transport.isOpen() ) {
			transport.onDispatch(encodedPacket, isVolatile);
		} else {
			if (!isVolatile) {
				this.manager.onClientDispatch(this.id, encodedPacket);
			}
//			this.manager.getStore().publich("dispatch:" + this.id, packet, isVolatile);
		}
	}

	/**
	 * Emit override for custom events.
	 *
	 * @see "Socket.prototype.emit"
	 * @param event
	 * @param jsonObject
	 * @return
	 */
	public void emit(String event, JsonObject jsonObject) {
//		if (ev == 'newListener') {
//			return this.$emit.apply(this, arguments);
//		}

		JsonObject packet = new JsonObject();
		packet.putString("type", "event");
		packet.putString("name", event);

//		if ('function' == typeof lastArg) {
//			packet.id = ++this.ackPackets;
//			packet.ack = lastArg.length ? 'data' : true;
//			this.acks[packet.id] = lastArg;
//			args = args.slice(0, args.length - 1);
//		}
		if(jsonObject != null) {
			JsonArray args = new JsonArray();
			args.addObject(jsonObject);
			packet.putArray("args", args);
		}
		this.packet(packet);
	}

	/**
	 * emit disconnection
	 *
	 * @param reason
	 */
	public void emitDisconnect(String reason) {
		JsonObject packet = new JsonObject();
		packet.putString("reason", reason);
		packet.putString("name", "disconnect");
		emit(packet);
	}

	/**
	 * Joins a user to a room.
	 *
	 * @see "Socket.prototype.join"
	 * @param room
	 */
	@Override
	public SocketIOSocket join(String room) {
		String roomName = namespace.getName() + "/" + room;
		this.manager.onJoin(this.id, roomName);
//		this.manager.store.publish('join', this.id, name);
		return this;
	}

	@Override
	public SocketIOSocket join(String room, Handler<Void> handler) {
		join(room);
		handler.handle(null);
		return this;
	}

	/**
	 * Un-joins a user from a room.
	 *
	 * @see "Socket.prototype.leave"
	 * @param room
	 */
	@Override
	public SocketIOSocket leave(String room) {
		String roomName = namespace.getName() + "/" + room;
		this.manager.onLeave(this.id, roomName);
//		this.manager.store.publish('leave', this.id, name);
		return this;
	}

	@Override
	public SocketIOSocket leave(String room, Handler<Void> handler) {
		leave(room);
		handler.handle(null);
		return this;
	}

	/**
	 * register handler to an event.
	 *
	 * @param event
	 * @param handler
	 */
	public void on(String event, final Handler<JsonObject> handler) {
		String address = id + ":" + namespace.getName() + ":" + event;
		Handler<Message<JsonObject>> localHandler = new Handler<Message<JsonObject>>() {
			public void handle(Message<JsonObject> event) {
				handler.handle(event.body);
			}
		};
		vertx.eventBus().registerHandler(address, localHandler);
		handlerMap.put(address, localHandler);
	}

	/**
	 * execute socket handler.
	 */
	public synchronized void onConnection() {
		if(this.socketHandler != null) {
			socketHandler.handle(this);
		}
	}

	// $emit
	public void emit(JsonObject params) {
		String name = params.getString("name", "message");
		JsonObject packet = flatten(params.getArray("args"));
		if(name.equals("disconnect")) {
			packet.putString("reason", params.getString("reason"));
		}

		Object message = params.getField("message");
		if(message != null) {
			if(message instanceof String) {
				packet.putString("message", params.getString("message"));
			} else if(message instanceof JsonObject) {
				packet.putObject("message", params.getObject("message"));
			}
		}

		vertx.eventBus().send(id + ":" + namespace.getName() + ":" + name, packet);
	}

	private JsonObject flatten(JsonArray jsonArray) {
		if(jsonArray == null) {
			return new JsonObject();
		}

		JsonObject result = new JsonObject();

		Iterator<Object> iterator = jsonArray.iterator();
		while (iterator.hasNext()) {
			Object o = iterator.next();
			if(o instanceof JsonObject) {
				result.mergeIn((JsonObject)o);
			} else if(o instanceof String) {
				if(result.getField("data") == null) {
					result.putString("data", (String)o);
				}
			}
		}

		return result;
	}

	/**
	 * Triggered on disconnect
	 *
	 * @see "Socket.prototype.onDisconnect"
	 * @param reason
	 */
	public void onDisconnect(String reason) {
		if(!this.disconnected) {
			emitDisconnect(reason);
			for(Map.Entry<String, Handler> entry : handlerMap.entrySet()) {
				vertx.eventBus().unregisterHandler(entry.getKey(), entry.getValue());
				handlerMap.remove(entry.getKey());
			}
			handlerMap.clear();
			this.disconnected = true;
		}
	}

	public Map<String, Handler<JsonArray>> getAcks() {
		return acks;
	}

	public Manager getManager() {
		return manager;
	}

	public String getId() {
		return id;
	}

	public Namespace getNamespace() {
		return namespace;
	}

	public boolean isReadable() {
		return readable;
	}
}
