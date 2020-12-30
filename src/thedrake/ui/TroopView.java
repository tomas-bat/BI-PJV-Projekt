package thedrake.ui;

import javafx.scene.layout.Pane;
import thedrake.PlayingSide;
import thedrake.Troop;
import thedrake.TroopFace;

public class TroopView extends Pane {

    private final Troop troop;
    private final PlayingSide side;
    private final TileBackgrounds tileBackgrounds = new TileBackgrounds();

    public TroopView(Troop troop, PlayingSide side) {
        this.troop = troop;
        this.side = side;
        this.setPrefSize(100,100);
        setBackground();
    }

    private void setBackground() {
        setBackground(tileBackgrounds.getTroop(troop, side, TroopFace.AVERS));
    }

    public Troop getTroop() { return troop; }
}
