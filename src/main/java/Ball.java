import java.awt.*;

public class Ball {

    public static final int DIAMETER = 20;

    private int x;
    private int y;
    private int velocityX;
    private int velocityY;

    public Ball(int x, int y) {
        this.x = x;
        this.y = y;
        this.velocityX = 6;
        this.velocityY = -6;
    }

    public void update() {
        this.x += velocityX;
        this.y += velocityY;
    }

    public void bounceX() {
        velocityX *= -1;
    }

    public void bounceY() {
        velocityY *= -1;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getVelocityY() {
        return this.velocityY;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public Rectangle getBounds() {
        return new Rectangle(this.x, this.y, DIAMETER, DIAMETER);
    }
}
