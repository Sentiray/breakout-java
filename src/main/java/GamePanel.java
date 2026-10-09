import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

class GamePanel extends JPanel {
     private static final int PANEL_WIDTH = 800;
     private static final int PANEL_HEIGHT = 600;

     private static final int BRICK_ROWS = 8;
     private static final int BRICK_COLUMNS = 13;

     private static final int BRICK_GAP = 2;
     private static final int BRICK_START_X = 13;
     private static final int BRICK_START_Y = 20;

     private static final double TOUGH_BRICK_CHANCE = 0.15;

     private int score = 0;

     private final Ball ball;
     private final Paddle paddle;
     private final List<Brick> bricks;

     public GamePanel() {
         this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
         this.setBackground(new Color(15, 20, 40));

         ball = new Ball();
         paddle = new Paddle();
         bricks = new ArrayList<>();

         createBricks();
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

        Graphics2D g2 = (Graphics2D) g;

        ball.draw(g2);
        paddle.draw(g2);

        for (Brick brick : bricks) {
            brick.draw(g2);
        }

        g2.setColor(Color.WHITE);
        g2.drawString("Score: " + score, 10, 580);
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

         Iterator<Brick> iterator = bricks.iterator();

         while (iterator.hasNext()) {
             Brick brick = iterator.next();

             if (ball.getBounds().intersects(brick.getBounds())){
                 resolveBrickCollision(brick);
                 if (brick.hit()) {
                     score += brick.getPoints();
                     iterator.remove();
                 }
                 break; // Only resolve first collision if ball overlaps two bricks during the same update
             }
         }
    }

    private void resolveBrickCollision(Brick brick) {
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

    private void createBricks() {
         for (int row = 0; row < BRICK_ROWS; row++) {
             for (int col = 0; col < BRICK_COLUMNS; col++) {

                 int x = BRICK_START_X + col * (Brick.WIDTH + BRICK_GAP);
                 int y = BRICK_START_Y + row * (Brick.HEIGHT + BRICK_GAP);

                 int points;
                 Color color;

                 if (row <= 1) {
                     points = 7;
                     color = Color.red;
                 } else if (row <= 3) {
                     points = 5;
                     color = new Color(255, 140, 0);
                 } else if (row <= 5) {
                     points = 3;
                     color = Color.green;
                 } else {
                     points = 1;
                     color = Color.yellow;
                 }

                 if (Math.random() < TOUGH_BRICK_CHANCE) {
                     bricks.add(new ToughBrick(x, y, color, points));
                 } else {
                     bricks.add(new Brick(x, y, color, points));
                 }
             }
         }
    }
}
