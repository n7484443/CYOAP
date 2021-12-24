package cyoap_main.grammer;

import cyoap_main.grammer.VarData.ValueType;
import cyoap_main.grammer.VarData.types;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Analyser {
	static final Analyser instance = new Analyser();
	final int plus = 1;// +
	final int minus = 2;// -
	final int multi = 3;// *
	final int div = 4;// /
	final int equal = 5;// =

	final int ints = 6;// 정수
	final int floats = 7;// 소수점 붙음
	final int booleans = 8;// true or false
	final int strs = 9;// string, ""로 표시

	final int others_string = 10;// 변수명

	final int trues = 15;// 그외
	final int falses = 16;// 그외

	final int function = 20;
	final int function_start = 21;// (
	final int function_end = 22;// )
	final int function_comma = 23;// ,
	final int function_semi = 24;// ;
	public boolean b = false;
	// 같은 값이 반환시->다음값으로
	// null 일때->함수 입력 끝
	public Recursive_Parser parser_null = null;
	public Recursive_Parser parser_comma = new Recursive_Parser();

	public static Analyser getInstance() {
		return instance;
	}

	/*
	 * 문자 입력->텍스트와 문법을 분리 {} 내부에 문법 사용. 즉, 실제 사용 가능한 것은 [], () 정도.
	 */
	public List<String> parser(String str) {
		if (str == null)
			return null;
		if (str.chars().filter(e -> e == '{').count() != str.chars().filter(e -> e == '}').count()) {
			System.err.println("{와 }의 개수가 다릅니다!");
			return null;
		}
		if (str.chars().filter(e -> e == '(').count() != str.chars().filter(e -> e == ')').count()) {
			System.err.println("(와 )의 개수가 다릅니다!");
			return null;
		}
		if (str.chars().filter(e -> e == '[').count() != str.chars().filter(e -> e == ']').count()) {
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

	public Pair<List<Integer>, List<String>> replace(String s) {
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
						} else if (func_int_List.get(size) == ints) {
							func_string_List.set(size, func_string_List.get(size) + c);
						} else if (func_int_List.get(size) == floats) {
							func_string_List.set(size, func_string_List.get(size) + c);
						} else {
							func_string_List.add(String.valueOf(c));
							func_int_List.add(ints);
						}
					} else if (!isDigit) {
						if (func_int_List.get(size) == ints) {
							if (c == '.') {
								func_string_List.set(size, func_string_List.get(size) + c);
								func_int_List.set(size, floats);
								if (!isStringDouble(func_string_List.get(size))) {
									System.err.println("error! float has more than two point(.)");
								}
							}
						}else if (func_int_List.get(size) == others_string) {
							func_string_List.set(size, func_string_List.get(size) + c);
							if (func_string_List.get(size).equalsIgnoreCase("true")) {
								func_int_List.set(size, trues);
							} else if (func_string_List.get(size).equalsIgnoreCase("false")) {
								func_int_List.set(size, falses);
							}
						} else {
							func_string_List.add(String.valueOf(c));
							func_int_List.add(others_string);
							if (func_string_List.get(size).equalsIgnoreCase("true")) {
								func_int_List.set(size, trues);
							} else if (func_string_List.get(size).equalsIgnoreCase("false")) {
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

	public boolean isStringDouble(String s) {
		try {
			Double.parseDouble(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public types getTypeFromInt(int t) {
		switch (t) {
			case ints:
				return types.ints;
			case floats:
				return types.floats;
			case booleans:
				return types.booleans;
			case strs:
				return types.strings;
			case function:
			return types.functions;
		default:
			return types.nulls;
		}
	}

	public Pair<Recursive_Parser, Integer> create_parser(int i, List<Integer> func, List<String> data,
														 Recursive_Parser motherParser) {
		if (i >= func.size()) return new Pair<Recursive_Parser, Integer>(motherParser, i);
		if (func.get(i) == function_start) {
			while (true) {
				var inner = create_parser(i + 1, func, data, motherParser);
				i = inner.getValue();
				var inner_parser = inner.getKey();
				if (inner_parser == parser_comma) {
					i++;
					continue;
				}else if(inner_parser == null) {
					System.out.println("break");
					break;
				}
				
				motherParser.add(inner_parser);
			}
			return new Pair<Recursive_Parser, Integer>(motherParser, i);
		} else if (func.get(i) == function_end) {
			return new Pair<Recursive_Parser, Integer>(parser_null, i);
		} else if (func.get(i) == function) {
			Recursive_Parser func_parser = new Recursive_Parser();
			func_parser.value = new ValueType(types.functions);
			func_parser.value.setData(data.get(i));
			return create_parser(i + 1, func, data, func_parser);
		} else if (func.get(i) == function_comma) {
			i++;
			return new Pair<Recursive_Parser, Integer>(parser_comma, i);
		} else{
			Recursive_Parser new_parser = new Recursive_Parser();
			if(func.get(i) == others_string) {
				new_parser.value = new ValueType(VarData.getInstance().getValue(data.get(i)));
			}else {
				new_parser.value = new ValueType(getTypeFromInt(func.get(i)));
				new_parser.value.setData(data.get(i));
			}

			Recursive_Parser function_parser = new Recursive_Parser();

			if (func.size() >= i + 2) {
				if (func.get(i + 1) == plus) {
					function_parser.value = new ValueType(getTypeFromInt(function));
					function_parser.value.data = "plus";
					function_parser.add(new_parser);
					var v = create_parser(i + 2, func, data, function_parser);
					i = v.getValue();
					function_parser.add(v.getKey());
					return new Pair<Recursive_Parser, Integer>(function_parser, i);
				} else if (func.get(i + 1) == minus) {
					function_parser.value = new ValueType(types.functions);
					function_parser.value.data = "minus";
					function_parser.add(new_parser);
					var v = create_parser(i + 2, func, data, function_parser);
					i = v.getValue();
					function_parser.add(v.getKey());
					return new Pair<Recursive_Parser, Integer>(function_parser, i);
				} else if (func.get(i + 1) == multi) {
					function_parser.value = new ValueType(types.functions);
					function_parser.value.data = "multi";
					function_parser.add(new_parser);
					var v = create_parser(i + 2, func, data, function_parser);
					i = v.getValue();
					function_parser.add(v.getKey());
					return new Pair<Recursive_Parser, Integer>(function_parser, i);
				} else if (func.get(i + 1) == div) {
					function_parser.value = new ValueType(types.functions);
					function_parser.value.data = "div";
					function_parser.add(new_parser);
					var v = create_parser(i + 2, func, data, function_parser);
					i = v.getValue();
					function_parser.add(v.getKey());
					return new Pair<Recursive_Parser, Integer>(function_parser, i);
				}
			}

			return new Pair<Recursive_Parser, Integer>(new_parser, i);
		}
	}

	public void analyse(Pair<List<Integer>, List<String>> analysed_data) {
		var func = analysed_data.getKey();
		var data = analysed_data.getValue();

		int equal_pos = func.indexOf(equal);
		Recursive_Parser parser = new Recursive_Parser();

		System.out.println("end parser");
		var parser_ans = create_parser(equal_pos + 1, func, data, parser);
		//parser_ans.getKey().checkParser(0);
		System.out.println("recursive parse end");
		String name = "";
		ValueType vatype = null;
		if (func.get(equal_pos - 1) == others_string) {
			name = data.get(equal_pos - 1);
			if (VarData.getInstance().hasValue(data.get(equal_pos - 1))) {
				vatype = VarData.getInstance().getValue(data.get(equal_pos - 1));
			} else {
				vatype = new ValueType();
			}
		} else if (func.get(equal_pos - 2) == others_string) {
			name = data.get(equal_pos - 2);
			if (VarData.getInstance().hasValue(data.get(equal_pos - 2))) {
				vatype = VarData.getInstance().getValue(data.get(equal_pos - 2));
			} else {
				System.err.println("non exist name!");
			}
		}
		System.out.println("left side end");
		ValueType datas = parser_ans.getKey().unzip();

		if (func.get(equal_pos - 1) == plus) {
			vatype.add(datas);
		} else if (func.get(equal_pos - 1) == minus) {
			vatype.sub(datas);
		} else if (func.get(equal_pos - 1) == multi) {
			vatype.mul(datas);
		} else if (func.get(equal_pos - 1) == div) {
			vatype.div(datas);
		} else {
			vatype.set(datas);
		}
		VarData.getInstance().setValue(name, vatype);
		System.out.println("all parsing end");

		// for (int i = 0; i < data.size(); i++) {
		// System.out.println(func.get(i) + ":" + data.get(i));
		// }

	}

	public ValueType checkValueType(String data, int ty) {
		if (ty == others_string) {
			return VarData.getInstance().getValue(data);
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

	public ValueType innerLoop_right(List<Integer> func, List<String> data, ValueType v) {
		return v;
	}

	public void analyse(List<String> str_list) {
		for (var str : str_list) {
			analyse(replace(str));
		}
	}
}