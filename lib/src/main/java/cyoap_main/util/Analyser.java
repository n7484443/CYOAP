package cyoap_main.util;

import java.util.ArrayList;
import java.util.List;

import cyoap_main.core.VarData;
import cyoap_main.core.VarData.ValueType;
import cyoap_main.core.VarData.types;
import javafx.util.Pair;

public class Analyser {
	public static final int plus = 1;// +
	public static final int minus = 2;// -
	public static final int multi = 3;// *
	public static final int div = 4;// /
	public static final int equal = 5;// =

	public static final int ints = 6;// 정수
	public static final int floats = 7;// 소수점 붙음
	public static final int booleans = 8;// true or false
	public static final int strs = 9;// string, ""로 표시

	public static final int others_string = 10;// 변수명

	public static final int trues = 15;// 그외
	public static final int falses = 16;// 그외

	public static final int function = 20;
	public static final int function_start = 21;// (
	public static final int function_end = 22;// )
	public static final int function_comma = 23;// ,
	public static final int function_semi = 24;// ;

	/*
	 * 문자 입력->텍스트와 문법을 분리 {} 내부에 문법 사용 즉, 실제 사용 가능한 것은 [], () 정도.
	 */
	public static List<String> parser(String str) {
		if (str == null)
			return null;
		if (str.chars().filter(e -> e == ((char) '{')).count() != str.chars().filter(e -> e == ((char) '}')).count()) {
			System.err.println("{와 }의 개수가 다릅니다!");
			return null;
		}
		if (str.chars().filter(e -> e == ((char) '(')).count() != str.chars().filter(e -> e == ((char) ')')).count()) {
			System.err.println("(와 )의 개수가 다릅니다!");
			return null;
		}
		if (str.chars().filter(e -> e == ((char) '[')).count() != str.chars().filter(e -> e == ((char) ']')).count()) {
			System.err.println("[와 ]의 개수가 다릅니다!");
			return null;
		}
		if (str.contains("{")) {
			List<String> str_text = new ArrayList<String>();
			List<String> str_func = new ArrayList<String>();
			var first_str = str.split("\\{");
			for (int i = 0; i < first_str.length; i++) {
				if (i == 0) {
					str_text.add(first_str[0]);
				} else {
					var second_str = first_str[i].split("\\}");
					str_func.add(second_str[0]);
					if (second_str.length == 2) {
						str_text.add(second_str[1]);
					}
				}
			}
			analyse(str_func);
			return str_text;
		} else
			return null;
	}

	public static boolean b = false;

	public static Pair<List<Integer>, List<String>> replace(String s) {
		var str = s.replaceAll(" ", "").replaceAll("\n", "");
		List<Integer> func_int_List = new ArrayList<Integer>();
		List<String> func_string_List = new ArrayList<String>();
		int i = 0;
		while (true) {
			if (str.length() <= i)
				break;
			var c = str.charAt(i);
			var size = func_string_List.size() - 1;
			switch (c) {
			case '+' -> {
				func_int_List.add(plus);
				func_string_List.add("+");
			}
			case '-' -> {
				func_int_List.add(minus);
				func_string_List.add("-");
			}
			case '*' -> {
				func_int_List.add(multi);
				func_string_List.add("*");
			}
			case '/' -> {
				func_int_List.add(div);
				func_string_List.add("/");
			}
			case '=' -> {
				func_int_List.add(equal);
				func_string_List.add("=");
			}
			case '"' -> {
				b = !b;
			}
			case '(' -> {
				if (func_int_List.get(size) == others_string) {
					func_int_List.set(size, function);
					func_int_List.add(function_start);
					func_string_List.add("(");
				} else {
					return null;
				}
			}
			case ')' -> {
				func_int_List.add(function_end);
				func_string_List.add(")");
			}
			case ',' -> {
				func_int_List.add(function_comma);
				func_string_List.add(",");
			}
			case ';' -> {
				func_int_List.add(function_semi);
				func_string_List.add(";");
			}
			default -> {
				if (b) {
					if (func_int_List.get(size).equals(strs)) {
						func_string_List.set(size, func_string_List.get(size) + c);
					} else {
						func_string_List.add(String.valueOf(c));
						func_int_List.add(strs);
					}
				} else {
					var isDigit = Character.isDigit(c);
					if (func_int_List.size() == 0) {
						func_int_List.add(isDigit ? ints : others_string);
						func_string_List.add(String.valueOf(c));
					} else if (c == '.') {
						func_string_List.set(size, func_string_List.get(size) + c);
						func_int_List.set(size, floats);
						if (!isStringDouble(func_string_List.get(size))) {
							System.err.println("error! float has more than two point(.)");
						}
					} else if (isDigit) {
						if (func_int_List.get(size) == others_string) {
							func_string_List.set(size, func_string_List.get(size) + c);
						}else if (func_int_List.get(size) == ints) {
							if (c == '.') {
								func_string_List.set(size, func_string_List.get(size) + c);
								func_int_List.set(size, floats);
								if (!isStringDouble(func_string_List.get(size))) {
									System.err.println("error! float has more than two point(.)");
								}
							} else {
								func_string_List.set(size, func_string_List.get(size) + c);
							}
						} else if (func_int_List.get(size) == floats) {
							func_string_List.set(size, func_string_List.get(size) + c);
						} else {
							func_string_List.add(String.valueOf(c));
							func_int_List.add(ints);
						}
					} else if (!isDigit){
						if(func_int_List.get(size) == others_string) {
							func_string_List.set(size, func_string_List.get(size) + c);
							if (func_string_List.get(size).toLowerCase().equals("true")) {
								func_int_List.set(size, trues);
							} else if (func_string_List.get(size).toLowerCase().equals("false")) {
								func_int_List.set(size, falses);
							}
						}else {
							func_string_List.add(String.valueOf(c));
							func_int_List.add(others_string);
							if (func_string_List.get(size).toLowerCase().equals("true")) {
								func_int_List.set(size, trues);
							} else if (func_string_List.get(size).toLowerCase().equals("false")) {
								func_int_List.set(size, falses);
							}
						}
					}
				}
			}
			}

			i++;
		}
		return new Pair<List<Integer>, List<String>>(func_int_List, func_string_List);

	}

