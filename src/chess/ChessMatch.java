package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Pawn;
import chess.pieces.Queen;
import chess.pieces.Rook;

public class ChessMatch {

	private Board board;
	private int turn;
	private Color currentPlayer;
	private boolean check;
	private boolean checkMate;
	private ChessPiece enPassantVulnerable;
	private ChessPiece promoted;
	
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
	
	public ChessPiece getEnpassantVulnerable() {
		return enPassantVulnerable;
	}
	
	public ChessPiece getPromoted() {
		return promoted;
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
	
	public boolean[][] kingCheckTrace(Position kingPosition, Color color){
		// TODO
		// ISSO AQUI NAO TÁ IMPLEMENTADO NEM CORRETO:
		List<Piece> opponentPieces = piecesOnBoard.stream().filter(p -> ((ChessPiece) p).getColor() == color).collect(Collectors.toList());
		for (Piece piece : opponentPieces) {
			boolean[][] mat = piece.possibleMoves();			
			if (mat[kingPosition.getRow()][kingPosition.getColumn()]) {
				// Position oppositePiecePosition = ((ChessPiece) piece).getPosition();
				return mat;
			}
		}
		return null;
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
		ChessPiece movedPiece = (ChessPiece)board.piece(positionDestiny);
		// special move: promoted
		promoted = null;
		if (movedPiece instanceof Pawn) {
			if (movedPiece.getColor()==Color.WHITE  && positionDestiny.getRow() == 0 || movedPiece.getColor()==Color.BLACK  && positionDestiny.getRow() == 7) {
				promoted = (ChessPiece)board.piece(positionDestiny);
				promoted = replacePromotedPiece("Q");
			}			
		}
		
		check = (testCheck(opponent(currentPlayer))) ? true : false;
		if (testCheckMate(opponent(currentPlayer))) {
			checkMate = true;
		} else {
			nextTurn();
		}
		// test En passant possibilty
		if (movedPiece instanceof Pawn && (destiny.getRow() == origin.getRow() - 2 || destiny.getRow() == origin.getRow() + 2)) {
			enPassantVulnerable = movedPiece;
		}
		else {
			enPassantVulnerable = null;
		}
		return (ChessPiece) capturedPiece;		
		
	}
	
	public ChessPiece replacePromotedPiece(String type) {
		if (promoted == null) {
			throw new IllegalStateException("There is no piece to be promoted");
		}
		if (!type.equals("B") && !type.equals("N") && !type.equals("R") & !type.equals("Q")) {
			return promoted;
		}
		
		Position pos = promoted.getChessPosition().toPosition();
		Piece p = board.removePiece(pos);
		piecesOnBoard.remove(p);
		
		ChessPiece newPiece = newPiece(type, promoted.getColor());
		board.placePiece(newPiece, pos);
		piecesOnBoard.add(newPiece);
		
		return newPiece;
	}
	
	private ChessPiece newPiece(String type, Color color) {
		if (type.equals("B")) return new Bishop(board, color);
		if (type.equals("N")) return new Knight(board, color);
		if (type.equals("Q")) return new Queen(board, color);
		return new Rook(board, color);
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
		// special move castling
		if (p instanceof King && destiny.getColumn() == origin.getColumn() + 2) {
			Position originT = new Position(origin.getRow(), origin.getColumn() + 3);
			Position destinyT = new Position(origin.getRow(), origin.getColumn() + 1);
			ChessPiece rook = (ChessPiece)board.removePiece(originT);
			board.placePiece(rook, destinyT);
			rook.increaseMoveCount();
		}
		if (p instanceof King && destiny.getColumn() == origin.getColumn() - 2) {
			Position originT = new Position(origin.getRow(), origin.getColumn() - 4);
			Position destinyT = new Position(origin.getRow(), origin.getColumn() - 1);
			ChessPiece rook = (ChessPiece)board.removePiece(originT);
			board.placePiece(rook, destinyT);
			rook.increaseMoveCount();
		}
		// special move En Passant
		if (p instanceof Pawn) {
			if (origin.getColumn() != destiny.getColumn() && capturedPiece == null) {
				Position pawnPosition;
				if (p.getColor() == Color.WHITE) {
					pawnPosition = new Position(destiny.getRow() + 1, destiny.getColumn());
				}
				else {
					pawnPosition = new Position(destiny.getRow() - 1, destiny.getColumn());
				}
				capturedPiece = board.removePiece(pawnPosition);
				capturedPieces.add(capturedPiece);
				piecesOnBoard.remove(capturedPiece);
			}
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
		// undo kingside castling
		if (p instanceof King && destiny.getColumn() == origin.getColumn() + 2) {
			Position originT = new Position(origin.getRow(), origin.getColumn() + 3);
			Position destinyT = new Position(origin.getRow(), origin.getColumn() + 1);
			ChessPiece rook = (ChessPiece)board.removePiece(destinyT);
			board.placePiece(rook, originT);
			rook.decreaseMoveCount();
		}
		// undo queenside castling
		if (p instanceof King && destiny.getColumn() == origin.getColumn() - 2) {
			Position originT = new Position(origin.getRow(), origin.getColumn() - 4);
			Position destinyT = new Position(origin.getRow(), origin.getColumn() - 1);
			ChessPiece rook = (ChessPiece)board.removePiece(destinyT);
			board.placePiece(rook, originT);
			rook.decreaseMoveCount();
		}
		// undo en passant
		if (p instanceof Pawn) {
			if (origin.getColumn() != destiny.getColumn() && capturedPiece == enPassantVulnerable) {
				ChessPiece pawn = (ChessPiece)board.removePiece(destiny);
				Position pawnPosition;
				if (p.getColor() == Color.WHITE) {
					pawnPosition = new Position(3, destiny.getColumn());
				}
				else {
					pawnPosition = new Position(4, destiny.getColumn());
				}
				board.placePiece(pawn, pawnPosition);
			}
		}
	}

	private void placeNewPiece(char column, int row, ChessPiece piece) {
		board.placePiece(piece, new ChessPosition(column, row).toPosition());
		piecesOnBoard.add(piece);
	}

	public Color opponent(Color color) {
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
	
	public Position kingPosition(Color color) {
		return king(color).getChessPosition().toPosition();
	}

	private boolean testCheck(Color color) {		
		List<Piece> opponentPieces = piecesOnBoard.stream().filter(p -> ((ChessPiece) p).getColor() == opponent(color)).collect(Collectors.toList());
		for (Piece piece : opponentPieces) {
			boolean[][] mat = piece.possibleMoves();
			if (mat[kingPosition(color).getRow()][kingPosition(color).getColumn()]) {
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
		placeNewPiece('b', 1, new Knight(board, Color.WHITE));
		placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
		placeNewPiece('d', 1, new Queen(board, Color.WHITE));
		placeNewPiece('e', 1, new King(board, Color.WHITE, this));
		placeNewPiece('h', 1, new Rook(board, Color.WHITE));
		placeNewPiece('g', 1, new Knight(board, Color.WHITE));
		placeNewPiece('a', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('b', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('c', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('d', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('e', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('f', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('g', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('h', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('a', 8, new Rook(board, Color.BLACK));
		placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
		
		
		placeNewPiece('e', 8, new King(board, Color.BLACK, this));
		placeNewPiece('h', 8, new Rook(board, Color.BLACK));
		placeNewPiece('b', 8, new Knight(board, Color.BLACK));
		placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
		placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
		placeNewPiece('d', 8, new Queen(board, Color.BLACK));
		placeNewPiece('g', 8, new Knight(board, Color.BLACK));
		placeNewPiece('a', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('b', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('c', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('d', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('e', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('f', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('g', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('h', 7, new Pawn(board, Color.BLACK, this));

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
