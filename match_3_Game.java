import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.time.Instant;
import static java.lang.Math.*;

/*
	type: 	
   		
   		0.Sword
   		1.Hammer
   		2.Bow
   		3.Defense
   		4.Treatment
   		5.Rage
   		6.None
   		7.InvalidBlock
   		
  gameState:		0:initial
  					1:start

  				
  matchScore:	0-7, refer to type
  				clear after each N round
  */

class Cell {
	public int x, y, type, matched, i, j;
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
		int[] returnScore = new int[8];
		for (int i = 0; i < 8; i++) {
			returnScore[i] = matchScore[i];
		}
		return returnScore;
	}

	public void clearScore() {
		matchScore = new int[8];
		for (int i = 0; i < 8; i++) {
			matchScore[i] = 0;
		}
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
		//System.out.println("update:");
		//for (int i = 0; i < 8; i++) {
		//	System.out.print(i + ":" + matchScore[i] + " ");
		//}
		//System.out.println();
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
	int hint_x0,hint_y0,hint_x1,hint_y1;
	boolean swaping = false;
	boolean animationPlaying = false;
	boolean bossAnimationPlaying = false;
	boolean matched = false;
	boolean checkDead = false;
	double initialSpeed = 30;
	double currentSpeed = initialSpeed;
	int roundNum;
	int row = 8;
	int col = 8;
	int[] score;
	int[] newScore;
	int boss_state, bossStateCount, bossHP, bossAttackValue, bossDefenseValue, boss_x_coordinate, boss_y_coordinate;
	int playerHP, playerRage, rageState, rageRound;
	// 0 None 1:PlayerWin 2:BossWin
	int gameEnd;
	int[] hitPoints_x;
	int[] hitPoints_y;
	Instant bossHitAnimationTime;
	Instant currentTime;
	Instant hintTime;
	boolean hint;
	Instant[] hitPoint;
	String[] typeName = { "Sword", "Hammer", "Bow", "DEF", "HEAL", "Rage", "None", "NULL" };

	public match_3_Game() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		addMouseListener(this);
	}

	public static void main(String[] args) {
		JFrame w = new JFrame("Match-3 Monster Battle");
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
			gameEnd = 0;
			view = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
			row = 8;
			col = 8;
			roundNum = 3;
			board = new Board();
			board.creatBoard(row, col);
			// board.displayBoard();
			// board.setRound(roundNum);
			hint = false;
			// Monster setting
			boss_state = 0;
			bossStateCount = 0;
			bossHP = 1000;
			bossAttackValue = 0;
			bossDefenseValue = 0;
			// Player setting
			playerHP = 100;
			playerRage = 0;
			boss_x_coordinate = 480;
			boss_y_coordinate = 0;
			hitPoints_x = new int[3];
			hitPoints_y = new int[3];
			// check dead every move
			checkDead = true;
			rageState = rageRound = 0;
			currentTime = Instant.now();
			bossHitAnimationTime = currentTime;
			hintTime = currentTime.plusSeconds(30);
			hitPoint = new Instant[3];
			for (int i = 0; i < 3; i++) {
				hitPoint[i] = currentTime;
			}

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
				// monster state 0 = end round, 1 = attack, 2 = defense then heal, 3 = defense then heal release skill
				// then skill
				// boss decided next round move
				if (boss_state == 0) {

					Random random = new Random();

					// clear score
					board.clearScore();
					score = board.getScore();
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
						//System.out.println("Boss Defense: " + bossDefenseValue);
						if (boss_state == 2) {
							// Heal
							//System.out.println("Boss Prepare to Heal");
						} else {
							// Skill
							//System.out.println("Boss Prepare to Release Skill");
							
						}

					} else {
						boss_state = 1;
						// ATK
						bossAttackValue = random.nextInt(5) + 3;
						//System.out.println("Boss ATK: " + bossAttackValue);
					}
					//System.out.println("Boss state:" + boss_state);
					// set player round number
					board.setRound(roundNum);

				} else {
					// Round End
					// wait
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					score = board.getScore();
					//System.out.println("Round Score:");
					//for (int i = 0; i < 8; i++) {
					//	System.out.print(typeName[i] + ":" + score[i] + " ");
					//}
					//System.out.println();
					// state end
					if (boss_state == 1) {
						// Boss Attack Animation
						bossAttackAnimation();
						if (score[3] < bossAttackValue) {
							bossAttackValue -= score[3];

							// BOSS ACK cause damage
							//System.out.println("Player HP - " + bossAttackValue * 10);
							playerHP = max(0, playerHP - bossAttackValue * 10);
							playerRage = min(100, playerRage + 10);

						}
					} else {
						if (bossDefenseValue > 0) {
							if (boss_state == 2) {
								// BOSS Heal
								//System.out.println("Boss Heal!");
								bossHP += (1000 - bossHP) / 2;

							} else {
								// BOSS use Skill
								//System.out.println("Boss Release Skill!");
								int totalInvalidBlock = 8;
								Random random = new Random();
								for(int i = 0; i < row; i ++){
									for(int j = 0; j < col; j ++){
										if(random.nextInt(10) == 0){
											board.getCell(i, j).type = 6;
											totalInvalidBlock --;
											if(totalInvalidBlock <= 0){
												break;
											}
										}
										if(totalInvalidBlock <= 0){
											break;
										}
									}
								}
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

			// WIN AND LOSE
			if (playerHP <= 0) {
				gameEnd = 2;
				isRunning = false;

			} else if (bossHP <= 0) {
				gameEnd = 1;
				isRunning = false;
			}

		}

	}

	public void bossAttackAnimation() {
		bossAnimationPlaying = true;
		while (boss_y_coordinate < 40) {
			boss_y_coordinate += 2;
			draw();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// display 0.5sec
		currentTime = Instant.now();
		bossHitAnimationTime = currentTime.plusMillis(500);
		while (boss_y_coordinate > 0) {
			boss_y_coordinate -= 2;
			draw();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	// draw GUI
	public void draw() {
		currentTime = Instant.now();
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
			g2.drawImage(bossS1, boss_x_coordinate, boss_y_coordinate, 250, 250, null);
			// boss atk icon
			g2.drawImage(gems.getSubimage(0 * 49, 48, 49, 49), 600, 230, 70, 70, null);
			g2.setFont(new Font("Arial", Font.BOLD, 25));
			g2.drawString(":" + bossAttackValue, 670, 273);
			// Boss say
			Color brown = new Color(139, 69, 19);
			g2.setColor(brown);
			g2.setFont(new Font("Arial", Font.BOLD, 16));
			g2.drawString("I will CRASH YOU !!!", 550, 60);
		} else {
			g2.drawImage(bossS2, boss_x_coordinate, boss_y_coordinate, 250, 250, null);
			// boss shield icon
			g2.drawImage(gems.getSubimage(3 * 49, 48, 49, 49), 500, 230, 70, 70, null);
			g2.setFont(new Font("Arial", Font.BOLD, 20));
			g2.drawString("" + bossDefenseValue, 530, 273);
			if (boss_state == 2) {
				// Boss say
				Color brown = new Color(139, 69, 19);
				g2.setColor(brown);
				g2.setFont(new Font("Arial", Font.BOLD, 16));
				g2.drawString("I will HEAL MYSELF !", 550, 60);
			} else if (boss_state == 3) {
				// Boss say
				Color brown = new Color(139, 69, 19);
				g2.setColor(brown);
				g2.setFont(new Font("Arial", Font.BOLD, 16));
				g2.drawString("I will RELEASE SKILL !", 550, 60);
			}
		}
		// play shield
		g2.drawImage(gems.getSubimage(3 * 49, 0, 49, 49), 480, 345, 40, 40, null);
		g2.setFont(new Font("Arial", Font.BOLD, 16));
		g2.drawString(":" + score[3], 515, 370);
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

		// draw Cells and cursor
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				g2.drawImage(gems.getSubimage(board.getCell(i, j).type * 49, 0, 49, 49),
						board.getCell(i, j).i + offsetX, board.getCell(i, j).j + offsetY, 49, 49, null);

				// Show cursor
				if (click == 1) {
					if (x0 == j && y0 == i) {
						g2.drawImage(cursor.getSubimage(49 * rageState,0,49,49), board.getCell(i, j).i + offsetX, board.getCell(i, j).j + offsetY, 54, 54, null); 
					}
				}
			}
		}


		// May draw
		//hint
		if (Instant.now().isAfter(hintTime)) {
			g2.drawImage(cursor.getSubimage(0,0,49,49), hint_x0 + offsetX, hint_y0 + offsetY, 54, 54, null); 
			g2.drawImage(cursor.getSubimage(0,0,49,49), hint_x1 + offsetX, hint_y1 + offsetY, 54, 54, null); 		
		}


		// Boss hit
		if (Instant.now().isBefore(bossHitAnimationTime)) {
			g2.drawImage(gems.getSubimage(0 * 49, 48, 49, 49), 500, 230, 250, 250, null);
		}

		// Hit Boss
		for (int i = 0; i < 3; i++) {
			if (Instant.now().isBefore(hitPoint[i])) {
				g2.setColor(Color.RED);
				g2.setFont(new Font("Arial", Font.BOLD, 25));
				g2.drawString("-" + newScore[i] * 10, hitPoints_x[i], hitPoints_y[i]);
				BufferedImage zoomedOutImage = zoomOutImage(gems.getSubimage(i * 49, 0, 49, 49), 70, 70);
				g2.drawImage(zoomedOutImage, hitPoints_x[i],hitPoints_y[i], null);
			}
		}
		// Win And Lose
		if (!isRunning) {
			if (gameEnd == 1) {
				g2.setColor(Color.GREEN);
				g2.setFont(new Font("Arial", Font.BOLD, 90));
				g2.drawString("YOU WIN !", 150, 230);
			} else {
				g2.setColor(Color.RED);
				g2.setFont(new Font("Arial", Font.BOLD, 90));
				g2.drawString("YOU LOSE !", 150, 230);
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
				rageRound --;
				if(rageRound <= 0){
					rageState = 0;
				}
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
					//System.out.println("Eliminate " + typeName[i] + " X " + newScore[i]);
					if (i < 3) {
						newScore[i] *= (1 + rageState);
						// type 0 to 2
						switch (i) {
						case 0:
							// Sword

							break;
						case 1:
							// Hammer

							break;
						case 2:
							// Bow

							break;
						default:
						}
						// set boss X and Y coordinate
						// boss_x_coordinate = 0;

						// hit boss
						if (bossDefenseValue > 0) {
							if (bossDefenseValue > newScore[i]) {
								bossDefenseValue -= newScore[i];
							} else {
								newScore[i] -= bossDefenseValue;
								bossDefenseValue = 0;
								bossHP = max(0, bossHP - newScore[i] * 10);
							}
						} else {
							bossHP = max(0, bossHP - newScore[i] * 10);
						}
						Random random = new Random();
						hitPoint[i] = currentTime.plusMillis(600);
						//hitPoints_x[i] = boss_x_coordinate + random.nextInt(100) - 50;
						//hitPoints_y[i] = boss_y_coordinate +   + random.nextInt(80)- 50;
						hitPoints_x[i] = boss_x_coordinate + 40 + random.nextInt(90);
						hitPoints_y[i] = boss_y_coordinate + 90 + random.nextInt(80);

					} else {
						// type 3 to 5
						switch (i) {
						case 3:
							// Defense
							break;
						case 4:
							// Heal
							playerHP = min(100, playerHP + newScore[i] * 5);
							break;
						case 5:
							// Rage
							playerRage = min(100, playerRage + newScore[i] * 10);
							if(playerRage >= 100){
								//enter rage state
								rageState = 1;
								rageRound = 4;
								playerRage = 0;
							}
							break;
						default:
							//System.out.println("Invalid i");
						}
					}

				}
			}
			// update Score
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
					hint_x0 = board.getCell(i, j).i;
					hint_x1 = board.getCell(i, j + 1).i;
					hint_y0 = board.getCell(i, j).j;
					hint_y1 = board.getCell(i, j + 1).j;
					currentTime = Instant.now();
					hintTime = currentTime.plusSeconds(30);
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
					hint_x0 = board.getCell(i, j).i;
					hint_x1 = board.getCell(i+1, j).i;
					hint_y0 = board.getCell(i, j).j;
					hint_y1 = board.getCell(i+1, j).j;
					currentTime = Instant.now();
					hintTime = currentTime.plusSeconds(30);
					return;
				}

			}

		}

		// initial cell
		if (dead) {
			//System.out.println("\\nDead\\n");
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
		while (true) {
			try {
				start();
				while (isRunning) {

					update();
					draw();
					Thread.sleep(1);

				}
				Thread.sleep(3000);
				isRunning = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
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
