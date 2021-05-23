package core;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferStrategy;
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

public class Starter extends JFrame implements Runnable {
	private static final long serialVersionUID = 1315572143966073521L;
	
	public static Starter starter;
	public Panel_Left_Side panel_side;
	public Panel_Main panel_main;
	public Timer timer;
	public Image image_buffer;

	public void init() {
		FlatDarkLaf.setup();
		timer = new Timer();
		setIgnoreRepaint(true);

		setTitle("dd");
		setSize(1920 / 2, 1080 / 2);
		setVisible(true);
		setResizable(false);
		setLayout(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		image_buffer = createImage(this.getWidth(), this.getHeight());
		setup();
	}

	public void setup() {
		panel_side = new Panel_Left_Side();
		panel_main = new Panel_Main();
		panel_side.init(this);
		panel_main.init(this);

		var mouse_handler = new MouseHandler();
		var file_handler = new FileDropHandler();
		addMouseListener(mouse_handler);
		setTransferHandler(file_handler);
	}

	// 60fps
	public final int maxUps = 60;
	public final int maxFps = 60;

	public void update(float interval) {
		panel_side.update();
		panel_main.update();
	}

	public void loop() throws InterruptedException {
		float elapsedTime;
		float accumulator = 0f;
		float interval = 1f / maxUps;

		while (true) {
			elapsedTime = timer.getElapsedTime();
			accumulator += elapsedTime;

			//input();

			while (accumulator >= interval) {
				update(interval);
				accumulator -= interval;
			}

			render();

			//if (!window.isVSync()) {
				sync();
			//}
		}

	}

	private void sync() {
		float loopSlot = 1f / maxFps;
		double endTime = timer.getLastLoopTime() + loopSlot;
		while (timer.getTime() < endTime) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException ie) {
			}
		}
	}

	public void update(File file) {
		panel_main.image = new ImageIcon(file.getAbsolutePath());
		panel_main.change = true;
	}

	public void render() {
		var g = image_buffer.getGraphics();
		
		this.paint(g);
		panel_main.paint(g, this.getInsets().top);
		panel_side.paint(g, this.getInsets().top);
		
		this.getGraphics().drawImage(image_buffer, 0, 0, null);
		g.dispose();
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
				if (file.getName().contains(".png") || file.getName().contains(".jpg")) {
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
			loop();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
