package chess;

import boardgame.Board;
import boardgame.Position;
import chess.pieces.Rook;
import chess.pieces.King;

public class ChessMatch {
	
	private Board board;
	
	public ChessMatch() {
		board = new Board(8, 8);
		initialSetup();
	}
	
	public ChessPiece[][] getPieces(){
		ChessPiece[][] mat = new ChessPiece[8][8];
		for (int i=0; i<8; i++) {
			for (int j=0; j<8; j++) {
				mat[i][j] = (ChessPiece) board.piece(i, j);
			}
		}
		return mat;
	}
	
	public void initialSetup() {		
		board.placePiece(new Rook(board, Color.WHITE), new Position(2, 1));
		board.placePiece(new Rook(board, Color.WHITE), new Position(2, 2));
		board.placePiece(new King(board, Color.WHITE), new Position(7, 4));
	}
}
