import java.awt.*;

import javax.swing.*;

/*
 * Java Draughts
 * 
 * Author: Thomas Auberson
 * Version: 0.14
 * 
 * This class controls a JFrame window with a single JPanel canvas display.  Extracted from Template Library v0.12
 */

public class Display extends JPanel implements Runnable {

	// FIELDS
	private static final long serialVersionUID = 1L;	
	private String TITLE = "Java Draughts";
	private String VERSION = "0.14"; // *RREQUIRES DEFINITION
	private String AUTHOR = "Thomas Auberson";
	private String DESCRIPTION = "<br><br>Play a game of English Draughts/Checkers.<br>Games can be played player vs player(hotseat<br>mode) or player vs computer AI. "; // *RREQUIRES DEFINITION
	private int SIZE_X = 646, SIZE_Y = 691;
	private int SIZE = 640; // Board Size in pixels
	private boolean RESIZABLE = false;
	
	private Thread thread = new Thread(this);

	private JFrame frame;
	private MenuBar menu;

	private Image board;
	private Field field;
	private boolean gameOver;

	// NOTE: Size of Frame:
	// Edges (Width) = 6; Edges (Height) = 28; Menubar (Height) = 23
	// ==> Frame (x,y) = JPanel(x)+6, JPanel(y)+51

	// CONSTRUCTOR
	public Display() {
		// Initialise the JFrame
		frame = createFrame();
		frame.add(this);

		// Initialise Menu Bar
		menu = new MenuBar(this);
		frame.setJMenuBar(menu);

		// Initialise Mouse Listeners
		MouseHandler mouse = new MouseHandler(this);
		this.addMouseListener(mouse);

		// Load Field
		loadImages();
		field = new Field(SIZE);
		gameOver = false;

		// Start Display		
		frame.setVisible(true);
		thread.start();
	}

	private JFrame createFrame() {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(SIZE_X, SIZE_Y);
		frame.setTitle(TITLE);
		frame.setResizable(RESIZABLE);
		frame.setLocationRelativeTo(null); // Sets window in centre
		frame.setLayout(new GridLayout(1, 1, 0, 0));
		return frame;
	}

	private void loadImages() {
		board = new ImageIcon("tex/board.png").getImage();
	}

	private void checkGameOver() {
		if (gameOver){
			return;
		}
		if (field.whitePieces.isEmpty())
			gameOver("Black");
		else if (field.blackPieces.isEmpty())
			gameOver("White");
	}

	private void gameOver(String winner) {
		gameOver = true;
		JOptionPane.showMessageDialog(this, "GAME OVER: " + winner + " Wins!");
	}

	// MOUSE ACTION LISTENR
	public void mouseReleased(Point p, int button) {
		if(gameOver || !field.playerCanAct())
			return;		
		SwingUtilities.convertPointFromScreen(p, this);
		if (button == 1)
			if (field.canMoveTo(field.findTile(p))) {
				field.moveTo(p);
			} else {
				field.selectTile(p);
			}
		else if (button == 3)
			field.deselectTile();		
	}

	// MENU ACTION LISTENER
	public void menuButtonClicked(String button) {
		switch (button) {
		case "About":
			JOptionPane.showMessageDialog(this, "<html>" + TITLE + "<br>Version: " + VERSION + "<br>Author: " + AUTHOR + DESCRIPTION + "</html>", "About", JOptionPane.PLAIN_MESSAGE);
			break;
		case "New Hotseat Game":
			field.startGame();
			gameOver = false;
			break;
		case "New AI Game":
			Object[] options = { "White", "Black", "Cancel" };
			int n = JOptionPane.showOptionDialog(this, "To play a game against the computer choose which side to play:", "Start AI Game", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, "White");
			if (n == 0) {
				field.startGame(1);
				gameOver = false;
			}
			if (n == 1) {
				field.startGame(2);
				gameOver = false;
			}
			break;
		}
	}

	// RENDERING
	public void paintComponent(Graphics g) {
		// Paint Board
		g.setColor(Color.blue);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		g.drawImage(board, 0, 0, this.getWidth(), this.getHeight(), null);
		field.paint(g);
	}

	// THREAD
	public void run() {
		while (true) {	
			
			repaint();

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
			
			checkGameOver();
		}
	}

	public static void main(String[] args) {
		new Display();
	}
}