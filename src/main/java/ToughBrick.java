import java.awt.*;

public class ToughBrick extends Brick {

    private static final int MAX_HITS = 3;
    private static final int POINT_MULTIPLIER = 3;

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

    public int getHitsRemaining() {
        return this.hitsRemaining;
    }
}
