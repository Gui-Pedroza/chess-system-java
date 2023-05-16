package application;

import java.util.InputMismatchException;
import java.util.Scanner;

import chess.ChessException;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;

public class Program {

	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		ChessMatch chessMatch = new ChessMatch();
		
		
		while (true) {
			try {
				UI.clearScreen();
				UI.printBoard(chessMatch.getPieces());
				System.out.println();
				System.out.print("Origin move: ");
				ChessPosition origin = UI.readChessPosition(sc);
				
				System.out.println();
				System.out.print("Destiny move: ");
				ChessPosition destiny = UI.readChessPosition(sc);
				
				ChessPiece capturedPiece = chessMatch.performChessMove(origin, destiny);
			} 
			catch (ChessException e) {
				System.out.println(e.getMessage());
				sc.nextLine();
						
			} catch (InputMismatchException e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			}			
			
		}
		
		
	}

}
