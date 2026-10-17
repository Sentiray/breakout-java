import java.awt.*;
import java.util.List;

public class GameRenderer {

    private static final int TOUGHBRICK_TEXT_OFFSET = 1;
    private static final int LEFT_HUD_MARGIN = 10;
    private static final int BOTTOM_HUD_MARGIN = 20;
    private static final int RIGHT_HUD_MARGIN = 10;
    private static final int DEBUG_BLOCK_STARTING_OFFSET_BOTTOM = 260;
    private static final int DEBUG_LINE_SPACING = 30;
    private static final int DEBUG_SECTION_SPACING = 60;

    private final Game game;

    public GameRenderer(Game game) {
        this.game = game;
    }

    public void draw(Graphics2D g) {

        // render ball
        Ball ball = game.getBall();
        g.setColor(Color.white);
        g.fillOval((int)Math.round(ball.getX()), (int)Math.round(ball.getY()), Ball.DIAMETER, Ball.DIAMETER);

        // render paddle
        Paddle paddle = game.getPaddle();
        g.setColor(Color.white);
        g.fillRoundRect(paddle.getX(), paddle.getY(),
                        Paddle.WIDTH, Paddle.HEIGHT,
                        2, 2);
        g.setColor(Color.black);
        g.setFont(new Font("Arial", Font.BOLD, 18));

        // render bricks
        List<Brick> bricks = game.getBricks();
        for (Brick brick : bricks) {
            // first normal bricks
            g.setColor(brick.getColor());
            g.fillRoundRect(brick.getX(), brick.getY(), brick.getWidth(), brick.getHeight(), 2, 2);
            g.setColor(brick.getColor().darker());
            g.drawRoundRect(brick.getX(), brick.getY(), brick.getWidth(), brick.getHeight(), 2, 2);
            g.setColor(brick.getColor().brighter());
            g.drawLine(brick.getX() + 2, brick.getY() + 2, brick.getX() + brick.getWidth() - 2, brick.getY() + 2);

            // additional rendering if brick is a ToughBrick
            if (brick instanceof ToughBrick) {
                String text = Integer.toString(((ToughBrick) brick).getHitsRemaining());

                g.setFont(new Font("Arial", Font.BOLD, 18));
                FontMetrics fmToughBricks = g.getFontMetrics();

                int textX = brick.getX() + ((brick.getWidth() - fmToughBricks.stringWidth(text)) / 2);
                int textY = brick.getY() + ((brick.getHeight() - fmToughBricks.getHeight()) / 2) + fmToughBricks.getAscent();

                g.setColor(Color.BLACK);
                g.drawString(text, textX + TOUGHBRICK_TEXT_OFFSET, textY + TOUGHBRICK_TEXT_OFFSET);

                g.setColor(Color.WHITE);
                g.drawString(text, textX, textY);
            }
        }

        switch (game.getGameState()) {
            case READY -> {
                renderTip(g);
                renderHUD(g);
            }
            case PLAYING -> renderHUD(g);
            case GAME_OVER -> renderGameOver(g);
            case WON -> renderGameWon(g);
        }
    }

