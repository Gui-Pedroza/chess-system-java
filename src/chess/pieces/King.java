package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class King extends ChessPiece {

	private ChessMatch match;

	public King(Board board, Color color, ChessMatch match) {
		super(board, color);
		this.match = match;

	}

	@Override
	public String toString() {
		return "K";
	}

	private boolean testRookCastling(Position position) {
		ChessPiece p = (ChessPiece) getBoard().piece(position);
		return p != null && p instanceof Rook && p.getColor() == getColor() && p.getMoveCount() == 0;
	}

	private boolean canMove(Position position) {
		ChessPiece p = (ChessPiece) getBoard().piece(position);
		return p == null || p.getColor() != this.getColor();
	}

	@Override
	public boolean[][] possibleMoves() {
		boolean[][] mat = new boolean[8][8];
		Position p = new Position(0, 0);
		// above:
		p.setValues(getPosition().getRow() - 1, getPosition().getColumn());
		if (getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		// below:
		p.setValues(getPosition().getRow() + 1, getPosition().getColumn());
		if (getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		// left:
		p.setValues(getPosition().getRow(), getPosition().getColumn() - 1);
		if (getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		// right:
		p.setValues(getPosition().getRow(), getPosition().getColumn() + 1);
		if (getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		// ↗
		p.setValues(getPosition().getRow() - 1, getPosition().getColumn() + 1);
		if (getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		// ↘
		p.setValues(getPosition().getRow() + 1, getPosition().getColumn() + 1);
		if (getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		// ↙
		p.setValues(getPosition().getRow() + 1, getPosition().getColumn() - 1);
		if (getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		// ↖
		p.setValues(getPosition().getRow() - 1, getPosition().getColumn() - 1);
		if (getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}

		// special move castling
		if (getMoveCount() == 0 && !match.getCheck()) {
			Position posT1 = new Position(getPosition().getRow(), getPosition().getColumn() + 3);
			if (testRookCastling(posT1)) {
				Position p1 = new Position(getPosition().getRow(), getPosition().getColumn() + 1);
				Position p2 = new Position(getPosition().getRow(), getPosition().getColumn() + 2);
				if (getBoard().piece(p1) == null && getBoard().piece(p2) == null) {
					mat[getPosition().getRow()][getPosition().getColumn() + 2] = true;
				}
			}
			Position posT2 = new Position(getPosition().getRow(), getPosition().getColumn() - 4);
			if (testRookCastling(posT2)) {
				Position p1 = new Position(getPosition().getRow(), getPosition().getColumn() - 1);
				Position p2 = new Position(getPosition().getRow(), getPosition().getColumn() - 2);
				Position p3 = new Position(getPosition().getRow(), getPosition().getColumn() - 3);
				if (getBoard().piece(p1) == null && getBoard().piece(p2) == null && getBoard().piece(p3) == null) {
					mat[getPosition().getRow()][getPosition().getColumn() - 2] = true;
				}
			}
		}

		return mat;
	}

}
