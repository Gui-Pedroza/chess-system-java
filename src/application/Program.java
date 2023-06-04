package application;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import boardgame.Position;
import chess.ChessException;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.Color;

public class Program {

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);
		ChessMatch chessMatch = new ChessMatch();
		List<ChessPiece> captured = new ArrayList<>();

		while (!chessMatch.getCheckMate()) {
			try {
				UI.clearScreen();
				UI.printMatch(chessMatch, captured);
				System.out.println();
				System.out.print("Origin move: ");
				ChessPosition origin = UI.readChessPosition(sc);

				boolean[][] possibleMoves = chessMatch.possibleMoves(origin);
				UI.clearScreen();
				UI.printBoard(chessMatch.getPieces(), possibleMoves);

				System.out.println();
				System.out.print("Destiny move: ");
				ChessPosition destiny = UI.readChessPosition(sc);
				
				// tentando printar o tracejado do cheque do rei
				Color currentPlayer = chessMatch.getCurrentPlayer();
				Position kingPosition = chessMatch.kingPosition(chessMatch.opponent(currentPlayer));
				boolean [][] kingCheckTrace = chessMatch.kingCheckTrace(kingPosition, chessMatch.opponent(currentPlayer));
				if (chessMatch.getCheck()) {
					
				}

				ChessPiece capturedPiece = chessMatch.performChessMove(origin, destiny);
				if (capturedPiece != null) {
					captured.add(capturedPiece);
				}
				
				if (chessMatch.getPromoted() != null) {
					System.out.print("Enter piece for promotion (B/N/R/Q): ");
					String type = sc.nextLine().toUpperCase();
					while (!type.equals("B") && !type.equals("N") && !type.equals("R") & !type.equals("Q")) {
						System.out.print("Invalid value! Enter piece for promotion (B/N/R/Q): ");
						type = sc.nextLine().toUpperCase();
					}
					chessMatch.replacePromotedPiece(type);
				}

			} catch (ChessException e) {
				System.out.println(e.getMessage());
				sc.nextLine();

			} catch (InputMismatchException e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			}

		}
		UI.clearScreen();
		UI.printMatch(chessMatch, captured);

	}

}
