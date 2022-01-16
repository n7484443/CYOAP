package cyoap_main.grammer;

import cyoap_main.grammer.VariableDataBase.types;
import javafx.util.Pair;

import java.util.Arrays;
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
	public List<String> parser(String str) {
		if (str == null)
			return null;
		return Arrays.stream(str.split("\n")).toList();
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
		if (analysed_data.isEmpty()) return;
		Recursive_Parser parser = new Recursive_Parser();

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
			boolean check = parser_ans.getKey().unzip().getData();
			if (check) {
				analyse(list_true);
			} else {
				analyse(list_false);
			}
			return;
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

		if (analysed_data.get(equal_pos - 1).type() == variable_name) {
			String name = analysed_data.get(equal_pos - 1).data();
			ValueType data_answer = parser_ans.getKey().unzip();
			VariableDataBase.getInstance().setValue(name, data_answer);
		}
	}

	public void analyseList(List<String> str_list) throws Exception {
		for (var str : str_list) {
			analyse(LexicalAnalyzer.getInstance().analyze(str));
		}
		System.out.println("all parsing end");
	}
}