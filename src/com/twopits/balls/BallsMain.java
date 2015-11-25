package com.twopits.balls;

import com.twopits.balls.libs.Utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * Main class of game "Balls"
 * Created by hiking on 2015/10/26.
 */
public class BallsMain extends JPanel {

	// App configurations
	private static final String APP_NAME = "Balls";
	private static final int PLAYER_SPEED = 150;
	private static final float FPS_CAP = 60f;
	private static final float MAX_VISIBLE_BLOCKS_IN_HEIGHT = 3.0f;
	private static final float MAX_VISIBLE_BLOCKS_IN_WIDTH = 5.0f;

	private static final float WALL_THICKNESS = 5f;
	private static final float PLAYER_SIZE = 12f;
	private static final float DOOR_WIDTH = 33f;

	private static BallsKeyManager mKeyManager;
	private static Font mGameFont;

	public static void main(String[] args) {
		initValues();

		JFrame frame = new JFrame(APP_NAME);
		frame.getContentPane().setPreferredSize(new Dimension(500, 300));
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.pack();
		Utils.setWindowsToCenter(frame);
		Utils.enableOsxFullscreen(frame, APP_NAME);

		BallsMain game = new BallsMain();
		frame.add(game);
		frame.setVisible(true);
		mKeyManager = new BallsKeyManager();
		frame.addKeyListener(mKeyManager);

		long lastFrameTime = System.currentTimeMillis();
		// noinspection InfiniteLoopStatement
		while (true) {
			long currentTime = System.currentTimeMillis();
			long dt = currentTime - lastFrameTime;
			lastFrameTime = currentTime;
			if (dt > 0) {
				game.update(dt);
			}
		}
	}

