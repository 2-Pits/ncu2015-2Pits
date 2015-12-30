package com.twopits.balls.cdc;

import com.google.gson.Gson;

import java.io.*;
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
    ServerSocket ss = null;     // 建立 TCP 伺服器。
    private Thread judgeTh, reciveTh;
    int winner;
    public JudgeThread(CentralizedDataCenter cdc){
        this.cdc=cdc;
        winner=-1;
    }
    public void setVector(Vector<Socket> skvector){
        this.skvector = skvector;
    }
    public void setServerSocket(ServerSocket ss){
        this.ss = ss;
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
        //    System.out.println(skvector.elementAt(i).getInetAddress());
            try {
                OutputStream os=skvector.elementAt(i).getOutputStream();
                PrintWriter printWriter = new PrintWriter(os);
                printWriter.print(String.valueOf(winner) + "\n");
                printWriter.flush();

                String s = new Gson().toJson(cdc.getBallMap());
                printWriter.print(s + "\n");
                printWriter.flush();

                System.out.printf("winner : %s , %s \n", String.valueOf(winner), s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    Runnable recieveRunnable = new Runnable() {
        @Override
        public void run() {
          //  System.out.println("TCPKeyopt Thread");
          //  System.out.printf("socket num : %d" , skvector.size());
            for(int i=0;i<skvector.size();i++) {
                new Thread(new clientService(skvector.elementAt(i))).start();
            }
        }
    };

    class clientService implements Runnable{

        Socket socket;
        byte[] b;

        public clientService(Socket s){
            socket = s;
        }

        @Override
        public void run() {
          //  System.out.println("run clientService");
            while (true) {
                try {
                    InputStream is=socket.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
                    if(bufferedReader.ready()){
                        String s = bufferedReader.readLine();
                        s = s.trim();
                        System.out.println(s);
                        KeyOpt temp = new Gson().fromJson(s, KeyOpt.class);
                        cdc.getOptQueue().add(temp);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

}