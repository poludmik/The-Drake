package thedrake;

import java.io.PrintWriter;
import java.util.Optional;

public class GameState implements JSONSerializable {
    private final Board board;
    private final PlayingSide sideOnTurn;
    private final Army blueArmy;
    private final Army orangeArmy;
    private final GameResult result;

    public GameState(
            Board board,
            Army blueArmy,
            Army orangeArmy) {
        this(board, blueArmy, orangeArmy, PlayingSide.BLUE, GameResult.IN_PLAY);
    }

    public GameState(
            Board board,
            Army blueArmy,
            Army orangeArmy,
            PlayingSide sideOnTurn,
            GameResult result) {
        this.board = board;
        this.sideOnTurn = sideOnTurn;
        this.blueArmy = blueArmy;
        this.orangeArmy = orangeArmy;
        this.result = result;
    }

    public Board board() {
        return board;
    }

    public PlayingSide sideOnTurn() {
        return sideOnTurn;
    }

    public GameResult result() {
        return result;
    }

    public Army army(PlayingSide side) {
        if (side == PlayingSide.BLUE) {
            return blueArmy;
        }

        return orangeArmy;
    }

    public Army armyOnTurn() {
        return army(sideOnTurn);
    }

    public Army armyNotOnTurn() {
        if (sideOnTurn == PlayingSide.BLUE)
            return orangeArmy;

        return blueArmy;
    }

    public Tile tileAt(TilePos pos) {
        if (blueArmy.boardTroops().at(pos).isPresent())
            return blueArmy.boardTroops().at(pos).get();
        else if (orangeArmy.boardTroops().at(pos).isPresent())
            return orangeArmy.boardTroops().at(pos).get();
        else return board.at(pos);
    }

    private boolean canStepFrom(TilePos origin) {
        if (result != GameResult.IN_PLAY)
            return false;
        if (armyNotOnTurn().boardTroops().at(origin).isPresent())
            return false;
        if (sideOnTurn == PlayingSide.ORANGE && orangeArmy.boardTroops().at(origin).isEmpty())
            return false;
        if (sideOnTurn == PlayingSide.BLUE && blueArmy.boardTroops().at(origin).isEmpty()) // .isPresent?
            return false;
        if (!blueArmy.boardTroops().isLeaderPlaced() || blueArmy.boardTroops().isPlacingGuards())
            return false;
        if (!orangeArmy.boardTroops().isLeaderPlaced() || orangeArmy.boardTroops().isPlacingGuards())
            return false;
        return true;
    }

    private boolean canStepTo(TilePos target) {
        if (target == TilePos.OFF_BOARD) return false;

        for (BoardPos pos : blueArmy.boardTroops().troopPositions()) {
            if (pos.equals(target)) return false;
        }
        for (BoardPos pos : orangeArmy.boardTroops().troopPositions()) {
            if (pos.equals(target)) return false;
        }

        return result == GameResult.IN_PLAY && board.at(target).canStepOn();
    }

    private boolean canCaptureOn(TilePos target) {
        return result == GameResult.IN_PLAY && (sideOnTurn == PlayingSide.BLUE ?
                orangeArmy.boardTroops().at(target).isPresent() :
                blueArmy.boardTroops().at(target).isPresent());
    }

    public boolean canStep(TilePos origin, TilePos target) {
        return canStepFrom(origin) && canStepTo(target);
    }

    public boolean canCapture(TilePos origin, TilePos target) {
        return canStepFrom(origin) && canCaptureOn(target);
    }

