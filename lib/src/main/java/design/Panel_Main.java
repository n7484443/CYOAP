package design;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.MatteBorder;


public class Panel_Main extends JPanel implements Panel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7976300570497450406L;
	public JPanel panel_image;
	public JPanel panel_text;
	
	public JScrollPane scrollpane_describe;
	public JTextArea textArea_describe;
	public JPanel mother_panel;
	public ImageIcon image;
	public Image image_size = null;
	public boolean change = false;

	public void init(JPanel mother_panel) {
		this.mother_panel = mother_panel;
		GridBagLayout layout = new GridBagLayout();
		this.setLayout(layout);
		
		panel_image = new JPanel();
		panel_image.setBorder(new MatteBorder(0, 0, 1, 0, Color.BLACK));
		panel_text = new JPanel();
		
		textArea_describe = new JTextArea(10, 50);
		scrollpane_describe = new JScrollPane(textArea_describe, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollpane_describe.setBorder(new MatteBorder(1, 1, 1, 1, Color.BLACK));
		panel_text.add(scrollpane_describe, BorderLayout.PAGE_END);
		
		var gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 5;
		gbc.weighty = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.BOTH;

		var gbc_image = new GridBagConstraints();
		gbc_image.gridx = 0;
		gbc_image.gridy = 0;
		gbc_image.weightx = 1;
		gbc_image.weighty = 2;
		gbc_image.gridwidth = 1;
		gbc_image.gridheight = 2;
		gbc_image.fill = GridBagConstraints.BOTH;
		
		var gbc_text = new GridBagConstraints();
		gbc_text.gridx = 0;
		gbc_text.gridy = 1;
		gbc_text.weightx = 1;
		gbc_text.weighty = 1;
		gbc_text.gridwidth = 1;
		gbc_text.gridheight = 1;
		gbc_text.fill = GridBagConstraints.BOTH;
		
		this.add(panel_image, gbc_image);
		this.add(panel_text, gbc_text);
		
		mother_panel.add(this, gbc);

	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (image != null) {
			float width = panel_image.getWidth() / (float)image.getIconWidth();
			float height = panel_image.getHeight() / (float)image.getIconHeight();
			float r = width < height ? width : height;
			int width_int = (int) (r * image.getIconWidth());
			int height_int = (int) (r * image.getIconHeight());
			
			var insets = mother_panel.getParent().getInsets();
			g.drawImage(image.getImage(), panel_image.getParent().getLocation().x + insets.left, panel_image.getParent().getLocation().y + insets.top, width_int, height_int, null);
		}
	}
	
	@Override
	public void update() {
		if (change) {
			//textArea_describe.append("dd\n");
			if (image != null) {
				change = false;
			}
		}
	}
	
	

	@Override
	public void paint(Graphics g, Insets insets) {
		if (image != null) {
			float width = panel_image.getWidth() / (float)image.getIconWidth();
			float height = panel_image.getHeight() / (float)image.getIconHeight();
			float r = width < height ? width : height;
			int width_int = (int) (r * image.getIconWidth());
			int height_int = (int) (r * image.getIconHeight());
			
			g.drawImage(image.getImage(), panel_image.getParent().getLocation().x + insets.left, panel_image.getParent().getLocation().y + insets.top, width_int, height_int, null);
		}
	}
}
