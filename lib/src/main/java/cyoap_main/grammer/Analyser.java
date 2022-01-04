package cyoap_main.grammer;

import cyoap_main.grammer.VariableDataBase.types;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Analyser implements IAnalyzer {
	static final Analyser instance = new Analyser();
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
				String str_tmp_front = null;
				if (pos_first != 0) {
					str_tmp_front = str_tmp.substring(0, pos_first - 1).trim();
				}
				if (str_tmp_front != null && !str_tmp_front.isEmpty()) {
					str_text.add(str_tmp.substring(0, pos_first - 1));
				}
				str_func.add(str_tmp_inner);
				str_tmp = str_tmp.substring(pos_second + 1);
			}
			return new Pair<>(str_text, str_func);
		} else
			return null;
	}


	public Pair<Recursive_Parser, Integer> create_parser(int i, List<ParsingUnit> data,
														 Recursive_Parser motherParser) {
		if (i >= data.size()) return new Pair<>(motherParser, i);
		switch (data.get(i).type()) {
			case function_start -> {
				while (true) {
					var inner = create_parser(i + 1, data, motherParser);
					i = inner.getValue();
					var inner_parser = inner.getKey();
					if (inner_parser == parser_comma) {
						i++;
						continue;
					} else if (inner_parser == parser_null) {
						break;
					}

					motherParser.add(inner_parser);
				}
				return new Pair<>(motherParser, i);
			}
			case function_end -> {
				return new Pair<>(parser_null, i);
			}
			case function -> {
				Recursive_Parser func_parser = new Recursive_Parser();
				func_parser.value = new ValueType(types.functions);
				func_parser.value.setData(data.get(i).data());
				return create_parser(i + 1, data, func_parser);
			}
			case function_comma -> {
				i++;
				return new Pair<>(parser_comma, i);
			}
			default -> {
				Recursive_Parser new_parser = new Recursive_Parser();
				if (data.get(i).type() == variable_name) {
					new_parser.value = new ValueType(VariableDataBase.getInstance().getValue(data.get(i).data()));
				} else {
					new_parser.value = new ValueType(LexicalAnalyzer.getInstance().getTypeFromInt(data.get(i)));
					new_parser.value.setData(data.get(i).data());
				}

				Recursive_Parser function_parser = new Recursive_Parser();

				if (data.size() >= i + 2) {
					if (data.get(i + 1).type() == function_unspecified) {
						function_parser.value = new ValueType(types.functions);
						function_parser.value.data = data.get(i + 1).data();
						function_parser.add(new_parser);
						var v = create_parser(i + 2, data, function_parser);
						i = v.getValue();
						function_parser.add(v.getKey());
						return new Pair<>(function_parser, i);
					}
				}

				return new Pair<>(new_parser, i);
			}
		}
	}

	public void analyse(List<ParsingUnit> analysed_data) throws Exception {
		Recursive_Parser parser = new Recursive_Parser();

		var t = analysed_data;

		if (analysed_data.get(0).data().equals("if") && analysed_data.get(0).type() == function) {
			int[] comma = new int[2];
			for (int i = 0; i < analysed_data.size(); i++) {
				if (analysed_data.get(i).type() == function_comma) {
					if (comma[0] == 0) {
						comma[0] = i;
					} else {
						comma[1] = i;
						break;
					}
				}
			}
			List<ParsingUnit> list_check = analysed_data.subList(2, comma[0]);
			List<ParsingUnit> list_true = analysed_data.subList(comma[0] + 1, comma[1]);
			List<ParsingUnit> list_false = analysed_data.subList(comma[1] + 1, analysed_data.size() - 1);


			var parser_ans = create_parser(0, list_check, parser);
			boolean check = (boolean) parser_ans.getKey().unzip().getData();
			if (check) {
				t = list_true;
			} else {
				t = list_false;
			}
		}
		int equal_pos = -1;
		for (int i = 0; i < analysed_data.size(); i++) {
			if (analysed_data.get(i).type() == equal) {
				equal_pos = i;
				break;
			}
		}

		if (equal_pos == -1) {
			throw new Exception("something wrong!");
		}
		var parser_ans = create_parser(equal_pos + 1, analysed_data, parser);

		String name = null;
		if (equal_pos != -1 && analysed_data.get(equal_pos - 1).type() == variable_name) {
			name = analysed_data.get(equal_pos - 1).data();
			ValueType data_answer = parser_ans.getKey().unzip();
			VariableDataBase.getInstance().setValue(name, data_answer);
		}
	}

	public void analyseList(List<String> str_list) {
		try {
			for (var str : str_list) {
				analyse(LexicalAnalyzer.getInstance().analyze(str));
			}
			System.out.println("all parsing end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}