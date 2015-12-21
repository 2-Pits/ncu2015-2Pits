package com.twopits.balls;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Manage key press
 * Created by hiking on 2015/12/21.
 */
public class KeyManager implements KeyListener {
	private List<Integer> mPressedKeys;

	public KeyManager() {
		mPressedKeys = new ArrayList<>();
	}

	public boolean isKeyPressed(int keyCode) {
		return mPressedKeys.contains(keyCode);
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (!mPressedKeys.contains(e.getKeyCode())) {
			mPressedKeys.add(e.getKeyCode());
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		mPressedKeys.remove((Integer) e.getKeyCode());
	}
}
