import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import static java.lang.Math.*;

/*
	type: 	
   		
   		0.Sword
   		1.Bow
   		2.Hammer
   		3.Defense
   		4.Treatment
   		5.Rage
   		6.None
   		7.InvalidBlock
   		
  gameState:		0:initial
  					1:player round
  					2:monster round
  					3:end
  				
  matchScore:	0-7, refer to type
  				clear after each N round
  */

class Cell {
	public int x, y, type, matched, i, j;

	/*
	 * x = row y = col i = x coor j = y coor
	 */
	public Cell(int x, int y) {
		this.x = x;
		this.y = y;
	}

}

class Board {
	Cell[][] board;
	int[] matchScore;
	int x, y, row, col, gameState, roundNum, totalRound, tileSize;

	public Board() {
		gameState = 0;
	}

	public Cell getCell(int x, int y) {
		return board[x][y];
	}

	public int[] getScore() {
		return matchScore;
	}

	public void clearScore() {
		matchScore = new int[8];
	}

	public void creatBoard(int row, int col) {
		this.row = row;
		this.col = col;
		tileSize = 54;
		clearScore();
		roundNum = 0;
		totalRound = 0;
		initialcells();

	}

	// return has matched and change cell's matched
	public boolean detectMatches() {
		boolean hasMatches = false;
		int count;
		// check row
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col - 2; j++) {
				count = 1;
				for (int k = j + 1; k < col; k++) {
					if (board[i][j].type == board[i][k].type) {
						count++;
					} else {
						//
						break;
					}
				}
				if (count >= 3) {
					hasMatches = true;
					for (int k = j; k < j + count; k++) {
						board[i][k].matched += count;
					}
					j = j + count - 1;
				}
				count = 0;

			}
		}
		// check col
		for (int j = 0; j < col; j++) {
			for (int i = 0; i < row - 2; i++) {
				count = 1;
				for (int k = i + 1; k < row; k++) {
					if (board[i][j].type == board[k][j].type) {
						count++;
					} else {
						//
						break;
					}
				}
				if (count >= 3) {
					hasMatches = true;
					for (int k = i; k < i + count && k < row; k++) {
						board[k][j].matched += count;
					}
					i = i + count - 1;
				}
				count = 0;

			}
		}

		return hasMatches;
	}

	public void displayBoard() {
		System.out.print("\n    ");
		for (int y = 0; y < col; y++) {
			System.out.printf("%-2d", y);
		}
		System.out.println();

		System.out.print("   ");
		for (int y = 0; y < col; y++) {
			System.out.print("--");
		}
		System.out.println();

		for (int x = 0; x < row; x++) {
			System.out.printf("%-2c| ", 'A' + x);
			for (int y = 0; y < col; y++) {
				System.out.printf("%-2d", board[x][y].type);
			}
			System.out.println();
		}
	}

	// test print score plane
	public void displayMatch() {
		System.out.print("\n    ");
		for (int y = 0; y < col; y++) {
			System.out.printf("%-2d", y);
		}
		System.out.println();

		System.out.print("   ");
		for (int y = 0; y < col; y++) {
			System.out.print("--");
		}
		System.out.println();

		for (int x = 0; x < row; x++) {
			System.out.printf("%-2c| ", 'A' + x);
			for (int y = 0; y < col; y++) {
				System.out.printf("%-2d", board[x][y].matched);
			}
			System.out.println();
		}
	}

	public void resetCellMatch() {
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				board[i][j].matched = 0;
			}
		}
	}

	public int getRound() {
		return roundNum;
	}

	public void setDead() {
		int deadcell = 0;
		for (int x = 0; x < row; x++) {
			for (int y = 0; y < col; y++) {
				board[x][y].type = deadcell % 6;
				deadcell++;
			}
		}
	}

	public void initialcells() {
		gameState = 0;
		board = new Cell[row][col];
		for (int x = 0; x < row; x++) {
			for (int y = 0; y < col; y++) {
				board[x][y] = new Cell(x, y);
				randomCell(x, y);
				board[x][y].matched = 0;
				board[x][y].i = y * tileSize;
				board[x][y].j = x * tileSize;
				//////// set coordinate//////////////

			}
		}
		// Make sure there are no 3 matches
		while (detectMatches()) {
			eliminateCells();
		}
		;
		for (int x = 0; x < row; x++) {
			for (int y = 0; y < col; y++) {
				board[x][y].i = y * tileSize;
				board[x][y].j = (x - 0) * tileSize;
			}
		}
		gameState = 1;
	}

	private void randomCell(int x, int y) {
		Random random = new Random();
		// random type 0 to 5(no Boundary and InvalidBlock)
		int randomtype = random.nextInt(5);
		if (randomtype == 4) {
			randomtype = random.nextInt(2) + 4;
		}
		board[x][y].type = randomtype;
	}

	/////////////////////// To Be Done//////////////////////////////////////
	public boolean animationMove() {
		boolean hasMove = false;
		int pixelsPerMove = 2;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				Cell cell = board[i][j];
				int dx = 0, dy = 0;
				dx = cell.i - cell.y * tileSize;
				dy = cell.j - cell.x * tileSize;

				if (dx != 0) {
					int moveAmount = min(pixelsPerMove, abs(dx));
					cell.i -= Integer.signum(dx) * moveAmount;
					hasMove = true;
				}

				if (dy != 0) {
					int moveAmount = min(pixelsPerMove, abs(dy));
					cell.j -= Integer.signum(dy) * moveAmount;
					hasMove = true;
				}
			}
		}
		return hasMove;
	}

	public void eliminateCells() {
		int count;
		// check column
		for (int j = 0; j < col; j++) {

			count = 0;
			// last row to 2nd row
			for (int i = row - 1; i >= count; i--) {
				// eliminated cell
				if (board[i][j].matched > 0) {
					count++;
					// update score
					if (gameState == 1) {
						if (board[i][j].type < 6)
							matchScore[board[i][j].type]++;
					}
					// System.out.println("eliminate:" + i +" " + j + " type=" + board[i][j].type);
					// reset matched
					board[i][j].matched = 0;
					// displayMatch();
					// reset type
					randomCell(i, j);
					// set coordinate
					board[i][j].j = count * -tileSize;

					// move to top
					if (i != 0) {
						for (int k = i; k > 0; k--) {
							swap(k - 1, j, k, j);
						}
						// re-check i-th
						i++;
					}
				}
			}
		}
		// score test
		// displayBoard();
		System.out.println("update:");
		for (int i = 0; i < 8; i++) {
			System.out.print(i + ":" + matchScore[i] + " ");
		}
		System.out.println();
	}

	public void setRound(int n) {
		roundNum = n;
	}

	public void swap(int x1, int y1, int x2, int y2) {
		Cell cell1 = board[x1][y1];
		Cell cell2 = board[x2][y2];
		cell1.x = x2;
		cell1.y = y2;
		cell2.x = x1;
		cell2.y = y1;
		board[x1][y1] = cell2;
		board[x2][y2] = cell1;
	}

}

