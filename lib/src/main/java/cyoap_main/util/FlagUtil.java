package cyoap_main.util;

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
	
	public static boolean getFlag(int flag, int position) {
		int t = 1 << position;
		return (flag & t) > 0;
	}
}
