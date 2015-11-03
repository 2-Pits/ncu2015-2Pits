package actionEvemt;

import actionEvemt.UI.ControlPanel;
import actionEvemt.UI.ScreenPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by fish on 2015/10/14.
 */
public class MainFrame {

    private JFrame mainFrame;
    private ControlPanel controlPanel;
    private ScreenPanel screenPanel;
    private JPanel gamePanel;

    public MainFrame() {
        mainFrame = new JFrame();
        mainFrame.setTitle("Event Dispatcher");
        mainFrame.setSize(new Dimension(800,600));
        mainFrame.setResizable(false);
        mainFrame.setLayout(new BorderLayout());
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // Set MainFrame to appear centered of screen
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);

        createView();
    }

    private void createView() {
        controlPanel = new ControlPanel();
        screenPanel = new ScreenPanel();
        gamePanel = new JPanel(null);
        gamePanel.setSize(new Dimension(800,600));

        screenPanel.setSize(screenPanel.getPreferredSize());
        screenPanel.setLocation(0, 0);
        gamePanel.add(screenPanel);

        controlPanel.setLocation(0, 450);
        controlPanel.setSize(controlPanel.getPreferredSize());
        gamePanel.add(controlPanel);

        controlPanel.registerListemer(screenPanel);
        mainFrame.getContentPane().add(gamePanel, BorderLayout.CENTER);

        gamePanel.addKeyListener(controlPanel);
        gamePanel.addMouseListener(controlPanel);
        mainFrame.addKeyListener(controlPanel);
        mainFrame.addMouseListener(controlPanel);
    }
}
