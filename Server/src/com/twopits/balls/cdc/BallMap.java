package com.twopits.balls.cdc;

import java.awt.event.KeyEvent;

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
    public int winnerScan(){
        for(int i=0;i<4;i++){
            if(item[i][0]>0){
                if(item[i][0]==item[i][1]&&item[i][0]==item[i][2]&&item[i][0]==item[i][3]){
                    return i;
                }
            }
        }
        return -1;
    }
    public void exchangeBall(int x,int y,int ID,int keycode){

        System.out.printf("ID : %d, keycode : %d, x : %d, y : %d   \n", ID, keycode, x, y);

        int key = 0;
        switch(keycode){
            case KeyEvent.VK_Q:
                System.out.println("press Q");
                key = 0;
            break;
            case KeyEvent.VK_W:
                System.out.println("press W");
                key = 1;
            break;
            case KeyEvent.VK_E:
                System.out.println("press E");
                key = 2;
            break;
            case KeyEvent.VK_R:
                System.out.println("press R");
                key = 3;
            break;
        }

        System.out.printf("item : %d, court : %d\n", item[ID][key], court[x][y]);

        int temp=item[ID][key];
        item[ID][key]=court[x][y];
        court[x][y]=temp;
    }
}
