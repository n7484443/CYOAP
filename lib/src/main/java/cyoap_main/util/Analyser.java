package cyoap_main.util;

import cyoap_main.core.VarData;
import cyoap_main.core.VarData.ValueType;
import cyoap_main.core.VarData.types;

public class Analyser {
	public static void analyse(String str) {
		if(str.contains("{")) {
			String newstr = str;
			while(newstr.contains("{")) {
				var start = newstr.indexOf("{");
				var end = newstr.indexOf("}");
				String innerstr = newstr.substring(start + 1, end);
				innerstr = innerstr.replaceAll(" ", "").replaceAll("\n", "");
				if(innerstr.contains("+=")) {
					var s = innerstr.replace("+", "").split("=");
					System.out.println(s[0]);
					VarData.changeValue(s[0], new ValueType(s[1]));
				}else if(innerstr.contains("-=")) {
					var s = innerstr.replace("-", "").split("=");
					VarData.changeValue(s[0], new ValueType("-" + s[1]));
				}else if(innerstr.contains("=")){
					var s = innerstr.split("=");
					VarData.setValue(s[0], new ValueType(s[1]));
				}else if(innerstr.contains("changeto&f")){
					var s = innerstr.replace("changeto&f", "");
					var v = new ValueType(types.floats);
					v.data = VarData.getValue(s).data;
					VarData.setValue(s, v);
				}
				
				newstr = newstr.substring(end + 1);
			}
		}
	}
}
