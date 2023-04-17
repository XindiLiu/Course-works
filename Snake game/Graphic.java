import java.util.*;
import java.awt.GridLayout;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.*;
import java.util.function.Consumer;
import java.awt.*;
import java.awt.event.*;

public class Graphic {
	public long win;
	int width;
	int height;
	int playerNumber;
	JFrame window;
	Block[][] blocks;
	ArrayList<ArrayList<Block>> Grid;
	int pressedKey;

	public static void main(String[] args) {
		Graphic u = new Graphic(1);
		u.init();
		byte[][] game = new byte[20][20];
		for (int i = 0; i < 20; ++i) {
			for (int j = 0; j < 20; ++j) {
				game[i][j] = 0;
			}
		}
		for (int i = 0; i < 20; ++i) {
			game[10][i] = 1;
		}
		u.startgame(20, 20);
		u.showGame(game);

	}

	public Graphic(int playerNumber) {

		this.playerNumber = playerNumber;
		window = new JFrame(String.format("Snake game Player %d", playerNumber));
		window.setSize(300, 300);
		window.setVisible(true);
		Consumer<Integer> keypressed = x -> {
			synchronized (this) {
				pressedKey = x;
				notifyAll();
			}
		};
		window.addKeyListener((KeyListener) new KeyboardInput(keypressed));
	}

	public void init() {
		window.getContentPane().removeAll();
		window.setLayout(new GridLayout(1, 1));
		JLabel text = new JLabel("Waiting for other players", SwingConstants.CENTER);
		window.add(text);
		window.revalidate();
		window.repaint();
	}

	public void startgame(int width, int height) {
		this.width = width;
		this.height = height;
		window.getContentPane().removeAll();
		window.repaint();
		window.setLayout(new GridLayout(height, width, 0, 0));
		blocks = new Block[height][width];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				blocks[i][j] = new Block();
			}
		}
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				window.add(blocks[i][j]);
			}
		}
	}

	public void showGame(byte[][] game) {
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				blocks[i][j].update(game[i][j]);
			}
		}
	}

	public void showResult(boolean win) {
		window.getContentPane().removeAll();
		window.repaint();
		window.setLayout(new GridLayout(1, 1));
		JLabel text = new JLabel("Game over, you " + (win ? "win" : "lose"));
		window.add(text);
	}

	public int getch() throws Exception {
		synchronized (this) {
			wait();
			return pressedKey;
		}
	}

	public static void endwin() {
		return;
	}
}
