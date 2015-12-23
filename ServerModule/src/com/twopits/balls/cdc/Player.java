package com.twopits.balls.cdc;


/**
 * Created by Lenovo on 2015/12/18.
 */
public class Player {

    private int clientno;
    private double x, y;
    private int dir;
    public Player(int clientno, double x, double y, int dir) {
        this.clientno=clientno;
        this.x=x;
        this.y=y;
        this.dir=dir;
    }
    public void setClientno(int clientno){
        this.clientno=clientno;
    }
    public int getClientno(){
        return this.clientno;
    }
    public void updateInfo(double x,double y, int dir){
        this.x=x;
        this.y=y;
        this.dir=dir;
    }
    public void setX(int x){
        this.x=x;
    }
    public double getX(){
        return this.x;
    }
    public void setY(int y){
        this.y=y;
    }
    public double getY(){
        return this.y;
    }
    public void setDir(int dir){
        this.dir=dir;
    }
    public int getDir(){
        return this.dir;
    }
    @Override
    public String toString() {
     String s="";
        return s;
    }
}
