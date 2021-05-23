package design;

import java.awt.Graphics;

import javax.swing.JFrame;

public interface Panel {
	public void init(JFrame mother_frame);
	public void update();
	public void paint(Graphics g);
}
