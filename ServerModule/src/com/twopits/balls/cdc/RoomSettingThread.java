package com.twopits.balls.cdc;


/**
 * Created by dblab2015 on 2015/12/21.
 */
public class RoomSettingThread  extends Thread  { // Waiting for four Ch
    long mSleepDuration;
    CentralizedDataCenter cdc;
    UDPBC udpbc;
    public RoomSettingThread(CentralizedDataCenter cdc,UDPBC udpbc){
        this.cdc=cdc;
        this.udpbc = udpbc;
    }
    public long getCurrentSleepDuration() {
        return mSleepDuration;
    }
    public void startRoomSettingThread() {
        this.start();
    }
    @Override
    public void run() {
        super.run();
        // noinspection InfiniteLoopStatement
        while (true) {
            System.out.println(cdc.getPlayerMapCount());
            if (cdc.getPlayerMapCount() == 4) break;
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(cdc.getPlayerMapCount());
        udpbc.startUDPBroadCast();
        udpbc.runRecieveThread();
        udpbc.runSendThread();
    }
}
