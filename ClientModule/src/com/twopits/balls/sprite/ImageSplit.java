package com.twopits.balls.sprite; /**
 * Created by dblab on 2015/11/24.
 */

// Dividing the image into 16 pieces, and getting them.


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ImageSplit{
    private File file;
    private FileInputStream fis;
    private BufferedImage image;
    private BufferedImage imgs[];

    public ImageSplit(){
        // TODO Auto-generated constructor stub
    }

    public void loadImage(String path){
        this.file = new File(path);
    }

    private void doSplit() throws IOException{
        int row = 4;
        int col = 4;
        int chunk = row * col;
        int chunkHeight = image.getHeight() / row;
        int chunkWidth = image.getWidth() / col;
        int count = 0;
        this.imgs = new BufferedImage[chunk];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                imgs[count] = new BufferedImage(chunkWidth, chunkHeight, image.getType());
                // draws the image chunk
                Graphics2D gr = imgs[count++].createGraphics();
                gr.drawImage(image, 0, 0, chunkWidth, chunkHeight, chunkWidth * j, chunkHeight * i, chunkWidth * j + chunkWidth, chunkHeight * i + chunkHeight, null);
                gr.dispose();
            }
        }

    }

    public BufferedImage[] getImages(){
        try{
            fis = new FileInputStream(file);
            image = ImageIO.read(fis);
            doSplit();
        } catch(IOException ex){
            ex.printStackTrace();
        }
        return imgs;
    }
}
