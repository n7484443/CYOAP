package core;

import java.awt.Graphics;
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
import javax.swing.TransferHandler;

import com.formdev.flatlaf.FlatDarkLaf;

import design.Panel_Left_Side;
import design.Panel_Main;

public class Starter extends JFrame implements Runnable{
	private static final long serialVersionUID = 6729357859414168146L;
	public static Starter starter;
	public Panel_Left_Side panel_side;
	public Panel_Main panel_main;

	public void init() {
		FlatDarkLaf.setup();
		
		this.setTitle("dd");
		this.setSize(1920 / 2, 1080 / 2);
		this.setVisible(true);
		this.setResizable(false);
		this.setLayout(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setup();
	}

	public void setup() {
		panel_side = new Panel_Left_Side();
		panel_main = new Panel_Main();
		panel_side.init(this);
		panel_main.init(this);
		
		var mouse_handler = new MouseHandler();
		var file_handler = new FileDropHandler();
		this.addMouseListener(mouse_handler);
		this.setTransferHandler(file_handler);	
	}
	//60fps 
	public final int maxUps = 60;
	public final int ups = 1000/maxUps;
	public void update() throws InterruptedException {
		int frame = 0;
		while(true) {
			long time = System.currentTimeMillis();
			panel_side.update();
			panel_main.update();
			time -= System.currentTimeMillis();
			Thread.sleep(ups - time);
			
			frame++;
			if(frame > maxUps)frame = 0;
		}
	}
	
	public void update(File file) {
		panel_main.image = new ImageIcon(file.getAbsolutePath());
		panel_main.change = true;
	}
	
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		panel_main.paint(g);
		panel_side.paint(g);
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
					update(file);
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
		System.out.println("Version|" + Core.version);
		Starter.starter = this;
		init();
		try {
			update();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
