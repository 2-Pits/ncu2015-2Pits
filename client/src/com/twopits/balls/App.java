package com.twopits.balls;

import com.twopits.balls.statusPanel.LoadingPanel;
import com.twopits.balls.statusPanel.LoadingThread;
import dom.DynamicObjectModule;

import javax.swing.*;
import java.awt.*;

/**
 * Main class of Balls
 * Created by hiking on 2015/12/21.
 */
public class App {

	public SceneRenderEngine getSceneRenderEngine() {
		return mRenderEngine;
	}

	private SceneRenderEngine mRenderEngine;
	private RenderThread mRenderThread;
	private KeyManager mKeyManager;
	private UDPUS mUdpus;
	private DynamicObjectModule mDom;
	private TCPCM mTcpcm;
	private JPanel mCardPanel ;
	private CardLayout mCardLayout;


	public RenderThread getRenderThread() {
		return mRenderThread;
	}

	public KeyManager getKeyManager() {
		return mKeyManager;
	}

	public DynamicObjectModule getDynamicObjectModule() {
		return mDom;
	}

	public static void main(String[] args) {
		new App();
	}

	public App() {

		mDom = new DynamicObjectModule();

		mRenderEngine = new SceneRenderEngine(this);
		mRenderThread = new RenderThread(mRenderEngine);

		JFrame window = new GameWindow();

		mTcpcm = new TCPCM(this,mDom);
		mTcpcm.buildConnection();

		mCardLayout = new CardLayout();
		mCardPanel = new JPanel(mCardLayout);
		LoadingPanel panel = new LoadingPanel(mTcpcm);
		LoadingThread thread = new LoadingThread(panel);

		mCardPanel.add(panel,"statusPanel");
		mCardPanel.add(mRenderEngine,"game");
		window.add(mCardPanel);

		mKeyManager = new KeyManager(mTcpcm);
		window.addKeyListener(mKeyManager);
		window.setVisible(true);

		mUdpus = new UDPUS(mDom);
		mUdpus.iniUDPServer();
		mUdpus.runReceiveThread();
		mUdpus.runSendThread();

		thread.start();
		mCardLayout.show(mCardPanel, "statusPanel");

		new Thread(new Runnable() {
			@Override
			public void run() {
				while(panel.isLoading()){
					try
					{
						Thread.sleep(100);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
				mCardLayout.show(mCardPanel,"game");
				mRenderThread.start();
				mDom.startGame();
			}
		}).start();

	}
}
