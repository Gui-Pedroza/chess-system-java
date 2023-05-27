package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Pawn;
import chess.pieces.Rook;

public class ChessMatch {

	private Board board;
	private int turn;
	private Color currentPlayer;
	private boolean check;
	private boolean checkMate;

	private List<Piece> piecesOnBoard = new ArrayList<>();
	private List<Piece> capturedPieces = new ArrayList<>();

	public ChessMatch() {
		board = new Board(8, 8);
		turn = 1;
		currentPlayer = Color.WHITE;
		initialSetup();
	}

	public int getTurn() {
		return turn;
	}

	public Color getCurrentPlayer() {
		return currentPlayer;
	}

	public boolean getCheck() {
		return check;
	}

	public boolean getCheckMate() {
		return checkMate;
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

	public boolean[][] possibleMoves(ChessPosition originPosition) {
		Position position = originPosition.toPosition();
		validateOriginPosition(position);
		return board.piece(position).possibleMoves();
	}

	public ChessPiece performChessMove(ChessPosition origin, ChessPosition destiny) {
		Position positionOrigin = origin.toPosition();
		Position positionDestiny = destiny.toPosition();
		validateOriginPosition(positionOrigin);
		validateDestinyPosition(positionOrigin, positionDestiny);
		Piece capturedPiece = makeMove(positionOrigin, positionDestiny);
		if (testCheck(currentPlayer)) {
			undoMove(positionOrigin, positionDestiny, capturedPiece);
			throw new ChessException("You can't put yourself in check");
		}
		check = (testCheck(opponent(currentPlayer))) ? true : false;
		if (testCheckMate(opponent(currentPlayer))) {
			checkMate = true;
		} else {
			nextTurn();
		}

		return (ChessPiece) capturedPiece;
	}

	private Piece makeMove(Position origin, Position destiny) {
		ChessPiece p = (ChessPiece) board.removePiece(origin);
		p.increaseMoveCount();
		Piece capturedPiece = board.removePiece(destiny);
		board.placePiece(p, destiny);
		if (capturedPiece != null) {
			piecesOnBoard.remove(capturedPiece);
			capturedPieces.add(capturedPiece);
		}
		return capturedPiece;
	}

	private void undoMove(Position origin, Position destiny, Piece capturedPiece) {
		ChessPiece p = (ChessPiece) board.removePiece(destiny);
		p.decreaseMoveCount();
		board.placePiece(p, origin);
		if (capturedPiece != null) {
			board.placePiece(capturedPiece, destiny);
			capturedPieces.remove(capturedPiece);
			piecesOnBoard.add(capturedPiece);
		}
	}

	private void placeNewPiece(char column, int row, ChessPiece piece) {
		board.placePiece(piece, new ChessPosition(column, row).toPosition());
		piecesOnBoard.add(piece);
	}

	private Color opponent(Color color) {
		return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}

	private ChessPiece king(Color color) {
		List<Piece> list = piecesOnBoard.stream().filter(p -> ((ChessPiece) p).getColor() == color).collect(Collectors.toList());
		for (Piece p : list) {
			if (p instanceof King) {
				return (ChessPiece) p;
			}
		}
		return null;
	}

	private boolean testCheck(Color color) {
		Position kingPosition = king(color).getChessPosition().toPosition();
		List<Piece> opponentPieces = piecesOnBoard.stream().filter(p -> ((ChessPiece) p).getColor() == opponent(color)).collect(Collectors.toList());
		for (Piece piece : opponentPieces) {
			boolean[][] mat = piece.possibleMoves();
			if (mat[kingPosition.getRow()][kingPosition.getColumn()]) {
				return true;
			}
		}
		return false;
	}

	private boolean testCheckMate(Color color) {
		if (!testCheck(color)) {
			return false;
		}
		List<Piece> list = piecesOnBoard.stream().filter(p -> ((ChessPiece) p).getColor() == color).toList();
		for (Piece piece : list) {
			boolean[][] mat = piece.possibleMoves();
			for (int i = 0; i < board.getRows(); i++) {
				for (int j = 0; j < board.getRows(); j++) {
					if (mat[i][j]) {
						Position source = ((ChessPiece) piece).getChessPosition().toPosition();
						Position target = new Position(i, j);
						Piece capturedPiece = makeMove(source, target);
						boolean testCheck = testCheck(color);
						undoMove(source, target, capturedPiece);
						if (!testCheck) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	public void initialSetup() {
		placeNewPiece('a', 1, new Rook(board, Color.WHITE));
		placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
		placeNewPiece('e', 1, new King(board, Color.WHITE));
		placeNewPiece('h', 1, new Rook(board, Color.WHITE));
		placeNewPiece('a', 2, new Pawn(board, Color.WHITE));
		placeNewPiece('b', 2, new Pawn(board, Color.WHITE));
		placeNewPiece('c', 2, new Pawn(board, Color.WHITE));
		placeNewPiece('d', 2, new Pawn(board, Color.WHITE));
		placeNewPiece('e', 2, new Pawn(board, Color.WHITE));
		placeNewPiece('f', 2, new Pawn(board, Color.WHITE));
		placeNewPiece('g', 2, new Pawn(board, Color.WHITE));
		placeNewPiece('h', 2, new Pawn(board, Color.WHITE));
		placeNewPiece('a', 8, new Rook(board, Color.BLACK));
		placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
		
		
		placeNewPiece('e', 8, new King(board, Color.BLACK));
		placeNewPiece('h', 8, new Rook(board, Color.BLACK));
		
		placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
		placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
		placeNewPiece('a', 7, new Pawn(board, Color.BLACK));
		placeNewPiece('b', 7, new Pawn(board, Color.BLACK));
		placeNewPiece('c', 7, new Pawn(board, Color.BLACK));
		placeNewPiece('d', 7, new Pawn(board, Color.BLACK));
		placeNewPiece('e', 7, new Pawn(board, Color.BLACK));
		placeNewPiece('f', 7, new Pawn(board, Color.BLACK));
		placeNewPiece('g', 7, new Pawn(board, Color.BLACK));
		placeNewPiece('h', 7, new Pawn(board, Color.BLACK));

	}

	private void validateOriginPosition(Position position) {
		if (!board.isThereAPiece(position)) {
			throw new ChessException("There is no piece in origin position, press enter to continue");
		}
		if (!board.piece(position).isThereAnyPossibleMove()) {
			throw new ChessException("There no possible moves for the chosen piece, press enter to continue");
		}
		if (((ChessPiece) board.piece(position)).getColor() != currentPlayer) {
			throw new ChessException("Cannot move opponent piece, press enter and select one of your own piece");
		}
	}

	private void validateDestinyPosition(Position positionOrigin, Position positionDestiny) {
		if (!board.piece(positionOrigin).possibleMove(positionDestiny)) {
			throw new ChessException("Invalid move, press enter to continue");
		}

	}

	private void nextTurn() {
		turn++;
		currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}
}
