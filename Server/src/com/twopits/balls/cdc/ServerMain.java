package com.twopits.balls.cdc;

import javax.swing.*;

/**
 * Created by dblab2015 on 2015/12/21.
 */
public class ServerMain {
    CentralizedDataCenter cdc;
    UDPBC udpbc;
    RoomSettingThread roomSettingThread;
    JudgeThread judgeThread;
    public static void main(String[] args) {
        new ServerMain();
    }
    public ServerMain() {
        try {
            cdc = new CentralizedDataCenter();
            udpbc = new UDPBC(cdc);
            judgeThread = new JudgeThread(cdc);
            roomSettingThread = new RoomSettingThread(cdc, udpbc,judgeThread);
            roomSettingThread.startRoomSettingThread();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
