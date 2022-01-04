package cyoap_main.grammer;

public interface IAnalyzer {
    final int ints = 1;// 정수
    final int floats = 2;// 소수점 붙음
    final int trues = 3;// 그외
    final int falses = 4;// 그외
    final int strs = 5;// string, ""로 표시

    final int equal = -1;

    final int variable_name = 10;// 변수명

    final int function_unspecified = 19;
    final int function = 20;
    final int function_start = 21;// (
    final int function_end = 22;// )
    final int function_comma = 23;// ,
}
