package com.twopits.balls;

import org.junit.Test;

import java.security.InvalidParameterException;

import static org.junit.Assert.assertTrue;

/**
 * A test class for SceneRenderEngine
 * Created by hiking on 2015/12/14.
 */
public class RenderTest {

	@org.junit.Test
	public void testStartRenderThread() throws Exception {
		SceneRenderEngine game = SceneRenderEngine.initRenderEngine();
		RenderThread renderThread = new RenderThread(game);
		renderThread.startRenderThread();
	}

	@org.junit.Test
	public void testGetVirtualCharacterXY() throws Exception {
		SceneRenderEngine game = SceneRenderEngine.initRenderEngine();
		SceneRenderEngine.Position playerPosition = game.getVirtualCharacterXY();
		assertTrue(isValidPosition(playerPosition));
	}

	private boolean isValidPosition(SceneRenderEngine.Position playerPosition) {
		return (playerPosition.x >= 0) &&
				(playerPosition.x < SceneRenderEngine.MAP_WIDTH * SceneRenderEngine.BLOCK_SIZE) &&
				(playerPosition.y >= 0) &&
				(playerPosition.y < SceneRenderEngine.MAP_HEIGHT * SceneRenderEngine.BLOCK_SIZE);
	}

	@Test
	public void testSetVirtualCharacterXY() throws Exception {
		SceneRenderEngine game = SceneRenderEngine.initRenderEngine();
		for (int i = 0; i < 50; i++) {
			game.setVirtualCharacterXY(getRandomPlayerPosition());
			assertTrue(isValidPosition(game.getVirtualCharacterXY()));
		}
	}

	private SceneRenderEngine.Position getRandomPlayerPosition() {
		double x = Math.random() * SceneRenderEngine.MAP_WIDTH * SceneRenderEngine.BLOCK_SIZE * 10;
		double y = Math.random() * SceneRenderEngine.MAP_HEIGHT * SceneRenderEngine.BLOCK_SIZE * 10;
		return new SceneRenderEngine.Position(x, y);
	}

	@org.junit.Test(expected = InvalidParameterException.class)
	public void testLoadWrongSizeMap() throws Exception {
		SceneRenderEngine game = SceneRenderEngine.initRenderEngine();
		game.loadMap(new SceneRenderEngine.BasicBlock[SceneRenderEngine.MAP_WIDTH -
				1][SceneRenderEngine.MAP_HEIGHT]);
	}
}