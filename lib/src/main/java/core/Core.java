package core;

public class Core {
	public static String version = "0.0.1";
	public static void main(String[] args) {
		new Thread(new Starter()).run();
		//SwingUtilities.invokeLater(new Starter());
	}
}
