package com.twopits.balls.cdc;

import javax.swing.*;

/**
 * Created by dblab2015 on 2015/12/21.
 */
public class ServerMain {
    CentralizedDataCenter cdc;
    UDPBC udpbc;
    RoomSettingThread roomSettingThread;
    public static void main(String[] args) {
        new ServerMain();
    }
    public ServerMain() {
        try {

            JFrame window = new JFrame();
            cdc = new CentralizedDataCenter();
            udpbc = new UDPBC(cdc);
            roomSettingThread = new RoomSettingThread(cdc, udpbc);
            roomSettingThread.startRoomSettingThread();
       //     TCPBall tcpServer = new TCPBall(cdc);
       //     tcpServer.initTCPServer();
        //    cdc.addPlayer();
        //    cdc.addPlayer();
        //    cdc.addPlayer();
         //   cdc.addPlayer();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
