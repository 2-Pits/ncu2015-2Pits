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

    void updateBall(String gsonBall);   // call by TCP �A�qServer�o��Ҧ��y���A�A�Ψӧ�s�y���A

    BallModel[][] getAllBall();  //call by Scene ,�Ψӵe��,�o��Ҧ����W�Ҧ��y�����A(��m)

    ArrayList<BallModel> getQWERState();    //call by Scene,�Ψӵe��,�eQWER��l���y

    void startGame();   //call by TCP,�ΨӶ}�l�C��

    boolean startMove();    //call by Scene,�i�H�ʤF

    void endGame(String winner);    //call by TCP,��C������,�ǰeĹ�a�O��

    boolean gameOver(); //call by Scene,�����C��

    String getWinner(); //call by Scene,�e�֬OĹ�a
}
