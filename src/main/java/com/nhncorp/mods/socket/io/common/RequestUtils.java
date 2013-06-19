package com.nhncorp.mods.socket.io.common;

import io.netty.channel.Channel;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.impl.AbstractConnection;
import org.vertx.java.core.http.impl.DefaultHttpServerRequest;
import org.vertx.java.core.net.impl.ConnectionBase;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author Keesun Baik
 * @
 */
public class RequestUtils {

	public static String getRemoteAddress(HttpServerRequest req) {
		String host = "unknown";

		DefaultHttpServerRequest request = (DefaultHttpServerRequest) req;
		AbstractConnection conn = (AbstractConnection)getPrivateMember(request, "conn", DefaultHttpServerRequest.class);
		Channel channel = (Channel)getPrivateMember(conn, "channel", ConnectionBase.class);

		if(channel != null) {
			SocketAddress remoteAddress = channel.remoteAddress();
			InetSocketAddress address = (InetSocketAddress) remoteAddress;
			host = address.getHostString();
		}

		return host;
	}

	private static Object getPrivateMember(Object o, String memberName, Class<?> oClass) {
		if (o == null) {
			System.out.println("failed to get the private member. the object is null");
			System.out.println("member: " + memberName);
			System.out.println("class: " + oClass.getName());
			return null;
		}

		Field fields [] = null;
		Object val = null;
		try {
			fields = oClass.getDeclaredFields();
			for (int i = 0; i < fields.length; i++){
				if (fields[i].getName().equals(memberName)) {
					fields[i].setAccessible(true);
					val = fields[i].get(o);
					break;
				}
			}
		} catch (SecurityException e) {
			System.out.println("failed to get the private member due to security violation.");
			System.out.println("member: " + memberName);
			System.out.println("class: " + oClass.getName());
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			System.out.println("failed to get the private member due to illegal argument.");
			System.out.println("member: " + memberName);
			System.out.println("class: " + oClass.getName());
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			System.out.println("failed to get the private member due to illegal access.");
			System.out.println("member: " + memberName);
			System.out.println("class: " + oClass.getName());
			e.printStackTrace();
		}
		return val;
	}
}
