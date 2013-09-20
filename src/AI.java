import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

public class AI implements Runnable {

	// FIELDS
	private int counter;
	private int thinkTimer;
	private Thread thread = new Thread(this);
	private Random random = new Random();

	private HashSet<Coord> pieces;

	private Field field;

	private final int THINK_TIME_MIN = 10, THINK_TIME_MAX = 50;

	// CONSTRUCTOR
	public AI(Field field, HashSet<Coord> pieces) {
		this.pieces = pieces;
		this.field = field;
		setThinkTimer();
		thread.start();
	}

	public void setThinkTimer() {
		thinkTimer = random.nextInt((THINK_TIME_MAX - THINK_TIME_MIN)) + THINK_TIME_MIN;
		counter = 0;
	}

	// ACTIONS
	public void act() {
		System.out.println("Act...?");
		if (!field.aiCanAct())
			return;
		// Check first for pieces that can take another piece
		System.out.println("Act!");
		for (Coord c : pieces) {
			field.selectTile(c);
			int x = c.x;
			int y = c.y;
			ArrayList<Coord> targets = new ArrayList<Coord>();
			targets.add(new Coord(x + 2, y + 2));
			targets.add(new Coord(x - 2, y + 2));
			targets.add(new Coord(x + 2, y - 2));
			targets.add(new Coord(x - 2, y - 2));
			Collections.shuffle(targets);

			for (Coord target : targets) {
				if (field.canMoveTo(target)) {
					field.moveTo(target);
					return;
				}
			}
		}
		for (Coord c : pieces) {
			field.selectTile(c);
			int x = c.x;
			int y = c.y;
			ArrayList<Coord> targets = new ArrayList<Coord>();
			targets.add(new Coord(x + 1, y + 1));
			targets.add(new Coord(x - 1, y + 1));
			targets.add(new Coord(x + 1, y - 1));
			targets.add(new Coord(x - 1, y - 1));
			Collections.shuffle(targets);

			for (Coord target : targets) {
				if (field.canMoveTo(target)) {
					field.moveTo(target);
					return;
				}
			}
		}
		System.out.println("COMPUTER SAYS NO!");
	}

	// THREAD
	public void run() {
		while (true) {

			counter++;

			if (counter >= thinkTimer) {
				setThinkTimer();
				act();
			}

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
	}
}
