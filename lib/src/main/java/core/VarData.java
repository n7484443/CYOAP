package core;

import java.util.HashMap;
import java.util.Map;

public class VarData {
	public static VarData instance;
	public static Map<String, ValueType> var_map = new HashMap<String, ValueType>();
	public static boolean isUpdated;
	
	public static void setValue(String name, ValueType value) {
		var_map.put(name, value);
		isUpdated = true;
	}
	
	public static void changeValue(String name, ValueType value) {
		var v = var_map.get(name);
		if(v == null) {
			setValue(name, value);
			return;
		}
		if(v.type.equals(types.ints) && value.type.equals(types.ints)) {
			v.data = String.valueOf(Integer.valueOf(v.data) + Integer.valueOf(value.data));
		}else if(v.type.equals(types.floats) && value.type.equals(types.floats)){
			v.data = String.valueOf(Float.valueOf(v.data) + Float.valueOf(value.data));
		}else if(v.type.equals(types.floats) && value.type.equals(types.ints)){
			v.data = String.valueOf(Float.valueOf(v.data) + Float.valueOf(value.data));
		}else{
			var_map.get(name).data += value.data;
		}
		
		isUpdated = true;
	}
	
	public static ValueType getValue(String name) {
		return var_map.get(name);
	}
	
	public enum types {
		ints("int"), floats("float"), strings("String"), booleans("boolean"), nulls("null");

		String toStr;

		types(String string) {
			toStr = string;
		}

		public String toString() {
			return toStr;
		}
	}

	public VarData() {
		VarData.instance = this;
		isUpdated = false;
	}

	public static class ValueType {
		public String data;
		public types type;

		public <T> types getType(T data) {
			if (data instanceof Integer)
				return types.ints;
			if (data instanceof Float)
				return types.floats;
			if (data instanceof String)
				return types.strings;
			if (data instanceof Boolean)
				return types.booleans;
			return types.nulls;
		}

		public <T> types getTypeFromData(String input) {
			if (input.contains("&i"))
				return types.ints;
			if (input.contains("&f"))
				return types.floats;
			if (input.contains("&s"))
				return types.strings;
			if (input.contains("&b"))
				return types.booleans;
			return types.nulls;
		}

		public ValueType(types t) {
			this.type = t;
		}
		
		public <T> ValueType(T data) {
			this.type = getType(data);
			setData(data);
		}
		
		public <T> ValueType(String data) {
			if(data.isEmpty()) {
				System.err.println("!!! empty name or empty data");
				return;
			}
			setDataFromData(data);
		}

		public <T> void setData(T b) {
			if (type.equals(types.booleans))
				data = ((boolean) b ? "true" : "false");
			else
				data = b.toString();
		}

		public <T> void setDataFromData(String s) {
			if (s.contains("&i")) {
				this.type = types.ints;
				this.data = s.replace("&i", "");
			}else if (s.contains("&f")) {
				this.type = types.floats;
				this.data = s.replace("&f", "");
			}else if (s.contains("&s")) {
				this.type = types.strings;
				this.data = s.replace("&s", "");
			}else if (s.contains("&b")) {
				this.type = types.booleans;
				this.data = s.replace("&b", "");
			}else {
				this.type = types.nulls;
				this.data = s;
			}
		}

		@SuppressWarnings("unchecked")
		public <T> T getData() {
			if (type.equals(types.booleans))
				return (T) (data.toLowerCase().equals("true") ? Boolean.TRUE : Boolean.FALSE);
			if (type.equals(types.strings))
				return (T) (data);
			if (type.equals(types.floats))
				return (T) (Float.valueOf(data));
			if (type.equals(types.ints))
				return (T) (Integer.valueOf(data));
			return (T) Boolean.FALSE;
		}
	}
}
