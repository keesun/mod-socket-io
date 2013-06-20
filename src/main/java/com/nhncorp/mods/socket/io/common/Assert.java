package com.nhncorp.mods.socket.io.common;

/**
 * @author Keesun Baik
 */
public class Assert {

	public static void notNull(Object object, String message) {
		if (object == null) {
			throw new IllegalArgumentException(message);
		}
	}

}
