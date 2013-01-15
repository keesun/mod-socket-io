package com.nhncorp.mods.socket.io.impl;

import org.junit.Test;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @author Keesun Baik
 */
public class ParserTest {

	@Test
	public void newParser(){
		Parser parser = new Parser();

		Map<String,Integer> packets = parser.packets;
		String[] packetslist = parser.packetslist;
		assertMapAndListInit(packets, packetslist, 9);
		assertMapAndList(packets, packetslist, "disconnect", 0);
		assertMapAndList(packets, packetslist, "connect", 1);
		assertMapAndList(packets, packetslist, "heartbeat", 2);
		assertMapAndList(packets, packetslist, "message", 3);
		assertMapAndList(packets, packetslist, "json", 4);
		assertMapAndList(packets, packetslist, "event", 5);
		assertMapAndList(packets, packetslist, "ack", 6);
		assertMapAndList(packets, packetslist, "error", 7);
		assertMapAndList(packets, packetslist, "noop", 8);

		Map<String, Integer> reasons = parser.reasons;
		String[] reasonsList = parser.reasonsList;
		assertMapAndListInit(reasons, reasonsList, 3);
		assertMapAndList(reasons, reasonsList, "transport not supported", 0);
		assertMapAndList(reasons, reasonsList, "client not handshaken", 1);
		assertMapAndList(reasons, reasonsList, "unauthorized", 2);

		Map<String, Integer> advices = parser.advices;
		String[] advicesList = parser.adviceList;
		assertMapAndListInit(advices, advicesList, 1);
		assertMapAndList(advices, advicesList, "reconnect", 0);
	}

	private void assertMapAndListInit(Map<String, Integer> map, String[] list, int size) {
		assertThat(map, is(notNullValue()));
		assertThat(map.size(), is(size));
		assertThat(list, is(notNullValue()));
		assertThat(list.length, is(size));
	}

	private <K> void assertMapAndList(Map<K, Integer> map, K[] list, K key, Integer value) {
		assertThat(map.containsKey(key), is(true));
		assertThat(map.get(key), is(value));
		assertThat(list[value], is(key));
	}

	@Test
	public void encodePacketMessage() {
		Parser parser = new Parser();
		JsonObject packet = new JsonObject();
		packet.putString("type", "message");
		packet.putString("data", "hello world");

		assertThat(parser.encodePacket(packet), is("3:::hello world"));
	}

	@Test
	public void encodePacketEvent() {
		Parser parser = new Parser();
		JsonObject packet = new JsonObject();
		packet.putString("type", "event");
		packet.putString("name", "hello");
		JsonArray args = new JsonArray();
		args.addString("whiteship");
		args.addObject(new JsonObject().putString("first name", "keesun"));
		packet.putArray("args", args);

		String encodedString = parser.encodePacket(packet);
		assertThat(encodedString.contains("hello"), is(true));
		assertThat(encodedString.contains("whiteship"), is(true));
	}

}
