import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.util.HashSet;

import javax.swing.ImageIcon;

/*
 * Java Draughts
 * 
 * Author: Thomas Auberson
 * Version: 0.12
 * 
 * This class controls the current field of pieces
 */

public class Field {

	// FIELDS
	private int[][] tiles = new int[8][8];
	private final int TILESIZE;

	private Coord selected; // Coord of currently selected tile
	private int currentTurn; // Whose turn it is: WHITE or BLACK
	private boolean inMultiJump = false;

	public HashSet<Coord> whitePieces = new HashSet<Coord>();
	public HashSet<Coord> blackPieces = new HashSet<Coord>();

	private AI ai;	// TODO Delete AI after game
	private boolean inAIGame;
	private int playerSide;

	// Pieces
	public final int EMPTY = 0;
	public final int WHITE = 1;
	public final int BLACK = 2;
	public final int WHITE_KING = 3; // White pieces are odd
	public final int BLACK_KING = 4; // Black pieces are even

	// Images
	private Image blackImg;
	private Image whiteImg;
	private Image blackKingImg;
	private Image whiteKingImg;

	// CONSTRUCTOR
	public Field(int size) {
		TILESIZE = size / 8;
		loadImages();
		startGame();
	}

	// LOAD IMAGES
	public void loadImages() {
		whiteImg = new ImageIcon("tex/White.png").getImage();
		blackImg = new ImageIcon("tex/Black.png").getImage();
		whiteKingImg = new ImageIcon("tex/White_King.png").getImage();
		blackKingImg = new ImageIcon("tex/Black_King.png").getImage();
	}

	// INTERACTIONS
	public void startGame() { // Start a Hotseat Game
		inAIGame = false;
		loadTiles();
		currentTurn = WHITE;
	}

	public void startGame(int n) { // Start an AI Game
		inAIGame = true;
		playerSide = n;
		loadTiles();
		currentTurn = WHITE;
		if (playerSide == WHITE)
			ai = new AI(this, blackPieces);
		else
			ai = new AI(this, whitePieces);
	}

