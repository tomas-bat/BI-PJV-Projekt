package thedrake.ui;

import javafx.geometry.Pos;
import javafx.scene.layout.*;
import thedrake.*;

import java.util.ArrayList;
import java.util.List;

public class StackView extends HBox {

    private final List<Troop> army;
    private final PlayingSide side;
    private final List<TroopView> troopViews = new ArrayList<>();

    public StackView(List<Troop> army, PlayingSide side) {
        this.army = army;
        this.side = side;
        this.setPrefSize(100,100);
        this.setSpacing(8);
        this.setAlignment(Pos.CENTER);
        setTroopViews();
    }

    public void addTroopView(TroopView troopView) {
        this.troopViews.add(troopView);
        getChildren().add(troopView);
        System.out.println("ahoj :D");
    }
    
    private void setTroopViews() {
        getChildren().clear();
        for (Troop troop: army) {
            TroopView troopView = new TroopView(troop, side);
            getChildren().add(troopView);
            troopViews.add(troopView);
        }
    }

    public void removeTroopView() {
        if (getChildren().contains(troopViews.get(0))) {
            getChildren().remove(troopViews.get(0));
            troopViews.remove((troopViews.get(0)));
        }

    }

    public List<TroopView> getTroopViews() { return troopViews; }
}
