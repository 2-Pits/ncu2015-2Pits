package com.twopits.balls.models;

/**
 * Model of a ball object in game
 * Created by hiking on 2015/12/21.
 */
public class BallModel {

	public enum BallType {
		NONE, BLACK, BROWN, RED, ORANGE, YELLOW, GREEN, BLUE, PURPLE, GREY, WHITE;
	}

	public static int[] BALL_COLOR =
			{0x0, 0xff1f2d39, 0xff795548, 0xffe53935, 0xfffb8c00, 0xffffb300, 0xff43a047,
					0xff1e88e5, 0xff5e35b1, 0xff6c8697, 0xffeeeeee};

	public BallType ballType;

	public BallModel(BallType type) {
		this.ballType = type;
	}
	@Override
	public boolean equals(Object obj) {
		return obj instanceof BallModel && this.ballType == ((BallModel) obj).ballType;
	}

	public int getBallColor() {
		return BALL_COLOR[ballType == null ? 0 : ballType.ordinal()];
	}
}
