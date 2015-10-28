package com.twopits.balls;

import com.twopits.balls.libs.Utils;

import java.awt.Color;
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

	private static final int[] BLOCK_COLORS =
			{0xff43a047, 0xff607d8b, 0xff795548, 0xff006aa6, 0xffff5722};
	private static final int MAP_WIDTH = 50, MAP_HEIGHT = 20;
	private static final int BLOCK_SIZE = 100;

	private enum BasicBlock {
		GRASS, ROCK, MUD, WATER, FIRE
	}

	private static BasicBlock[][] mBlocks = initMap();

	private static BasicBlock[][] initMap() {
		BasicBlock[][] scene = new BasicBlock[MAP_WIDTH][];
		for (int row = 0; row < MAP_WIDTH; row++) {
			scene[row] = new BasicBlock[MAP_HEIGHT];
			for (int col = 0; col < MAP_HEIGHT; col++) {
				scene[row][col] =
						BasicBlock.values()[((int) (Math.random() * BasicBlock.values().length))];
			}
		}
		return scene;
	}

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
			if (mPlayerX < 0 || mPlayerX > MAP_WIDTH * BLOCK_SIZE) {
				mPlayerX = Utils.floorMod(mPlayerX, MAP_WIDTH * BLOCK_SIZE);
			}
			if (mPlayerY < 0 || mPlayerY > MAP_HEIGHT * BLOCK_SIZE) {
				mPlayerY = Utils.floorMod(mPlayerY, MAP_HEIGHT * BLOCK_SIZE);
			}
			repaint();
		}
	}

	@SuppressWarnings("SuspiciousNameCombination")
	@Override
	public void paint(Graphics g) {
		super.paint(g);

		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Map
		int screenLocationX = (int) mPlayerX - mScreenDimen.width / 2;
		int screenLocationY = (int) mPlayerY - mScreenDimen.height / 2;
		int visibleBlockX = Math.floorDiv(screenLocationX, BLOCK_SIZE);
		int visibleBlockY = Math.floorDiv(screenLocationY, BLOCK_SIZE);
		int visibleBlockW = mScreenDimen.width / BLOCK_SIZE + 1;
		int visibleBlockH = mScreenDimen.height / BLOCK_SIZE + 1;
		int paintOffsetX = Math.floorMod(screenLocationX, BLOCK_SIZE);
		int paintOffsetY = Math.floorMod(screenLocationY, BLOCK_SIZE);

		for (int row = 0; row < visibleBlockW; row++) {
			for (int col = 0; col < visibleBlockH; col++) {
				int drawingBlockX = Math.floorMod(row + visibleBlockX, MAP_WIDTH);
				int drawingBlockY = Math.floorMod(col + visibleBlockY, MAP_HEIGHT);
				BasicBlock block = mBlocks[drawingBlockX][drawingBlockY];
				g2d.setColor(new Color(BLOCK_COLORS[block.ordinal()]));
				g2d.fillRect(row * BLOCK_SIZE - paintOffsetX, col * BLOCK_SIZE - paintOffsetY,
						BLOCK_SIZE, BLOCK_SIZE);
				g2d.setColor(Color.BLACK);
				g2d.drawRect(row * BLOCK_SIZE - paintOffsetX, col * BLOCK_SIZE - paintOffsetY,
						BLOCK_SIZE, BLOCK_SIZE);
				g2d.drawString(String.format("(%d,%d)", drawingBlockX, drawingBlockY),
						row * BLOCK_SIZE - paintOffsetX + 10, col * BLOCK_SIZE - paintOffsetY + 20);
			}
		}

		// Debug
		g2d.setColor(Color.BLACK);
		g2d.fillOval(mScreenDimen.width / 2 - 4, mScreenDimen.height / 2 - 4, 8, 8);

		int screenBottom = mScreenDimen.height - 10;
		g2d.drawString("Time stamp: " + System.currentTimeMillis(), 10, screenBottom);
		g2d.drawString(String.format("Position: (%d,%d)", (int) mPlayerX, (int) mPlayerY), 10,
				screenBottom - 20);
		g2d.drawString(String.format("Offset: (%d,%d)", paintOffsetX, paintOffsetY), 10,
				screenBottom - 40);
		g2d.drawString(String.format("Screen block: (%d,%d)", visibleBlockX, visibleBlockY), 10,
				screenBottom - 60);
	}
}
