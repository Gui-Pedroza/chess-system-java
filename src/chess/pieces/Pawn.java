package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class Pawn extends ChessPiece {

	private ChessMatch match;

	public Pawn(Board board, Color color, ChessMatch match) {
		super(board, color);
		this.match = match;
	}

	@Override
	public String toString() {
		return "P";
	}

	@Override
	public boolean[][] possibleMoves() {
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];

		Position p = new Position(0, 0);

		if (getColor() == Color.WHITE) {
			p.setValues(getPosition().getRow() - 1, getPosition().getColumn());
			if (getBoard().positionExists(p) && !getBoard().isThereAPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(getPosition().getRow() - 2, getPosition().getColumn());
			Position p2 = new Position(getPosition().getRow() - 1, getPosition().getColumn());
			if (getBoard().positionExists(p) && !getBoard().isThereAPiece(p) && getBoard().positionExists(p2)
					&& !getBoard().isThereAPiece(p2) && getMoveCount() == 0) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(getPosition().getRow() - 1, getPosition().getColumn() - 1);
			if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(getPosition().getRow() - 1, getPosition().getColumn() + 1);
			if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			// special move En passant white
			if (getPosition().getRow() == 3) {
				Position left = new Position(getPosition().getRow(), getPosition().getColumn() - 1);
				if (getBoard().positionExists(left) && isThereOpponentPiece(left)
						&& getBoard().piece(left) == match.getEnpassantVulnerable()) {
					mat[left.getRow() - 1][left.getColumn()] = true;
				}
				Position right = new Position(getPosition().getRow(), getPosition().getColumn() + 1);
				if (getBoard().positionExists(right) && isThereOpponentPiece(right)
						&& getBoard().piece(right) == match.getEnpassantVulnerable()) {
					mat[right.getRow() - 1][right.getColumn()] = true;
				}
			}
		} 
		else {
			p.setValues(getPosition().getRow() + 1, getPosition().getColumn());
			if (getBoard().positionExists(p) && !getBoard().isThereAPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(getPosition().getRow() + 2, getPosition().getColumn());
			Position p2 = new Position(getPosition().getRow() + 1, getPosition().getColumn());
			if (getBoard().positionExists(p) && !getBoard().isThereAPiece(p) && getBoard().positionExists(p2)
					&& !getBoard().isThereAPiece(p2) && getMoveCount() == 0) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(getPosition().getRow() + 1, getPosition().getColumn() - 1);
			if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(getPosition().getRow() + 1, getPosition().getColumn() + 1);
			if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			// special move En passant black
			if (getPosition().getRow() == 4) {
				Position left = new Position(getPosition().getRow(), getPosition().getColumn() - 1);
				if (getBoard().positionExists(left) && isThereOpponentPiece(left)
						&& getBoard().piece(left) == match.getEnpassantVulnerable()) {
					mat[left.getRow() + 1][left.getColumn()] = true;
				}
				Position right = new Position(getPosition().getRow(), getPosition().getColumn() + 1);
				if (getBoard().positionExists(right) && isThereOpponentPiece(right)
						&& getBoard().piece(right) == match.getEnpassantVulnerable()) {
					mat[right.getRow() + 1][right.getColumn()] = true;
				}
			}
		}		
		return mat;
	}

}
