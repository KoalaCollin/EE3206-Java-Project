import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import static java.lang.Math.abs;

/*
	type: 	0:Boundary
   		1.Water
   		2.Fire
   		3.Grass
   		4.Treatment
   		5.Defense
   		6.InvalidBlock
   		
  gameState:		0:initial
  					1:player round
  					2:monster round
  					3:end
  				
  matchScore:	0-6, refer to type
  				clear after each N round
  */


class Cell {
	public int x,y,type,i,j,matched;
	/*x = row
	  y = col
	  i = x coor
	  j = y coor
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
		matchScore = new int[7];
	}
	
	public void creatBoard(int row, int col) {
		this.row = row;
		this.col = col;
		tileSize = 54;
		board = new Cell[row][col];
		clearScore();
		roundNum = 3;
		totalRound = 0;
		initialcells();

		gameState = 1;

	}

	//return has matched and change cell's matched
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

	//test print score plane
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
	
	public int getRound() {
		return roundNum;
	}

	private void initialcells() {
		for (int x = 0; x < row; x++) {
			for (int y = 0; y < col; y++) {
				board[x][y] = new Cell(x,y);
				randomCell(x,y);
				board[x][y].matched = 0;
				board[x][y].i = y * tileSize;
				board[x][y].j = x * tileSize;
				////////set coordinate//////////////
				
			}
		}
		// Make sure there are no 3 matches
		//while (detectMatches()) {
		//	eliminateCells();
		//};
	}
	
	private void randomCell(int x, int y) {
		Random random = new Random();
		// random type 1 to 5(no Boundary and InvalidBlock)
		int randomtype = random.nextInt(5);
		board[x][y].type = randomtype;
	}

	///////////////////////To Be Done//////////////////////////////////////
	public boolean animationMove() {
		int speedSwapAnimation = 4;
		boolean hasMove = false;
		
		for(int i = 0;i < 8;i++) {
            for (int j = 0;j < 8;j++) {
                Cell cell = board[i][j];
                int dx = 0, dy = 0;
                dx = cell.i - cell.y * tileSize;
                dy = cell.j - cell.x * tileSize;
                if (dx != 0) {
                   cell.i -= dx / abs(dx);
                   hasMove = true;
                }
                if (dy != 0) {
                   cell.j -= dy / abs(dy);
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
            	if(board[i][j].matched > 0) {
            		count ++;
            		// update score
            		if (gameState == 1) {
            			if (board[i][j].type < 5)            				
            				matchScore[board[i][j].type]++;
            		}
            		System.out.println("elimate:" + i +" " + j + " type=" + board[i][j].type);
            		//reset matched
            		board[i][j].matched = 0;
            		displayMatch();
                	//reset type
                	randomCell(i, j);
                	//set coordinate
                	board[i][j].j = count * -tileSize; 
                	
            		//move to top
            		if(i != 0) {
            			for (int k = i; k > 0; k--) {
            				swap(k - 1, j, k, j);
            			}
            			//re-check i-th
            			i++;
            		}
            	}
            }
        }
		// score test
        displayBoard();
		System.out.println("update Score:");
		for (int i = 0; i < 7; i++) {
		    System.out.print(i + ":" + matchScore[i] + " ");
		}
	}

	public void setRound(int n) {
		roundNum = n;
	}

	///////////////////////To Be Done//////////////////////////////////////

	public void swap(int x1, int y1, int x2, int y2) {
		Cell cell1 = board[x1][y1];
		Cell cell2 = board[x2][y2];
		/*
		int tempi = cell1.i;
		cell1.i = cell2.i;
		cell2.i = tempi;
		
		int tempj = cell1.j;
		cell1.j = cell2.j;
		cell2.j = tempj;
		*/
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

