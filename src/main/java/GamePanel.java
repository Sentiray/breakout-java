import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

class GamePanel extends JPanel {
     private static final int PANEL_WIDTH = 800;
     private static final int PANEL_HEIGHT = 600;

     private Ball ball;
     private Paddle paddle;

     public GamePanel() {
         this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
         this.setBackground(new Color(15, 20, 40));

         ball = new Ball();
         paddle = new Paddle();

         setUpKeyBindings();

         Timer timer = new Timer(16, e -> {
             update();
             repaint();
         });
         timer.start();
    }

     public void update() {
         ball.update();
         paddle.update();

         // Ball-Paddle collisions
         if (isColliding(ball, paddle) && ball.getVelocityY() > 0) {
             ball.setY(paddle.getY() - ball.getDiameter());
             ball.bounceY();
         }

         // Ball collisions
         if (ball.getX() <= 0 || ball.getX() + ball.getDiameter() >= getWidth()) {
             ball.bounceX();
         }

         if (ball.getY() <= 0) {
             ball.bounceY();
         }

         // Paddle boundaries
         if (paddle.getX() < 0) {
             paddle.setX(0);
         }

         if (paddle.getX() + paddle.getWidth() > getWidth()) {
             paddle.setX(getWidth() - paddle.getWidth());
         }
     }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        ball.draw((Graphics2D) g);
        paddle.draw((Graphics2D) g);
    }

    private boolean isColliding(Ball ball, Paddle paddle) {
        return ball.getY() + ball.getDiameter() >= paddle.getY() &&
                ball.getX() + ball.getDiameter() >= paddle.getX() &&
                ball.getX() <= paddle.getX() + paddle.getWidth() &&
                ball.getY() <= paddle.getY() + paddle.getHeight();
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
                 paddle.setMovingLeft(true);
             }
         });

         actionMap.put("leftReleased", new AbstractAction() {
             @Override
             public void actionPerformed(ActionEvent e) {
                 paddle.setMovingLeft(false);
             }
         });

        actionMap.put("rightPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                paddle.setMovingRight(true);
            }
        });

        actionMap.put("rightReleased", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                paddle.setMovingRight(false);
            }
        });
    }
}
