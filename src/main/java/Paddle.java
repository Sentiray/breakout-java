import java.awt.*;

public class Paddle {

    public static final int WIDTH = 100;
    public static final int HEIGHT = 20;

    private int x;
    private int y;
    private int speed;

    private boolean movingLeft;
    private boolean movingRight;

    public Paddle(int x, int y) {
        this.x = x;
        this.y = y;
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
}
