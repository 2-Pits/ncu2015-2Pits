package com.twopits.balls.cdc;

/**
 * Created by dblab2015 on 2015/12/21.
 */
public class JudgThread extends Thread {
    long mSleepDuration;
    CentralizedDataCenter cdc;
    public JudgThread(CentralizedDataCenter cdc){
        this.cdc=cdc;
    }
    public long getCurrentSleepDuration() {
        return mSleepDuration;
    }
    public void startJudgThread() {
        this.start();
    }
    @Override
    public void run() {
        super.run();
        // noinspection InfiniteLoopStatement
        while (true) {

        }
    }
}