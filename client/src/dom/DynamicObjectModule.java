package dom;

import com.google.gson.Gson;
import com.twopits.balls.libs.OneGamer;
import sprite.Character;

import java.util.ArrayList;


/**
 * Created by dblab on 2015/12/14.
 */
public class DynamicObjectModule implements DOMInterface{
    private final Boolean GAMESTART = true;
    private final Boolean GAMESTOP = false;
    private Character myChar;
    private ArrayList<Character> other;
    private Boolean gameState;
    private String winner;

    public DynamicObjectModule() {
        other = new ArrayList<Character>();
        gameState = GAMESTOP;
        initEmptyCharacter();
    }

    private void initEmptyCharacter(){
        myChar = new Character(0, 0, 0);
    }

    public void initMyCharacter(int ID, double x, double y) {
        myChar.setID(ID);
        myChar.setPosition(x,y);
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
        OneGamer[] gamers = new OneGamer[gsons.length];
        for (int i = 0; i < gsons.length; i++) {
            gamers[i] = gson.fromJson(gsons[i], OneGamer.class);
        }
        for (int i = 0; i < other.size(); i++) {
            other.get(i).setPosition(gamers[other.get(i).getID()].getX(), gamers[other.get(i).getID()].getY());
        }

        /*for(int i =0;i<gsons.length;i++){
            System.out.println(gsons[i]);
        }*/
    }

    public ArrayList<Character> getOtherCharacter() {
        /* call by ScenRenderEngine
           get all character object.*/

        return other;
    }

<<<<<<< HEAD
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
=======
    @Override
    public void updateBall(String gsonBall) {

    }

    @Override
    public ArrayList getAllBall() {
        return null;
    }

    @Override
    public ArrayList getQWERState() {
        return null;
    }
>>>>>>> origin/master

    @Override
    public void startGame() {
        this.gameState = GAMESTART;
    }

    @Override
    public boolean startMove() {
        return gameState;
    }

    @Override
    public void endGame(String winner) {
        gameState = GAMESTOP;
        this.winner = winner;
    }

    @Override
    public boolean gameOver() {
        return gameState;
    }

    @Override
    public String getWinner() {
        return winner;
    }

}
