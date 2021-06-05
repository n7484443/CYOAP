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

	public static final int ints = 6;// &i
	public static final int floats = 7;// &f
	public static final int booleans = 8;// &b
	public static final int strings = 9;// &s

	public static final int others_string = 10;// 그외
	public static final int others_num = 11;// 그외

	public static final int function = 20;
	public static final int function_start = 21;// (
	public static final int function_end = 22;// )
	public static final int function_comma = 23;// ,
	public static final int function_semi = 24;// ;

	/*
	 * 문자 입력->텍스트와 문법을 분리
	 */
	public static List<String> parser(String str) {
		if (str == null)
			return null;
		if (str.chars().filter(e -> e == ((char) '{')).count() != str.chars().filter(e -> e == ((char) '}')).count()) {
			System.err.println("{와 }의 개수가 다릅니다!");
			return null;
		}
		if (str.contains("{")) {
			List<String> str_text = new ArrayList<String>();
			List<String> str_func = new ArrayList<String>();
			var first_str = str.split("\\{");
			for (int i = 0; i < first_str.length; i++) {
				if(i == 0) {
					str_text.add(first_str[0]);
				}else {
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

			case '&' -> {
				if (str.length() <= i + 1 + 1)
					break;
				if (str.charAt(i + 1) == 'i') {
					func_int_List.add(ints);
					func_string_List.add("&i");
					i++;
				} else if (str.charAt(i + 1) == 'f') {
					func_int_List.add(floats);
					func_string_List.add("&f");
					i++;
				} else if (str.charAt(i + 1) == 'b') {
					func_int_List.add(booleans);
					func_string_List.add("&b");
					i++;
				} else if (str.charAt(i + 1) == 's') {
					func_int_List.add(strings);
					func_string_List.add("&s");
					i++;
				}
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
				var isDigit = Character.isDigit(c);
				var others = isDigit ? others_num : others_string;
				if (func_int_List.size() == 0) {
					func_int_List.add(others);
					func_string_List.add(String.valueOf(c));
				} else {
					if (func_int_List.get(size) != others) {
						if (func_int_List.get(size) == others_string) {
							func_string_List.set(size, func_string_List.get(size) + c);
						} else if (func_int_List.get(size) == others_num) {
							if (c == '.') {
								func_string_List.set(size, func_string_List.get(size) + c);
								if (!isStringDouble(func_string_List.get(size))) {
									func_int_List.set(size, others_string);
								}
							} else {
								func_int_List.set(size, others_string);
								func_string_List.set(size, func_string_List.get(size) + c);
							}
						} else {
							func_int_List.add(others);
							func_string_List.add(String.valueOf(c));
						}
					} else {
						func_string_List.set(size, func_string_List.get(size) + c);
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
		for (int i = 0; i < data.size(); i++) {
			if (func.get(i) == equal) {
				ValueType vatype = null;
				int j = i;
				String name = "";
				if (func.get(i + 1) == ints) {
					vatype = new ValueType(types.ints);
					name = data.get(i - 1);
					j++;
				} else if (func.get(i + 1) == floats) {
					vatype = new ValueType(types.floats);
					name = data.get(i - 1);
					j++;
				} else if (func.get(i + 1) == booleans) {
					vatype = new ValueType(types.booleans);
					name = data.get(i - 1);
					j++;
				} else if (func.get(i + 1) == strings) {
					vatype = new ValueType(types.strings);
					name = data.get(i - 1);
					j++;
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

				ValueType datas = null;
				if (func.get(j + 1) == function) {
					// func ( data )
					if (func.get(j + 3) == others_string) {
						datas = function_find(data.get(j + 1), VarData.getValue(data.get(j + 3)));
					} else {
						datas = function_find(data.get(j + 1), data.get(j + 3));
					}
				} else if (func.get(j + 1) == others_string) {
					if (VarData.hasValue(data.get(j + 1))) {
						datas = VarData.getValue(data.get(j + 1));
					} else {
						datas = new ValueType(types.strings);
						datas.setData(data.get(j + 1));
					}
				} else if (func.get(j + 1) == others_num) {
					datas = new ValueType(vatype.type);
					datas.setData(data.get(j + 1));
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

	public static ValueType function_find(String func_name, ValueType input) {
		ValueType val = new ValueType(input);
		switch (func_name) {
		case "print" -> {

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

	public static ValueType function_find(String func_name, String input) {
		switch (func_name) {
		case "print" -> {
		}
		case "floor" -> {
			ValueType v = new ValueType(types.ints);
			v.setData(String.valueOf((int) Math.floor(Float.valueOf(input))));
			return v;
		}
		case "round" -> {
			ValueType v = new ValueType(types.ints);
			v.setData(String.valueOf((int) Math.round(Float.valueOf(input))));
			return v;
		}
		case "ceil" -> {
			ValueType v = new ValueType(types.ints);
			v.setData(String.valueOf((int) Math.ceil(Float.valueOf(input))));
			return v;
		}
		}
		return null;
	}

	public static void analyse(List<String> str_list) {
		for (var str : str_list) {
			analyse(replace(str));
		}
	}
}
