package cyoap_main.grammer;

import cyoap_main.grammer.VariableDataBase.ValueType;
import cyoap_main.grammer.VariableDataBase.types;

import java.util.Random;

public class FunctionList {
	final static Func_two func_plus = (a, b) -> {
		if (b == null) {
			System.err.println("null error!");
			return null;
		}
		System.out.println("plusTest:" + a.data + ":" + b.data);
		if (a.type.equals(types.strings)) {
			a.data += b.data;
			return a;
		} else if (a.type.equals(b.type) && (a.type.equals(types.ints) || a.type.equals(types.floats))) {
			float d1 = Float.parseFloat(a.data);
			float d2 = Float.parseFloat(b.data);
			if (a.type.equals(types.floats)) {
				a.data = String.valueOf(d1 + d2);
			} else {
				a.data = String.valueOf((int) (d1 + d2));
			}
			return a;
		}else if(a.type.equals(types.floats) && b.type.equals(types.ints)) {
			float d1 = Float.parseFloat(a.data);
			float d2 = Float.parseFloat(b.data);
			a.data = String.valueOf(d1 + d2);
			return a;
		} else if (b.type.equals(types.floats) && a.type.equals(types.ints)) {
			float d1 = Float.parseFloat(a.data);
			float d2 = Float.parseFloat(b.data);
			b.data = String.valueOf(d1 + d2);
			System.out.println(b.data);
			return b;
		} else {
			System.err.println("type error!");
			return null;
		}
	};
	final static Func_two func_minus = (a, b) -> {
		if (b == null) {
			System.err.println("null error!");
			return null;
		}
		if (a.type.equals(b.type) && (a.type.equals(types.ints) || a.type.equals(types.floats))) {
			float d1 = Float.parseFloat(a.data);
			float d2 = -Float.parseFloat(b.data);
			if (a.type.equals(types.floats)) {
				a.data = String.valueOf(d1 + d2);
			} else {
				a.data = String.valueOf((int) (d1 + d2));
			}
			return a;
		}else if(a.type.equals(types.floats) && b.type.equals(types.ints)) {
			float d1 = Float.parseFloat(a.data);
			float d2 = -Float.parseFloat(b.data);
			a.data = String.valueOf(d1 + d2);
			return a;
		} else if (b.type.equals(types.floats) && a.type.equals(types.ints)) {
			float d1 = Float.parseFloat(a.data);
			float d2 = -Float.parseFloat(b.data);
			b.data = String.valueOf(d1 + d2);
			return b;
		} else {
			System.err.println("type error!");
			return null;
		}
	};
	final static Func_two func_multi = (a, b) -> {
		if (b == null) {
			System.err.println("null error!");
			return null;
		}
		if (a.type.equals(b.type) && (a.type.equals(types.ints) || a.type.equals(types.floats))) {
			float d1 = Float.parseFloat(a.data);
			float d2 = Float.parseFloat(b.data);
			if (a.type.equals(types.floats)) {
				a.data = String.valueOf(d1 * d2);
			} else {
				a.data = String.valueOf((int) (d1 * d2));
			}
			return a;
		}else if(a.type.equals(types.floats) && b.type.equals(types.ints)) {
			float d1 = Float.parseFloat(a.data);
			float d2 = Float.parseFloat(b.data);
			a.data = String.valueOf(d1 * d2);
			return a;
		} else if (b.type.equals(types.floats) && a.type.equals(types.ints)) {
			float d1 = Float.parseFloat(a.data);
			float d2 = Float.parseFloat(b.data);
			b.data = String.valueOf(d1 * d2);
			return b;
		} else {
			System.err.println("type error!");
			return null;
		}
	};
	final static Func_two func_div = (a, b) -> {
		if (b == null) {
			System.err.println("null error!");
			return null;
		}
		if (a.type.equals(b.type) && (a.type.equals(types.ints) || a.type.equals(types.floats))) {
			float d1 = Float.parseFloat(a.data);
			float d2 = Float.parseFloat(b.data);
			if (a.type.equals(types.floats)) {
				a.data = String.valueOf(d1 / d2);
			} else {
				a.data = String.valueOf((int) (d1 / d2));
			}
			return a;
		}else if(a.type.equals(types.floats) && b.type.equals(types.ints)) {
			float d1 = Float.parseFloat(a.data);
			float d2 = Float.parseFloat(b.data);
			a.data = String.valueOf(d1 / d2);
			return a;
		} else if (b.type.equals(types.floats) && a.type.equals(types.ints)) {
			float d1 = Float.parseFloat(a.data);
			float d2 = Float.parseFloat(b.data);
			b.data = String.valueOf(d1 / d2);
			return b;
		} else {
			System.err.println("type error!");
			return null;
		}
	};
	final static Func_three func_if = (bool, then, not) -> {
		if (bool == null) return null;
		if (bool.getData() == null) return null;
		return (boolean) bool.getData() ? then : not;
	};

	public static Function_for_d getFunction(String s) {
		return switch (s) {
			case "if" -> func_if;
			case "floor" -> func_floor;
			case "round" -> func_round;
			case "ceil" -> func_ceil;
			case "plus" -> func_plus;
			case "minus" -> func_minus;
			case "multi" -> func_multi;
			case "div" -> func_div;
			case "random" -> func_rand;
			default -> null;
		};
	}

	public interface Function_for_d {
	}

	@FunctionalInterface
	public interface Func_three extends Function_for_d {
		ValueType func(ValueType b, ValueType x, ValueType nor);
	}

	@FunctionalInterface
	public interface Func_two extends Function_for_d {
		ValueType func(ValueType b, ValueType x);
	}

	@FunctionalInterface
	public interface Func_one extends Function_for_d {
		ValueType func(ValueType b);
	}


	final static Func_one func_floor = (input) -> {
		if (input.type == types.ints) {
			int f = input.getData();
			int i = (int) Math.floor(f);
			input.setData(String.valueOf(i));
			input.type = types.ints;
		} else if (input.type == types.floats) {
			float f = input.getData();
			int i = (int) Math.floor(f);
			input.setData(String.valueOf(i));
			input.type = types.ints;
		}
		return input;
	};


	final static Func_one func_round = (input) -> {
		if (input.type == types.ints || input.type == types.floats) {
			int f = Math.round(input.getData());
			input.setData(String.valueOf(f));
			input.type = types.ints;
		}
		return input;
	};

	final static Func_one func_ceil = (input) -> {
		if (input.type == types.ints || input.type == types.floats) {
			float f = input.getData();
			int i = (int) Math.ceil(f);
			input.setData(String.valueOf(i));
			input.type = types.ints;
		}
		return input;
	};

	final static Func_one func_rand = (input) -> {
		float f;
		if (input.type.equals(types.ints)) {
			int i = input.getData();
			f = (new Random()).nextInt(i);
		} else {
			f = (new Random()).nextFloat();
		}
		input.setData(f);
		return input;
	};


}
