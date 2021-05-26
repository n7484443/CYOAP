package design;

import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JPanel;

public interface Panel {
	public void init(JPanel mother_panel);
	public void update();
	public void paint(Graphics g, Insets insets);
}
