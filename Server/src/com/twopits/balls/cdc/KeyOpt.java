package com.twopits.balls.cdc;

import java.security.Key;

/**
 * Created by dblab2015 on 2015/12/28.
 */
public class KeyOpt {
    private int ID;
    private int keyCode;
    public KeyOpt(int ID,int keyCode){
        this.ID=ID;
        this.keyCode=keyCode;
    }
    public int getID(){
        return ID;
    }
    public int getkeyCode(){
        return keyCode;
    }
}
