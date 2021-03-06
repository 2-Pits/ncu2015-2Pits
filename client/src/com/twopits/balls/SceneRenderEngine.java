package com.twopits.balls;

import com.twopits.balls.libs.FakeData;
import com.twopits.balls.libs.Utils;
import com.twopits.balls.models.BallModel;
import com.twopits.balls.models.IntegerPosition;
import dom.DynamicObjectModule;
import sprite.Character;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Screen & world map rendering
 * Created by hiking on 2015/10/26.
 */
public class SceneRenderEngine extends JPanel {

	// Scene configurations
	private static final int PLAYER_SPEED = 150;
	private static final float MAX_VISIBLE_BLOCKS_IN_HEIGHT = 2.5f;
	private static final float MAX_VISIBLE_BLOCKS_IN_WIDTH = 3.5f;

	private static final float WALL_THICKNESS = 5f;
	private static final float PLAYER_SIZE = 12f;
	private static final float DOOR_WIDTH = 33f;

	private static final int MAP_WIDTH = 10, MAP_HEIGHT = 10;
	private static final int BLOCK_SIZE = 100;
	private static final float BALL_RADIUS = BLOCK_SIZE * .08f;

	// Initial player at the 1/4 of (0,0)
	private double mPlayerX = BLOCK_SIZE / 4, mPlayerY = BLOCK_SIZE / 4;

	private static final int BUTTON_SIZE = BLOCK_SIZE / 4;

	private enum BasicBlock {
		DARK, LIGHT
	}

	private App mApp;
	private Font mGameFont;
	private Image[] mBlockImages;
	private Map<Integer, ItemRectangle> mRectangleMap;

	private DynamicObjectModule dom;
	private Character myCharacter;
	private int mDt;
	private Point mGameOverStringPos;

	public SceneRenderEngine(App app) {
		mApp = app;
		initValues();
	}

	/**
	 * Set local player position
	 *
	 * @param x The x coordinate of player
	 * @param y The x coordinate of player
	 */
	public void setPlayerPosition(double x, double y) {
		mPlayerX = x;
		mPlayerY = y;
		modPlayerPosition();
	}

	private void initValues() {
		mBlockImages = initBlockResources();
		mRectangleMap = createRectangles();
		mDt = 0;
		mGameOverStringPos = new Point() ;
		//TODO Remove fake data
		FakeData.initBallsMap(MAP_WIDTH, MAP_HEIGHT);

		try {
			mGameFont = Font.createFont(Font.TRUETYPE_FONT, new File("res/supercell_magic.ttf"));
		} catch (Exception e) {
			e.printStackTrace();
			mGameFont = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
		}

		dom = mApp.getDynamicObjectModule();
		myCharacter = dom.getMyCharacter();
	}

	private Map<Integer, ItemRectangle> createRectangles() {
		String[] names = {"Q", "W", "E", "R"};
		int[] codes = {KeyEvent.VK_Q, KeyEvent.VK_W, KeyEvent.VK_E, KeyEvent.VK_R};

		Map<Integer, ItemRectangle> rectangles = new HashMap<Integer, ItemRectangle>();
		for (int i = 0; i < 4; i++) {
			Rectangle rectangle = new Rectangle();
			rectangles.put(codes[i], new ItemRectangle(rectangle, names[i], i));
		}

		return rectangles;
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
		updateItemRenctangle();
		updatePlayerSprite((int) dt);
		updateWinnerPos((int )dt);
		repaint();
	}

	private void updatePlayerSprite(int dt) {
		myCharacter.update(dt);
		ArrayList<Character> otherCharacters = dom.getOtherCharacter();
		for (Character character : otherCharacters) {
			character.update(dt);
		}
	}

	private void updateItemRenctangle() {
		KeyManager keyManager = mApp.getKeyManager();
		int[] codes = {KeyEvent.VK_Q, KeyEvent.VK_W, KeyEvent.VK_E, KeyEvent.VK_R};

		//  effect of feedback
		for (int code : codes) {
			boolean isPressed = keyManager.isKeyPressed(code);
			mRectangleMap.get(code).setPressed(isPressed);
		}
	}

