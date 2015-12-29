
package com.twopits.balls.cdc;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Vector;

/**
 * Created by DBLAB on 2015/12/21.
 */
public class UDPBC {

    private final int PORT = 45368;
    private final int MULTIBORADCASTPORT = 45369;
    private final int CLIENTCOUNT = 4;
    private final String MULTIBORADCASTIP = "239.255.255.255";
    private final String SERVERIP = "127.0.0.1";
    private final int PACKETLENGTH = 1000;

    private DatagramPacket recivePacket;
    private DatagramPacket sendMultiPacket[] =new DatagramPacket[CLIENTCOUNT];
    private DatagramSocket reciveSocket;
    private DatagramSocket sendMultiSocket;
    private Thread sendTh, reciveTh;
    private InetAddress multiGroup;
    private Vector<Player> allPlayerMap = new Vector<Player>();
    private Vector<InetAddress> ipTable = new Vector<InetAddress>();
    private byte[] sendByteArr;
    private int[] client_ID;
    private int[] diration;
    private double[] position_X;
    private double[] position_Y;
    private int my_ID;
    private int myDiration;
    private double myPosition_X;
    private double myPosition_Y;
    private CentralizedDataCenter cdc;

    public UDPBC(CentralizedDataCenter cdc) {
        this.cdc = cdc;
    }

    public void startUDPBroadCast() {
        ini();
        createSocket();
        newThread();
    }

    public void runRecieveThread() {
        reciveTh.start();
    }

    public void runSendThread() {
        sendTh.start();
    }

    private String decode(byte[] b) {
        String s;
        s = new String(b);
        return s;
    }

    private void newThread() {
        reciveTh = new Thread(recieveRunnable);
        sendTh = new Thread(sendRunnable);
    }

    private void ini() {
        client_ID = new int[CLIENTCOUNT];
        position_X = new double[CLIENTCOUNT];
        position_Y = new double[CLIENTCOUNT];
        diration = new int[CLIENTCOUNT];
        allPlayerMap = new Vector<Player>();

    }

    public void setClientIP(Vector<InetAddress> ip){
        ipTable = ip;

    }

    private void createSocket() {
        byte[] buff = new byte[PACKETLENGTH];
        sendByteArr = new byte[PACKETLENGTH];
        try {
//            multiGroup = InetAddress.getByName(MULTIBORADCASTIP);
            sendMultiSocket = new DatagramSocket();
            sendMultiSocket.setBroadcast(true);
            for(int i=0;i<4;i++){
                //System.out.println(ipTable.elementAt(i));
                sendMultiPacket[i] = new DatagramPacket(sendByteArr,PACKETLENGTH,ipTable.elementAt(i)
                        ,MULTIBORADCASTPORT);
            }
//            sendMultiSocket.joinGroup(multiGroup);
//            sendMultiPacket = new DatagramPacket(sendByteArr, PACKETLENGTH);
//            sendMultiPacket.setPort(MULTIBORADCASTPORT);
//            sendMultiPacket.setAddress(multiGroup);

            reciveSocket = new DatagramSocket(PORT);
            recivePacket = new DatagramPacket(buff, PACKETLENGTH);

        }
//        catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    Runnable recieveRunnable = new Runnable() {
        byte[] b;
        String s;

        @Override
        public void run() {


            while (true) {
                try {
                    reciveSocket.receive(recivePacket);
                    b = recivePacket.getData();
                    s = decode(b);
                    s = s.trim();

                    Player gamer = new Gson().fromJson(s, Player.class);

                    my_ID = gamer.getID();
                    myDiration = gamer.getDir();
                    myPosition_X = gamer.getX();
                    myPosition_Y = gamer.getY();
                    cdc.updateCharacterStatus(my_ID, myPosition_X, myPosition_Y, myDiration);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    Runnable sendRunnable = new Runnable() {
        @Override
        public void run() {


            while (true) {
                Arrays.fill(sendByteArr,(byte)0);
                allPlayerMap =cdc.getPlayerMap();
                String s="";
                for(int i=0;i<allPlayerMap.size();i++) {
                    s += new Gson().toJson(allPlayerMap.elementAt(i)) + ";";
                }
                byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
                for(int i=0;i<bytes.length;i++){
                    sendByteArr[i] = bytes[i];
                }

                try {
                    for(int i=0;i<4;i++){
                        sendMultiSocket.send(sendMultiPacket[i]);
                       // System.out.println("SEND " + i);
                    }

                 /*   for(Player each : allPlayerMap){
                       // System.out.println(each.getID()+"("+each.getX()+","+each.getY()+")"+" dir:"+each.getDir());
                    }*/
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

}
