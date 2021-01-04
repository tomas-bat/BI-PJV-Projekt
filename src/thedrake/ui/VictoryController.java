package thedrake.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import thedrake.GameResult;

public class VictoryController {

    @FXML
    public Label winnerLabel;

    public void backClicked() {
        GameResult.changeStateTo(GameResult.START);
    }

    public void setLabel(String text) {
        winnerLabel.setText(text);
    }
}
