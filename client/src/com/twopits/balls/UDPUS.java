package com.twopits.balls;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.twopits.balls.libs.OneGamer;
import dom.DynamicObjectModule;
import sprite.*;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Created by DBLAB on 2015/12/21.
 */
public class UDPUS {

    private final int PORT = 45368;
    private final int MULTIBORADCASTPORT = 45369;
    private final String MULTIBORADCASTIP = "239.255.255.255";
    private final String MYCLIENTIP = "140.115.155.92";
    private final int PACKETLENGTH = 1000;

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
        byte buff[] = new byte[PACKETLENGTH];
        sendByteArr = new byte[PACKETLENGTH];
        try {
            multiGroup = InetAddress.getByName(MULTIBORADCASTIP);
            myAddress = InetAddress.getByName(MYCLIENTIP);
            sendSocket = new DatagramSocket();
            sendPacket = new DatagramPacket(sendByteArr, PACKETLENGTH, myAddress, PORT);
            reciveMultiPacket = new DatagramPacket(buff, PACKETLENGTH);
            reciveMultiSocket = new MulticastSocket(MULTIBORADCASTPORT);
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

                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
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
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
