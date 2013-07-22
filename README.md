# Socket.io for Vert.x

This module allows the Vert.x users can make a socket.io server as node.js users do.
Now, This module supports the latest version of the socket.io, 0.9.10.

## Name

The module name is `mod-socket-io`.

## Dependency

Add a maven repository contains `mod-socket-io`.

### Gradle

```groovy
repositories {
  maven { url 'https://github.com/keesun/mvn-repo/raw/master' }
  ...
}
```
### Maven

```xml
<repository>
    <id>my.mvn.repo</id>
    <url>https://github.com/keesun/mvn-repo/raw/master</url>
</repository>
```


Add a dependency.

### Gradle (for the Vert.x 2.0.0-CR3)

```groovy
dependencies {
  compile      "com.nhncorp:mod-socket-io:1.0.0"
  ...
}
```

### Gradle (for the Vert.x 1.3.1.final)

```groovy
dependencies {
  compile      "com.nhncorp:mod-socket-io:0.9.0"
  ...
}
```

### Maven (for the Vert.x 2.0.0-CR3)

```maven
<dependency>
    <groupId>com.nhncorp</groupId>
    <artifactId>mod-socket-io</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Maven (for the Vert.x 1.3.1.final)

```maven
<dependency>
    <groupId>com.nhncorp</groupId>
    <artifactId>mod-socket-io</artifactId>
    <version>0.9.0</version>
</dependency>
```

## Configuration

You can configure everything that you can configure in the [Socket.io](https://github.com/LearnBoost/Socket.IO/wiki/Configuring-Socket.IO) like:

```java
	io.configure(new Configurer() {
		public void configure(JsonObject config) {
			config.putString("transports", "websocket,flashsocket,htmlfile,xhr-polling,jsonp-polling");
			config.putBoolean("authorization", true);
		}
	});
```

## Examples

### Verticle

You can use this module in a simple Verticle like:

```java
	import com.nhncorp.mods.socket.io.SocketIOServer;
	import com.nhncorp.mods.socket.io.SocketIOSocket;
	import com.nhncorp.mods.socket.io.impl.DefaultSocketIOServer;
	import org.vertx.java.core.Handler;
	import org.vertx.java.core.http.HttpServer;
	import org.vertx.java.core.json.JsonObject;
	import org.vertx.java.deploy.Verticle;

	/**
	 * @author Keesun Baik
	 */
	public class SampleVerticle extends Verticle {

		@Override
		public void start() throws Exception {
			int port = 9090;
			HttpServer server = vertx.createHttpServer();
			SocketIOServer io = new DefaultSocketIOServer(vertx, server);

			io.sockets().onConnection(new Handler<SocketIOSocket>() {
				public void handle(final SocketIOSocket socket) {
					socket.on("timer", new Handler<JsonObject>() {
						public void handle(JsonObject event) {
							socket.emit("timer", event);
						}
					});
				}
			});

			System.out.println("server is running on http://localshot:" + port);
			server.listen(port);
		}
}
```

The code is located in `samples/verticle/SampleVerticle.java`.

You can run this module by `vertx run` but before run this simple verticle, you should put some jars to the classpath.

Here is some options you can use.
* simply add all files in the `dist` directory to your `VERTX_HOME/libs` directory
* or use `-cp` option when you run the verticle.

Now, you can run the verticle like:

samples/verticle> vertx run SampleVerticle.java

### Module

First, you should include this module's resource by `includes:`.

	{
		"main": "package.to.your.RunnableClassName",
		"includes": "com.nhncorp.socket-io-v0.9.10"
	}

And, after you put the module's jar file to you module's classpath. You can code like:

```java
	public class RunnableClassName extends Verticle {

		@Override
		public void start() {
			HttpServer server = vertx.createHttpServer();
			SocketIOServer io = new DefaultSocketIOServer(vertx, server);

			io.sockets().onConnection(new Handler<SocketIOSocket>() {
				public void handle(final SocketIOSocket socket) {
					JsonObject data = new JsonObject();
					data.putString("hello", "world");
					socket.emit("news", data);

					socket.on("my other event", new Handler<JsonObject>() {
						public void handle(JsonObject data) {
							System.out.println(data);
						}
					});
				}
			});

			server.listen(9090);
		}
	}
```

In the view, you can use the same socket.io javascript like:

```javascript
	<script type="text/javascript" src="/socket.io/socket.io.js"></script>
	<script>
		var socket = io.connect('http://localhost:9090');
		socket.on('news', function (data) {
			console.log(data);
			socket.emit('my other event', { my: 'data' });
		});
	</script>
```
