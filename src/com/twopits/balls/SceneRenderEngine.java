package com.twopits.balls;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JPanel;

/**
 * Screen & world map rendering
 * Created by hiking on 2015/10/26.
 */
public class SceneRenderEngine extends JPanel {

	// Scene configurations
	private static final int PLAYER_SPEED = 150;
	private static final float MAX_VISIBLE_BLOCKS_IN_HEIGHT = 3.0f;
	private static final float MAX_VISIBLE_BLOCKS_IN_WIDTH = 5.0f;

	private static final float WALL_THICKNESS = 5f;
	private static final float PLAYER_SIZE = 12f;
	private static final float DOOR_WIDTH = 33f;

	private static final int MAP_WIDTH = 10, MAP_HEIGHT = 10;
	private static final int BLOCK_SIZE = 100;

	// Initial player at the center of (0,0)
	private double mPlayerX = BLOCK_SIZE / 2, mPlayerY = BLOCK_SIZE / 2;

	private enum BasicBlock {
		DARK, LIGHT
	}

	private App mApp;
	private Font mGameFont;
	private Image[] mBlockImages;

	public SceneRenderEngine(App app) {
		mApp = app;
		initValues();
	}

	private void initValues() {
		mBlockImages = initBlockResources();

		try {
			mGameFont = Font.createFont(Font.TRUETYPE_FONT, new File("res/supercell_magic.ttf"));
		} catch (Exception e) {
			e.printStackTrace();
			mGameFont = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
		}
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

	public void update(long dt) {
		updatePlayerPosition(dt);
		repaint();
	}

	private void updatePlayerPosition(long dt) {
		double playerWayX = 0, playerWayY = 0;
		KeyManager keyManager = mApp.getKeyManager();

		if (keyManager.isKeyPressed(KeyEvent.VK_LEFT)) {
			playerWayX -= 1.0;
		}
		if (keyManager.isKeyPressed(KeyEvent.VK_RIGHT)) {
			playerWayX += 1.0;
		}
		if (keyManager.isKeyPressed(KeyEvent.VK_UP)) {
			playerWayY -= 1.0;
		}
		if (keyManager.isKeyPressed(KeyEvent.VK_DOWN)) {
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
		double playerOffsetXBefore = Utils.floorMod(mPlayerX, BLOCK_SIZE);
		double playerOffsetYBefore = Utils.floorMod(mPlayerY, BLOCK_SIZE);
		double playerOffsetXAfter = Utils.floorMod(mPlayerX + dx, BLOCK_SIZE);
		double playerOffsetYAfter = Utils.floorMod(mPlayerY + dy, BLOCK_SIZE);
		float playerRadius = PLAYER_SIZE / 2f;

		boolean validLeftBefore = playerOffsetXBefore - playerRadius - WALL_THICKNESS > 0;
		boolean validRightBefore = playerOffsetXBefore + playerRadius + WALL_THICKNESS < BLOCK_SIZE;
		boolean validTopBefore = playerOffsetYBefore - playerRadius - WALL_THICKNESS > 0;
		boolean validBottomBefore =
				playerOffsetYBefore + playerRadius + WALL_THICKNESS < BLOCK_SIZE;
		boolean validLeftAfter = playerOffsetXAfter - playerRadius - WALL_THICKNESS > 0;
		boolean validRightAfter = playerOffsetXAfter + playerRadius + WALL_THICKNESS < BLOCK_SIZE;
		boolean validTopAfter = playerOffsetYAfter - playerRadius - WALL_THICKNESS > 0;
		boolean validBottomAfter = playerOffsetYAfter + playerRadius + WALL_THICKNESS < BLOCK_SIZE;

		// Check if player isn't near a door
		boolean isInsideRoom =
				validLeftAfter && validRightAfter && validTopAfter && validBottomAfter;
		if (isInsideRoom) {
			mPlayerX += dx;
			mPlayerY += dy;
			return;
		}

		// Check door
		double doorSideWallWidth = (BLOCK_SIZE - DOOR_WIDTH) / 2.0;
		boolean insideXDoorRangeBefore = playerOffsetXBefore - playerRadius > doorSideWallWidth &&
				playerOffsetXBefore + playerRadius < BLOCK_SIZE - doorSideWallWidth;
		boolean insideYDoorRangeBefore = playerOffsetYBefore - playerRadius > doorSideWallWidth &&
				playerOffsetYBefore + playerRadius < BLOCK_SIZE - doorSideWallWidth;

		if ((!validLeftAfter || !validRightAfter) && !insideYDoorRangeBefore) {
			dx = 0;
		}
		if ((!validLeftBefore || !validRightBefore) &&
				(playerOffsetYAfter - playerRadius < doorSideWallWidth ||
						playerOffsetYAfter + playerRadius > BLOCK_SIZE - doorSideWallWidth)) {
			dy = 0;
		}

		if ((!validTopAfter || !validBottomAfter) && !insideXDoorRangeBefore) {
			dy = 0;
		}
		if ((!validTopBefore || !validBottomBefore) &&
				(playerOffsetXAfter - playerRadius < doorSideWallWidth ||
						playerOffsetXAfter + playerRadius > BLOCK_SIZE - doorSideWallWidth)) {
			dx = 0;
		}

		mPlayerX += dx;
		mPlayerY += dy;
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
				g2d.drawString(String.format("(%d,%d)", Math.floorMod(mapBlockX, MAP_WIDTH),
						Math.floorMod(mapBlockY, MAP_HEIGHT)), drawPositionX + (10 * zoom),
						drawPositionY + (20 * zoom));
			}
		}

		// TODO Draw players
		int fakePlayerRadius = (int) (PLAYER_SIZE / 2 * zoom);
		g2d.setColor(Color.LIGHT_GRAY);
		g2d.fillRect(this.getWidth() / 2 - fakePlayerRadius,
				this.getHeight() / 2 - fakePlayerRadius, 2 * fakePlayerRadius,
				2 * fakePlayerRadius);
		g2d.setColor(Color.BLACK);
		g2d.setStroke(new BasicStroke(2 * zoom));
		g2d.drawRect(this.getWidth() / 2 - fakePlayerRadius,
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

			RenderThread renderThread = mApp.getRenderThread();
			if (renderThread != null) {
				g2d.drawString(String.format("FPS: %.0f (%d)", renderThread.getCurrentFPS(),
						renderThread.getCurrentSleepDuration()), drawTextPositionX,
						drawTextPositionY);
			}

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
