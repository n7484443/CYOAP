package cyoap_main.grammer;

import cyoap_main.grammer.VariableDataBase.ValueType;
import cyoap_main.grammer.VariableDataBase.types;
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
	final int trues = 8;// 그외
	final int falses = 9;// 그외
	final int strs = 10;// string, ""로 표시

	final int others_string = 15;// 변수명

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
	public Pair<List<String>, List<String>> parser(String str) {
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
			List<String> str_text = new ArrayList<>();
			List<String> str_func = new ArrayList<>();
			String str_tmp = String.copyValueOf(str.toCharArray());
			while (true) {
				int pos_first = str_tmp.indexOf("{");
				int pos_second = str_tmp.indexOf("}");
				if (pos_first == -1 || pos_second == -1) {
					str_text.addAll(List.of(str_tmp.split("\n")));
					break;
				}

				var str_tmp_inner = str_tmp.substring(pos_first + 1, pos_second);
				var str_tmp_front = str_tmp.substring(0, pos_first - 1).trim();
				if (!str_tmp_front.isEmpty()) {
					str_text.add(str_tmp.substring(0, pos_first - 1));
				}
				str_func.add(str_tmp_inner);
				str_tmp = str_tmp.substring(pos_second + 1);
			}
			return new Pair<>(str_text, str_func);
		} else
			return null;
	}

	public Pair<List<Integer>, List<String>> replace(String s) {
		var str = s.replaceAll(" ", "").replaceAll("\n", "");
		List<Integer> func_int_List = new ArrayList<>();
		List<String> func_string_List = new ArrayList<>();
		int i = 0;
		while (str.length() > i) {
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
				case '"' -> b = !b;
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
						} else {
							if (func_int_List.get(size) == others_string) {
								func_string_List.set(size, func_string_List.get(size) + c);
								if (func_string_List.get(size).equalsIgnoreCase("true")) {
									func_int_List.set(size, trues);
								} else if (func_string_List.get(size).equalsIgnoreCase("false")) {
									func_int_List.set(size, falses);
								}
							} else if (func_int_List.get(size) != ints) {
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
		return new Pair<>(func_int_List, func_string_List);

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
		return switch (t) {
			case ints -> types.ints;
			case floats -> types.floats;
			case trues, falses -> types.booleans;
			case strs -> types.strings;
			case function -> types.functions;
			default -> types.nulls;
		};
	}

	public Pair<Recursive_Parser, Integer> create_parser(int i, List<Integer> func, List<String> data,
														 Recursive_Parser motherParser) {
		if (i >= func.size()) return new Pair<>(motherParser, i);
		if (func.get(i) == function_start) {
			while (true) {
				var inner = create_parser(i + 1, func, data, motherParser);
				i = inner.getValue();
				var inner_parser = inner.getKey();
				if (inner_parser == parser_comma) {
					i++;
					continue;
				} else if (inner_parser == null) {
					System.out.println("break");
					break;
				}
				
				motherParser.add(inner_parser);
			}
			return new Pair<>(motherParser, i);
		} else if (func.get(i) == function_end) {
			return new Pair<>(parser_null, i);
		} else if (func.get(i) == function) {
			Recursive_Parser func_parser = new Recursive_Parser();
			func_parser.value = new ValueType(types.functions);
			func_parser.value.setData(data.get(i));
			return create_parser(i + 1, func, data, func_parser);
		} else if (func.get(i) == function_comma) {
			i++;
			return new Pair<>(parser_comma, i);
		} else{
			Recursive_Parser new_parser = new Recursive_Parser();
			if(func.get(i) == others_string) {
				new_parser.value = new ValueType(VariableDataBase.getInstance().getValue(data.get(i)));
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
					return new Pair<>(function_parser, i);
				} else if (func.get(i + 1) == minus) {
					function_parser.value = new ValueType(types.functions);
					function_parser.value.data = "minus";
					function_parser.add(new_parser);
					var v = create_parser(i + 2, func, data, function_parser);
					i = v.getValue();
					function_parser.add(v.getKey());
					return new Pair<>(function_parser, i);
				} else if (func.get(i + 1) == multi) {
					function_parser.value = new ValueType(types.functions);
					function_parser.value.data = "multi";
					function_parser.add(new_parser);
					var v = create_parser(i + 2, func, data, function_parser);
					i = v.getValue();
					function_parser.add(v.getKey());
					return new Pair<>(function_parser, i);
				} else if (func.get(i + 1) == div) {
					function_parser.value = new ValueType(types.functions);
					function_parser.value.data = "div";
					function_parser.add(new_parser);
					var v = create_parser(i + 2, func, data, function_parser);
					i = v.getValue();
					function_parser.add(v.getKey());
					return new Pair<>(function_parser, i);
				}
			}

			return new Pair<>(new_parser, i);
		}
	}

	public void analyse(Pair<List<Integer>, List<String>> analysed_data) {
		var func = analysed_data.getKey();
		var data = analysed_data.getValue();

		int equal_pos = func.indexOf(equal);
		Recursive_Parser parser = new Recursive_Parser();

		System.out.println("end parser");
		var parser_ans = create_parser(equal_pos + 1, func, data, parser);

		System.out.println("recursive parse end");
		String name = "";
		ValueType vartype = null;
		if (func.get(equal_pos - 1) == others_string) {
			name = data.get(equal_pos - 1);
			if (VariableDataBase.getInstance().hasValue(data.get(equal_pos - 1))) {
				vartype = VariableDataBase.getInstance().getValue(data.get(equal_pos - 1));
			} else {
				vartype = new ValueType();
			}
		} else if (func.get(equal_pos - 2) == others_string) {
			name = data.get(equal_pos - 2);
			if (VariableDataBase.getInstance().hasValue(data.get(equal_pos - 2))) {
				vartype = VariableDataBase.getInstance().getValue(data.get(equal_pos - 2));
			} else {
				System.err.println("non exist name!");
			}
		}
		System.out.println("left side end");
		ValueType datas = parser_ans.getKey().unzip();

		if (vartype == null) {
			return;
		}
		if (func.get(equal_pos - 1) == plus) {
			vartype.add(datas);
		} else if (func.get(equal_pos - 1) == minus) {
			vartype.sub(datas);
		} else if (func.get(equal_pos - 1) == multi) {
			vartype.mul(datas);
		} else if (func.get(equal_pos - 1) == div) {
			vartype.div(datas);
		} else {
			vartype.set(datas);
		}
		VariableDataBase.getInstance().setValue(name, vartype);
		System.out.println("all parsing end");

	}

	public ValueType checkValueType(String data, int ty) {
		if (ty == others_string) {
			return VariableDataBase.getInstance().getValue(data);
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