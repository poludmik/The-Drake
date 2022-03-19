package thedrake;

import java.util.ArrayList;
import java.util.List;

public class SlideAction extends TroopAction {

    public SlideAction(Offset2D offset) {
        super(offset);
    }

    public SlideAction(int offsetX, int offsetY) {
        super(offsetX, offsetY);
    }

    @Override
    public List<Move> movesFrom(BoardPos origin, PlayingSide side, GameState state) {
        List<Move> result = new ArrayList<>();
        TilePos target = origin.stepByPlayingSide(offset(), side);

        int slider = 2;
        while (state.canStep(origin, target) || state.canCapture(origin, target)) {

            if (state.canStep(origin, target)) {
                result.add(new StepOnly(origin, (BoardPos) target));
            } else if (state.canCapture(origin, target)) {
                result.add(new StepAndCapture(origin, (BoardPos) target));
                break;
            }

            Offset2D newOffset = new Offset2D(offset().x * slider, offset().y * slider);
            target = origin.stepByPlayingSide(newOffset, side);

            slider += 1;
        }
        return result;
    }
}
