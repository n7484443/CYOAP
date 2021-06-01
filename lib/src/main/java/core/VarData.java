package core;

import java.util.ArrayList;
import java.util.List;

public class VarData {
	public static VarData instance;
	public static List<ValueType> var_value = new ArrayList<ValueType>();
	public boolean isUpdated;

	public enum types{
		ints, floats, strings, booleans, nulls
	}
	
	public VarData() {
		VarData.instance = this;
		isUpdated = false;
	}
	public static class ValueType{
		public String name;
		public String data;
		public types type;
		
		public ValueType(types t, String name) {
			this.type = t;
			this.name = name;
			VarData.instance.isUpdated = true;
		}
		
		public <T> types getType(T data) {
			if(data instanceof Integer) return types.ints;
			if(data instanceof Float) return types.floats;
			if(data instanceof String) return types.strings;
			if(data instanceof Boolean) return types.booleans;
			return types.nulls;
		}
		
		public <T> ValueType(T data, String name) {
			this.type = getType(data);
			this.name = name;
			setData(data);
			VarData.instance.isUpdated = true;
		}
		
		public <T> void setData(T b) {
			if(type.equals(types.booleans))data = ((boolean) b ? "True" : "False");
			else data = b.toString();
		}
		
		@SuppressWarnings("unchecked")
		public <T> T getData() {
			if(type.equals(types.booleans))return (T) (data.equals("True") ? Boolean.TRUE : Boolean.FALSE);
			if(type.equals(types.strings))return (T) (data);
			if(type.equals(types.floats))return (T) (Float.valueOf(data));
			if(type.equals(types.ints))return (T) (Integer.valueOf(data));
			return (T) Boolean.FALSE;
		}
	}
}
