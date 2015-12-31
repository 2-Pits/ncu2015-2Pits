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
        while(true) {
            ServerMain serverMain= new ServerMain();
            System.out.println("Server Online");
            while(!serverMain.getGameEnd()){
                continue;
            }
            try {
                serverMain.finalize();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }
    public boolean getGameEnd(){
        return judgeThread.getGameEnd();
    }
    public ServerMain() {
        try {
            cdc = new CentralizedDataCenter();
            judgeThread = new JudgeThread(cdc);
            udpbc = new UDPBC(cdc,judgeThread);
            roomSettingThread = new RoomSettingThread(cdc, udpbc,judgeThread);
            roomSettingThread.startRoomSettingThread();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
