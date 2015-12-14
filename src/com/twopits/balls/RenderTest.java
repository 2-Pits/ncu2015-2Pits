package com.twopits.balls;

import org.junit.Test;

import java.security.InvalidParameterException;

import static com.twopits.balls.SceneDataManager.BLOCK_SIZE;
import static com.twopits.balls.SceneDataManager.MAP_HEIGHT;
import static com.twopits.balls.SceneDataManager.MAP_WIDTH;
import static com.twopits.balls.SceneDataManager.SceneBlock;
import static org.junit.Assert.assertTrue;

/**
 * A test class for SceneRenderEngine & RenderThread
 * Created by hiking on 2015/12/14.
 */
public class RenderTest {

	@org.junit.Test
	public void testStartRenderThread() throws Exception {
		SceneRenderEngine game =
				SceneRenderEngine.initRenderEngine(SceneDataManager.newRandomDataInstance());
		RenderThread renderThread = new RenderThread(game);
		renderThread.startRenderThread();
	}

	@org.junit.Test
	public void testGetVirtualCharacterXY() throws Exception {
		SceneRenderEngine game =
				SceneRenderEngine.initRenderEngine(SceneDataManager.newRandomDataInstance());
		SceneRenderEngine.Position playerPosition = game.getVirtualCharacterXY();
		assertTrue(isValidPosition(playerPosition));
	}

	private boolean isValidPosition(SceneRenderEngine.Position playerPosition) {
		return (playerPosition.x >= 0) &&
				(playerPosition.x < MAP_WIDTH * BLOCK_SIZE) &&
				(playerPosition.y >= 0) &&
				(playerPosition.y < MAP_HEIGHT * BLOCK_SIZE);
	}

	@Test
	public void testSetVirtualCharacterXY() throws Exception {
		SceneRenderEngine game =
				SceneRenderEngine.initRenderEngine(SceneDataManager.newRandomDataInstance());
		for (int i = 0; i < 50; i++) {
			game.setVirtualCharacterXY(getRandomPlayerPosition());
			assertTrue(isValidPosition(game.getVirtualCharacterXY()));
			game.update(0);
			Thread.sleep(100);
		}
	}

	private SceneRenderEngine.Position getRandomPlayerPosition() {
		double x = Math.random() * MAP_WIDTH * BLOCK_SIZE * 10;
		double y = Math.random() * MAP_HEIGHT * BLOCK_SIZE * 10;
		return new SceneRenderEngine.Position(x, y);
	}

	@org.junit.Test(expected = InvalidParameterException.class)
	public void testLoadWrongSizeMap() throws Exception {
		SceneDataManager sceneDataManager = new SceneDataManager();
		sceneDataManager.loadMap(new SceneBlock[MAP_WIDTH - 1][MAP_HEIGHT]);
	}

	@org.junit.Test(expected = InvalidParameterException.class)
	public void testLoadNullElementMap() throws Exception {
		SceneDataManager sceneDataManager = new SceneDataManager();
		SceneBlock[][] blocks = SceneDataManager.getRandomMap();
		blocks[0][0] = null;
		sceneDataManager.loadMap(blocks);
	}
}