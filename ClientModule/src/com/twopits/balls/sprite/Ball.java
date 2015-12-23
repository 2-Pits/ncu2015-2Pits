package com.twopits.balls.sprite;

/**
 * Created by dblab on 2015/12/14.
 */
public class Ball extends Character{
    private String name;
    private Boolean shared;
    private int index;
    private int owner;

    public Ball(String name, int index, boolean shared,int x, int y){
        super(x,y);
        this.name = name;
        this.index = index;
        this.shared = shared;
        this.owner = -1;
        initBall();
    }

    private void initBall(){
        loadImage("resource/room_dark.png");
    }

    public void setShared(boolean b){
        this.shared = b;
    }

    public void setOwner(int o){
        this.owner = o;
    }

    public int getOwner(){
       return this.owner;
    }

    public String getName(){
        return this.name;
    }

    public boolean isShared(){
        return this.shared;
    }

    public int getIndex(){
        return this.index;
    }
}
