import javax.sound.sampled.AudioInputStream;
import java.io.*;
import sun.audio.*;
import javax.swing.*;

import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Scanner;

public class Board extends JPanel implements ActionListener {
	private int B_WIDTH = 1500;
	private int B_HEIGHT = 700;
	private final float PADDLE_ACCELERATION = 1.3f;
	private final int PADDLE_WIDTH = 20;
	private final int PADDLE_HEIGHT = 100;
	private Ball ball;
	private Paddle userPaddle;
	private Paddle computerPaddle;
	private Timer timer;
	private int computerScore = 0;
	private int userScore = 0;
	private static final int WINNING_SCORE = 7;
	private int numPlayers = 1;
	private int sizeDecrement = 2;
	private int difficulty;
	private boolean started = false;
	public static Board board;
	public static JButton startButton;
	public static JComboBox playerChoice;
	public static JComboBox difficultyChoice;
	public static JComboBox ballSpeedChoice;
	public static JButton resetPaddlesButton;
	public static String[] players = {"One Player", "Two Players"};
	public static String[] difficulties = {"Easy","Medium","Hard"};
	public static String[] ballSpeeds = {"Slow","Medium","Fast"};
	public final int PADDING = 10;
	public final int MINIMUM_PADDLE_SIZE = 20;
	public Key[] keys = {new Key(KeyEvent.VK_W), new Key(KeyEvent.VK_S), new Key(KeyEvent.VK_UP), new Key(KeyEvent.VK_DOWN)};
	
	
	public Board() {
		setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
		setBackground(new Color(0,0,0));
		setFocusable(true);
		setDoubleBuffered(true);
		userPaddle = new Paddle(0, B_HEIGHT/2, PADDLE_WIDTH, PADDLE_HEIGHT, 1);
		computerPaddle = new Paddle(B_WIDTH - PADDLE_WIDTH, B_HEIGHT/2, PADDLE_WIDTH, PADDLE_HEIGHT, 2);
		ball = new Ball(B_WIDTH/2, B_HEIGHT/2);
		
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("A"), "Reset Paddles");
		getActionMap().put("Reset Paddles", new ResetPaddles());
		
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("S"), "Move Down 1");
		getActionMap().put("Move Down 1", new MoveDown(userPaddle));
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("W"), "Move Up 1");
		getActionMap().put("Move Up 1", new MoveUp(userPaddle));
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released W"), "Stop Moving W");
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released S"), "Stop Moving S");
		getActionMap().put("Stop Moving W", new StopMoving(userPaddle, KeyEvent.VK_W));
		getActionMap().put("Stop Moving S", new StopMoving(userPaddle, KeyEvent.VK_S));

		
		
	}
	
	public void startBall() {
		ball.reset(getWidth()/2, getHeight()/2);
	}
	
	public int getInitWidth() {
		return B_WIDTH;
	}
	
	public int getInitHeight() {
		return B_HEIGHT;
	}
	
	public void paintComponent(Graphics g) {
		g.setFont(new Font("Helvetica",Font.BOLD,50));
		super.paintComponent(g);
		ball.paintBall(g);
		userPaddle.paintPaddle(g);
		computerPaddle.paintPaddle(g);
		g.drawString("" +userScore, 50, 50);
		g.drawString("" +computerScore, B_WIDTH - 80, 50);
	}
	
	public void checkCollisions() {
		if(ball.getX() <= userPaddle.getWidth()/2) {
			if(ball.getY() - PADDING < userPaddle.getY() + userPaddle.getHeight() && ball.getY() + ball.getHeight() > userPaddle.getY() - PADDING) {
				ball.horizontalCollide();
				pongNoise(true);
				ball.setDy(userPaddle.getDy()/2 + ball.getDy());
				if(userPaddle.getHeight() >= MINIMUM_PADDLE_SIZE)
				userPaddle.decreasePaddleSize(sizeDecrement);
			} else {
				ball.randomSpeed();
				if(ball.getDx() > 0) {
					ball.reset(getWidth()/4, getHeight()/2);
				} else {
					ball.reset(3 * getWidth()/4, getHeight()/2);
				}
				computerScore++;
			}
		}
		
		if(ball.getX() >= getWidth() - computerPaddle.getWidth()) {
			if(ball.getY() - PADDING < computerPaddle.getY() + computerPaddle.getHeight() && ball.getY() + ball.getHeight() > computerPaddle.getY() - PADDING) {
				ball.horizontalCollide();
				pongNoise(true);
				ball.setDy(computerPaddle.getDy()/2 + ball.getDy());
				if(computerPaddle.getHeight() >= MINIMUM_PADDLE_SIZE)
				computerPaddle.decreasePaddleSize(sizeDecrement);
			} else {
				ball.randomSpeed();
				if(ball.getDx() > 0) {
					ball.reset(getWidth()/4, getHeight()/2);
				} else {
					ball.reset(3 * getWidth()/4, getHeight()/2);
				}
				userScore++;
			}
		}
		if(ball.getY() <= 0 || ball.getY() >= getHeight() - ball.getHeight()) {
			ball.verticalCollide();
			pongNoise(false);
		}
	}
	
	public void acceleratePaddle(Paddle paddle) {
		if(paddle.getDy() != 0) paddle.accelerate(PADDLE_ACCELERATION);
		if(paddle.getDy() > 0) paddle.setDy((int) (paddle.getPaddleSpeed() + paddle.getAcceleration()));
		else if(paddle.getDy() != 0) paddle.setDy((int) (-paddle.getPaddleSpeed() - paddle.getAcceleration()));
	}
	
	
	public void actionPerformed(ActionEvent e) {
		if(started) {
			acceleratePaddle(computerPaddle);
			acceleratePaddle(userPaddle);
			if(e.getSource() == startButton) {
				if(timer.isRunning()) {
					timer.stop();
					startButton.setText("Resume");
				}
				else {
					timer.start();
					startButton.setText("Pause");
				}
			}
			if(ball.getX() > (7-difficulty)*getWidth()/8 && numPlayers == 1) {
				if(ball.getY() > computerPaddle.getY() + computerPaddle.getHeight()/2) {
					computerPaddle.setDy(computerPaddle.getPaddleSpeed());
				} else {
					computerPaddle.setDy(-computerPaddle.getPaddleSpeed());
				}
			} else if(numPlayers == 1){
				computerPaddle.setDy(0);
			}
			userPaddle.translate(getHeight());
			computerPaddle.translate(getHeight());
			if(started) {
				ball.translate();
			}
			checkCollisions();
			if(computerScore == WINNING_SCORE) {
				JOptionPane.showMessageDialog(null, "Congratulations, player two won!");
				started = false;
				startButton.setText("Start");
				timer.stop();
				for(int i = 0; i < keys.length; i++) keys[i].setKeyPressed(false);
			}
			if(userScore == WINNING_SCORE) {
				JOptionPane.showMessageDialog(null, "Congratulations, player one won!");
				started = false;
				startButton.setText("Start");
				timer.stop();
				for(int i = 0; i < keys.length; i++) keys[i].setKeyPressed(false);
			}
			repaint();
		} else {
			started = true;
			startButton.setText("Pause");
			requestFocusInWindow();
			numPlayers = playerChoice.getSelectedIndex() + 1;
			if(numPlayers == 2) {
				getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), "Move Down 2");
				getActionMap().put("Move Down 2", new MoveDown(computerPaddle));
				getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), "Move Up 2");
				getActionMap().put("Move Up 2", new MoveUp(computerPaddle));
				getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released UP"), "Stop Moving UP");
				getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released DOWN"), "Stop Moving DOWN");
				getActionMap().put("Stop Moving UP", new StopMoving(computerPaddle, KeyEvent.VK_UP));
				getActionMap().put("Stop Moving DOWN", new StopMoving(computerPaddle, KeyEvent.VK_DOWN));

			}
			difficulty = difficultyChoice.getSelectedIndex();
			int index = ballSpeedChoice.getSelectedIndex();
			if(index == 0) {
				ball.setSpeeds(5, 10);
				computerPaddle.setPaddleSpeed(8);
				userPaddle.setPaddleSpeed(8);
			}
			else if(index == 1) {
				ball.setSpeeds(7, 13);
				computerPaddle.setPaddleSpeed(10);
				userPaddle.setPaddleSpeed(10);
			}
			else {
				ball.setSpeeds(10, 15);
				computerPaddle.setPaddleSpeed(12);
				userPaddle.setPaddleSpeed(12);
			}
			userScore = 0;
			computerScore = 0;
			timer = new Timer(12,this);
			timer.start();
		}
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		board = new Board();
		frame.setTitle("Java Pong!");
		frame.setSize(board.getInitWidth() + 20, board.getInitHeight());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.add(board, BorderLayout.NORTH);
		
		JPanel optionsPanel = new JPanel();
		optionsPanel.setLayout(new GridLayout(1,8));
		optionsPanel.add(new JLabel("Number of players:", JLabel.CENTER));
		playerChoice = new JComboBox(players);
		optionsPanel.add(playerChoice);
		optionsPanel.add(new JLabel("Difficulty:", JLabel.CENTER));
		difficultyChoice = new JComboBox(difficulties);
		optionsPanel.add(difficultyChoice);
		optionsPanel.add(new JLabel("Ball Speed:", JLabel.CENTER));
		ballSpeedChoice = new JComboBox(ballSpeeds);
		optionsPanel.add(ballSpeedChoice);
		resetPaddlesButton = new JButton("Reset Paddle Size");
		resetPaddlesButton.addActionListener(board.new ResetPaddles());
		optionsPanel.add(resetPaddlesButton);
		startButton = new JButton("Start!");
		startButton.addActionListener(board);
		optionsPanel.add(startButton);
		
		frame.add(optionsPanel, BorderLayout.SOUTH);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		JOptionPane.showMessageDialog(null, "Welcome to my Pong game! Choose your options from the choices at the bottom.\n"
									       +"The W and S keys control the left paddle while the UP and DOWN keys control the \n"
				                           +"right paddle. First person to 5 points wins. Have fun!");
		
		
	}
	
	class Key {
		private int key;
		private boolean pressed;
		public Key(int key){
			this.key = key;
			pressed = false;
		}
		
		private boolean isKeyPressed() {
			return pressed;
		}
		
		private int getKeyCode() {
			return key;
		}
		
		private void setKeyPressed(boolean pressed) {
			this.pressed = pressed;
		}
	}
	
	class MoveDown extends AbstractAction {
		private Paddle paddle;
		public MoveDown(Paddle paddle) {
			this.paddle = paddle;
		}
		public void actionPerformed(ActionEvent e) {
			paddle.setAcceleration(0);
			for(int i = 0; i < keys.length; i++) {
				if(paddle.getPlayerNumber() == 1) {
					if(keys[i].getKeyCode() == KeyEvent.VK_S) keys[i].setKeyPressed(true);
				}
				if(paddle.getPlayerNumber() == 2) {
					if(keys[i].getKeyCode() == KeyEvent.VK_DOWN) keys[i].setKeyPressed(true);
				}
			}
			paddle.setDy(paddle.getPaddleSpeed());
		}
	}
	
	class MoveUp extends AbstractAction {
		private Paddle paddle;
		public MoveUp(Paddle paddle) {
			this.paddle = paddle;
		}
		public void actionPerformed(ActionEvent e) {
			paddle.setAcceleration(0);
			for(int i = 0; i < keys.length; i++) {
				if(paddle.getPlayerNumber() == 1) {
					if(keys[i].getKeyCode() == KeyEvent.VK_W) keys[i].setKeyPressed(true);
				}
				if(paddle.getPlayerNumber() == 2) {
					if(keys[i].getKeyCode() == KeyEvent.VK_UP) keys[i].setKeyPressed(true);
				}
			}
			paddle.setDy(-paddle.getPaddleSpeed());
		}
	}
	
	class StopMoving extends AbstractAction {
		private Paddle paddle;
		private int key;
		public StopMoving(Paddle paddle, int key) {
			this.paddle = paddle;
			this.key = key;
		}
		public void actionPerformed(ActionEvent e) {
			boolean notPressed = true;
			for(int i = 0; i < keys.length; i++) {
				if(keys[i].getKeyCode() == key) keys[i].setKeyPressed(false);
				if(keys[i].isKeyPressed()) {
					//if(paddle.getPlayerNumber() == 1 && (keys[i].getKeyCode() == KeyEvent.VK_W || keys[i].getKeyCode() == KeyEvent.VK_S)) notPressed = false;
					//if(paddle.getPlayerNumber() == 2 && (keys[i].getKeyCode() == KeyEvent.VK_UP || keys[i].getKeyCode() == KeyEvent.VK_DOWN)) notPressed = false;
					if(paddle.getPlayerNumber() == 2 && keys[i].getKeyCode() == KeyEvent.VK_UP) {
						notPressed = false;
						paddle.setDy(-paddle.getPaddleSpeed());
					}
					if(paddle.getPlayerNumber() == 2 && keys[i].getKeyCode() == KeyEvent.VK_DOWN) {
						notPressed = false;
						paddle.setDy(paddle.getPaddleSpeed());
					}
					if(paddle.getPlayerNumber() == 1 && keys[i].getKeyCode() == KeyEvent.VK_W) {
						notPressed = false;
						paddle.setDy(-paddle.getPaddleSpeed());
					}
					if(paddle.getPlayerNumber() == 1 && keys[i].getKeyCode() == KeyEvent.VK_S) {
						notPressed = false;
						paddle.setDy(paddle.getPaddleSpeed());
					}
				}
			}
			if(notPressed) {
				paddle.setDy(0);
			}
		}
	}
	
	class ResetPaddles extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			userPaddle.resetPaddle();
			computerPaddle.resetPaddle();
			repaint();
		}
	}
	
	private void pongNoise(boolean paddle) {
		InputStream in;
		String path;
		try {
			if(!paddle) path = "C:\\Users\\Brian\\Documents\\Sounds\\pongblip.wav";
			else path = "C:\\Users\\Brian\\Documents\\Sounds\\paddleblip.wav";
			in = new FileInputStream(new File(path));
			AudioStream sound = new AudioStream(in);
			AudioPlayer.player.start(sound);
			
		} catch (Exception e2){
			System.out.println(e2.getMessage());
		}
	}
	
	class PongNoise extends AbstractAction {
		private boolean paddle;
		public PongNoise(boolean paddle) {
			this.paddle = paddle;
		}
		
		public void actionPerformed(ActionEvent e) {
			InputStream in;
			String path;
			try {
				if(!paddle) path = "‪C:\\Users\\Brian\\Downloads\\pongblip.wav";
				else path = "‪C:\\Users\\Brian\\Downloads\\paddleblip.wav";
				in = new FileInputStream(new File(path));
				AudioStream sound = new AudioStream(in);
				AudioPlayer.player.start(sound);
				
			} catch (Exception e2){
				System.out.println(e2.getMessage());
			}
		}
	}
	
}
