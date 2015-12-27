package com.twopits.balls.cdc;


import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStream;
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
    Vector<OutputStream> osvector;
    CentralizedDataCenter cdc;
    UDPBC udpbc;
    public RoomSettingThread(CentralizedDataCenter cdc,UDPBC udpbc){
        this.cdc=cdc;
        this.udpbc = udpbc;
        osvector=new Vector<>();
    }
    public long getCurrentSleepDuration() {
        return mSleepDuration;
    }
    public void startRoomSettingThread() {
        this.start();
    }
    public void sendBallStatus() {
        this.start();
    }
    @Override
    public void run() {
        super.run();
        // noinspection InfiniteLoopStatement
        ServerSocket ss = null;     // 建立 TCP 伺服器。
        try {
            ss = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("TCPCon Thread");

        while (true) {
            Socket sc = null;                // 接收輸入訊息。
            try {
                sc = ss.accept();
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
            osvector.add(os);
            String s="";
            s = new Gson().toJson(cdc.addPlayer());
            byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
            System.out.println(s);
            try {
                os.write(bytes);// 送訊息到 Client 端。
                os.close();                                // 關閉輸出串流。
                sc.close();                                // 關閉 TCP 伺服器。

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (cdc.getPlayerMapCount() == 4) break;
        }
        System.out.println(cdc.getPlayerMapCount());
        udpbc.startUDPBroadCast();
        udpbc.runSendThread();
        udpbc.runRecieveThread();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
