package com.twopits.balls;

import dom.DynamicObjectModule;

import javax.swing.JFrame;

/**
 * Main class of Balls
 * Created by hiking on 2015/12/21.
 */
public class App {

	private RenderThread mRenderThread;
	private KeyManager mKeyManager;
	private UDPUS mudpus;
	private DynamicObjectModule dom;

	public RenderThread getRenderThread() {
		return mRenderThread;
	}

	public KeyManager getKeyManager() {
		return mKeyManager;
	}

	public DynamicObjectModule getDynamicObjectModule() {
		return dom;
	}

	public static void main(String[] args) {
		new App();
	}

	public App() {
		dom = new DynamicObjectModule();
		dom.initMyCharacter(1,2,3);
		SceneRenderEngine renderEngine = new SceneRenderEngine(this);
		JFrame window = new GameWindow();

		window.add(renderEngine);
		mKeyManager = new KeyManager();
		window.addKeyListener(mKeyManager);
		window.setVisible(true);

		mRenderThread = new RenderThread(renderEngine);
		mRenderThread.startRenderThread();

		mudpus = new UDPUS(dom);
		mudpus.iniUDPServer();
		mudpus.runReciveThread();
		mudpus.runSendThread();
	}
}
