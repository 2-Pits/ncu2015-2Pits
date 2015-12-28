package sprite;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by dblab on 2015/12/14.
 */
public class Character{
    private BufferedImage[] images;
    private int frame;
    private int frameInit = -1;
    private int ID;
    private double width;
    private double height;
    private double x;
    private double y;
    private double last_x;
    private double last_y;
    private boolean isMove;
    private int speed;
    private int dt = 0;
    private final int FRAME_TIME = 150;

    public Character(int id, double x, double y){
        setID(id);
        initChar();
        setPosition(x, y);
    }

    public Character(int id, double x, double y,int dir){
        this.ID = id;
        this.x = x;
        this.y = y;
        this.frame = dir;
    }


    public void update(int dt){
        this.dt += dt;
        while (this.dt >= FRAME_TIME){
            incFrame();
            this.dt -= FRAME_TIME;
        }
    }

    private void initChar(){
        last_x = x;
        last_y = y;
        isMove = false;
        loadImage("res/Man1.png");
        setDirection(0);
    }

    public void setPosition(double x, double y){
        last_x = this.x;
        last_y = this.y;
        this.x = x;
        this.y = y;
       // incFrame();
    }

    public void setPosition(double x, double y,int dir){
        setPosition(x,y);
        setDirection(dir);
        update(20);
    }

    public double getX(){
        return this.x;
    }

    public double getY(){
        return this.y;
    }

    public double getWidth(){
        return this.width;
    }

    public double getHeight(){
        return this.height;
    }

    public void setDirection(int dir){
        isMove = true;
        // If pressing the key continually before releaseing it, it would not initalize the frame.
        if(frameInit != dir){
            frame = dir;
            frameInit = dir;
        }
    }

    public int getDirection(){
        return frameInit;
    }

    public void setID(int i){
        this.ID = i;
    }

    public int getID(){
        return this.ID;
    }

    public void setSpeed(int speed){
        this.speed = speed;
    }

    private void incFrame(){
        if(checkMove())
            frame++;
        if(frame == images.length || frame % 4  == 0)
            frame = frameInit;
    }

    public boolean checkMove(){
        return (last_x != x || last_y != y);
    }

    public void stop(){
        isMove = false;
    }

    private void loadImage(String imagePath){
        ImageSplit imageSplit = new ImageSplit();
        imageSplit.loadImage(imagePath);
        images = imageSplit.getImages();
        setImageSize();
    }

    private void setImageSize(){
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        width = screenSize.getWidth()/2;
        height = screenSize.getHeight()/2;
    }

    public BufferedImage getImage(){
        return images[frame];
    }

    public BufferedImage[] getSprites(){
        return this.images;
    }


}
