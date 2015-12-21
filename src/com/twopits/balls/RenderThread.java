package com.twopits.balls;

/**
 * Render thread
 * Created by hiking on 2015/12/14.
 */
public class RenderThread extends Thread {

	private SceneRenderEngine mRenderEngine;

	private static final float FPS_CAP = 60f;
	private float mSmoothedDt = 1000 / FPS_CAP;
	private long mSleepDuration;

	public RenderThread(SceneRenderEngine renderEngine) {
		mRenderEngine = renderEngine;
	}

	public void startRenderThread() {
		this.start();
	}

	public float getCurrentFPS() {
		return 1000 / (mSmoothedDt + mSleepDuration);
	}

	public long getCurrentSleepDuration() {
		return mSleepDuration;
	}

	@Override
	public void run() {
		super.run();

		long lastFrameTime = System.currentTimeMillis();
		// noinspection InfiniteLoopStatement
		while (true) {
			long currentTime = System.currentTimeMillis();
			long dt = currentTime - lastFrameTime;
			lastFrameTime = currentTime;
			if (dt > 0) {
				mRenderEngine.update(dt);
			}

			// Calculate FPS cap
			mSmoothedDt = (mSmoothedDt * 9f + dt - mSleepDuration) / 10f;
			mSleepDuration = (long) (1000 / FPS_CAP - mSmoothedDt);
			if (mSleepDuration > 0L) {
				// Cap the screen rate
				try {
					Thread.sleep(mSleepDuration);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				mSleepDuration = 0;
			}
		}
	}
}
