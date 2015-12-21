package sprite;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by dblab on 2015/12/14.
 */
public class Sprite {
    protected BufferedImage[] images;
    protected int frame;
    protected int frameInit = -1;
    protected double width;
    protected double height;
    protected int x;
    protected int y;

    public Sprite(int x,int y){
        this.x = x;
        this.y = y;

    }

    protected void loadImage(String imagePath){
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

    public void setPosition(int x, int y){
        this.x = x;
        this.y = y;
    }

    public BufferedImage getImage(){
        return images[frame];
    }

    public int getX(){
        return this.x;
    }

    public int getY(){
        return this.y;
    }

    public double getWidth(){
        return this.width;
    }

    public double getHeight(){
        return this.height;
    }

}