	private static void initValues() {
		try {
			mGameFont = Font.createFont(Font.TRUETYPE_FONT, new File("res/supercell_magic.ttf"));
		} catch (Exception e) {
			e.printStackTrace();
			mGameFont = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
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

	private static final int MAP_WIDTH = 10, MAP_HEIGHT = 10;
	private static final int BLOCK_SIZE = 100;

	// Initial player at the center of (0,0)
	private double mPlayerX = BLOCK_SIZE / 2, mPlayerY = BLOCK_SIZE / 2;

	private enum BasicBlock {
		DARK, LIGHT
	}

	private static Image[] mBlockImages;

	public BallsMain() {
		mBlockImages = initBlockResources();
	}

	private Image[] initBlockResources() {
		Image[] images = new Image[BasicBlock.values().length];
		images[BasicBlock.DARK.ordinal()] =
				Toolkit.getDefaultToolkit().getImage("res/room_dark.png");
		images[BasicBlock.LIGHT.ordinal()] =
				Toolkit.getDefaultToolkit().getImage("res/room_light.png");
		return images;
	}

	private float getZoomFactor() {
		if (getWidth() / (float) getHeight() >
				MAX_VISIBLE_BLOCKS_IN_WIDTH / MAX_VISIBLE_BLOCKS_IN_HEIGHT) {
			return getWidth() / (MAX_VISIBLE_BLOCKS_IN_WIDTH * BLOCK_SIZE);
		} else {
			return getHeight() / (MAX_VISIBLE_BLOCKS_IN_HEIGHT * BLOCK_SIZE);
		}
	}

	// For the update cycle
	private float mSmoothedDt = 1000 / FPS_CAP;
	private long mSleepDuration;

	private void update(long dt) {
		updatePlayerPosition(dt);
		repaint();

		mSmoothedDt = (mSmoothedDt * 9f + dt - mSleepDuration) / 10f;
		mSleepDuration = (long) (1000 / FPS_CAP - mSmoothedDt);
		if (mSleepDuration > 0l) {
			// Cap the screen rate
			try {
				Thread.sleep(mSleepDuration);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			mSleepDuration = 0;
		}
	}

	private void updatePlayerPosition(long dt) {
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

		double dx = playerWayX * PLAYER_SPEED * dt / 1000.0;
		double dy = playerWayY * PLAYER_SPEED * dt / 1000.0;
		movePlayer(dx, dy);

		// Move player
		if (dx != 0 || dy != 0) {
			if (mPlayerX < 0 || mPlayerX > MAP_WIDTH * BLOCK_SIZE) {
				mPlayerX = Utils.floorMod(mPlayerX, MAP_WIDTH * BLOCK_SIZE);
			}
			if (mPlayerY < 0 || mPlayerY > MAP_HEIGHT * BLOCK_SIZE) {
				mPlayerY = Utils.floorMod(mPlayerY, MAP_HEIGHT * BLOCK_SIZE);
			}
		}
	}

	private void movePlayer(double dx, double dy) {
		double playerOffsetX = Utils.floorMod(mPlayerX + dx, BLOCK_SIZE);
		double playerOffsetY = Utils.floorMod(mPlayerY + dy, BLOCK_SIZE);
		float playerRadius = PLAYER_SIZE / 2f;

		boolean validXLeft = playerOffsetX - playerRadius - WALL_THICKNESS > 0;
		boolean validXRight = playerOffsetX + playerRadius + WALL_THICKNESS < BLOCK_SIZE;
		boolean validYTop = playerOffsetY - playerRadius - WALL_THICKNESS > 0;
		boolean validYBottom = playerOffsetY + playerRadius + WALL_THICKNESS < BLOCK_SIZE;

		// Check if player isn't near a door
		boolean isInsideRoom = validXLeft && validXRight && validYTop && validYBottom;
		if (isInsideRoom) {
			mPlayerX += dx;
			mPlayerY += dy;
			return;
		}

		// Check door
		double doorSideWallWidth = (BLOCK_SIZE - DOOR_WIDTH) / 2.0;
		boolean insideXDoorRange = playerOffsetX - playerRadius > doorSideWallWidth &&
				playerOffsetX + playerRadius < BLOCK_SIZE - doorSideWallWidth;
		boolean insideYDoorRange = playerOffsetY - playerRadius > doorSideWallWidth &&
				playerOffsetY + playerRadius < BLOCK_SIZE - doorSideWallWidth;
		if ((!validXLeft || !validXRight) && insideYDoorRange) {
			mPlayerX += dx;
			mPlayerY += dy;
		}
		if ((!validYTop || !validYBottom) && insideXDoorRange) {
			mPlayerX += dx;
			mPlayerY += dy;
		}
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		Graphics2D g2d = (Graphics2D) g;
		float zoom = getZoomFactor();
		g2d.setFont(mGameFont.deriveFont(10 * zoom));

		drawMap(g2d, zoom);
	}

	@SuppressWarnings("SuspiciousNameCombination")
	private void drawMap(Graphics2D g2d, float zoom) {
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Map
		int screenPositionX = (int) (mPlayerX - this.getWidth() / zoom / 2.0);
		int screenPositionY = (int) (mPlayerY - this.getHeight() / zoom / 2.0);
		int screenOffsetX = Math.floorMod(screenPositionX, BLOCK_SIZE);
		int screenOffsetY = Math.floorMod(screenPositionY, BLOCK_SIZE);
		int visibleBlockX = Math.floorDiv(screenPositionX, BLOCK_SIZE);
		int visibleBlockY = Math.floorDiv(screenPositionY, BLOCK_SIZE);
		int visibleBlockW = (int) (this.getWidth() / zoom) / BLOCK_SIZE + 2;
		int visibleBlockH = (int) (this.getHeight() / zoom) / BLOCK_SIZE + 2;

		for (int screenBlockX = 0; screenBlockX < visibleBlockW; screenBlockX++) {
			for (int screenBlockY = 0; screenBlockY < visibleBlockH; screenBlockY++) {
				int mapBlockX = screenBlockX + visibleBlockX;
				int mapBlockY = screenBlockY + visibleBlockY;
				BasicBlock block =
						isUserInBlock(mapBlockX, mapBlockY) ? BasicBlock.LIGHT : BasicBlock.DARK;

				int drawPositionX = (int) ((screenBlockX * BLOCK_SIZE - screenOffsetX) * zoom);
				int drawPositionY = (int) ((screenBlockY * BLOCK_SIZE - screenOffsetY) * zoom);
				int drawBlockSize = (int) Math.ceil(BLOCK_SIZE * zoom);

				g2d.drawImage(mBlockImages[block.ordinal()], drawPositionX, drawPositionY,
						drawBlockSize, drawBlockSize, null);
				// Draw block info
				g2d.setColor(new Color(0xff35160a));
				g2d.drawString(String.format("(%d,%d)", mapBlockX, mapBlockY),
						drawPositionX + (10 * zoom), drawPositionY + (20 * zoom));
			}
		}

		// TODO Draw players
		int fakePlayerRadius = (int) (8 * zoom);
		g2d.setColor(Color.LIGHT_GRAY);
		g2d.fillOval(this.getWidth() / 2 - fakePlayerRadius,
				this.getHeight() / 2 - fakePlayerRadius, 2 * fakePlayerRadius,
				2 * fakePlayerRadius);
		g2d.setColor(Color.BLACK);
		g2d.setStroke(new BasicStroke(2 * zoom));
		g2d.drawOval(this.getWidth() / 2 - fakePlayerRadius,
				this.getHeight() / 2 - fakePlayerRadius, 2 * fakePlayerRadius,
				2 * fakePlayerRadius);

		boolean DEBUG = true;
		boolean DEBUG_PLAYER = true;
		// noinspection ConstantConditions
		if (DEBUG) {
			// Debug messages
			int drawTextPositionX = (int) (10 * zoom);
			int drawTextPositionY = this.getHeight() - (int) (10 * zoom);
			int lineHeight = (int) (20 * zoom);

			g2d.setColor(Color.WHITE);
			g2d.drawString(String.format("FPS: %.0f (%d)", 1000 / (mSmoothedDt + mSleepDuration),
					mSleepDuration), drawTextPositionX, drawTextPositionY);

			// noinspection ConstantConditions
			if (DEBUG_PLAYER) {
				drawTextPositionY -= lineHeight;
				g2d.drawString(String.format("Player offset: (%d,%d)", (int) mPlayerX % BLOCK_SIZE,
						(int) mPlayerY % BLOCK_SIZE), drawTextPositionX, drawTextPositionY);

				drawTextPositionY -= lineHeight;
				g2d.drawString(String.format("Player block: (%d,%d)",
								(int) mPlayerX / BLOCK_SIZE % MAP_WIDTH,
								(int) mPlayerY / BLOCK_SIZE % MAP_HEIGHT), drawTextPositionX,
						drawTextPositionY);
			} else {
				drawTextPositionY -= lineHeight;
				g2d.drawString(
						String.format("Screen offset: (%d,%d)", screenOffsetX, screenOffsetY),
						drawTextPositionX, drawTextPositionY);

				drawTextPositionY -= lineHeight;
				g2d.drawString(String.format("Screen block: (%d,%d)", visibleBlockX, visibleBlockY),
						drawTextPositionX, drawTextPositionY);
			}

			drawTextPositionY -= lineHeight;
			g2d.drawString(
					String.format("Player position: (%d,%d)", (int) mPlayerX, (int) mPlayerY),
					drawTextPositionX, drawTextPositionY);
		}
	}

	private boolean isUserInBlock(int mapBlockX, int mapBlockY) {
		int playerBlockX = Math.floorDiv((int) mPlayerX, BLOCK_SIZE);
		int playerBlockY = Math.floorDiv((int) mPlayerY, BLOCK_SIZE);
		return playerBlockX == mapBlockX && playerBlockY == mapBlockY;
	}
}
