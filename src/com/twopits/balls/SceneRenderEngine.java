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
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * Scene render engine
 * Created by hiking on 2015/10/26.
 */
public class SceneRenderEngine extends JPanel {

	// App configurations
	private static final String APP_NAME = "Balls";
	private static final int PLAYER_SPEED = 300;
	private static final float MAX_VISIBLE_BLOCKS_IN_HEIGHT = 3.0f;
	private static final float MAX_VISIBLE_BLOCKS_IN_WIDTH = 5.0f;

	private static RenderThread mRenderThread;
	private static BallsKeyManager mKeyManager;
	private static Font mGameFont;

	public static void main(String[] args) {
		SceneRenderEngine game = initRenderEngine();

		mRenderThread = new RenderThread(game);
		mRenderThread.startRenderThread();
	}

	public static SceneRenderEngine initRenderEngine() {
		initValues();

		JFrame frame = new JFrame(APP_NAME);
		frame.getContentPane().setPreferredSize(new Dimension(500, 300));
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.pack();
		Utils.setWindowsToCenter(frame);
		Utils.enableOsxFullscreen(frame, APP_NAME);

		SceneRenderEngine game = new SceneRenderEngine();
		frame.add(game);
		frame.setVisible(true);
		mKeyManager = new BallsKeyManager();
		frame.addKeyListener(mKeyManager);

		return game;
	}

	private static void initValues() {
		try {
			mGameFont = Font.createFont(Font.TRUETYPE_FONT, new File("res/supercell_magic.ttf"));
		} catch (Exception e) {
			e.printStackTrace();
			mGameFont = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
		}
	}

	public Position getVirtualCharacterXY() {
		return new Position(mPlayerX, mPlayerY);
	}

	public void setVirtualCharacterXY(Position position) {
		setVirtualCharacterXY(position.x, position.y);
	}

	public void setVirtualCharacterXY(double x, double y) {
		mPlayerX = x;
		mPlayerY = y;

		if (mPlayerX < 0 || mPlayerX > MAP_WIDTH * BLOCK_SIZE) {
			mPlayerX = Utils.floorMod(mPlayerX, MAP_WIDTH * BLOCK_SIZE);
		}
		if (mPlayerY < 0 || mPlayerY > MAP_HEIGHT * BLOCK_SIZE) {
			mPlayerY = Utils.floorMod(mPlayerY, MAP_HEIGHT * BLOCK_SIZE);
		}
	}

	static class BallsKeyManager implements KeyListener {

		private List<Integer> mPressedKeys;

