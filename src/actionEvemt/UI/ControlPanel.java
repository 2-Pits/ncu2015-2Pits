package actionEvemt.UI;

import actionEvemt.ItemEventListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fish on 2015/10/14.
 */
public class ControlPanel extends JPanel implements KeyListener, MouseListener, MouseMotionListener {

    private static final int PADDING = 5;
    private static final int BUTTON_WIDTH = 90;
    private static final int BUTTON_HEIGHT = 90;
    private static final Color DEFAULT_COLOR = Color.decode( "#448AFF");
    private static final Color PRESSED_COLOR = Color.decode( "#2962FF");
    private static final String []SHAPE_NAME= {"Space","Q","W","E","R","Mouse"};
    private Map<String, ItemShape> shapMap ;

    private ItemEventListener itemEventListener;

    public ControlPanel() {
        this.setLayout(null);
        this.setVisible(true);

        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        createShap();

    }

    private void createShap() {
        shapMap= new HashMap<>();
        shapMap.put(SHAPE_NAME[0], new ItemShape(new Rectangle(PADDING, PADDING, 100, 100), DEFAULT_COLOR,SHAPE_NAME[0]));
        for(int i=1;i<=4;i++){
            shapMap.put(SHAPE_NAME[i], new ItemShape(new Rectangle(PADDING * (i+1) + i*100, PADDING, BUTTON_WIDTH, BUTTON_HEIGHT), DEFAULT_COLOR, SHAPE_NAME[i]));
        }
        shapMap.put(SHAPE_NAME[5], new ItemShape(new Rectangle(PADDING * (6) + 5*100 + 150, PADDING, BUTTON_WIDTH, BUTTON_HEIGHT), DEFAULT_COLOR, SHAPE_NAME[5]));
    }

    public void registerListemer(ItemEventListener itemEventListener) {
        this.itemEventListener = itemEventListener;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800, 150);
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g2d);
        setBackground(Color.WHITE);

        // travel the shapMap
        for (ItemShape item : shapMap.values()) {
            g2d.setColor(item.getColor());
            g2d.fill(item.getShape());
            g2d.setColor(Color.black);
            g2d.drawString(item.getShapeName(),(float)item.getShape().getBounds().getX(),(float)item.getShape().getBounds().getHeight());
        }

        g2d.dispose();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {

        int keyCode = e.getKeyCode();

        String message ="";
        switch (keyCode) {
            case KeyEvent.VK_Q:
                //break;
            case KeyEvent.VK_W:
               // break;
            case KeyEvent.VK_E:
                //break;
            case KeyEvent.VK_R:
                //break;
            case KeyEvent.VK_SPACE:
                //break;
            case KeyEvent.VK_LEFT:
                //break;
            case KeyEvent.VK_UP:
                //break;
            case KeyEvent.VK_DOWN:
                //break;
            case KeyEvent.VK_RIGHT:
                message = KeyEvent.getKeyText(keyCode);
                itemEventListener.click(message);
                break;
            default:
                break;
        }

        //  effect of feedback
        for(String name : SHAPE_NAME){
            if(message.equals(name)){
                shapMap.get(message).setPressed(true);
                repaint();
                break;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

        int keyCode = e.getKeyCode();
        for (ItemShape item : shapMap.values()) {
            if(KeyEvent.getKeyText(keyCode).equals(item.getShapeName())){
                item.setPressed(false);
                repaint();
                break;
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        ItemShape itemShape = shapMap.get(SHAPE_NAME[5]);
        if(itemShape.getShape().contains(e.getPoint()))
        {
            itemEventListener.click("Click");
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

        ItemShape itemShape = shapMap.get(SHAPE_NAME[5]);
        if(itemShape.getShape().contains(e.getPoint()))
        {
            itemEventListener.click("Pressed");
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    class ItemShape {

        private Color color;
        private Shape shape;
        private String shapeName;
        private boolean isPressed ;
        public ItemShape(Shape shape, Color color,String shapeName) {
            this.shape = shape;
            this.color = color;
            this.shapeName = shapeName;
            isPressed = false;
        }

        public boolean isPressed()
        {
            return isPressed;
        }

        public void  setPressed(boolean pressed)
        {
            isPressed = pressed;
            if(isPressed){
                color = PRESSED_COLOR;
            }else {
                color = DEFAULT_COLOR;
            }
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public Color getColor() {
            return color;
        }

        public Shape getShape() {
            return shape;
        }

        public String getShapeName(){
            return shapeName;
        }
    }
}
