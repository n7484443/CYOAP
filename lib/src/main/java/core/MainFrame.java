package core;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseAdapter;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.TransferHandler;
//import com.formdev.flatlaf.FlatDarkLaf;
import design.Panel_Left_Side;
import design.Panel_Main;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = 1315572143966073521L;
	public Panel_Left_Side panel_side;
	public Panel_Main panel_main;
	public JPanel panel_all_contain;
	public Image image_buffer;

	public void init() {
		//FlatDarkLaf.setup();

		setTitle("dd");
		setSize(1920 / 2, 1080 / 2);
		setVisible(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//setIgnoreRepaint(true);
		
		image_buffer = createImage(this.getWidth(), this.getHeight());
		setup();
	}

	public void setup() {
		GridBagLayout layout = new GridBagLayout();
		
		panel_all_contain = new JPanel();
		panel_all_contain.setLayout(layout);
		
		panel_side = new Panel_Left_Side();
		panel_main = new Panel_Main();
		panel_side.init(panel_all_contain);
		panel_main.init(panel_all_contain);
		
		panel_all_contain.setPreferredSize(new Dimension(1920 / 2, 1080 / 2));
		getContentPane().add(panel_all_contain);
		pack();

		var mouse_handler = new MouseHandler();
		var file_handler = new FileDropHandler();
		addMouseListener(mouse_handler);
		setTransferHandler(file_handler);
	}
	
	public void update(float interval) {
		panel_side.update();
		panel_main.update();
	}

	public void update(File file) {
		panel_main.image = new ImageIcon(file.getAbsolutePath());
		panel_main.change = true;
	}

	public void render() {
		
		//var g = this.getGraphics();
		//super.paint(g);
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
				if (canRead(file)) {
					image_file.put(file.getName(), file);
					update(file);
				} else if (file.getName().contains(".data")) {
					System.out.println(file.getName());
				}
			}

			return true;
		}
	}
	
	public boolean canRead(File file) {
		var name = file.getName();
		if(name.contains(".png")) {
			return true;
		}
		if(name.contains(".jpg")) {
			return true;
		}
		//if(name.contains(".webp")) {
		//	return true;
		//}
		return false;
	}

	public class MouseHandler extends MouseAdapter {
	}
}
