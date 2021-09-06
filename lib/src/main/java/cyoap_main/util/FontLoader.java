package cyoap_main.util;

import java.util.stream.IntStream;

import javafx.scene.text.Font;

public class FontLoader {
	public int[] size;
	public String fontFolder = "/lib/font";
	public FontLoader(){
		System.setProperty("prism.lcdtext", "false");
		size = IntStream.range(1, 20).toArray();
		

		loadFont("/NanumGothic/NanumGothic");
	}
	
	public void loadFont(String str) {
		System.out.println("load font : " + fontFolder + str + ".tff");
		for(var s : size) {
			Font.loadFont(LoadUtil.class.getResourceAsStream(fontFolder + str + ".tff"), s);
		}
	}
}
