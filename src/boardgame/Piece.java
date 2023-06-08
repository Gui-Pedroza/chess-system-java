package boardgame;


public abstract class Piece {
	
	protected Position position;
	private Board board;
	
	public Piece() {
		
	}
	
	public Piece(Board board) {
		this.board = board;
	}
	
	protected Board getBoard() {
		return board;
	}

	public Position getPosition() {
		return position;
	}

	public abstract boolean[][] possibleMoves();
		
	public boolean possibleMove(Position position) {
		return possibleMoves()[position.getRow()][position.getColumn()];
	}
	
	public boolean isThereAnyPossibleMove(){
		boolean[][] mat = possibleMoves();
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (mat[i][j]) {
					return true;
				}
			}
		}
		return false;
	}	
	
}
