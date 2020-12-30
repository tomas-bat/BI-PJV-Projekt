package thedrake;

import java.io.PrintWriter;

public class Board implements JSONSerializable {

	private final int dimension;

	private final BoardTile[][] board;

	// Konstruktor. Vytvoří čtvercovou hrací desku zadaného rozměru, kde všechny dlaždice jsou prázdné, tedy BoardTile.EMPTY
	public Board(int dimension) {
		this.dimension = dimension;

		BoardTile[][] board = new BoardTile[dimension][dimension];

		for (int i = 0; i < board.length; i++ ) {
			for (int j = 0; j < board[i].length; j++) {
				board[i][j] = BoardTile.EMPTY;
			}
		}
		this.board = board;
	}

	// Rozměr hrací desky
	public int dimension() {
		return dimension;
	}

	// Vrací dlaždici na zvolené pozici.
	public BoardTile at(TilePos pos) {
		return board[pos.i()][pos.j()];
	}

	// Vytváří novou hrací desku s novými dlaždicemi. Všechny ostatní dlaždice zůstávají stejné
	public Board withTiles(TileAt ...ats) {
		Board newBoard = new Board(dimension);
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				newBoard.board[i][j] = this.board[i][j];
			}
		}

		for (TileAt tile: ats) {
			newBoard.board[tile.pos.i()][tile.pos.j()] = tile.tile;
		}
		return newBoard;
	}

	// Vytvoří instanci PositionFactory pro výrobu pozic na tomto hracím plánu
	public PositionFactory positionFactory() {
		return new PositionFactory(dimension);
	}

	@Override
	public void toJSON(PrintWriter writer) {
		writer.print("{\"dimension\":" + dimension + ",\"tiles\":[");
		int count = 0;
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				board[j][i].toJSON(writer);
				count++;
				if (count < dimension*dimension)
					writer.print(",");
			}
		}
		writer.print("]}");
	}

	public static class TileAt {
		public final BoardPos pos;
		public final BoardTile tile;
		
		public TileAt(BoardPos pos, BoardTile tile) {
			this.pos = pos;
			this.tile = tile;
		}
	}
}

