package thedrake.ui;

import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import thedrake.*;

public class BoardView extends GridPane implements TileViewContext {

    private GameState gameState;

    private ValidMoves validMoves;

    private TileView selected;

    private StackView stackView;

    private PlayingSide currentPlaying = PlayingSide.BLUE;

    public BoardView(GameState gameState) {
        this.gameState = gameState;
        this.validMoves = new ValidMoves(gameState);

        PositionFactory positionFactory = gameState.board().positionFactory();
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                BoardPos boardPos = positionFactory.pos(x, 3 - y);
                add(new TileView(gameState.tileAt(boardPos), boardPos, this), x, y);
            }
        }

        setHgap(5);
        setVgap(5);
        setPadding(new Insets(15));
        setAlignment(Pos.CENTER);
    }

    @Override
    public void tileViewSelected(TileView tileView) {
        if (selected != null && selected != tileView) {
            selected.unselect();
            selected = null;
        }

        selected = tileView;

        clearMoves();
        showMoves(validMoves.boardMoves(tileView.position()));
    }

    @Override
    public GameState gameState() {
        return gameState;
    }

    @Override
    public void executeMove(Move move) {
        if (selected != null) {
            selected.unselect();
            selected = null;
        }

        clearMoves();

        gameState = move.execute(gameState);
        validMoves = new ValidMoves(gameState);
        updateTiles();

        if (stackView != null) {
            stackView.removeTroopView();
        }
        stackView = null;

        if (currentPlaying == PlayingSide.ORANGE)
            currentPlaying = PlayingSide.BLUE;
        else
            currentPlaying = PlayingSide.ORANGE;

        GameResult.changeStateTo(gameState.result());
    }

    private void updateTiles() {
        for (Node node: getChildren()) {
            TileView tileView = (TileView) node;
            tileView.setTile(gameState.tileAt(tileView.position()));
            tileView.update();
        }
    }

    private void clearMoves() {
        for (Node node: getChildren()) {
            TileView tileView = (TileView) node;
            tileView.clearMove();
        }
    }

    private void showMoves(List<Move> moveList) {
        for (Move move: moveList)
            tileViewAt(move.target()).setMove(move);
    }

    private TileView tileViewAt(BoardPos target) {
        int index = (gameState.board().dimension() - 1 - target.j()) * 4 + target.i();
        return (TileView) getChildren().get(index);
    }

    public void clickOnStackTile() {
        showMoves(validMoves.movesFromStack());
    }

    public void onClickStackView(StackView stackView, PlayingSide playingSide) {
        if (playingSide != currentPlaying)
            return;
        clearMoves();
        this.stackView = stackView;
        clickOnStackTile();
    }
}
