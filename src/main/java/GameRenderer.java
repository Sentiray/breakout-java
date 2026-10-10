import java.awt.*;
import java.util.List;

public class GameRenderer {

    private static final int TOUGHBRICK_TEXT_OFFSET = 1;
    private static final int LEFT_HUD_MARGIN = 10;
    private static final int BOTTOM_HUD_MARGIN = 20;
    private static final int RIGHT_HUD_MARGIN = 10;

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
        FontMetrics fmPaddle = g.getFontMetrics();
        g.drawString("SR",  paddle.getX() + (Paddle.WIDTH - fmPaddle.stringWidth("SR")) / 2, paddle.getY() + (Paddle.HEIGHT - fmPaddle.getHeight()) / 2 + fmPaddle.getAscent());

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