	public static boolean isStringDouble(String s) {
		try {
			Double.parseDouble(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static void analyse(Pair<List<Integer>, List<String>> analysed_data) {
		var func = analysed_data.getKey();
		var data = analysed_data.getValue();

		//for (int i = 0; i < data.size(); i++) {
		//	System.out.println(func.get(i) + ":" + data.get(i));
		//}

		for (int i = 0; i < data.size(); i++) {
			if (func.get(i) == equal) {
				ValueType vatype = null;
				int j = i;
				String name = "";
				if (func.get(i + 1) == ints) {
					vatype = new ValueType(types.ints);
					name = data.get(i - 1);
				} else if (func.get(i + 1) == floats) {
					vatype = new ValueType(types.floats);
					name = data.get(i - 1);
				} else if (func.get(i + 1) == booleans) {
					vatype = new ValueType(types.booleans);
					name = data.get(i - 1);
					j++;
				} else if (func.get(i + 1) == strs) {
					vatype = new ValueType(types.strings);
					name = data.get(i - 1);
				} else {
					if (func.get(i - 1) == others_string) {
						vatype = VarData.getValue(data.get(i - 1));
						if (vatype == null) {
							vatype = new ValueType();
							name = data.get(i - 1);
						}
					} else if (func.get(i - 2) == others_string) {
						vatype = VarData.getValue(data.get(i - 2));
						name = data.get(i - 2);
					}
				}

				// func ( data )
				ValueType datas = null;
				if (func.get(j + 1) == function) {
					ValueType v = checkValueType(data.get(j + 3), func.get(j + 3));
					datas = function_find(data.get(j + 1), v);
				} else {
					datas = checkValueType(data.get(j + 1), func.get(j + 1));
				}

				if (func.get(i - 1) == plus) {
					vatype.add(datas);
				} else if (func.get(i - 1) == minus) {
					vatype.sub(datas);
				} else if (func.get(i - 1) == multi) {
					vatype.mul(datas);
				} else if (func.get(i - 1) == div) {
					vatype.div(datas);
				} else {
					vatype.set(datas);
				}
				VarData.setValue(name, vatype);
			}
		}
	}

	public static ValueType checkValueType(String data, int ty) {
		if (ty == others_string) {
			return VarData.getValue(data);
		} else if (ty == strs) {
			return new ValueType(types.strings, data);
		} else if (ty == ints) {
			return new ValueType(types.ints, data);
		} else if (ty == trues || ty == falses) {
			return new ValueType(types.booleans, ty == trues ? "true" : "false");
		} else if (ty == floats) {
			return new ValueType(types.floats, data);
		} else {
			return new ValueType(types.nulls);
		}

	}

	public static ValueType innerLoop_right(List<Integer> func, List<String> data, ValueType v) {

		return v;
	}

	public static ValueType function_find(String func_name, ValueType input) {
		ValueType val = new ValueType(input);
		switch (func_name) {
		case "print" -> {

		}
		//if ( 조건, 결과)
		//elseif ( 조건, 결과)
		//else ( 결과)
		case "if" -> {
			
		}
		case "elseif" -> {
			
		}
		case "else" -> {
			
		}
		case "floor" -> {
			float f;
			if (val.type.equals(types.ints)) {
				int i = val.getData();
				f = i;
			} else {
				f = val.getData();
			}
			val.setData(String.valueOf((int) Math.floor(f)));
			return val;
		}
		case "round" -> {
			float f;
			if (val.type.equals(types.ints)) {
				int i = val.getData();
				f = i;
			} else {
				f = val.getData();
			}
			val.setData(String.valueOf((int) Math.round(f)));
			return val;
		}
		case "ceil" -> {
			float f;
			if (val.type.equals(types.ints)) {
				int i = val.getData();
				f = i;
			} else {
				f = val.getData();
			}
			val.setData(String.valueOf((int) Math.ceil(f)));
			return val;
		}
		}
		return val;
	}

	public static void analyse(List<String> str_list) {
		for (var str : str_list) {
			analyse(replace(str));
		}
	}
}