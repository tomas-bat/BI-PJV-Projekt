package thedrake.ui;

import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import thedrake.*;

import java.util.ArrayList;
import java.util.List;

public class StackView extends HBox {

    private List<Troop> army;
    private final PlayingSide side;
    private List<TroopView> troopViews = new ArrayList<>();

    public StackView(List<Troop> army, PlayingSide side) {
        this.army = army;
        this.side = side;
        this.setPrefSize(100,100);
        this.setSpacing(8);
        this.setAlignment(Pos.CENTER);
        setTroopViews();
    }
    
    private void setTroopViews() {
        getChildren().clear();
        for (Troop troop: army) {
            TroopView troopView = new TroopView(troop, side);
            getChildren().add(troopView);
            troopViews.add(troopView);
        }
    }

    public void removeTroopView(TroopView troopView) {
        if (getChildren().contains(troopView)) {
            getChildren().remove(troopView);
            troopViews.remove(troopView);
        }

    }

    public List<TroopView> getTroopViews() { return troopViews; }

}