	private void updatePlayerPosition(long dt) {
		double playerWayX = 0, playerWayY = 0;
		KeyManager keyManager = mApp.getKeyManager();

		if (keyManager.isKeyPressed(KeyEvent.VK_LEFT)) {
			playerWayX -= 1.0;
			myCharacter.setDirection(4);
		}
		if (keyManager.isKeyPressed(KeyEvent.VK_RIGHT)) {
			playerWayX += 1.0;
			myCharacter.setDirection(8);
		}
		if (keyManager.isKeyPressed(KeyEvent.VK_UP)) {
			playerWayY -= 1.0;
			myCharacter.setDirection(12);
		}
		if (keyManager.isKeyPressed(KeyEvent.VK_DOWN)) {
			playerWayY += 1.0;
			myCharacter.setDirection(0);
		}

		double unitFactor = Math.sqrt(playerWayX * playerWayX + playerWayY * playerWayY);
		if (unitFactor != 0) {
			playerWayX /= unitFactor;
			playerWayY /= unitFactor;
		}

		double dx = playerWayX * PLAYER_SPEED * dt / 1000.0;
		double dy = playerWayY * PLAYER_SPEED * dt / 1000.0;
		movePlayer(dx, dy);

		myCharacter.setPosition(mPlayerX, mPlayerY);
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
			boolean isTouchingBall = Math.pow(playerOffsetXAfter - BLOCK_SIZE / 2f, 2) +
					Math.pow(playerOffsetYAfter - BLOCK_SIZE / 2f, 2) <
					Math.pow(BALL_RADIUS + PLAYER_SIZE / 2f, 2f);

			if (isTouchingBall) {
				return;
			}
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

		modPlayerPosition();
	}

	private void modPlayerPosition() {
		if (mPlayerX < 0 || mPlayerX > MAP_WIDTH * BLOCK_SIZE) {
			mPlayerX = Utils.floorMod(mPlayerX, MAP_WIDTH * BLOCK_SIZE);
		}
		if (mPlayerY < 0 || mPlayerY > MAP_HEIGHT * BLOCK_SIZE) {
			mPlayerY = Utils.floorMod(mPlayerY, MAP_HEIGHT * BLOCK_SIZE);
		}
	}

	private void updateWinnerPos(int dt){
		if(!dom.gameOver()){
			mDt += dt;
			mGameOverStringPos.x = getWidth()/4;
			while (mDt >= 10 && mGameOverStringPos.y != getHeight()/2){
					mGameOverStringPos.y++;
					mDt -= 10;
			}
		}
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		Graphics2D g2d = (Graphics2D) g;
		float zoom = getZoomFactor();
		g2d.setFont(mGameFont.deriveFont(5 * zoom));

		drawMap(g2d, zoom);
		drawItemRectangle(g2d, zoom);
		drawWinnerText(g2d,zoom);
	}

	private void drawWinnerText(Graphics2D g2d, float zoom){
		if(!dom.gameOver()){
			int posX = mGameOverStringPos.x;
			int posY = mGameOverStringPos.y;
			int padding = (int) (5 * zoom);

			g2d.setFont(mGameFont.deriveFont(20 * zoom));
			g2d.setColor(new Color(0xffeceff1));
			g2d.drawString(String.format("Winner is %s ",dom.getWinner()), (float) posX - padding ,
					posY);
		}
	}

