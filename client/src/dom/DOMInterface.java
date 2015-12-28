package dom;

import com.google.gson.Gson;
import com.twopits.balls.models.BallModel;
import sprite.*;
import sprite.Character;

import java.util.ArrayList;

/**
 * Created by dblab on 2015/12/23.
 */
public interface DOMInterface {

    void initMyCharacter(int ID, double x, double y);    //call by TCP.

    Character getMyCharacter();   //call by Scene.

    ArrayList<Character> getOtherCharacter();   //call by Scene.

    Character updateMyPosition();   //call by UDP. For uploading data to server.

    void downloadCharacter(String gson);   //call by UDP. For getting data from server.

    void updateBall(String gsonBall);   // call by TCP ，從Server得到所有球狀態，用來更新球狀態

    BallModel[][] getAllBall();  //call by Scene ,用來畫圖,得到所有場上所有球的狀態(位置)

    ArrayList<BallModel> getQWERState();    //call by Scene,用來畫圖,畫QWER格子的球

    void startGame();   //call by TCP,用來開始遊戲

    boolean startMove();    //call by Scene,可以動了

    void endGame(String winner);    //call by TCP,當遊戲結束,傳送贏家是誰

    boolean gameOver(); //call by Scene,結束遊戲

    String getWinner(); //call by Scene,畫誰是贏家
}