    public void drawDebug(Graphics2D g) {

        // render paddle zones
        Paddle paddle = game.getPaddle();
        double zoneWidth = Paddle.WIDTH/4.0;
        double firstBoundary = paddle.getX() + zoneWidth;
        double secondBoundary = paddle.getX() + zoneWidth*2;
        double thirdBoundary = paddle.getX() + zoneWidth*3;
        double fourthBoundary = paddle.getX() + Paddle.WIDTH;

        g.setFont(new Font("Arial", Font.BOLD, 18));
        FontMetrics fmDebugZone = g.getFontMetrics();

        g.setColor(new Color(255, 0, 0, 140));
        g.fillRect(paddle.getX(), paddle.getY(), (int)zoneWidth, Paddle.HEIGHT);

        g.setColor(new Color(226, 206, 23, 140));
        g.fillRect(paddle.getX()+(int)zoneWidth, paddle.getY(), (int)zoneWidth, Paddle.HEIGHT);

        g.setColor(new Color(0, 255, 0, 140));
        g.fillRect(paddle.getX()+(int)zoneWidth*2, paddle.getY(), (int)zoneWidth, Paddle.HEIGHT);

        g.setColor(new Color(0, 0, 255, 140));
        g.fillRect(paddle.getX()+(int)zoneWidth*3, paddle.getY(), (int)zoneWidth, Paddle.HEIGHT);

        Stroke oldStroke = g.getStroke();

        g.setColor(new Color(20, 20, 20));
        g.setStroke(new BasicStroke(2.0f));

        g.drawLine(paddle.getX(), paddle.getY(), paddle.getX(), paddle.getY()+Paddle.HEIGHT);
        g.drawLine((int)firstBoundary, paddle.getY(), (int)firstBoundary, paddle.getY()+Paddle.HEIGHT);
        g.drawLine((int)secondBoundary, paddle.getY(), (int)secondBoundary, paddle.getY()+Paddle.HEIGHT);
        g.drawLine((int)thirdBoundary, paddle.getY(), (int)thirdBoundary, paddle.getY()+Paddle.HEIGHT);
        g.drawLine((int)fourthBoundary, paddle.getY(), (int)fourthBoundary, paddle.getY()+Paddle.HEIGHT);

        g.setColor(new Color(255, 255, 255, 234));
        g.drawRect(paddle.getX(), paddle.getY(), Paddle.WIDTH, Paddle.HEIGHT);

        g.setStroke(oldStroke);

        // render debug data
        Ball ball = game.getBall();
        double velocityX = ball.getVelocityX();
        double velocityY = ball.getVelocityY();
        double speed = ball.getSpeed();
        String lastPaddleZone = game.getLastPaddleZone();
        double lastBounceFraction = game.getLastBounceFraction();
        double lastSpinInfluence = game.getLastSpinInfluence();
        double lastResultVelocityX = game.getLastResultVelocityX();

        g.setFont(new Font("Arial", Font.BOLD, 18));
        FontMetrics fmDebugData = g.getFontMetrics();

        int currentY = game.getGameHeight() - DEBUG_BLOCK_STARTING_OFFSET_BOTTOM;
        // live data
        g.drawString("Speed: " + String.format("%.2f", speed), LEFT_HUD_MARGIN, currentY);
        currentY += DEBUG_LINE_SPACING;
        g.drawString("Velocity: (" + String.format("%.2f", velocityX) + ", " + String.format("%.2f", velocityY) + ")", LEFT_HUD_MARGIN, currentY);

        currentY += DEBUG_SECTION_SPACING;
        // last-hit data
        g.drawString("Last Zone: " + lastPaddleZone, LEFT_HUD_MARGIN, currentY);
        currentY += DEBUG_LINE_SPACING;
        g.drawString("Fraction: " + String.format("%.2f", lastBounceFraction), LEFT_HUD_MARGIN, currentY);
        currentY += DEBUG_LINE_SPACING;
        g.drawString("Spin: " + String.format("%.2f", lastSpinInfluence), LEFT_HUD_MARGIN, currentY);
        currentY += DEBUG_LINE_SPACING;
        g.drawString("Last Paddle X: " + String.format("%.2f", lastResultVelocityX), LEFT_HUD_MARGIN, currentY);

        // render brick collision data
        BrickCollisionSnapshot snapshot = game.getLastBrickCollision();
        List<BrickBoundsSnapshot> boundsSnapshots = game.getLastOverlappingBricks();


        if (snapshot != null) {

            String axisMarker = String.valueOf(snapshot.collisionAxis());
            g.setFont(new Font("Arial", Font.BOLD, 18));
            FontMetrics fmBrickDebugData = g.getFontMetrics();

            int surfaceX = (int) Math.round(snapshot.x());
            int surfaceY = (int) Math.round(snapshot.y());
            int surfaceWidth = (int) Math.round(snapshot.width());
            int surfaceHeight = (int) Math.round(snapshot.height());

            // draw yellow outline of all bricks that collide with ball
            g.setColor(Color.YELLOW);
            for (BrickBoundsSnapshot boundsSnapshot : boundsSnapshots) {
                g.drawRect(boundsSnapshot.x(), boundsSnapshot.y(), boundsSnapshot.width(), boundsSnapshot.height());
            }

            // draw collision surface used for resolution
            int axisMarkerX = surfaceX + ((surfaceWidth - fmBrickDebugData.stringWidth(axisMarker)) / 2);
            int axisMarkerY = surfaceY + ((surfaceHeight - fmBrickDebugData.getHeight()) / 2) + fmBrickDebugData.getAscent();

            g.setColor(Color.RED);

            g.drawRect(surfaceX, surfaceY, surfaceWidth, surfaceHeight);

            for (BrickHitResult hitResult : snapshot.hitResults()) {
                if (hitResult.destroyed()) {
                    g.drawLine(hitResult.bounds().x(), hitResult.bounds().y() + hitResult.bounds().height(), hitResult.bounds().x() + hitResult.bounds().width(), hitResult.bounds().y());
                }
            }


            // yellow X and Y to display which axis resolver chose
            g.setColor(Color.YELLOW);
            if (snapshot.collisionAxis() == CollisionAxis.X) {
                g.drawString(axisMarker, axisMarkerX, axisMarkerY);
            } else if (snapshot.collisionAxis() == CollisionAxis.Y) {
                g.drawString(axisMarker, axisMarkerX, axisMarkerY);
            }

            g.setColor(Color.WHITE);

            currentY = game.getGameHeight() - DEBUG_BLOCK_STARTING_OFFSET_BOTTOM;
            String text = "Brick Axis: " + snapshot.collisionAxis();
            int debugTextWidth = fmDebugData.stringWidth(text);
            g.drawString(text, game.getGameWidth() - RIGHT_HUD_MARGIN - debugTextWidth, currentY);
            currentY += DEBUG_LINE_SPACING;
            text = "H Overlap: " + String.format("%.2f", snapshot.horizontalOverlap());
            debugTextWidth = fmDebugData.stringWidth(text);
            g.drawString(text, game.getGameWidth() - RIGHT_HUD_MARGIN - debugTextWidth, currentY);
            currentY += DEBUG_LINE_SPACING;
            text = "V Overlap: " + String.format("%.2f", snapshot.verticalOverlap());
            debugTextWidth = fmDebugData.stringWidth(text);
            g.drawString(text, game.getGameWidth() - RIGHT_HUD_MARGIN - debugTextWidth, currentY);
            currentY += DEBUG_SECTION_SPACING;
            text = "Overlapping Bricks: " + boundsSnapshots.size();
            debugTextWidth = fmDebugData.stringWidth(text);
            g.drawString(text, game.getGameWidth() - RIGHT_HUD_MARGIN - debugTextWidth, currentY);
        }

    }