		public BallsKeyManager() {
			mPressedKeys = new ArrayList<>();
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

	public static final int MAP_WIDTH = 50, MAP_HEIGHT = 20;
	private double mPlayerX = 2500, mPlayerY = 1000;

	public static final int BLOCK_SIZE = 100;

	public enum BasicBlock {
		GRASS, ROCK, DIRT, WATER, LAVA;
	}

	private static Image[] mBlockImages;

	private static BasicBlock[][] mBlocks;

	public SceneRenderEngine() {
		loadMap(getRandomMap());
		mBlockImages = initBlockResources();
	}

	public void loadMap(BasicBlock[][] map) {
		if (map.length != MAP_WIDTH || map[0].length != MAP_HEIGHT) {
			throw new InvalidParameterException("Map should be " + MAP_WIDTH + " * " + MAP_HEIGHT);
		}
		for (int row = 0; row < map.length; row++) {
			BasicBlock[] blockRow = map[row];
			for (int col = 0; col < blockRow.length; col++) {
				BasicBlock block = blockRow[col];
				if (block == null) {
					throw new InvalidParameterException("Block at (" + row + ", " +
							col + ") shouldn't be null.");
				}
			}
		}

		mBlocks = map;
	}

	private BasicBlock[][] getRandomMap() {
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

	private Image[] initBlockResources() {
		Image[] images = new Image[BasicBlock.values().length];
		images[BasicBlock.GRASS.ordinal()] = Toolkit.getDefaultToolkit().getImage("res/grass.png");
		images[BasicBlock.ROCK.ordinal()] = Toolkit.getDefaultToolkit().getImage("res/rock.png");
		images[BasicBlock.DIRT.ordinal()] = Toolkit.getDefaultToolkit().getImage("res/dirt.png");
		images[BasicBlock.WATER.ordinal()] = Toolkit.getDefaultToolkit().getImage("res/water.png");
		images[BasicBlock.LAVA.ordinal()] = Toolkit.getDefaultToolkit().getImage("res/lava.png");
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

	public void update(long dt) {
		updatePlayerPosition(dt);
		repaint();
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

		double newPlayerX = mPlayerX + playerWayX * PLAYER_SPEED * dt / 1000.0;
		double newPlayerY = mPlayerY + playerWayY * PLAYER_SPEED * dt / 1000.0;
		setVirtualCharacterXY(newPlayerX, newPlayerY);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		renderScene((Graphics2D) g);
	}

	@SuppressWarnings("SuspiciousNameCombination")
	private void renderScene(Graphics2D g2d) {
		Position playerPosition = getVirtualCharacterXY();
		float zoom = getZoomFactor();

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setFont(mGameFont.deriveFont(10 * zoom));

		// Map
		int screenPositionX = (int) (playerPosition.x - this.getWidth() / zoom / 2.0);
		int screenPositionY = (int) (playerPosition.y - this.getHeight() / zoom / 2.0);
		int screenOffsetX = Math.floorMod(screenPositionX, BLOCK_SIZE);
		int screenOffsetY = Math.floorMod(screenPositionY, BLOCK_SIZE);
		int visibleBlockX = Math.floorDiv(screenPositionX, BLOCK_SIZE);
		int visibleBlockY = Math.floorDiv(screenPositionY, BLOCK_SIZE);
		int visibleBlockW = (int) (this.getWidth() / zoom) / BLOCK_SIZE + 2;
		int visibleBlockH = (int) (this.getHeight() / zoom) / BLOCK_SIZE + 2;

		g2d.setStroke(new BasicStroke(2 * zoom));
		for (int row = 0; row < visibleBlockW; row++) {
			for (int col = 0; col < visibleBlockH; col++) {
				int drawingBlockX = Math.floorMod(row + visibleBlockX, MAP_WIDTH);
				int drawingBlockY = Math.floorMod(col + visibleBlockY, MAP_HEIGHT);
				BasicBlock block = mBlocks[drawingBlockX][drawingBlockY];

				int drawPositionX = (int) ((row * BLOCK_SIZE - screenOffsetX) * zoom);
				int drawPositionY = (int) ((col * BLOCK_SIZE - screenOffsetY) * zoom);
				int drawBlockSize = (int) Math.ceil(BLOCK_SIZE * zoom);

				g2d.drawImage(mBlockImages[block.ordinal()], drawPositionX, drawPositionY,
						drawBlockSize, drawBlockSize, null);
				g2d.setColor(Color.BLACK);
				g2d.drawRect(drawPositionX, drawPositionY, drawBlockSize, drawBlockSize);
				g2d.drawString(String.format("(%d,%d)", drawingBlockX, drawingBlockY),
						drawPositionX + (10 * zoom), drawPositionY + (20 * zoom));
			}
		}

		// TODO Draw players
		int fakePlayerRadius = (int) (4 * zoom);
		g2d.setColor(Color.LIGHT_GRAY);
		g2d.fillOval(this.getWidth() / 2 - fakePlayerRadius,
				this.getHeight() / 2 - fakePlayerRadius, 2 * fakePlayerRadius,
				2 * fakePlayerRadius);
		g2d.setColor(Color.BLACK);
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
			if (mRenderThread != null) {
				g2d.drawString(String.format("FPS: %.0f (%d)", mRenderThread.getCurrentFPS(),
								mRenderThread.getCurrentSleepDuration()), drawTextPositionX,
						drawTextPositionY);
			}

			// noinspection ConstantConditions
			if (DEBUG_PLAYER) {
				drawTextPositionY -= lineHeight;
				g2d.drawString(
						String.format("Player offset: (%d,%d)", (int) playerPosition.x % BLOCK_SIZE,
								(int) playerPosition.y % BLOCK_SIZE), drawTextPositionX,
						drawTextPositionY);

				drawTextPositionY -= lineHeight;
				g2d.drawString(String.format("Player block: (%d,%d)",
								(int) playerPosition.x / BLOCK_SIZE % MAP_WIDTH,
								(int) playerPosition.y / BLOCK_SIZE % MAP_HEIGHT),
						drawTextPositionX, drawTextPositionY);
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
			g2d.drawString(String.format("Player position: (%d,%d)", (int) playerPosition.x,
					(int) playerPosition.y), drawTextPositionX, drawTextPositionY);
		}
	}

	static class Position {

		public double x, y;

		public Position(double x, double y) {
			this.x = x;
			this.y = y;
		}
	}
}
