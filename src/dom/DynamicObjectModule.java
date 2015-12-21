package dom;


import dom.DOMinterface;
import sprite.Ball;
import sprite.Character;
import sprite.Sprite;

import java.awt.*;
import java.util.Vector;

/**
 * Created by dblab on 2015/12/14.
 */
public class DynamicObjectModule implements DOMinterface {

    private Character[] characters;
    private Character mainChar;
    private Vector object;
    //private TCPCM tcp;

    public DynamicObjectModule(){
        characters = new Character[4];
        mainChar = new Character(0,0);
        object = new Vector();
        object.add(characters);
        //this.tcp = tcp;
    }

    @Override
    public void addVirtualCharacter(int clientno){
        /* called by UDPUC to add a main virtual character for the client computer clientno
           in the module programming exercise, a virtual character has the following basic
           attributes (you can extend in the future)
           x.y ¡V current pposition
           dir ¡V direction the virtual character is heading
           speed ¡V the moving speed
           You should create a sprite class and initialize its attributes like (x,y), dir, speed
        */
        Character otherChar = new Character(100*clientno, 100*clientno);
        otherChar.setDirection(clientno*4);
        characters[clientno] = otherChar;
    }

    @Override
    public void addItem(String name, int index, boolean shared,int x, int y){
        /* called by UDPUS to create an shared item
           An item is can be indexed by a name and an index.
           if shared is true, the item can only be own by a client at any time
           if shared is false, the item can be obtained by any client as if it can reappear
           when it is obtained by a virtual character (¨Ò¦p«æ±Ï¥])
           In this function, you should create a sprite class which contain
           attributes like name, index, and shared
        */
        Ball ball = new Ball(name,index,shared,x,y);
        object.add(ball);
    }

    @Override
    public void updateVirtualCharacter(int clientno, int dir, int speed, int x, int y){
        // called by UDPUS
        // update the data of a virtual character
        if(dir % 4 != 0 || dir < 0 || dir > 15){
            throw new IllegalArgumentException();
        }
        characters[clientno].setDirection(dir);
        characters[clientno].setSpeed(speed);
        characters[clientno].setPosition(x, y);

    }

    @Override
    public void updateItem(int index, boolean shared, int owner, int x, int y){
        // called by UDPUS
        // update the data of an item
        for(int i = 1 ; i< object.size() ; i++){
            Ball b =  (Ball) object.get(i);
            if(b.getIndex() == index){
                b.setShared(shared);
                b.setOwner(owner);
                b.setPosition(x,y);
            }
        }

    }

    @Override
    public Vector<Sprite> getAllDynamicObjects(){
        // get all character object.
        return object;
    }

    @Override
    public Point getVirtualCharacterXY(){
       /* called by Scene Render Engine
          This function returns the coordinates of the virtual character
          controlled by this client computer
          The position (x,y) is the location on the map.
          It is used to compute the view port and decide which part
          of the map should be displayed in the view port
       */
        Point point  = new Point((int)characters[0].getX(), (int)characters[0].getY());
        return point;
    }

    @Override
    public void keyGETPressed(){
        /* called by UIM
           When UIM accepts an keyboard input and it is a GET key
           it calls this method.
           This method should determine if the GET action is possible
           by comparing the virtual character¡¦s position and any
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
*/
    }

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
