package thedrake;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

final public class TroopTile implements Tile, JSONSerializable {

    Troop troop;
    PlayingSide side;
    TroopFace face;

    // Konstruktor
    public TroopTile(Troop troop, PlayingSide side, TroopFace face){
        this.troop = troop;
        this.side = side;
        this.face = face;
    }

    // Vrací barvu, za kterou hraje jednotka na této dlaždici
    public PlayingSide side(){
        return side;
    }

    // Vrací stranu, na kterou je jednotka otočena
    public TroopFace face(){
        return face;
    }

    // Jednotka, která stojí na této dlaždici
    public Troop troop(){
        return troop;
    }

    // Vrací False, protože na dlaždici s jednotkou se nedá vstoupit
    public boolean canStepOn(){
        return false;
    }

    // Vrací True
    public boolean hasTroop(){
        return true;
    }

    @Override
    public List<Move> movesFrom(BoardPos pos, GameState state) {

        List<Move> moves = new ArrayList<>();

        if (state.armyOnTurn().boardTroops().at(pos).isPresent()) {
            Troop troop = state.armyOnTurn().boardTroops().at(pos).get().troop;
            TroopFace face = state.armyOnTurn().boardTroops().at(pos).get().face;

            for (TroopAction action : troop.actions(face)) {
                moves.addAll(action.movesFrom(pos, state.sideOnTurn(), state));
            }

        }

        return moves;
    }

    // Vytvoří novou dlaždici, s jednotkou otočenou na opačnou stranu (z rubu na líc nebo z líce na rub)
    public TroopTile flipped(){
        if (face == TroopFace.AVERS)
            return new TroopTile(troop, side, TroopFace.REVERS);
        else
            return new TroopTile(troop, side, TroopFace.AVERS);
    }

    @Override
    public void toJSON(PrintWriter writer) {
        writer.printf("{");

        writer.printf("\"troop\":");
        troop.toJSON(writer);
        writer.printf(",\"side\":");
        side.toJSON(writer);
        writer.printf(",\"face\":");
        face.toJSON(writer);

        writer.printf("}");
    }
}


