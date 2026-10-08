import java.awt.*;

public class Paddle {
    private int x;
    private int y;
    private int width;
    private int height;
    private int speed;

    private boolean movingLeft;
    private boolean movingRight;

    public Paddle() {
        this.x = 250;
        this.y = 560;
        this.width = 100;
        this.height = 20;
        this.speed = 8;
    }

    public void moveLeft() {
        this.x -= this.speed;
    }

    public void moveRight() {
        this.x += this.speed;
    }

    public void update() {
        if (movingLeft) {
            moveLeft();
        }

        if (movingRight) {
            moveRight();
        }
    }

    public void setMovingLeft(boolean movingLeft) {
        this.movingLeft = movingLeft;
    }

    public void setMovingRight(boolean movingRight) {
        this.movingRight = movingRight;
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.white);
        g.fillRoundRect(this.x, this.y, this.width, this.height, 2, 2);
        g.setColor(Color.black);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        FontMetrics fm = g.getFontMetrics();
        g.drawString("SR", this.x + (this.width - fm.stringWidth("SR")) / 2, this.y + (this.height - fm.getHeight()) / 2 + fm.getAscent());
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

    public void setX(int x) {
        this.x = x;
    }
}
