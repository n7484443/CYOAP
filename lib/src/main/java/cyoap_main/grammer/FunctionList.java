package cyoap_main.grammer;

import java.util.Random;

import cyoap_main.grammer.VarData.ValueType;
import cyoap_main.grammer.VarData.types;

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
			float d1 = Float.valueOf(a.data);
			float d2 = Float.valueOf(b.data);
			if(a.type.equals(types.floats)) {
				a.data = String.valueOf(d1 + d2);
			}else {
				a.data = String.valueOf((int)(d1 + d2));
			}
			return a;
		}else if(a.type.equals(types.floats) && b.type.equals(types.ints)) {
			float d1 = Float.valueOf(a.data);
			float d2 = Float.valueOf(b.data);
			a.data = String.valueOf(d1 + d2);
			return a;
		} else if (b.type.equals(types.floats) && a.type.equals(types.ints)) {
			float d1 = Float.valueOf(a.data);
			float d2 = Float.valueOf(b.data);
			b.data = String.valueOf(d1 + d2);
			System.out.println(b.data);
			return b;
		} else {
			System.err.println("type error!");
			return null;
		}
	};
	final static Func_three func_if = (bool, then, not) -> {
		if (bool == null) System.err.println();
		return (boolean) bool.getData() ? then : not;
	};
	final static Func_two func_minus = (a, b) -> {
		if (b == null) {
			System.err.println("null error!");
			return null;
		}
		if (a.type.equals(b.type) && (a.type.equals(types.ints) || a.type.equals(types.floats))) {
			float d1 = Float.valueOf(a.data);
			float d2 = -Float.valueOf(b.data);
			if (a.type.equals(types.floats)) {
				a.data = String.valueOf(d1 + d2);
			} else {
				a.data = String.valueOf((int)(d1 + d2));
			}
			return a;
		}else if(a.type.equals(types.floats) && b.type.equals(types.ints)) {
			float d1 = Float.valueOf(a.data);
			float d2 = -Float.valueOf(b.data);
			a.data = String.valueOf(d1 + d2);
			return a;
		} else if (b.type.equals(types.floats) && a.type.equals(types.ints)) {
			float d1 = Float.valueOf(a.data);
			float d2 = -Float.valueOf(b.data);
			b.data = String.valueOf(d1 + d2);
			return b;
		} else {
			System.err.println("type error!");
			return null;
		}
	};
	final static Func_one func_floor = (input) -> {
		float f;
		if (input.type.equals(types.ints)) {
			int i = input.getData();
			f = i;
		} else {
			f = input.getData();
		}
		input.setData(String.valueOf((int) Math.floor(f)));
		return input;
	};
	final static Func_two func_multi = (a, b) -> {
		if (b == null) {
			System.err.println("null error!");
			return null;
		}
		if (a.type.equals(b.type) && (a.type.equals(types.ints) || a.type.equals(types.floats))) {
			float d1 = Float.valueOf(a.data);
			float d2 = Float.valueOf(b.data);
			if (a.type.equals(types.floats)) {
				a.data = String.valueOf(d1 * d2);
			} else {
				a.data = String.valueOf((int)(d1 * d2));
			}
			return a;
		}else if(a.type.equals(types.floats) && b.type.equals(types.ints)) {
			float d1 = Float.valueOf(a.data);
			float d2 = Float.valueOf(b.data);
			a.data = String.valueOf(d1 * d2);
			return a;
		} else if (b.type.equals(types.floats) && a.type.equals(types.ints)) {
			float d1 = Float.valueOf(a.data);
			float d2 = Float.valueOf(b.data);
			b.data = String.valueOf(d1 * d2);
			return b;
		} else {
			System.err.println("type error!");
			return null;
		}
	};
	final static Func_one func_round = (input) -> {
		float f;
		if (input.type.equals(types.ints)) {
			int i = input.getData();
			f = i;
		} else {
			f = input.getData();
		}
		input.setData(String.valueOf(Math.round(f)));
		return input;
	};
	final static Func_two func_div = (a, b) -> {
		if (b == null) {
			System.err.println("null error!");
			return null;
		}
		if (a.type.equals(b.type) && (a.type.equals(types.ints) || a.type.equals(types.floats))) {
			float d1 = Float.valueOf(a.data);
			float d2 = Float.valueOf(b.data);
			if (a.type.equals(types.floats)) {
				a.data = String.valueOf(d1 / d2);
			} else {
				a.data = String.valueOf((int)(d1 / d2));
			}
			return a;
		}else if(a.type.equals(types.floats) && b.type.equals(types.ints)) {
			float d1 = Float.valueOf(a.data);
			float d2 = Float.valueOf(b.data);
			a.data = String.valueOf(d1 / d2);
			return a;
		} else if (b.type.equals(types.floats) && a.type.equals(types.ints)) {
			float d1 = Float.valueOf(a.data);
			float d2 = Float.valueOf(b.data);
			b.data = String.valueOf(d1 / d2);
			return b;
		} else {
			System.err.println("type error!");
			return null;
		}
	};
	final static Func_one func_ceil = (input) -> {
		float f;
		if (input.type.equals(types.ints)) {
			int i = input.getData();
			f = i;
		} else {
			f = input.getData();
		}
		input.setData(String.valueOf((int) Math.ceil(f)));
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


	public static Function_for_d getFunction(String s) {
		switch (s) {
			case "if":
				return func_if;
			case "floor":
				return func_floor;
			case "round":
				return func_round;
		case "ceil":
			return func_ceil;
		case "plus":
			return func_plus;
		case "minus":
			return func_minus;
		case "multi":
			return func_multi;
		case "div":
			return func_div;
		case "random":
			return func_rand;
		default:
			return null;
		}
	}
	public interface Function_for_d{}
	@FunctionalInterface
	public interface Func_three extends Function_for_d{
		ValueType func(ValueType b, ValueType x, ValueType nor);
	}
	@FunctionalInterface
	public interface Func_two extends Function_for_d{
		ValueType func(ValueType b, ValueType x);
	}
	
	@FunctionalInterface
	public interface Func_one extends Function_for_d{
		ValueType func(ValueType b);
	}
}
