import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

class GamePanel extends JPanel {

     private final Game game;
     private final GameRenderer renderer;

     public GamePanel() {
         game = new Game();
         renderer = new GameRenderer(game);

         this.setPreferredSize(new Dimension(game.getGameWidth(), game.getGameHeight()));
         this.setBackground(new Color(15, 20, 40));

         setUpKeyBindings();

         Timer timer = new Timer(16, e -> {
             game.update();
             repaint();
         });
         timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderer.draw((Graphics2D) g);
    }

    private void setUpKeyBindings() {
         InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

         ActionMap actionMap = getActionMap();

         inputMap.put(KeyStroke.getKeyStroke("pressed LEFT"), "leftPressed");
         inputMap.put(KeyStroke.getKeyStroke("released LEFT"), "leftReleased");

         inputMap.put(KeyStroke.getKeyStroke("pressed RIGHT"), "rightPressed");
         inputMap.put(KeyStroke.getKeyStroke("released RIGHT"), "rightReleased");

         inputMap.put(KeyStroke.getKeyStroke("pressed UP"), "upPressed");

         inputMap.put(KeyStroke.getKeyStroke("pressed ENTER"), "enterPressed");

         actionMap.put("leftPressed", new AbstractAction() {
             @Override
             public void actionPerformed(ActionEvent e) {
                 game.startMovingLeft();
             }
         });

         actionMap.put("leftReleased", new AbstractAction() {
             @Override
             public void actionPerformed(ActionEvent e) {
                 game.stopMovingLeft();
             }
         });

        actionMap.put("rightPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.startMovingRight();
            }
        });

        actionMap.put("rightReleased", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.stopMovingRight();
            }
        });

        actionMap.put("upPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.launchBall();
            }
        });

        actionMap.put("enterPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.restartGame();
            }
        });
    }
}
