package thedrake.ui;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import thedrake.BoardPos;
import thedrake.Move;
import thedrake.Tile;

public class TileView extends Pane {

    private BoardPos boardPos;

    private Tile tile;

    private TileBackgrounds backgrounds = new TileBackgrounds();

    private Border selectBorder = new Border(
        new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(3)));

    private TileViewContext tileViewContext;

    private Move move;

    private final ImageView moveImage;

    @FXML
    private javafx.scene.control.Button closeButton;

    private final ImageView moveImage_stack;

    public TileView(BoardPos boardPos, Tile tile, TileViewContext tileViewContext) {
        this.boardPos = boardPos;
        this.tile = tile;
        this.tileViewContext = tileViewContext;

        setPrefSize(100, 100);
        update();

        setOnMouseClicked(e -> onClick());

        moveImage = new ImageView(getClass().getResource("/assets/move.png").toString());
        moveImage_stack = new ImageView(getClass().getResource("/assets/move_stack.png").toString());
        moveImage_stack.setVisible(false);
        moveImage.setVisible(false);
        getChildren().add(moveImage);
        getChildren().add(moveImage_stack);
    }

    private void onClick() {
        if (move != null)
            tileViewContext.executeMove(move);
        else if (tile.hasTroop())
            select();
    }

    public void select() {
        setBorder(selectBorder);
        tileViewContext.tileViewSelected(this);
    }

    public void unselect() {
        setBorder(null);
    }

    public void update() {
        setBackground(backgrounds.get(tile));
    }

    public void setMove(Move move) {
        this.move = move;
        if (move.toString().contains("PlaceFromStack")) {
            moveImage_stack.setVisible(true);
        } else {
            moveImage.setVisible(true);
        }
    }

    public void clearMove() {
        this.move = null;
        moveImage.setVisible(false);
        moveImage_stack.setVisible(false);
    }

    public BoardPos position() {
        return boardPos;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

}
