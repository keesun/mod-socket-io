package com.nhncorp.mods.socket.io.impl;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @author Keesun Baik
 */
public class RoomTest {

	@Test
	public void create() {
		Room room = new Room();
		assertThat(room, is(notNullValue()));
	}

}
