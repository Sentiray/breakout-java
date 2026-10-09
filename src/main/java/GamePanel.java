import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

class GamePanel extends JPanel {
     private static final int PANEL_WIDTH = 800;
     private static final int PANEL_HEIGHT = 600;

     private final Ball ball;
     private final Paddle paddle;
     private final Brick brick;

     public GamePanel() {
         this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
         this.setBackground(new Color(15, 20, 40));

         ball = new Ball();
         paddle = new Paddle();
         brick = new Brick(13, 20, Color.red);

         setUpKeyBindings();

         Timer timer = new Timer(16, e -> {
             update();
             repaint();
         });
         timer.start();
    }

     private void update() {
         ball.update();
         paddle.update();

         handlePaddleCollision();
         handleBrickCollision();
         handleWallCollisions();
         constrainPaddle();
     }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        ball.draw((Graphics2D) g);
        paddle.draw((Graphics2D) g);
        brick.draw((Graphics2D) g);
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

    private void handlePaddleCollision() {
        if (ball.getBounds().intersects(paddle.getBounds()) && ball.getVelocityY() > 0) {
            ball.setY(paddle.getY() - ball.getDiameter());
            ball.bounceY();
        }
    }

    private void handleBrickCollision() {

         if (!ball.getBounds().intersects(brick.getBounds())) {
             return;
         }

         int overlapLeft   = (ball.getX() + ball.getDiameter()) - brick.getX();
         int overlapRight  = (brick.getX() + brick.getWidth()) - ball.getX();
         int overlapTop    = (ball.getY() + ball.getDiameter()) - brick.getY();
         int overlapBottom = (brick.getY() + brick.getHeight()) - ball.getY();

         int horizontalOverlap = Math.min(overlapLeft, overlapRight);
         int verticalOverlap   = Math.min(overlapTop, overlapBottom);

         if (horizontalOverlap < verticalOverlap) {
             if (ball.getX() < brick.getX()) {
                 // ball approached from left side
                 ball.setX(brick.getX() - ball.getDiameter());
             } else {
                 // ball approached from the right side
                 ball.setX(brick.getX() + brick.getWidth());
             }
             ball.bounceX();
         } else {
             if (ball.getY() < brick.getY()) {
                 // ball approached from above
                 ball.setY(brick.getY() - ball.getDiameter());
             } else {
                 // ball approached from below
                 ball.setY(brick.getY() + brick.getHeight());
             }
             ball.bounceY();
         }
    }

    private void handleWallCollisions() {
        if (ball.getX() <= 0) {
            ball.setX(0);
            ball.bounceX();
        }

        if (ball.getX() + ball.getDiameter() >= getWidth()) {
            ball.setX(getWidth() - ball.getDiameter());
            ball.bounceX();
        }

        if (ball.getY() <= 0) {
            ball.setY(0);
            ball.bounceY();
        }
    }

    private void constrainPaddle() {
        if (paddle.getX() < 0) {
            paddle.setX(0);
        }

        if (paddle.getX() + paddle.getWidth() > getWidth()) {
            paddle.setX(getWidth() - paddle.getWidth());
        }
    }
}
