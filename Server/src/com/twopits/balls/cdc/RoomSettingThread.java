package com.twopits.balls.cdc;


import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Vector;

/**
 * Created by dblab2015 on 2015/12/21.
 */
public class RoomSettingThread  extends Thread  { // Waiting for four Ch
    long mSleepDuration;
    public static int port = 5278; // 連接埠
    ServerSocket ss = null;     // 建立 TCP 伺服器。
    Vector<InetAddress> ipvector;
    Vector<Socket> skvector;
    CentralizedDataCenter cdc;
    UDPBC udpbc;
    JudgeThread judgeThread;
    public RoomSettingThread(CentralizedDataCenter cdc,UDPBC udpbc,JudgeThread judgeThread){
        this.cdc=cdc;
        this.udpbc = udpbc;
        this.judgeThread=judgeThread;
        ipvector=new Vector<InetAddress>();
        skvector=new  Vector<Socket>();
    }
    public Vector<Socket> getskvector(){
        return skvector;
    }
    public long getCurrentSleepDuration() {
        return mSleepDuration;
    }
    public void startRoomSettingThread() {
        this.start();
    }
    public ServerSocket getServerSocket(){
        return ss;
    }
    @Override
    public void run() {
        super.run();
        // noinspection InfiniteLoopStatement
        try {
            ss = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("TCPCon Thread");

        while (true) {
            System.out.println(skvector.size());
            for(int i=0;i<skvector.size();i++) {
                System.out.println(skvector.elementAt(i).getInetAddress());
                try {
                    skvector.elementAt(i).getOutputStream().write(skvector.size());
                    skvector.elementAt(i).getOutputStream().flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Socket sc = null;                // 接收輸入訊息。
            try {
                sc = ss.accept();
                ipvector.add(sc.getInetAddress());
                skvector.add(sc);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Waiting!!");
            OutputStream os = null;    // 取得輸出串流。
            try {
                os = sc.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String s="";
            s = new Gson().toJson(cdc.addPlayer());
            byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
            System.out.println(s);
            try {
                os.write(bytes);// 送訊息到 Client 端。
                os.flush();
                os.write('\n');// 送訊息到 Client 端。
                os.flush();
                s = new Gson().toJson(cdc.getBallMap());
                bytes = s.getBytes(StandardCharsets.UTF_8);
                os.write(bytes);// 送訊息到 Client 端。
                os.flush();
                os.write('\n');// 送訊息到 Client 端。
                os.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (cdc.getPlayerMapCount() == 4) break;
        }
        System.out.println(cdc.getPlayerMapCount());
        udpbc.startUDPBroadCast();
        udpbc.runSendThread();
        udpbc.runRecieveThread();
        for(int i=0;i<skvector.size();i++) {
            try {
                skvector.elementAt(i).getOutputStream().write(skvector.size());
                skvector.elementAt(i).getOutputStream().flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        judgeThread.setVector(skvector);
        judgeThread.setServerSocket(ss);
        judgeThread.startJudgeThread();
    }
}
