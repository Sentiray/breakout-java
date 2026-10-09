import java.awt.*;

public class ToughBrick extends Brick {

    private static final int MAX_HITS = 3;
    private static final int POINT_MULTIPLIER = 3;
    private static final int TEXT_OFFSET = 1;

    private int hitsRemaining = MAX_HITS;

    public ToughBrick (int x, int y, Color color, int points) {
        super(x, y, color, points);
    }

    @Override
    public boolean hit() {
        hitsRemaining--;
        return hitsRemaining <= 0;
    }

    @Override
    public int getPoints() {
        return super.getPoints() * POINT_MULTIPLIER;
    }

    @Override
    public void draw(Graphics2D g) {
        super.draw(g);

        String text = Integer.toString(hitsRemaining);

        g.setFont(new Font("Arial", Font.BOLD, 18));
        FontMetrics fm = g.getFontMetrics();

        int textX = getX() + ((getWidth() - fm.stringWidth(text)) / 2);
        int textY = getY() + ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();

        g.setColor(Color.BLACK);
        g.drawString(text, textX + TEXT_OFFSET, textY + TEXT_OFFSET);

        g.setColor(Color.WHITE);
        g.drawString(text, textX, textY);
    }

}
