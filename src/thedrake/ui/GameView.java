package thedrake.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import thedrake.*;

import java.util.Collections;

public class GameView extends BorderPane {

    private BoardView boardView;
    private GameState gameState;
    private StackView orangeStackView;
    private StackView blueStackView;

    private StackView orangeBArmy;
    private StackView blueBArmy;

    public GameView(GameState gameState) {
        this.gameState = gameState;
        this.boardView = new BoardView(gameState);

        VBox stackBox = new VBox();
        stackBox.setAlignment(Pos.CENTER);
        stackBox.setSpacing(15);

        Label blueArmies = new Label("Zajaté jednotky modrého hráče");
        stackBox.getChildren().add(blueArmies);
        stackBox.getChildren().add(blueBArmy = new StackView(Collections.emptyList(), PlayingSide.BLUE));

        Label stackOrange = new Label("Zásobník oranžového hráče");
        stackBox.getChildren().add(stackOrange);
        stackBox.getChildren().add(orangeStackView = new StackView(boardView.gameState().army(PlayingSide.ORANGE).stack(), PlayingSide.ORANGE));

        stackBox.getChildren().add(boardView);

        Label stackBlue = new Label("Zásobník modrého hráče");
        stackBox.getChildren().add(stackBlue);
        stackBox.getChildren().add(blueStackView = new StackView(boardView.gameState().army(PlayingSide.BLUE).stack(), PlayingSide.BLUE));

        Label orangeArmies = new Label("Zajaté jednotky oranžového hráče");
        stackBox.getChildren().add(orangeArmies);
        stackBox.getChildren().add(orangeBArmy = new StackView(Collections.emptyList(), PlayingSide.ORANGE));

        setCenter(stackBox);

        // When user clicks on troop:
        blueStackView.getTroopViews().forEach(troop -> troop.setOnMouseClicked(e
                -> boardView.onClickStackView(blueStackView, PlayingSide.BLUE)));
        orangeStackView.getTroopViews().forEach(troop -> troop.setOnMouseClicked(e
                -> boardView.onClickStackView(orangeStackView, PlayingSide.ORANGE)));
    }

    public BoardView boardView() { return boardView; }
}
