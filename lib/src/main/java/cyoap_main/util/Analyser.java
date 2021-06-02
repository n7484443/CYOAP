package cyoap_main.util;

import cyoap_main.core.VarData;
import cyoap_main.core.VarData.ValueType;
import cyoap_main.core.VarData.types;

public class Analyser {
	public static void analyse(String str) {
		if (str.contains("{")) {
			String newstr = str.replaceAll(" ", "").replaceAll("\n", "");

			while (newstr.contains("{")) {
				var start = newstr.indexOf("{");
				var end = newstr.indexOf("}");
				String innerstr = newstr.substring(start + 1, end);

				if (innerstr.contains("+=")) {
					var s = innerstr.replace("+", "").split("=");
					VarData.changeValue(s[0], new ValueType(s[1]));
				} else if (innerstr.contains("-=")) {
					var s = innerstr.replace("-", "").split("=");
					VarData.changeValue(s[0], new ValueType("-" + s[1]));
				} else if (innerstr.contains("=")) {
					var s = innerstr.split("=");
					VarData.setValue(s[0], new ValueType(s[1]));
				} else if (innerstr.contains("changeto&f")) {
					var s = innerstr.replace("changeto&f", "");
					var v = new ValueType(types.floats);
					v.data = VarData.getValue(s).data;
					VarData.setValue(s, v);
				} else if (innerstr.contains("changeto&i")) {
					var s = innerstr.replace("changeto&i", "");
					var v = new ValueType(types.ints);
					var news = s.replace("floor", "").replace("ceil", "").replace("round", "");
					float f = Float.valueOf(VarData.getValue(news).data);
					if (innerstr.contains("floor"))
						v.data = String.valueOf((int) Math.floor(f));
					else if (innerstr.contains("ceil"))
						v.data = String.valueOf((int) Math.ceil(f));
					else if (innerstr.contains("round"))
						v.data = String.valueOf((int) Math.round(f));
					else
						v.data = String.valueOf((int) Math.round(f));
					VarData.setValue(news, v);
				}

				newstr = newstr.substring(end + 1);
			}
		}
	}
}