	private void drawItemRectangle(Graphics2D g2d, float zoom) {

		int rectangleSize = (int) (BUTTON_SIZE * zoom);
		int padding = (int) (5 * zoom);
		int drawRectanglePositionY = this.getHeight() - rectangleSize - padding;
		int ballRadius = (int) (BALL_RADIUS * zoom);

		// Travel the shape map
		for (ItemRectangle item : mRectangleMap.values()) {
			double posX = padding * item.getIndex() + item.getIndex() * rectangleSize +
					padding;
			g2d.setColor(item.getColor());
			g2d.fillRoundRect((int) posX, drawRectanglePositionY, rectangleSize, rectangleSize,
					(int) (5 * zoom), (int) (5 * zoom));

			// Draw balls
			ArrayList<BallModel> qwerBall = dom.getQWERState();
			BallModel ball = qwerBall.get(item.getIndex());
			double ballRectPadding = (rectangleSize - ballRadius * 2) / 2.0;

			if (ball.ballType == BallModel.BallType.NONE) {
				g2d.setStroke(
						new BasicStroke(2f * zoom, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER,
								2f * zoom, new float[]{6f * zoom}, 0f));
			} else {
				g2d.setColor(new Color(ball.getBallColor()));
				g2d.fillOval((int) (posX + ballRectPadding),
						(int) (drawRectanglePositionY + ballRectPadding), 2 * ballRadius,
						2 * ballRadius);
				g2d.setColor(new Color(0x66ffffff, true));
				g2d.fillOval((int) (posX + ballRectPadding + ballRadius * 1.2f),
						(int) (drawRectanglePositionY + ballRectPadding + ballRadius * .4f),
						(int) (ballRadius * .4f), (int) (ballRadius * .4f));
				g2d.setStroke(new BasicStroke(2 * zoom));
			}
			g2d.setColor(Color.BLACK);
			g2d.drawOval((int) (posX + ballRectPadding),
					(int) (drawRectanglePositionY + ballRectPadding), 2 * ballRadius,
					2 * ballRadius);

			// Draw label
			g2d.setFont(mGameFont.deriveFont(8 * zoom));
			g2d.setColor(Color.BLACK);
			g2d.drawString(item.getShapeName(), (float) posX + padding / 2,
					drawRectanglePositionY + rectangleSize - padding / 3);
			g2d.setColor(new Color(0xffeceff1));
			g2d.drawString(item.getShapeName(), (float) posX + padding / 2,
					drawRectanglePositionY + rectangleSize - padding / 2);
		}

	}

