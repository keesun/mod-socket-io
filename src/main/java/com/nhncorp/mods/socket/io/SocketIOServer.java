package com.nhncorp.mods.socket.io;

import com.nhncorp.mods.socket.io.impl.AuthorizationHandler;
import com.nhncorp.mods.socket.io.impl.Configurer;
import com.nhncorp.mods.socket.io.impl.Namespace;
import org.vertx.java.core.json.JsonObject;

/**
 * @author Keesun Baik
 */
public interface SocketIOServer {

	SocketIOServer configure(String env, Configurer configurer);

	SocketIOServer configure(String env, JsonObject newConfig);

	SocketIOServer configure(Configurer configurer);

	SocketIOServer configure(JsonObject newConfig);

	SocketIOServer setAuthHandler(AuthorizationHandler globalAuthorizationCallback);

	Namespace sockets();
	
	Namespace of(String name);

}
