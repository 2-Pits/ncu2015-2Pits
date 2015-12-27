package com.twopits.balls;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.lang.reflect.Method;

import javax.swing.JFrame;

/**
 * Utilities for Balls
 * Created by hiking on 2015/10/26.
 */
public class Utils {

	public static void setWindowsToCenter(JFrame frm) {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int w = frm.getSize().width;
		int h = frm.getSize().height;
		int x = (dim.width - w) / 2;
		int y = (dim.height - h) / 2;
		frm.setLocation(x, y);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static void enableOsxFullscreen(Window window, String appName) {
		// Enable fullscreen
		try {
			Class util = Class.forName("com.apple.eawt.FullScreenUtilities");
			Class params[] = new Class[]{Window.class, Boolean.TYPE};
			Method method = util.getMethod("setWindowCanFullScreen", params);
			method.invoke(util, window, true);
		} catch (Exception e) {
			// no op
		}
	}

	public static double floorMod(double a, int b) {
		double r = a % b;
		if (r < 0) {
			r += b;
		}
		return r;
	}
}
