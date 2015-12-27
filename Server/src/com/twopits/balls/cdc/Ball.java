package com.twopits.balls.cdc;

/**
 * Created by Lenovo on 2015/12/18.
 */
public class Ball {
    private int color;
    private int index;
    private int x;
    private int y;
    private int owner;
    public Ball(int color,int index,int x,int y){
        this.x=x;
        this.y=y;
        this.index=index;
        this.color=color;
        owner=-1;
    }
    public void setXY(int x,int y){
        this.x=x;
        this.y=y;
    }
    public int getColor(){
        return this.color;
    }
    public int getIndex(){
        return this.index;
    }
    public void setOwner(int owner){
        this.owner=owner;
    }
    public int getOwner(){
        return this.owner;
    }
    public void setX(int x){
        this.x=x;
    }
    public int getX(){
        return this.x;
    }
    public void setY(int y){
        this.y=y;
    }
    public int getY(){
        return this.y;
    }
    @Override
    public String toString() {
        String s="";
        return s;
    }
}
