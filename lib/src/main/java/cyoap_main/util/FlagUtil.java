package cyoap_main.util;

import java.util.List;

public class FlagUtil {
	public static int setFlag(int flag, int position, boolean b) {
		int t = 1 << position;
		if(b) {
			flag |= t;
		}else {
			flag &= ~t;
		}
		return flag;
	}
	public static int setFlag(int flag, int position) {
		return setFlag(flag, position, true);
	}
	
	public static boolean getFlag(int flag, int position) {
		int t = 1 << position;
		return (flag & t) > 0;
	}

	public static int createFlag(List<Boolean> position) {
		int t = 0;
		for(int pos = 0; pos < position.size(); pos++){
			t = setFlag(t, pos, position.get(pos));
		}
		return t;
	}
}
