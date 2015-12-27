package com.twopits.balls;

/**
 * Created by DBLAB on 2015/12/27.
 */
public class BallMap {
    private int court[][]=new int[10][10];
    private int item[][]=new int[4][4];

    public int[][] getCourt(){
        return court;
    }

    public int[][] getItem(){
        return item;
    }
}