    private void renderHUD(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        FontMetrics fmHUD = g.getFontMetrics();
        // render score
        g.drawString("Score: " + game.getScore(), LEFT_HUD_MARGIN, game.getGameHeight() - BOTTOM_HUD_MARGIN);

        // render lives
        String text = "Lives: " + game.getLives();
        int textWidth = fmHUD.stringWidth(text);
        g.drawString(text, game.getGameWidth() - RIGHT_HUD_MARGIN - textWidth, game.getGameHeight() - BOTTOM_HUD_MARGIN);
    }

    private void renderTip(Graphics2D g) {
        String tipText = "Press UP to launch the ball";
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fmTip = g.getFontMetrics();
        int tipTextWidth = fmTip.stringWidth(tipText);
        g.drawString(tipText, (game.getGameWidth()-tipTextWidth)/2, (game.getGameHeight()/2));
    }

    private void renderGameOver(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, game.getGameWidth(), game.getGameHeight());

        // game over text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        FontMetrics fmGameOver = g.getFontMetrics();
        String gameOver = "GAME OVER!";
        int gameOverWidth = fmGameOver.stringWidth(gameOver);
        g.drawString(gameOver, (game.getGameWidth()-gameOverWidth)/2, (game.getGameHeight()/3));

        // final score + enter text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fmFinalEnter = g.getFontMetrics();
        String finalScore = "Final Score: " + game.getScore();
        int finalScoreWidth = fmFinalEnter.stringWidth(finalScore);
        g.drawString(finalScore, (game.getGameWidth()-finalScoreWidth)/2, (game.getGameHeight()/2));
        String enterRestart = "Press ENTER to restart";
        int enterRestartWidth = fmFinalEnter.stringWidth(enterRestart);
        g.drawString(enterRestart, (game.getGameWidth()-enterRestartWidth)/2, (game.getGameHeight()/3)*2);
    }

    private void renderGameWon(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, game.getGameWidth(), game.getGameHeight());

        // game won text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        FontMetrics fmGameWon = g.getFontMetrics();
        String gameWon = "YOU WIN!";
        int gameWonWidth = fmGameWon.stringWidth(gameWon);
        g.drawString(gameWon, (game.getGameWidth()-gameWonWidth)/2, (game.getGameHeight()/3));

        // final score + enter text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fmFinalEnter = g.getFontMetrics();
        String finalScore = "Final Score: " + game.getScore();
        int finalScoreWidth = fmFinalEnter.stringWidth(finalScore);
        g.drawString(finalScore, (game.getGameWidth()-finalScoreWidth)/2, (game.getGameHeight()/2));
        String enterRestart = "Press ENTER to restart";
        int enterRestartWidth = fmFinalEnter.stringWidth(enterRestart);
        g.drawString(enterRestart, (game.getGameWidth()-enterRestartWidth)/2, (game.getGameHeight()/3)*2);
    }
}
