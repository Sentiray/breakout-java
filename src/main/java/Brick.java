import java.awt.*;

public class Brick {
    public static final int WIDTH = 58;
    public static final int HEIGHT = 28;

    private int points;
    private int x;
    private int y;
    private int width;
    private int height;
    private Color color;

    public Brick(int x, int y, Color color, int points) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.points = points;

        this.width = WIDTH;
        this.height = HEIGHT;
    }

    public Rectangle getBounds() {
        return new Rectangle(this.x, this.y, this.width, this.height);
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getPoints() {
        return this.points;
    }

    public Color getColor() {
        return this.color;
    }

    public boolean hit() { return true; }
}
