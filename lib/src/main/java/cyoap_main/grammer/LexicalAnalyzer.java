package cyoap_main.grammer;

import java.util.ArrayList;
import java.util.List;

public class LexicalAnalyzer implements IAnalyzer {
    static final LexicalAnalyzer instance = new LexicalAnalyzer();
    // 같은 값이 반환시->다음값으로
    // null 일때->함수 입력 끝
    public boolean b = false;

    public static LexicalAnalyzer getInstance() {
        return instance;
    }

    /*
    어휘분석기. 토큰으로 변환한다.
     */
    public List<ParsingUnit> analyze(String s) {
        var str = s.replaceAll(" ", "").replaceAll("\n", "");
        List<ParsingUnit> func = new ArrayList<>();
        for (int i = 0; i < str.length(); i++) {
            var c = str.charAt(i);
            var size = func.size() - 1;
            switch (c) {
                case '+', '-', '*', '/' -> {
                    func.add(new ParsingUnit(function_unspecified, String.valueOf(c)));
                }
                case '=' -> {
                    if (str.charAt(i - 1) == '=') {
                        func.set(size, new ParsingUnit(function_unspecified, "=="));
                    } else if (str.charAt(i - 1) == '+' || str.charAt(i - 1) == '-' || str.charAt(i - 1) == '*' || str.charAt(i - 1) == '/') {
                        func.remove(size);
                        func.add(new ParsingUnit(equal, "="));
                        func.add(new ParsingUnit(variable_name, func.get(0).data()));//a += b 를 a = a + b 꼴로 변환
                        func.add(new ParsingUnit(function_unspecified, String.valueOf(str.charAt(i - 1))));//a += b 를 a = a + b 꼴로 변환
                    } else {
                        func.add(new ParsingUnit(equal, "="));
                    }
                }
                case '"' -> b = !b;
                case '(' -> {
                    if (func.get(size).type() == variable_name) {
                        func.set(size, func.get(size).changeUnitType(function));
                        func.add(new ParsingUnit(function_start, "("));
                    } else {
                        return null;
                    }
                }
                case ')' -> {
                    func.add(new ParsingUnit(function_end, ")"));
                }
                case ',' -> {
                    func.add(new ParsingUnit(function_comma, ","));
                }
                default -> {
                    if (b) {
                        if (func.get(size).type() == strs) {
                            func.set(size, func.get(size).addUnitData(c));
                        } else {
                            func.add(new ParsingUnit(strs, String.valueOf(c)));
                        }
                    } else {
                        var isDigit = Character.isDigit(c);
                        if (func.size() == 0) {
                            func.add(new ParsingUnit(isDigit ? ints : variable_name, String.valueOf(c)));
                        } else if (c == '.') {
                            func.set(size, new ParsingUnit(floats, func.get(size).data() + c));
                            if (!isStringDouble(func.get(size).data())) {
                                System.err.println("error! float has more than two point(.)");
                            }
                        } else if (isDigit) {
                            switch (func.get(size).type()) {
                                case variable_name, ints, floats -> func.set(size, func.get(size).addUnitData(c));
                                default -> func.add(new ParsingUnit(ints, String.valueOf(c)));
                            }
                        } else {
                            if (func.get(size).type() == variable_name) {
                                func.set(size, func.get(size).addUnitData(c));
                                if (func.get(size).data().equalsIgnoreCase("true")) {
                                    func.set(size, func.get(size).changeUnitType(trues));
                                } else if (func.get(size).data().equalsIgnoreCase("false")) {
                                    func.set(size, func.get(size).changeUnitType(falses));
                                }
                            } else if (func.get(size).type() != ints) {
                                func.add(new ParsingUnit(variable_name, String.valueOf(c)));
                                if (func.get(size).data().equalsIgnoreCase("true")) {
                                    func.set(size, func.get(size).changeUnitType(trues));
                                } else if (func.get(size).data().equalsIgnoreCase("false")) {
                                    func.set(size, func.get(size).changeUnitType(falses));
                                }
                            }
                        }
                    }
                }
            }
        }
        return func;
    }

    public VariableDataBase.types getTypeFromInt(int t) {
        return switch (t) {
            case ints -> VariableDataBase.types.ints;
            case floats -> VariableDataBase.types.floats;
            case trues, falses -> VariableDataBase.types.booleans;
            case strs -> VariableDataBase.types.strings;
            case function -> VariableDataBase.types.functions;
            case variable_name -> VariableDataBase.types.variable;
            default -> VariableDataBase.types.nulls;
        };
    }

    public VariableDataBase.types getTypeFromInt(ParsingUnit t) {
        return getTypeFromInt(t.type());
    }

    public boolean isStringDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