    public boolean canPlaceFromStack(TilePos target) {
        if (result != GameResult.IN_PLAY)
            return false;

        if (target == TilePos.OFF_BOARD)
            return false;

        if (sideOnTurn == PlayingSide.BLUE) {
            if (blueArmy.stack().isEmpty())
                return false;

            if (!blueArmy.boardTroops().isLeaderPlaced()) {
                if (target.row() != 1)
                    return false;
            } else if (blueArmy.boardTroops().isPlacingGuards()) {
                if (!blueArmy.boardTroops().leaderPosition().isNextTo(target))
                    return false;
                for (BoardPos pos : blueArmy.boardTroops().troopPositions()) {
                    if (pos.equals(target)) return false;
                }
                for (BoardPos pos : orangeArmy.boardTroops().troopPositions()) {
                    if (pos.equals(target)) return false;
                }
            } else {
                boolean detected_neighbor = false;
                for (BoardPos pos : blueArmy.boardTroops().troopPositions()) {
                    if (pos.isNextTo(target)) {
                        detected_neighbor = true;
                    }
                    if (pos.equals(target)) return false;
                }
                for (BoardPos pos : orangeArmy.boardTroops().troopPositions()) {
                    if (pos.equals(target)) return false;
                }
                if (!detected_neighbor) return false;
            }

        }

        if (sideOnTurn == PlayingSide.ORANGE) {
            if (orangeArmy.stack().isEmpty())
                return false;

            if (!orangeArmy.boardTroops().isLeaderPlaced()) {
                if (target.row() != board.dimension)
                    return false;
            } else if (orangeArmy.boardTroops().isPlacingGuards()) {
                if (!orangeArmy.boardTroops().leaderPosition().isNextTo(target))
                    return false;
                for (BoardPos pos : blueArmy.boardTroops().troopPositions()) {
                    if (pos.equals(target)) return false;
                }
                for (BoardPos pos : orangeArmy.boardTroops().troopPositions()) {
                    if (pos.equals(target)) return false;
                }
            } else {
                boolean detected_neighbor = false;
                for (BoardPos pos : blueArmy.boardTroops().troopPositions()) {
                    if (pos.equals(target)) return false;
                }
                for (BoardPos pos : orangeArmy.boardTroops().troopPositions()) {
                    if (pos.isNextTo(target)) {
                        detected_neighbor = true;
                    }
                    if (pos.equals(target)) return false;
                }
                if (!detected_neighbor) return false;
            }
        }

        return board.at(target).canStepOn();
    }

    public GameState stepOnly(BoardPos origin, BoardPos target) {
        if (canStep(origin, target))
            return createNewGameState(
                    armyNotOnTurn(),
                    armyOnTurn().troopStep(origin, target), GameResult.IN_PLAY);

        throw new IllegalArgumentException();
    }

    public GameState stepAndCapture(BoardPos origin, BoardPos target) {
        if (canCapture(origin, target)) {
            Troop captured = armyNotOnTurn().boardTroops().at(target).get().troop();
            GameResult newResult = GameResult.IN_PLAY;

            if (armyNotOnTurn().boardTroops().leaderPosition().equals(target))
                newResult = GameResult.VICTORY;

            return createNewGameState(
                    armyNotOnTurn().removeTroop(target),
                    armyOnTurn().troopStep(origin, target).capture(captured), newResult);
        }

        throw new IllegalArgumentException();
    }

    public GameState captureOnly(BoardPos origin, BoardPos target) {
        if (canCapture(origin, target)) {
            Troop captured = armyNotOnTurn().boardTroops().at(target).get().troop();
            GameResult newResult = GameResult.IN_PLAY;

            if (armyNotOnTurn().boardTroops().leaderPosition().equals(target))
                newResult = GameResult.VICTORY;

            return createNewGameState(
                    armyNotOnTurn().removeTroop(target),
                    armyOnTurn().troopFlip(origin).capture(captured), newResult);
        }

        throw new IllegalArgumentException();
    }

    public GameState placeFromStack(BoardPos target) {
        if (canPlaceFromStack(target)) {
            return createNewGameState(
                    armyNotOnTurn(),
                    armyOnTurn().placeFromStack(target),
                    GameResult.IN_PLAY);
        }

        throw new IllegalArgumentException();
    }

    public GameState resign() {
        return createNewGameState(
                armyNotOnTurn(),
                armyOnTurn(),
                GameResult.VICTORY);
    }

    public GameState draw() {
        return createNewGameState(
                armyOnTurn(),
                armyNotOnTurn(),
                GameResult.DRAW);
    }

    private GameState createNewGameState(Army armyOnTurn, Army armyNotOnTurn, GameResult result) {
        if (armyOnTurn.side() == PlayingSide.BLUE) {
            return new GameState(board, armyOnTurn, armyNotOnTurn, PlayingSide.BLUE, result);
        }

        return new GameState(board, armyNotOnTurn, armyOnTurn, PlayingSide.ORANGE, result);
    }

    @Override
    public void toJSON(PrintWriter writer) {
        writer.printf("{\"result\":");
        result.toJSON(writer);
        writer.printf(",\"board\":");
        board.toJSON(writer);
        writer.printf(",\"blueArmy\":");
        blueArmy.toJSON(writer);
        writer.printf(",\"orangeArmy\":");
        orangeArmy.toJSON(writer);

        writer.printf("}");
    }
}
