package chess;

import boardgame.Board;
import boardgame.Piece;
import chess.pieces.Rook;
import chess.pieces.King;
import boardgame.Position;

public class ChessMatch {

	private Board board;

	public ChessMatch() {
		board = new Board(8, 8);
		initialSetup();
	}

	public ChessPiece[][] getPieces() {
		ChessPiece[][] mat = new ChessPiece[8][8];
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				mat[i][j] = (ChessPiece) board.piece(i, j);
			}
		}
		return mat;
	}
	
	public ChessPiece performChessMove(ChessPosition origin, ChessPosition destiny) {		
		Position positionOrigin = origin.toPosition();
		Position positionDestiny = destiny.toPosition();
		validateOriginPosition(positionOrigin);
		validateDestinyPosition(positionOrigin, positionDestiny);
		Piece capturedPiece = makeMove(positionOrigin, positionDestiny);
		return (ChessPiece) capturedPiece;		
	}
	
	private Piece makeMove(Position origin, Position destiny) {
		Piece p = board.removePiece(origin);
		Piece capturedPiece = board.removePiece(destiny);
		board.placePiece(p, destiny);
		return capturedPiece;
	}

	private void placeNewPiece(char column, int row, ChessPiece piece) {
		board.placePiece(piece, new ChessPosition(column, row).toPosition());
	}

	public void initialSetup() {
		placeNewPiece('c', 1, new Rook(board, Color.WHITE));
		placeNewPiece('c', 2, new Rook(board, Color.WHITE));
		placeNewPiece('d', 2, new Rook(board, Color.WHITE));
		placeNewPiece('e', 2, new Rook(board, Color.WHITE));
		placeNewPiece('e', 1, new Rook(board, Color.WHITE));
		placeNewPiece('d', 1, new King(board, Color.WHITE));

		placeNewPiece('c', 7, new Rook(board, Color.BLACK));
		placeNewPiece('c', 8, new Rook(board, Color.BLACK));
		placeNewPiece('d', 7, new Rook(board, Color.BLACK));
		placeNewPiece('e', 7, new Rook(board, Color.BLACK));
		placeNewPiece('e', 8, new Rook(board, Color.BLACK));
		placeNewPiece('d', 8, new King(board, Color.BLACK));

	}
	
	private void validateOriginPosition(Position position) {
		if (!board.isThereAPiece(position)) {
			throw new ChessException("There is no piece in origin position, press enter to continue");
		}
		if (!board.piece(position).isThereAnyPossibleMove()) {
			throw new ChessException("There no possible moves for the chosen piece, press enter to continue");
		}
	}
	private void validateDestinyPosition(Position positionOrigin, Position positionDestiny) {
		if (!board.piece(positionOrigin).possibleMove(positionDestiny)) {
			throw new ChessException("Invalid move, press enter to continue");
		}
		
	}
}
