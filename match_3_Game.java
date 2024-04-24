import java.util.*;

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
class Board {
	int[][] board;
	List<Coordinate> cellsToBeDeleted;
	int[] matchScore;
	Coordinate[] previousSwap;
	int x, y, row, col, type, gameState, roundNum, totalRound;

	public Board() {
		gameState = 0;
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
		board = new int[row][col];
		clearScore();
		roundNum = 3;
		cellsToBeDeleted = new ArrayList<>();
		totalRound = 0;
		initialcells();

		gameState = 1;

	}

	private boolean detectHorizontalMatches(int x, int y, int type) {
		int count = 1;
		int startCol = y - 1;
		int endCol = y + 1;
		boolean hasMatches = false;

		while (startCol >= 0 && board[x][startCol] == type) {
			count++;
			startCol--;
		}

		while (endCol < col && board[x][endCol] == type) {
			count++;
			endCol++;
		}

		if (count >= 3) {
			for (int i = startCol + 1; i < endCol; i++) {
				cellsToBeDeleted.add(new Coordinate(x, i));
			}
			hasMatches = true;
		}

		return hasMatches;
	}

	private boolean detectMatches() {
		boolean hasMatches = false;
		cellsToBeDeleted.clear();

		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				int currentType = board[i][j];
				if (currentType >= 1 && currentType <= 6) {
					if (detectHorizontalMatches(i, j, currentType) || detectVerticalMatches(i, j, currentType)) {
						hasMatches = true;
					}
				}
			}
		}
		if (!cellsToBeDeleted.isEmpty()) {
			removeDetectedCells();
		}
		return hasMatches;
	}

	private boolean detectVerticalMatches(int x, int y, int type) {
		int count = 1;
		int startRow = x - 1;
		int endRow = x + 1;
		boolean hasMatches = false;

		while (startRow >= 0 && board[startRow][y] == type) {
			count++;
			startRow--;
		}

		while (endRow < row && board[endRow][y] == type) {
			count++;
			endRow++;
		}

		if (count >= 3) {
			for (int i = startRow + 1; i < endRow; i++) {
				cellsToBeDeleted.add(new Coordinate(i, y));
			}
			hasMatches = true;
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
				System.out.printf("%-2d", board[x][y]);
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
				randomCell(x, y);
			}
		}
		// Make sure there are no 3 matches
		while (detectMatches());
	}

	private void moveCells() {
		for (y = 0; y < col; y++) {
			int currentRow = row - 1;
			for (x = row - 1; x >= 0; x--) {
				if (board[x][y] != 0) {
					board[currentRow][y] = board[x][y];
					currentRow--;
				}
			}
			// create new cells
			while (currentRow >= 0) {
				board[currentRow][y] = 0;
				randomCell(currentRow, y);
				currentRow--;
			}
		}
	}

	///////////////////////To Be Done//////////////////////////////////////
	private void animationMove() {
		
	}
	

	private void randomCell(int x, int y) {
		Random random = new Random();
		// random type 1 to 5(no Boundary and InvalidBlock)
		int randomtype = random.nextInt(5) + 1;
		board[x][y] = randomtype;
	}

	private void removeDetectedCells() {
		for (Coordinate coordinate : cellsToBeDeleted) {
			int x = coordinate.x;
			int y = coordinate.y;
			if (gameState == 1) {
				matchScore[board[x][y]]++;
			}
			board[x][y] = 0;
		}
		cellsToBeDeleted.clear();
		// test
		System.out.println("RemoveCells");
		displayBoard();
		moveCells();
		// test
		System.out.println("MoveCells");
		displayBoard();
		// score test
		System.out.println("update Score:");
		for (int i = 0; i < 7; i++) {
		    System.out.print(i + ":" + matchScore[i] + " ");
		}
	}

	public void setRound(int n) {
		roundNum = n;
	}

	///////////////////////To Be Done//////////////////////////////////////
	public void swap(char row1, int col1, char row2, int col2) {
		int x1 = row1 - 'A';
		int y1 = col1;
		int x2 = row2 - 'A';
		int y2 = col2;

		boolean isAdjacent = Math.abs(x1 - x2) == 1 && Math.abs(y1 - y2) == 0
				|| Math.abs(x1 - x2) == 0 && Math.abs(y1 - y2) == 1;

		if (!isAdjacent) {
			throw new IllegalArgumentException("Cannot swap non-adjacent cells.");
		}

		// save it for following undo swap
		previousSwap = new Coordinate[] { new Coordinate(x1, y1), new Coordinate(x2, y2) };

		int temp = board[x1][y1];
		board[x1][y1] = board[x2][y2];
		board[x2][y2] = temp;

		// test if it is a available swap
		if (!detectMatches()) {
			undoSwap();
			return;
		}
		while (detectMatches());
		// modify round
		totalRound++;
		roundNum--;
	}

	private void undoSwap() {
		if (previousSwap != null) {
			int x1 = previousSwap[0].x;
			int y1 = previousSwap[0].y;
			int x2 = previousSwap[1].x;
			int y2 = previousSwap[1].y;

			int temp = board[x1][y1];
			board[x1][y1] = board[x2][y2];
			board[x2][y2] = temp;

			previousSwap = null;
			throw new IllegalArgumentException("No match cells, cannot swap.");
		}
	}

}

class Coordinate {
	public int x;
	public int y;

	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}

}

public class match_3_Game {

	public static void main(String[] args) {
		start();

	}

	// start a new game
	public static void start() {
		int row = 8;
		int col = 8;
		int roundNum = 3;
		int[] score;
		String[] typeName = {"NULL","Fire ATK", "Water ATK","Grass ATK","HEAL","DEF","None"};
		Board board = new Board();
		board.creatBoard(row, col);
		board.displayBoard();
		board.setRound(roundNum);
		// Monster setting

		Scanner scanner = new Scanner(System.in);
		boolean shouldContinue = true;
		while (shouldContinue) {
			
			//player round
			//swap
			while (board.getRound() > 0) {
				// player input
				System.out.print("Enter two cells to swap in the format(A 0 C 2), or enter q to quit: \n");
				String input = scanner.nextLine();
				if (input.equalsIgnoreCase("q")) {
					shouldContinue = false;
				} else {
					try {
						String[] inputs = input.split(" ");
						char row1 = inputs[0].charAt(0);
						int col1 = Integer.parseInt(inputs[1]);
						char row2 = inputs[2].charAt(0);
						int col2 = Integer.parseInt(inputs[3]);
						board.swap(row1, col1, row2, col2); // Swap the specified cells
						board.displayBoard();

					} catch (IllegalArgumentException e) {
						board.displayBoard();
						System.out.println("Swap failed: " + e.getMessage());
					}

				}
			}
			//calculate score
			score = board.getScore();
			System.out.println("Score: ");
			for (int i = 0; i < 7; i++) {
			    System.out.print(typeName[i] + ":" + score[i] + " ");
			}
			////////////////Monster event////////////////////
			///////////////////////To Be Done//////////////////////////////////////
			//clear score
			board.clearScore();
			//set round
			board.setRound(roundNum);

		}
		scanner.close();

	}

}
