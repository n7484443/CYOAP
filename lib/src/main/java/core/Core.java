package core;

import javax.swing.SwingUtilities;

public class Core implements Runnable{
	public static MainFrame mainFrame;
	

	public Timer timer;
	public Core() {
		mainFrame = new MainFrame();
		init();
		try {
			loop();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void init() {
		timer = new Timer();
		mainFrame.init();
	}

	// 60fps
	public final int maxUps = 60;
	public final int maxFps = 60;
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
	
	public void update(float interval) {
		mainFrame.update(interval);
	}
	
	public void render() {
		mainFrame.render();
	}

	private void sync() {
		float loopSlot = 1f / maxFps;
		double endTime = timer.getLastLoopTime() + loopSlot;
		while (timer.getTime() < endTime) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		
	}
	
}
