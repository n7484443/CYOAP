package cyoap_main.util;

import javafx.scene.paint.Color;

public class ColorUtil {
	public static Color getColorFromHex(int hex) {
		return getColorFromString("#" + Integer.toHexString(hex));
	}
	
	public static Color getColorFromString(String hex) {
		return Color.web(hex);
	}
	
	public static String getStringFromColor(Color color) {
		return color.toString().substring(0, 8);
	}
	public static int getHexFromColor(Color color) {
		return color.hashCode();
	}
}
