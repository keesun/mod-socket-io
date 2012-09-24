package com.nhncorp.mods.socket.io.impl.handlers;

import com.nhncorp.mods.socket.io.impl.ClientData;
import com.nhncorp.mods.socket.io.impl.Manager;
import org.springframework.util.FileCopyUtils;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.HttpServerResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * @author Keesun Baik
 */
public class StaticHandler {

	private static final List<String> STAIC_FLIE_NAMES = Arrays.asList(new String[]{
			"/static/flashsocket/WebSocketMainInsecure.swf",
			"/static/flashsocket/WebSocketMain.swf",
			"/socket.io.js",
			"/socket.io.min.js"
	});

	private Manager manager;

	public StaticHandler(Manager manager) {
		this.manager = manager;
	}

	public void handle(ClientData clientData) {
		HttpServerRequest req = clientData.getRequest();
		HttpServerResponse res = req.response;
		String requestedFileName = clientData.getPath();
		String resourceRootDir = getRootDir();

		File rootDir = new File(requestedFileName);
		if(!rootDir.exists()) {
			copyFilesToDir();
		}

		switch (requestedFileName) {
			case "/static/flashsocket/WebSocketMainInsecure.swf":
				res.sendFile(resourceRootDir + "/WebSocketMainInsecure.swf");
				break;
			case "/static/flashsocket/WebSocketMain.swf":
				res.sendFile(resourceRootDir + "/WebSocketMain.swf");
				break;
			case "/socket.io.js":
				res.sendFile(resourceRootDir + "/socket.io.js");
				break;
			case "/socket.io.min.js":
				res.sendFile(resourceRootDir + "/socket.io.min.js");
				break;
			default:
				throw new IllegalArgumentException(requestedFileName);
		}
		res.close();
	}

	private void copyFilesToDir() {
		String[] fileNames = new String[]{"socket.io.js", "socket.io.min.js", "WebSocketMain.swf", "WebSocketMainInsecure.swf"};
		String rootDir = getRootDir() + "/";

		for(String fileName : fileNames) {
			ClassLoader loader = this.getClass().getClassLoader();
			InputStream is = loader.getResourceAsStream(fileName);
			String path = rootDir + fileName;
			File parent = new File(path).getParentFile();
			if(parent != null && !parent.exists()) {
				parent.mkdirs();
			}
			try {
				FileCopyUtils.copy(is, new FileOutputStream(path));
			} catch (IOException e) {
				throw new IllegalStateException("static file copy failed", e);
			}
		}
	}

	public static boolean has(String path) {
		return STAIC_FLIE_NAMES.contains(path);
	}

	public static String getRootDir(){
		return System.getProperty("user.dir") + "/mods/nhn.socket-io-v0.9.10/static";
	}

}
