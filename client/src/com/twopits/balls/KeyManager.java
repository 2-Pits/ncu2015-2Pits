package com.twopits.balls;

import dom.DynamicObjectModule;

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
	private TCPCM mTcpcm;

	public KeyManager( TCPCM tcpcm) {
		this.mTcpcm = tcpcm;
		mPressedKeys = new ArrayList<>();
	}

	public boolean isKeyPressed(int keyCode) {
		return mPressedKeys.contains(keyCode);
	}

	void updateStatus(){

		int QWER[] = {KeyEvent.VK_Q,KeyEvent.VK_W,KeyEvent.VK_E,KeyEvent.VK_R};
		for(int code : QWER){
			if(isKeyPressed(code)){
				mTcpcm.pickUpBalls(code);
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (!mPressedKeys.contains(e.getKeyCode())) {
			mPressedKeys.add(e.getKeyCode());
		}
		updateStatus();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		mPressedKeys.remove((Integer) e.getKeyCode());
	}
}