	@SuppressWarnings("SuspiciousNameCombination")
	private void drawMap(Graphics2D g2d, float zoom) {
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Map
		int screenPositionX = (int) (mPlayerX - this.getWidth() / zoom / 2.0);
		int screenPositionY = (int) (mPlayerY - this.getHeight() / zoom / 2.0);
		int screenOffsetX = Math.floorMod(screenPositionX, BLOCK_SIZE);
		int screenOffsetY = Math.floorMod(screenPositionY, BLOCK_SIZE);
		int firstVisibleBlockX = Math.floorDiv(screenPositionX, BLOCK_SIZE);
		int firstVisibleBlockY = Math.floorDiv(screenPositionY, BLOCK_SIZE);
		int visibleBlockW = (int) (this.getWidth() / zoom) / BLOCK_SIZE + 2;
		int visibleBlockH = (int) (this.getHeight() / zoom) / BLOCK_SIZE + 2;

		// TODO Remove usage of fake data
		BallModel[][] balls = dom.getAllBall();
		int ballRadius = (int) (BALL_RADIUS * zoom);
		int roomRadius = (int) (BLOCK_SIZE * zoom / 2f);
		int playerRadius = (int) (PLAYER_SIZE / 2 * zoom);

		ArrayList<Character> otherCharacters = dom.getOtherCharacter();
		for (int screenBlockX = 0; screenBlockX < visibleBlockW; screenBlockX++) {
			for (int screenBlockY = 0; screenBlockY < visibleBlockH; screenBlockY++) {
				// Actual block index in map
				int mapBlockX = Math.floorMod(screenBlockX + firstVisibleBlockX, MAP_WIDTH);
				int mapBlockY = Math.floorMod(screenBlockY + firstVisibleBlockY, MAP_HEIGHT);

				boolean isPlayerInBlock = isPlayerInBlock(mapBlockX, mapBlockY);
				boolean hasPlayerInBlock = isPlayerInBlock;
				for (Character character : otherCharacters) {
					hasPlayerInBlock |=
							isPlayerInBlock((int) character.getX(), (int) character.getY(),
									mapBlockX, mapBlockY);
				}

				BasicBlock block = hasPlayerInBlock ? BasicBlock.LIGHT : BasicBlock.DARK;

				int drawPositionX = (int) ((screenBlockX * BLOCK_SIZE - screenOffsetX) * zoom);
				int drawPositionY = (int) ((screenBlockY * BLOCK_SIZE - screenOffsetY) * zoom);
				int drawBlockSize = (int) Math.ceil(BLOCK_SIZE * zoom);

				g2d.drawImage(mBlockImages[block.ordinal()], drawPositionX, drawPositionY,
						drawBlockSize, drawBlockSize, null);
				// Draw block info
				g2d.setColor(isPlayerInBlock ? new Color(0xff35160a) : new Color(0xff444444));
				g2d.drawString(String.format("(%d,%d)", Math.floorMod(mapBlockX, MAP_WIDTH),
						Math.floorMod(mapBlockY, MAP_HEIGHT)), drawPositionX + (10 * zoom),
						drawPositionY + (15 * zoom));

				// Draw ball
				if (hasPlayerInBlock) {
					BallModel ballInRoom = balls[mapBlockX][mapBlockY];
					if (ballInRoom != null) {
						if (ballInRoom.ballType == BallModel.BallType.NONE) {
							g2d.setStroke(new BasicStroke(2f * zoom, BasicStroke.CAP_ROUND,
									BasicStroke.JOIN_MITER, 2f * zoom, new float[]{6f * zoom}, 0f));
						} else {
							g2d.setColor(new Color(ballInRoom.getBallColor()));
							g2d.fillOval(drawPositionX + roomRadius - ballRadius,
									drawPositionY + roomRadius - ballRadius, 2 * ballRadius,
									2 * ballRadius);
							g2d.setColor(new Color(0x66ffffff, true));
							g2d.fillOval(drawPositionX + roomRadius + (int) (ballRadius * .2f),
									drawPositionY + roomRadius - (int) (ballRadius * .6f),
									(int) (ballRadius * .4f), (int) (ballRadius * .4f));
							g2d.setStroke(new BasicStroke(2 * zoom));
						}
						g2d.setColor(Color.BLACK);
						g2d.drawOval(drawPositionX + roomRadius - ballRadius,
								drawPositionY + roomRadius - ballRadius, 2 * ballRadius,
								2 * ballRadius);
					}
				}

				// Draw other players
				for (Character character : otherCharacters) {
					boolean isCharacterInBlock =
							isPlayerInBlock((int) character.getX(), (int) character.getY(),
									mapBlockX, mapBlockY);
					if (isCharacterInBlock) {
						int offsetX = (int) (character.getX() % 100 * zoom) - playerRadius;
						int offsetY = (int) (character.getY() % 100 * zoom) - playerRadius;
						g2d.drawImage(character.getImage(), drawPositionX + offsetX,
								drawPositionY + offsetY, 2 * playerRadius, 2 * playerRadius, null);
					}
				}
			}
		}

		// TODO Draw players
		g2d.drawImage(myCharacter.getImage(), this.getWidth() / 2 - playerRadius,
				this.getHeight() / 2 - playerRadius, 2 * playerRadius, 2 * playerRadius, null);

		boolean DEBUG = true;
		boolean DEBUG_PLAYER = true;
		// noinspection ConstantConditions
		if (DEBUG) {
			// Debug messages
			int lineHeight = (int) (10 * zoom);
			int drawTextPositionX = (int) (5 * zoom);
			int drawTextPositionY = lineHeight;

			g2d.setColor(new Color(0xffeeeeee));

			RenderThread renderThread = mApp.getRenderThread();
			if (renderThread != null) {
				g2d.drawString(String.format("FPS: %.0f (%d)", renderThread.getCurrentFPS(),
						renderThread.getCurrentSleepDuration()), drawTextPositionX,
						drawTextPositionY);
			}

			// noinspection ConstantConditions
			if (DEBUG_PLAYER) {
				drawTextPositionY += lineHeight;
				g2d.drawString(String.format("Player offset: (%d,%d)", (int) mPlayerX % BLOCK_SIZE,
						(int) mPlayerY % BLOCK_SIZE), drawTextPositionX, drawTextPositionY);

				drawTextPositionY += lineHeight;
				g2d.drawString(String.format("Player block: (%d,%d)",
						(int) mPlayerX / BLOCK_SIZE % MAP_WIDTH,
						(int) mPlayerY / BLOCK_SIZE % MAP_HEIGHT), drawTextPositionX,
						drawTextPositionY);
			} else {
				drawTextPositionY += lineHeight;
				g2d.drawString(
						String.format("Screen offset: (%d,%d)", screenOffsetX, screenOffsetY),
						drawTextPositionX, drawTextPositionY);

				drawTextPositionY += lineHeight;
				g2d.drawString(String.format("Screen block: (%d,%d)", firstVisibleBlockX,
						firstVisibleBlockY), drawTextPositionX, drawTextPositionY);
			}

			drawTextPositionY += lineHeight;
			g2d.drawString(
					String.format("Player position: (%d,%d)", (int) mPlayerX, (int) mPlayerY),
					drawTextPositionX, drawTextPositionY);
		}
	}

