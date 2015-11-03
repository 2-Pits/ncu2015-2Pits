package actionEvemt.UI;

import actionEvemt.ItemEventListener;

import javax.swing.*;
import java.awt.*;

/**
 * Created by fish on 2015/10/20.
 */
public class ScreenPanel extends JPanel implements ItemEventListener {

    private String message ="";
    public ScreenPanel() {
        this.setLayout(null);
        this.setVisible(true);

    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800, 450);
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g2d);
        setBackground(Color.PINK);

        g2d.setFont(new Font("Serif", Font.PLAIN,30));
        g2d.drawString(message,getWidth()/2,getHeight()/2);

    }

    @Override
    public void click(String message) {
        this.message=message;
        this.repaint();
    }
}
