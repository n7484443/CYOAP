package cyoap_main.grammer;

import java.util.HashMap;
import java.util.Map;

public class VariableDataBase {
	static final VariableDataBase instance = new VariableDataBase();
	public Map<String, ValueType> var_map = new HashMap<>();
	public boolean isUpdated = false;

	public static VariableDataBase getInstance() {
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
		ints("int"), floats("float"), strings("String"), booleans("boolean"), nulls("null"), functions("function"), variable("variable");

		String toStr;

		types(String string) {
			toStr = string;
		}

		public String toString() {
			return toStr;
		}
	}
}
