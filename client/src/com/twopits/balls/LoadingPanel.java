package com.twopits.balls;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by FishMan on 2015/12/27.
 */
public class LoadingPanel extends JPanel {

    private Font mGameFont;

    public LoadingPanel() {
        super();
        //
//
//		JFrame window = new GameWindow();
//		LoadingPanel pa = new LoadingPanel();
//		pa.setBackground(Color.blue);
//		//window.add(pa);
//		window.setVisible(true);
//
//		window.setLayout(new BorderLayout());
//		window.add(slideContainer, BorderLayout.CENTER);
//
//		slideContainer.add(pa);
        initValues();
    }

    private void initValues() {
        try {
            mGameFont = Font.createFont(Font.TRUETYPE_FONT, new File("res/supercell_magic.ttf"));
        } catch (Exception e) {
            e.printStackTrace();
            mGameFont = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2d = (Graphics2D) g;
        float zoom = getZoomFactor();
        g2d.setFont(mGameFont.deriveFont(10 * zoom));

        int drawTextPositionX = this.getWidth() / 2 ;
        int drawTextPositionY = this.getHeight() / 2 ;

        g2d.drawString("Loading",drawTextPositionX,drawTextPositionY);
    }

    private float getZoomFactor() {
        if (getWidth() / (float) getHeight() >
                5 / 3) {
            return getWidth() / (5 * 100);
        } else {
            return getHeight() / (3 * 100);
        }
    }
    class SlideContainer extends JLayeredPane {
        private static final int SLIDE_DELAY = 20;
        protected static final int DELTA_X = 2;
        Component oldComponent;

        public SlideContainer() {
            setLayout(null);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(500, 300);
        }

        @Override
        public Component add(Component comp) {
            Component[] comps = getComponents();
            if (comps.length > 0) {
                oldComponent = comps[0];
            }
            if (oldComponent == comp) {
                return super.add(comp);
            }
            if (oldComponent != null) {
                putLayer((JComponent) oldComponent, JLayeredPane.DEFAULT_LAYER);
            }
            Component returnResult = super.add(comp);
            putLayer((JComponent) comp, JLayeredPane.DRAG_LAYER);
            comp.setSize(getPreferredSize());
            comp.setVisible(true);
            comp.setLocation(getPreferredSize().width, 0);
            slideFromRight(comp, oldComponent);
            return returnResult;
        }

        private void slideFromRight(final Component comp,
                                    final Component oldComponent2) {
            new Timer(SLIDE_DELAY, new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent aEvt) {
                    int x = comp.getX();
                    if (x <= 0) {
                        comp.setLocation(0, 0);
                        putLayer((JComponent) comp, JLayeredPane.DEFAULT_LAYER);
                        if (oldComponent2 != null) {
                            remove(oldComponent2);
                        }
                        ((Timer) aEvt.getSource()).stop();
                    } else {
                        x -= DELTA_X;
                        comp.setLocation(x, 0);
                    }
                    repaint();
                }
            }).start();
        }
    }
}
