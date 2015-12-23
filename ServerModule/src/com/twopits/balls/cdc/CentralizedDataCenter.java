package com.twopits.balls.cdc;

import java.util.Vector;

/**
 * Created by Lenovo on 2015/12/18.
 */
public class CentralizedDataCenter implements CentralizedDataCenterInterface {

    private Vector<Player> playerMap;
    private Vector<Ball> ballMap;
    public CentralizedDataCenter(){
        playerMap = new Vector<>();
        ballMap = new Vector<>();
    }
    public Vector getPlayerMap(){
        return playerMap;
    }
    public int getPlayerMapCount(){
        return playerMap.size();
    }

    public Player addPlayer() {
        if(getPlayerMapCount()==4){
            return null;
        }
        int x=0,y=0,dir=0;
        Player player = new Player(playerMap.size(),x,y,dir);
        playerMap.add(player);
        return player;
    }
    public void updateCharacterStatus(int clientno,double x,double y,int dir){
        Player temp = findPlayer(clientno);
        temp.updateInfo(x,y,dir);
    }
    public Player findPlayer(int clientno){
        for(int i = 0; i< playerMap.size(); i++){
            if(playerMap.elementAt(i).getClientno()==clientno) {
                return playerMap.elementAt(i);
            }
        }
        return null;
    }

    boolean addBall(int color,int index,int x,int y) {
        Ball ball = new Ball(color,index,x,y);
        ballMap.add(ball);
        return true;
    }
    Ball findBall(int index){
        for(int i=0;i<ballMap.size();i++){
            if(ballMap.elementAt(i).getIndex()==index) {
                return ballMap.elementAt(i);
            }
        }
        return null;
    }

    void startUpdatingThread() throws InterruptedException {

    }

}
