package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Bishop extends ChessPiece {

	public Bishop(Board board, Color color) {
		super(board, color);
		
	}
	
	@Override
	public String toString() {
		return "B";
	}
	
	public boolean[][] possibleMoves(){
		boolean mat[][] = new boolean[8][8];
		Position p = new Position();
		
		p.setValues(position.getRow() -1, position.getColumn() -1);
		while (getBoard().positionExists(p) && getBoard().isThereAPiece(p)) {
			mat[p.getRow()][p.getColumn()] = true;
			p.setValues(position.getRow() -1, position.getColumn() -1);			
		}
		if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		p.setValues(position.getRow() +1, position.getColumn() -1);
		while (getBoard().positionExists(p) && getBoard().isThereAPiece(p)) {
			mat[p.getRow()][p.getColumn()] = true;
			p.setValues(position.getRow() +1, position.getColumn() -1);			
		}
		if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		p.setValues(position.getRow() -1, position.getColumn() +1);
		while (getBoard().positionExists(p) && getBoard().isThereAPiece(p)) {
			mat[p.getRow()][p.getColumn()] = true;
			p.setValues(position.getRow() +1, position.getColumn() -1);			
		}
		if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		p.setValues(position.getRow() +1, position.getColumn() +1);
		while (getBoard().positionExists(p) && getBoard().isThereAPiece(p)) {
			mat[p.getRow()][p.getColumn()] = true;
			p.setValues(position.getRow() +1, position.getColumn() -1);			
		}
		if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		return mat;
	}

}
