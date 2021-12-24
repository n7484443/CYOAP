package cyoap_main.grammer;

import java.util.HashMap;
import java.util.Map;

import cyoap_main.grammer.FunctionList.Function_for_d;

public class VarData {
	static final VarData instance = new VarData();
	public static Map<String, ValueType> var_map = new HashMap<>();
	public boolean isUpdated = false;

	public static VarData getInstance() {
		return instance;
	}

	public void setValue(String name, ValueType value) {
		var_map.put(name, value);
		isUpdated = true;
	}


	public boolean hasValue(String name) {
		return var_map.containsKey(name);
	}

	public void changeValue(String name, ValueType value) {
		var v = var_map.get(name);
		if (v == null) {
			setValue(name, value);
			return;
		}
		if (v.type.equals(types.ints) && value.type.equals(types.ints)) {
			v.data = String.valueOf(Integer.parseInt(v.data) + Integer.parseInt(value.data));
		} else if (v.type.equals(types.floats) && value.type.equals(types.floats)) {
			v.data = String.valueOf(Float.parseFloat(v.data) + Float.parseFloat(value.data));
		} else if (v.type.equals(types.floats) && value.type.equals(types.ints)) {
			v.data = String.valueOf(Float.parseFloat(v.data) + Float.parseFloat(value.data));
		}else{
			var_map.get(name).data += value.data;
		}
		
		isUpdated = true;
	}

	public ValueType getValue(String name) {
		return var_map.get(name);
	}
	
	public enum types {
		ints("int"), floats("float"), strings("String"), booleans("boolean"), nulls("null"), functions("function");

		String toStr;

		types(String string) {
			toStr = string;
		}

		public String toString() {
			return toStr;
		}
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
			if (data instanceof Function_for_d) {
				return types.functions;
			}
			return types.nulls;
		}

		public ValueType(types t) {
			this.type = t;
		}
		
		public ValueType(types t, String data) {
			this.type = t;
			this.data = data;
		}
		
		public <T> ValueType(T data) {
			this.type = getType(data);
			setData(data);
		}
		
		public ValueType(ValueType v) {
			this.type = v.type;
			this.data = v.data;
		}
		
		public ValueType() {
			this.type = types.nulls;
		}

		public <T> void setData(T b) {
			if (type.equals(types.booleans))
				data = ((boolean) b ? "true" : "false");
			else
				data = b.toString();
		}

		@SuppressWarnings("unchecked")
		public <T> T getData() {
			if (type.equals(types.booleans))
				return (T) (data.equalsIgnoreCase("true") ? Boolean.TRUE : Boolean.FALSE);
			if (type.equals(types.strings))
				return (T) (data);
			if (type.equals(types.floats))
				return (T) (Float.valueOf(data));
			if (type.equals(types.ints))
				return (T) (Integer.valueOf(data));
			if (type.equals(types.functions))
				return (T) (FunctionList.getFunction(data));
			return (T) Boolean.FALSE;
		}

		public void add(ValueType a) {
			if(a == null) {
				System.err.println("null error!");
				return;
			}
			if(this.type.equals(types.strings)) {
				data += a.data;
			}else if(this.type.equals(a.type) && !this.type.equals(types.booleans)) {
				float d1 = Float.parseFloat(this.data);
				float d2 = Float.parseFloat(a.data);
				if(this.type.equals(types.floats)) {
					this.data = String.valueOf(d1 + d2);
				}else {
					this.data = String.valueOf((int)(d1 + d2));
				}
			}else if(this.type.equals(types.floats) && a.type.equals(types.ints)) {
				float d1 = Float.parseFloat(this.data);
				float d2 = Float.parseFloat(a.data);
				this.data = String.valueOf(d1 + d2);
			}else{
				System.err.println("type error!");
			}
		}
		
		public void sub(ValueType a) {
			float d1 = Float.parseFloat(this.data);
			if(a == null) {
				System.err.println("null error!");
				return;
			}
			if(this.type.equals(a.type) && !this.type.equals(types.booleans) && !this.type.equals(types.strings)) {
				float d2 = Float.parseFloat(a.data);
				if(this.type.equals(types.floats)) {
					this.data = String.valueOf(d1 + d2);
				}else {
					this.data = String.valueOf((int)(d1 + d2));
				}
			}else if(this.type.equals(types.floats) && a.type.equals(types.ints)) {
				float d2 = Float.parseFloat(a.data);
				this.data = String.valueOf(d1 + d2);
			}else{
				System.err.println("type error!");
			}
		}
		
		public void mul(ValueType a) {
			if(a == null) {
				System.err.println("null error!");
				return;
			}
			if(this.type.equals(a.type) && !this.type.equals(types.booleans) && !this.type.equals(types.strings)) {
				float d1 = Float.parseFloat(this.data);
				float d2 = Float.parseFloat(a.data);
				if(this.type.equals(types.floats)) {
					this.data = String.valueOf(d1 * d2);
				}else {
					this.data = String.valueOf((int)(d1 * d2));
				}
			}else if(this.type.equals(types.floats) && a.type.equals(types.ints)) {
				float d1 = Float.parseFloat(this.data);
				float d2 = Float.parseFloat(a.data);
				this.data = String.valueOf(d1 * d2);
			}else{
				System.err.println("type error!");
			}
		}
		
		public void div(ValueType a) {
			if(a == null) {
				System.err.println("null error!");
				return;
			}
			if(this.type.equals(a.type) && !this.type.equals(types.booleans) && !this.type.equals(types.strings)) {
				if(this.type.equals(types.floats)) {
					float d1 = Float.parseFloat(this.data);
					float d2 = Float.parseFloat(a.data);
					this.data = String.valueOf(d1 / d2);
				}else {
					int d1 = Integer.parseInt(this.data);
					int d2 = Integer.parseInt(a.data);
					this.data = String.valueOf(d1 / d2);
				}
			}else if(this.type.equals(types.floats) && a.type.equals(types.ints)) {
				float d1 = Float.parseFloat(this.data);
				float d2 = Float.parseFloat(a.data);
				this.data = String.valueOf(d1 / d2);
			}else{
				System.err.println("type error!");
			}
		}
		
		public void set(ValueType a) {
			if(this.type.equals(types.nulls)) {
				this.type = a.type;
				this.data = a.data;
			}else if(this.type.equals(a.type)) {
				this.data = a.data;
			}else if(this.type.equals(types.floats) && a.type.equals(types.ints)) {
				this.data = a.data;
			}
		}
	}
}
