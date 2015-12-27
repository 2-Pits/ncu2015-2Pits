package dom;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.twopits.balls.libs.OneGamer;
import sprite.Character;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;


/**
 * Created by dblab on 2015/12/14.
 */
public class DynamicObjectModule {
    private final Boolean GameStart = true;
    private final Boolean GameStop = false;
    private Character myChar;
    private ArrayList<Character> other;
    private Boolean GameState;

    public DynamicObjectModule() {
        other = new ArrayList<Character>();
        GameState = GameStop;
    }

    public void initMyCharacter(int ID, double x, double y) {
        myChar = new Character(ID, x, y);
        initAllCharacter(4);
    }

    private void initAllCharacter(int clientNum) {
        for (int i = 0; i < clientNum; i++) {
            if (i != myChar.getID()) {
                Character tempC = new Character(i, 0, 0);
                other.add(tempC);
            }
        }
    }

    public Character getMyCharacter() {
        return myChar;
    }

    public Character updateMyPosition() {
        Character me = new Character(myChar.getID(), myChar.getX(), myChar.getY(), myChar.getDirection());
        return myChar;
    }

    public void getOtherPosition(int clientno) {
        Character otherChar = new Character(clientno, 100 * clientno, 100 * clientno);
        other.add(otherChar);
    }

    public void downloadCharacter(String UDPgson) {
        /* called by UDP
           get all character information from UDP*/
        Gson gson = new Gson();
        String[] gsons = UDPgson.split(";");
       /* for(int i=0;i<gsons.length;i++){
            System.out.println(gsons[i]);
        }*/
        OneGamer[] gamers = new OneGamer[gsons.length];
        for (int i = 0; i < gsons.length; i++) {
            gamers[i] = gson.fromJson(gsons[i], OneGamer.class);
        }
        for (int i = 0; i < other.size(); i++) {
            other.get(i).setPosition(gamers[other.get(i).getID()].getX(), gamers[other.get(i).getID()].getY());
        }

       /* for(int i=0; i < other.size();i++){
            System.out.println("ID: " + other.get(i).getID());
            System.out.println("x: " + other.get(i).getX());
            System.out.println("y: " + other.get(i).getY());
            System.out.println("dir: " + other.get(i).getDirection());
        }*/
    }

    public ArrayList<Character> getOtherCharacter() {
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
