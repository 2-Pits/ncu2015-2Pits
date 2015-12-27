package com.twopits.balls.libs;

/**
 * Created by DBLAB on 2015/12/21.
 */
public class OneGamer {

    private int ID = 0;
    private int dir = 0;
    private double x = 0;
    private double y = 0;

    public OneGamer(int id,double x,double y,int dir){
        ID = id;
        this.dir = dir;
        this.x = x;
        this.y = y;
    }
    public int getID(){
        return ID;
    }
    public int getDir(){
        return dir;
    }
    public double getX(){
        return x;
    }
    public double getY(){
        return y;
    }
}
