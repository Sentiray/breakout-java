import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class Game {

    private static final double MAX_HORIZONTAL_FRACTION = 0.90;
    private static final double SPIN_STRENGTH = 0.20;
    private static final double EDGE_ZONE_FRACTION = 0.75;
    private static final double OUTER_ZONE_FRACTION = 0.60;
    private static final double INNER_ZONE_FRACTION = 0.35;

    private static final int GAME_WIDTH = 800;
    private static final int GAME_HEIGHT = 600;

    private static final int BRICK_ROWS = 8;
    private static final int BRICK_COLUMNS = 13;

    private static final int BRICK_GAP = 2;
    private static final int BRICK_START_X = 13;
    private static final int BRICK_START_Y = 20;

    private static final double TOUGH_BRICK_CHANCE = 0.15;

    private static final int PADDLE_BOTTOM_MARGIN = 20;

    private long updateNumber = 0;

    // Debug data
    private String lastPaddleZone;
    private double lastBounceFraction;
    private double lastSpinInfluence;
    private double lastResultVelocityX;
    private BrickCollisionSnapshot lastBrickCollision;
    private List<BrickBoundsSnapshot> lastOverlappingBricks;
    private final DebugLogger debugLogger;

    private static final int STARTING_LIVES = 3;
    private int lives;

    private GameState gameState = GameState.READY;

    private int score = 0;

    private final Ball ball;
    private final Paddle paddle;
    private final List<Brick> bricks;

    public Game() {

        paddle = new Paddle((GAME_WIDTH - Paddle.WIDTH)/2, GAME_HEIGHT - Paddle.HEIGHT - PADDLE_BOTTOM_MARGIN);
        ball = new Ball(paddle.getX() + ((Paddle.WIDTH - Ball.DIAMETER) / 2), paddle.getY() - Ball.DIAMETER);
        bricks = new ArrayList<>();

        createBricks();
        lives = STARTING_LIVES;
        lastOverlappingBricks = new ArrayList<>();
        debugLogger = new DebugLogger();
    }

    public void update() {

        updateNumber++;

        switch (gameState) {
            case READY -> {
                paddle.update();
                constrainPaddle();
                centerBallOnPaddle();
            }

            case PLAYING -> {
                paddle.update();
                constrainPaddle();
                ball.update();
                handlePaddleCollision();
                handleBrickCollision();
                handleWallCollisions();

                if (bricks.isEmpty()) {
                    gameState = GameState.WON;
                } else if (ball.getY() >= GAME_HEIGHT) {
                    lives--;

                    if (lives > 0) {
                        resetBall();
                        gameState = GameState.READY;
                    } else {
                        gameState = GameState.GAME_OVER;
                    }
                }
            }
        }
    }

    private void handlePaddleCollision() {
        if (ball.getBounds().intersects(paddle.getBounds()) && ball.getVelocityY() > 0) {
            applyPaddleBounce();
            ball.setY(paddle.getY() - Ball.DIAMETER);
        }
    }

    private void applyPaddleBounce() {
        // paddle steering inspired by https://gamedev.stackexchange.com/questions/20456/a-more-sophisticated-ball-paddle-collision-algorithm-for-breakout
        // and also the super breakout atari version https://www.ataricompendium.com/archives/manuals/5200/super_breakout.pdf
        double ballCenter = ball.getX() + Ball.DIAMETER/2.0;

        // magnitude of ball's velocity vector
        double speed = ball.getSpeed();

        // ball spin
        double spinInfluence = paddle.getMovementDirection() * speed * SPIN_STRENGTH;

        // test involving dividing paddle into four distinct zones instead of a normalized value
        double zoneWidth = Paddle.WIDTH/4.0;
        double firstBoundary = paddle.getX() + zoneWidth;
        double secondBoundary = paddle.getX() + zoneWidth*2.0;
        double thirdBoundary = paddle.getX() + zoneWidth*3.0;
        double fourthBoundary = paddle.getX() + Paddle.WIDTH;

        double maxSpeedX = speed * MAX_HORIZONTAL_FRACTION;
        double bounceFraction;

        if (ballCenter < paddle.getX()) {
            // extreme bounce left
            bounceFraction = -EDGE_ZONE_FRACTION;
            lastPaddleZone = "EDGE_LEFT";
        } else if (ballCenter < firstBoundary) {
            bounceFraction = -OUTER_ZONE_FRACTION;
            lastPaddleZone = "OUTER_LEFT";
        } else if (ballCenter < secondBoundary) {
            bounceFraction = -INNER_ZONE_FRACTION;
            lastPaddleZone = "INNER_LEFT";
        } else if (ballCenter < thirdBoundary) {
            bounceFraction = INNER_ZONE_FRACTION;
            lastPaddleZone = "INNER_RIGHT";
        } else if (ballCenter <= fourthBoundary) {
            bounceFraction = OUTER_ZONE_FRACTION;
            lastPaddleZone = "OUTER_RIGHT";
        } else {
            // extreme bounce right
            bounceFraction = EDGE_ZONE_FRACTION;
            lastPaddleZone = "EDGE_RIGHT";
        }

        double candidateVelocityX = (speed * bounceFraction) + spinInfluence;
        double newVelocityX = Math.clamp(candidateVelocityX, -maxSpeedX, maxSpeedX);
        ball.setVelocityX(newVelocityX);
        double velocityY = Math.sqrt(speed*speed - newVelocityX*newVelocityX) * -1;
        ball.setVelocityY(velocityY);

        lastBounceFraction = bounceFraction;
        lastSpinInfluence = spinInfluence;
        lastResultVelocityX = newVelocityX;

    }

    private void handleBrickCollision() {


        List<Brick> overlappingBricks = new ArrayList<>();
        for (Brick brick : bricks) {
            if (ball.getBounds().intersects(brick.getBounds())) {
                overlappingBricks.add(brick);
            }
        }


        // create snapshot for debugging
        List<BrickBoundsSnapshot> snapshotList = new ArrayList<>();
        for (Brick brick : overlappingBricks) {
            BrickBoundsSnapshot brickBoundsSnapshot = new BrickBoundsSnapshot(brick.getX(), brick.getY(), brick.getWidth(), brick.getHeight());
            snapshotList.add(brickBoundsSnapshot);
        }
        if (!snapshotList.isEmpty()) {
            lastOverlappingBricks = snapshotList;
        }



        if (isHorizontalGroup(overlappingBricks)) {

            List<Brick> sortedBricks = new ArrayList<>(overlappingBricks);
            sortedBricks.sort(Comparator.comparingInt(Brick::getX));

            double combinedX = sortedBricks.getFirst().getX();
            double combinedY = sortedBricks.getFirst().getY();
            double combinedRight = sortedBricks.getLast().getX() + sortedBricks.getLast().getWidth();
            double combinedWidth = combinedRight - combinedX;
            double combinedHeight = sortedBricks.getFirst().getHeight();

            handleGroupedCollision(combinedX, combinedY, combinedWidth, combinedHeight, sortedBricks, snapshotList);

        } else if (isVerticalGroup(overlappingBricks)) {

            List<Brick> sortedBricks = new ArrayList<>(overlappingBricks);
            sortedBricks.sort(Comparator.comparingInt(Brick::getY));

            double combinedX = sortedBricks.getFirst().getX();
            double combinedY = sortedBricks.getFirst().getY();
            double combinedWidth = sortedBricks.getFirst().getWidth();
            double combinedHeight = sortedBricks.getLast().getY() + sortedBricks.getLast().getHeight() - sortedBricks.getFirst().getY();

            handleGroupedCollision(combinedX, combinedY, combinedWidth, combinedHeight, sortedBricks, snapshotList);
        } else {

        /*
        List<BrickBoundsSnapshot> tempList = new ArrayList<>();
        for (Brick brick : bricks) {
            if (ball.getBounds().intersects(brick.getBounds())) {
                BrickBoundsSnapshot boundsSnapshot = new BrickBoundsSnapshot(brick.getX(), brick.getY(), brick.getWidth(), brick.getHeight());
                tempList.add(boundsSnapshot);
            }
        }
        if (!tempList.isEmpty()) {
            lastOverlappingBricks = tempList;
        }
        */

            Iterator<Brick> iterator = bricks.iterator();

            while (iterator.hasNext()) {
                Brick brick = iterator.next();

                if (ball.getBounds().intersects(brick.getBounds())) {

                    double ballBeforeX = ball.getX();
                    double ballBeforeY = ball.getY();
                    double ballBeforeVX = ball.getVelocityX();
                    double ballBeforeVY = ball.getVelocityY();

                    CollisionResolution collisionResolution = resolveBrickCollision(brick.getX(), brick.getY(), brick.getWidth(), brick.getHeight());

                    if (collisionResolution == null) {
                        continue;
                    }

                    double ballAfterX = ball.getX();
                    double ballAfterY = ball.getY();
                    double ballAfterVX = ball.getVelocityX();
                    double ballAfterVY = ball.getVelocityY();


                    boolean destroyed = brick.hit();

                    BrickBoundsSnapshot bounds = new BrickBoundsSnapshot(brick.getX(), brick.getY(), brick.getWidth(), brick.getHeight());
                    List<BrickHitResult> hitResults = List.of(new BrickHitResult(bounds, destroyed));

                    lastBrickCollision = new BrickCollisionSnapshot(brick.getX(), brick.getY(), brick.getWidth(), brick.getHeight(),
                            collisionResolution.collisionAxis(), collisionResolution.horizontalOverlap(),
                            collisionResolution.verticalOverlap(), hitResults);

                    if (destroyed) {
                        score += brick.getPoints();
                        iterator.remove();
                    }

                    BrickCollisionLogEntry entry = new BrickCollisionLogEntry(updateNumber, ballBeforeX, ballBeforeY, ballBeforeVX, ballBeforeVY,
                            snapshotList, brick.getX(), brick.getY(), brick.getWidth(), brick.getHeight(), collisionResolution.collisionAxis(),
                            collisionResolution.horizontalOverlap(), collisionResolution.verticalOverlap(),
                            hitResults, ballAfterX, ballAfterY, ballAfterVX, ballAfterVY);
                    debugLogger.logBrickCollision(entry);
                    break; // Only resolve first collision if ball overlaps two bricks during the same update
                }
            }
        }
    }

    private CollisionResolution resolveBrickCollision(double x, double y, double width, double height) {
        double overlapLeft   = (ball.getX() + Ball.DIAMETER) - x;
        double overlapRight  = (x + width) - ball.getX();
        double overlapTop    = (ball.getY() + Ball.DIAMETER) - y;
        double overlapBottom = (y + height) - ball.getY();

        double horizontalOverlap = Math.min(overlapLeft, overlapRight);
        double verticalOverlap   = Math.min(overlapTop, overlapBottom);

        if (horizontalOverlap < verticalOverlap) {
            if (ball.getX() < x) {
                // ball is on the left side of the brick
                if (ball.getVelocityX() > 0) {
                    ball.setX(x - Ball.DIAMETER);
                    ball.bounceX();
                    return new CollisionResolution(CollisionAxis.X, horizontalOverlap, verticalOverlap);
                }
            } else {
                // ball is on the right side of the brick
                if (ball.getVelocityX() < 0) {
                    ball.setX(x + width);
                    ball.bounceX();
                    return new CollisionResolution(CollisionAxis.X, horizontalOverlap, verticalOverlap);
                }
            }
        } else {
            if (ball.getY() < y) {
                // ball is above the brick
                if (ball.getVelocityY() > 0) {
                    ball.setY(y - Ball.DIAMETER);
                    ball.bounceY();
                    return new CollisionResolution(CollisionAxis.Y, horizontalOverlap, verticalOverlap);
                }
            } else {
                // ball is below the brick
                if (ball.getVelocityY() < 0) {
                    ball.setY(y + height);
                    ball.bounceY();
                    return new CollisionResolution(CollisionAxis.Y, horizontalOverlap, verticalOverlap);
                }
            }
        }
        // (temp) no impact -> return null
        return null;
    }

    private void handleWallCollisions() {
        if (ball.getX() <= 0) {
            ball.setX(0);
            ball.bounceX();
        }

        if (ball.getX() + Ball.DIAMETER >= GAME_WIDTH) {
            ball.setX(GAME_WIDTH - Ball.DIAMETER);
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

        if (paddle.getX() + Paddle.WIDTH > GAME_WIDTH) {
            paddle.setX(GAME_WIDTH - Paddle.WIDTH);
        }
    }

    private void createBricks() {

        bricks.clear();

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

    public int getGameWidth() {
        return GAME_WIDTH;
    }

    public int getGameHeight() {
        return GAME_HEIGHT;
    }

    public void startMovingLeft() {
        paddle.setMovingLeft(true);
    }

    public void stopMovingLeft() {
        paddle.setMovingLeft(false);
    }

    public void startMovingRight() {
        paddle.setMovingRight(true);
    }

    public void stopMovingRight() {
        paddle.setMovingRight(false);
    }

    public int getScore() {
        return score;
    }

    public Ball getBall() {
        return ball;
    }

    public int getLives() {
        return lives;
    }

    public GameState getGameState() {
        return gameState;
    }

    public Paddle getPaddle() {
        return paddle;
    }

    public List<Brick> getBricks() {
        return bricks;
    }

    private void centerBallOnPaddle() {
        ball.setX(paddle.getX() + ((Paddle.WIDTH - Ball.DIAMETER) / 2.0));
        ball.setY(paddle.getY() - Ball.DIAMETER);
    }

    public void launchBall() {
        if (gameState == GameState.READY) {
            gameState = GameState.PLAYING;
        }
    }

    private void resetBall() {
        ball.resetVelocity();
        centerBallOnPaddle();
    }

    public void restartGame() {
        if (gameState == GameState.WON || gameState == GameState.GAME_OVER) {
            score = 0;
            lives = STARTING_LIVES;
            createBricks();
            paddle.stopMovement();
            centerPaddle();
            resetBall();
            gameState = GameState.READY;
        }
    }

    private void centerPaddle() {
        paddle.setX((GAME_WIDTH - Paddle.WIDTH)/2);
        paddle.setY(GAME_HEIGHT - Paddle.HEIGHT - PADDLE_BOTTOM_MARGIN);
    }

    public String getLastPaddleZone() {
        return lastPaddleZone;
    }

    public double getLastBounceFraction() {
        return lastBounceFraction;
    }

    public double getLastSpinInfluence() {
        return lastSpinInfluence;
    }

    public double getLastResultVelocityX() {
        return lastResultVelocityX;
    }

    public BrickCollisionSnapshot getLastBrickCollision() {
        return lastBrickCollision;
    }

    public List<BrickBoundsSnapshot> getLastOverlappingBricks() {
        return lastOverlappingBricks;
    }

    private boolean isHorizontalGroup(List<Brick> overLappingBricks) {

        if (overLappingBricks.size() < 2) {
            return false;
        }

        List<Brick> sortedBricks = new ArrayList<>(overLappingBricks);
        sortedBricks.sort(Comparator.comparingInt(Brick::getX));

        int referenceY = sortedBricks.getFirst().getY();

        for (Brick brick : sortedBricks) {
            if (brick.getY() != referenceY) {
                return false;
            }
        }

        Brick current = sortedBricks.getFirst();

        for (int i = 1; i < sortedBricks.size(); i++) {
            Brick next = sortedBricks.get(i);

            if (next.getX() != current.getX() + current.getWidth() + BRICK_GAP) {
                return false;
            }
            current = next;
        }
        return true;
    }

    private boolean isVerticalGroup(List<Brick> overLappingBricks) {

        if (overLappingBricks.size() < 2) {
            return false;
        }

        List<Brick> sortedBricks = new ArrayList<>(overLappingBricks);
        sortedBricks.sort(Comparator.comparingInt(Brick::getY));

        int referenceX = sortedBricks.getFirst().getX();

        for (Brick brick : sortedBricks) {
            if (brick.getX() != referenceX) {
                return false;
            }
        }

        Brick current = sortedBricks.getFirst();

        for (int i = 1; i < sortedBricks.size(); i++) {
            Brick next = sortedBricks.get(i);

            if (next.getY() != current.getY() + current.getHeight() + BRICK_GAP) {
                return false;
            }
            current = next;
        }
        return true;
    }

    private void handleGroupedCollision(double combinedX, double combinedY, double combinedWidth, double combinedHeight, List<Brick> groupedBricks, List<BrickBoundsSnapshot> snapshotList) {

        double ballBeforeX = ball.getX();
        double ballBeforeY = ball.getY();
        double ballBeforeVX = ball.getVelocityX();
        double ballBeforeVY = ball.getVelocityY();

        CollisionResolution collisionResolution = resolveBrickCollision(combinedX, combinedY, combinedWidth, combinedHeight);

        if (collisionResolution == null) {
            return;
        }

        double ballAfterX = ball.getX();
        double ballAfterY = ball.getY();
        double ballAfterVX = ball.getVelocityX();
        double ballAfterVY = ball.getVelocityY();

        List<BrickHitResult> hitResults = new ArrayList<>();

        List<Brick> bricksToRemove = new ArrayList<>();

        for (Brick brick : groupedBricks) {
            boolean destroyed = brick.hit();
            BrickBoundsSnapshot bounds = new BrickBoundsSnapshot(brick.getX(), brick.getY(), brick.getWidth(), brick.getHeight());
            hitResults.add(new BrickHitResult(bounds, destroyed));

            if (destroyed) {
                score += brick.getPoints();
                bricksToRemove.add(brick);
            }
        }

        bricks.removeAll(bricksToRemove);

        lastBrickCollision = new BrickCollisionSnapshot(combinedX, combinedY, combinedWidth, combinedHeight,
                collisionResolution.collisionAxis(), collisionResolution.horizontalOverlap(),
                collisionResolution.verticalOverlap(), hitResults);

        BrickCollisionLogEntry entry = new BrickCollisionLogEntry(updateNumber, ballBeforeX, ballBeforeY, ballBeforeVX, ballBeforeVY,
                snapshotList, combinedX, combinedY, combinedWidth, combinedHeight, collisionResolution.collisionAxis(),
                collisionResolution.horizontalOverlap(), collisionResolution.verticalOverlap(),
                hitResults, ballAfterX, ballAfterY, ballAfterVX, ballAfterVY);

        debugLogger.logBrickCollision(entry);
    }


}
