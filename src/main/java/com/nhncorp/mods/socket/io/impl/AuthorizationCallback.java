package com.nhncorp.mods.socket.io.impl;

/**
 * @author Keesun Baik
 */
public interface AuthorizationCallback {

	public void handle(Exception e, boolean isAuthorized);
}
