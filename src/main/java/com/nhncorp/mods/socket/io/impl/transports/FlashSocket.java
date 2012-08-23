package com.nhncorp.mods.socket.io.impl.transports;

import com.nhncorp.mods.socket.io.impl.ClientData;
import com.nhncorp.mods.socket.io.impl.Manager;

/**
 * @author Keesun Baik
 */
public class FlashSocket extends WebSocketTransport {

	public FlashSocket(Manager manager, ClientData clientData) {
		super(manager, clientData);
	}

	@Override
	protected String getName() {
		return "flashsocket";
	}
}
