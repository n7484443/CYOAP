package design;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

public class Panel_Left_Side implements Panel{
	public JPanel panel_side_variable;
	public Frame mother_frame;
	public JButton button;
	
	public void init(Frame mother_frame) {
		this.mother_frame = mother_frame;
		
		panel_side_variable = new JPanel();
		
		panel_side_variable.setSize(mother_frame.getSize().width / 5, mother_frame.getSize().height);
		panel_side_variable.setLocation(0, 0);
		mother_frame.add(panel_side_variable);
		
		button = new JButton();
		button.setLocation(50, 50);
		button.setSize(100, 50);
		button.setText("Next");
		panel_side_variable.add(button);
		panel_side_variable.setLayout(null);
		panel_side_variable.setBorder(new MatteBorder(0, 0, 0, 1, Color.BLACK));
		
	}

	@Override
	public void update() {
		
	}

	@Override
	public void paint(Graphics g, int top) {
		// TODO Auto-generated method stub
		
	}
}
