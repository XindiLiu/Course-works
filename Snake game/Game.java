import java.io.*;
import java.net.*;
import java.util.*;

public class Game {
    int boardLength;
    int boardHeight;
    byte[][] gameBoard;
    Player[] players;
    int plCount;
    HashSet<Pii> items;
    HashSet<Pii> wall;
    final int numberItems = 20;
    boolean on;
    int winner;

    public class Player {
        public int x;
        public int y;
        public int len;
        public LinkedHashSet<Pii> body;
        public int dir;
        public boolean alive;
        public byte headChar;
        public byte bodyChar;

        public Player(int x, int y, int len, int dir, char h, char b) {
            this.x = x;
            this.y = y;
            this.len = len;
            this.dir = dir;
            this.body = new LinkedHashSet<>();
            alive = true;
            gameBoard[y][x] = (byte) h;
            headChar = (byte) h;
            bodyChar = (byte) b;
        }

        public String toString() {
            return "(" + y + "," + x + ")" + body.toString();
        }

        public void extend(int dir) {
            body.add(new Pii(x, y));
            gameBoard[y][x] = bodyChar;
            switch (dir) {
                case 0:
                    x++;
                    break;
                case 1:
                    y--;
                    break;
                case 2:
                    x--;
                    break;
                case 3:
                    y++;
                    break;
            }
            gameBoard[y][x] = headChar;
            this.dir = dir;
            len++;
        }

        public void move(int dir) {
            body.add(new Pii(x, y));
            gameBoard[y][x] = bodyChar;
            switch (dir) {
                case 0:
                    x++;
                    break;
                case 1:
                    y--;
                    break;
                case 2:
                    x--;
                    break;
                case 3:
                    y++;
                    break;
            }
            gameBoard[y][x] = headChar;
            Iterator<Pii> it = body.iterator();

            Pii temp = it.next();
            body.remove(temp);
            gameBoard[temp.y][temp.x] = 0;
            this.dir = dir;
        }
    }

    //Class for coordinates in gameboard
    public static class Pii {
        public int x;
        public int y;

        public Pii(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int hashCode() {
            return (x << 16) + y;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Pii) {
                Pii p = (Pii) obj;
                return this.x == p.x && this.y == p.y;
            } else
                return false;
        }

        public boolean equals(int x, int y) {
            return this.x == x && this.y == y;
        }

        public String toString() {
            return "(" + y + "," + x + ")";
        }
    }

    public Game(int pl, int hei, int len) {
        boardHeight = hei;
        boardLength = len;
        gameBoard = new byte[boardHeight][boardLength];
        plCount = pl;
        players = new Player[pl];
        // wall = buildWall();
        wall = new HashSet<Pii>();
        players[0] = new Player(0, 0, 0, 0, 'A', 'a');
        players[1] = new Player(boardLength - 1, boardHeight - 1, 0, 2, 'B', 'b');
        items = generateItems(numberItems);

        on = true;

    }

    public void checkKill() {
        for (int i = 0; i < plCount; i++) {
            for (int j = 0; j < plCount; j++) {
                if (i != j) {
                    Pii next = null;

                    switch (players[j].dir) {
                        case 0:
                            next = pii(players[j].x + 1, players[j].y);
                            break;
                        case 1:
                            next = pii(players[j].x, players[j].y - 1);
                            break;
                        case 2:
                            next = pii(players[j].x - 1, players[j].y);
                            break;
                        case 3:
                            next = pii(players[j].x, players[j].y + 1);
                            break;
                    }
                    //print("   " +next.toString());
                    if (players[i].body.contains(next) || players[j].body.contains(next)) {
                        on = false;
                        winner = i;
                    }
                }
            }
        }
    }

    //Check if a movement is blocked or not, do movement if not blocked.
    public boolean playerMove(int pn, int dir) {
        //print("Try move:" + String.valueOf(dir));
        //player can not turn back if it has any body
        if (players[pn].len > 0 && (dir + 2) % 4 == players[pn].dir)
            return false;
        Pii next = null;
        boolean notGoPlayer = true;
        boolean notGoOutside = true;
        switch (dir) {
            case 0:
                next = pii(players[pn].x + 1, players[pn].y);
                notGoOutside = players[pn].x + 1 < boardLength;
                break;
            case 1:
                next = pii(players[pn].x, players[pn].y - 1);
                notGoOutside = players[pn].y >= 1;
                break;
            case 2:
                next = pii(players[pn].x - 1, players[pn].y);
                notGoOutside = players[pn].x >= 1;
                break;
            case 3:
                next = pii(players[pn].x, players[pn].y + 1);
                notGoOutside = players[pn].y + 1 < boardHeight;
                break;
        }
        print("Move target:" + next.toString());
        for (int i = 0; i < plCount; i++) {
            if (i != pn) {
                //Check if blocked by other players
                if (next.equals(players[i].x, players[i].y)) {
                    notGoPlayer = false;
                    print("Blocked by other player");
                }
                //Specialcase: go in other players body with a turn. Change the players direction and handle in checkkill()
                if (players[i].body.contains(next)) {
                    //print(dir);

                    players[pn].dir = dir;
                    //print(players[pn].dir);
                    return true;
                }
            } else {
                //Check if blocked by body of itself
                if (players[i].body.contains(next)) {
                    notGoPlayer = false;
                    print("Blocked by self");
                    return true;
                }

            }
        }
        //If not blocked by anything
        if (notGoOutside && !wall.contains(next) && notGoPlayer) {
            if (items.contains(next)) {
                players[pn].extend(dir);
                print("Player extended new length:" + String.valueOf(players[pn].len));
            } else {
                players[pn].move(dir);
            }
            //print(players[pn]);
            return true;
        } else
            return false;
    }

    //Generate items for extend layer length. 
    private HashSet<Pii> generateItems(int n) {
        Random random = new Random();
        int area = boardLength * boardHeight;
        HashSet<Pii> items = new HashSet<>();
        for (int i = 0; i < n; i++) {
            int p = random.nextInt(area);
            int row = p % boardHeight;
            int col = p / boardHeight;
            Pii it = new Pii(col, row);
            //Items should not overlap walls or players
            if (!(wall.contains(it) || it.equals(0, 0) || it.equals(boardLength - 1, boardHeight - 1))) {
                items.add(it);
                gameBoard[row][col] = '+';
            }

        }
        return items;
    }

    //wall is a cross in centre of the board
    private HashSet<Pii> buildWall() {
        HashSet<Pii> wall = new HashSet<>();
        final Pii c = new Pii(boardLength / 2, boardHeight / 2);
        wall.add(c);
        gameBoard[c.y][c.x] = '#';
        for (int i = 1; i < Math.min(boardLength / 5, boardHeight / 5); i++) {
            wall.add(new Pii(c.x + i, c.y + i));
            wall.add(new Pii(c.x - i, c.y + i));
            wall.add(new Pii(c.x + i, c.y - i));
            wall.add(new Pii(c.x - i, c.y - i));
            gameBoard[c.y + i][c.x + i] = '#';
            gameBoard[c.y - i][c.x + i] = '#';
            gameBoard[c.y + i][c.x - i] = '#';
            gameBoard[c.y - i][c.x - i] = '#';
        }
        return wall;
    }

    private Pii pii(int x, int y) {
        return new Pii(x, y);
    }

    private static void print(Object o) {
        System.out.println(o);
    }

}
