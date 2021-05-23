package design;

import java.awt.Frame;
import java.awt.Graphics;

public interface Panel {
	public void init(Frame mother_frame);
	public void update();
	public void paint(Graphics g, int top);
}
