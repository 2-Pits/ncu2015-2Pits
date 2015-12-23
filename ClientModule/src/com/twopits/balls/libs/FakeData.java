package com.twopits.balls.libs;

import com.twopits.balls.models.BallModel;

/**
 * Fake data generator
 * Created by hiking on 2015/12/21.
 */
public class FakeData {

	private static BallModel[][] mBallsMap;

	public static BallModel[][] getBallsMap() {
		return mBallsMap;
	}

	public static void initBallsMap(int width, int height) {
		mBallsMap = new BallModel[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				BallModel.BallType ballType = BallModel.BallType.values()[(int) (Math.random() *
						BallModel.BallType.values().length)];
				mBallsMap[x][y] = new BallModel(ballType);
			}
		}
	}
}
