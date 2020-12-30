package thedrake.ui;

import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import thedrake.BoardPos;
import thedrake.Move;
import thedrake.Tile;

public class TileView extends Pane {

    private Tile tile;
    private final TileBackgrounds backgrounds = new TileBackgrounds();
    private final Border selectionBorder = new Border(
            new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(3))
    );
    private final ImageView moveImage;

    private final BoardPos position;

    private final TileViewContext tileViewContext;
    private Move move;

    public TileView(Tile tile, BoardPos position, TileViewContext tileViewContext) {
        this.tile = tile;
        this.position = position;
        this.tileViewContext = tileViewContext;

        setPrefSize(100, 100);

        update();

        setOnMouseClicked(e-> onClick() );

        moveImage = new ImageView(getClass().getResource("/assets/move.png").toString());
        moveImage.setVisible(false);
        getChildren().add(moveImage);
    }

    private void onClick() {
        if(move != null) {
            tileViewContext.executeMove(move);
        } else if(tile.hasTroop()) {
            select();
        }
    }

    public void update() {
        setBackground(backgrounds.get(tile));
    }

    private void select() {
        setBorder(selectionBorder);
        tileViewContext.tileViewSelected(this);
    }

    public void unselect() {
        setBorder(null);
    }

    public void setTile(Tile tile) {
        this.tile = tile;
        update();
    }

    public BoardPos position() {
        return position;
    }

    public void setMove(Move move) {
        this.move = move;
        moveImage.setVisible(true);
    }

    public void clearMove() {
        this.move = null;
        moveImage.setVisible(false);
    }
}
