package com.nhncorp.mods.socket.io.impl;

import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.shareddata.Shareable;

import java.util.ArrayList;

/**
 * @author Keesun Baik
 */
public class ShareableList<T> extends ArrayList<Buffer> implements Shareable {
}
