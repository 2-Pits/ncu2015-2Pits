package com.twopits.balls.statusPanel;

import com.twopits.balls.App;
import com.twopits.balls.TCPCM;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JPanel;

import sprite.Character;

/**
 * Created by fish on 2015/12/28.
 */
public class LoadingPanel extends JPanel {

	private static final float MAX_VISIBLE_BLOCKS_IN_HEIGHT = 3f;
	private static final float MAX_VISIBLE_BLOCKS_IN_WIDTH = 6f;
	private static final int MAX_PLAYER_COUNT = 4;
	private static final int BLOCK_SIZE = 100;
	private static final float PLAYER_SIZE = 90f;
	private static final int FRAME_TIME = 300;

	private App mApp;
	private int mDt = 0;
	private Font mGameFont;
	private ArrayList<Character> mCharacterList;
	private int mClientCount;
	private boolean isLoading;
	private String mLoadingString;
	private TCPCM mTCPCM;

	public LoadingPanel(App app, TCPCM tcpcm) {
		super();

		mApp = app;
		mTCPCM = tcpcm;
		initValues();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		Graphics2D g2d = (Graphics2D) g;

		float zoom = getZoomFactor();
		g2d.setFont(mGameFont.deriveFont(10 * zoom));

		drawPlayers(g2d, zoom);
		drawLoadingText(g2d, zoom);
	}

	public boolean isLoading() {
		return isLoading;
	}

	private void initValues() {
		mCharacterList = createCharacters();
		mClientCount = 0;
		mLoadingString = "Loading";
		isLoading = true;

		try {
			mGameFont = Font.createFont(Font.TRUETYPE_FONT, new File("res/supercell_magic.ttf"));
		} catch (Exception e) {
			e.printStackTrace();
			mGameFont = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
		}
	}

	private ArrayList<Character> createCharacters() {
		ArrayList<Character> characters = new ArrayList<>();
		for (int i = 0; i < MAX_PLAYER_COUNT; i++) {
			Character character = new Character(i, 0, 0);
			character.setDirection(0);
			characters.add(character);
		}
		return characters;
	}

	public void update(long dt) {
		mClientCount = mTCPCM.getClientCount();
		updatePlayerSprite((int) dt);
		updateLoadingString((int) dt);

		repaint();

		if (mClientCount == 4) {
			isLoading = false;
		}
	}

	private void updateLoadingString(int dt) {
		this.mDt += dt;
		while (this.mDt >= FRAME_TIME) {
			mLoadingString += ".";
			if (mLoadingString.contains("....")) {
				mLoadingString = "Loading";
			}
			this.mDt -= FRAME_TIME;
		}
	}

	private void updatePlayerSprite(int dt) {

		// Use fake position to show animation
		int fakePosition = (int) (Math.random() * 100);

		for (int i = 0; i < mClientCount; i++) {
			Character character = mCharacterList.get(i);
			character.setPosition(fakePosition, fakePosition);
			character.update(dt);
		}
	}

	private void drawPlayers(Graphics2D g2d, float zoom) {
		int rectangleSize = (int) (BLOCK_SIZE * zoom);
		int padding = (int) (10 * zoom);
		int drawRectanglePositionY = this.getHeight() / 2 - rectangleSize / 2;
		int playerRadius = (int) (PLAYER_SIZE / 2 * zoom);

		for (int i = 0; i < MAX_PLAYER_COUNT; i++) {
			double posX = padding * i + i * rectangleSize + rectangleSize / 2;
			g2d.setStroke(new BasicStroke(5 * zoom));
			RenderingHints rh = g2d.getRenderingHints();
			rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHints(rh);
			g2d.drawRoundRect((int) posX, drawRectanglePositionY, rectangleSize, rectangleSize,
					(int) (10 * zoom), (int) (10 * zoom));

			if (i < mClientCount) {
				// Draw characters
				Character character = mCharacterList.get(i);
				rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
				g2d.setRenderingHints(rh);
				g2d.drawImage(character.getImage(), (int) posX + padding / 2,
						drawRectanglePositionY + padding / 2, 2 * playerRadius, 2 * playerRadius,
						null);
			}
		}
	}

	private void drawLoadingText(Graphics2D g2d, float zoom) {
		int padding = (int) (10 * zoom);
		int drawTextPosX = getWidth() / 2 - padding * 4;
		int drawTextPosY = getHeight() - padding;

		RenderingHints rh = g2d.getRenderingHints();
		rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHints(rh);
		g2d.setFont(mGameFont.deriveFont(15 * zoom));
		g2d.drawString(mLoadingString, drawTextPosX, drawTextPosY);
	}

	private float getZoomFactor() {
		if (getWidth() / (float) getHeight() >
				MAX_VISIBLE_BLOCKS_IN_WIDTH / MAX_VISIBLE_BLOCKS_IN_HEIGHT) {
			return getWidth() / (MAX_VISIBLE_BLOCKS_IN_WIDTH * BLOCK_SIZE);
		} else {
			return getHeight() / (MAX_VISIBLE_BLOCKS_IN_HEIGHT * BLOCK_SIZE);
		}
	}
}
