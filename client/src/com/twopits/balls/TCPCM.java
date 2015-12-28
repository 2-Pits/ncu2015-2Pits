package com.twopits.balls;

import com.google.gson.Gson;
import com.twopits.balls.libs.OneGamer;
import dom.DynamicObjectModule;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by DBLAB on 2015/12/27.
 */
public class TCPCM {

    private final int PORT = 5278;
    private final String IP = "140.115.155.92";

    private DynamicObjectModule dom;
    private App app;
    private InetAddress ServerIP;
    private Socket socket;
    private Thread recieveThread, sendThread;
    private InputStream in;
    private OutputStream out;
    private BufferedReader br;
    private InputStreamReader isr;

    public TCPCM(App app, DynamicObjectModule dom) {
        this.dom = dom;
        this.app = app;
    }

    public void buildConnection() {
        try {
            ServerIP = InetAddress.getByName(IP);
            socket = new Socket(ServerIP, PORT);
            System.out.println("Create Socket");
            initAll();
            createThread();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //等待初始指令
    public void startrecieveInitThread(){

    }
    //等待開始遊戲
    public void startrecieveStartGameThread(){

    }
    //請求GetBall指令

    public void requestGetBall(String s){

    }
    //接收球狀態
    public void startUpdateRecieveBallStatus(){

    }

    private void createThread() {
        recieveThread = new Thread(recieve);
        sendThread = new Thread(send);

        recieveThread.start();
    }

    private void firstCall() {
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
        }

    }



    private void initAll() {
        try {
            in = socket.getInputStream();
            out = socket.getOutputStream();
            isr = new InputStreamReader(in);
            br = new BufferedReader(isr);
            firstCall();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    Runnable send = new Runnable() {
        @Override
        public void run() {

        }
    };

    Runnable recieve = new Runnable() {

        boolean isStart = false;
        boolean isStop = false;
        int clientCount;
        String s;

        @Override
        public void run() {

            if (!isStart) {
                try {
                    while (clientCount <= 4) {
                        clientCount = br.read();
                        System.out.println("Client Count = " + clientCount);
                        if (clientCount == 4) {
                            app.getRenderThread().start();
                            isStart = true;
                            break;
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {

                while (!isStop) {

                }
            }

        }
    };

}
