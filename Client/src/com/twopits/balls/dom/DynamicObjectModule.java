package com.twopits.balls.dom;

import com.google.gson.Gson;
import jdk.nashorn.api.scripting.JSObject;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.twopits.balls.sprite.Character;
import java.awt.*;
import java.util.ArrayList;


/**
 * Created by dblab on 2015/12/14.
 */
public class DynamicObjectModule implements DOMInterface{

    private Character mainChar;
    private ArrayList<Character> other;
    //private TCPCM tcp;

    public DynamicObjectModule(){
        other = new  ArrayList<Character>();
        //this.tcp = tcp;
    }

    public void initMyCharacter(int ID){
        mainChar = new Character(0,0);
        mainChar.setID(ID);
    }

    public void initMyCharacter(int ID,double x, double y){
        /*Init all Character.
          Extract Gson and translate it.
        Call by TCP .*/

       // Character otherChar = new Character();
       // other.add(otherChar);
       // mainChar.setID(ID);
       // other.add(otherChar);
    }

    public Character getMyCharacter(){
        return mainChar;
    }

    public Character updateMyPosition(){
        Character me = new Character(mainChar.getX(),mainChar.getY());
        me.setDirection(mainChar.getDirection());
        me.setID(mainChar.getID());
        return mainChar;
    }

    @Override
    public void downloadCharacter(String gson) {

    }

    public void getOtherPosition(int clientno){
        Character otherChar = new Character(100*clientno, 100*clientno);
        other.add(otherChar);
    }

    public void downloadCharacter(Gson all){
        /* called by UDP
           get all character information from UDP*/
       /* all.
        for(int i = 0; i < other.size(); i++){
            if(other.get(i).getID() == clientno){
                other.get(i).setPosition(x,y);
                other.get(i).setDirection(dir);
            }
        }*/
    }

    public ArrayList<Character> getOtherCharacter(){
        /* call by ScenRenderEngine
           get all character object.*/

        return other;
    }

    /*public void keyGETPressed(){
         called by UIM
           When UIM accepts an keyboard input and it is a GET key
           it calls this method.
           This method should determine if the GET action is possible
           by comparing the virtual character��s position and any
           item nearby. If the GET action is possible, it should call
           inputMoves(GET) of TCPCM
        */
        /*int GET = 0;
        Position charPosi = getMainCharCenterPoint();
        for(int i = 1; i < object.size(); i++){
            Ball ball = (Ball) object.get(i);
            if(charPosi.getX() > ball.getX() && charPosi.getX() < (ball.getX() + ball.getWidth()) ){
                if(characters[0].getDirection() == 0){  //DOWN
                    if(characters[0].getY() < ball.getY() && (characters[0].getY() + characters[0].getHeight()) >= ball.getY()){
                       // tcp.inputMoves(GET);
                    }
                }if(characters[0].getDirection() == 12){  //UP
                    if(characters[0].getY() > ball.getY() && (characters[0].getY() <= (ball.getY() + ball.getHeight()))){
                        // tcp.inputMoves(GET);
                    }
                }

            }else if(charPosi.getY() > ball.getY() && charPosi.getY() < (ball.getY() + ball.getHeight())){
                if(characters[0].getDirection() == 4){  //LEFT
                    if(characters[0].getX() > ball.getX() && (characters[0].getX() <= (ball.getX() + ball.getWidth()))){
                       // tcp.inputMoves(GET);
                    }
                }if(characters[0].getDirection() == 8){  //RIGHT
                    if(characters[0].getX() < ball.getX() && (characters[0].getX() + characters[0].getWidth()) >= ball.getX()){
                        // tcp.inputMoves(GET);
                    }

                }
            }
        }

    }*/

   /* private double calDistance(Position ball){
        Position mainChar = getMainCharCenterPoint();
        double d = Math.sqrt(Math.pow((ball.getX() - mainChar.getX()),2) + Math.pow((ball.getY() - mainChar.getY()),2));
        return d;
    }

    private Position getMainCharCenterPoint(){
        int x = characters[0].getX();
        int y = characters[0].getY();
        int w = (int) characters[0].getWidth();
        int h = (int) characters[0].getHeight();
        double c_x = (double)x + (double)w/2;
        double c_y = (double)y + (double)h/2;

        return new Position(c_x,c_y);
    }

    /*ivate Position getBallPoint(Ball b){
        int x = b.getX();
        int y = b.getY();
        int w = (int) b.getWidth();
        int h = (int) b.getHeight();
        double c_x = (double)x + (double)w/2;
        double c_y = (double)y + (double)h/2;

        return new Position(c_x,c_y);
    }*/
}
