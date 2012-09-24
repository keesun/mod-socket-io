package sandbox;

import org.junit.Test;

import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * @author Keesun Baik
 */
public class FileTests {

	@Test
	public void path() {
		Path path = FileSystems.getDefault().getPath("./src/main/resources/socket.io.js");
		System.out.println(path.toFile().isFile());
		System.out.println(path.toString());
	}
}
