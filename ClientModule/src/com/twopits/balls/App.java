package com.twopits.balls;

import com.twopits.balls.dom.DynamicObjectModule;

import javax.swing.JFrame;
import java.net.InetAddress;

/**
 * Main class of Balls
 * Created by hiking on 2015/12/21.
 */
public class App {

	private RenderThread mRenderThread;
	private KeyManager mKeyManager;

	public RenderThread getRenderThread() {
		return mRenderThread;
	}

	public KeyManager getKeyManager() {
		return mKeyManager;
	}

	public static void main(String[] args) {
		new App();
	}

	public App() {
        try {

            SceneRenderEngine renderEngine = new SceneRenderEngine(this);
            JFrame window = new GameWindow();

            window.add(renderEngine);
            mKeyManager = new KeyManager();
            window.addKeyListener(mKeyManager);
            window.setVisible(true);

            mRenderThread = new RenderThread(renderEngine);
            mRenderThread.startRenderThread();

            DynamicObjectModule DOM = new DynamicObjectModule();
            Client client = new Client(DOM);
            client.connectServer(InetAddress.getByName("127.0.0.1"));



        }catch(Exception e){
            e.printStackTrace();
        }
	}
}
