package com.twopits.balls;

import org.junit.Test;

import java.security.InvalidParameterException;

import static org.junit.Assert.assertTrue;

/**
 * A test class for BallsMain
 * Created by hiking on 2015/12/14.
 */
public class BallsMainTest {

	@org.junit.Test
	public void testStartRenderThread() throws Exception {
		BallsMain game = BallsMain.initGame();
		game.startRenderThread();
	}

	@org.junit.Test
	public void testGetVirtualCharacterXY() throws Exception {
		BallsMain game = BallsMain.initGame();
		BallsMain.Position playerPosition = game.getVirtualCharacterXY();
		assertTrue(isValidPosition(playerPosition));
	}

	private boolean isValidPosition(BallsMain.Position playerPosition) {
		return (playerPosition.x >= 0) &&
				(playerPosition.x < BallsMain.MAP_WIDTH * BallsMain.BLOCK_SIZE) &&
				(playerPosition.y >= 0) &&
				(playerPosition.y < BallsMain.MAP_HEIGHT * BallsMain.BLOCK_SIZE);
	}

	@Test
	public void testSetVirtualCharacterXY() throws Exception {
		BallsMain game = BallsMain.initGame();
		for (int i = 0; i < 50; i++) {
			game.setVirtualCharacterXY(getRandomPlayerPosition());
			assertTrue(isValidPosition(game.getVirtualCharacterXY()));
		}
	}

	private BallsMain.Position getRandomPlayerPosition() {
		double x = Math.random() * BallsMain.MAP_WIDTH * BallsMain.BLOCK_SIZE * 10;
		double y = Math.random() * BallsMain.MAP_HEIGHT * BallsMain.BLOCK_SIZE * 10;
		return new BallsMain.Position(x, y);
	}

	@org.junit.Test(expected = InvalidParameterException.class)
	public void testLoadWrongSizeMap() throws Exception {
		BallsMain game = BallsMain.initGame();
		game.loadMap(new BallsMain.BasicBlock[BallsMain.MAP_WIDTH - 1][BallsMain.MAP_HEIGHT]);
	}
}