package cyoap_main.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.IntStream;

import javafx.scene.text.Font;

public class FontLoader {
	public int[] size;
	public String fontFolder = "lib/font";

	public FontLoader() {
		System.setProperty("prism.lcdtext", "false");
		
		size = IntStream.range(1, 25).toArray();

		try {
			loadAllFont(fontFolder);
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		}
	}

	public void loadFont(String str) {
		for (var s : size) {
			var font = Font.loadFont(LoadUtil.class.getResourceAsStream("/" + str), s);
			if (font == null) {
				System.out.println("error with " + str);
			}
		}
	}

	public void loadFonts(String str) {
		for (var s : size) {
			var font = Font.loadFont(LoadUtil.class.getResourceAsStream(str), s);
			if (font == null) {
				System.out.println("error with " + str);
			}
		}
	}

	public void loadAllFont(String str) throws URISyntaxException, IOException {
		final File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());

		if (jarFile.isFile()) { // Run with IDE
			final JarFile jar = new JarFile(jarFile);
			final Enumeration<JarEntry> entries = jar.entries(); // gives ALL entries in jar
			while (entries.hasMoreElements()) {
				final String name = entries.nextElement().getName();
				if (name.startsWith(str + "/")) { // filter according to the path
					if (name.endsWith(".ttf")) { // filter according to the ttf file
						loadFont(name);
					}
				}
			}
			jar.close();
		} else { // Run with excuter
			LoadUtil.isIDE = false;
			Path p = Paths.get(URI.create("jrt:/")).resolve("/modules/cyoap_module/lib/font");
			Files.list(p).forEach(m -> {
				var p2 = p.resolve(m);
				try {
					Files.list(p2).forEach(m2 -> {
						loadFonts(m2.toString().replace("/modules/cyoap_module", ""));
					});
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}
	}
}
