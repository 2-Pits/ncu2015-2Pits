package com.twopits.balls;

import com.google.gson.Gson;
import com.twopits.balls.libs.Constants;
import com.twopits.balls.libs.OneGamer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import dom.DynamicObjectModule;

/**
 * UDP networking
 * Created by DBLAB on 2015/12/21.
 */
public class UDPUS {

	private DatagramPacket sendPacket;
	private DatagramPacket receiveMultiPacket;
	private DatagramSocket sendSocket;
	private DatagramSocket receiveMultiSocket;
	private Thread sendThread, receiveThread;
	private byte[] sendByteArr;
	private DynamicObjectModule dom;

	public UDPUS(DynamicObjectModule dom) {
		this.dom = dom;
	}

	public void iniUDPServer() {
		createSocket();
		newThread();
	}

	private void newThread() {
		sendThread = new Thread(sendRunnable);
		receiveThread = new Thread(receiveRunnable);
	}

	public void runReceiveThread() {
		receiveThread.start();
	}

	public void runSendThread() {
		sendThread.start();
	}

	private void createSocket() {
		byte buff[] = new byte[Constants.UDP_PACKET_LENGTH];
		sendByteArr = new byte[Constants.UDP_PACKET_LENGTH];
		try {
			InetAddress serVerAddress = InetAddress.getByName(Constants.SERVER_IP);
			sendSocket = new DatagramSocket();
			sendPacket = new DatagramPacket(sendByteArr, Constants.UDP_PACKET_LENGTH, serVerAddress,
					Constants.UDP_PORT);
			receiveMultiPacket = new DatagramPacket(buff, Constants.UDP_PACKET_LENGTH);
			receiveMultiSocket = new DatagramSocket(Constants.MULTI_BROADCAST_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String decode(byte[] b) {
		String s;
		s = new String(b);
		return s;
	}

	Runnable receiveRunnable = new Runnable() {
		byte[] b;
		String s;

		@Override
		public void run() {
			while (true) {
				try {
					receiveMultiSocket.receive(receiveMultiPacket);
					b = receiveMultiPacket.getData();
					s = decode(b);
					s = s.trim();
					dom.downloadCharacter(s);

					Thread.sleep(50);
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};

	Runnable sendRunnable = new Runnable() {
		int tempID = 0;
		int tempFaceTo = 0;
		double tempX = 0;
		double tempY = 0;
		OneGamer myData;

		@Override
		public void run() {
			while (true) {
				Arrays.fill(sendByteArr, (byte) 0);
				sprite.Character character = dom.updateMyPosition();
				tempID = character.getID();
				tempFaceTo = character.getDirection();
				tempY = character.getY();
				tempX = character.getX();
				myData = new OneGamer(tempID, tempX, tempY, tempFaceTo);
				String tempS = new Gson().toJson(myData);
				byte[] bytes = tempS.getBytes(StandardCharsets.UTF_8);
				System.arraycopy(bytes, 0, sendByteArr, 0, bytes.length);
				try {
					sendSocket.send(sendPacket);
					Thread.sleep(50);
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};
}
