package design;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Panel_Main implements Panel {
	public JPanel panel_image;
	public JPanel panel_text;
	public JPanel panel_all;
	public JLabel label_image;
	public JFrame mother_frame;
	public ImageIcon image;
	public Image image_size = null;
	public boolean change = false;

	public void init(JFrame mother_frame) {
		this.mother_frame = mother_frame;

		panel_all = new JPanel();
		panel_text = new JPanel();
		panel_image = new JPanel();
		label_image = new JLabel();
		panel_all.add(panel_text);
		panel_all.add(panel_image);
		panel_image.add(label_image);
		
		panel_image.setLayout(null);
		panel_text.setLayout(null);
		panel_all.setLayout(null);

		panel_all.setSize(mother_frame.getSize().width / 5 * 4, mother_frame.getSize().height);
		panel_all.setLocation(mother_frame.getSize().width / 5, 0);
		mother_frame.add(panel_all);

		panel_image.setSize(mother_frame.getSize().width / 5 * 4, (int) (mother_frame.getSize().height / 3f * 2));
		panel_image.setLocation(mother_frame.getSize().width / 5, 0);
		panel_text.setSize(mother_frame.getSize().width / 5 * 4, (int) (mother_frame.getSize().height / 3f * 1));
		panel_text.setLocation(mother_frame.getSize().width / 5, (int) (mother_frame.getSize().height / 3f * 2));
		label_image.setSize(panel_image.getSize());
		label_image.setLocation(panel_image.getLocation());
		
		
		/*
		 * describe = new JTextArea(); describe.setVisible(true); describe.setSize(200,
		 * 100); describe.setPreferredSize(new Dimension(250, 100));
		 * panel_main.add(describe, BorderLayout.CENTER);
		 */
	}

	@Override
	public void update() {
		if(change) {
			if (image != null) {
				var width = panel_image.getSize().width / image.getIconWidth();
				var height = panel_image.getSize().height / image.getIconHeight();
				float dx = Math.min(width, height);
				width = (int) (image.getIconWidth() * dx);
				height = (int) (image.getIconHeight() * dx);
				label_image.setIcon(new ImageIcon(image.getImage().getScaledInstance(width, height, java.awt.Image.SCALE_DEFAULT)));
				change = false;
			}
		}
	}

	@Override
	public void paint(Graphics g) {
		
	}
}
