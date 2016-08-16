import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.security.KeyStore;

public class Paddle {
	private int x;
	private int y;
	private int height;
	private int width;
	private int dy;
	private int PADDLE_SPEED = 1;
	private int playerNumber;
	private final int PADDLE_HEIGHT = 100;
	private float acceleration = 0;
	
	public Paddle(int x, int y, int width, int height, int playerNumber) {
		this.x = x;
		this.y = y;
		this.height = PADDLE_HEIGHT;
		this.width = width;
		this.playerNumber = playerNumber;
	}
	
	public void resetPaddle() {
		height = PADDLE_HEIGHT;
	}
	
	public void setPaddleSpeed(int speed) {
		PADDLE_SPEED = speed;
	}
	
	public int getPaddleSpeed() {
		return PADDLE_SPEED;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getPlayerNumber() {
		return playerNumber;
	}
	
	public void decreasePaddleSize(int pixels) {
		height -= pixels;
	}
	
	public void translate(int lowerBound) {
		y += dy;
		if(y <= 0) {
			y = 1;
			acceleration = 0;
		}
		if(y >= lowerBound - height){
			y = lowerBound - height - 1;
			acceleration = 0;
		}
	}
	
	public void accelerate(float acceleration) {
		this.acceleration += acceleration;
	}
	
	public void setAcceleration(float acceleration) {
		this.acceleration = acceleration;
	}
	
	public float getAcceleration() {
		return acceleration;
	}
	
	public void setDy(int dy) {
		this.dy = dy;
	}
	
	public void paintPaddle(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(x, y, width, height);
	}
	
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if(playerNumber == 2) {
			if(key == KeyEvent.VK_UP) {
				dy = -1*PADDLE_SPEED;
			}
			if(key == KeyEvent.VK_DOWN) {
				dy = PADDLE_SPEED;
			}
		} else {
			if(key == KeyEvent.VK_W) {
				/*if(dy > -PADDLE_SPEED/2) {
					dy = -PADDLE_SPEED/2 + 1;
				}
				if(!(dy <= -PADDLE_SPEED)) {
					dy -= ACCELERATION;
				}*/
				dy = -PADDLE_SPEED;
			}
			if(key == KeyEvent.VK_S) {
				/*if(dy < PADDLE_SPEED/2) {
					dy = PADDLE_SPEED/2 - 1;
				}
				if(!(dy >= PADDLE_SPEED)) {
					dy += ACCELERATION;
				}*/
				dy = PADDLE_SPEED;
			}
		}
	}
	
	public void keyReleased(KeyEvent e) {
		if(playerNumber == 2) {
			if(e.getKeyCode() == KeyEvent.VK_UP)
				dy = 0;
			if(e.getKeyCode() == KeyEvent.VK_DOWN)
				dy = 0;
		} else {
			if(e.getKeyCode() == KeyEvent.VK_W)
				dy = 0;
			if(e.getKeyCode() == KeyEvent.VK_S)
				dy = 0;
		}
	}

	public int getY() {
		return y;
	}

	public int getHeight() {
		return height;
	}

	public int getDy() {
		return dy;
	}
}
