import java.awt.*;

public class Ball {
    private int x;
    private int y;
    private int velocityX;
    private int velocityY;
    private int diameter;

    public Ball() {
        this.x = 270;
        this.y = 500;
        this.diameter = 20;
        this.velocityX = 6;
        this.velocityY = -6;
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.white);
        g.fillOval(this.x, this.y, this.diameter, this.diameter);
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

    public int getDiameter() {
        return this.diameter;
    }
}
