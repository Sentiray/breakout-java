import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

class GamePanel extends JPanel {

     private final Game game;

     public GamePanel() {

         game = new Game();

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
        game.draw((Graphics2D) g);
    }

    private void setUpKeyBindings() {
         InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

         ActionMap actionMap = getActionMap();

         inputMap.put(KeyStroke.getKeyStroke("pressed LEFT"), "leftPressed");
         inputMap.put(KeyStroke.getKeyStroke("released LEFT"), "leftReleased");

         inputMap.put(KeyStroke.getKeyStroke("pressed RIGHT"), "rightPressed");
         inputMap.put(KeyStroke.getKeyStroke("released RIGHT"), "rightReleased");

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
    }
}
