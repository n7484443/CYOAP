package cyoap_main.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javafx.scene.text.Font;

public class FontLoader {
	public int[] size;
	public String fontFolder = "/lib/font";

	public FontLoader() {
		System.setProperty("prism.lcdtext", "false");
		size = IntStream.range(1, 20).toArray();

		loadFont("/NanumGothic/NanumGothic");
		loadFont("/NanumBarunGothic/NanumBarunGothic");
	}

	public void loadFont(String str) {
		System.out.println("load font : " + fontFolder + str + ".tff");
		for (var s : size) {
			Font.loadFont(LoadUtil.class.getResourceAsStream(fontFolder + str + ".ttf"), s);
		}
	}

	public void loadAllFont(String str) {
		System.out.println("load font : " + fontFolder + str);
		var stream = LoadUtil.class.getResourceAsStream(fontFolder + str);
		var isreader = new InputStreamReader(stream);
		var bfreader = new BufferedReader(isreader);
		var list = bfreader.lines().map(l -> fontFolder + str + "/" + l + ".tff")
				.map(r -> LoadUtil.class.getResourceAsStream(r)).collect(Collectors.toList());
		for (var l : list) {
			for (var s : size) {
				Font.loadFont(l, s);
			}
		}
	}
}
