package com.nhncorp.mods.socket.io.impl;

import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.shareddata.Shareable;
import org.vertx.java.core.shareddata.impl.SharedMap;

import java.util.Map;
import java.util.Set;

/**
 * @author Keesun Baik
 */
public class RoomClient implements Shareable {

	SharedMap<String, Boolean> rooms;

	public RoomClient() {
		this.rooms = new SharedMap<>();
	}

	public void put(String namespaceName, boolean isIn) {
		rooms.put(namespaceName, isIn);
	}

	public void remove(String roomName) {
		rooms.remove(roomName);
	}

	public boolean isIn(String name) {
		Boolean result = rooms.get(name);
		if(result == null) result = false;
		return result;
	}

	public Set<String> rooms() {
		return rooms.keySet();
	}

	@Override
	public String toString() {
		JsonObject result = new JsonObject();
		for(Map.Entry<String, Boolean> entry : rooms.entrySet()) {
			result.putBoolean(entry.getKey(), entry.getValue());
		}
		return result.toString();
	}
}
