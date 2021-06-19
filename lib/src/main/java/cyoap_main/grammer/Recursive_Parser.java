package cyoap_main.grammer;

import cyoap_main.grammer.FunctionList.Func_one;
import cyoap_main.grammer.FunctionList.Func_three;
import cyoap_main.grammer.FunctionList.Func_two;
import cyoap_main.grammer.FunctionList.Function_for_d;
import cyoap_main.grammer.VarData.ValueType;

public class Recursive_Parser {
	public Recursive_Parser[] child_node = new Recursive_Parser[3];
	public Recursive_Parser parent_node;
	public ValueType value;
	// 노드마다 가지는 최대의 데이터:3
	// if ( a, then, else)

	public Recursive_Parser() {

	}

	public void add(Recursive_Parser parser) {
		for (int i = 0; i < child_node.length; i++) {
			if (child_node[i] == null) {
				child_node[i] = parser;
				return;
			}
		}
	}

	public int checkParser(int i) {
		if(value != null)System.out.println(i + ":" + value.data + ":" + value.type);
		for(int k = 0; k < 3; k++) {
			if(child_node[k] != null) {
				i++;
				i = child_node[k].checkParser(i);
			}
		}
		return i;
	}

	public ValueType unzip() {
		if (value.getData() instanceof Function_for_d) {
			Function_for_d func = value.getData();

			if (func instanceof Func_one) {
				Func_one func_one = (Func_one) func;
				return func_one.func(child_node[0].unzip());
			} else if (func instanceof Func_two) {
				Func_two func_two = (Func_two) func;
				return func_two.func(child_node[0].unzip(), child_node[1].unzip());
			} else {
				Func_three func_three = (Func_three) func;
				return func_three.func(child_node[0].unzip(), child_node[1].unzip(), child_node[2].unzip());
			}
		} else {
			return value;
		}
	}
}
