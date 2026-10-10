import java.awt.geom.Rectangle2D;

public class Ball {

    public static final int DIAMETER = 20;

    private static final double INITIAL_VELOCITY_X = 6;
    private static final double INITIAL_VELOCITY_Y = -6;

    private double x;
    private double y;
    private double velocityX;
    private double velocityY;

    public Ball(int x, int y) {
        this.x = x;
        this.y = y;
        this.velocityX = INITIAL_VELOCITY_X;
        this.velocityY = INITIAL_VELOCITY_Y;
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

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getVelocityY() {
        return this.velocityY;
    }

    public double getVelocityX() {
        return this.velocityX;
    }

    public void setVelocityX(double velocityX) {
        this.velocityX = velocityX;
    }

    public void setVelocityY(double velocityY) {
        this.velocityY = velocityY;
    }

    public double getSpeed() {
        // calculate magnitude of velocity vector using pythagoras
        return Math.hypot(velocityX, velocityY);
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public Rectangle2D.Double getBounds() {
        return new Rectangle2D.Double(this.x, this.y, DIAMETER, DIAMETER);
    }

    public void resetVelocity() {
        this.velocityX = INITIAL_VELOCITY_X;
        this.velocityY = INITIAL_VELOCITY_Y;
    }
}