    //Cell[][] Board;
    static Board board;
    BufferedImage background, gems, cursor, bossS1, bossS2, HPBar, HP;
    MouseEvent mouse;
    int tileSize = 54;
    int offsetX = 44, offsetY = 23;
    int x0, y0, x, y;
    int click = 0;
    int posX, posY;
    int speedSwapAnimation = 4;
    boolean swaping = false;
    boolean animationPlaying = false;
    boolean matched = false;

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
        if(thread == null) {
            thread = new Thread(this);
            isRunning = true;
            thread.start();
        }
    }

    public void start () {
        try {
            view = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    		int row = 8;
    		int col = 8;
    		int roundNum = 3;
    		int[] score;
    		String[] typeName = {"NULL","Fire ATK", "Water ATK","Grass ATK","HEAL","DEF","None"};
    		board = new Board();
    		board.creatBoard(row, col);
    		board.displayBoard();
    		board.setRound(roundNum);
    		// Monster setting
    		
            background = ImageIO.read(getClass().getResource("/background2.png"));
            gems = ImageIO.read(getClass().getResource("/gems2.png"));
            cursor = ImageIO.read(getClass().getResource("/cursor.png"));
            bossS1= ImageIO.read(getClass().getResource("/The-guardian-atk-pretty.png"));
            bossS2= ImageIO.read(getClass().getResource("/The-guardian-def-pretty.png"));
            HPBar= ImageIO.read(getClass().getResource("/HpBar.png"));
            HP= ImageIO.read(getClass().getResource("/Hp.png"));
            

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    
    public void update () {
        if (mouse != null && mouse.getID() == MouseEvent.MOUSE_PRESSED) {
        	//Click to Swap
            if (mouse.getButton() == MouseEvent.BUTTON1) {
                if(!swaping && !animationPlaying) {
                    click++;
                }
                posX = mouse.getX() - offsetX;
                posY = mouse.getY() - offsetY;

                if (click == 1) {
                    x0 = posX / tileSize;
                    y0 = posY / tileSize;
                }
                if (click == 2) {
                    x = posX / tileSize;
                    y = posY / tileSize;
                    if (abs(x - x0) + abs(y - y0) == 1) {
                        board.swap(y0,x0,y,x);
                        swaping = true;
                        click = 0;
                    } else {
                        click = 1;
                    }
                }
 
            }
            mouse = null;
        }
        
        //Moving animation
        animationPlaying = board.animationMove();
        

        //Second swap if no match
        if(swaping && !animationPlaying) {
        	
        	if(!board.detectMatches()) {
        		board.swap(y0,x0,y,x);
        	}else {
        		board.eliminateCells();
        		animationPlaying = true;
        	}
        	swaping = false;	
        }else if(!animationPlaying && board.detectMatches()){
        	//Update Board
            board.eliminateCells();
            animationPlaying = true;
        }
        
        
    }

    public void draw () {
        Graphics2D g2 = (Graphics2D) view.getGraphics();
        g2.drawImage(background, 0, 0, WIDTH, HEIGHT, null);
        g2.drawImage(HPBar, 480, 30, 250, 15, null);
        g2.drawImage(HP, 512, 32, 100, 11, null);   //(max 216 for WIDTH)
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString(50+"/"+100, 514, 42);
        //if(boss_state==1) then
        //g2.drawImage(bossS1, 480, 50, 250, 250, null);
        //else
            g2.drawImage(bossS2, 480, 50, 250, 250, null);
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.drawString("I will CRASH YOU!!!", 510, 90);
        
        g2.drawImage(HPBar, 480, 400, 250, 15, null);
        g2.drawImage(HP, 512, 402, 216, 11, null);

        for (int i = 0;i < 8;i++) {
            for (int j = 0;j < 8;j++) {
                g2.drawImage(
                        gems.getSubimage(board.getCell(i, j).type * 49, 0, 49, 49),
                        board.getCell(i, j).i + offsetX,
                        board.getCell(i, j).j + offsetY,
                        49,
                        49,
                        null
                );

                //Show cursor
                if(click == 1) {
                    if(x0 == j && y0 == i) {
                        g2.drawImage(
                                cursor,
                                board.getCell(i, j).i + offsetX,
                                board.getCell(i, j).j + offsetY,
                                cursor.getWidth(),
                                cursor.getHeight(),
                                null
                        );
                    }
                }
            }
        }

        Graphics g = getGraphics();
        g.drawImage(view, 0, 0, WIDTH, HEIGHT, null);
        g.dispose();
    }

    @Override
    public void run() {
        try {
            start();
            while(isRunning) {
                update();
                draw();
                Thread.sleep(1000/60);
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

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}




