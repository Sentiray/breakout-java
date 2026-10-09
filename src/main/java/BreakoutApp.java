import javax.swing.*;
import java.awt.*;


public class BreakoutApp {
    public static void main(String[] args) {
        // create window
        JFrame gameWindow = new JFrame("Breakout");
        gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameWindow.setResizable(false);
        // create game panel
        gameWindow.add(new GamePanel());
        // size and show window
        gameWindow.pack();
        gameWindow.setLocationRelativeTo(null);
        gameWindow.setVisible(true);
    }
}
