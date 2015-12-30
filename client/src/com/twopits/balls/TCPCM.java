package com.twopits.balls;

import com.google.gson.Gson;
import com.twopits.balls.libs.Constants;
import com.twopits.balls.libs.KeyOpt;
import com.twopits.balls.libs.OneGamer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import dom.DynamicObjectModule;
import sprite.Character;

/**
 * TCP module
 * Created by DBLAB on 2015/12/27.
 */
public class TCPCM {

	private DynamicObjectModule dom;
	private App app;
	private Socket socket;
	private Thread receiveThread;
	private InputStream in;
	private OutputStream out;
	private BufferedReader br;
	private int clientCount = 0;

	public TCPCM(App app, DynamicObjectModule dom) {
		this.dom = dom;
		this.app = app;
	}

	public void buildConnection() {
		try {
			InetAddress serverIP = InetAddress.getByName(Constants.SERVER_IP);
			socket = new Socket(serverIP, Constants.TCP_PORT);
			System.out.println("Create Socket");
			if (socket != null) {
				initAll();
				createThread();
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Connection Fail!");
		}
	}

	public int getClientCount() {
		return clientCount;
	}

	public void pickUpBalls(int keyCode) {
		System.out.println("click");
		Character character = dom.getMyCharacter();
		int ID = character.getID();
		KeyOpt myData = new KeyOpt(ID, keyCode);
		String tempS = new Gson().toJson(myData);
		//        byte[] bytes = tempS.getBytes(StandardCharsets.UTF_8);
		PrintWriter printWriter = new PrintWriter(out);

		printWriter.print(tempS + "\n");
		printWriter.flush();
	}

	private void createThread() {
		receiveThread = new Thread(() -> {
			try {
				while (clientCount < 4) {
					clientCount = br.read();
					System.out.println("Client Count = " + clientCount);
				}
			} catch (IOException e) {
				e.printStackTrace();
				try {
					stopConnection();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

			boolean gameHadStarted = false;
			while (true) {
				if (dom.startMove()) {
					gameHadStarted = true;
					System.out.println("enter");
					String line;
					String gameBall;
					try {
						line = br.readLine();
						gameBall = br.readLine();
						System.out.println(gameBall);
						dom.updateBall(gameBall);
						if (Integer.valueOf(line) != -1) {
							// If winner show up, then close connection and show winner.
							System.out.println("Winner is : " + line + "\n");
							dom.endGame(line);
							stopConnection();
							break;
						}
					} catch (IOException e) {
						e.printStackTrace();
						try {
							stopConnection();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				} else if (gameHadStarted) {
					break;
				} else {
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			System.out.println("Stop");
		});
		receiveThread.start();
	}

	private void firstCall() throws IOException {
		String s;
		String gball;
		OneGamer one;
		try {
			s = br.readLine();
			System.out.println(s);
			one = new Gson().fromJson(s, OneGamer.class);
			dom.initMyCharacter(one.getID(), one.getX(), one.getY());
			app.getSceneRenderEngine().setPlayerPosition(one.getX(), one.getY());
			System.out.println("ID = " + one.getID() + " X= " + one.getX() + " Y = " + one.getY());
			gball = br.readLine();
			System.out.println(gball);
			dom.updateBall(gball);
		} catch (IOException e) {
			e.printStackTrace();
			stopConnection();
		}

	}

	private void initAll() {
		try {
			in = socket.getInputStream();
			out = socket.getOutputStream();
			InputStreamReader isr = new InputStreamReader(in);
			br = new BufferedReader(isr);
			firstCall();
		} catch (IOException e) {
			e.printStackTrace();
			if (socket != null) {
				socket = null;
			}
			if (in != null) {
				in = null;
			}
			if (out != null) {
				out = null;
			}
		}
	}

	private void stopConnection() throws IOException {
		if (socket != null) {
			socket = null;
		}
		if (in != null) {
			in.close();
		}
		if (out != null) {
			out.close();
		}

		receiveThread = null;
		System.out.println("Stop Connection!");
	}
}
