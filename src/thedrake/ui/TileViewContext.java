package thedrake.ui;

import thedrake.GameState;
import thedrake.Move;

public interface TileViewContext {

    void tileViewSelected(TileView tileView);

    GameState gameState();

    void executeMove(Move move);

}
