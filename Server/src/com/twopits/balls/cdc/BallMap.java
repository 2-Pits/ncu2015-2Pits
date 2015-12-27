package com.twopits.balls.cdc;

/**
 * Created by Lenovo on 2015/12/18.
 */
public class BallMap {
    private int court[][]=new int[10][10];
    private int item[][]=new int[4][4];
    public BallMap(){
        int randmize[] =new int [100];
        int value[] =new int [100];
        for(int i=0;i<100;i++){
            randmize[i]=(int)(Math.random()*5000);
            value[i]=0;
        }
        for(int i=0;i<=10;i++){
            for(int j=0;j<6;j++){
                value[i*6+j]=i;
            }
        }
        for(int i=0;i<100;i++){
            for(int j=i+1;j<100;j++) {
                if (randmize[i] > randmize[j]) {
                    int temp = randmize[i];
                    randmize[i] = randmize[j];
                    randmize[j] = temp;
                    temp = value[i];
                    value[i] = value[j];
                    value[j] = temp;
                }
            }
        }
        for(int i=0;i<100;i++){
            court[i/10][i%10]=value[i];
        }
        for(int i=0;i<16;i++){
            item[i/4][i%4]=0;
        }
    }
}
