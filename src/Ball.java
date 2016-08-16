import java.awt.Graphics;
import java.awt.Color;

public class Ball {

	private int width;
	private int height;
	private int x;
	private int y;
	private int dx;
	private int dy;
	private int MIN_SPEED = 5;
	private int MAX_SPEED = 10;
	
	public Ball(int x, int y) {
		width = 20;
		height = 20;
		this.x = x;
		this.y = y;	
		randomSpeed();
	}
	
	public void setSpeeds(int min, int max) {
		MIN_SPEED = min;
		MAX_SPEED = max;
	}
	
	public int getDx() {
		return dx;
	}
	
	public void paintBall(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillOval(x, y, width, height);
		
	}
	
	public void reset(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getWidth() {
		return width;
	}
	
	public void setDy(int dy) {
		this.dy = dy;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void translate() {
		x += dx;
		y += dy;
	}
	
	public void horizontalCollide() {
		dx = -dx;
		x += dx;
	}
	
	public int randomSign() {
		if((int)(2*Math.random()) == 1) return -1;
		else return 1;
	}
	
	public void randomSpeed() {
		dx = randomSign() * (MIN_SPEED + (int) (Math.random() * (MAX_SPEED - MIN_SPEED + 1)));
		dy = randomSign() * (MIN_SPEED + (int) (Math.random() * (MAX_SPEED - MIN_SPEED + 1)));
	}
	
	public void verticalCollide() {
		dy = -dy;
		y += dy;
	}

	public int getDy() {
		return dy;
	}
}
