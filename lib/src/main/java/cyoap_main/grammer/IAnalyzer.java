package cyoap_main.grammer;

public interface IAnalyzer {
    int ints = 1;// 정수
    int floats = 2;// 소수점 붙음
    int trues = 3;// 그외
    int falses = 4;// 그외
    int strs = 5;// string, ""로 표시

    int equal = -1;

    int variable_name = 10;// 변수명

    int function_unspecified = 19;
    int function = 20;
    int function_start = 21;// (
    int function_end = 22;// )
    int function_comma = 23;// ,
}
