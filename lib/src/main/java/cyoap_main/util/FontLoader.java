package cyoap_main.util;

import javafx.scene.text.Font;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.IntStream;
import java.util.zip.ZipEntry;

public class FontLoader {
	public static int[] size;
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
		boolean isLoaded = true;
		Font font = null;
		for (var s : size) {
			font = Font.loadFont(LoadUtil.class.getResourceAsStream(str), s);
			if (font == null) {
				isLoaded = false;
			}
		}
		if (isLoaded && font != null) {
			System.out.println("loaded with " + font.getName());
		} else {
			System.out.println("error with " + str);
		}
	}

	public void loadAllFont(String str) throws URISyntaxException, IOException {
		final File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());

		if (jarFile.isFile()) { // Run with IDE
			JarFile jar = new JarFile(jarFile);
			Enumeration<JarEntry> entries = jar.entries(); // gives ALL entries in jar

			Collections.list(entries)
					.parallelStream()
					.map(ZipEntry::getName)
					.filter(t -> t.startsWith(str + "/") && (t.endsWith(".ttf") || t.endsWith(".otf")))
					.forEach(t -> loadFont("/" + t));

			jar.close();
		} else { // Run with excuter
			LoadUtil.isIDE = false;
			Path p = Paths.get(URI.create("jrt:/")).resolve("/modules/cyoap_module/lib/font");
			var list_font = new ArrayList<String>();
			Files.list(p).parallel().forEach(m -> {
				var p2 = p.resolve(m);
				try {
					Files.list(p2).forEach(m2 -> list_font.add(m2.toString()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			list_font.parallelStream()
					.map(t -> t.replace("/modules/cyoap_module", ""))
					.filter(t -> t.endsWith(".ttf") || t.endsWith(".otf"))
					.forEach(this::loadFont);
		}
	}
}
