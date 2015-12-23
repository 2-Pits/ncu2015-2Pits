package com.twopits.balls;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * Container of <code>SceneRenderEngine</code>
 * Created by hiking on 2015/12/21.
 */
public class GameWindow extends JFrame {

	public GameWindow() {
		this.setTitle(Constants.APP_NAME);
		this.getContentPane().setPreferredSize(new Dimension(500, 300));
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.pack();
		Utils.setWindowsToCenter(this);
		Utils.enableOsxFullscreen(this, Constants.APP_NAME);
	}
}
