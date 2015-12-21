package dom;


import sprite.Sprite;

import java.awt.*;
import java.util.Vector;

/**
 * Created by dblab on 2015/12/16.
 */
public interface DOMinterface {

    void addVirtualCharacter(int clientno);

    void addItem(String name, int index, boolean shared, int x, int y);

    void updateVirtualCharacter(int clientno, int dir, int speed, int x, int y);

    void updateItem(int index, boolean shared, int owner, int x, int y);

    Vector<Sprite> getAllDynamicObjects();

    Point getVirtualCharacterXY();

    void keyGETPressed();
}
