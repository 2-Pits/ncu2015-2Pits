package com.twopits.balls.libs;

import java.awt.Dimension;
import java.awt.Toolkit;

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

	public static double floorMod(double a, int b) {
		double r = a % b;
		if (r < 0) {
			r += b;
		}
		return r;
	}
}