	/**
	 * Check if local player is inside a room
	 *
	 * @param mapBlockX The x coordinate of the room
	 * @param mapBlockY The y coordinate of the room
	 */
	private boolean isPlayerInBlock(int mapBlockX, int mapBlockY) {
		IntegerPosition playerBlock = getPlayerCurrentBlock();
		return playerBlock.x == mapBlockX && playerBlock.y == mapBlockY;
	}

	/**
	 * Check if a player is inside a room
	 *
	 * @param playerX   The x position of the player
	 * @param playerY   The y position of the player
	 * @param mapBlockX The x coordinate of the room
	 * @param mapBlockY The y coordinate of the room
	 * @return The local player is inside (mapBlockX, mapBlockY)
	 */
	private boolean isPlayerInBlock(int playerX, int playerY, int mapBlockX, int mapBlockY) {
		IntegerPosition playerBlock = getPlayerCurrentBlock(playerX, playerY);
		return playerBlock.x == mapBlockX && playerBlock.y == mapBlockY;
	}

	/**
	 * Get block coordinates of local player
	 */
	private IntegerPosition getPlayerCurrentBlock() {
		return getPlayerCurrentBlock((int) mPlayerX, (int) mPlayerY);
	}

	/**
	 * Get block coordinates of any player
	 */
	private IntegerPosition getPlayerCurrentBlock(int playerX, int playerY) {
		int playerBlockX = Math.floorDiv(playerX, BLOCK_SIZE);
		int playerBlockY = Math.floorDiv(playerY, BLOCK_SIZE);
		return new IntegerPosition(playerBlockX, playerBlockY);
	}

	class ItemRectangle {

		private final Color DEFAULT_COLOR = new Color(0xff607d8b);
		private final Color PRESSED_COLOR = new Color(0xff37474f);
		private Color color;
		private Rectangle rectangle;
		private String shapeName;
		private int index;

		public ItemRectangle(Rectangle rectangle, String shapeName, int index) {
			this.rectangle = rectangle;
			this.color = DEFAULT_COLOR;
			this.shapeName = shapeName;
			this.index = index;
		}

		public void setPressed(boolean pressed) {
			color = pressed ? PRESSED_COLOR : DEFAULT_COLOR;
		}

		public int getIndex() {
			return index;
		}

		public Color getColor() {
			return color;
		}

		public String getShapeName() {
			return shapeName;
		}

	}

}
