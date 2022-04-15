package thedrake;

import java.io.PrintWriter;

public class Board {

    BoardTile[][] gameField;
    int dimension;

    // Konstruktor. Vytvoří čtvercovou hrací desku zadaného rozměru, kde všechny dlaždice jsou prázdné, tedy BoardTile.EMPTY
    public Board(int dimension) {
        // Místo pro váš kód
        this.dimension = dimension;
        gameField = new BoardTile[dimension][dimension];

        for (int idx1 = 0; idx1 < dimension; ++idx1) {
            for (int idx2 = 0; idx2 < dimension; ++idx2) {
                gameField[idx1][idx2] = BoardTile.EMPTY;
            }
        }
    }

    // Rozměr hrací desky
    public int dimension() {
        return dimension;
    }

    // Vrací dlaždici na zvolené pozici.
    public BoardTile at(TilePos pos) {
        return gameField[pos.i()][pos.j()];
    }

    // Vytváří novou hrací desku s novými dlaždicemi. Všechny ostatní dlaždice zůstávají stejné
    public Board withTiles(TileAt... ats) {

        Board newBoard = new Board(dimension);

        for (int i = 0; i < dimension; ++i) {
            newBoard.gameField[i] = this.gameField[i].clone();
        }

        for (TileAt tile : ats) {
            newBoard.gameField[tile.pos.i()][tile.pos.j()] = tile.tile;
        }

        return newBoard;
    }

    // Vytvoří instanci PositionFactory pro výrobu pozic na tomto hracím plánu
    public PositionFactory positionFactory() {
        return new PositionFactory(dimension);
    }

    public void toJSON(PrintWriter writer) {
        writer.printf("{\"dimension\":%s", dimension);
        writer.printf(",\"tiles\":[");
        int count = 0;
        for (int i = 0; i < dimension; ++i) {
            for (int j = 0; j < dimension; ++j) {
                at(new BoardPos(dimension, j, i)).toJSON(writer);
                ++count;
                if (count < dimension * dimension) {
                    writer.printf(",");
                }
            }
        }
        writer.printf("]}");
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

