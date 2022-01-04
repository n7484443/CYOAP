package cyoap_main.grammer;

import cyoap_main.grammer.FunctionList.Func_one_input;
import cyoap_main.grammer.FunctionList.Func_two_input;
import cyoap_main.grammer.FunctionList.iFunction;

public class Recursive_Parser {
	public Recursive_Parser[] child_node = new Recursive_Parser[3];
	public ValueType value;
	// 노드마다 가지는 최대의 데이터:3
	// if ( a, then, else) 가 최대

	public void add(Recursive_Parser parser) {
		for (int i = 0; i < child_node.length; i++) {
			if (child_node[i] == null) {
				child_node[i] = parser;
				return;
			}
		}
	}

	public int checkParser(int i) {
		if (value != null) System.out.println(i + ":" + value.data + ":" + value.type);
		for (int k = 0; k < 3; k++) {
			if (child_node[k] != null) {
				i++;
				i = child_node[k].checkParser(i);
			}
		}
		return i;
	}

	public void checkParser() {
		checkParser(0);
	}

	public ValueType unzip() {
		if (value.getData() instanceof iFunction func) {
			if (func instanceof Func_one_input func_oneInput) {
				return func_oneInput.func(child_node[0].unzip());
			} else if (func instanceof Func_two_input func_twoInput) {
				return func_twoInput.func(child_node[0].unzip(), child_node[1].unzip());
			} else {
				FunctionList.Func_three_input func_three = (FunctionList.Func_three_input) func;
				return func_three.func(child_node[0].unzip(), child_node[1].unzip(), child_node[2].unzip());
			}
		} else {
			return value;
		}
	}
}
