package thedrake;

final public class TroopTile implements Tile{

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

    // Vytvoří novou dlaždici, s jednotkou otočenou na opačnou stranu (z rubu na líc nebo z líce na rub)
    public TroopTile flipped(){
        if (face == TroopFace.AVERS)
            return new TroopTile(troop, side, TroopFace.REVERS);
        else
            return new TroopTile(troop, side, TroopFace.AVERS);
    }

}


