import java.awt.*;

public class Brick {
    private int x;
    private int y;
    private int width;
    private int height;
    private Color color;

    public Brick(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.width = 58;
        this.height = 28;
        this.color = color;
    }

    public void draw(Graphics2D g) {
        g.setColor(color);
        g.fillRoundRect(this.x, this.y, this.width, this.height, 2, 2);
        g.setColor(color.darker());
        g.drawRoundRect(this.x, this.y, this.width, this.height, 2, 2);
        g.setColor(color.brighter());
        g.drawLine(this.x + 2, this.y + 2, this.x + this.width - 2, this.y + 2);
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
}
