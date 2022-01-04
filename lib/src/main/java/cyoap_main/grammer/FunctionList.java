package cyoap_main.grammer;

import com.google.common.collect.Range;
import cyoap_main.grammer.VariableDataBase.types;

import java.util.Random;

public class FunctionList {
	public static iFunction getFunction(String s) {
		return switch (s) {
			case "if" -> func_if;
			case "floor" -> func_floor;
			case "round" -> func_round;
			case "ceil" -> func_ceil;
			case "+" -> func_plus;
			case "-" -> func_minus;
			case "*" -> func_multi;
			case "/" -> func_div;
			case "==" -> func_isEqual;
			case "!=" -> func_isNotEqual;
			case ">" -> func_bigger;
			case "<" -> func_smaller;
			case ">=" -> func_bigger_equal;
			case "<=" -> func_smaller_equal;
			case "random" -> func_rand;
			default -> null;
		};
	}

	public interface iFunction {
	}

	@FunctionalInterface
	public interface Func_one_input extends iFunction {
		ValueType func(ValueType a);
	}

	@FunctionalInterface
	public interface Func_two_input extends iFunction {
		ValueType func(ValueType a, ValueType b);
	}

	final static Func_two_input func_plus = (a, b) -> {
		if (b == null) {
			System.err.println("null error!");
			return null;
		}
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
	final static Func_two_input func_minus = (a, b) -> {
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
	final static Func_two_input func_multi = (a, b) -> {
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
	final static Func_two_input func_div = (a, b) -> {
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

	final static float epsilon = 0.0001f;

	final static Func_three_input func_if = (bool, then, not) -> {
		if (bool == null) return null;
		if (bool.getData() == null) return null;
		return (boolean) bool.getData() ? then : not;
	};

	@FunctionalInterface
	public interface Func_three_input extends iFunction {
		ValueType func(ValueType a, ValueType b, ValueType c);
	}

	final static Func_two_input func_isEqual = (a, b) -> {
		if (a == b) return new ValueType(true);
		if (a.type == types.ints && b.type == types.ints && a.getData() == b.getData()) return new ValueType(true);
		if (a.type == types.floats && b.type == types.floats) {
			float a_data = (float) a.getData();
			float b_data = (float) b.getData();
			return new ValueType(Range.closed(-epsilon, epsilon).contains(a_data - b_data));
		}
		if (((a.type == types.ints && b.type == types.floats) || (a.type == types.floats && b.type == types.ints))
				&& a.getData() == b.getData()) return new ValueType(true);
		return new ValueType(false);
	};

	final static Func_two_input func_isNotEqual = (a, b) -> {
		boolean bool_isEqaul = func_isEqual.func(a, b).getData();
		return new ValueType(!bool_isEqaul);
	};
	final static Func_two_input func_bigger_equal = (a, b) -> {
		if (a.type == types.ints && b.type == types.ints) {
			int alpha = (int) a.getData();
			int beta = (int) b.getData();
			return new ValueType(alpha >= beta);
		} else if (a.type == types.floats && b.type == types.ints) {
			float alpha = (float) a.getData();
			int beta = (int) b.getData();
			return new ValueType(alpha >= beta);
		} else if (a.type == types.ints && b.type == types.floats) {
			int alpha = (int) a.getData();
			float beta = (float) b.getData();
			return new ValueType(alpha >= beta);
		} else if (a.type == types.floats && b.type == types.floats) {
			float alpha = (float) a.getData();
			float beta = (float) b.getData();
			return new ValueType(alpha >= beta);
		} else {
			return new ValueType(false);
		}
	};

	final static Func_two_input func_smaller_equal = (a, b) -> {
		if (a.type == types.ints && b.type == types.ints) {
			int alpha = (int) a.getData();
			int beta = (int) b.getData();
			return new ValueType(alpha <= beta);
		} else if (a.type == types.floats && b.type == types.ints) {
			float alpha = (float) a.getData();
			int beta = (int) b.getData();
			return new ValueType(alpha <= beta);
		} else if (a.type == types.ints && b.type == types.floats) {
			int alpha = (int) a.getData();
			float beta = (float) b.getData();
			return new ValueType(alpha <= beta);
		} else if (a.type == types.floats && b.type == types.floats) {
			float alpha = (float) a.getData();
			float beta = (float) b.getData();
			return new ValueType(alpha <= beta);
		} else {
			return new ValueType(false);
		}
	};
	static Func_two_input func_bigger = (a, b) -> {
		boolean bool_isEqaul = func_smaller_equal.func(a, b).getData();
		return new ValueType(!bool_isEqaul);
	};

	final static Func_two_input func_smaller = (a, b) -> {
		boolean bool_isEqaul = func_bigger_equal.func(a, b).getData();
		return new ValueType(!bool_isEqaul);
	};


	final static Func_one_input func_floor = (input) -> {
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


	final static Func_one_input func_round = (input) -> {
		if (input.type == types.ints || input.type == types.floats) {
			int f = Math.round(input.getData());
			input.setData(String.valueOf(f));
			input.type = types.ints;
		}
		return input;
	};

	final static Func_one_input func_ceil = (input) -> {
		if (input.type == types.ints || input.type == types.floats) {
			float f = input.getData();
			int i = (int) Math.ceil(f);
			input.setData(String.valueOf(i));
			input.type = types.ints;
		}
		return input;
	};

	final static Func_one_input func_rand = (input) -> {
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
