package com.twopits.balls;

import com.twopits.balls.libs.Utils;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * Main class of Balls
 * Created by hiking on 2015/10/26.
 */
public class BallsMain extends JPanel {

	private static Dimension mScreenDimen = new Dimension(500, 300);
	private static BallsKeyManager mKeyManager;

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.getContentPane().setPreferredSize(mScreenDimen);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.pack();
		Utils.setWindowsToCenter(frame);

		BallsMain game = new BallsMain();
		frame.add(game);
		frame.setVisible(true);
		mKeyManager = new BallsKeyManager();
		frame.addKeyListener(mKeyManager);

		long lastFrameTime = System.currentTimeMillis();
		while (true) {
			long currentTime = System.currentTimeMillis();
			long dt = currentTime - lastFrameTime;
			lastFrameTime = currentTime;
			if (dt != 0) {
				game.update(dt);
			}
		}
	}

	static class BallsKeyManager implements KeyListener {

		private List<Integer> mPressedKeys;

		public BallsKeyManager() {
			mPressedKeys = new ArrayList<Integer>();
		}

		public boolean isKeyPressed(int keyCode) {
			return mPressedKeys.contains(keyCode);
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void keyPressed(KeyEvent e) {
			if (!mPressedKeys.contains(e.getKeyCode())) {
				mPressedKeys.add(e.getKeyCode());
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			mPressedKeys.remove((Integer) e.getKeyCode());
		}
	}

	private static final int PLAYER_SPEED = 500;
	private double mPlayerX = 0, mPlayerY = 0;

	public BallsMain() {
	}

	private void update(long dt) {
		double playerWayX = 0, playerWayY = 0;

		if (mKeyManager.isKeyPressed(KeyEvent.VK_LEFT)) {
			playerWayX -= 1.0;
		}
		if (mKeyManager.isKeyPressed(KeyEvent.VK_RIGHT)) {
			playerWayX += 1.0;
		}
		if (mKeyManager.isKeyPressed(KeyEvent.VK_UP)) {
			playerWayY -= 1.0;
		}
		if (mKeyManager.isKeyPressed(KeyEvent.VK_DOWN)) {
			playerWayY += 1.0;
		}

		double unitFactor = Math.sqrt(playerWayX * playerWayX + playerWayY * playerWayY);
		if (unitFactor != 0) {
			playerWayX /= unitFactor;
			playerWayY /= unitFactor;
		}

		mPlayerX += playerWayX * PLAYER_SPEED * dt / 1000.0;
		mPlayerY += playerWayY * PLAYER_SPEED * dt / 1000.0;

		if (playerWayX != 0 || playerWayY != 0) {
			// Check out-of-bound
			if (mPlayerX + 30 > mScreenDimen.width) {
				mPlayerX = mScreenDimen.width - 30;
			}
			if (mPlayerX < 0) {
				mPlayerX = 0;
			}
			if (mPlayerY + 30 > mScreenDimen.height) {
				mPlayerY = mScreenDimen.height - 30;
			}
			if (mPlayerY < 0) {
				mPlayerY = 0;
			}

			repaint();
		}
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2d.fillOval((int) mPlayerX, (int) mPlayerY, 30, 30);
		g2d.drawString("Position: " + String.format("(%d,%d)", (int) mPlayerX, (int) mPlayerY), 10,
				270);
		g2d.drawString("Time stamp: " + System.currentTimeMillis(), 10, 290);
	}
}
