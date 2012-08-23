package com.nhncorp.mods.socket.io.impl;

/**
 * @author Keesun Baik
 */
public interface AuthorizationHandler {

	public void handle(HandshakeData handshakeData, AuthorizationCallback callback);
}
