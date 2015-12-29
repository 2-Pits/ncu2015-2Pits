package com.twopits.balls;

import com.google.gson.Gson;
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
import java.net.UnknownHostException;

import dom.DynamicObjectModule;
import sprite.Character;

/**
 * Created by DBLAB on 2015/12/27.
 */
public class TCPCM {

    private final int PORT = 5278;
    private final String IP = "127.0.0.1";

    private DynamicObjectModule dom;
    private App app;
    private InetAddress ServerIP;
    private Socket socket;
    private Thread recieveThread, sendThread;
    private InputStream in;
    private OutputStream out;
    private BufferedReader br;
    private InputStreamReader isr;
    private int clientCount = 0 ;
    private boolean isLoading = true;

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

    public int getClientCount(){
        return clientCount;
    }
    public void setLoadingStatus(boolean status){ isLoading = status;}

    //���ݪ�l���O
    public void startrecieveInitThread() {

    }

    //���ݶ}�l�C��
    public void startrecieveStartGameThread() {

    }
    //�ШDGetBall���O

    public void requestGetBall(String s) {

    }

    //�����y���A
    public void startUpdateRecieveBallStatus() {

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

    private void stopConnection() throws IOException {
        if (socket != null) socket = null;
        if (in != null) in.close();
        if (out != null) out.close();

    }

    Runnable send = new Runnable() {
        @Override
        public void run() {

        }
    };

    Runnable recieve = new Runnable() {

        boolean isStart = false;
        boolean isStop = false;
        String s;

        @Override
        public void run() {

            if (!dom.startMove()) {
                try {
                    while (clientCount <= 4) {
                        clientCount = br.read();
                        System.out.println("Client Count = " + clientCount);
                        if (!isLoading) {
                            break;
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            while (dom.startMove()) {
                System.out.println("enter");
                String s;
                String gball;
                try {
                    s = br.readLine();
                    gball = br.readLine();
                    System.out.println(gball);
                    dom.updateBall(gball);
                    if (Integer.valueOf(s) != -1) {
                        //If winner showup, then close connection and show winner.
                        System.out.println("Winner is : " + s + "\n");
                        dom.endGame(s);

                        stopConnection();
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    };

}
