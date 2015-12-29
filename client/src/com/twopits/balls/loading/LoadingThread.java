package com.twopits.balls.loading;

import com.twopits.balls.SceneRenderEngine;

/**
 * Render thread
 * Created by hiking on 2015/12/14.
 */
public class LoadingThread extends Thread {

	private LoadingPanel mLoadingPanel;

	private static final float FPS_CAP = 60f;
	private float mSmoothedDt = 1000 / FPS_CAP;
	private long mSleepDuration;

	public LoadingThread(LoadingPanel loadingPanel) {
		mLoadingPanel = loadingPanel;
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
		while (mLoadingPanel.isLoading()) {
			long currentTime = System.currentTimeMillis();
			long dt = currentTime - lastFrameTime;
			lastFrameTime = currentTime;
			if (dt > 0) {
				mLoadingPanel.update(dt);
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
