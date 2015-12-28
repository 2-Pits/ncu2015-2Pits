package com.twopits.balls;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.twopits.balls.libs.Constants;
import com.twopits.balls.libs.OneGamer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import dom.DynamicObjectModule;

/**
 * Created by DBLAB on 2015/12/21.
 */
public class UDPUS {



    private DatagramPacket sendPacket;
    private DatagramPacket reciveMultiPacket;
    private DatagramSocket sendSocket;
    private MulticastSocket reciveMultiSocket;
    private Thread sendTh, reciveTh;
    private InetAddress multiGroup, myAddress;
    private byte[] sendByteArr;
    private DynamicObjectModule dom;
    private sprite.Character character;

    public UDPUS(DynamicObjectModule dom) {
        this.dom = dom;
    }

    public void iniUDPServer() {
        createSocket();
        newThread();
    }

    public JsonObject updateItem() {
        JsonObject json = new JsonObject();
        return json;
    }

    public void setPostion(JsonObject json) {

    }

    private void newThread() {
        sendTh = new Thread(sendRunnable);
        reciveTh = new Thread(reciveRunnable);
    }

    public void runReciveThread() {
        reciveTh.start();
    }

    public void runSendThread() {
        sendTh.start();
    }

    private void createSocket() {
        byte buff[] = new byte[Constants.PACKETLENGTH];
        sendByteArr = new byte[Constants.PACKETLENGTH];
        try {
            multiGroup = InetAddress.getByName(Constants.MULTIBORADCASTIP);
            myAddress = InetAddress.getByName(Constants.SERVERIP);
            sendSocket = new DatagramSocket();
            sendPacket = new DatagramPacket(sendByteArr, Constants.PACKETLENGTH, myAddress, Constants.PORT);
            reciveMultiPacket = new DatagramPacket(buff, Constants.PACKETLENGTH);
            reciveMultiSocket = new MulticastSocket(Constants.MULTIBORADCASTPORT);
            reciveMultiSocket.joinGroup(multiGroup);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String decode(byte[] b) {
        String s;
        s = new String(b);
        return s;
    }

    Runnable reciveRunnable = new Runnable() {
        byte[] b;
        String s;

        @Override
        public void run() {
            while (true) {

                try {
                    reciveMultiSocket.receive(reciveMultiPacket);
                    b = reciveMultiPacket.getData();
                    s = decode(b);
                    s = s.trim();
                    dom.downloadCharacter(s);

                    Thread.sleep(50);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
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
                character = dom.updateMyPosition();
                tempID = character.getID();
                tempFaceTo = character.getDirection();
                tempY = character.getY();
                tempX = character.getX();
                myData = new OneGamer(tempID, tempX, tempY, tempFaceTo);
                String tempS = new Gson().toJson(myData);
                byte[] bytes = tempS.getBytes(StandardCharsets.UTF_8);
                for (int i = 0; i < bytes.length; i++) {
                    sendByteArr[i] = bytes[i];
                }
                try {
                    sendSocket.send(sendPacket);
                    Thread.sleep(50);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
