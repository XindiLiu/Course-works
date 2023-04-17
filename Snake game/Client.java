import java.net.*;
import java.io.*;

public class Client {
	final static int KEY_DOWN = 0x102;
	final static int KEY_UP = 0x103;
	final static int KEY_LEFT = 0x104;
	final static int KEY_RIGHT = 0x105;
	final static int PADENTER = 0x1cb;

	public static void main(String[] args) throws Exception {

		byte[][] game;
		int boardLength;
		int boardHeight;
		int playerNumber;
		Graphic board;
		Socket sckt;
		try {
			if (args.length > 0) {
				if (args.length != 2) {
					throw new IllegalArgumentException("Invalid arguments. Correct format: java Client [host] [port]");
				}
				String host = args[0];
				int port;
				try {
					port = Integer.parseInt(args[1]);
				} catch (Exception e) {
					throw new IllegalArgumentException("Invalid arguments. Correct format: java Client [host] [port]");
				}
				sckt = new Socket(host, port);

			} else {
				sckt = new Socket("localhost", 1234);
			}
			final OutputStream scout = sckt.getOutputStream();
			final InputStream scin = sckt.getInputStream();
			playerNumber = scin.read();
			board = new Graphic(playerNumber + 1);
			board.init();
			print("Waiting for other players");
			boardHeight = scin.read();
			boardLength = scin.read();
			game = new byte[boardHeight][boardLength];
			//Ignore the first win or lose signal
			scin.read();
			for (int i = 0; i < boardHeight; i++) {
				scin.read(game[i]);
			}

			//print("read");
			board.startgame(boardLength, boardHeight);
			board.showGame(game);

			Thread readTangent = new Thread() {
				public void run() {
					while (true) {
						try {
							int c = board.getch();
							scout.write(c);
						} catch (Exception e) {
							System.out.println(e);
						}
					}
				}
			};
			readTangent.start();

			//Game loop
			while (true) {
				int on = scin.read();
				//If game is over
				if (on == 0) {
					int winner = scin.read();
					board.showResult(winner == playerNumber);
					Graphic.endwin();
					print("Game over, you " + (winner == playerNumber ? "win" : "lose"));
					break;
				} else {
					for (int i = 0; i < boardHeight; i++) {
						scin.read(game[i]);
					}

					board.showGame(game);
				}
			}
			System.exit(0);

		} catch (IOException ioe) {
			System.out.println("nagot IO fel intraffade!" + ioe);
			Graphic.endwin();
			System.exit(0);
		}
	}

	private static void print(Object o) {
		System.out.println(o);
	}

}
