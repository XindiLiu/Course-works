import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
	static final int boardLength = 20;
	static final int boardHeight = 20;
	static final int playerCount = 2;
	static volatile int[] move = new int[2];
	static volatile boolean moveflag = false;

	public static void main(String[] args) throws Exception {
		ServerSocket serverSckt = new ServerSocket(1234);
		Socket[] sckt = new Socket[playerCount];
		InputStream[] scin = new InputStream[playerCount];
		OutputStream[] scout = new OutputStream[playerCount];
		//Thread for reading tangent inputs from clients
		Thread[] clin = new Thread[playerCount];
		//Thread[] disconnect = new Thread[playerCount];

		//Connect clients
		for (int i = 0; i < playerCount; i++) {
			sckt[i] = serverSckt.accept();
			scin[i] = sckt[i].getInputStream();
			scout[i] = sckt[i].getOutputStream();
			System.out.printf("Player %d connected\n", i + 1);
			//Send playernumber
			scout[i].write(i);
		}
		//sckt[0].close();

		//Create game
		Game game = new Game(playerCount, boardHeight, boardLength);
		System.out.printf("New game. %d players. Gameboard %d * %d\n", playerCount, boardHeight, boardLength);

		//initialize clin.
		for (int i = 0; i < playerCount; i++) {
			final int ii = i;
			clin[i] = new Thread() {
				public void run() {
					try {
						while (true) {
							int a = (int) scin[ii].read();
							//if(game.on){
							if (0 <= a && a < 4) {
								synchronized (this) {
									System.out.printf("Player %d pressed %d\n", ii + 1, a);
									while (moveflag) {
									}
									move[0] = ii;
									move[1] = a;
									moveflag = true;
								}
							} else {
								print("Wrong data from player " + String.valueOf(ii));
							}
						}
					} catch (IOException e) {
						System.out.println(e);
						System.exit(1);
					}
				}
			};

		}

		//Send basic game information
		scout[0].write(boardHeight);
		scout[1].write(boardHeight);
		scout[0].write(boardLength);
		scout[1].write(boardLength);
		clin[0].start();
		clin[1].start();
		for (int i = 0; i < playerCount; i++) {
			sendGame(game, scout[i]);
		}
		Thread[] send = new Thread[playerCount];
		while (game.on) {
			while (!moveflag) {
			}
			//If move is not blocked
			if (game.playerMove(move[0], move[1])) {

				game.checkKill();
				System.out.printf("Player %d move to %d succesed.\n", move[0], move[1]);
				for (int i = 0; i < playerCount; i++) {
					//sendGameThread(game, scout[i]);
					final int ii = i;
					send[i] = new Thread() {
						public void run() {
							sendGame(game, scout[ii]);
						}
					};
					send[i].start();
				}
				for (int i = 0; i < playerCount; i++) {
					send[i].join();
				}
			} else
				print("Move failed");
			moveflag = false;

		}
		print("Game over winner: " + String.valueOf(game.winner));
		//clin[0].interrupt();
		//clin[1].interrupt();
		//sendGame(game, scout[0]);
		//sendGame(game, scout[1]);
		//sckt[ii].close();

		for (int i = 0; i < playerCount; i++) {
			sckt[i].close();
		}
	}

	public static void sendGame(Game game, OutputStream out) {
		try {
			if (game.on) {
				out.write(game.on ? 1 : 0);
				for (int i = 0; i < game.boardHeight; i++) {
					out.write(game.gameBoard[i]);
				}
			} else {
				out.write(game.on ? 1 : 0);
				out.write(game.winner);
			}
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	private static void print(Object o) {
		System.out.println(o);
	}

}
