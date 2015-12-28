package com.twopits.balls;

import dom.DynamicObjectModule;

import javax.swing.JFrame;

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

		window.add(mRenderEngine);
		mKeyManager = new KeyManager();
		window.addKeyListener(mKeyManager);
		window.setVisible(true);
		
		mUdpus = new UDPUS(mDom);
		mUdpus.iniUDPServer();
		mUdpus.runReciveThread();
		mUdpus.runSendThread();
	}
}
