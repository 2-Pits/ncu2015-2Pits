package com.twopits.balls.cdc;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Vector;

/**
 * Created by dblab2015 on 2015/12/21.
 */
public class JudgeThread {
    public static int port = 5278; // 連接埠
    long mSleepDuration;
    CentralizedDataCenter cdc;
    Vector<Socket> skvector;
    private Thread judgeTh, reciveTh;
    int winner;
    public JudgeThread(CentralizedDataCenter cdc){
        this.cdc=cdc;
        winner=-1;
    }
    public void setVector(Vector<Socket> skvector){
        this.skvector = skvector;
    }
    public long getCurrentSleepDuration() {
        return mSleepDuration;
    }
    public void startJudgeThread() {
        judgeTh=new Thread(judgeRunnable);
        judgeTh.start();
        reciveTh=new Thread(recieveRunnable);
        reciveTh.start();
    }
    Runnable judgeRunnable = new Runnable() {
        @Override
        public void run() {
            while (winner < 0) {
                if (cdc.getOptQueue().isEmpty()) {
                    continue;
                }
                KeyOpt tempkeyopt = cdc.getOptQueue().remove(); // ID qwer
                cdc.getBallMap().exchangeBall((int) cdc.findPlayer(tempkeyopt.getID()).getX() / 100,
                        (int) cdc.findPlayer(tempkeyopt.getID()).getY() / 100,
                        tempkeyopt.getID(),
                        tempkeyopt.getkeyCode());
                winner = cdc.getBallMap().winnerScan();
                sendBallStatus(winner);
            }
        }
    };
    public void sendBallStatus(int winner) {
        for(int i=0;i<skvector.size();i++) {
            System.out.println(skvector.elementAt(i).getInetAddress());
            try {
                OutputStream os=skvector.elementAt(i).getOutputStream();
                os.write(winner);// 送訊息到 Client 端。
                os.flush();
                os.write('\n');// 送訊息到 Client 端。
                os.flush();
                String s = new Gson().toJson(cdc.getBallMap());
                byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
                os.write(bytes);// 送訊息到 Client 端。
                os.flush();
                os.write('\n');// 送訊息到 Client 端。
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    Runnable recieveRunnable = new Runnable() {
        byte[] b;
        String s;

        @Override
        public void run() {
            ServerSocket ss = null;     // 建立 TCP 伺服器。
            try {
                ss = new ServerSocket(port);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("TCPKeyopt Thread");
            while (true) {
                Socket sc = null;                // 接收輸入訊息。
                try {
                    sc = ss.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Waiting!!");
                InputStream is = null;    // 取得輸出串流。
                try {
                    is=sc.getInputStream();
                    is.read(b);
                    s = new String(b);
                    s = s.trim();
                    KeyOpt temp = new Gson().fromJson(s, KeyOpt.class);
                    cdc.getOptQueue().add(temp);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    };
}