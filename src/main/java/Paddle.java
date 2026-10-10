import java.awt.*;

public class Paddle {

    public static final int WIDTH = 100;
    public static final int HEIGHT = 20;

    private static final int PADDLE_BASE_SPEED = 8;

    private int x;
    private int y;
    private int speed;

    private boolean movingLeft;
    private boolean movingRight;

    public Paddle(int x, int y) {
        this.x = x;
        this.y = y;
        this.speed = PADDLE_BASE_SPEED;
    }

    private void moveLeft() {
        this.x -= this.speed;
    }

    private void moveRight() {
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

    public void stopMovement() {
        this.movingLeft = false;
        this.movingRight = false;
    }

    public Rectangle getBounds() {
        return new Rectangle(this.x, this.y, WIDTH, HEIGHT);
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) { this.y = y; }
}
