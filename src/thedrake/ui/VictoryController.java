package thedrake.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import thedrake.GameResult;

public class VictoryController {

    @FXML
    public Label label;

    public void onClick() {
        GameResult.changeStateTo(GameResult.START);
    }

    public void setLabel(String text) {
        label.setText(text);
    }
}
