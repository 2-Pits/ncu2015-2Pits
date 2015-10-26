package com.twopits.balls;

import com.twopits.balls.libs.Utils;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * Main class of Balls
 * Created by hiking on 2015/10/26.
 */
public class BallsMain extends JPanel {

	private static Dimension mScreenDimen = new Dimension(500, 300);
	private static long mLastFrameTime;

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.getContentPane().setPreferredSize(mScreenDimen);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.pack();
		Utils.setWindowsToCenter(frame);

		BallsMain game = new BallsMain();
		frame.add(game);
		frame.setVisible(true);

		mLastFrameTime = System.currentTimeMillis();
		while (true) {
			game.moveBall();
			game.repaint();
		}
	}

	double x = 0, y = 0;
	int speedX = 100, speedY = 100;

	private void moveBall() {
		long currentTime = System.currentTimeMillis();
		long dt = currentTime - mLastFrameTime;
		mLastFrameTime = currentTime;

		x += speedX * dt / 1000.0;
		y += speedY * dt / 1000.0;

		if (x + 30 > mScreenDimen.width) {
			x -= (x + 30 - mScreenDimen.width) * 2;
			speedX *= -1;
		}
		if (x < 0) {
			x *= -1;
			speedX *= -1;
		}

		if (y + 30 > mScreenDimen.height) {
			y -= (y + 30 - mScreenDimen.height) * 2;
			speedY *= -1;
		}
		if (y < 0) {
			y *= -1;
			speedY *= -1;
		}
	}

	public BallsMain() {

	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.fillOval((int) x, (int) y, 30, 30);
		g2d.drawString("Position = " + String.format("(%d,%d)", (int) x, (int) y), 10, 250);
		g2d.drawString("Time = " + System.currentTimeMillis(), 10, 270);
	}
}
