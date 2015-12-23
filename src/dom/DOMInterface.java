package dom;

import com.google.gson.Gson;
import sprite.*;
import sprite.Character;

import java.util.ArrayList;

/**
 * Created by dblab on 2015/12/23.
 */
public interface DOMInterface {

    void initMyCharacter(int ID,double x, double y);    //call by TCP.

    Character getMyCharacter();   //call by Scene.

    ArrayList<Character> getOtherCharacter();   //call by Scene.

    Character updateMyPosition();   //call by UDP. For uploading data to server.

    void downloadCharacter(String gson);   //call by UDP. For getting data from server.


}
