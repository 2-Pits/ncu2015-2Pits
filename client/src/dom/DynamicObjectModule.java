package dom;

import com.google.gson.Gson;
import com.twopits.balls.BallMap;
import com.twopits.balls.libs.OneGamer;
import com.twopits.balls.models.BallModel;
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
    private BallMap mballMap;


    public DynamicObjectModule() {
        other = new ArrayList<Character>();
        mballMap = new BallMap();
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
            other.get(i).setDirection(gamers[other.get(i).getID()].getDir());
        }

    }

    public ArrayList<Character> getOtherCharacter() {
        /* call by ScenRenderEngine
           get all character object.*/

        return other;
    }
    
    @Override
    public void updateBall(String gsonBall) {
        Gson gson  = new Gson();
        mballMap = gson.fromJson(gsonBall,BallMap.class);
    }

    @Override
    public BallModel[][] getAllBall() {
        BallModel[][] mballModel = new BallModel[10][10];
        for(int i = 0; i < 10; i++){
            for (int j = 0; j < 10; j++) {
                BallModel.BallType ballType = BallModel.BallType.values()[mballMap.getCourt()[i][j]];
                mballModel[i][j] = new BallModel(ballType);
            }
        }
        return mballModel;
    }

    @Override
    public ArrayList<BallModel> getQWERState() {
        ArrayList<BallModel> mballModel = new ArrayList();
        int id = myChar.getID();
        for(int i = 0; i < 4; i++){
                BallModel.BallType ballType = BallModel.BallType.values()[mballMap.getItem()[id][i]];
                mballModel.add(new BallModel(ballType));
        }
        return mballModel;
    }

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