	public void loadTiles() {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (((i + j) % 2 == 1) && j < 3) {
					tiles[i][j] = BLACK;
				} else if (((i + j) % 2 == 1) && j > 4) {
					tiles[i][j] = WHITE;
				} else {
					tiles[i][j] = EMPTY;
				}
			}
		}
	}

	public void selectTile(Point p) {
		if (!inMultiJump)
			selected = findTile(p);
	}

	public void selectTile(Coord c) {
		if (!inMultiJump)
			selected = c;
	}

	public boolean playerCanAct() {
		if (inAIGame && playerSide != currentTurn)
			return false;
		else
			return true;
	}

	public boolean aiCanAct() {
		if (inAIGame && playerSide != currentTurn)
			return true;
		else
			return false;
	}

	public void deselectTile() {

		selected = null;
		if (inMultiJump) {
			// Change Turn
			inMultiJump = false;
			if (currentTurn == WHITE)
				currentTurn = BLACK;
			else
				currentTurn = WHITE;
		}
	}

	public Coord findTile(Point p) {
		for (int i = 0; i < 8; i++) {
			if (i * TILESIZE <= p.x && (i + 1) * TILESIZE > p.x) {
				for (int j = 0; j < 8; j++) {
					if (j * TILESIZE <= p.y && (j + 1) * TILESIZE > p.y) {
						return new Coord(i, j);
					}
				}
			}
		}
		return null;
	}

	public void moveTo(Point p) {
		moveTo(findTile(p));		
	}
	
	public void moveTo(Coord target) {
		tiles[target.x][target.y] = tiles[selected.x][selected.y];
		tiles[selected.x][selected.y] = EMPTY;
		// If a jump is made remove taken piece
		if ((Math.abs(target.x - selected.x) == 2) && (Math.abs(target.y - selected.y) == 2)) {
			Coord middle = new Coord(((target.x + selected.x) / 2), ((target.y + selected.y) / 2));
			tiles[middle.x][middle.y] = EMPTY;

			selected = target;
			if (canTakePiece()) {// Multi-taking condition 
				inMultiJump = true;
				return;
			}
		}

		// King Me!
		if ((tiles[target.x][target.y] == WHITE) && (target.y == 0))
			tiles[target.x][target.y] = WHITE_KING;
		if ((tiles[target.x][target.y] == BLACK) && (target.y == 7))
			tiles[target.x][target.y] = BLACK_KING;

		// Change Turn
		if (currentTurn == WHITE)
			currentTurn = BLACK;
		else
			currentTurn = WHITE;
		inMultiJump = false;

		deselectTile();
	}

	public boolean canTakePiece() {
		int x = selected.x;
		int y = selected.y;
		if (canMoveTo(new Coord(x + 2, y + 2)))
			return true;
		if (canMoveTo(new Coord(x + 2, y - 2)))
			return true;
		if (canMoveTo(new Coord(x - 2, y + 2)))
			return true;
		if (canMoveTo(new Coord(x - 2, y - 2)))
			return true;
		return false;
	}

	public boolean canMoveTo(Coord target) {
		if (!pieceIsSelected())
			return false; // A piece must be selected
		if ((tiles[selected.x][selected.y] % 2 == 1) && (currentTurn == BLACK))
			return false; // White pieces cannot move in Black turn
		if ((tiles[selected.x][selected.y] % 2 == 0) && (currentTurn == WHITE))
			return false; // Black pieces cannot move in White turn
		if (!coordIsValid(target))
			return false; // Target must be on board
		if ((target.x + target.y) == 0)
			return false; // Target tile must be black not white
		if (tiles[target.x][target.y] != EMPTY)
			return false; // Target tile must be empty
		if ((tiles[selected.x][selected.y] == WHITE) && ((target.y - selected.y) > 0))
			return false; // WHITE pieces cannot move down
		if ((tiles[selected.x][selected.y] == BLACK) && ((target.y - selected.y) < 0))
			return false; // BLACK pieces cannot move up

		if ((Math.abs(target.x - selected.x) == 1) && (Math.abs(target.y - selected.y) == 1))
			return true; // If target tile is adjacent can move

		if ((Math.abs(target.x - selected.x) == 2) && (Math.abs(target.y - selected.y) == 2)) {
			Coord middle = new Coord(((target.x + selected.x) / 2), ((target.y + selected.y) / 2));
			if (tiles[middle.x][middle.y] != EMPTY) {
				if (tiles[middle.x][middle.y] % 2 == tiles[selected.x][selected.y] % 2) {
					return false; // Cannot jump over friendly pieces
				} else {
					return true; // If a jump can be made can move
				}
			}
		}

		return false; // No positive conditions met
	}

	public boolean coordIsValid(Coord c) {
		if (c.x > 7 || c.x < 0)
			return false;
		if (c.y > 7 || c.y < 0)
			return false;
		return true;
	}

	public boolean pieceIsSelected() { // Return true if there is a piece
										// currently selected
		if (selected == null)
			return false;
		if (tiles[selected.x][selected.y] == EMPTY)
			return false;
		return true;
	}

	// RENDERING
	public void paint(Graphics g) {
		// Paint Selection Tile (Red square)
		if (selected != null) {
			if (currentTurn == WHITE)
				g.setColor(Color.red);
			if (currentTurn == BLACK)
				g.setColor(Color.green);
			if (playerCanAct())
				g.fillRect(selected.x * TILESIZE, selected.y * TILESIZE, TILESIZE, TILESIZE);
		}

		// Paint Pieces & Update piece sets
		whitePieces.clear();
		blackPieces.clear();

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (tiles[i][j] == WHITE) {
					g.drawImage(whiteImg, i * TILESIZE, j * TILESIZE, TILESIZE, TILESIZE, null);
					whitePieces.add(new Coord(i, j));
				} else if (tiles[i][j] == BLACK) {
					g.drawImage(blackImg, i * TILESIZE, j * TILESIZE, TILESIZE, TILESIZE, null);
					blackPieces.add(new Coord(i, j));
				} else if (tiles[i][j] == WHITE_KING) {
					g.drawImage(whiteKingImg, i * TILESIZE, j * TILESIZE, TILESIZE, TILESIZE, null);
					whitePieces.add(new Coord(i, j));
				} else if (tiles[i][j] == BLACK_KING) {
					g.drawImage(blackKingImg, i * TILESIZE, j * TILESIZE, TILESIZE, TILESIZE, null);
					blackPieces.add(new Coord(i, j));
				}
			}
		}
	}
}
