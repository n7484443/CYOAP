package design;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

public class Panel_Left_Side implements Panel{
	public JPanel panel_side_variable;
	public JPanel mother_panel;
	public JButton button;
	
	public void init(JPanel mother_panel) {
		this.mother_panel = mother_panel;
		panel_side_variable = new JPanel();

		var gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.BOTH;
		mother_panel.add(panel_side_variable, gbc);

		button = new JButton();
		button.setText("Next");
		panel_side_variable.add(button);
		panel_side_variable.setBorder(new MatteBorder(0, 0, 0, 1, Color.BLACK));
		
		
	}

	@Override
	public void update() {
		
	}

	@Override
	public void paint(Graphics g, Insets insets) {
		// TODO Auto-generated method stub
		
	}
}
