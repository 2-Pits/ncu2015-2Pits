package com.twopits.balls;

import java.security.InvalidParameterException;

/**
 * Scene data module (SDM)
 * Created by hiking on 2015/12/14.
 */
public class SceneDataManager {

	public static final int MAP_WIDTH = 50, MAP_HEIGHT = 20;

	public static final int BLOCK_SIZE = 100;

	public enum SceneBlock {
		GRASS, ROCK, DIRT, WATER, LAVA;
	}

	private SceneBlock[][] mBlocks;

	/**
	 * Get current scene data
	 *
	 * @return Current scene data, <code>null</code> if no map was loaded.
	 */
	public SceneBlock[][] getBlocks() {
		return mBlocks;
	}

	public static SceneDataManager newRandomDataInstance() {
		SceneDataManager sceneDataManager = new SceneDataManager();
		sceneDataManager.loadMap(getRandomMap());
		return sceneDataManager;
	}

	public void loadMap(SceneBlock[][] map) {
		if (map.length != MAP_WIDTH || map[0].length != MAP_HEIGHT) {
			throw new InvalidParameterException("Map should be " + MAP_WIDTH + " * " + MAP_HEIGHT);
		}
		for (int row = 0; row < map.length; row++) {
			SceneBlock[] blockRow = map[row];
			for (int col = 0; col < blockRow.length; col++) {
				SceneBlock block = blockRow[col];
				if (block == null) {
					throw new InvalidParameterException("Block at (" + row + ", " +
							col + ") shouldn't be null.");
				}
			}
		}

		mBlocks = map;
	}

	public static SceneBlock[][] getRandomMap() {
		SceneBlock[][] scene = new SceneBlock[MAP_WIDTH][];
		for (int row = 0; row < MAP_WIDTH; row++) {
			scene[row] = new SceneBlock[MAP_HEIGHT];
			for (int col = 0; col < MAP_HEIGHT; col++) {
				scene[row][col] =
						SceneBlock.values()[((int) (Math.random() * SceneBlock.values().length))];
			}
		}
		return scene;
	}
}
