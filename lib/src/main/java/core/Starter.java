package core;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseAdapter;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.TransferHandler;

import com.formdev.flatlaf.FlatLightLaf;

public class Starter extends JFrame implements Runnable{
	private static final long serialVersionUID = 6729357859414168146L;
	public static Starter starter;
	public JPanel panel_side_variable;
	public JPanel panel_main;
	public JTextArea describe;

	public void init() {
		FlatLightLaf.setup();
		
		this.setTitle("");
		this.setSize(1920 / 2, 1080 / 2);
		this.setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setup();
	}

	public void setup() {
		panel_main = new JPanel();
		panel_side_variable = new JPanel();
		this.add(panel_main, BorderLayout.EAST);
		this.add(panel_side_variable);

		var but = new JButton();
		but.setLocation(50, 50);
		but.setVisible(true);
		but.setSize(100, 50);
		but.setText("Next");
		panel_main.add(but);
		panel_main.setVisible(true);
		panel_main.setSize(200, 100);

		describe = new JTextArea();
		describe.setVisible(true);
		describe.setSize(200, 100);
		describe.setMaximumSize(new Dimension(250, 100));
		panel_main.add(describe, BorderLayout.CENTER);

		var mouse_handler = new MouseHandler();
		var file_handler = new FileDropHandler();
		this.addMouseListener(mouse_handler);
		this.setTransferHandler(file_handler);
		
	}
	
	public Map<String, File> image_file = new HashMap<String, File>();
	
	public class FileDropHandler extends TransferHandler {
		private static final long serialVersionUID = -7069254235375785714L;

		@Override
		public boolean canImport(TransferSupport support) {
			for (var flavor : support.getDataFlavors()) {
				if (flavor.isFlavorJavaFileListType()) {
					return true;
				}
			}
			return false;
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean importData(TransferSupport support) {
			if (!this.canImport(support))
				return false;

			List<File> files;
			try {
				files = (List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
			} catch (UnsupportedFlavorException | IOException e) {
				e.printStackTrace();
				return false;
			}

			for (var file : files) {
				if (file.getName().contains(".png")) {
					image_file.put(file.getName(), file);
				} else if (file.getName().contains(".data")) {
					System.out.println(file.getName());
				}
			}

			return true;
		}
	}

	public class MouseHandler extends MouseAdapter {
	}

	@Override
	public void run() {
		Starter.starter = this;
		init();
	}
}