public class match_3_Game extends JPanel implements Runnable, MouseListener {
	final int WIDTH = 740;
	final int HEIGHT = 480;

	boolean isRunning;
	Thread thread;
	BufferedImage view;

	static Board board;
	BufferedImage background, gems, cursor, bossS1, bossS2, HPBar, HP, HP_G, Rage;
	MouseEvent mouse;
	int tileSize = 54;
	int offsetX = 44, offsetY = 23;
	int x0, y0, x, y;
	int click = 0;
	int posX, posY;
	boolean swaping = false;
	boolean animationPlaying = false;
	boolean matched = false;
	boolean checkDead = false;
	double initialSpeed = 30;
	double currentSpeed = initialSpeed;
	int roundNum;
	int row = 8;
	int col = 8;
	int[] score;
	int[] newScore;
	int boss_state;
	int bossStateCount;
	int bossHP;
	int bossAttackValue;
	int bossDefenseValue;
	int playerHP;
	int playerRage;
	String[] typeName = { "Sword", "Bow", "Hammer", "DEF", "HEAL", "Rage", "None", "NULL" };

	public match_3_Game() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		addMouseListener(this);
	}

	public static void main(String[] args) {
		JFrame w = new JFrame("Match-3");
		w.setResizable(false);
		w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		w.add(new match_3_Game());
		w.pack();
		w.setLocationRelativeTo(null);
		w.setVisible(true);
	}

	@Override
	public void addNotify() {
		super.addNotify();
		if (thread == null) {
			thread = new Thread(this);
			isRunning = true;
			thread.start();
		}
	}

	// initial setting
	public void start() {
		try {
			view = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
			row = 8;
			col = 8;
			roundNum = 3;
			board = new Board();
			board.creatBoard(row, col);
			// board.displayBoard();
			// board.setRound(roundNum);

			// Monster setting
			boss_state = 0;
			bossStateCount = 0;
			bossHP = 1000;
			bossAttackValue = 0;
			bossDefenseValue = 0;
			// Player setting
			playerHP = 100;
			playerRage = 0;

			// check dead every move
			checkDead = true;
			background = ImageIO.read(getClass().getResource("/background2.png"));
			gems = ImageIO.read(getClass().getResource("/gems2.png"));
			cursor = ImageIO.read(getClass().getResource("/cursor.png"));
			bossS1 = ImageIO.read(getClass().getResource("/The-guardian-atk-pretty.png"));
			bossS2 = ImageIO.read(getClass().getResource("/The-guardian-def-pretty.png"));
			HPBar = ImageIO.read(getClass().getResource("/HpBar.png"));

			HP = ImageIO.read(getClass().getResource("/Hp.png"));
			HP_G = ImageIO.read(getClass().getResource("/Hp_G.png"));
			Rage = ImageIO.read(getClass().getResource("/Rage.png"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// program logic
	public void update() {
		// Moving animation
		// modify speed by current speed
		while (animationPlaying) {
			movingAnimation();
			// detect if match
			detectMatch();
		}

		if (!animationPlaying) {
			// player round
			if (board.getRound() > 0) {
				// click to swap
				playerClickEvent();
			} else {

				// Monster round
				// monster state 0 = end round, 1 = attack, 2 = defense then heal, 3 = defense
				// then skill
				// boss decided next round move
				if (boss_state == 0) {

					Random random = new Random();

					// clear score
					board.clearScore();
					score = new int[8];
					bossAttackValue = 0;
					bossDefenseValue = 0;
					// random state 1 to 3
					int randomState = random.nextInt(3) + 3;
					bossStateCount += randomState;
					if (bossStateCount >= 10) {
						boss_state = random.nextInt(2) + 2;
						bossStateCount = 0;

						// Defense: Heal or Skill
						bossDefenseValue = random.nextInt(4) + 5;
						System.out.println("Boss Defense: " + bossDefenseValue);
						if (boss_state == 2) {
							// Heal
							System.out.println("Boss Prepare to Heal");
						} else {
							// Skill
							System.out.println("Boss Prepare to Release Skill");
						}

					} else {
						boss_state = 1;
						// ATK
						bossAttackValue = random.nextInt(3) + 3;
						System.out.println("Boss ATK: " + bossAttackValue);
					}
					System.out.println("Boss state:" + boss_state);
					// set player round number
					board.setRound(roundNum);

				} else {
					// Round End
					System.out.println("Round Score:");
					for (int i = 0; i < 8; i++) {
						System.out.print(typeName[i] + ":" + score[i] + " ");
					}
					System.out.println();
					// state end
					if (boss_state == 1) {
						if (score[3] < bossAttackValue) {
							bossAttackValue -= score[3];
							// BOSS ACK cause damage
							System.out.println("Player HP - " + bossAttackValue * 10);
							playerHP -= bossAttackValue * 10;
						}
					} else {
						if (bossDefenseValue > 0) {
							if (boss_state == 2) {
								// BOSS Heal
								System.out.println("Boss Heal!");
								bossHP += (bossHP - 1000) / 2;

							} else {
								// BOSS use Skill
								System.out.println("Boss Release Skill!");
							}

						}

					}

					boss_state = 0;

				}

			}
			if (checkDead) {
				// check dead every move
				avoidDead();
				checkDead = false;
			}
		}

	}

	// draw GUI
	public void draw() {
		Graphics2D g2 = (Graphics2D) view.getGraphics();

		// Must draw
		g2.drawImage(background, 0, 0, WIDTH, HEIGHT, null);
		// Boss HP font
		g2.setColor(Color.BLACK);
		g2.setFont(new Font("Arial", Font.BOLD, 12));
		g2.drawString(bossHP + "/" + 1000, 514, 28);
		// HP Bar
		g2.drawImage(HPBar, 480, 30, 250, 15, null);
		// boss HP
		g2.drawImage(HP, 512, 32, (bossHP * 216 / 1000), 11, null); // 100->variable:0-216(length)

		if (boss_state == 1) {
			g2.drawImage(bossS1, 480, 50, 250, 250, null);
		} else {
			g2.drawImage(bossS2, 480, 50, 250, 250, null);
		}

		// Boss say
		g2.setColor(Color.BLACK);
		g2.setFont(new Font("Arial", Font.BOLD, 16));
		g2.drawString("I will CRASH YOU!!!", 510, 90);

		// Player HP font
		g2.setFont(new Font("Arial", Font.BOLD, 12));
		g2.drawString(playerHP + "/" + 100, 514, 397);
		// Player HP Bar
		g2.drawImage(HPBar, 480, 400, 250, 15, null);
		g2.drawImage(HP_G, 512, 402, (playerHP * 216 / 100), 11, null);
		// Player Rage font
		g2.setFont(new Font("Arial", Font.BOLD, 11));
		g2.drawString("Rage:", 480, 430);
		// player Rage Bar
		g2.drawImage(HPBar.getSubimage(26, 0, 224, 15), 506, 420, 224, 15, null);
		// player Rage Icon
		g2.drawImage(Rage, 512, 422, (playerRage * 216 / 100), 11, null); // variable:216(length)
		if (playerRage >= 50) {
			BufferedImage ragee = zoomOutImage(gems.getSubimage(5 * 49, 0, 49, 49), 30, 30);
			g2.drawImage(ragee, 502 + (playerRage * 216 / 100) - 10, 412, null); // (x:502-712,y:412)
		}

		// May draw
		/*
		 * 
		 * if (loopnum > 0) { if (loopnum > 150) { g2.setColor(Color.BLACK);
		 * g2.setFont(new Font("Arial", Font.BOLD, 25)); g2.drawString("-50", 650, 70);
		 * for (int i = 1; i <= 3; i++) { BufferedImage zoomedOutImage =
		 * zoomOutImage(gems.getSubimage(0 * 49, 0, 49, 49), 40, 40);
		 * g2.drawImage(zoomedOutImage, 500 + i, 130 + i, null); }
		 * 
		 * for (int i = 1; i <= 3; i++) { BufferedImage zoomedOutImage =
		 * zoomOutImage(gems.getSubimage(1 * 49, 0, 49, 49), 40, 40);
		 * g2.drawImage(zoomedOutImage, 500 + i * random.nextInt(100), 130 + i *
		 * random.nextInt(70), null); } for (int i = 1; i <= 3; i++) { BufferedImage
		 * zoomedOutImage = zoomOutImage(gems.getSubimage(2 * 49, 0, 49, 49), 40, 40);
		 * g2.drawImage(zoomedOutImage, 500 + i * random.nextInt(100), 130 + i *
		 * random.nextInt(70), null); } } loopnum--; } else { loopnum = 250; }
		 */
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				g2.drawImage(gems.getSubimage(board.getCell(i, j).type * 49, 0, 49, 49),
						board.getCell(i, j).i + offsetX, board.getCell(i, j).j + offsetY, 49, 49, null);

				// Show cursor
				if (click == 1) {
					if (x0 == j && y0 == i) {
						g2.drawImage(cursor, board.getCell(i, j).i + offsetX, board.getCell(i, j).j + offsetY,
								cursor.getWidth(), cursor.getHeight(), null);
					}
				}
			}
		}

		Graphics g = getGraphics();
		g.drawImage(view, 0, 0, WIDTH, HEIGHT, null);
		g.dispose();
	}

	public BufferedImage zoomOutImage(BufferedImage originalImage, int zoomedWidth, int zoomedHeight) {
		BufferedImage zoomedOutImage = new BufferedImage(zoomedWidth, zoomedHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2Zoomed = zoomedOutImage.createGraphics();
		// Draw the zoomed-out image
		g2Zoomed.drawImage(originalImage, 0, 0, zoomedWidth, zoomedHeight, null);
		g2Zoomed.dispose();
		return zoomedOutImage;
	}

	// for player click to select swap cell
	public void playerClickEvent() {
		if (mouse != null && mouse.getID() == MouseEvent.MOUSE_PRESSED) {
			// Click to Swap
			if (mouse.getButton() == MouseEvent.BUTTON1) {
				if (!swaping && !animationPlaying) {
					click++;
				}
				posX = mouse.getX() - offsetX;
				posY = mouse.getY() - offsetY;
				// new select
				if (click == 1) {
					x0 = posX / tileSize;
					y0 = posY / tileSize;
				}
				// second select
				if (click == 2) {
					x = posX / tileSize;
					y = posY / tileSize;

					if (row > x && x >= 0 && col > y && y >= 0 && row > x0 && x0 >= 0 && col > y0 && y0 >= 0) {
						// in board
						if (abs(x - x0) + abs(y - y0) == 1) {
							board.swap(y0, x0, y, x);
							swaping = true;
							click = 0;
							currentSpeed = initialSpeed;
							animationPlaying = true;
							return;
						} else if (x == x0 && y == y0) {
							// cancel select
							click = 0;
						} else {
							// new select
							x0 = x;
							y0 = y;
							click = 1;
						}
					} else {
						// not in board
						click = 0;

					}
				}

			}
			mouse = null;
		}
		// if no event
		// Second swap if no match
		if (swaping && !animationPlaying) {
			if (!board.detectMatches()) {
				board.swap(y0, x0, y, x);
				currentSpeed = initialSpeed;
				animationPlaying = true;
			} else {
				// board.eliminateCells();
				board.roundNum--;
				currentSpeed = initialSpeed;
				// check dead every move
				checkDead = true;
				animationPlaying = true;
			}
			board.resetCellMatch();
			swaping = false;
		}

	}

	// check if there has match, then eliminate
	public void detectMatch() {
		if (!swaping && !animationPlaying && board.detectMatches()) {
			// Update Board
			board.eliminateCells();

			// Score Calculate
			newScore = board.getScore();
			for (int i = 0; i < 6; i++) {
				newScore[i] -= score[i];
				if (newScore[i] > 0) {
					System.out.println("Eliminate " + typeName[i] + " X " + newScore[i]);
					switch (i) {
					case 0:

						break;
					case 1:

						break;
					case 2:

						break;
					case 3:

						break;
					case 4:

						break;
					case 5:

						break;
					default:
						System.out.println("Invalid i");
					}
				}
			}
			//update Score
			score = board.getScore();
			// check dead every move
			checkDead = true;
			animationPlaying = true;
		}
	}

	// check if the board cannot be eliminate
	public void avoidDead() {
		boolean dead = true;

		// check column
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col - 1; j++) {
				// try swap
				board.swap(i, j, i, j + 1);
				dead = !board.detectMatches();
				board.swap(i, j, i, j + 1);
				board.resetCellMatch();
				// if not a dead board
				if (!dead) {
					// System.out.println("\nNo dead\\n");
					return;
				}

			}

		}

		// check row
		for (int j = 0; j < col; j++) {
			for (int i = 0; i < row - 1; i++) {
				// try swap
				board.swap(i, j, i + 1, j);
				dead = !board.detectMatches();
				board.swap(i, j, i + 1, j);
				board.resetCellMatch();
				// if not a dead board
				if (!dead) {
					// System.out.println("\\nNo dead\\n");
					return;
				}

			}

		}

		// initial cell
		if (dead) {
			System.out.println("\\nDead\\n");
			board.initialcells();
		}
	}

	// moving cell animation
	public void movingAnimation() {
		if (animationPlaying) {
			animationPlaying = board.animationMove();
			if (!animationPlaying) {
				return;
			}
			// draw animation
			draw();
			currentSpeed = currentSpeed * 1.004;
			try {
				if (currentSpeed > 37)
					currentSpeed = 37;
				Thread.sleep((int) (40 - currentSpeed));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		try {
			start();
			while (isRunning) {
				update();
				draw();
				Thread.sleep(1);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		mouse = e;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mouse = e;
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}
}
