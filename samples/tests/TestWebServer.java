import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.impl.DefaultVertx;
import org.vertx.java.platform.Verticle;

/**
 * @author Keesun Baik
 */
public class TestWebServer extends Verticle {

	private static final String WEB_ROOT = "samples/web";

	public TestWebServer() {
	}

	public TestWebServer(Vertx vertx) {
		this.vertx = vertx;
	}

	@Override
	public void start() {
		HttpServer server = vertx.createHttpServer();
		server.requestHandler(new Handler<HttpServerRequest>() {
			@Override
			public void handle(HttpServerRequest request) {
				String filePath = WEB_ROOT;
				String requestPath = request.path();
				if(requestPath.equals("/")) {
					filePath += "/index.html";
				} else {
					filePath += requestPath;
				}

				request.response().sendFile(filePath);
			}
		});

		server.listen(8080);
	}

	public static void main(String[] args) throws Exception {
		Vertx vertx = new DefaultVertx();
		TestWebServer webServer = new TestWebServer(vertx);
		webServer.start();
		Thread.sleep(Long.MAX_VALUE);
	}


}
