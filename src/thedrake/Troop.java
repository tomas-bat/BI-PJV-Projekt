package thedrake;

import java.io.PrintWriter;
import java.util.List;

import static thedrake.TroopFace.*;

public class Troop implements JSONSerializable {

    private final String name;

    private final Offset2D aversPivot;

    private final Offset2D reversPivot;

    private List<TroopAction> aversActions;

    private List<TroopAction> reversActions;

    public Troop(String name, Offset2D aversPivot, Offset2D reversPivot, List<TroopAction> aversActions,
                 List<TroopAction> reversActions) {
        this.name = name;
        this.aversPivot = aversPivot;
        this.reversPivot = reversPivot;
        this.aversActions = aversActions;
        this.reversActions = reversActions;
    }

    public Troop(String name, Offset2D pivot, List<TroopAction> aversActions, List<TroopAction> reversActions) {
        this.name = name;
        this.aversPivot = pivot;
        this.reversPivot = pivot;
        this.aversActions = aversActions;
        this.reversActions = reversActions;
    }

    public Troop(String name, List<TroopAction> aversActions, List<TroopAction> reversActions) {
        this.name = name;
        this.aversPivot = new Offset2D(1, 1);
        this.reversPivot = new Offset2D(1, 1);
        this.aversActions = aversActions;
        this.reversActions = reversActions;
    }

    public String name() {
        return name;
    }

    public Offset2D pivot(TroopFace face) {
        if (face == AVERS) {
            return aversPivot;
        }
        return reversPivot;
    }

    public List<TroopAction> actions(TroopFace face) {
        return face.equals(AVERS) ? aversActions : reversActions;
    }

    public void toJSON(PrintWriter writer) {
        writer.print("\"" + this.name + "\"");
    }
}
