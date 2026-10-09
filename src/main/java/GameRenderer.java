import java.awt.*;
import java.util.List;

public class GameRenderer {

    private static final int TOUGHBRICK_TEXT_OFFSET = 1;
    private static final int LEFT_HUD_MARGIN = 10;
    private static final int BOTTOM_HUD_MARGIN = 20;

    private final Game game;

    public GameRenderer(Game game) {
        this.game = game;
    }

    public void draw(Graphics2D g) {

        // render ball
        Ball ball = game.getBall();
        g.setColor(Color.white);
        g.fillOval(ball.getX(), ball.getY(), Ball.DIAMETER, Ball.DIAMETER);

        // render paddle
        Paddle paddle = game.getPaddle();
        g.setColor(Color.white);
        g.fillRoundRect(paddle.getX(), paddle.getY(),
                        Paddle.WIDTH, Paddle.HEIGHT,
                        2, 2);
        g.setColor(Color.black);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        FontMetrics fm = g.getFontMetrics();
        g.drawString("SR",  paddle.getX() + (Paddle.WIDTH - fm.stringWidth("SR")) / 2, paddle.getY() + (Paddle.HEIGHT - fm.getHeight()) / 2 + fm.getAscent());

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
                fm = g.getFontMetrics();

                int textX = brick.getX() + ((brick.getWidth() - fm.stringWidth(text)) / 2);
                int textY = brick.getY() + ((brick.getHeight() - fm.getHeight()) / 2) + fm.getAscent();

                g.setColor(Color.BLACK);
                g.drawString(text, textX + TOUGHBRICK_TEXT_OFFSET, textY + TOUGHBRICK_TEXT_OFFSET);

                g.setColor(Color.WHITE);
                g.drawString(text, textX, textY);
            }
        }

        // render score
        g.setColor(Color.WHITE);
        g.drawString("Score: " + game.getScore(), LEFT_HUD_MARGIN, game.getGameHeight() - BOTTOM_HUD_MARGIN);
    }
}
